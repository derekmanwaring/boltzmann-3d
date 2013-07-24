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

package edu.byu.chem.boltzmann.model.statistics.interfaces;

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * 25 May 2012
 */
public abstract class ProbabilityDensityFunctionPointCreater {

    private final Set<ParticleType> types;
    private final SimulationInfo simInfo;
    
    public ProbabilityDensityFunctionPointCreater(Set<ParticleType> types, SimulationInfo simInfo) {
        this.types = types;
        this.simInfo = simInfo;
    }
    
    public double[] createPointsForProbabilityDensityFunction(
            Range domainRange,
            int points) {
        
        double[] distribution = new double[ points ];
        double span = domainRange.getDistanceFromMinToMax();

        for (int i = 0; i < points; i++) {
            double xValue = span * (((double) i) + 0.5) / ((double) points) + domainRange.min;

            double yValue = 0.0;
            int totalParticles = 0;
            for (ParticleType type: types) {
                //Probability for current x value
                double yValueForType = probabilityDensity(xValue, type);
                int particles = simInfo.getNumberOfParticles(type);
                yValue += yValueForType * particles;
                totalParticles += particles;
            }
            
            double pointHeight = yValue / totalParticles;
            
            distribution[ i ] = pointHeight;
        }

        return distribution;        
    }
    
    public abstract double probabilityDensity(double value, ParticleType type);
    
}
