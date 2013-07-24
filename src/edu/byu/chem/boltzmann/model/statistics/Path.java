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
import edu.byu.chem.boltzmann.model.statistics.interfaces.ProbabilityDensityFunctionPointCreater;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.utils.ParticleStateAnalyzer;
import edu.byu.chem.boltzmann.model.statistics.utils.WeightedValueTracker;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Joshua Olson
 * June 8, 2012
 */
public class Path implements StatisticWithDistribution<Length> {
    private static final Length DEFAULT_UNIT = Length.NANOMETER;

    private boolean useFiniteSystemCorrections = false;
    private boolean useRealGasCorrections = false;
    
    private double simulationTime = 0;
    
    private final ParticleStateAnalyzer stateAnalyzer;
    
    private final WeightedValueTracker<Length> currentWeightedValues = 
            new WeightedValueTracker<Length>(DEFAULT_UNIT, false);
    private HistogramBins currentBins;
    
    private final WeightedValueTracker<Length> cumulativeWeightedValues = 
            new WeightedValueTracker<Length>(DEFAULT_UNIT, false);
    private HistogramBins cumulativeBins;
    
    private final SimulationInfo simInfo;
    private final Set<ParticleType> typesRecorded;
    
    private Range distributionRange;
    private boolean hasNewPredictionCurve = true;
    
    private final ProbabilityDensityFunctionPointCreater predictionCurvePointCreater;
    
    public Path(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        stateAnalyzer = new ParticleStateAnalyzer(
            new ParticleStateAnalyzer.ParticleStateWatcher(types, simInfo.reactionMode) {

                @Override
                public void update(Particle particle, double timeElapsedInState) {
                    double speed = particle.getVel();
                    double value = Units.convert(Length.METER, DEFAULT_UNIT, speed * timeElapsedInState);
                    cumulativeWeightedValues.addWeightedValue(value, 1.0);
                    cumulativeBins.addWeightedValue(value, 1, particle.getDisplayColor());
                }

                @Override
                public void update(Particle particle) {
                    double speed = particle.getVel();
                    double timeElapsedInState = simulationTime - stateAnalyzer.getLastCollisionTime(particle);
                    double value = Units.convert(Length.METER, DEFAULT_UNIT, speed * timeElapsedInState);
                    currentWeightedValues.addWeightedValue(value, 1.0);
                    currentBins.addWeightedValue(value, 1.0, particle.getDisplayColor());
                }        
            }, 
                Collision.PARTICLE);
        
        predictionCurvePointCreater = new ProbabilityDensityFunctionPointCreater(typesRecorded, simInfo) {
            @Override
            public double probabilityDensity(double value, ParticleType type) {
                return distributionFunction(value, type);
            }
        };
        
        distributionRange = new Range(0.0, getPredictionForAverage(DEFAULT_UNIT) * 2.5);
        currentBins = new HistogramBins(distributionRange, 15, typesRecorded);
        cumulativeBins = new HistogramBins(distributionRange, 30, typesRecorded);
    }
    
    public static double getLambda(final SimulationInfo simInfo, Set<ParticleType> particleTypes, final boolean finiteCorrections, final boolean realGasCorrections){
        return InstantaneousSpeed.averageVelocity(simInfo, particleTypes, finiteCorrections)
                / Formulas.predictCollisionRate(simInfo, particleTypes, realGasCorrections);
    }

    public double distributionFunction(double x, ParticleType particleType) {
        Set<ParticleType> singleton = new HashSet<ParticleType>();
        singleton.add(particleType);
        double lambda = Units.convert(Length.METER, DEFAULT_UNIT, getLambda(simInfo, singleton, useFiniteSystemCorrections, useRealGasCorrections));
        return (Math.exp(-x / lambda) / lambda);//optimize to require only one division? how to convert units...
    }

    @Override
    public double getDistributionWidthPrediction(Length unit) {
        return getPredictionForAverage(unit);
    }

    @Override
    public double getCurrentDistributionWidth(Length unit) {
        return currentWeightedValues.getWidth(unit);
    }

    @Override
    public double getCumulativeDistributionWidth(Length unit) {
        return cumulativeWeightedValues.getWidth(unit);
    }

    @Override
    public HistogramBins getHistogramBinsForCurrentDistribution() {
        return currentBins;
    }

    @Override
    public HistogramBins getHistogramBinsForCumulativeDistribution() {
        return cumulativeBins;
    }

    @Override
    public Range getDistributionRange(Length unit) {
        return Units.convert(DEFAULT_UNIT, unit, distributionRange);
    }

    @Override
    public void setDistributionRange(Range range, Length unit) {
        hasNewPredictionCurve = true;
        distributionRange = Units.convert(unit, DEFAULT_UNIT, range);
        cumulativeBins = new HistogramBins(distributionRange, 30, typesRecorded);
        currentBins = new HistogramBins(distributionRange, 15, typesRecorded);
    }

    @Override
    public boolean hasNewPredictionCurve() {
        return hasNewPredictionCurve;
    }

    @Override
    public double[] getPredictionCurve(int points) {
        return predictionCurvePointCreater.createPointsForProbabilityDensityFunction(distributionRange, points);
    }

    @Override
    public double getPredictionForAverage(Length unit) {
        return Units.convert(Length.METER, DEFAULT_UNIT, getLambda(simInfo, typesRecorded, useFiniteSystemCorrections, useRealGasCorrections));
    }

    @Override
    public double getCurrentAverage(Length unit) {
        return currentWeightedValues.getAverage(unit);
    }

    @Override
    public double getCumulativeAverage(Length unit) {
        return cumulativeWeightedValues.getAverage(unit);
    }

    @Override
    public Set<Length> getDisplayUnits() {
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public Length getDefaultDisplayUnit() {
        return DEFAULT_UNIT;
    }

    @Override
    public void useFrameForCurrentCalculations(FrameInfo frame) {
        simulationTime = frame.endTime;//usually updated in notifyOfSimulationTime, but then we could get negative timeElapsed
        currentWeightedValues.clear();
        currentBins.resetBins();
        stateAnalyzer.analyzeFrame(frame);
    }

    @Override
    public void notifyOfEvent(EventInfo event) {
        stateAnalyzer.analyzeEvent(event);
    }

    @Override    
    public void notifyOfSimulationTime(double simTime) {
    }    

    @Override
    public void reset() {
        cumulativeWeightedValues.clear();
        cumulativeBins.resetBins();
        stateAnalyzer.reset();
    }

    @Override
    public void clear() {
        cumulativeWeightedValues.clear();
        cumulativeBins.resetBins();
    }

    @Override
    public void setFiniteSysCorrections(boolean corrections) {
        useFiniteSystemCorrections = corrections;
    }
    
    @Override
    public void setRealGasCorrections(boolean corrections){
        useRealGasCorrections = corrections;
    }
}
