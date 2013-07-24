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

import edu.byu.chem.boltzmann.model.physics.*;
import edu.byu.chem.boltzmann.model.statistics.StatUtils.CalculatorByType;
import edu.byu.chem.boltzmann.model.statistics.interfaces.ProbabilityDensityFunctionPointCreater;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.utils.ParticleStateAnalyzer;
import edu.byu.chem.boltzmann.model.statistics.utils.WeightedValueTracker;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Velocity;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Joshua Olson
 * June 1, 2012
 */
public class ZVelocity implements StatisticWithDistribution<Velocity> {
    private static final Velocity DEFAULT_UNIT = Units.Velocity.METER_PER_SECOND;

    private boolean useFiniteSystemCorrections = false;
    
    private final ParticleStateAnalyzer stateAnalyzer;
    
    private final WeightedValueTracker<Velocity> currentWeightedValues = 
            new WeightedValueTracker<Velocity>(DEFAULT_UNIT, false);
    private HistogramBins currentBins;
    
    private final WeightedValueTracker<Velocity> cumulativeWeightedValues = 
            new WeightedValueTracker<Velocity>(DEFAULT_UNIT, false);
    private HistogramBins cumulativeBins;
    
    private final SimulationInfo simInfo;
    private final Set<ParticleType> typesRecorded;
    
    private Range distributionRange;
    private boolean hasNewPredictionCurve = true;
    
    private final ProbabilityDensityFunctionPointCreater predictionCurvePointCreater;
    
