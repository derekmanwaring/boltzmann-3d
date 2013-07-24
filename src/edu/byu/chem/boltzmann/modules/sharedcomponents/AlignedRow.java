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
package edu.byu.chem.boltzmann.modules.sharedcomponents;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Derek Manwaring
 * 11 Nov 2011
 */
public class AlignedRow extends JPanel {
    
    private final JPanel pnlAlign;
    
    private final int widthForAlignedSection;
    
    public AlignedRow(int widthForAlignedSection) {
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));       
        
        this.widthForAlignedSection = widthForAlignedSection;
        pnlAlign = new JPanel(new BorderLayout());
        
        Dimension alignedSize = pnlAlign.getPreferredSize();
        alignedSize.width = widthForAlignedSection;
        alignedSize.height = 50;
        pnlAlign.setPreferredSize(alignedSize);
        
        add(pnlAlign);
    }
    
    public void addLeftAligned(Component component) {   
        forgetCurrentPreferredSize();
        
        pnlAlign.add(component, BorderLayout.WEST);
        
        setPreferredSizeForAlignment();
    }
    
    public void addRightAligned(Component component) {        
        forgetCurrentPreferredSize();
        
        pnlAlign.add(component, BorderLayout.EAST);
        
        setPreferredSizeForAlignment();
    }
    
    private void forgetCurrentPreferredSize() {
        pnlAlign.setPreferredSize(null);        
    }
    
    private void setPreferredSizeForAlignment() {
        Dimension alignedSize = pnlAlign.getPreferredSize();
        alignedSize.width = widthForAlignedSection;
        pnlAlign.setPreferredSize(alignedSize);        
    }
}
