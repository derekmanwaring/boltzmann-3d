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
 * Created 9 May 2011
 * @author Derek Manwaring
 */
public class StatSettingsController {

    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final StatSettingsView view;

    /**
     * A new Controller with a reference to the main controller for Boltzmann and
     * this controller's view.
     */
    public StatSettingsController(edu.byu.chem.boltzmann.controller.Controller root,
            StatSettingsView view) {
        rootController = root;
        this.view = view;
    }

    public void hideView() {
        rootController.hideStatSettings();
    }

    public void setForgetMultiplier(double forgetMult) {
        rootController.setForgetMultiplier(forgetMult);
    }
    
    public void setFiniteSysCorrections(boolean finSysC) {
        rootController.setFiniteSysCorrections(finSysC);
    }
    
    public void setPressureAveragingTime(double avgTime) {
        rootController.setPressureAveragingTime(avgTime);
    }
    
    public void setRealGasCorrections(boolean realGasC) {
        rootController.setRealGasCorrections(realGasC);
    }
    
    public void setRDFUpdateMultiplier(double rdfMult) {
        rootController.setRDFUpdateMultiplier(rdfMult);
    }
    
    public void setExhaustiveRDFCalcs(boolean exhaustiveRDF) {
        rootController.setExhaustiveRDFCalcs(exhaustiveRDF);
    }
    
    public StatSettingsInfo getStatSettings(){
        return view.getStatSettings();
    }
}
