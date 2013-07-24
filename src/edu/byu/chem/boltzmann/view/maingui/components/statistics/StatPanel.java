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
package edu.byu.chem.boltzmann.view.maingui.components.statistics;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.statistics.StatUtils;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.SingleAverageStatistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.model.statistics.plots.History;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//Legacy comments:
// rendering and could be greatly sped up if rewritten in OpenGL as well.  (I
// have done this in the Lennard-Jones simulator and plan on one day porting that
// to Java to improve the performance.

/** Class StatPanel - Shows the statistics display.  This uses standard Java 2D rendering. */
public class StatPanel extends JPanel {
	
    /** Left and right margin (in pixels) */
    public static final int edge = 22;

    /** Top margin (in pixels) */
    public static final int topEdge = 15;

    /** Bottom margin (in pixels) */
    public static final int bottomBuffer = 15;

    private PlotType currentType = PlotType.HISTOGRAM;
    private Statistic currentStatistic = null;
    private boolean cumulativeAverage = false; //Plotting instantaneous / cumulative data
    
    //used by redrawPredictionCurve() and reused to scale lines in drawHistogramAvgAndWidthLines()
    private int[] predCurveYValues;

    /** Constructor - initializes background, font, etc. */
    public StatPanel() {
        setBackground (Color.WHITE);
        setBorder (BorderFactory.createBevelBorder (BevelBorder.LOWERED));
        setPreferredSize (new Dimension(200,190));
        setFont (new Font ("Arial", Font.PLAIN, 10));

        
    }
        
    @Override
    public void paintComponent (Graphics g) {
        //Draw the background.
        super.paintComponent(g);

        if (currentStatistic != null && currentType != null) {
            switch (currentType) {
                case HISTORY:
                    if (!(currentStatistic instanceof SingleAverageStatistic)) {
                        throw new IllegalArgumentException("History plots cannot be drawn for " + 
                                currentStatistic.getClass());
                    }
                    drawHistory(g, (SingleAverageStatistic) currentStatistic);
                    break;
                case HISTOGRAM:
                    if (!(currentStatistic instanceof StatisticWithDistribution)) {
                        throw new IllegalArgumentException("Histogram plots cannot be drawn for " + 
                                currentStatistic.getClass());
                    }
                    drawHistogram(g, (StatisticWithDistribution) currentStatistic);
                    break;
                default:
                    throw new IllegalArgumentException("Plot type " + currentType + " is not supported");                    
            }            
        }
            
    }

    BufferedImage currentPredictionLine = null;

    public void setPlotInfo(Statistic statistic, PlotType plotType, boolean cumulativeAverage) {
        currentStatistic = statistic;
        currentType = plotType;
        this.cumulativeAverage = cumulativeAverage;
    }

    private final Map<SingleAverageStatistic, History> historiesOfCurrentAverages = new HashMap<SingleAverageStatistic, History>();
    private final Map<SingleAverageStatistic, History> historiesOfCumulativeAverages = new HashMap<SingleAverageStatistic, History>();
    
    private void drawHistory(Graphics g, SingleAverageStatistic statistic) {
        History history;
        if (cumulativeAverage)
            history = historiesOfCumulativeAverages.get(statistic);
        else
            history = historiesOfCurrentAverages.get(statistic);
        if(history != null)//happens if the number of enabled statistics is changed while the plot type is history
            history.drawHistory(g, this);
    }

    @SuppressWarnings("unchecked")
    public void updateHistories() {
        for (SingleAverageStatistic statistic : historiesOfCurrentAverages.keySet()) {
            historiesOfCurrentAverages.get(statistic).addPoint(
                    statistic.getCurrentAverage(statistic.getDefaultDisplayUnit()));
            historiesOfCumulativeAverages.get(statistic).addPoint(
                    statistic.getCumulativeAverage(statistic.getDefaultDisplayUnit()));
        }
    }

