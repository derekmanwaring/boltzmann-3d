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
package edu.byu.chem.boltzmann.fullapplication.view.maingui;

import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.InstantaneousSpeed;
import edu.byu.chem.boltzmann.model.statistics.KineticEnergy;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.SingleAverageStatistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticWithDistribution;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.model.statistics.plots.PlotUtils.PlotType;
import edu.byu.chem.boltzmann.resources.ResourceLoader;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Energy;
import edu.byu.chem.boltzmann.utils.Units.Time;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.view.maingui.MainGuiController;
import edu.byu.chem.boltzmann.view.maingui.components.GLPanel;
import edu.byu.chem.boltzmann.view.maingui.components.GLPanel.ColorMode;
import edu.byu.chem.boltzmann.view.maingui.components.GLPanel.ParticleColorer;
import edu.byu.chem.boltzmann.view.maingui.components.PlayPauseButton;
import edu.byu.chem.boltzmann.view.maingui.components.speedspinner.SpeedSpinner;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatisticReadout;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatPanel;
import edu.byu.chem.boltzmann.view.maingui.components.statistics.StatisticReadoutSingle;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitReadout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Derek Manwaring
 * 24 Mar 2011
 */
public class MainGuiFullView extends 
        javax.swing.JPanel implements 
        edu.byu.chem.boltzmann.view.maingui.MainGuiView {

    private final GLPanel glPanel = new GLPanel(this);
    
    private SimulationInfo simulationInfo;

    private Map<StatisticID, StatisticReadout> statisticReadouts = 
            new EnumMap<StatisticID, StatisticReadout>(StatisticID.class);
    private static final Comparator<Object> TO_STRING_COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            int returnVal =  o1.toString().compareTo(o2.toString());
            o1.toString();
            if (returnVal == 0) {
                //Only say the two are equal if they're equal on their terms. Otherwise the order does
                //not matter, but we say one's ahead of the other so it still is added to the set.
                return o1.equals(o2) ? 0 : 1;
            } else {
                return returnVal;
            }
        }
    };

    private PlotType currentPlotType = PlotType.HISTOGRAM;

    final private StatPanel plotPanel;

    public static final String CMB_BOX_NONE = "None";

    private MainGuiController controller;
    
    private ColorMode colorMode;
    
    private ParticleColorer defaultSpeedColorer = null;
    private ParticleColorer defaultKEColorer = null;
    
    private SpeedSpinner speedSpinner = new SpeedSpinner();
    private PlayPauseButton playButton = new PlayPauseButton(false);
    private UnitReadout<Time> txtFldSimTime = new UnitReadout<Time>(0.0, Time.SECOND, EnumSet.allOf(Time.class));

    /** Creates new form ViewImplementation */
    public MainGuiFullView() {

        this.simulationInfo = new SimulationInfo();
        
        setLocation(100, 50);
        initComponents();
    
        pnlControls.add(txtFldSimTime, 0);
        pnlControls.add(playButton, 3);
        pnlControls.add(speedSpinner, 6);
        
        plotPanel = new StatPanel();
        plotPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlPlotHolderMouseClicked(evt);
            }
        });
        
        pnlPlotHolder.add(plotPanel, BorderLayout.NORTH);

        glPanel.setBorder(BorderFactory.createMatteBorder(5,
                        5, 5, 5, glPanel.getDivColor()));

        glPanel.setPreferredSize(pnlArenaHolder.getSize());
        pnlArenaHolder.add(glPanel, "Center");

        validate();
    }

    /**
     * Directs this view to use the given reference for its controller.
     */
    @Override
    public void attachController(MainGuiController controller) {
        this.controller = controller;
        
        controller.attachSpeedSpinner(speedSpinner);
        controller.attachPlayPauseButton(playButton);
    }

    /**
     * Returns a reference to this view's controller.
     */
    @Override
    public MainGuiController getController() {
        return controller;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuPlotLimits = new javax.swing.JPopupMenu();
        mnuBtnChangePlotLimits = new javax.swing.JMenuItem();
        pnlArenaHolder = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        btnRestart = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        btnFrameStep = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pnlStats = new javax.swing.JPanel();
        pnlStatHeader = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbBoxTypes = new javax.swing.JComboBox();
        pnlSeparator1 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        pnlActivePlot = new javax.swing.JPanel();
        pnlPlotHeader = new javax.swing.JPanel();
        pnlPlotSelector = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cmbBoxPlots = new javax.swing.JComboBox();
        pnlPlotHolder = new javax.swing.JPanel();
        pnlPlotReadouts = new javax.swing.JPanel();
        pnlSeparator2 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        pnlOtherStats = new javax.swing.JPanel();
        pnlStatOptions = new javax.swing.JPanel();
        chkBoxCumulative = new javax.swing.JCheckBox();
        chkBoxWidth = new javax.swing.JCheckBox();
        chkBoxHistory = new javax.swing.JCheckBox();

        mnuBtnChangePlotLimits.setText("Change Plot Limits");
        mnuBtnChangePlotLimits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBtnChangePlotLimitsActionPerformed(evt);
            }
        });
        mnuPlotLimits.add(mnuBtnChangePlotLimits);

        pnlArenaHolder.setMaximumSize(new java.awt.Dimension(3000, 3000));
        pnlArenaHolder.setMinimumSize(new java.awt.Dimension(1, 1));
        pnlArenaHolder.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlArenaHolderComponentResized(evt);
            }
        });
        pnlArenaHolder.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        pnlControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));

        btnRestart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/Restart24.gif"))); // NOI18N
        btnRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestartActionPerformed(evt);
            }
        });
        pnlControls.add(btnRestart);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/TimeReverse24.gif"))); // NOI18N
        jToggleButton1.setEnabled(false);
        pnlControls.add(jToggleButton1);

        btnFrameStep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/StepForward24.gif"))); // NOI18N
        btnFrameStep.setEnabled(false);
        btnFrameStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFrameStepActionPerformed(evt);
            }
        });
        pnlControls.add(btnFrameStep);

        jLabel3.setText("Speed:");
        pnlControls.add(jLabel3);

        pnlStats.setLayout(new javax.swing.BoxLayout(pnlStats, javax.swing.BoxLayout.PAGE_AXIS));

        pnlStatHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 0));
        pnlStatHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel2.setText("Particles:");
        pnlStatHeader.add(jLabel2);

        cmbBoxTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None" }));
        cmbBoxTypes.setMaximumSize(new java.awt.Dimension(189, 32767));
        cmbBoxTypes.setMinimumSize(new java.awt.Dimension(189, 27));
        cmbBoxTypes.setPreferredSize(new java.awt.Dimension(189, 27));
        cmbBoxTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxTypesActionPerformed(evt);
            }
        });
        pnlStatHeader.add(cmbBoxTypes);

        pnlStats.add(pnlStatHeader);

        pnlSeparator1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlSeparator1.setLayout(new javax.swing.BoxLayout(pnlSeparator1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMinimumSize(null);
        jSeparator4.setPreferredSize(new java.awt.Dimension(32767, 2));
        pnlSeparator1.add(jSeparator4);

        pnlStats.add(pnlSeparator1);

        pnlActivePlot.setLayout(new javax.swing.BoxLayout(pnlActivePlot, javax.swing.BoxLayout.PAGE_AXIS));

        pnlPlotHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pnlPlotHeader.setLayout(new javax.swing.BoxLayout(pnlPlotHeader, javax.swing.BoxLayout.PAGE_AXIS));

        pnlPlotSelector.setPreferredSize(new java.awt.Dimension(266, 30));

        jLabel4.setText("Plot:");

        cmbBoxPlots.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None" }));
        cmbBoxPlots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxPlotsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlPlotSelectorLayout = new org.jdesktop.layout.GroupLayout(pnlPlotSelector);
        pnlPlotSelector.setLayout(pnlPlotSelectorLayout);
        pnlPlotSelectorLayout.setHorizontalGroup(
            pnlPlotSelectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPlotSelectorLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(jLabel4)
                .add(5, 5, 5)
                .add(cmbBoxPlots, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(184, 184, 184))
        );
        pnlPlotSelectorLayout.setVerticalGroup(
            pnlPlotSelectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPlotSelectorLayout.createSequentialGroup()
                .add(pnlPlotSelectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlPlotSelectorLayout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(jLabel4))
                    .add(pnlPlotSelectorLayout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(cmbBoxPlots, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(25, 25, 25))
        );

        pnlPlotHeader.add(pnlPlotSelector);

        pnlActivePlot.add(pnlPlotHeader);

        pnlPlotHolder.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlPlotHolder.setLayout(new javax.swing.BoxLayout(pnlPlotHolder, javax.swing.BoxLayout.PAGE_AXIS));
        pnlActivePlot.add(pnlPlotHolder);

        pnlPlotReadouts.setLayout(new javax.swing.BoxLayout(pnlPlotReadouts, javax.swing.BoxLayout.PAGE_AXIS));
        pnlActivePlot.add(pnlPlotReadouts);

        pnlStats.add(pnlActivePlot);

        pnlSeparator2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        pnlSeparator2.setLayout(new javax.swing.BoxLayout(pnlSeparator2, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator5.setMinimumSize(null);
        jSeparator5.setPreferredSize(null);
        pnlSeparator2.add(jSeparator5);

        pnlStats.add(pnlSeparator2);

        pnlOtherStats.setLayout(new javax.swing.BoxLayout(pnlOtherStats, javax.swing.BoxLayout.Y_AXIS));
        pnlStats.add(pnlOtherStats);

        chkBoxCumulative.setText("Cumulative");
        pnlStatOptions.add(chkBoxCumulative);

        chkBoxWidth.setText("Width");
        pnlStatOptions.add(chkBoxWidth);

        chkBoxHistory.setText("History");
        chkBoxHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxHistoryActionPerformed(evt);
            }
        });
        pnlStatOptions.add(chkBoxHistory);

        pnlStats.add(pnlStatOptions);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlStats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 276, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlControls, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlArenaHolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlStats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(pnlArenaHolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlControls, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pnlArenaHolderComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlArenaHolderComponentResized
        rescaleArena();
}//GEN-LAST:event_pnlArenaHolderComponentResized

    private void btnRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestartActionPerformed
        controller.restartSimulation();
}//GEN-LAST:event_btnRestartActionPerformed

    @SuppressWarnings({"unchecked"})
    private void cmbBoxTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxTypesActionPerformed
        Object selectedItem = cmbBoxTypes.getSelectedItem();
        if (selectedItem == null || selectedItem.equals(CMB_BOX_NONE)) {
            cmbBoxPlots.removeAllItems();
            cmbBoxPlots.addItem(CMB_BOX_NONE);
            return;
        }

        StatisticID lastSelectedStat = null;
        Object lastSelectedItem = cmbBoxPlots.getSelectedItem();
        if (StatisticID.class.isInstance(lastSelectedItem)) {
            lastSelectedStat = (StatisticID) cmbBoxPlots.getSelectedItem();
        }
        boolean wasHist = chkBoxHistory.isSelected();

        Set<ParticleType> selectedTypes = (Set<ParticleType>) cmbBoxTypes.getSelectedItem();
        for (StatisticReadout readout: statisticReadouts.values()) {
            ((javax.swing.JPanel) readout).setVisible(false);
        }

        if (Set.class.isInstance(selectedItem)) { //It might be the string that says "None"
            populatePlotsCombo(selectedTypes);
            for (StatisticID statisticID: simulationInfo.getStatistics(selectedTypes)) {
                StatisticReadout readout = statisticReadouts.get(statisticID);
                if (readout != null) {
                    ((javax.swing.JPanel) readout).setVisible(true);
                }
            }
        }

        //Before setting the selected plot so it will be unselected if unavailable
        chkBoxHistory.setSelected(wasHist);
        if (lastSelectedStat != null) {
            if (simulationInfo.getStatistics(selectedTypes).contains(lastSelectedStat)) {
                cmbBoxPlots.setSelectedItem(controller.getStatistic(selectedTypes, lastSelectedStat));
            }
        } else if (cmbBoxPlots.getItemCount() > 1) { //At least one plot is available so show it
            cmbBoxPlots.setSelectedIndex(0);
        } else {
            cmbBoxPlots.setSelectedItem(CMB_BOX_NONE);
        }
        setupPlotOptions();
}//GEN-LAST:event_cmbBoxTypesActionPerformed

    private void cmbBoxPlotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxPlotsActionPerformed
        setupPlotOptions();
}//GEN-LAST:event_cmbBoxPlotsActionPerformed

    private void chkBoxHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxHistoryActionPerformed
        decidePlotType();
}//GEN-LAST:event_chkBoxHistoryActionPerformed

    private void mnuBtnChangePlotLimitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBtnChangePlotLimitsActionPerformed
        StatisticID currentStat = getSelectedStatistic();
        
        if (currentStat.implementsInterface(StatisticWithDistribution.class)) {            
            controller.showPlotSettings();
        }
    }//GEN-LAST:event_mnuBtnChangePlotLimitsActionPerformed

    private void btnFrameStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFrameStepActionPerformed
        controller.advanceOneFrame();
    }//GEN-LAST:event_btnFrameStepActionPerformed
 
    private void pnlPlotHolderMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            StatisticID currentPlotStat = getSelectedStatistic();
            
            if (currentPlotStat.implementsInterface(StatisticWithDistribution.class)) {
                mnuPlotLimits.show(plotPanel, evt.getX(), evt.getY());
            }
        }    
    }
    
    @Override
    public void setSimulationInfo(SimulationInfo simulationInfo) {
        this.simulationInfo = simulationInfo;

        glPanel.setSimulationInfo(simulationInfo);

        initiateTypesBox();
        
        setupDefaultColorers();

        initializeStatisticDisplayComponents();

        cmbBoxTypesActionPerformed(null);

        rescaleArena();
        validate();
    }
    
    private void setupDefaultColorers() {
        defaultSpeedColorer = new ParticleColorer() {
            private final double maxSpeed = 2.5 * InstantaneousSpeed.averageVelocity(
                    simulationInfo, 
                    new HashSet<ParticleType>(simulationInfo.getParticleTypes()), 
                    false);
            
            @Override
            public Color getParticleColor(PartState state) {
                double particleSpeed = Formulas.speed(state);
                float hue = getHue(particleSpeed, maxSpeed);
                return Color.getHSBColor(hue, 1.0f, 1.0f);
            }            
        };
        
        defaultKEColorer = new ParticleColorer() {
            private final double maxKE = 2.5 * KineticEnergy.averageKineticEnergy(
                    simulationInfo, 
                    new HashSet<ParticleType>(simulationInfo.getParticleTypes()), 
                    false);
            
            public Color getParticleColor(PartState state) {
                double particleEnergy = Formulas.kineticEnergy(state);
                particleEnergy = Units.convert(Energy.JOULE, Energy.KILOJOULE_PER_MOLE, particleEnergy);
                float hue = getHue(particleEnergy, maxKE);
                return Color.getHSBColor(hue, 1.0f, 1.0f);
            }            
        };
    }

    /**
     * Set up the combo box that determines which particle types to display statistics for
     * based on new simulation info
     */
    @SuppressWarnings({"unchecked"})
    private void initiateTypesBox() {
        cmbBoxTypes.removeAllItems();

        //Sort sets of particle types alphabetically
        SortedSet<Set<ParticleType>> typesInOrder = new TreeSet<Set<ParticleType>>(new Comparator<Set<ParticleType>>() {
            public int compare(Set<ParticleType> o1, Set<ParticleType> o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        //Populate with all possible combinations after creating meaningful names for each combination
        for (Set<ParticleType> types: Formulas.getAllCombinations(
                new HashSet<ParticleType>(simulationInfo.getParticleTypes()))) {
            
            Iterator<ParticleType> typeIterator = types.iterator();
            String setName = typeIterator.next().displayName;
            while (typeIterator.hasNext()) {
                setName += " & " + typeIterator.next().displayName;
            }
            final String comboBoxElementName = setName;

            Set<ParticleType> sortableSet = new HashSet<ParticleType>(types) {
                @Override
                public String toString() {
                    return comboBoxElementName;
                }
            };

            if (!simulationInfo.getStatistics(types).isEmpty()) {
                typesInOrder.add(sortableSet);
            }
        }

        //Add combinations to the combo box (meaningful names will be displayed thanks to the last step
        for (Set<ParticleType> types: typesInOrder) {
            cmbBoxTypes.addItem(types);
        }

        if (typesInOrder.isEmpty()) {
            cmbBoxTypes.addItem(CMB_BOX_NONE);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void populatePlotsCombo(final Set<ParticleType> selectedTypes) {
        cmbBoxPlots.removeAllItems();

        Comparator<StatisticID> statComparator = 
                new Comparator<StatisticID>() {
            
            public int compare(StatisticID o1, StatisticID o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }            
        };
        
        SortedSet<StatisticID> orderedStats = new TreeSet<StatisticID>(statComparator);
                        
        for (StatisticID statisticID: simulationInfo.getStatistics(selectedTypes)) {
            orderedStats.add(statisticID);
        }
        
        for (StatisticID statisticID: orderedStats) {
            cmbBoxPlots.addItem(statisticID);
        }
    }

    /**
     * Erases the old collections of readouts and creates new ones based on the new simulation
     * info. New readouts are added to the appropriate panel and made invisible.
     */
    @SuppressWarnings("unchecked")
    private void initializeStatisticDisplayComponents() {
        statisticReadouts = new EnumMap<StatisticID, StatisticReadout>(StatisticID.class);
        pnlOtherStats.removeAll();

        Set<SingleAverageStatistic> statisticsWithHistories = new HashSet<SingleAverageStatistic>();
        
        for (Set<ParticleType> types: Formulas.getAllCombinations(
                new HashSet<ParticleType>(simulationInfo.getParticleTypes()))) {
            for (StatisticID currentStatisticID: simulationInfo.getStatistics(types)) {
                if (currentStatisticID.implementsInterface(SingleAverageStatistic.class)) {
                    SingleAverageStatistic statistic = (SingleAverageStatistic) controller.getStatistic(types, currentStatisticID);
                    statisticsWithHistories.add(statistic);
                    if (!statisticReadouts.containsKey(currentStatisticID)) {
                        StatisticReadoutSingle newReadout = new StatisticReadoutSingle(
                                currentStatisticID, statistic.getDisplayUnits());
                        statisticReadouts.put(currentStatisticID, newReadout);
                    }
                } else {
                    throw new RuntimeException("No readouts defined for statistics that are not " +
                            "single average statistics");
                }
            }
        }

        plotPanel.setStatisticsWithHistories(statisticsWithHistories);

        //Order the displayed statistics
        SortedSet<StatisticReadout> sortedReadouts = new TreeSet<StatisticReadout>(TO_STRING_COMPARATOR);
        sortedReadouts.addAll(statisticReadouts.values());
        for (StatisticReadout readout: sortedReadouts) {
            pnlOtherStats.add((javax.swing.JPanel) readout);
            ((javax.swing.JPanel) readout).setVisible(false);
        }
    }

    private void rescaleArena() {
        int newArenaHeight = pnlArenaHolder.getHeight();
        int newArenaWidth = pnlArenaHolder.getWidth();
        glPanel.rescaleArena(newArenaWidth, newArenaHeight);
    }
    
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
        StatisticID selectedStat = getSelectedStatistic();

        Set<ParticleType> selectedTypes = getSelectedTypes();

        plotPanel.updateHistories();

        if (selectedTypes != null) {
            plotPanel.setPlotInfo(controller.getStatistic(selectedTypes, selectedStat),
                    currentPlotType, chkBoxCumulative.isSelected());
            for(StatisticID statisticID: simulationInfo.getStatistics(selectedTypes)) {
                Statistic statistic = controller.getStatistic(selectedTypes, statisticID);
                StatisticReadout readoutForStatistic = statisticReadouts.get(statisticID);
                readoutForStatistic.update(statistic, chkBoxCumulative.isSelected(), chkBoxWidth.isSelected());
            }
        } else {
            plotPanel.clearPlot();
        }
        plotPanel.repaint();
        
        validate();
    }

    @SuppressWarnings({"unchecked"})
    private Set<ParticleType> getSelectedTypes() {
        Object selectedItem = cmbBoxTypes.getSelectedItem();
        Set<ParticleType> selectedTypes = null;
        if (selectedItem != null && !selectedItem.equals(CMB_BOX_NONE)) {
            selectedTypes = (Set<ParticleType>) cmbBoxTypes.getSelectedItem();
        }
        return selectedTypes;
    }

    private StatisticID getSelectedStatistic() {
        Object selectedItem = cmbBoxPlots.getSelectedItem();
        StatisticID selectedStat = null;
        if (selectedItem != null && !selectedItem.equals(CMB_BOX_NONE)) {
            selectedStat = (StatisticID) cmbBoxPlots.getSelectedItem();
        }
        return selectedStat;
    }

    private void setupPlotOptions() {
        StatisticID selectedPlot = getSelectedStatistic();
        
        if (selectedPlot != null) {
       
            if (selectedPlot.implementsInterface(StatisticWithDistribution.class)) {        
                controller.setCurrentPlot((StatisticWithDistribution) controller.getStatistic(getSelectedTypes(), selectedPlot));

		chkBoxCumulative.setEnabled(true);
                chkBoxWidth.setEnabled(true);
                chkBoxHistory.setEnabled(true);
            } else if (selectedPlot.implementsInterface(SingleAverageStatistic.class)) {
                if(selectedPlot == StatisticID.PRESSURE){
                    chkBoxCumulative.setEnabled(false);
                    chkBoxCumulative.setSelected(false);
                }
                else
                    chkBoxCumulative.setEnabled(true);
                chkBoxWidth.setEnabled(false);
                chkBoxWidth.setSelected(false);
                chkBoxHistory.setEnabled(false);
                chkBoxHistory.setSelected(true);
            } else {
		chkBoxCumulative.setEnabled(false);
		chkBoxCumulative.setSelected(false);
                chkBoxWidth.setEnabled(false);
                chkBoxWidth.setSelected(false);
                chkBoxHistory.setSelected(false);
                chkBoxHistory.setEnabled(false);
            }

            decidePlotType();
        }
    }

    private void decidePlotType() {
        StatisticID selectedPlotID = getSelectedStatistic();
        if (chkBoxHistory.isSelected()) {
            currentPlotType = PlotType.HISTORY;
        } else if (selectedPlotID != null) {
            if (selectedPlotID.implementsInterface(StatisticWithDistribution.class)) {
                currentPlotType = PlotType.HISTOGRAM;
            }            
        } else {
            currentPlotType = null;
        }
    }
    
    protected void setParticleColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        setParticleColorer();        
    }
    
    private void setParticleColorer() {
        switch (colorMode) {
            case NORMAL_COLORING:
                glPanel.setParticleColorer(null);
                break;
            case COLOR_BY_SPEED:
                glPanel.setParticleColorer(getSpeedColorer());
                break;
            case COLOR_BY_KE:
                glPanel.setParticleColorer(getKEColorer());
        }
    }
    
    private ParticleColorer getSpeedColorer() {
        return defaultSpeedColorer;
    }
	
    private static float getHue(double value, double limit) {
        double percent = ((limit - value) / (limit));
        if(percent < 0.0) {
            percent = 0.0;
        } else if(percent > 1.0) {
            percent = 1.0;
        }
        float hue = (float) (0.7 * percent);
        return hue;
    }
    
    private ParticleColorer getKEColorer() {
        return defaultKEColorer;
    }
    
    @Override
    public void setNextFrameToDisplay(FrameInfo frame) {
        currentFrame = frame;
        glPanel.setNextFrameToDisplay(frame);
    }
    
    @Override
    public void setPaused(boolean paused) {
        btnFrameStep.setEnabled(paused);
    }
    
    @Override
    public void showLicenses() {
        String boltzmannLicense = ResourceLoader.getBoltzmannLicense();
        String lwjglLicense = ResourceLoader.getLwjglLicense();

        JTextArea licenseText = new JTextArea(boltzmannLicense);
        licenseText.setMargin(new Insets(5,5,5,5));
	licenseText.setEditable(false);
	JScrollPane licensePane = new JScrollPane(licenseText);
        licensePane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, licensePane,
                "Boltzmann 3D License", JOptionPane.INFORMATION_MESSAGE, ResourceLoader.getBoltzmannIcon());
        JOptionPane.showMessageDialog(this, lwjglLicense,
                "LWJGL License", JOptionPane.INFORMATION_MESSAGE, ResourceLoader.getLwjglIcon());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFrameStep;
    private javax.swing.JButton btnRestart;
    private javax.swing.JCheckBox chkBoxCumulative;
    private javax.swing.JCheckBox chkBoxHistory;
    private javax.swing.JCheckBox chkBoxWidth;
    private javax.swing.JComboBox cmbBoxPlots;
    private javax.swing.JComboBox cmbBoxTypes;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JMenuItem mnuBtnChangePlotLimits;
    private javax.swing.JPopupMenu mnuPlotLimits;
    private javax.swing.JPanel pnlActivePlot;
    private javax.swing.JPanel pnlArenaHolder;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlOtherStats;
    private javax.swing.JPanel pnlPlotHeader;
    private javax.swing.JPanel pnlPlotHolder;
    private javax.swing.JPanel pnlPlotReadouts;
    private javax.swing.JPanel pnlPlotSelector;
    private javax.swing.JPanel pnlSeparator1;
    private javax.swing.JPanel pnlSeparator2;
    private javax.swing.JPanel pnlStatHeader;
    private javax.swing.JPanel pnlStatOptions;
    private javax.swing.JPanel pnlStats;
    // End of variables declaration//GEN-END:variables
}
