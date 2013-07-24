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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * 21 May 2012
 */
public class InstantaneousSpeed implements StatisticWithDistribution<Velocity> {
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

    public InstantaneousSpeed(SimulationInfo simInfo, Set<ParticleType> types) {
        this.simInfo = simInfo;
        typesRecorded = types;
        
        stateAnalyzer = new ParticleStateAnalyzer(
            new ParticleStateAnalyzer.ParticleStateWatcher(types, simInfo.reactionMode) {

                @Override
                public void update(Particle particle, double timeElapsedInState) {
                    double value = particle.getVel();
                    cumulativeWeightedValues.addWeightedValue(
                            value, timeElapsedInState);
                    cumulativeBins.addWeightedValue(value, timeElapsedInState, particle.getDisplayColor());
                }

                @Override
                public void update(Particle particle) {
                    double value = particle.getVel();
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
        
        distributionRange = new Range(0.0, getPredictionForAverage(DEFAULT_UNIT) * 2.5);
        currentBins = new HistogramBins(distributionRange, 15, typesRecorded);
        cumulativeBins = new HistogramBins(distributionRange, 30, typesRecorded);
    }

    /**
     * @return Predicted average velocity in m/s
     */
    public static double averageVelocity(
            final SimulationInfo simInfo, 
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
        
        CalculatorByType speedCalculator = new CalculatorByType() {
            @Override
            public double valueForType(ParticleType type) {
                if (finiteCorrections) {
                    return predictAvgWithCorrections(simInfo, type);
                } else {
                    return predictAvgForType(simInfo, type);
                }
            }            
        };
        
        return StatUtils.weightedAverage(simInfo, particleTypes, speedCalculator);
    }

    public static double velocityWidth(
            final SimulationInfo simInfo, 
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
                
        CalculatorByType speedCalculator = new CalculatorByType() {
            @Override
            public double valueForType(ParticleType type) {
                if (finiteCorrections) {
                    return predictAvgWithCorrections(simInfo, type);
                } else {
                    return predictAvgForType(simInfo, type);
                }
            }            
        };
        
        CalculatorByType widthCalculator = new CalculatorByType() {
            @Override
            public double valueForType(ParticleType type) {
                if (finiteCorrections) {
                    return Formulas.correctedVelocityWidth(
                            simInfo.initialTemperature,
                            Units.convert("amu", "kg", type.particleMass), 
                            simInfo.dimension,
                            simInfo.isPeriodic(),
                            simInfo.totalNumParticles,
                            Units.convert("amu", "kg", simInfo.totalMassOfParticles));
                } else {
                    return Formulas.velocityWidth(simInfo.initialTemperature,
                    Units.convert("amu", "kg", type.particleMass), simInfo.dimension);
                }
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
        double firstFactor = 0.0;
        double expFactor = - (mass * velocity * velocity) / (2.0 * Formulas.BOLTZMANN_CONST * temperature);

        switch (dimension) {
            case 1:
                firstFactor = Math.sqrt(mass / (Formulas.BOLTZMANN_CONST * temperature));
                break;
            case 2:
                firstFactor = mass * velocity / (Formulas.BOLTZMANN_CONST * temperature);
                break;
            case 3:
                double fourPi = 4.0 * Math.PI;
                double mrtFactor = mass / (2.0 * Math.PI * Formulas.BOLTZMANN_CONST * temperature);
                double velSquared = velocity * velocity;
                firstFactor = fourPi * Math.pow(mrtFactor, 3.0 / 2.0) * velSquared;
        }

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
        // calculate cutoff for step function
        // (1) find minimum particle mass
        // (2) find total energy, amu*(m/s)^2
        // assume the minimum-mass particle has all the energy
        double minMassKG = Units.convert("amu", "kg", minMass);
        
        double maxEnergyForOne = totalEnergy;
        
        if (periodic) {
            maxEnergyForOne = totalEnergy * (totalMass - minMass) / totalMass;
        }
        
        double maxSpeed = Math.sqrt(2.0 * maxEnergyForOne / minMassKG);

        double value = 0.0;
        
        int numParticles = totalParticles;
        if (periodic) {
            numParticles--;
        }
                
        /** 
         * These are all derived from equation (12) in the paper, "Periodic boundary condition 
         * induced breakdown of the equipartition principle and other kinetic effects of finite 
         * sample size in classical hard-sphere molecular dynamics simulation", Shirts, Burt
         * and Johnson 2006.
         */
        
        /**
         * First, the power of the factor (1-(v_1/v_1m)^2) changes from d(N-1)/2-1 to d(N-2)/2-1  
         * (This looks like it is done correctly) and 
         * v_1m changes from (dNkT/m_1)^1/2 to 
         * (dNkT*(M_tot-m_1)/(m_1*M_tot))^1/2.  This change was not made.
         */
        switch (dimension) {
            case 1:
                value = (Formulas.gamma(numParticles / 2.0) / Formulas.gamma((numParticles - 1) / 2.0)) * 
                        Math.sqrt(4.0 * mass / 
                        (Math.PI * Formulas.BOLTZMANN_CONST * temperature * numParticles)) * 
                        Math.pow(1 - ((mass * velocity * velocity) / 
                        (Formulas.BOLTZMANN_CONST * temperature * numParticles)), 
                        (numParticles - 3) / 2.0);
                break;
            case 2:
                value = ((numParticles - 1) * mass * velocity) / 
                        (Formulas.BOLTZMANN_CONST * temperature * numParticles) * 
                        Math.pow(1 - ((mass * velocity * velocity) / 
                        (2.0 * Formulas.BOLTZMANN_CONST * temperature * numParticles)), 
                        numParticles - 2);
                break;
            case 3:
                value = (Formulas.gammaFrac(3 * numParticles / 2.0,
                        (3 * numParticles - 3) / 2.0)) * 
                        Math.pow(mass / (3 * Formulas.BOLTZMANN_CONST * temperature * numParticles), 1.5) * 
                        ((4 * velocity * velocity) / Math.sqrt(Math.PI)) * 
                        Math.pow(1 - ((mass * velocity * velocity) / 
                        (3.0 * Formulas.BOLTZMANN_CONST * temperature * numParticles)), 
                        (3 * numParticles - 5) / 2.0);
        }

        // step function (cutoff)
        if (velocity > maxSpeed) {
            value = 0.0;
        }

        if (Double.isNaN(value)) {
            value = 0.0;
        }
        
        return value;
    }

    protected static double predictAvgForType(SimulationInfo simInfo, ParticleType type) {
        double avgSpeedForType = Formulas.averageVelocity(simInfo.initialTemperature,
                Units.convert("amu", "kg", type.particleMass), simInfo.dimension);
        return avgSpeedForType;
    }

    protected static double predictAvgWithCorrections(SimulationInfo simInfo, ParticleType type) {
        double avgSpeedForType = Formulas.correctedAverageVelocity(
                simInfo.initialTemperature,
                Units.convert("amu", "kg", type.particleMass), 
                simInfo.dimension,
                simInfo.isPeriodic(),
                simInfo.totalNumParticles,
                Units.convert("amu", "kg", simInfo.totalMassOfParticles));
        return avgSpeedForType;
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