    @SuppressWarnings("unchecked")
    public void setStatisticsWithHistories(Set<SingleAverageStatistic> statisticsWithHistories) {
        historiesOfCurrentAverages.clear();
        historiesOfCumulativeAverages.clear();
        
        for (SingleAverageStatistic statistic : statisticsWithHistories) {
            History historyOfCurrentAverages = new History();
            History historyOfCumulativeAverages = new History();
            
            Unit statisticUnit = statistic.getDefaultDisplayUnit();
            double averagePrediction = statistic.getPredictionForAverage(statisticUnit);
            final double distanceToMax;
            if (statistic instanceof StatisticWithDistribution) {
                StatisticWithDistribution statisticWithDist = (StatisticWithDistribution) statistic;
                double widthPrediction = statisticWithDist.getDistributionWidthPrediction(statisticUnit);
                if(Double.isNaN(widthPrediction))
                    widthPrediction = 0;
                distanceToMax = Math.max(widthPrediction, averagePrediction);
                predictionCurves.put(statisticWithDist, null);
            } else {
                distanceToMax = 0.5 * averagePrediction;
            }
            
            Range plotRange = new Range(averagePrediction, distanceToMax, true);
            historyOfCurrentAverages.setRange(plotRange);
            historyOfCumulativeAverages.setRange(plotRange);
            
            historiesOfCurrentAverages.put(statistic, historyOfCurrentAverages);
            historiesOfCumulativeAverages.put(statistic, historyOfCumulativeAverages);
        }
    }

    private final Map<StatisticWithDistribution, BufferedImage> predictionCurves = new HashMap<StatisticWithDistribution, BufferedImage>();
    
    @SuppressWarnings("unchecked")
    private void drawHistogram(Graphics g, StatisticWithDistribution statisticWithDistribution) {
        Unit statisticUnit = statisticWithDistribution.getDefaultDisplayUnit();        
        Range distributionRange = statisticWithDistribution.getDistributionRange(statisticUnit);        
        Dimension size = getSize();

        HistogramBins bins;
        if (cumulativeAverage) {
            bins = statisticWithDistribution.getHistogramBinsForCumulativeDistribution();
        } else {
            bins = statisticWithDistribution.getHistogramBinsForCurrentDistribution();
        }
        bins.drawHistogramBars(size, g);
        
        drawHistogramLines(size, g, distributionRange);

        overlayPredictionCurve(g, statisticWithDistribution);
        
        drawHistogramAvgAndWidthLines(g, statisticWithDistribution, distributionRange, statisticUnit);
    }

    private void redrawPredictionCurve(StatisticWithDistribution statisticWithDistribution) {
        int width = getSize().width - (2 * StatPanel.edge);
        int bottomY = getSize().height - StatPanel.bottomBuffer - StatPanel.edge;
        int height = bottomY - StatPanel.topEdge;
        
        BufferedImage predictionCurve = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = predictionCurve.createGraphics();

        double[] unscaledPoints = statisticWithDistribution.getPredictionCurve(width);
        predCurveYValues = PlotUtils.scalePoints(unscaledPoints, height);

        PlotUtils.reversePoints(predCurveYValues, height);

        int[] xValues = new int[ predCurveYValues.length ];
        for (int i = 0; i < xValues.length; i++) {
            xValues[ i ] = i;
        }

        g.setColor(Color.BLACK);
        g.drawPolyline(xValues, predCurveYValues, xValues.length);
        
        predictionCurves.put(statisticWithDistribution, predictionCurve);
    }

