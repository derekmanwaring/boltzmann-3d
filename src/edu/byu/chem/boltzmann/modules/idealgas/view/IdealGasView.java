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
package edu.byu.chem.boltzmann.modules.idealgas.view;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.statistics.StatUtils;
import edu.byu.chem.boltzmann.model.statistics.WallPressure;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Time;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.view.maingui.MainGuiController;
import edu.byu.chem.boltzmann.view.maingui.MainGuiView;
import edu.byu.chem.boltzmann.view.maingui.components.GLPanel;
import edu.byu.chem.boltzmann.view.maingui.components.PlayPauseButton;
import edu.byu.chem.boltzmann.view.maingui.components.speedspinner.SpeedSpinner;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitReadout;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

/**
 *
 * @author Derek Manwaring
 * 25 Oct 2011
 */
public class IdealGasView extends javax.swing.JPanel implements MainGuiView {

    private MainGuiController controller;
    
    private SimulationInfo simInfo;
    
    private final GLPanel glPanel = new GLPanel(this);
    
    private final IdealGasSimSettings simSettingsView = new IdealGasSimSettings();

    private final SpeedSpinner speedSpinner = new SpeedSpinner();
    private final PlayPauseButton playButton = new PlayPauseButton(false);
    private final UnitReadout<Time> txtFldSimTime = new UnitReadout<Time>(0.0, Time.SECOND);
    
    /** Creates new form Template */
    public IdealGasView() {
        initComponents();
        pnlStats.add(simSettingsView, 3);    
        
        pnlControls.add(txtFldSimTime, 0);
        pnlControls.add(playButton, 1);
        pnlControls.add(speedSpinner, 3);

        glPanel.setBorder(BorderFactory.createMatteBorder(5,
                        5, 5, 5, glPanel.getDivColor()));

        glPanel.setPreferredSize(pnlArenaHolder.getSize());
        pnlArenaHolder.add(glPanel, "Center");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlArenaHolder = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        pnlStats = new javax.swing.JPanel();
        pnlStatHeader = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlOtherStats = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pnlPrediction = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtFldPressurePrediction = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        pnlPrediction1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFldPressure = new javax.swing.JTextField();
        pnlSeparator1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        pnlExpandToFill = new javax.swing.JPanel();

        pnlArenaHolder.setMaximumSize(new java.awt.Dimension(3000, 3000));
        pnlArenaHolder.setMinimumSize(new java.awt.Dimension(1, 1));
        pnlArenaHolder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        pnlControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));

        jLabel3.setText("Speed:");
        pnlControls.add(jLabel3);

        pnlStats.setLayout(new javax.swing.BoxLayout(pnlStats, javax.swing.BoxLayout.PAGE_AXIS));

        pnlStatHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 0));
        pnlStatHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel2.setText("Pressure:");
        pnlStatHeader.add(jLabel2);

        pnlStats.add(pnlStatHeader);

        pnlOtherStats.setLayout(new javax.swing.BoxLayout(pnlOtherStats, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        pnlPrediction.setPreferredSize(new java.awt.Dimension(240, 25));
        pnlPrediction.setLayout(new java.awt.BorderLayout(5, 5));

        jLabel4.setText("Prediction");
        pnlPrediction.add(jLabel4, java.awt.BorderLayout.WEST);

        txtFldPressurePrediction.setColumns(8);
        txtFldPressurePrediction.setEditable(false);
        txtFldPressurePrediction.setText("30.0 MPa");
        pnlPrediction.add(txtFldPressurePrediction, java.awt.BorderLayout.EAST);

        jPanel2.add(pnlPrediction);

        pnlOtherStats.add(jPanel2);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        pnlPrediction1.setPreferredSize(new java.awt.Dimension(240, 25));
        pnlPrediction1.setLayout(new java.awt.BorderLayout(5, 5));

        jLabel5.setText("Average from walls");
        pnlPrediction1.add(jLabel5, java.awt.BorderLayout.WEST);

        txtFldPressure.setColumns(8);
        txtFldPressure.setEditable(false);
        txtFldPressure.setText("27.9 MPa");
        pnlPrediction1.add(txtFldPressure, java.awt.BorderLayout.EAST);

        jPanel3.add(pnlPrediction1);

        pnlOtherStats.add(jPanel3);

        pnlStats.add(pnlOtherStats);

        pnlSeparator1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlSeparator1.setLayout(new javax.swing.BoxLayout(pnlSeparator1, javax.swing.BoxLayout.LINE_AXIS));
        pnlSeparator1.add(jSeparator1);

        pnlStats.add(pnlSeparator1);

        pnlExpandToFill.setLayout(new java.awt.CardLayout());
        pnlStats.add(pnlExpandToFill);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlStats, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlArenaHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addComponent(pnlControls, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlArenaHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(pnlStats, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel pnlArenaHolder;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlExpandToFill;
    private javax.swing.JPanel pnlOtherStats;
    private javax.swing.JPanel pnlPrediction;
    private javax.swing.JPanel pnlPrediction1;
    private javax.swing.JPanel pnlSeparator1;
    private javax.swing.JPanel pnlStatHeader;
    private javax.swing.JPanel pnlStats;
    private javax.swing.JTextField txtFldPressure;
    private javax.swing.JTextField txtFldPressurePrediction;
    // End of variables declaration//GEN-END:variables

    private void rescaleArena() {
        
        int newArenaHeight = pnlArenaHolder.getHeight();
        int newArenaWidth = pnlArenaHolder.getWidth();
        glPanel.rescaleArena(newArenaWidth, newArenaHeight);
    }
    
    @Override
    public void attachController(MainGuiController controller) {        
        controller.attachSpeedSpinner(speedSpinner);
        controller.attachPlayPauseButton(playButton);
        this.controller = controller;
    }

    private Set<ParticleType> singleType;
    
    @Override
    public void setSimulationInfo(SimulationInfo simulationInfo) {
        this.simInfo = simulationInfo;
        glPanel.setSimulationInfo(simulationInfo);
        singleType = new HashSet<ParticleType>(simInfo.getParticleTypes());
        rescaleArena();
        revalidate();
    }

    private int statRefreshCounter = 0;
    
    private FrameInfo currentFrame = null;
    
    @Override
    public void displayFrame() {
        glPanel.drawFrame();
        
        final double frameEndTime = currentFrame.endTime;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtFldSimTime.setValue(frameEndTime, Time.SECOND);
            }
        });
    }
    
    @Override
    public void displayStatistics() {       
        WallPressure wallPressStat = (WallPressure) controller.getStatistic(singleType, WallPressure.class);

        String unit = wallPressStat.getUnit(StatUtils.PREDICTION).getSymbol();

        double pressurePredVal = wallPressStat.getCalculation(StatUtils.PREDICTION);
        String pressurePred = ErrorHandler.format(pressurePredVal, 5);
        txtFldPressurePrediction.setText(pressurePred + " " + unit);

        double pressureVal = wallPressStat.getCalculation(StatUtils.AVERAGE);
        String pressure = ErrorHandler.format(pressureVal, 5);
        txtFldPressure.setText(pressure + " " + unit);
    }
    
    public IdealGasSimSettings getSimSettingsView() {
        return simSettingsView;
    }
    
    @Override
    public void setNextFrameToDisplay(FrameInfo currentFrame) {
        this.currentFrame = currentFrame;
        glPanel.setNextFrameToDisplay(currentFrame);
    }

    @Override
    public void setPaused(boolean paused) {
    }

    @Override
    public MainGuiController getController() {
        return controller;
    }
}