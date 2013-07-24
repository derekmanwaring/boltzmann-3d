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

import java.awt.Dimension;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

/**
 *
 * @author Derek Manwaring
 * 24 Oct 2011
 */
public class SpeedSpinner extends JSpinner {
    
    private static final int DEFAULT_HEIGHT = 28;
    private static final int DEFAULT_WIDTH = 92;    
    
    public static class SpeedSpinnerSetting {
        
        private final Fraction fraction;
        
        private SpeedSpinnerSetting(String fraction) {
            this.fraction = new Fraction(fraction);
        }
        
        @Override
        public String toString() {
            return fraction.toString() + "x";
        }
        
        public double getSpeedMultiplier() {
            return fraction.getValue();
        }
    }
    
    private static final SpeedSpinnerSetting defaultSetting = new SpeedSpinnerSetting("1");
    
    private static final SpeedSpinnerSetting[] speedSettings = {
        new SpeedSpinnerSetting("1/64"),
        new SpeedSpinnerSetting("1/32"),
        new SpeedSpinnerSetting("1/16"),
        new SpeedSpinnerSetting("1/8"),
        new SpeedSpinnerSetting("1/4"),
        new SpeedSpinnerSetting("1/2"),
        defaultSetting,
        new SpeedSpinnerSetting("2"),
        new SpeedSpinnerSetting("4"),
        new SpeedSpinnerSetting("8"),
        new SpeedSpinnerSetting("16"),
        new SpeedSpinnerSetting("32"),
        new SpeedSpinnerSetting("64"),
        new SpeedSpinnerSetting("128"),
        new SpeedSpinnerSetting("256"),
        new SpeedSpinnerSetting("512"),
        new SpeedSpinnerSetting("1024"),
        new SpeedSpinnerSetting("2048"),
        new SpeedSpinnerSetting("100000")
    };
    
    public SpeedSpinner() {        
        setModel(new SpinnerListModel(speedSettings));
        setValue(defaultSetting);
        
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
    
    public double getSpeedMultiplier() {
        return ((SpeedSpinnerSetting) getValue()).getSpeedMultiplier();
    }
    
}
