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

/**
 *
 * @author Derek Manwaring
 * 27 Oct 2011
 */
public class IntegerTextField extends NumericTextField<Integer> {
    
    public static boolean isValidIntegerString(NumericTextField textField) {
        String inputString = textField.getText();

        try {
            Integer.parseInt(inputString);
            return true;
        } catch (NumberFormatException ex) {
            try {
                double doubleValue = Double.parseDouble(inputString);
                long longValue = Math.round(doubleValue);
                if ((longValue > Integer.MIN_VALUE) && (longValue < Integer.MAX_VALUE)) {
                    int rounded = (int) longValue;
                    textField.setText(Integer.toString(rounded));
                    return true;                    
                } else {
                    return false;
                }                
            } catch (NumberFormatException ex2) {
                return false;
            }
        }         
    }
    
    public IntegerTextField(int initialValue, int min, int max, int columns) {
        super(initialValue, min, max, columns);
    }

    @Override
    public Integer getNumericValue() {
        return Integer.parseInt(getText());
    }

    @Override
    public boolean isInputStringValid() {
        return isValidIntegerString(this);
    }
    
}
