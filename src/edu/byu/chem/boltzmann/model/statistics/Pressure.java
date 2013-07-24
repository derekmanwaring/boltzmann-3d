/*
 * Boltzmann 3D, a kinetic theory demonstrator
 * Copyright (C) 2013 Dr. Randall B. Shirts
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.byu.chem.boltzmann.model.statistics;

import edu.byu.chem.boltzmann.model.physics.Collision;
import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.statistics.interfaces.SingleAverageStatistic;
import edu.byu.chem.boltzmann.model.statistics.utils.VirialPressureTracker;
import edu.byu.chem.boltzmann.model.statistics.utils.WallPressureTracker;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.PressureUnit;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import java.util.*;

/**
 *
 * @author Joshua Olson
 * June 8, 2012
 */
public class Pressure implements SingleAverageStatistic<PressureUnit> {
    private static PressureUnit DEFAULT_UNIT;
    private final int dimension;

    private boolean useRealGasCorrections = false;
    private final boolean periodic;
    
    private final SimulationInfo simInfo;
    private final Set<ParticleType> typesRecorded;
    
    private final WallPressureTracker wallTracker;
    private final VirialPressureTracker virialTracker;
    
    public Pressure(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        periodic = simInfo.isPeriodic();
        typesRecorded = types;
        
        dimension = simInfo.dimension;
        DEFAULT_UNIT = PressureUnit.getBaseUnit(dimension);
        
        wallTracker = new WallPressureTracker(DEFAULT_UNIT, 60 /*default averagingTime in ps*/, typesRecorded, simInfo);
        virialTracker = new VirialPressureTracker(DEFAULT_UNIT, typesRecorded, simInfo);
    }

    @Override
    public double getPredictionForAverage(PressureUnit unit) {
        Map<ParticleType, Double> pressureByType = new HashMap<ParticleType, Double>(typesRecorded.size());
        for(ParticleType type: typesRecorded)
            pressureByType.put(type, Formulas.pressure(simInfo.getNumberOfParticles(type),
                simInfo.initialTemperature, Formulas.effectiveVolume(simInfo, type)));
//        for(ParticleType type: typesRecorded){
//            double pressureInDefaultUnits = Formulas.pressure(simInfo.getNumberOfParticles(type),
//                simInfo.initialTemperature, Formulas.effectiveVolume(simInfo, type));
//            switch(dimension){
//                case 1:
//                    pressureByType.put(type, Units.convert("N", "pN", pressureInDefaultUnits));
//                    break;
//                case 2:
//                    pressureByType.put(type, Units.convert("N/m", "µN/m", pressureInDefaultUnits));
//                    break;
//                default:
//                    pressureByType.put(type, Units.convert("Pa", "MPa", pressureInDefaultUnits));
//            }
//        }
        if(useRealGasCorrections){
            if(dimension > 1)
                for(ParticleType type: typesRecorded){
                    double x = Formulas.realGasX(simInfo, type);
                    pressureByType.put(type, pressureByType.get(type) * (1 + x * Formulas.realGasQx(x, dimension)));
                }
            else{
                double arenaLength = simInfo.arenaXSize;
                if(simInfo.arenaType == ArenaType.DIVIDED_ARENA)
                    arenaLength = arenaLength / 2 - simInfo.ARENA_DIVIDER_RADIUS;
                double sum = 0.0;
                int totalParticles = 0;
                for(ParticleType type: typesRecorded){
                    sum += type.particleRadius * simInfo.getNumberOfParticles(type);
                    totalParticles += simInfo.getNumberOfParticles(type);
                }
                sum *= 2;
                double correctedVolume = arenaLength - sum;
                for(ParticleType type: typesRecorded){
                    double correctedPressure = 0.0;
                    try{
                        correctedPressure = pressureByType.get(type) * Formulas.effectiveVolume(simInfo, type) / correctedVolume;
                    }
                    catch(Exception e){
                    }
                    pressureByType.put(type, correctedPressure);
                }
                if(periodic && totalParticles > 0 && arenaLength > 0)
                    for(ParticleType type: typesRecorded)
                        pressureByType.put(type, pressureByType.get(type) * (1 - sum / (totalParticles * arenaLength)));
            }
        }
        double totalPressure = 0.0;
        for(ParticleType type: typesRecorded)
            totalPressure += pressureByType.get(type);
        return Units.convert(DEFAULT_UNIT, unit, totalPressure);
    }
    
    @Override
    public double getCurrentAverage(PressureUnit unit) {
        if(periodic)
            return virialTracker.getAverage(unit);
        else
            return wallTracker.getAverage(unit);
    }

    @Override
    public double getCumulativeAverage(PressureUnit unit) {
        return 0;
    }

    @Override
    public Set<PressureUnit> getDisplayUnits() {//////////modify so base unit not used for displaying
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public PressureUnit getDefaultDisplayUnit() {//////////modify so base unit not used for displaying
        return DEFAULT_UNIT;
    }

    @Override
    public void useFrameForCurrentCalculations(FrameInfo frame) {
    }

    @Override
    public void notifyOfEvent(EventInfo event) {
        if(periodic)
            virialTracker.analyzeEvent(event);
        else
            wallTracker.analyzeEvent(event);
    }

    @Override    
    public void notifyOfSimulationTime(double simTime) {
        if(periodic)
            virialTracker.setSimulationTime(simTime);
        else
            wallTracker.setSimulationTime(simTime);
    }

    @Override
    public void reset() {
        if(periodic)
            virialTracker.reset();
        else
            wallTracker.reset();
    }

    @Override
    public void clear() {
    }

    @Override
    public void setFiniteSysCorrections(boolean corrections) {
    }
    
    @Override
    public void setRealGasCorrections(boolean corrections){
        useRealGasCorrections = corrections;
    }
    
    public void setAveragingTime(double avgTime){
        if(!periodic)
            wallTracker.setAveragingTime(avgTime);
    }
}
