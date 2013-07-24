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
package edu.byu.chem.boltzmann.model.statistics;

import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.statistics.parents.StatisticOld;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Calculates the Radial Distribution Function for a simulation's particles.
 * @author Derek Manwaring
 */
public class RadialDistribution extends StatisticOld {

    private static final String DISPLAY_NAME = "Radial Distribution";
    private static final Unit DEFAULT_UNIT = Units.DIMENSIONLESS;

    private static final String MIN_RATIO_NAME = "Minimum Ratio";
    private static final String MIN_RATIO_DESCRIPTION =
            "First interior minimum: right-most limit";

    private static final String MAX_RATIO_NAME = "Maximum Ratio";
    private static final String MAX_RATIO_DESCRIPTION =
            "Global maximum: right-most limit";

    // Calculations supplied by RadialDistribution
    public static final StatisticCalculation MIN_RATIO = new StatisticCalculation(
            MIN_RATIO_NAME, MIN_RATIO_DESCRIPTION, null);
    public static final StatisticCalculation MAX_RATIO = new StatisticCalculation(
            MAX_RATIO_NAME, MAX_RATIO_DESCRIPTION, null);

    private static final Set<StatisticCalculation> AVAILABLE_CALCULATIONS =
            new HashSet<StatisticCalculation>(Arrays.asList(
            new StatisticCalculation[] { MIN_RATIO, MAX_RATIO }));

    private static final Set<PlotType> AVAILABLE_PLOTS = EnumSet.of(PlotType.HISTOGRAM);

    private static final List<StatisticCalculation> PREFERRED_ORDER =
        Arrays.asList(
        new StatisticCalculation[] { MIN_RATIO, MAX_RATIO });

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public Unit getUnit(StatisticCalculation calculation) {
        return DEFAULT_UNIT;
    }

    @Override
    public void drawPlot(PlotType plotType, Graphics graphics, StatPanel plotPanel, boolean instantaneous) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<PlotType> getAvailablePlotTypes() {
        return AVAILABLE_PLOTS;
    }

    @Override
    public Set<StatisticCalculation> getAvailableCalculations() {
        return AVAILABLE_CALCULATIONS;
    }

    @Override
    public List<StatisticCalculation> getPreferredCalcOrder() {
        return PREFERRED_ORDER;
    }

    @Override
    public void prepareInstCalculations(FrameInfo frame) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMinimumCalculations() {
        return 2;
    }

    @Override
    public double getCalculation(StatisticCalculation calculation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getInstCalculation(StatisticCalculation calculation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateByEvent(EventInfo event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
