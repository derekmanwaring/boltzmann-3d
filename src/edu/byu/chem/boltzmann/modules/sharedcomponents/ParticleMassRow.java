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

import edu.byu.chem.boltzmann.utils.Units.Mass;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.IntegerUnitTextField;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;
/**
 *
 * @author Derek Manwaring
 * 15 Nov 2011
 */
public class ParticleMassRow extends SingleTextFieldSettingRow {
    
    private static final Mass MASS_UNITS = Mass.ATOMIC_MASS_UNIT;    
    private static final String MASS_LABEL = "Particle mass (" + MASS_UNITS.getSymbol() + ")";
        
    private IntegerUnitTextField<Mass> mass;
    
    public ParticleMassRow(int widthForAlignedSection,
            double initialMass,
            double minMass,
            double maxMass, 
            int textFieldColumns,
            NewNumberListener massChangedListener) {
        super(widthForAlignedSection);
        
        addLabel(MASS_LABEL);
    
        mass = new IntegerUnitTextField<Mass>(MASS_UNITS,
                initialMass, 
                minMass, 
                maxMass,
                textFieldColumns);
        
        mass.addListener(massChangedListener);
        
        addSettingsField(mass);        
    }
    
    public double getMass(Mass massUnits) {
        return mass.getValueInUnits(massUnits);
    }
    
    public void setMass(double newMass, Mass newMassUnits) {
        mass.setValueInUnits(newMass, newMassUnits);
    }
}
