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

package edu.byu.chem.boltzmann.model.statistics.utils;

import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Unit;

/**
 *
 * @author Derek Manwaring
 * 21 May 2012
 */
public class WeightedValueTracker<UnitType extends Unit<UnitType>> {

    private double totalWeightedValueSquares = 0;
    private double totalWeightedValues = 0;
    private double totalWeight = 0;
    
    private final UnitType baseUnit;
    boolean trackingRMSVelocity;

    public WeightedValueTracker(UnitType unitType, boolean trackRMSVelocity) {
        baseUnit = unitType;//.getBaseUnit();
        trackingRMSVelocity = trackRMSVelocity;
    }
    
    public void addWeightedValue(double value, /*UnitType valueUnit,*/ double weight) {
        //value = Units.convert(valueUnit, baseUnit, value);
        totalWeightedValueSquares += value * value * weight;
        totalWeightedValues += value * weight;
        totalWeight += weight;
    }

    public double getWidth(UnitType unit) {
        double average = getRawAverage();
        return Units.convert(baseUnit, unit, Math.sqrt(getRawRMS() - average * average));
    }
    
    public double getAverage(UnitType unit) {
        return Units.convert(baseUnit, unit, trackingRMSVelocity ? Math.sqrt(getRawRMS()) : getRawAverage());
    }
    
    private double getRawAverage() {
        if (totalWeight == 0.0)
            return 0.0;
        return totalWeightedValues / totalWeight;
    }
    
    private double getRawRMS() {
        if (totalWeight == 0.0)
            return 0.0;
        return totalWeightedValueSquares / totalWeight;
    }
    
    public void clear() {
        totalWeightedValueSquares = 0.0;
        totalWeightedValues = 0.0;
        totalWeight = 0.0;
    }
}
