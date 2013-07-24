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
package edu.byu.chem.boltzmann.model.statistics.parents;

import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.statistics.InstantaneousSpeed;
import edu.byu.chem.boltzmann.model.statistics.StatisticCalculation;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created Jan 2011
 * @author Derek Manwaring
 */
public abstract class StatisticOld {

    protected Set<ParticleType> types; //Types of particles this keeps track of
    protected Set<Color> colorsRecorded = new HashSet<Color>(); //Colors of particle types recorded

    protected double lastFrameEndTime;

    protected SimulationInfo simulationInfo;
    
    //Calculate predictions with finite system corrections taken into account
    private boolean useFiniteSysCorrections = false;

    public void checkCalculation(StatisticCalculation calculation) {
        if (!getAvailableCalculations().contains(calculation)) {
            throw new RuntimeException("Calculation " + calculation.calculationName +
                    " not available for " + getDisplayName());
        }
    }

    public void checkFrameTimes(double frameStartTime, double lastFrameEndTime) {
        if (frameStartTime != lastFrameEndTime) {
            throw new RuntimeException("Non-continuous frame time: " + frameStartTime + ", last recorded time: " + lastFrameEndTime);
        }
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public void reset(double newStartTime, Physics physics) {
        lastFrameEndTime = newStartTime;
    }

    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        simulationInfo = physics.getSimulationInfo();
        setRecordedTypes(types);

        lastFrameEndTime = physics.getTime();
    }

    public void initiateStatistic(Set<ParticleType> types, SimulationInfo simInfo) {
        simulationInfo = simInfo;
        setRecordedTypes(types);

        lastFrameEndTime = 0.0;
    }

    protected void setRecordedTypes(Set<ParticleType> recordedTypes) {
        this.types = recordedTypes;

        colorsRecorded.clear();
        for (ParticleType type: types) {
            this.colorsRecorded.add(type.defaultColor);
        }
    }

    public abstract void drawPlot(PlotType plotType,
            Graphics graphics,
            StatPanel plotPanel,
            boolean instantaneous);

    public abstract Set<PlotType> getAvailablePlotTypes();

    public void checkPlotType(PlotType type) {
        if (!getAvailablePlotTypes().contains(type)) {
            throw new RuntimeException("Plot " + type +
                    "not available for " + getDisplayName());
        }
    }

    public void setLastFrameEndTime(double lastFrameEndTime) {
        this.lastFrameEndTime = lastFrameEndTime;
    }

    protected boolean shouldRecordStats(Color particleColor) {
        return (colorsRecorded.contains(particleColor));
    }
    
    protected boolean usingFiniteSysCorrections() {
        return useFiniteSysCorrections;
    }
    
    public void setFinitSysCorrections(boolean corrections) {
        useFiniteSysCorrections = corrections;
    }

    public abstract void prepareInstCalculations(FrameInfo frame);

    public abstract Set<StatisticCalculation> getAvailableCalculations();
    public abstract List<StatisticCalculation> getPreferredCalcOrder();
    public abstract int getMinimumCalculations();
    public abstract double getCalculation(StatisticCalculation calculation);
    public abstract double getInstCalculation(StatisticCalculation calculation);

    public abstract String getDisplayName();
    public abstract Unit getUnit(StatisticCalculation calculation);
    
    public abstract void updateByEvent(EventInfo event);
}
