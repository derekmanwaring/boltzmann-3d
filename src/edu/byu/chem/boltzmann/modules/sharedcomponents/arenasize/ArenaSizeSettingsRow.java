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

import edu.byu.chem.boltzmann.modules.sharedcomponents.AlignedRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberCommitted;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannListenerHandler;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Derek Manwaring
 * 17 Nov 2011
 */
public class ArenaSizeSettingsRow extends AlignedRow implements ArenaSizeSettings {
    
    private BoltzmannListenerHandler<ArenaSizeChangedListener, ArenaSizeChangedEvent> listeners = 
            new BoltzmannListenerHandler<ArenaSizeChangedListener, ArenaSizeChangedEvent>();
    
    private final UnitTextField<Length> xSize;
    private final UnitTextField<Length> ySize;
    private final UnitTextField<Length> zSize;
    
    private static final Length LENGTH_UNITS = Length.NANOMETER;
    
    private final DimensionSelector dimension;
    private final ArenaSizeTextBoxStateHandler stateHandler;
    
    public NewNumberListener sizeChangedListener = new NewNumberListener() {
        public void notify(NewNumberCommitted event) {
            listeners.notifyListeners(new ArenaSizeChangedEvent());
        }
    };
    
    public ArenaSizeSettingsRow(int widthForAlignedSection,
            double defaultArenaSize, 
            double minArenaDimension, 
            double maxArenaDimension, 
            int textFieldColumns,
            DimensionSelector dimension) {
        super(widthForAlignedSection);
        this.dimension = dimension;        
        
        xSize = new UnitTextField<Length>(LENGTH_UNITS, defaultArenaSize, minArenaDimension, maxArenaDimension, textFieldColumns);
        ySize = new UnitTextField<Length>(LENGTH_UNITS, defaultArenaSize, minArenaDimension, maxArenaDimension, textFieldColumns);
        zSize = new UnitTextField<Length>(LENGTH_UNITS, defaultArenaSize, minArenaDimension, maxArenaDimension, textFieldColumns);
        
        xSize.addListener(sizeChangedListener);
        ySize.addListener(sizeChangedListener);
        zSize.addListener(sizeChangedListener);
        
        JPanel pnlFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel xLabel = new JLabel(); // not used; just to make the state handler happy
        pnlFields.add(xSize);
        JLabel xyLabel = new JLabel("X");
        pnlFields.add(xyLabel);
        pnlFields.add(ySize);
        JLabel yzLabel = new JLabel("X");
        pnlFields.add(yzLabel);
        pnlFields.add(zSize);
        
        addRightAligned(pnlFields);
        
        stateHandler = new ArenaSizeTextBoxStateHandler(
                xLabel, xSize, 
                xyLabel, ySize, 
                yzLabel, zSize, dimension);
    }

    public void addListener(ArenaSizeChangedListener listener) {
        listeners.addListener(listener);
    }

    public void setSize(double xSize, double ySize, double zSize, Length sizeUnits) {
        this.xSize.setValueInUnits(xSize, sizeUnits);
        this.ySize.setValueInUnits(ySize, sizeUnits);
        this.zSize.setValueInUnits(zSize, sizeUnits);
    }

    public double getXSize(Length sizeUnits) {
        return xSize.getValueInUnits(sizeUnits);
    }

    public double getYSize(Length sizeUnits) {
        return ySize.getValueInUnits(sizeUnits);
    }

    public double getZSize(Length sizeUnits) {
        return zSize.getValueInUnits(sizeUnits);
    }
    
    public void updateState() {
        stateHandler.updateState(dimension.getSelectedDimension());
    }

    public void setXSize(double xSize, Length units) {
        this.xSize.setValueInUnits(xSize, units);
    }

    public void setYSize(double ySize, Length units) {
        this.ySize.setValueInUnits(ySize, units);
    }

    public void setZSize(double zSize, Length units) {
        this.zSize.setValueInUnits(zSize, units);
    }
}
