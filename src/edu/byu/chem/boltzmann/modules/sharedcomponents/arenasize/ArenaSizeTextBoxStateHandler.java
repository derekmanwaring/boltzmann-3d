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
package edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize;

import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeTextBoxStateHandler.ArenaSizeSettingsGUIState;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedEvent;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.Dimension;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;
import edu.byu.chem.boltzmann.view.utils.guistate.GUIStateHandler;
import javax.swing.JLabel;

/**
 *
 * @author Derek Manwaring
 * 17 Nov 2011
 */
public class ArenaSizeTextBoxStateHandler implements GUIStateHandler<ArenaSizeSettingsGUIState> {
    
    private final JLabel xSizeLabel;
    private final JLabel ySizeLabel;
    private final JLabel zSizeLabel;
    
    private final UnitTextField<Length> xSize;
    private final UnitTextField<Length> ySize;
    private final UnitTextField<Length> zSize;
    
    public ArenaSizeTextBoxStateHandler(
            JLabel xSizeLabel, UnitTextField<Length> xSize,
            JLabel ySizeLabel, UnitTextField<Length> ySize,
            JLabel zSizeLabel, UnitTextField<Length> zSize,
            DimensionSelector dimension) {
        
        this.xSizeLabel = xSizeLabel;
        this.ySizeLabel = ySizeLabel;
        this.zSizeLabel = zSizeLabel;
        
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        
        dimension.addListener(new DimensionSelector.DimensionChangedListener() {
            public void notify(DimensionChangedEvent newDimEvent) {
                updateState(newDimEvent.newDimension);
            }
        });
    }
    
    public void updateState(Dimension dimension) {
        switch (dimension) {
            case ONE:
                setState(ArenaSizeSettingsGUIState.ONE_DIMENSIONAL_ARENA);
                break;
            case TWO:
                setState(ArenaSizeSettingsGUIState.TWO_DIMENSIONAL_ARENA);
                break;
            case THREE:
                setState(ArenaSizeSettingsGUIState.THREE_DIMENSIONAL_ARENA);
        }        
    }

    public void setState(ArenaSizeSettingsGUIState state) {
        switch (state) {
            case ONE_DIMENSIONAL_ARENA:
                xSizeLabel.setEnabled(true);
                xSize.setEnabled(true);
                ySizeLabel.setEnabled(false);
                ySize.setEnabled(false);
                zSizeLabel.setEnabled(false);
                zSize.setEnabled(false);
                break;
            case TWO_DIMENSIONAL_ARENA:
                xSizeLabel.setEnabled(true);
                xSize.setEnabled(true);
                ySizeLabel.setEnabled(true);
                ySize.setEnabled(true);
                zSizeLabel.setEnabled(false);
                zSize.setEnabled(false);
                break;
            case THREE_DIMENSIONAL_ARENA:
                xSizeLabel.setEnabled(true);
                xSize.setEnabled(true);
                ySizeLabel.setEnabled(true);
                ySize.setEnabled(true);
                zSizeLabel.setEnabled(true);
                zSize.setEnabled(true);
        }
    }
    
    public enum ArenaSizeSettingsGUIState {
        ONE_DIMENSIONAL_ARENA,
        TWO_DIMENSIONAL_ARENA,
        THREE_DIMENSIONAL_ARENA        
    }
    
}
