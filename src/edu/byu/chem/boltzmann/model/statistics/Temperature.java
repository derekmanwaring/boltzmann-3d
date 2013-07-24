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

import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.statistics.parents.SingleAverageStatisticOld;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import java.util.Set;

/**
 * Created 11 Jun 2011
 * @author Derek Manwaring
 */
public class Temperature extends SingleAverageStatisticOld {
    
    private static final String DISPLAY_NAME = "Temperature";
    private static final Unit DEFAULT_UNIT = Units.Temperature.KELVIN;
    
    private double currentTemperature = 0.0;
    
    @Override
    protected void updateStatisticAvgs(FrameInfo frame) {
        
    }

    @Override
    public void resetInstData() {
        
    }

    @Override
    public double getAverage() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public double getInstAverage() {
        return currentTemperature;
    }

    @Override
    protected double calculatePrediction() {
        return simulationInfo.initialTemperature;
    }

    @Override
    protected double getCenterPlotValue() {
        return simulationInfo.initialTemperature;
    }

    @Override
    protected double getScalePlotValue() {
        return 200.0;
    }

    @Override
    public void prepareInstCalculations(FrameInfo frame) {
        double totalKE = 0.0;
        Set<Particle> allParticles = frame.getParticles();
        
        for (Particle particle: allParticles) {
            PartState frameState = frame.getParticleState(particle);
            totalKE += Formulas.kineticEnergy(frameState);
        }
        
        double avgKE = totalKE / allParticles.size();
        
        currentTemperature = Formulas.temperature(avgKE, simulationInfo.dimension);
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Unit getUnit(StatisticCalculation calculation) {
        return DEFAULT_UNIT;
    }

    @Override
    public double getCalculation(StatisticCalculation calculation) {
        if (calculation == StatUtils.AVERAGE) {
            return super.getInstCalculation(StatUtils.AVERAGE);
        } else {
            return super.getCalculation(calculation);
        }
    }

    @Override
    public void updateByEvent(EventInfo event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
