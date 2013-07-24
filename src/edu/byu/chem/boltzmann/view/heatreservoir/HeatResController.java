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
package edu.byu.chem.boltzmann.view.heatreservoir;

import edu.byu.chem.boltzmann.utils.data.SimulationInfo;

/**
 * Created 7 Jun 2011
 * @author Derek Manwaring
 */
public class HeatResController {
    
    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final HeatResView view;

    public HeatResController(edu.byu.chem.boltzmann.controller.Controller rootController, HeatResView view) {
        this.view = view;
        this.rootController = rootController;
    }
        
    public void setSimulationInfo(SimulationInfo simInfo) {
        view.setReservoirTemp(simInfo.getHeatReservoirTemperature());
    }
    
    public void setReservoirTemp(double temperature) {
        rootController.setHeatReservoirTemp(temperature);
    }
    
    public void hideReservoirControls() {
        rootController.hideReservoirControls();    
    }
}
