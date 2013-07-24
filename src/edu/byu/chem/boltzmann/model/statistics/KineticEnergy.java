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
import edu.byu.chem.boltzmann.model.statistics.parents.TimeWeightedByParticleCollisions;
import edu.byu.chem.boltzmann.model.statistics.StatUtils.CalculatorByType;
import edu.byu.chem.boltzmann.model.statistics.interfaces.ProbabilityDensityFunctionPointCreater;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.utils.ParticleStateAnalyzer;
import edu.byu.chem.boltzmann.model.statistics.utils.WeightedValueTracker;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Energy;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 */
public class KineticEnergy implements StatisticWithDistribution<Energy> {
    private static final Energy DEFAULT_UNIT = Units.Energy.KILOJOULE_PER_MOLE;

    private boolean useFiniteSystemCorrections = false;
    
    private final ParticleStateAnalyzer stateAnalyzer;
    
    private final WeightedValueTracker<Units.Energy> currentWeightedValues = 
            new WeightedValueTracker<Units.Energy>(DEFAULT_UNIT, false);
    private HistogramBins currentBins;
    
    private final WeightedValueTracker<Units.Energy> cumulativeWeightedValues = 
            new WeightedValueTracker<Units.Energy>(DEFAULT_UNIT, false);
    private HistogramBins cumulativeBins;
    
    private final SimulationInfo simulationInfo;
    private final Set<ParticleType> typesRecorded;
    
    private Range distributionRange;
    private boolean hasNewPredictionCurve = true;
    
    private final ProbabilityDensityFunctionPointCreater predictionCurvePointCreater;

