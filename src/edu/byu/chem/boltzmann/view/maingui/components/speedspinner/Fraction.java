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
package edu.byu.chem.boltzmann.view.maingui.components.speedspinner;

/**
 *
 * @author Derek Manwaring
 * 24 Oct 2011
 */
public class Fraction {
    
    private final int numerator;
    private final int denominator;
    private final double value;
    
    public Fraction(String fraction) {
        int dividerPos = fraction.indexOf('/');
        
        if (dividerPos == -1) { //Not found
            numerator = Integer.valueOf(fraction);
            denominator = 1;
        } else {
            numerator = Integer.valueOf(fraction.substring(0, dividerPos));
            denominator = Integer.valueOf(fraction.substring(dividerPos + 1, fraction.length()));
        }
        
        if (denominator == 0) {
            throw new NumberFormatException("Invalid denominator: 0");
        }
        
        value = ((double) numerator) / ((double) denominator);
    }
    
    public double getValue() {
        return value;        
    }
    
    @Override
    public String toString() {
        
        if (numerator == 0) {
            return "0";
        } else {
        
            String numeratorString = String.valueOf(numerator);        
            if (denominator == 1) {
                return numeratorString;
            } else {
                return numeratorString + "/" + denominator;
            }
        }        
    }
}
