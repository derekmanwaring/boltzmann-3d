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
import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.fullapplication.view.heatreservoir.HeatResWindow;
import edu.byu.chem.boltzmann.fullapplication.view.maingui.MainGuiWindow;
import edu.byu.chem.boltzmann.fullapplication.view.piston.PistonWindow;
import edu.byu.chem.boltzmann.fullapplication.view.plotsettings.PlotSettingsWindow;
import edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.SimSettingsWindow;
import edu.byu.chem.boltzmann.fullapplication.view.statisticsettings.StatSettingsWindow;
import edu.byu.chem.boltzmann.model.io.Load;
import edu.byu.chem.boltzmann.resources.ResourceLoader;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * When run as an application, Boltzmann starts in the main() method.
 * @author Derek Manwaring
 */
public class Main {
    
    private static SimulationInfo defaultSim = null;
    
    /**
     * Make windows for the different views. Create the central controller for
     * Boltzmann and pass it references to the windows.
     */
    public static void main(String args[]) {
        ErrorHandler.attachDefaultExceptionHandlers();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {        
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    final MainGuiWindow mainWindow = new MainGuiWindow();
                    final SimSettingsWindow simSettingsWindow =
                            new SimSettingsWindow();
                    final PistonWindow pistonWindow = new PistonWindow();
                    final StatSettingsWindow statSettingsWindow =
                            new StatSettingsWindow();
                    final HeatResWindow reservoirWindow = 
                            new HeatResWindow();
                    final PlotSettingsWindow plotSettingsWindow = 
                            new PlotSettingsWindow(); 

                    final Controller rootController = new ApplicationController(
                            mainWindow, 
                            simSettingsWindow, 
                            pistonWindow, 
                            statSettingsWindow,
                            reservoirWindow,
                            plotSettingsWindow);

                    Load loader = new Load(null);
                    defaultSim = loader.loadFile(ResourceLoader.getDefaultSetFileURL());
                    rootController.setSimulationInfo(defaultSim);

                    mainWindow.setVisible(true);
                } catch (Exception e) {
                    throw new RuntimeException("Simulation could not be started", e);
                }
            }
        });        
    }
    
    public static SimulationInfo getDefaultSimulationInfo() {
        if (defaultSim == null) {
            throw new IllegalStateException("Default simulation settings have not been set");
        }
        
        return defaultSim;
    }
}
