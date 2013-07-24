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
package edu.byu.chem.boltzmann.controller;

import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.physics.Piston.PistonMode;
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.Units.Time;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import edu.byu.chem.boltzmann.view.heatreservoir.HeatResController;
import edu.byu.chem.boltzmann.view.heatreservoir.HeatResView;
import edu.byu.chem.boltzmann.view.maingui.MainGuiController;
import edu.byu.chem.boltzmann.view.maingui.MainGuiView;
import edu.byu.chem.boltzmann.view.piston.PistonController;
import edu.byu.chem.boltzmann.view.piston.PistonView;
import edu.byu.chem.boltzmann.view.plotsettings.PlotSettingsController;
import edu.byu.chem.boltzmann.view.plotsettings.PlotSettingsView;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsController;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsView;
import edu.byu.chem.boltzmann.view.statisticsettings.StatSettingsController;
import edu.byu.chem.boltzmann.view.statisticsettings.StatSettingsView;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * Controls Boltzmann's model from its various views.
 * @author Derek Manwaring
 */
public abstract class Controller {

    private final MainGuiController mainGUI;
    private final SimSettingsController simSettings;
    private final PistonController pistonControls;
    private final StatSettingsController statSettings;
    private final HeatResController reservoirControls;
    private final PlotSettingsController plotSettings;

    private ThreadController threadController;
    
    private Physics physics;
    private boolean simulationThreadsRunning = false;
    private final BinarySemaphore setSimulationInfoLock = new BinarySemaphore(true);

    /**
     * A new controller for the current advanceToNextFrame of Boltzmann with references to views
     * created for this advanceToNextFrame.The constructor creates controllers for all Boltzmann 
     * views and gives them the references to their views. It also gives each view
     * a reference to its controller.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    protected Controller(MainGuiView mainView,
            SimSettingsView simSettingsView,
            PistonView pistonView,
            StatSettingsView statSettingsView,
            HeatResView reservoirView,
            PlotSettingsView plotSettingsView) {
        
        mainGUI = new MainGuiController(this, mainView);
        simSettings = new SimSettingsController(this, simSettingsView);
        pistonControls = new PistonController(this, pistonView);
        statSettings = new StatSettingsController(this, statSettingsView);
        reservoirControls = new HeatResController(this, reservoirView);
        plotSettings = new PlotSettingsController(this, plotSettingsView);

        mainView.attachController(mainGUI);
        simSettingsView.attachController(simSettings);
        pistonView.attachController(pistonControls);
        statSettingsView.attachController(statSettings);
        reservoirView.attachController(reservoirControls);
        plotSettingsView.attachController(plotSettings);
        
        ThreadController.setRootController(this);
        threadController = ThreadController.getInstance();
    }

    /**
     * Allow the user to edit the simulation settings.
     */
    public abstract void showSimSettings();

    /**
     * Hide the simulation settings view.
     */
    public abstract void hideSimSettings();

    /**
     * Allow the user to edit the statistics settings.
     */
    public abstract void showStatSettings();

    /**
     * Hide the statistics settings view.
     */
    public abstract void hideStatSettings();

    /**
     * Allow the user to see the piston controls.
     */
    public abstract void showPistonControls();

    /**
     * Hide the piston controls.
     */
    public abstract void hidePistonControls();
    
    public abstract void showReservoirControls();
    public abstract void hideReservoirControls();    

    public abstract void showPlotSettings();
    public abstract void hidePlotSettings();

    /**
     * Restarts the model with the starting conditions and environment described by
     * simInfo.
     */
    public void setSimulationInfo(final SimulationInfo simInfo) {
        // this lock prevents many threads from being started up when the user
        // trys to rapidly set the simulation info several times in a row
        if (setSimulationInfoLock.tryAcquire()) {
            new Thread("Set Simulation Info") {
                @Override
                public void run() {
                    setSimulationInfoWithLock(simInfo);
                    setSimulationInfoLock.release();
                }
            }.start();    
        } else {
            ErrorHandler.infoMessage("The last simulation settings are still being applied.\n"
                    + "Please wait before applying new settings");
        }
    }
    
    private synchronized void setSimulationInfoWithLock(SimulationInfo simInfo) {    
        if (simulationThreadsRunning) {
            stopSimulationThreads();
        }

        setSimulationInfoWithThreadsStopped(simInfo);

        startSimulationSpecificThreads();

        simulationThreadsRunning = true;
    }
    
