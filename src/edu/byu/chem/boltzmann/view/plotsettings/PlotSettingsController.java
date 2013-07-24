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
package edu.byu.chem.boltzmann.view.plotsettings;

import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;

/**
 * Created 15 Jun 2011
 * @author Derek Manwaring
 */
public class PlotSettingsController {
    
    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final PlotSettingsView view;

    public PlotSettingsController(edu.byu.chem.boltzmann.controller.Controller rootController, PlotSettingsView view) {
        this.view = view;
        this.rootController = rootController;
    }
    
    public void setCurrentStatistic(StatisticWithDistribution statistic) {
        view.setCurrentStatistic(statistic);
    }
    
    public void hidePlotSettings() {
        rootController.hidePlotSettings();
    }    
}
