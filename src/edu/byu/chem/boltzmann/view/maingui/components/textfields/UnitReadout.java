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

import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.JTextField;

/**
 *
 * @author Derek Manwaring
 * 11 May 2012
 */

public class UnitReadout<UnitType extends Unit<UnitType>> extends JTextField {
    
    private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("0.000");
    private static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("0.000E0");
        
    private static final int MAX_DESIRED_OFFSET = 2;
    private static final int MIN_DESIRED_OFFSET = 0;
    
    private final UnitType baseUnit;
    private final Set<UnitType> availableUnits;
    
    private double baseUnitValue = Double.NaN;
    private double predictionBaseUnitValue = Double.NaN;
    private UnitType displayUnit = null;
    private UnitType predictionDisplayUnit = null;
    
    @SuppressWarnings("unchecked")
    public UnitReadout(double initialValue, UnitType initialValueUnits, Set<UnitType> unitsToUseForDisplay) {
        setEditable(false);
        setColumns(9);
        availableUnits = unitsToUseForDisplay;
        baseUnit = initialValueUnits.getBaseUnit();
        setValue(initialValue, initialValueUnits);
    }
    
    public void setMaxDecimalDigits(int max) {
        DEFAULT_FORMAT.setMaximumFractionDigits(max);
        SCIENTIFIC_FORMAT.setMaximumFractionDigits(max);
        setTextWithCurrentDisplayUnit();
    }
    
    public final void setValue(double value, UnitType valueUnits) {
        double newBaseUnitValue = Units.convert(valueUnits, baseUnit, value);
        
        if (newBaseUnitValue == baseUnitValue) {
            return;
        }    
        
        baseUnitValue = newBaseUnitValue;

        determineBestUnitForSmallestLeftmostDigitOffset();
        
        setTextWithCurrentDisplayUnit();
    }
    
    public final void setPredictionValue(double value, UnitType valueUnits) {
        double newBaseUnitValue = Units.convert(valueUnits, baseUnit, value);
        
        if (newBaseUnitValue == predictionBaseUnitValue) {
            return;
        }    
        
        predictionBaseUnitValue = newBaseUnitValue;

        determineBestPredictionUnitForSmallestLeftmostDigitOffset();
        
        setToolTipTextWithCurrentDisplayUnit();
    }
    
    private void setTextWithCurrentDisplayUnit() {
        double displayUnitValue = Units.convert(baseUnit, displayUnit, baseUnitValue);
        
        String formattedDisplayValue;
        if (calculateDistanceFromDesiredRange(calculateLeftmostDigitOffset(
                displayUnitValue)) > 2) {
            formattedDisplayValue = SCIENTIFIC_FORMAT.format(displayUnitValue);
        } else {
            formattedDisplayValue = DEFAULT_FORMAT.format(displayUnitValue);
        }
        setText(formattedDisplayValue + " " + displayUnit.getSymbol());
    }
    
    private void setToolTipTextWithCurrentDisplayUnit() {
        double displayUnitValue = Units.convert(baseUnit, predictionDisplayUnit, predictionBaseUnitValue);
        
        String formattedDisplayValue;
        if (calculateDistanceFromDesiredRange(calculateLeftmostDigitOffset(
                displayUnitValue)) > 2) {
            formattedDisplayValue = SCIENTIFIC_FORMAT.format(displayUnitValue);
        } else {
            formattedDisplayValue = DEFAULT_FORMAT.format(displayUnitValue);
        }
        setToolTipText("Prediction: " + formattedDisplayValue + " " + predictionDisplayUnit.getSymbol());
    }

    /**
     * 
     * @param number
     * @return The offset of the leftmost digit in <code>number</code> from the
     * decimal point. Offsets to the left of the decimal point are positive. The
     * offset of a digit in the ones' place is zero.
     * Examples:
     * Offset of 0.0 is one.
     * Offset of 1.0 is zero.
     * Offset of 100.2 is two.
     * Offset of 0.01 is negative two.
     * Offset of -34.9 is one.
     */
    private static int calculateLeftmostDigitOffset(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new RuntimeException("No leftmost digit valid for " + number);
        }
        
        if (number == 0.0) {
            return 1;
        }
        
        if (number < 0.0) {
            number = -number;
        }

        return (int) Math.floor(Math.log10(number));
    }

    private void determineBestUnitForSmallestLeftmostDigitOffset() {        
        UnitType bestUnitForOffset = null;
        int bestDistanceFromDesiredRange = Integer.MAX_VALUE;
        
        for (UnitType unit : availableUnits) {
            double unitValue = Units.convert(baseUnit, unit, baseUnitValue);
            int leftmostDigitOffset = calculateLeftmostDigitOffset(unitValue);
            
            int distanceFromDesiredRange = calculateDistanceFromDesiredRange(leftmostDigitOffset);
            
            if (distanceFromDesiredRange < bestDistanceFromDesiredRange) {
                bestUnitForOffset = unit;
                bestDistanceFromDesiredRange = distanceFromDesiredRange;
            }
        }
        
        if (bestUnitForOffset == null) {
            throw new IllegalStateException("No unit could be found to display value");
        }
        
        displayUnit = bestUnitForOffset;
    }
    
    private void determineBestPredictionUnitForSmallestLeftmostDigitOffset() {        
        UnitType bestUnitForOffset = null;
        int bestDistanceFromDesiredRange = Integer.MAX_VALUE;
        
        for (UnitType unit : availableUnits) {
            double unitValue = Units.convert(baseUnit, unit, baseUnitValue);
            int leftmostDigitOffset = calculateLeftmostDigitOffset(unitValue);
            
            int distanceFromDesiredRange = calculateDistanceFromDesiredRange(leftmostDigitOffset);
            
            if (distanceFromDesiredRange < bestDistanceFromDesiredRange) {
                bestUnitForOffset = unit;
                bestDistanceFromDesiredRange = distanceFromDesiredRange;
            }
        }
        
        if (bestUnitForOffset == null) {
            throw new IllegalStateException("No unit could be found to display value");
        }
        
        predictionDisplayUnit = bestUnitForOffset;
    }
    
    private static int calculateDistanceFromDesiredRange(int leftmostDigitOffset) {
        int distanceFromDesiredRange = 0;

        if (leftmostDigitOffset > MAX_DESIRED_OFFSET) {
            distanceFromDesiredRange = leftmostDigitOffset - MAX_DESIRED_OFFSET;
        } else if (leftmostDigitOffset < MIN_DESIRED_OFFSET) {
            distanceFromDesiredRange = MIN_DESIRED_OFFSET - leftmostDigitOffset;
        }
        
        return distanceFromDesiredRange;
    }
    
}
