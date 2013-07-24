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
package edu.byu.chem.boltzmann.view.maingui;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import edu.byu.chem.boltzmann.view.maingui.components.PlayPauseButton;
import edu.byu.chem.boltzmann.view.maingui.components.speedspinner.SpeedSpinner;
import java.awt.Desktop;
import java.io.IOException;
import java.util.Set;
import javax.swing.event.ChangeEvent;

/**
 * Controls the maingui view and relays requests back to the main controller for
 * Boltzmann.
 * @author Derek Manwaring
 */
public class MainGuiController {

    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final MainGuiView view;

    /**
     * A new Controller with a reference to the main controller for Boltzmann and
     * this controller's view.
     */
    public MainGuiController(edu.byu.chem.boltzmann.controller.Controller root,
            MainGuiView view) {
        rootController = root;
        this.view = view;
    }
    
    public void attachSpeedSpinner(final SpeedSpinner speedSpinner) {
        speedSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                MainGuiController.this.setPhysicsFrameSpeed(speedSpinner.getSpeedMultiplier());
            }
        });
    }
    
    public void attachPlayPauseButton(final PlayPauseButton playPauseButton) {
        playPauseButton.setListener(new PlayPauseButton.PlayButtonListener() {
            public void setPaused(boolean paused) {
                view.setPaused(paused);
                rootController.setPaused(paused);
            }
        });
    }
    
    public void newSimulation() {
        rootController.showSimSettings();
    }

    public void applySimulationInfo(SimulationInfo simInfo) {
        rootController.setSimulationInfo(simInfo);
    }

    public void showStatisticSettings() {
        rootController.showStatSettings();
    }

    public void showPistonControls() {
        rootController.showPistonControls();
    }
    
    public void showReservoirControls() {
        rootController.showReservoirControls();
    }

    public void recordSimulation() {
        new UnsupportedOperationException("Not yet implemented").printStackTrace(System.err);    
    }

    public void setSimulationInfo(SimulationInfo simInfo) {
        view.setSimulationInfo(simInfo);
        rootController.setPhysicsFrameSpeedMultiplier(frameSpeedMultiplier);
    }

    public void restartSimulation() {
        new Thread("Restart Simulation") {
            @Override
            public void run() {
                rootController.restartSimulation();
            }
        }.start();
    }

    private double frameSpeedMultiplier = 1.0;
    
    public void setPhysicsFrameSpeed(double multiplier) {
        frameSpeedMultiplier = multiplier;
        rootController.setPhysicsFrameSpeedMultiplier(multiplier);
    }

    public Statistic getStatistic(Set<ParticleType> types, StatisticID statisticID) {
        return rootController.getStatistic(types, statisticID);
    }

    public void showPlotSettings() {
        rootController.showPlotSettings();
    }

    public void setCurrentPlot(StatisticWithDistribution selectedPlot) {
        rootController.setCurrentPlot(selectedPlot);
    }

    public void exitBoltzmann() {
        new Thread("Exit Boltzmann") {
            @Override
            public void run() {
                rootController.exitBoltzmann();
            }
        }.start();
    }

    public void setNextFrameToDisplay(FrameInfo currentFrame) {
        view.setNextFrameToDisplay(currentFrame);
    }

    public void displayFrame() {
        view.displayFrame();
    }

    public void advanceOneFrame() {
        rootController.advanceOneFrame();
    }

    public void displayStatistics() {        
        view.displayStatistics();
    }

    public void saveSimulationInfo() {
        ErrorHandler.infoMessage("Saving is not supported yet.");
    }

    private static final String BOLTZMANN_WEBPAGE_URL = "http://www.chem.byu.edu/boltzmann3d";
    private static final String LWJGL_WEBSITE_URL = "http://lwjgl.org/";
    
    public void boltzmannWebpageClicked() {
        new Thread("Show Boltzmann Webpage") {
            @Override
            public void run() {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create(BOLTZMANN_WEBPAGE_URL));
                } catch (IOException e) {
                    throw new RuntimeException("Could not open Boltzmann webpage", e);
                }
            }
        }.start();
    }

    public void lwjglSiteClicked() {
        new Thread("Show LWJGL Website") {
            @Override
            public void run() {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create(LWJGL_WEBSITE_URL));
                } catch (IOException e) {
                    throw new RuntimeException("Could not open LWJGL website", e);
                }
            }
        }.start();
    }

    public void licensesClicked() {
        view.showLicenses();
    }

    public void setArenaHoleOpen(boolean arenaHoleOpen) {
        rootController.setArenaHoleOpen(arenaHoleOpen);
    }
}
