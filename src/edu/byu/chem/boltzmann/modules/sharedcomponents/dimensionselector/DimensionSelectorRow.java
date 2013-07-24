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
package edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector;

import edu.byu.chem.boltzmann.modules.sharedcomponents.LabeledAlignedRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.ButtonGroupWithValues.SelectionChangedListener;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.Dimension;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannListenerHandler;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Derek Manwaring
 * 16 Nov 2011
 */
public class DimensionSelectorRow extends LabeledAlignedRow implements DimensionSelector {
    
    BoltzmannListenerHandler<DimensionChangedListener, DimensionChangedEvent> listeners = 
            new BoltzmannListenerHandler<DimensionChangedListener, DimensionChangedEvent>();
                
    ButtonGroupWithValues<Dimension> btnGrpDimension = 
            new ButtonGroupWithValues<Dimension>(
                    new SelectionChangedListener<Dimension>() {
                        public void selectionChanged(Dimension newSelectedDimension) {
                            listeners.notifyListeners(new DimensionChangedEvent(newSelectedDimension));
                        }                      
                    });
    
    public DimensionSelectorRow(int widthForAlignedSection, Dimension selectedDimension) {
        super (widthForAlignedSection);
        
        addLabel("Dimension");
        
        JRadioButton radBtn2Dim = new JRadioButton("2 Dimensional");
        JRadioButton radBtn3Dim = new JRadioButton("3 Dimensional");
        btnGrpDimension.addButtonWithValue(radBtn2Dim, Dimension.TWO);
        btnGrpDimension.addButtonWithValue(radBtn3Dim, Dimension.THREE);
        btnGrpDimension.setSelectedValue(selectedDimension);
        
        JPanel pnl2Dim = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
        JPanel pnl3Dim = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));        
        pnl2Dim.add(radBtn2Dim);
        pnl3Dim.add(radBtn3Dim);
        
        JPanel pnlDimensionSelectors = new JPanel();
        pnlDimensionSelectors.setLayout(new BoxLayout(
                pnlDimensionSelectors, 
                BoxLayout.PAGE_AXIS));
        pnlDimensionSelectors.add(pnl2Dim);    
        pnlDimensionSelectors.add(pnl3Dim);                 
        
        addRightAligned(pnlDimensionSelectors);
    }

    public void addListener(DimensionChangedListener listener) {
        listeners.addListener(listener);
    }

    public Dimension getSelectedDimension() {
        return btnGrpDimension.getSelectedValue();
    }

    public void setSelectedDimension(Dimension selectedDimension) {
        btnGrpDimension.setSelectedValue(selectedDimension);
    }
    
}
