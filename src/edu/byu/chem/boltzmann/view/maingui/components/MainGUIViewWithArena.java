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
package edu.byu.chem.boltzmann.view.maingui.components;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.view.maingui.MainGuiController;
import edu.byu.chem.boltzmann.view.maingui.MainGuiView;
import edu.byu.chem.boltzmann.view.maingui.components.speedspinner.SpeedSpinner;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Derek Manwaring
 * 9 May 2012
 */
public abstract class MainGUIViewWithArena implements MainGuiView {

    private MainGuiController controller;
    
    private final GLPanel glPanel = new GLPanel(this);

    private final SpeedSpinner speedSpinner = new SpeedSpinner();
    private final PlayPauseButton playButton = new PlayPauseButton(false);
    
    private final JPanel pnlArenaHolder = new JPanel();
    private final JTextField txtFldSimTime = new JTextField();
    
    public MainGUIViewWithArena() {
        pnlArenaHolder.setMaximumSize(new java.awt.Dimension(3000, 3000));
        pnlArenaHolder.setMinimumSize(new java.awt.Dimension(1, 1));
        pnlArenaHolder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        
        glPanel.setBorder(BorderFactory.createMatteBorder(5,
                        5, 5, 5, glPanel.getDivColor()));
        glPanel.setPreferredSize(pnlArenaHolder.getSize());
        pnlArenaHolder.add(glPanel, "Center");
        
    }

    @Override
    public void attachController(MainGuiController controller) {   
        controller.attachSpeedSpinner(speedSpinner);
        controller.attachPlayPauseButton(playButton);
        this.controller = controller;
    }
    
    private FrameInfo currentFrame = null;

    @Override
    public void displayFrame() {
        glPanel.drawFrame();
        
        final double frameEndTime = currentFrame.endTime;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtFldSimTime.setText(ErrorHandler.format(Units.convert("s", "ps", frameEndTime), 5) + " ps");
            }
        });
    }
    
    @Override
    public void setNextFrameToDisplay(FrameInfo currentFrame) {
        this.currentFrame = currentFrame;
        glPanel.setNextFrameToDisplay(currentFrame);
    }
    
    @Override
    public MainGuiController getController() {
        return controller;
    }

}
