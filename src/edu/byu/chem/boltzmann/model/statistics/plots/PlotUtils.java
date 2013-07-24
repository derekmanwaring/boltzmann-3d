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

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created 5 May 2011
 * Utility methods for plots.
 * @author Derek Manwaring
 */
public class PlotUtils {

    public enum PlotType {
        HISTOGRAM,
        HISTORY
    }

    //For drawing the histogram with lighter particles on the bottom
    protected final static Comparator<ParticleType> SORT_BY_WEIGHT = new Comparator<ParticleType>() {
        public int compare(ParticleType o1, ParticleType o2) {
            int returnVal = -Double.compare(o2.particleMass, o1.particleMass); //Lightest drawn first
            if (returnVal == 0) {
                return o1.displayName.compareTo(o2.displayName);
            } else {
                return returnVal;
            }
        }
    };

    /**
     * @return A double that is the factor bin heights should be multiplied by
     * to get their normalized heights.
     */
    protected static double calculateNormalizingFactor(int maxBinHeight, double[] bins) {
        //First, find the largest bin
        int max = 0;
        for (int i = 1; i < bins.length; i++) {
            if (bins[i] > bins[max]) max = i;
        }

        //Calculate the normalization factor
        double normalize = maxBinHeight / (bins[max]);

        return normalize;
    }

    public static int[] scalePoints(double[] points, int height) {
        double maxValue = -1.0;

        for (int i = 0; i < points.length; i++) {
            if (points[i] > maxValue && points[i] != Double.POSITIVE_INFINITY) {
                maxValue = points[i];
            }
        }

        return scalePoints(points, height, maxValue);
    }

    public static int[] scalePoints(double[] points, int height, double maxValue) {
        int[] scaledPoints = new int[points.length];

        if (maxValue <= 0) {
            Arrays.fill(scaledPoints, height - 1);
        } else {
            for (int i = 0; i < points.length; i++) {
                scaledPoints[i] = (int) (((points[i] / maxValue)) * height);
            }
        }

        return scaledPoints;
    }

    public static void reversePoints(int[] points, int height) {
        for (int i = 0; i < points.length; i++) {
            points[i] = height - points[i];
        }
    }

    /**
     * Centers the points at zeroPoint by subracting zeroPoint from each value
     */
    protected static void centerPoints(double[] points, double zeroPoint) {
        for (int i = 0; i < points.length; i++) {
            points[i] -= zeroPoint;
        }
    }

    protected static void centerPoints(int[] points, int zeroPoint) {
        for (int i = 0; i < points.length; i++) {
            points[i] -= zeroPoint;
        }
    }
}
