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
package edu.byu.chem.boltzmann.modules.idealgas;

import edu.byu.chem.boltzmann.controller.Controller;
import edu.byu.chem.boltzmann.modules.idealgas.view.IdealGasView;
import edu.byu.chem.boltzmann.view.heatreservoir.HeatResView;
import edu.byu.chem.boltzmann.view.piston.PistonView;
import edu.byu.chem.boltzmann.view.plotsettings.PlotSettingsView;
import edu.byu.chem.boltzmann.view.statisticsettings.StatSettingsView;

/**
 *
 * @author Derek Manwaring
 * 28 Oct 2011
 */
public class IdealGasController extends Controller {

    public IdealGasController(IdealGasView idealGasView) {
        super(idealGasView, 
                idealGasView.getSimSettingsView(), 
                PistonView.NO_VIEW, 
                StatSettingsView.NO_VIEW, 
                HeatResView.NO_VIEW, 
                PlotSettingsView.NO_VIEW);
    }
    
    @Override
    public void showSimSettings() {
    }

    @Override
    public void hideSimSettings() {
    }

    @Override
    public void showStatSettings() {
    }

    @Override
    public void hideStatSettings() {
    }

    @Override
    public void showPistonControls() {
    }

    @Override
    public void hidePistonControls() {
    }

    @Override
    public void showReservoirControls() {
    }

    @Override
    public void hideReservoirControls() {
    }

    @Override
    public void showPlotSettings() {
    }

    @Override
    public void hidePlotSettings() {
    }

    @Override
    protected void finishBoltzmannExit() {
        System.exit(0);
    }
    
}