    public ZVelocity(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        stateAnalyzer = new ParticleStateAnalyzer(
            new ParticleStateAnalyzer.ParticleStateWatcher(types, simInfo.reactionMode) {

                @Override
                public void update(Particle particle, double timeElapsedInState) {
                    double value = particle.getZVel();
                    cumulativeWeightedValues.addWeightedValue(
                            value, timeElapsedInState);
                    cumulativeBins.addWeightedValue(value, timeElapsedInState, particle.getDisplayColor());
                }

                @Override
                public void update(Particle particle) {
                    double value = particle.getZVel();
                    currentWeightedValues.addWeightedValue(
                            value, 1.0);
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
        double predictedWidth = getDistributionWidthPrediction(DEFAULT_UNIT);
        distributionRange = new Range(-predictedWidth * 2.5, predictedWidth * 2.5);
        currentBins = new HistogramBins(distributionRange, 15, typesRecorded);
        cumulativeBins = new HistogramBins(distributionRange, 30, typesRecorded);
    }

    public static double averageVelocity(
            final SimulationInfo simInfo, 
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
        
        CalculatorByType speedCalculator = new CalculatorByType() {
            @Override
            public double valueForType(ParticleType type) {
                return 0;
            }            
        };
        
        return StatUtils.weightedAverage(simInfo, particleTypes, speedCalculator);
    }

    public static double velocityWidth(
            final SimulationInfo simInfo, 
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
                
        CalculatorByType speedCalculator = new CalculatorByType() {
            public double valueForType(ParticleType type) {
                return 0;
            }            
        };
        
        CalculatorByType widthCalculator = new CalculatorByType() {
            public double valueForType(ParticleType type) {
                    return Formulas.velocityComponentWidth(simInfo.initialTemperature,
                    Units.convert("amu", "kg", type.particleMass));
            }
        };
        
        return StatUtils.weightedWidth(simInfo, particleTypes, speedCalculator, widthCalculator);
    }

public double distributionFunction(double x, ParticleType particleType) {
        if (useFiniteSystemCorrections) {
            return correctedVelDistribution(x,
                    Units.convert("amu", "kg", particleType.particleMass),
                    simInfo.initialTemperature,
                    simInfo.dimension,
                    simInfo.isPeriodic(),
                    simInfo.totalNumParticles,
                    simInfo.totalInitialKE,
                    simInfo.totalMassOfParticles,
                    simInfo.minParticleMass);
        } else {
            return velocityDistribution(x,
                    Units.convert("amu", "kg", particleType.particleMass),
                    simInfo.initialTemperature,
                    simInfo.dimension);
        }
    }

    /**
     * Calculations here come from equations in the Boltzmann manual.
     */
    public static double velocityDistribution(double velocity, double mass, double temperature, int dimension) {
        double firstFactor = Math.sqrt(mass / (2 * Math.PI * Formulas.BOLTZMANN_CONST * temperature));
        double expFactor = - (mass * velocity * velocity) / (2.0 * Formulas.BOLTZMANN_CONST * temperature);

        return firstFactor * Math.exp(expFactor);
    }
    
    public static double correctedVelDistribution(
            double velocity, 
            double mass, 
            double temperature, 
            int dimension,
            boolean periodic,
            int totalParticles,
            double totalEnergy,
            double totalMass,
            double minMass) {
        
        int numParticles = totalParticles;
        if (periodic) {
            numParticles--;
        }

        double value = (Formulas.gamma(dimension * numParticles / 2.0) / Formulas.gamma((dimension * numParticles - 1) / 2.0)) * 
                        Math.sqrt(mass / 
                        (dimension * Math.PI * Formulas.BOLTZMANN_CONST * temperature * numParticles)) * 
                        Math.pow(1 - ((mass * velocity * velocity) / 
                        (dimension * Formulas.BOLTZMANN_CONST * temperature * numParticles)), 
                        (dimension * numParticles - 3) / 2.0);

        if (Double.isNaN(value)) {
            value = 0.0;
        }
        
        return value;
    }

    @Override
    public double getDistributionWidthPrediction(Velocity unit) {
        return Units.convert(DEFAULT_UNIT, unit, 
                velocityWidth(simInfo, typesRecorded, useFiniteSystemCorrections));
    }

    @Override
    public double getCurrentDistributionWidth(Velocity unit) {
        return currentWeightedValues.getWidth(unit);
    }

    @Override
    public double getCumulativeDistributionWidth(Velocity unit) {
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
    public Range getDistributionRange(Velocity unit) {
        return Units.convert(DEFAULT_UNIT, unit, distributionRange);
    }

    @Override
    public void setDistributionRange(Range range, Velocity unit) {
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
    public double getPredictionForAverage(Velocity unit) {
        return Units.convert(DEFAULT_UNIT, unit, averageVelocity(simInfo, typesRecorded, useFiniteSystemCorrections));
    }

    @Override
    public double getCurrentAverage(Velocity unit) {
        return currentWeightedValues.getAverage(unit);
    }

    @Override
    public double getCumulativeAverage(Velocity unit) {
        return cumulativeWeightedValues.getAverage(unit);
    }

    @Override
    public Set<Velocity> getDisplayUnits() {
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public Velocity getDefaultDisplayUnit() {
        return DEFAULT_UNIT;
    }

    @Override
    public void useFrameForCurrentCalculations(FrameInfo frame) {
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
    }
}
//    protected double calculatePrediction() {
//        return averageVelocity(simulationInfo, types, usingFiniteSysCorrections());
//
//    @Override
//    protected double calculateDistribWidth() {
//        return velocityWidth(simulationInfo, types, usingFiniteSysCorrections());
//    }
//
//    public static double velocityWidth(
//            final SimulationInfo simInfo, 
//            Set<ParticleType> particleTypes,
//            final boolean finiteCorrections) {
//                
//        CalculatorByType speedCalculator = new CalculatorByType() {
//            public double valueForType(ParticleType type) {
//                return 0;
//            }            
//        };
//        
//        CalculatorByType widthCalculator = new CalculatorByType() {
//            public double valueForType(ParticleType type) {
//                    return Formulas.velocityComponentWidth(simInfo.initialTemperature,
//                    Units.convert("amu", "kg", type.particleMass));
//            }
//        };
//        
//        return StatUtils.weightedWidth(simInfo, particleTypes, speedCalculator, widthCalculator);
//    }
