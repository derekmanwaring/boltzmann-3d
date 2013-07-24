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
package edu.byu.chem.boltzmann.view.statisticsettings;

import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;

/**
 *
 * @author Derek Manwaring
 * 20 Oct 2011
 */
public interface StatSettingsView {
    
    public static final StatSettingsView NO_VIEW = new StatSettingsView() {
        public void attachController(StatSettingsController controller) {
        }
        public StatSettingsInfo getStatSettings(){
            return new StatSettingsInfo();
        }
    };
    
    public void attachController(StatSettingsController controller);
    
    public StatSettingsInfo getStatSettings();
}
