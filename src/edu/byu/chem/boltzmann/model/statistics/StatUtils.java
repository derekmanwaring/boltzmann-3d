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

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.Set;

/**
 * Created Feb 2011
 * @author Derek Manwaring
 */
public class StatUtils {

    private static final String AVERAGE_NAME = "Average";
    private static final String AVERAGE_DESCRIPTION = "Simulated Average";

    private static final String PREDICTION_NAME = "Prediciton";
    private static final String PREDICTION_DESCRIPTION = "Theoretical Average";

    private static final String WIDTH_NAME = "Width";
    private static final String WIDTH_DESCRIPTION = "Simulated average width";

    private static final String WIDTH_PREDICTION_NAME = "Prediction";
    private static final String WIDTH_PREDICTION_DESCRIPTION = "Theorectical width";

    // Calculations supplied by most statistics
    public static final StatisticCalculation AVERAGE = new StatisticCalculation(
            AVERAGE_NAME, AVERAGE_DESCRIPTION, null);
    public static final StatisticCalculation PREDICTION = new StatisticCalculation(
            PREDICTION_NAME, PREDICTION_DESCRIPTION, null);
    public static final StatisticCalculation WIDTH = new StatisticCalculation(
            WIDTH_NAME, WIDTH_DESCRIPTION, null);
    public static final StatisticCalculation WIDTH_PREDICTION = new StatisticCalculation(
            WIDTH_PREDICTION_NAME, WIDTH_PREDICTION_DESCRIPTION, null);

    public interface CalculatorByType {
        public double valueForType(ParticleType type);
    }
    
    public static double weightedAverage(
            SimulationInfo simInfo, 
            Set<ParticleType> types,
            CalculatorByType averager
            ) {
        double totalAvg = 0.0;
        int totalParticles = 0;
        for (ParticleType type: types) {
            double avgForType = 0.0;
            avgForType = averager.valueForType(type);
            int particles = simInfo.getNumberOfParticles(type);
            totalAvg += avgForType * particles;
            totalParticles += particles;
        }

        if (totalParticles == 0) {
            return 0.0;
        } else {
            return totalAvg / totalParticles;
        }
    }
    
    public static double weightedWidth(
            SimulationInfo simInfo,
            Set<ParticleType> types,
            CalculatorByType averager,
            CalculatorByType width) {
        
        int totalParticles = 0;
        for (ParticleType type: types) {
            int particles = simInfo.getNumberOfParticles(type);
            totalParticles += particles;
        }

        double sumWidsAndAvgs = 0.0;
        double sumAvgs = 0.0;
        for (ParticleType type: types) {
            double typeWid = width.valueForType(type);
            double typeAvg = averager.valueForType(type);
            double weight = ((double) simInfo.getNumberOfParticles(type)) / totalParticles;
            sumWidsAndAvgs += weight * ((typeWid * typeWid) + (typeAvg * typeAvg));
            sumAvgs += weight * typeAvg;
        }
        sumAvgs = sumAvgs * sumAvgs;

        if (totalParticles == 0) {
            return 0.0;
        } else {
            return Math.sqrt(sumWidsAndAvgs - sumAvgs);
        }
    }
    
    /**
     * Rounds to 2 significant figures, relatively. The larger magnitude
     * value determines where these significant figures lie.
     */
    public static double[] roundValues (Double value1, Double value2) {
            if (value1.isInfinite() || value1.isNaN() || value1 == 0 ||
                    value2.isInfinite() || value2.isNaN() || value2 == 0)
                    return new double[]{roundValue(value1), roundValue(value2)};

            double val1 = value1;
            double val2 = value2;
            double biggerValue;

            //Significant figures are determined by the larger magnitude value.
            if (Math.abs(val1) >= Math.abs(val2))
                    biggerValue = val1;
            else
                    biggerValue = val2;

            int shift = 0;
            if (Math.abs(biggerValue) >= 1) // |biggerValue| >= 1
            {
                    //Make sure we're starting with a value >= 10 (2 digits).
                    val1 *= 10;
                    val2 *= 10;
                    biggerValue *= 10;

                    //Consider the two leading numbers.
                    while (Math.abs(biggerValue) > 100)
                    {
                            val1 /= 10.0;
                            val2 /= 10.0;
                            biggerValue /= 10.0;
                            shift++;
                    }

                    //Set all lesser numbers to zero.
                    val1 = Math.round(val1);
                    val2 = Math.round(val2);

                    //Restore original magnitude.
                    for (; shift > 0; shift--)
                    {
                            val1 *= 10;
                            val2 *= 10;
                    }

                    //One last divide to offset the first multiplication.
                    val1 /= 10;
                    val2 /= 10;
            }
            else // 0 < |biggerValue| < 1
            {
                    //Consider the two leading numbers.
                    while (Math.abs(biggerValue) < 10)
                    {
                            val1 *= 10;
                            val2 *= 10;
                            biggerValue *= 10;
                            shift++;
                    }

                    //Set all lesser numbers to zero.
                    val1 = Math.round(val1);
                    val2 = Math.round(val2);

                    //Restore original magnitude.
                    for (; shift > 0; shift--)
                    {
                            val1 /= 10.0;
                            val2 /= 10.0;
                    }
            }

            return new double[]{val1, val2};
    }

    /** Rounds to 2 significant figures. */
    protected static double roundValue (Double value) {
            if (value.isInfinite() || value.isNaN() || value == 0)
                    return value;

            double val = value;
            int shift = 0;
            if (Math.abs(val) >= 1) // |val| >= 1
            {
                    //Make sure we're starting with a value >= 10 (2 digits).
                    val *= 10;

                    //Consider the two leading numbers.
                    while (Math.abs(val) > 100)
                    {
                            val /= 10.0;
                            shift++;
                    }

                    //Set all lesser numbers to zero.
                    val = Math.round(val);

                    //Restore original magnitude.
                    for (; shift > 0; shift--)
                            val *= 10;

                    //One last divide to offset the first multiplication.
                    val /= 10;
            }
            else // 0 < |val| < 1
            {
                    //Consider the two leading numbers.
                    while (Math.abs(val) < 10)
                    {
                            val *= 10;
                            shift++;
                    }

                    //Set all lesser numbers to zero.
                    val = Math.round(val);

                    //Restore original magnitude.
                    for (; shift > 0; shift--)
                            val /= 10.0;
            }

            return val;
    }
}
