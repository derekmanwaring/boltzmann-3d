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
package edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 *
 * @author Derek Manwaring
 * 16 Nov 2011
 */
public class ButtonGroupWithValues<SelectionObj> extends ButtonGroup {
 
    private Map<AbstractButton, SelectionObj> valuesByButton = new HashMap<AbstractButton, SelectionObj>(5);
    private Map<SelectionObj, AbstractButton> buttonsByValue = new HashMap<SelectionObj, AbstractButton>(5);
    private SelectionObj selectedValue = null;
    
    private SelectionChangedListener<SelectionObj> listener = null;
    
    public interface SelectionChangedListener<SelectionObj> {
        public void selectionChanged(SelectionObj newSelectedValue);
    }
    
    private final ActionListener selectionChangedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            AbstractButton sourceButton = (AbstractButton) ae.getSource();
            SelectionObj newSelectedValue = valuesByButton.get(sourceButton);
            changeSelectedValue(newSelectedValue);
        }        
    };
    
    public static class NoValueSelectedException extends RuntimeException {        
    }
    
    public static class BadValueException extends RuntimeException {   
        public BadValueException(String message) {
            super(message);
        }     
    }
    
    public static class BadButtonToValueMappingException extends RuntimeException {
        public BadButtonToValueMappingException(String message) {
            super(message);
        }
    }

    public static class BadListenerException extends RuntimeException {
        public BadListenerException(String message) {
            super(message);
        }
    }
    
    public ButtonGroupWithValues(SelectionChangedListener<SelectionObj> listener) {
        if (listener == null) {
            throw new BadListenerException("Listener was null");
        }
        
        this.listener = listener;
    }
    
    public void addButtonWithValue(AbstractButton btn, SelectionObj value) {
        if (valuesByButton.containsKey(btn)) {
            throw new BadButtonToValueMappingException(
                    "Button already added with value " + valuesByButton.get(btn));
        }
        if (buttonsByValue.containsKey(value)) {
            throw new BadButtonToValueMappingException(
                    "Mapping for value " + value + " already exists");
        }
        
        super.add(btn);        
        valuesByButton.put(btn, value);
        buttonsByValue.put(value, btn);
        btn.addActionListener(selectionChangedListener);
    }
    
    public SelectionObj getSelectedValue() {
        if (selectedValue == null) {
            throw new NoValueSelectedException();
        }
        return selectedValue;
    }
    
    public void setSelectedValue(SelectionObj value) {
        if (!buttonsByValue.containsKey(value)) {
            throw new BadValueException("No button added for value " + value);
        }
        buttonsByValue.get(value).setSelected(true);
        changeSelectedValue(value);
    }
    
    private void changeSelectedValue(SelectionObj newValue) {
        if (newValue != selectedValue) {
            selectedValue = newValue;
            listener.selectionChanged(newValue);
        }
    }
}
