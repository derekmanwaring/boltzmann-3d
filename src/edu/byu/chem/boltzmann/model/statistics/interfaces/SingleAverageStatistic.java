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

import edu.byu.chem.boltzmann.utils.Units.Unit;
import java.util.Set;

/**
 * Statistics that provide a predicted and measured average.
 * 
 * @author Derek Manwaring
 * 18 May 2012
 */
public interface SingleAverageStatistic<UnitType extends Unit<UnitType>> extends Statistic {
    
    /**
     * @param unit
     * @return The predicted average for this statistic in the requested unit,
     * taking finite system corrections into account if necessary.
     */
    public double getPredictionForAverage(UnitType unit);
    
    /**
     * @param unit
     * @return The current measured average for this statistic in the requested 
     * unit.
     */
    public double getCurrentAverage(UnitType unit);
    
    /**
     * @param unit
     * @return The cumulative measured average for this statistic from the last
     * time its data was cleared. The average will be in the requested unit.
     */
    public double getCumulativeAverage(UnitType unit);
    
    /**
     * @return Set of units to choose from when displaying this statistic.
     */
    public Set<UnitType> getDisplayUnits();
    
    /**
     * @return Set of units to choose from when displaying this statistic.
     */
    public UnitType getDefaultDisplayUnit();
    
}
