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
package edu.byu.chem.boltzmann.model.statistics.plots;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.statistics.StatUtils;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.swing.SwingUtilities;

/**
 * Keeps track of and draws a histogram plot. This includes axes, prediction curve,
 * and bins.
 * @author Derek Manwaring
 */
public class Histogram {

    public interface DistributionFunction {
        public double probability(double value, ParticleType type);
    }

    private HistogramBins bins = null;

    private double plotMinimum = 0.0;
    private double plotMaximum = 0.0;

    private BufferedImage predictionCurve = null;

    private DistributionFunction distributionFunction = null;

    private Set<ParticleType> typesPlotted = null;

    private SimulationInfo simInfo = null;
    
    private boolean newPredictionCurveNeeded = true;

    public Histogram(double minValue,
            double maxValue,
            int numberOfBins,
            Set<ParticleType> typesPlotted,
            DistributionFunction distributionFunction,
            SimulationInfo simInfo) {
        plotMinimum = minValue;
        plotMaximum = maxValue;

        //bins = new HistogramBins(minValue, maxValue, numberOfBins, typesPlotted);

        this.distributionFunction = distributionFunction;

        this.typesPlotted = typesPlotted;

        this.simInfo = simInfo;
    }

    public synchronized void addWeightedValue(double value, double weight, Color color) {
        bins.addWeightedValue(value, weight, color);
    }

    public synchronized void drawHistogram(Graphics graphics, StatPanel plotPanel) {

        Dimension size = plotPanel.getSize();

        bins.drawHistogramBars(size, graphics);
        
        drawHistogramLines(size, graphics);

        int width = size.width - (2 * StatPanel.edge);
        int bottomY = size.height - StatPanel.bottomBuffer - StatPanel.edge;
        int height = bottomY - StatPanel.topEdge;

        overlayPredictionCurve(graphics, width, height);
    }

    private void drawHistogramLines(Dimension panelSize, Graphics graphics) {
        graphics.setColor(Color.BLACK);
        FontMetrics font = graphics.getFontMetrics();

        //Draw the axis.
        graphics.drawLine(StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer, panelSize.width-StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer);
        graphics.drawLine(StatPanel.edge, StatPanel.topEdge, StatPanel.edge, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer);
        double dStat = plotMaximum - plotMinimum;
        int width = panelSize.width-(2*StatPanel.edge);
        
        //Draw the tick marks and x-axis labels.
        for (int i = 0; i < 5; i++)
        {
            int x = StatPanel.edge + (i * width) / 4;
            double value = plotMinimum + (i * dStat) / 4.0;
            

            //Vertically displaces every other label.
            int fall = ((i % 2 == 1) ? 10 : 0);

            graphics.drawLine(x,panelSize.height-StatPanel.edge-StatPanel.bottomBuffer-1,x,panelSize.height-StatPanel.edge-StatPanel.bottomBuffer+2+fall);

            //Round the first and last labels, as in the text box abscissa limits.
            String str;
            if (i == 0 || i == 4) {
                double[] minAndMax = StatUtils.roundValues(plotMinimum, plotMaximum);
                if (i == 0) str = ErrorHandler.format(minAndMax[0], 5);
                else str = ErrorHandler.format(minAndMax[1], 5);
            } else {
                str = ErrorHandler.format(0.0 + value, 5);
            }

            graphics.drawString(str ,x-SwingUtilities.computeStringWidth(font,str)/2, panelSize.height-StatPanel.edge-StatPanel.bottomBuffer+15+fall);
        }
    }

    private void overlayPredictionCurve(Graphics g, int width, int height) {
        if (distributionFunction == null) { //No prediction curve
            return;
        }

        if (predictionCurve == null || newPredictionCurveNeeded == true) {
            createPredictionCurve(width, height, plotMinimum, plotMaximum);
        }

        int topY = StatPanel.topEdge;
        int leftX = StatPanel.edge;

        g.drawImage(predictionCurve, leftX, topY, null);
    }

    private void createPredictionCurve(int width, int height, double plotMin, double plotMax) {
        predictionCurve = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = predictionCurve.createGraphics();

        double[] unscaledPoints = calculateCurvePoints(width, plotMin, plotMax);
        int[] yPoints = PlotUtils.scalePoints(unscaledPoints, height);

        PlotUtils.reversePoints(yPoints, height);

        int[] xPoints = new int[ yPoints.length ];
        for (int i = 0; i < xPoints.length; i++) {
            xPoints[ i ] = i;
        }

        g.setColor(Color.BLACK);
        g.drawPolyline(xPoints, yPoints, xPoints.length);
        
        newPredictionCurveNeeded = false;
    }
    
    private double[] calculateCurvePoints(int plotWidth, double plotMin, double plotMax) {
        double[] distribution = new double[ plotWidth ];
        double span = plotMax - plotMin;
        boolean wasZero = false;

        for (int i = 0; i < plotWidth; i++) {
            double xValue = span * (((double) i) + 0.5) / ((double) plotWidth) + plotMin;

            double yValue = 0.0;
            int totalParticles = 0;
            for (ParticleType type: typesPlotted) {
                //Probability for current x value
                double yValueForType = distributionFunction.probability(xValue, type);
                int particles = simInfo.getNumberOfParticles(type);
                yValue += yValueForType * particles;
                totalParticles += particles;
            }
            
            double pointHeight = yValue / totalParticles;
            
            distribution[ i ] = pointHeight;
        }

        return distribution;
    }

    public synchronized void resetBins() {
        bins.resetBins();
    }
    
    public void requestNewPredictionCurve() {
        newPredictionCurveNeeded = true;
    }
    
//    public void setPlotLimits(double min, double max) {
//        plotMinimum = min;
//        plotMaximum = max;
//        newPredictionCurveNeeded = true;
//    }
}
