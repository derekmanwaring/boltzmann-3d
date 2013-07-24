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

import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEvent;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEventListener;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannListenerHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/**
 *
 * @author Derek Manwaring
 * 26 Oct 2011
 */
public abstract class NumericTextField<NumType extends Number & Comparable<NumType>> extends JTextField {
    
    public static class NewNumberCommitted extends BoltzmannEvent {
    }
    
    public static interface NewNumberListener extends BoltzmannEventListener<NewNumberCommitted> {    
    }
    
    private BoltzmannListenerHandler<NewNumberListener, NewNumberCommitted> listeners =
            new BoltzmannListenerHandler<NewNumberListener, NewNumberCommitted>();
    
    NumType lastValue = null;
    NumType minValue = null;
    NumType maxValue = null;
    
    public NumericTextField(NumType initialValue, NumType minValue, NumType maxValue, int numColumns) {    
        super(numColumns);
        
        if (minValue.compareTo(maxValue) > 0) {
            throw new RuntimeException("Invalid min/max bounds " + minValue + ", " + maxValue);
        }
        
        if ((minValue.compareTo(initialValue) > 0) || (maxValue.compareTo(initialValue) < 0)) {
            throw new RuntimeException("Initial value " + 
                    initialValue +
                    " not within min/max bounds " +
                    minValue +
                    ", " +
                    maxValue);
        }
        
        setText(initialValue.toString());
        lastValue = initialValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent fe) {
                selectAll();
            }

            public void focusLost(FocusEvent fe) {
                validateInput();
            }
        });
        
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                validateInput();  
                listeners.notifyListeners(new NewNumberCommitted());
            }
        });
    }
    
    private void validateInput() {        
        if (isInputStringValid()) {
            NumType input = getNumericValue();
            if (minValue.compareTo(input) > 0) { // coerce above minimum
                input = minValue;
                setText(input.toString());
            } else if (maxValue.compareTo(input) < 0) { // coerce below maximum
                input = maxValue;
                setText(input.toString());
            }
            lastValue = input;
        } else {
            setText(lastValue.toString());
        }        
    }
    
    public void setNumericValue(NumType value) {
        this.setText(value.toString());
    }
    
    public void addListener(NewNumberListener listener) {
        listeners.addListener(listener);
    }
    
    public void setMinimum(NumType newMinimum) {
        minValue = newMinimum;
        validateInput();
    }
    
    public void setMaximum(NumType newMaximum) {
        maxValue = newMaximum;
        validateInput();
    }
    
    public abstract NumType getNumericValue();
    
    public abstract boolean isInputStringValid();
    
}
