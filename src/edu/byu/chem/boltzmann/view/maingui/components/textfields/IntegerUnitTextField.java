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
package edu.byu.chem.boltzmann.view.maingui.components.textfields;

import edu.byu.chem.boltzmann.utils.Units.Unit;

/**
 *
 * @author Derek Manwaring
 * 14 Nov 2011
 */
public class IntegerUnitTextField<UnitType extends Unit<UnitType>> extends UnitTextField<UnitType> {   
    
    public IntegerUnitTextField(UnitType associatedUnits, double initialValue, double min, double max, int columns) {
        super(associatedUnits, initialValue, min, max, columns);
    }
    
    @Override    
    public boolean isInputStringValid() {
        return IntegerTextField.isValidIntegerString(this);
    }
    
    @Override    
    public void setValueInUnits(double newValue, UnitType newValueUnits) {
        super.setValueInUnits(newValue, newValueUnits);
        isInputStringValid();
    }
}
