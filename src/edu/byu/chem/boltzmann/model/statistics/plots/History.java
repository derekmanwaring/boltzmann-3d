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

import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created 5 May 2011
 * @author Derek Manwaring
 */
public class History {
    
    private static final int MAX_HIST_POINTS = 300;

    private List<Double> historyPoints = new LinkedList<Double>();
    private Range plotRange;

    public void drawHistory(Graphics graphics,
            StatPanel plotPanel) {
        Dimension size = plotPanel.getSize();

        drawHistoryBackground(graphics, plotPanel);
        
        int[] xCoords = new int[size.width];
        int[] yCoords = new int[size.width];

        double[] histPointsArray = new double[historyPoints.size()];
        Double[] histPointsArrayRef = new Double[historyPoints.size()];
        historyPoints.toArray(histPointsArrayRef);

        for (int i = 0; i < histPointsArray.length; i++) {
            histPointsArray[i] = histPointsArrayRef[i];
        }

        PlotUtils.centerPoints(histPointsArray, plotRange.getMidpoint());

        int[] yCoordsSub = PlotUtils.scalePoints(histPointsArray, size.height / 2, 
                plotRange.max);

        for (int i = 0; i < xCoords.length; i++) {
            if ((xCoords.length - i) < yCoordsSub.length) {
                yCoords[i] = yCoordsSub[yCoordsSub.length - (xCoords.length - i)];
            } else {
                yCoords[i] = 0;
            }

            xCoords[i] = i;
        }

        PlotUtils.reversePoints(yCoords, 0);
        PlotUtils.centerPoints(yCoords, - size.height / 2);

        graphics.drawPolyline(xCoords, yCoords, xCoords.length);
    }

    private void drawHistoryBackground(Graphics graphics, StatPanel plotPanel) {
        Dimension size = plotPanel.getSize();

        graphics.setColor(Color.RED);
        //Draw the middle red line
        graphics.drawLine(0,size.height/2, size.width, size.height/2);
        //Draw the dotted lines at 1/2 sigma
        ((Graphics2D)graphics).setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,1, new float[]{7,3}, 0));
        graphics.drawLine(0,size.height/4, size.width, size.height/4);
        graphics.drawLine(0,3*size.height/4, size.width, 3*size.height/4);
        ((Graphics2D)graphics).setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        //Draw the 1/2 sigma labels
        graphics.drawString("\u00BD\u03C3", size.width/2-8, size.height/4-2);
        graphics.drawString("\u00BD\u03C3", size.width/2-8, 3*size.height/4-2);
    }

    public void reset() {
        historyPoints.clear();
    }

    public void addPoint(double value) {
        historyPoints.add(value);

        if (historyPoints.size() > MAX_HIST_POINTS) {
            historyPoints.remove(0);
        }
    }

    public void setRange(Range range) {
        this.plotRange = range;
    }
}
