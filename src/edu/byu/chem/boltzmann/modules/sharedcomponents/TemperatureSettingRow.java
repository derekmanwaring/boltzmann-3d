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
package edu.byu.chem.boltzmann.modules.sharedcomponents;

import edu.byu.chem.boltzmann.utils.Units.Temperature;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;

/**
 *
 * @author Derek Manwaring
 * 11 Nov 2011
 */
public class TemperatureSettingRow extends SingleTextFieldSettingRow {
    
    private static final Temperature TEMPERATURE_UNITS = Temperature.KELVIN;    
    private static final String TEMPERATURE_LABEL = "Temperature (" + TEMPERATURE_UNITS.getSymbol() + ")";
        
    private UnitTextField<Temperature> temperature;
    
    public TemperatureSettingRow(int widthForAlignedSection,
            double initialTemperature,
            double minTemperature,
            double maxTemperature, 
            int textFieldColumns,
            NewNumberListener temperatureChangedListener) {
        super(widthForAlignedSection);
        
        addLabel(TEMPERATURE_LABEL);
    
        temperature = new UnitTextField<Temperature>(TEMPERATURE_UNITS,
                initialTemperature, 
                minTemperature, 
                maxTemperature,
                textFieldColumns);
        
        temperature.addListener(temperatureChangedListener);
        
        addSettingsField(temperature);        
    }
    
    public double getTemperature(Temperature temperatureUnits) {
        return temperature.getValueInUnits(temperatureUnits);
    }
    
    public void setTemperature(double newTemperature, Temperature newTempUnits) {
        temperature.setValueInUnits(newTemperature, newTempUnits);
    }
}
