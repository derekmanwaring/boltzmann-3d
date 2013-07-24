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
package edu.byu.chem.boltzmann.model.statistics.interfaces;

import edu.byu.chem.boltzmann.model.statistics.plots.HistogramBins;
import edu.byu.chem.boltzmann.utils.Units.Unit;

/**
 * Statistics that have distributions and can provide information about them. 
 * 
 * @author Derek Manwaring
 * 18 May 2012
 */
public interface StatisticWithDistribution<UnitType extends Unit<UnitType>> extends SingleAverageStatistic<UnitType> {
    
    /**
     * @param unit
     * @return The predicted width of this statistic's distribution in the requested
     * unit.
     */
    public double getDistributionWidthPrediction(UnitType unit);
    
    /**
     * @param unit
     * @return The current measured width of this statistic's distribution in the
     * requested unit.
     */
    public double getCurrentDistributionWidth(UnitType unit);
    
    /**
     * @param unit
     * @return The cumulative measured width for this statistic's distribution from 
     * the last time its data was cleared. The average will be in the requested unit.
     */
    public double getCumulativeDistributionWidth(UnitType unit);
    
    /**
     * @return The bins for this statistic's current measured distribution. The
     * bins' values are spread according to the distribution's range.
     */
    public HistogramBins getHistogramBinsForCurrentDistribution();
    
    /**
     * @return The bins for this statistic's cumulative measured distribution. The
     * bins' values are spread according to the distribution's range.
     */
    public HistogramBins getHistogramBinsForCumulativeDistribution();
    
    /**
     * @param unit
     * @return The range of this statistic's distribution in the requested unit.
    */
    public Range getDistributionRange(UnitType unit);
    
    /**
     * Sets a new range to be used for this statistic's distribution. This will
     * reset the histogram bins.
     * @param range
     * @param unit 
     */
    public void setDistributionRange(Range range, UnitType unit);
    
    /**
     * @return True if a different distribution curve is available since the last
     * time {@link getPredictionCurve(int)} was called.
     */
    public boolean hasNewPredictionCurve();
    
    /**
     * @param points The number of points to draw over the distribution range.
     * @return An array of points for the prediction curve over this statistic's
     * distribution range.
     */
    public double[] getPredictionCurve(int points);
    
}
