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

import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.statistics.StatUtils;
import edu.byu.chem.boltzmann.model.statistics.StatisticCalculation;
import edu.byu.chem.boltzmann.model.statistics.plots.History;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Statistics one central calculation they display. Usually this is an average.
 * This class provides a history plot whose values are this average over time.
 * @author Derek Manwaring
 */
public abstract class SingleAverageStatisticOld extends StatisticOld {

    private double prediction;

    private History cumulativeHistory = new History();
    private History instHistory = new History();

    private static final Set<StatisticCalculation> AVAILABLE_CALCULATIONS =
            new HashSet<StatisticCalculation>(Arrays.asList(
            new StatisticCalculation[] { StatUtils.AVERAGE, StatUtils.PREDICTION }));

    private static final Set<PlotType> AVAILABLE_PLOTS = EnumSet.of(PlotType.HISTORY);

    private static final List<StatisticCalculation> PREFERRED_ORDER =
        Arrays.asList(
        new StatisticCalculation[] { StatUtils.AVERAGE, StatUtils.PREDICTION });

    @Override
    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        super.initiateStatistic(types, physics);

        prediction = calculatePrediction();

        scaleHistories();
    }
    
    protected void scaleHistories() {
//        cumulativeHistory.setScale(getCenterPlotValue(), getScalePlotValue());
//        instHistory.setScale(getCenterPlotValue(), getScalePlotValue());
    }

    @Override
    public Set<StatisticCalculation> getAvailableCalculations() {
        return AVAILABLE_CALCULATIONS;
    }

    @Override
    public void reset(double newStartTime, Physics physics) {
        super.reset(newStartTime, physics);
        prediction = calculatePrediction();
        resetHistories();
    }

    private void resetHistories() {
        cumulativeHistory.reset();
        instHistory.reset();
    }

    @Override
    public double getInstCalculation(StatisticCalculation calculation) {
        checkCalculation(calculation);

        double calcValue = 0.0;
        if (calculation == StatUtils.AVERAGE) {
            calcValue = getInstAverage();
        } else if (calculation == StatUtils.PREDICTION) {
            calcValue = prediction;
        }

        return calcValue;
    }

    @Override
    public double getCalculation(StatisticCalculation calculation) {
        checkCalculation(calculation);

        double calcValue = 0.0;
        if (calculation == StatUtils.AVERAGE) {
            calcValue = getAverage();
        } else if (calculation == StatUtils.PREDICTION) {
            calcValue = prediction;
        }

        return calcValue;
    }

    @Override
    public List<StatisticCalculation> getPreferredCalcOrder() {
        return PREFERRED_ORDER;
    }

    @Override
    public void drawPlot(PlotType plotType,
            Graphics graphics,
            StatPanel plotPanel,
            boolean instantaneous) {
        
        switch (plotType) {
            case HISTORY:
                drawHistory(graphics, plotPanel, instantaneous);
                break;
        }
    }

    private void drawHistory(Graphics graphics, StatPanel plotPanel, boolean instantaneous) {
        if (instantaneous) {
            instHistory.drawHistory(graphics, plotPanel);
        } else {
            cumulativeHistory.drawHistory(graphics, plotPanel);
        }
    }

    public void updateHistory() {
        cumulativeHistory.addPoint(getCalculation(StatUtils.AVERAGE));
        instHistory.addPoint(getInstCalculation(StatUtils.AVERAGE));
    }

    @Override
    public Set<PlotType> getAvailablePlotTypes() {
        return AVAILABLE_PLOTS;
    }

    @Override
    public int getMinimumCalculations() {
        return 1;
    }
    
    @Override
    public void setFinitSysCorrections(boolean corrections) {
        super.setFinitSysCorrections(corrections);
        prediction = calculatePrediction();
    }

    protected abstract void updateStatisticAvgs(FrameInfo frame);
    
    public abstract void resetInstData();

    public abstract double getAverage();
    public abstract double getInstAverage();

    protected abstract double calculatePrediction();
    
    protected abstract double getCenterPlotValue();
    protected abstract double getScalePlotValue();
}
