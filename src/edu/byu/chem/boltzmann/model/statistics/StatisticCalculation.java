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

import edu.byu.chem.boltzmann.utils.Units.Unit;

/**
 * @author Derek Manwaring
 */
public class StatisticCalculation {
    public final String calculationName;
    public final String description;
    public final Unit units;

    /**
     * @param name Name of this calculation
     * @param description Description of the calculation
     * @param units Units of the calculation (<code>null</code> will use the default
     * units for the related statistic.)
     */
    public StatisticCalculation(String name, String description, Unit units) {
        this.calculationName = name;
        this.description = description;
        this.units = units;
    }
}
