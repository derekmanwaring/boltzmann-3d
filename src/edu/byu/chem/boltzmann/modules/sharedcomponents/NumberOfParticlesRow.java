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

import edu.byu.chem.boltzmann.view.maingui.components.textfields.IntegerTextField;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;

/**
 *
 * @author Derek Manwaring
 * 15 Nov 2011
 */
public class NumberOfParticlesRow extends SingleTextFieldSettingRow {
     
    private static final String NUM_PARTICLES_LABEL = "Number of particles";
        
    private IntegerTextField numParticles;
    
    public NumberOfParticlesRow(int widthForAlignedSection,
            int initialNumParticles,
            int minNumParticles,
            int maxNumParticles, 
            int textFieldColumns,
            NewNumberListener numParticlesChangedListener) {
        super(widthForAlignedSection);
        
        addLabel(NUM_PARTICLES_LABEL);
    
        numParticles = new IntegerTextField(initialNumParticles, 
                minNumParticles, 
                maxNumParticles,
                textFieldColumns);
        
        numParticles.addListener(numParticlesChangedListener);
        
        addSettingsField(numParticles);        
    }
    
    public int getNumParticles() {
        return numParticles.getNumericValue();
    }
    
    public void setNumParticles(int newNumParticles) {
        numParticles.setNumericValue(newNumParticles);
    }
    
}
