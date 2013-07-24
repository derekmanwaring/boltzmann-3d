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
package edu.byu.chem.boltzmann.fullapplication;

import edu.byu.chem.boltzmann.controller.Controller;
import edu.byu.chem.boltzmann.fullapplication.view.heatreservoir.HeatResWindow;
import edu.byu.chem.boltzmann.fullapplication.view.maingui.MainGuiWindow;
import edu.byu.chem.boltzmann.fullapplication.view.piston.PistonWindow;
import edu.byu.chem.boltzmann.fullapplication.view.plotsettings.PlotSettingsWindow;
import edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.SimSettingsWindow;
import edu.byu.chem.boltzmann.fullapplication.view.statisticsettings.StatSettingsWindow;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import javax.swing.SwingUtilities;

/**
 * Controls Boltzmann's model where its views are in separate windows.
 * @author Derek Manwaring
 */
public class ApplicationController extends Controller {

    private static final int SIM_SETTINGS_X_OFFSET = 125;
    private static final int SIM_SETTINGS_Y_OFFSET = 75;

    private static final int STAT_SETTINGS_X_OFFSET = 135;
    private static final int STAT_SETTINGS_Y_OFFSET = 85;

    private static final int PISTON_CONTROLS_X_OFFSET = 135;
    private static final int PISTON_CONTROLS_Y_OFFSET = 85;

    private static final int RESERVOIR_CONTROLS_X_OFFSET = 145;
    private static final int RESERVOIR_CONTROLS_Y_OFFSET = 105;

    private static final int PLOT_SETTINGS_X_OFFSET = 125;
    private static final int PLOT_SETTINGS_Y_OFFSET = 95;

    private final MainGuiWindow mainWindow;
    private final SimSettingsWindow simSettingsWindow;
    private final PistonWindow pistonWindow;
    private final StatSettingsWindow statSettingsWindow;
    private final HeatResWindow reservoirWindow;
    private final PlotSettingsWindow plotSettingsWindow;

    public ApplicationController(MainGuiWindow mainWindow,
            SimSettingsWindow simSettingsWindow,
            PistonWindow pistonWindow,
            StatSettingsWindow statSettingsWindow,
            HeatResWindow reservoirWindow,
            PlotSettingsWindow plotSettingsWindow) {
        super(mainWindow.getView(),
                simSettingsWindow.getView(),
                pistonWindow.getView(),
                statSettingsWindow.getView(),
                reservoirWindow.getView(),
                plotSettingsWindow.getView());

        this.mainWindow = mainWindow;
        this.simSettingsWindow = simSettingsWindow;
        this.pistonWindow = pistonWindow;
        this.statSettingsWindow = statSettingsWindow;
        this.reservoirWindow = reservoirWindow;
        this.plotSettingsWindow = plotSettingsWindow;

        positionSimSettings();
        positionStatSettings();
        positionPistonControls();
        positionReservoirControls();
        positionPlotSettings();
    }
    
    @Override
    public void setSimulationInfo(final SimulationInfo simInfo) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (simInfo.isPiston()) {
                    showPistonControls();
                } else {
                    hidePistonControls();
                }
                mainWindow.setSimulationInfo(simInfo);
            }
        });
        
        super.setSimulationInfo(simInfo);
    }

    /**
     * Positions the simulation settings window to an appropriate spot based on the
     * current position of the main window.
     */
    private void positionSimSettings() {
        int xPos = mainWindow.getLocation().x + SIM_SETTINGS_X_OFFSET;
        int yPos = mainWindow.getLocation().y + SIM_SETTINGS_Y_OFFSET;
        simSettingsWindow.setLocation(xPos, yPos);
    }

    /**
     * Positions the statistics settings window to an appropriate spot based on the
     * current position of the main window.
     */
    private void positionStatSettings() {
        int xPos = mainWindow.getLocation().x + STAT_SETTINGS_X_OFFSET;
        int yPos = mainWindow.getLocation().y + STAT_SETTINGS_Y_OFFSET;
        statSettingsWindow.setLocation(xPos, yPos);
    }

    /**
     * Positions the piston controls window to an appropriate spot based on the
     * current position of the main window.
     */
    private void positionPistonControls() {
        int xPos = mainWindow.getLocation().x + PISTON_CONTROLS_X_OFFSET;
        int yPos = mainWindow.getLocation().y + PISTON_CONTROLS_Y_OFFSET;
        pistonWindow.setLocation(xPos, yPos);
    }
    
    private void positionReservoirControls() {
        int xPos = mainWindow.getLocation().x + RESERVOIR_CONTROLS_X_OFFSET;
        int yPos = mainWindow.getLocation().y + RESERVOIR_CONTROLS_Y_OFFSET;
        reservoirWindow.setLocation(xPos, yPos);
    }
    
    private void positionPlotSettings() {
        int xPos = mainWindow.getLocation().x + PLOT_SETTINGS_X_OFFSET;
        int yPos = mainWindow.getLocation().y + PLOT_SETTINGS_Y_OFFSET;
        plotSettingsWindow.setLocation(xPos, yPos);
    }

    @Override
    public void showSimSettings() {
        simSettingsWindow.setVisible(true);
    }

    @Override
    public void hideSimSettings() {
        simSettingsWindow.setVisible(false);
    }

    @Override
    public void showStatSettings() {       
        statSettingsWindow.setVisible(true);
    }

    @Override
    public void hideStatSettings() {
        statSettingsWindow.setVisible(false);
    }

    @Override
    public void showPistonControls() {
        pistonWindow.setVisible(true);
    }

    @Override
    public void hidePistonControls() {
        pistonWindow.setVisible(false);
    }

    @Override
    public void showReservoirControls() {
        reservoirWindow.setVisible(true);
    }

    @Override
    public void hideReservoirControls() {        
        reservoirWindow.setVisible(false);
    }

    @Override
    public void showPlotSettings() {
        plotSettingsWindow.setVisible(true);
    }

    @Override
    public void hidePlotSettings() {
        plotSettingsWindow.setVisible(false);
    }

    @Override
    protected void finishBoltzmannExit() {     
        System.exit(0);
    }

}
