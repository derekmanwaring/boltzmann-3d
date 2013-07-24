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
package edu.byu.chem.boltzmann.view.piston;

import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.physics.Piston.PistonMode;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;

/**
 * Controls the piston view and relays requests back to the main controller for
 * Boltzmann.
 * @author Derek Manwaring
 */
public class PistonController {

    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final PistonView view;

    public PistonController(edu.byu.chem.boltzmann.controller.Controller rootController, PistonView pistonView) {
        this.view = pistonView;
        this.rootController = rootController;
    }

    public void hidePistonControls() {
        rootController.hidePistonControls();
    }

    public void movePiston(double newPosition, PistonMode moveMode, double moveSpeed) {
        rootController.movePiston(newPosition, moveMode, moveSpeed);
    }

    public  void setPistonMoveEnabled(boolean enabled) {
        view.setPistonMoveEnabled(enabled);
    }

    public void stopPiston() {
        rootController.stopPiston();
    }

    public void setPistonPosInView(double pistonPosition) {
        view.setPistonPosition(pistonPosition);
    }

    public void setSimulationInfo(SimulationInfo simInfo) {
        double pistonLength = 0.0;
        
        switch(simInfo.dimension) {
            case 1:
                pistonLength = simInfo.arenaXSize;
                break;
            case 2:
                pistonLength = simInfo.arenaYSize;
                break;
            case 3:
                pistonLength = simInfo.arenaYSize;
        }
                
        view.setPistonLength(pistonLength);
        view.setPistonPos(0);
        view.setMaxPistonPosition(100.0 - Physics.calculateCompressionLimit(simInfo));
    }
}
