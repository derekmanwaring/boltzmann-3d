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
import edu.byu.chem.boltzmann.model.statistics.interfaces.ProbabilityDensityFunctionPointCreater;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.utils.IndividualParticleStatisticTracker;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Frequency;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Joshua Olson
 * July 11, 2012
 */

public class CollisionRate implements StatisticWithDistribution<Frequency> {
    private static final Frequency DEFAULT_UNIT = Frequency.TERAHERTZ;

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
    
    public CollisionRate(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        statisticTracker = new IndividualParticleStatisticTracker(simInfo.totalNumParticles, typesRecorded, simInfo.reactionMode, Collision.PARTICLE){
            public double updatedValue(double prevValue, double velocitySquared, double timeElapsed, double mass){
                return prevValue + 1;
            }
            public double binFunction(double value, int numCollisions){
                return Units.convert(Frequency.HERTZ, DEFAULT_UNIT, value / simulationTime);
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
        double avgRate = Formulas.predictCollisionRate(simInfo, particleType, useRealGasCorrections);
        double rateWidth = Units.convert(Frequency.HERTZ, DEFAULT_UNIT, avgRate / Math.sqrt(avgRate * simulationTime));
        avgRate = Units.convert(Frequency.HERTZ, DEFAULT_UNIT, avgRate);
        double value = (Math.sqrt(2 * Math.PI) / rateWidth) * Math.exp(-Math.pow(x - avgRate, 2) / (2 * Math.pow(rateWidth, 2)));
        
        if(Double.isNaN(value))
            return 0.0;
        return value;
    }

    @Override
    public double getDistributionWidthPrediction(Frequency unit) {
        double colRate = Formulas.predictCollisionRate(simInfo, typesRecorded, useRealGasCorrections);
        return Units.convert(Frequency.HERTZ, unit, colRate / Math.sqrt(colRate * simulationTime));
    }

    @Override
    public double getCurrentDistributionWidth(Frequency unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getWidth());
    }

    @Override
    public double getCumulativeDistributionWidth(Frequency unit) {
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
    public Range getDistributionRange(Frequency unit) {
        return Units.convert(DEFAULT_UNIT, unit, distributionRange);
    }

    @Override
    public void setDistributionRange(Range range, Frequency unit) {
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
    public double getPredictionForAverage(Frequency unit) {
        return Units.convert(Frequency.HERTZ, unit, Formulas.predictCollisionRate(simInfo, typesRecorded, useRealGasCorrections));
    }

    @Override
    public double getCurrentAverage(Frequency unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getAverage());
    }

    @Override
    public double getCumulativeAverage(Frequency unit) {
        return Units.convert(DEFAULT_UNIT, unit, statisticTracker.getAverage());
    }

    @Override
    public Set<Frequency> getDisplayUnits() {
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public Frequency getDefaultDisplayUnit() {
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

///**
// * Created 16 May 2011
// * @author Derek Manwaring
// */
//public class CollisionRate extends WeightedValueStatistic {
//
//    private static final String DISPLAY_NAME = "Collision Rate";
//    private static final Unit DEFAULT_UNIT = Frequency.TERAHERTZ;
//    
//    private Map<Particle, Integer> collisions = new HashMap<Particle, Integer>();
//    
//    private Map<ParticleType, Double> currentAvgs = new HashMap<ParticleType, Double>();
//    private Map<ParticleType, Double> currentWidths = new HashMap<ParticleType, Double>();
//    
//    private double startTime = 0.0;
//    
//    //How often necessary variables for the prediction curve are recalculated and
//    //a new curve is requested for drawing.
//    private static final int FRAMES_PER_CURVE_UPDATE = 10;
//    private int framesPassed = 0;
//    
//    @Override
//    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
//        super.initiateStatistic(types, physics);
//        startTime = physics.currSimTime;
//        initCollisions(physics.getParticles());
//        updatePlot();
//    }
//    
//    private void initCollisions(Particle[] particles) {
//        for (Particle p: particles) {
//            collisions.put(p, 0);
//        }
//    }
//    
//    @Override
//    public void setLastFrameEndTime(double time) {
//        super.setLastFrameEndTime(time);
//        framesPassed++;
//        
//        if (framesPassed > FRAMES_PER_CURVE_UPDATE) {
//            updatePlot();
//            framesPassed = 0;
//        }
//        
//    }
//
//    @Override
//    protected void updateStatisticAvgs(FrameInfo frame) {
//        
//        framesPassed++;
//        
//        if (framesPassed > FRAMES_PER_CURVE_UPDATE) {
//            updatePlot();
//            framesPassed = 0;
//        }
//        
////        for (EventInfo event: frame.getEvents()) {
////            if (event.colType == Collision.PARTICLE) {
////                updateForCollision(event);
////            }
////        }               
//    }
//    
//    @Override
//    public void updateByEvent(EventInfo event) {
//        if (event.colType == Collision.PARTICLE) {
//            updateForCollision(event);
//        }        
//    }
//    
//    private static final int FRAMES_PER_PLOT_CHANGE = 25;
//    private int framesSinceChange = 0;
//    
//    private void updatePlot() {
//        distribWidth = Units.convert("Hz", "THz", calculateDistribWidth());
//        
//        for (ParticleType type: types) {
//            double colRate = Formulas.predictCollisionRate(simulationInfo, type, usingFiniteSysCorrections());
//            double width = colRateWidth(colRate, lastFrameEndTime);
//            currentAvgs.put(type, colRate);
//            currentWidths.put(type, width);
//        }
//        
//        requestNewPredictionCurve();   
//        
//        framesSinceChange++;
//        
//        if (framesSinceChange > FRAMES_PER_PLOT_CHANGE) {
//            //setupHistogram();
//            framesSinceChange = 0;
//        }
//    }
//    
//    private void updateForCollision(EventInfo event) {
//        for (Particle p: event.getInvolvedParticles()) {
//            if (shouldRecordStats(p.getDisplayColor())) {
//                int oldCols = collisions.get(p);
//                oldCols++;
//                collisions.put(p, oldCols);
////                double timePassed = event.colTime - startTime;
////                double colRate = Units.convert("Hz", "THz", oldCols / timePassed);
////                addWeightedValue(colRate, 1.0, p.getDisplayColor());
//            }
//        }
//    }
//
//    @Override
//    public void prepareInstCalculations(FrameInfo frame) {
//        resetInstData();
//        for (Particle p: frame.getParticles()) {
//            if (shouldRecordStats(p.getDisplayColor())) {
//                int numCols = collisions.get(p);
//                double timePassed = frame.endTime - startTime;
//                double colRate = Units.convert("Hz", "THz", numCols / timePassed);
//                addInstWeightedValue(colRate, 1.0, p.getDisplayColor());
//            }
//        }
//    }
//
//    @Override
//    protected double calculateDistribWidth() {
//        StatUtils.CalculatorByType colRateCalculator = new StatUtils.CalculatorByType() {
//            public double valueForType(ParticleType type) {
//                return Formulas.predictCollisionRate(simulationInfo, type, usingFiniteSysCorrections());
//            }
//        };
//        
//        StatUtils.CalculatorByType widthCalculator = new StatUtils.CalculatorByType() {
//            public double valueForType(ParticleType type) {
//                double collisionRate = Formulas.predictCollisionRate(simulationInfo, type, usingFiniteSysCorrections());
//                double simTime = lastFrameEndTime;
//                return colRateWidth(collisionRate, simTime);
//            }
//        };
//        
//        return StatUtils.weightedWidth(simulationInfo, types, colRateCalculator, widthCalculator);
//    }
//
//    @Override
//    protected double distributionFunction(double x, ParticleType particleType) {
//        double rightX = Units.convert("THz", "Hz", x);
//        double currentWidth = currentWidths.get(particleType);
//        return ((Math.sqrt(2.0 * Math.PI) / 
//                currentWidth) * 
//                Math.exp(-Math.pow(rightX - currentAvgs.get(particleType), 2) / 
//                (2.0 * Math.pow(
//                currentWidth,
//                2))));
//    }
//
//    @Override
//    protected double calculatePrediction() {
//        double colRate = Formulas.predictCollisionRate(simulationInfo, types, usingFiniteSysCorrections());
//        return Units.convert("Hz", "THz", colRate);
//        
////        timeUpdate=true;
////        nextTime=GUIGlobal.simTime*timeMult;
////        int start=Const.RED, end=Const.BLUE;
////        if(statColor!=Const.BOTH)
////                start=end=statColor;
////        for(int i=start;i<=end;i++)
////        {
////                statName==Const.COLRATE)
////                avgs[i]=1/tao[statColor];
////                wids[i]=(f[statName]*avgs[i]*Math.sqrt(tao[statColor]/GUIGlobal.simTime));
////        }
////        if(statColor==Const.BOTH)
////        {
////                returnVal.avg=weightedAvg(avgs[Const.RED],avgs[Const.BLUE]);
////                returnVal.wid=Math.sqrt((((N[Const.RED]*N[Const.BLUE]*Math.pow(avgs[Const.RED]-avgs[Const.BLUE],2))/(N[Const.BOTH]))+(N[Const.RED]*Math.pow(wids[Const.RED],2))+(N[Const.BLUE]*Math.pow(wids[Const.BLUE],2)))/(N[Const.BOTH]));
////        }
////        else
////        {
////                returnVal.avg=avgs[statColor];
////                returnVal.wid=wids[statColor];
////        }
//    }
//
//    @Override
//    public void drawPlot(PlotType plotType, Graphics graphics, StatPanel plotPanel, boolean instantaneous) {
//        super.drawPlot(plotType, graphics, plotPanel, true);
//    }
//    
//    @Override
//    public double getDefaultPlotMin() {
//        return getCalculation(StatUtils.PREDICTION) * 0.5;// - getCalculation(StatUtils.WIDTH_PREDICTION);
//    }
//    
//    @Override
//    public double getDefaultPlotMax() {
//        return getCalculation(StatUtils.PREDICTION) * 1.5;// getCalculation(StatUtils.WIDTH_PREDICTION);
//    }
//
//    @Override
//    public double getCalculation(StatisticCalculation calculation) {
//        if (calculation == StatUtils.AVERAGE) {
//            return super.getInstCalculation(StatUtils.AVERAGE);
//        } else {
//            return super.getCalculation(calculation);
//        }
//    }
//
//    @Override
//    public String getDisplayName() {
//        return DISPLAY_NAME;
//    }
//
//    @Override
//    public Unit getUnit(StatisticCalculation calculation) {
//        return DEFAULT_UNIT;
//    }
//    
//    @Override
//    public void reset(double newStartTime, Physics physics) {
//        super.reset(newStartTime, physics);
//        startTime = newStartTime;
//        initCollisions(physics.getParticles());
//    }    
//    
//    private static double colRateWidth(
//            double collisionRate,
//            double simulationTime
//            ) {
//        return collisionRate * Math.sqrt(1.0 / (collisionRate * simulationTime));
//    }
//}
