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
import edu.byu.chem.boltzmann.utils.Units.Time;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Joshua Olson
 * July 11, 2012
 */
public class TimePerCollision implements StatisticWithDistribution<Time> {
    private static final Time DEFAULT_UNIT = Time.PICOSECOND;

    private boolean useRealGasCorrections = false;
    
    private double simulationTime = 0;
    
    private final ParticleStateAnalyzer stateAnalyzer;
    
    private final WeightedValueTracker<Time> currentWeightedValues = 
            new WeightedValueTracker<Time>(DEFAULT_UNIT, false);
    private HistogramBins currentBins;
    
    private final WeightedValueTracker<Time> cumulativeWeightedValues = 
            new WeightedValueTracker<Time>(DEFAULT_UNIT, false);
    private HistogramBins cumulativeBins;
    
    private final SimulationInfo simInfo;
    private final Set<ParticleType> typesRecorded;
    
    private Range distributionRange;
    private boolean hasNewPredictionCurve = true;
    
    private final ProbabilityDensityFunctionPointCreater predictionCurvePointCreater;
    
    public TimePerCollision(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        stateAnalyzer = new ParticleStateAnalyzer(
            new ParticleStateAnalyzer.ParticleStateWatcher(types, simInfo.reactionMode) {

                @Override
                public void update(Particle particle, double timeElapsedInState) {
                    double value = Units.convert(Time.SECOND, DEFAULT_UNIT, timeElapsedInState);
                    cumulativeWeightedValues.addWeightedValue(value, 1.0);
                    cumulativeBins.addWeightedValue(value, 1, particle.getDisplayColor());
                }

                @Override
                public void update(Particle particle) {
                    double timeElapsedInState = simulationTime - stateAnalyzer.getLastCollisionTime(particle);
                    double value = Units.convert(Time.SECOND, DEFAULT_UNIT, timeElapsedInState);
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

    public double distributionFunction(double x, ParticleType particleType) {
        double colRate = Formulas.predictCollisionRate(simInfo, particleType, useRealGasCorrections) * 1E-12;//explicit unit conversion would only make sense with its inverse
        return Math.exp(-x * colRate) * colRate;
    }

    @Override
    public double getDistributionWidthPrediction(Time unit) {
        return getPredictionForAverage(unit);
    }

    @Override
    public double getCurrentDistributionWidth(Time unit) {
        return currentWeightedValues.getWidth(unit);
    }

    @Override
    public double getCumulativeDistributionWidth(Time unit) {
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
    public Range getDistributionRange(Time unit) {
        return Units.convert(DEFAULT_UNIT, unit, distributionRange);
    }

    @Override
    public void setDistributionRange(Range range, Time unit) {
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
    public double getPredictionForAverage(Time unit) {
        return Units.convert(Time.SECOND, DEFAULT_UNIT, 1. / Formulas.predictCollisionRate(simInfo, typesRecorded, useRealGasCorrections));
    }

    @Override
    public double getCurrentAverage(Time unit) {
        return currentWeightedValues.getAverage(unit);
    }

    @Override
    public double getCumulativeAverage(Time unit) {
        return cumulativeWeightedValues.getAverage(unit);
    }

    @Override
    public Set<Time> getDisplayUnits() {
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public Time getDefaultDisplayUnit() {
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
    }
    
    @Override
    public void setRealGasCorrections(boolean corrections){
        useRealGasCorrections = corrections;
    }
}
