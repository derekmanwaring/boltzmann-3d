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

/**
 *
 * @author Derek Manwaring
 * 18 May 2012
 */
public class Range {

    public final double min;
    public final double max;
    
    public Range(double midpoint, double distanceToMax, boolean implicitBounds) {
        this.max = midpoint + distanceToMax;
        this.min = midpoint - distanceToMax;
        checkInvariant();
    }
    
    public Range(double min, double max) {
        this.min = min;
        this.max = max;
        checkInvariant();
    }

    public double getMidpoint() {
        return (max + min) / 2.0;
    }
    
    @Override
    public String toString() {
        return "Range [" + min  + ", " + max + "]";
    }

    private void checkInvariant() {
        if (min > max) {
            throw new IllegalStateException("Min cannot be greater than max");
        }
    }

    public double getDistanceFromMinToMax() {
        return max - min;
    }

    public double getIncrement(int numberOfIncrements) {
        return (max - min) / (double) numberOfIncrements;
    }
}