    @SuppressWarnings("unchecked")
    private void drawHistogramAvgAndWidthLines(Graphics g, StatisticWithDistribution statisticWithDistribution, Range range, Unit defaultUnit) {
        double predictedAverage = statisticWithDistribution.getPredictionForAverage(defaultUnit);
        double actualAverage = cumulativeAverage ? statisticWithDistribution.getCumulativeAverage(defaultUnit)
                    : statisticWithDistribution.getCurrentAverage(defaultUnit);
        double predictedWidth = statisticWithDistribution.getDistributionWidthPrediction(defaultUnit);
        double actualWidth = cumulativeAverage ? statisticWithDistribution.getCumulativeDistributionWidth(defaultUnit)
                    : statisticWithDistribution.getCurrentDistributionWidth(defaultUnit);
        
        double rangeToPixelScaleFactor = predCurveYValues.length / range.getDistanceFromMinToMax();
        int bottomY = getSize().height - StatPanel.bottomBuffer - StatPanel.edge;
        
        drawHistogramAvgOrWidthLine(g, range, predictedAverage, actualAverage, rangeToPixelScaleFactor, bottomY);
        drawHistogramAvgOrWidthLine(g, range, predictedAverage - predictedWidth, actualAverage - actualWidth, rangeToPixelScaleFactor, bottomY);
        drawHistogramAvgOrWidthLine(g, range, predictedAverage + predictedWidth, actualAverage + actualWidth, rangeToPixelScaleFactor, bottomY);
    }
    
    private void drawHistogramAvgOrWidthLine(Graphics g, Range range, double predictedValue, double actualValue, double rangeToPixelScaleFactor, int bottomY){
        if(predictedValue >= range.min && predictedValue <= range.max){
            int predictedXValue = (int)Math.round((predictedValue - range.min) * rangeToPixelScaleFactor);
            int actualXValue = (int)Math.round((actualValue - range.min) * rangeToPixelScaleFactor);
            if(actualXValue == predictedXValue)
                actualXValue++;
            int predictedYValue = predCurveYValues[predictedXValue];
                
            g.setColor(Color.ORANGE);
            g.drawRect(predictedXValue + StatPanel.edge, bottomY, 1, predictedYValue - bottomY + StatPanel.topEdge);
            
            g.setColor(new Color(0, 210, 0));//special green
            g.drawRect(actualXValue + StatPanel.edge, bottomY, 1, predictedYValue - bottomY + StatPanel.topEdge);//same height as prediction
        }
    }
    
    private void drawHistogramLines(Dimension panelSize, Graphics g, Range distributionRange) {
        g.setColor(Color.BLACK);
        FontMetrics font = g.getFontMetrics();

        //Draw the axis.
        g.drawLine(StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer, panelSize.width-StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer);
        g.drawLine(StatPanel.edge, StatPanel.topEdge, StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer);
        double dStat = distributionRange.getDistanceFromMinToMax();
        int width = panelSize.width-(2*StatPanel.edge);
        
        //Draw the tick marks and x-axis labels.
        for (int i = 0; i < 5; i++)
        {
            int x = StatPanel.edge + (i * width) / 4;
            double value = distributionRange.min + (i * dStat) / 4.0;
            

            //Vertically displaces every other label.
            int fall = ((i % 2 == 1) ? 10 : 0);

            g.drawLine(x,panelSize.height-StatPanel.edge-StatPanel.bottomBuffer-1,x,panelSize.height-StatPanel.edge-StatPanel.bottomBuffer+2+fall);

            //Round the first and last labels, as in the text box abscissa limits.
            String str;
            if (i == 0 || i == 4) {
                double[] minAndMax = StatUtils.roundValues(distributionRange.min, distributionRange.max);
                if (i == 0) str = ErrorHandler.format(minAndMax[0], 5);
                else str = ErrorHandler.format(minAndMax[1], 5);
            } else {
                str = ErrorHandler.format(0.0 + value, 5);
            }

            g.drawString(str ,x-SwingUtilities.computeStringWidth(font,str)/2, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer+15+fall);
        }
    }

    private void overlayPredictionCurve(Graphics g, StatisticWithDistribution statisticWithDistribution) {        
        if (statisticWithDistribution.hasNewPredictionCurve()) {
            redrawPredictionCurve(statisticWithDistribution);
        }

        int topY = StatPanel.topEdge;
        int leftX = StatPanel.edge;

        g.drawImage(predictionCurves.get(statisticWithDistribution), leftX, topY, null);
    }

    public void clearPlot() {
        currentStatistic = null;
        currentType = null; 
    }
}
