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
package edu.byu.chem.boltzmann.view.simulationsettings;

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SelectedStatistics;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypeDescriptor;
import java.util.List;

/**
 *
 * @author Derek Manwaring
 * 20 Oct 2011
 */
public interface SimSettingsView {
    
    public void attachController(SimSettingsController controller);

    public ArenaType getArenaType();

    public int getDimension();

    public double getArenaXSize();

    public double getArenaYSize();

    public double getArenaZSize();

    public double getHoleDiameter();

    public boolean isMaxwellDemonModeSelected();

    public boolean isReactionModeSelected();

    public boolean areAttractiveInteractionsAllowed();
    
    public double getWellDepth(TypeDescriptor type);
    
    public double getRadiusOfInteractionMultiplier();

    public boolean includeAttractiveWall();

    public WallEnum getAttractiveWall();

    public double getWallWellWidth();

    public double getWallWellDepth();

    public boolean includeHeatReservoir();

    public double getInitialReservoirTemperature();

    public double getInitialTemperature();

    public SelectedStatistics<TypeDescriptor> getSelectedStats();
    
    public List<TypeDescriptor> getTypeDescriptors();

    public double getForwardEnergy();

    public double getReverseEnergy();

    public boolean shouldSuppressRevReaction();
    
    public void setSimulationInfo(SimulationInfo simInfo);
    
}