    private synchronized void setSimulationInfoWithThreadsStopped(final SimulationInfo simInfo) {   
        this.simulationInfo = simInfo;

        simTime = 0;

        physics = new Physics(simulationInfo, statSettings.getStatSettings());
                
        if (SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Cannot set simulation info on event dispatcher thread");
        }
        
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    mainGUI.setSimulationInfo(simInfo);
                    pistonControls.setSimulationInfo(simInfo);
                    simSettings.setViewInfo(simInfo);
                    reservoirControls.setSimulationInfo(simInfo);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Thread interrupted while setting simulation info for views", e);
        }
    }

    public void setPaused(boolean paused) {
        if (paused) {
            physics.stopProcessingCurrentFrame();
        }
        threadController.setPaused(paused);
    }

    public void setPhysicsFrameSpeedMultiplier(double multiplier) {
        if (physics != null) {
            physics.stopProcessingCurrentFrame();
            physics.setFrameDuration(multiplier * Physics.DEFAULT_FRAME_DURATION, Time.SECOND);
        }
    }

    public Statistic getStatistic(
            Set<ParticleType> types, StatisticID statisticID) {
        return physics.getStatistic(types, statisticID);
    }
    
    public void setForgetMultiplier(double mult) {
        physics.setForgetMultiplier(mult);
    }  
    
    public void setFiniteSysCorrections(boolean corrections) {
        physics.setFiniteSysCorrections(corrections);
    }
    
    public void setPressureAveragingTime(double avgTime){
        physics.setPressureAveragingTime(avgTime);
    }
    
    public void setRealGasCorrections(boolean corrections) {
        physics.setRealGasCorrections(corrections);
    }
    
    public void setRDFUpdateMultiplier(double rdfMult) {
        physics.setRDFUpdateMultiplier(rdfMult);
    }
    
    public void setExhaustiveRDFCalcs(boolean exhaustiveRDF) {
        physics.setExhaustiveRDFCalcs(exhaustiveRDF);
    }

    public void movePiston(double newPosition, PistonMode moveMode, double moveSpeed) {
        pistonMoving = true;
        getPhysics().AdjustPistonLevel(newPosition, moveMode, moveSpeed); 
    }

    public void stopPiston() {
        double pistonLevel = getPhysics().getPiston().getCurLevel();
        getPhysics().AdjustPistonLevel(pistonLevel); 
    }

    protected void enablePistonMovement() {
        pistonControls.setPistonMoveEnabled(true);
    }

    protected void disablePistonMovement() {
        pistonControls.setPistonMoveEnabled(false);
    }

    protected void setPistonPosInView(double pistonPosition) {
        pistonControls.setPistonPosInView(pistonPosition);
    }
    
    public void setHeatReservoirTemp(double temperature) {
        simulationInfo.setHeatReservoirTemperature(temperature);
    }

    public void setCurrentPlot(StatisticWithDistribution selectedPlot) {
        plotSettings.setCurrentStatistic(selectedPlot);
    }

    public void exitBoltzmann() {
        try {            
            stopSimulationThreads();
        } catch (ThreadController.CouldNotStopThreadsException e) {
            throw new RuntimeException("Boltzmann's threads were not all stopped on exit", e);
        }
        finishBoltzmannExit();
    }
    
    /**
     * Called by exitBoltzmann() after threads have all died
     */
    protected abstract void finishBoltzmannExit();

    protected void advancePhysicsToNextFrame() {
        physics.advanceToNextFrame();
    }

    protected void displayFrame() {
        mainGUI.displayFrame();
    }
    
    public Physics getPhysics() {
        return physics;
    }

    public synchronized void restartSimulation() {   
        stopSimulationThreads();
        
        physics.continueRunning();
        simTime = 0.0;        
        physics.reset();    
        pistonControls.setPistonPosInView(0.0);
        
        startSimulationSpecificThreads();
    }
    public SimulationInfo simulationInfo;
    private boolean pistonMoving = false;

    public double simTime = 0.0;
    
    private void syncWithPistonControls(double pistonPosition) {        
        pistonMoving = false;
        
        double pistonLength = simulationInfo.arenaYSize;
        if (simulationInfo.dimension == 1) {
            pistonLength = simulationInfo.arenaXSize;
        }
        
        double percentOfLength = (pistonLength - pistonPosition) / pistonLength;
        
        //TODO: Move this code - Derek Manwaring 17 April 2012
        setPistonPosInView(percentOfLength);
        enablePistonMovement();        
    }

    protected void giveFrameToViewForDisplaying() {
        mainGUI.setNextFrameToDisplay(physics.getCurrentFrame());  
    }

    public void advanceOneFrame() {
        threadController.advanceOneFrame();
    }

    protected void displayStatistics() {
        physics.notifyStatisticsOfCurrentFrame();
        mainGUI.displayStatistics();
    }
    
    /**
     * @param waitTime in milliseconds for threads to stopProcessingCurrentFrame
     */
    private void stopSimulationThreads() {
        physics.stop();
        threadController.stopSimulationThreads(2000);
    }

    private void startSimulationSpecificThreads() {
        threadController.startSimulationThreads();
    }

    protected void createFirstFrame() {
        physics.createFirstFrame();
    }

    public void setArenaHoleOpen(boolean arenaHoleOpen) {
        physics.setArenaHoleOpen(arenaHoleOpen);
    }
}