    public KineticEnergy(SimulationInfo simInfo, Set<ParticleType> types) {
        simulationInfo = simInfo;
        typesRecorded = types;
        
        stateAnalyzer = new ParticleStateAnalyzer(
            new ParticleStateAnalyzer.ParticleStateWatcher(types, simInfo.reactionMode) {

                @Override
                public void update(Particle particle, double timeElapsedInState) {
                    double energy = Formulas.kineticEnergy(particle.getMass(), particle.getVel());
                    energy = Units.convert(Energy.AMU_JOULE, DEFAULT_UNIT, energy);
                    cumulativeWeightedValues.addWeightedValue(energy, timeElapsedInState);
                    cumulativeBins.addWeightedValue(energy, timeElapsedInState, particle.getDisplayColor());
                }

                @Override
                public void update(Particle particle) {
                    double energy = Formulas.kineticEnergy(particle.getMass(), particle.getVel());
                    energy = Units.convert(Energy.AMU_JOULE, DEFAULT_UNIT, energy);
                    currentWeightedValues.addWeightedValue(energy, 1.0);
                    currentBins.addWeightedValue(energy, 1.0, particle.getDisplayColor());
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
    
    public static double averageKineticEnergy(
            final SimulationInfo simInfo,
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
        double prediction = 0.0;
        CalculatorByType KECalculator = new CalculatorByType() {
            public double valueForType(ParticleType type) {
                if (finiteCorrections && simInfo.isPeriodic()) {
                    return Formulas.correctedKE(
                                Units.convert("amu", "kg", type.particleMass),
                                simInfo.initialTemperature,
                                simInfo.dimension,
                                simInfo.totalNumParticles,
                                Units.convert("amu", "kg", simInfo.totalMassOfParticles)
                                );
                } else {
                    return Formulas.avgKineticEnergy(
                                simInfo.initialTemperature, 
                                simInfo.dimension);
                }
            }
        };
        
        prediction = StatUtils.weightedAverage(simInfo, particleTypes, KECalculator);
        
        return Units.convert(Energy.JOULE, DEFAULT_UNIT, prediction);
    }
    
    public static double kineticEnergyWidth(
            final SimulationInfo simInfo, 
            Set<ParticleType> particleTypes,
            final boolean finiteCorrections) {
                
        CalculatorByType KECalculator = new CalculatorByType() {
            public double valueForType(ParticleType type) {
                if (finiteCorrections && simInfo.isPeriodic()) {
                    return Formulas.correctedKE(
                                Units.convert("amu", "kg", type.particleMass),
                                simInfo.initialTemperature,
                                simInfo.dimension,
                                simInfo.totalNumParticles,
                                Units.convert("amu", "kg", simInfo.totalMassOfParticles)
                                );
                } else {
                    return Formulas.avgKineticEnergy(
                                simInfo.initialTemperature, 
                                simInfo.dimension);
                }
            }
        };
        
        CalculatorByType widthCalculator = new CalculatorByType() {
            @Override
            public double valueForType(ParticleType type) {
                if (finiteCorrections) {
                    return Formulas.correctedKEWidth(
                            simInfo.initialTemperature,
                            Units.convert("amu", "kg", type.particleMass), 
                            simInfo.dimension,
                            simInfo.isPeriodic(),
                            simInfo.totalNumParticles,
                            Units.convert("amu", "kg", simInfo.totalMassOfParticles));
                } else {
                    return Formulas.KEWidth(simInfo.initialTemperature,
                    Units.convert("amu", "kg", type.particleMass), simInfo.dimension);
                }
            }
        };
        
        double prediction = StatUtils.weightedWidth(simInfo, particleTypes, KECalculator, widthCalculator);

        return Units.convert(Energy.JOULE, DEFAULT_UNIT, prediction);
    }

    public double distributionFunction(double x, ParticleType particleType) {
        if (useFiniteSystemCorrections) {
            return correctedKEDistrib(x,
                    simulationInfo.dimension,
                    simulationInfo.isPeriodic(),
                    simulationInfo.totalNumParticles,
                    simulationInfo.totalInitialKE);
        } else {
            return kinEnergyDistribution(x, 
                    simulationInfo.initialTemperature,
                    simulationInfo.dimension);
        }
    }

    private static double kinEnergyDistribution(double energy, double temperature, int dimension) {
        return (Math.pow(energy, (dimension - 2) / 2.0) * 
                Math.exp( -(energy / (Formulas.GAS_CONSTANT * temperature))));
    }
    
    private static double correctedKEDistrib(
            double energy, 
            int dimension,
            boolean periodic,
            int totalParticles,
            double totalEnergy) {
        
        /** 
         * This is equation (9) in the paper, "Periodic boundary condition induced breakdown of the equipartition
         * principle and other kinetic effects of finite sample size in classical hard-sphere molecular dynamics
         * simulation", Shirts, Burt and Johnson 2006.
         */

        int numParticles = totalParticles;
        
        if (periodic) {
            numParticles--;
        }
        
        double E1 = Units.convert("kJ/mol", "J/mol", energy);

        double value = 0.0;
        // Total energy of the system
        double E = Units.convert("J", "J/mol", totalEnergy);
//        System.err.println("E1 = " + E1 + ", E = " + E);
        // No one particle can have more than the total energy
        // (This implements the step function in the equation)
        if (E1 > E) return 0.0;

        //TODO: Clean up - Derek Manwaring 12 May 2011
        switch (dimension) {
            case 1:
                value = Math.sqrt (1.0 / Math.PI)                         // TERM 1: 1/Gamma(1/2) = 1/Sqrt(PI) 
                 * Formulas.gammaFrac (numParticles / 2.0, (numParticles - 1.0) / 2.0) // TERM 2: Gamma(dN/2)/Gamma(d(N-1)/2)
                 * Math.sqrt (1.0 / (energy * E))                              // TERMS 3 AND 6 combined: (E1/E)^(d/2) * (1/E1) => [1/(E1 E)]^(1/2)
                 * Math.pow ( 1.0 - (E1 / E), (numParticles - 3.0) / 2.0)     // TERM 4: (1-E1/E)^[d(N-1)/2-1] = (1-E1/E)^[(N-3)/2]
                 ;

                // Previous formula:
                /*
                value = gammaFrac ( Ncorrected / 2.0, (Ncorrected - 1.0) / 2.0 ) // Gamma(dN/2)/Gamma(d(N-1)/2)
                 * Math.sqrt ( 2.0 / (x * Math.PI * Units.AVAG * RT[statColor] * Ncorrected) ) // (...)^(d/2) term w/ extras
                 * Math.pow (1.0 - ((2.0 * x) / (Ncorrected * Units.AVAG * RT[statColor])), (Ncorrected - 3) / 2.0);
                */

                break;
            case 2:
                value = 
                        // TERM 1 = 1.0
                        (numParticles - 1.0)  						 // TERM 2 = (N-1)
                        * E                 						 // TERMS 3 and 6 combined = E
                        * Math.pow ( 1.0 - E1 / E, numParticles - 2.0) // TERM 4: (1-E1/E)^(N-2)
                        // TERM 5 is the step function, already evaluated as a separate case
                        ;
                /*
                value = ( (Ncorrected - 1.0) / (RT[statColor] * Ncorrected) ) // ??? (1 / N kB T), (E1/E)^(d/2) * (1/E1) term
                 * Math.pow (1.0 - (x / (Ncorrected * Units.AVAG * RT[statColor] / 1000.0)), Ncorrected - 2.0);
                 */
                /*
                value = Math.pow (1.0 - (x / (Ncorrected * Units.AVAG * RT[statColor] / 1000.0)), Ncorrected - 2.0);
                */

                break;

            case 3:
                // BROKEN:
                value = 
                        (2.0 / Math.sqrt(Math.PI)) // TERM 1, d=3: 1/Gamma(3/2) is 2.0/Sqrt(PI)
                         * Formulas.gammaFrac(1.5 * numParticles, 1.5 * (numParticles - 1.0)) // TERM 2, d=3: Gamma(3N/2)/Gamma(3(N-1)/2)
                         * Math.sqrt (E1 / (E * E * E))
                         //* Math.pow ( E1 / E, 1.5 ) / E1 // TERMS 3 and 6, (E1/E)^(3/2) / E1
                         * Math.pow (1.0 - E1/E, 3.0 * (numParticles - 1.0) / 2.0 - 1.0)
                         // (1-E1/E)^[d(N-1)/2-1]
//								 * Math.pow ( 1.0 - (E1 / E), (1.5 * Ncorrected) - 2.5 ) // TERM 4, d=3: (1-E1/E) ^ (1.5*N - 2.5)
                ;

                // Original
                /*
                value = gammaFrac ( 3.0 * Ncorrected / 2.0, 3.0 * (Ncorrected - 1) / 2.0 ) // Gamma(3N/2)/Gamma(3(N-1)/2)
                 * (2.0 / Math.sqrt(Math.PI)) // 1/Gamma(3/2)
                 * Math.pow (2.0 / (3.0 * Units.AVAG * RT[statColor] * Ncorrected), 1.5) // (E_1/E)^d/2
                 * Math.pow (1.0 - ((2.0 * x) / (3.0 * Ncorrected * Units.AVAG * RT[statColor])), (3.0 * Ncorrected - 5.0) / 2.0);
                 */

        }
        if ( Double.isNaN ( value ) ) {
                value = 0.0;
        }
        return value;
    }

    @Override
    public double getDistributionWidthPrediction(Energy unit) {
        return Units.convert(DEFAULT_UNIT, unit, kineticEnergyWidth(simulationInfo, typesRecorded, useFiniteSystemCorrections));
    }

    @Override
    public double getCurrentDistributionWidth(Energy unit) {
        return currentWeightedValues.getWidth(unit);
    }

    @Override
    public double getCumulativeDistributionWidth(Energy unit) {
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
    public Range getDistributionRange(Energy unit) {
        return Units.convert(DEFAULT_UNIT, unit, distributionRange);
    }

    @Override
    public void setDistributionRange(Range range, Energy unit) {
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
    public double getPredictionForAverage(Energy unit) {
        return Units.convert(DEFAULT_UNIT, unit, averageKineticEnergy(simulationInfo, typesRecorded, useFiniteSystemCorrections));
    }

    @Override
    public double getCurrentAverage(Energy unit) {
        return currentWeightedValues.getAverage(unit);
    }

    @Override
    public double getCumulativeAverage(Energy unit) {
        return cumulativeWeightedValues.getAverage(unit);
    }

    @Override
    public Set<Energy> getDisplayUnits() {
        return EnumSet.of(DEFAULT_UNIT);
    }

    @Override
    public Energy getDefaultDisplayUnit() {
        return DEFAULT_UNIT;
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

    @Override
    public void useFrameForCurrentCalculations(FrameInfo frame) {
        currentWeightedValues.clear();
        currentBins.resetBins();
        stateAnalyzer.analyzeFrame(frame);
    }
}
