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
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.statistics.StatUtils.CalculatorByType;
import edu.byu.chem.boltzmann.model.statistics.interfaces.ProbabilityDensityFunctionPointCreater;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.utils.IndividualParticleStatisticTracker;
import edu.byu.chem.boltzmann.model.statistics.utils.ParticleStateAnalyzer;
import edu.byu.chem.boltzmann.model.statistics.utils.WeightedValueTracker;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.Units.Velocity;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Joshua Olson
 * July 10, 2012
 */

public class AveragePath implements StatisticWithDistribution<Length> {
    private static final Length DEFAULT_UNIT = Length.NANOMETER;

    private boolean useFiniteSystemCorrections = false;
    private boolean useRealGasCorrections = false;
    
    private double simulationTime = 0;
    
    private final IndividualParticleStatisticTracker statisticTracker;

    //currentBins and cumulativeBins are the same (cumulative), except the first has 15 bars and the second has 30
    private HistogramBins currentBins;
    private HistogramBins cumulativeBins;
    
    private final SimulationInfo simInfo;
    private final Set<ParticleType> typesRecorded;
    
    private Range distributionRange;
    private boolean hasNewPredictionCurve = true;
    
    private final ProbabilityDensityFunctionPointCreater predictionCurvePointCreater;
    
    public AveragePath(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        statisticTracker = new IndividualParticleStatisticTracker(simInfo.totalNumParticles, typesRecorded, simInfo.reactionMode, Collision.PARTICLE){
            public double updatedValue(double prevValue, double velocitySquared, double timeElapsed, double mass){
                return prevValue + Units.convert(Length.METER, DEFAULT_UNIT, Math.sqrt(velocitySquared) * timeElapsed);
            }
            public double binFunction(double value, int numCollisions){
                return value / numCollisions;
            }
        };
        
        predictionCurvePointCreater = new ProbabilityDensityFunctionPointCreater(typesRecorded, simInfo) {
            @Override
            public double probabilityDensity(double value, ParticleType type) {
                return distributionFunction(value, type);
            }
        };
        double prediction = getPredictionForAverage(DEFAULT_UNIT);
        distributionRange = new Range(prediction * .5, prediction * 1.5);
        currentBins = new HistogramBins(distributionRange, 15, typesRecorded);
        cumulativeBins = new HistogramBins(distributionRange, 30, typesRecorded);
    }

    public double distributionFunction(double x, ParticleType particleType) {
        Set<ParticleType> singleton = new HashSet<ParticleType>();
        singleton.add(particleType);
        double avgPath = Units.convert(Length.METER, DEFAULT_UNIT, Path.getLambda(simInfo, singleton, useFiniteSystemCorrections, useRealGasCorrections));
        double pathWidth = 1.14 * avgPath / Math.sqrt(Formulas.predictCollisionRate(simInfo, particleType, useRealGasCorrections) * simulationTime);
        double value = (Math.sqrt(2 * Math.PI) / pathWidth) * Math.exp(-Math.pow(x - avgPath, 2) / (2 * Math.pow(pathWidth, 2)));
        
        if(Double.isNaN(value))
            return 0.0;
        return value;
    }

    @Override
    public double getDistributionWidthPrediction(Length unit) {
//        double tao = 1. / Formulas.predictCollisionRate(simInfo, typesRecorded, useFiniteSystemCorrections);
//        double lambda = InstantaneousSpeed.averageVelocity(simInfo, typesRecorded, useFiniteSystemCorrections) * tao;
//        return Units.convert(Length.METER, unit, 1.14 * lambda * Math.sqrt(tao / simulationTime));
        double avgVel = InstantaneousSpeed.averageVelocity(simInfo, typesRecorded, useFiniteSystemCorrections);
        double colRate = Formulas.predictCollisionRate(simInfo, typesRecorded, useRealGasCorrections);
        return Units.convert(Length.METER, unit, 1.14 * avgVel / (colRate * Math.sqrt(colRate * simulationTime)));
    }

    @Override
    public double getCurrentDistributionWidth(Length unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getWidth());
    }

    @Override
    public double getCumulativeDistributionWidth(Length unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getWidth());
    }

    @Override
    public HistogramBins getHistogramBinsForCurrentDistribution() {
        currentBins.resetBins();
        Map<Color, List<Double>> data = statisticTracker.getBinData();
        for(Color color : data.keySet())
            for(double value : data.get(color))
                currentBins.addWeightedValue(value, 1.0, color);
        return currentBins;
    }

    @Override
    public HistogramBins getHistogramBinsForCumulativeDistribution() {
        cumulativeBins.resetBins();
        Map<Color, List<Double>> data = statisticTracker.getBinData();
        for(Color color : data.keySet())
            for(double value : data.get(color))
                cumulativeBins.addWeightedValue(value, 1.0, color);
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
        double lambda = InstantaneousSpeed.averageVelocity(simInfo, typesRecorded, useFiniteSystemCorrections)
                / Formulas.predictCollisionRate(simInfo, typesRecorded, useRealGasCorrections);
        return Units.convert(Length.METER, unit, lambda);
    }

    @Override
    public double getCurrentAverage(Length unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getAverage());
    }

    @Override
    public double getCumulativeAverage(Length unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getAverage());
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
    }

    @Override
    public void notifyOfEvent(EventInfo event) {
        statisticTracker.analyzeEvent(event);
    }

    @Override    
    public void notifyOfSimulationTime(double simTime) {
        simulationTime = simTime;
    }    

    @Override
    public void reset() {
        statisticTracker.reset();
        simulationTime = 0;
    }

    @Override
    public void clear() {
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
