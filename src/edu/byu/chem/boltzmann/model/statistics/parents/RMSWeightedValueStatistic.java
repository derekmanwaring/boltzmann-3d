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

import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.statistics.StatUtils;
import edu.byu.chem.boltzmann.model.statistics.StatisticCalculation;
import edu.byu.chem.boltzmann.model.statistics.plots.Histogram;
import edu.byu.chem.boltzmann.model.statistics.plots.Histogram.DistributionFunction;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created Apr 2011
 * Statistics that calculate averages based on collected values with their weights.
 * Statistics that use this system can generate histogram plots.
 * @author Derek Manwaring
 */
public abstract class RMSWeightedValueStatistic extends SingleAverageStatisticOld {

    private double totalWeightedValues = 0.0;
    private double totalWeight = 0.0;

    private double instTotalWeightedValues = 0.0;
    private double instTotalWeight = 0.0;
    
    protected double distribWidth;
    
    private double plotMinimum = 0.0;
    private double plotMaximum = 0.0;

    private Histogram cumulativeHistogram;
    private Histogram instHistogram;

    private static final Set<StatisticCalculation> AVAILABLE_CALCULATIONS = new HashSet<StatisticCalculation>(Arrays.asList(
            new StatisticCalculation[] {
        StatUtils.AVERAGE,
        StatUtils.PREDICTION,
        StatUtils.WIDTH,
        StatUtils.WIDTH_PREDICTION
    }));

    private static final Set<PlotType> AVAILABLE_PLOTS = EnumSet.of(
            PlotType.HISTOGRAM,
            PlotType.HISTORY);

    @Override
    public Set<StatisticCalculation> getAvailableCalculations() {
        return AVAILABLE_CALCULATIONS;
    }

    @Override
    public Set<PlotType> getAvailablePlotTypes() {
        return AVAILABLE_PLOTS;
    }

    @Override
    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        super.initiateStatistic(types, physics);

        distribWidth = calculateDistribWidth();

        plotMinimum = getDefaultPlotMin();
        plotMaximum = getDefaultPlotMax();
        
        setupHistogram();
        scaleHistories();
    }

    public void addWeightedValue(double value, double weight, Color color) {
        totalWeightedValues += value * value * weight;
        totalWeight += weight;

        cumulativeHistogram.addWeightedValue(value, weight, color);
    }

    public void addInstWeightedValue(double value, double weight, Color color) {
        instTotalWeightedValues += value * value * weight;
        instTotalWeight += weight;

        instHistogram.addWeightedValue(value, weight, color);
    }

    public double getAverage() {
        return Math.sqrt(totalWeightedValues / totalWeight);
    }

    public double getInstAverage() {
        return Math.sqrt(instTotalWeightedValues / instTotalWeight);
    }

    protected void setupHistogram() {
        DistributionFunction distributionFunction = new DistributionFunction() {
            public double probability(double value, ParticleType type) {
                return distributionFunction(value, type);
            }
        };

        cumulativeHistogram = new Histogram(plotMinimum, plotMaximum,
                HistogramBins.DEFAULT_NUM_OF_BINS,
                types,
                distributionFunction, 
                simulationInfo);

        instHistogram = new Histogram(plotMinimum, plotMaximum,
                HistogramBins.DEFAULT_INST_BINS,
                types,
                distributionFunction,
                simulationInfo);
    }

    @Override
    public void drawPlot(PlotType plotType, Graphics graphics, StatPanel plotPanel, boolean instantaneous) {
        switch (plotType) {
            case HISTOGRAM:
                drawHistogram(graphics, plotPanel, instantaneous);
                break;
            default:
                super.drawPlot(plotType, graphics, plotPanel, instantaneous);
        }
    }

    private void drawHistogram(Graphics graphics, StatPanel plotPanel, boolean instantaneous) {
        if (instantaneous) {
            instHistogram.drawHistogram(graphics, plotPanel);
        } else {
            cumulativeHistogram.drawHistogram(graphics, plotPanel);
        }
    }

    @Override
    public void reset(double newStartTime, Physics physics) {
        super.reset(newStartTime, physics);

        totalWeightedValues = 0.0;
        totalWeight = 0.0;

        cumulativeHistogram.resetBins();
    }

    public void resetInstData() {
        instTotalWeightedValues = 0.0;
        instTotalWeight = 0.0;

        instHistogram.resetBins();
    }

    @Override
    public double getCalculation(StatisticCalculation calculation) {
        if (calculation == StatUtils.WIDTH_PREDICTION) {
            return distribWidth;
        } else {
            return super.getCalculation(calculation);
        }
    }

    @Override
    protected double getCenterPlotValue() {
        return getCalculation(StatUtils.PREDICTION);
    }

    @Override
    protected double getScalePlotValue() {
        return getCalculation(StatUtils.WIDTH_PREDICTION);
    }
    
    @Override
    public void setFinitSysCorrections(boolean corrections) {
        super.setFinitSysCorrections(corrections);
        requestNewPredictionCurve();
    }
    
    protected double getDefaultPlotMin() {
        return 0.0;
    }
    
    protected double getDefaultPlotMax() {
        return getCalculation(StatUtils.PREDICTION) * 2.5;        
    }

    public double getPlotMin() {
        return plotMinimum;
    }

    public double getPlotMax() {
        return plotMaximum;
    }
    
    public void setPlotLimits(double min, double max) {
        plotMinimum = min;
        plotMaximum = max;
        setupHistogram();
    }
    
    protected void requestNewPredictionCurve() {
        cumulativeHistogram.requestNewPredictionCurve();
        instHistogram.requestNewPredictionCurve();
    }
    
    protected abstract double calculateDistribWidth();
    protected abstract double distributionFunction(double x, ParticleType particleType);
}
