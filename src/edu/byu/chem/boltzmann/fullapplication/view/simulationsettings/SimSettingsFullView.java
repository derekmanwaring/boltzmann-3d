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
package edu.byu.chem.boltzmann.fullapplication.view.simulationsettings;

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.StatisticChkBox;
import edu.byu.chem.boltzmann.model.statistics.*;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedEvent;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedListener;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.ReactionRelationship;
import edu.byu.chem.boltzmann.utils.data.SelectedStatistics;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.Dimension;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsController;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsView;
import edu.byu.chem.boltzmann.view.simulationsettings.components.ActivationEnergyPanel;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypeDescriptor;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypeDescriptorImpl;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypesHeader;
import java.awt.Color;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

/**
 * View for simulation settings.
 * @author Derek Manwaring
 * Aug 2010
 */
public class SimSettingsFullView extends javax.swing.JPanel implements SimSettingsView {
    
    private ActivationEnergyPanel energyGraphPanel;

    private SelectedStatistics<TypeDescriptorImpl> selectedStats =
            new SelectedStatistics<TypeDescriptorImpl>();
    
    private Map<TypeDescriptorImpl, Double> wellDepths = new HashMap<TypeDescriptorImpl, Double>();

    private TypesHeader typesHeader = new TypesHeader();
    private List<TypeDescriptorImpl> typeDescriptors = new LinkedList<TypeDescriptorImpl>();

    private final Map<StatisticID, StatisticChkBox> statisticCheckBoxes = 
            new EnumMap<StatisticID, StatisticChkBox>(StatisticID.class);
    
    private SimSettingsController controller;

    /** Creates new form View */
    public SimSettingsFullView() {
        initComponents();
        
        pnlArenaSettings.addListener(new DimensionChangedListener(){
            @Override
            public void notify(DimensionChangedEvent newDimEvent){
               updateVelocityComponentsEnabled();
           }
        });
        
        setupStatisticCheckBoxes();
        
        double particle0To1Energy = Double.parseDouble(txtFldForwardEnergy.getText());
        double particle1To0Energy = Double.parseDouble(txtFldReverseEnergy.getText());
        energyGraphPanel = new ActivationEnergyPanel(particle0To1Energy, particle1To0Energy);
        pnlReactionGraphHolder.add(energyGraphPanel);

        pnlTypes.add(typesHeader);
        addTypeDescriptor();
        
        syncReactionControlsEnabled();
        
        syncParticleWellControlsEnabled();
        lastWellDepth = txtFldWellDepth.getText();
        
        lastForwardEnergy = txtFldForwardEnergy.getText();
        lastReverseEnergy = txtFldReverseEnergy.getText();
        
        btnReactedColor.setBackground(
                DefaultParticleInfo.getDefaultType(1).defaultColor);
    }

    @Override
    public void attachController(SimSettingsController controller) {
        this.controller = controller;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpDimension = new javax.swing.ButtonGroup();
        btnGrpStatistics = new javax.swing.ButtonGroup();
        btnGroupArenaType = new javax.swing.ButtonGroup();
        paneTabs = new javax.swing.JTabbedPane();
        pnlArenaSettings = new edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.ArenaSettingsTab();
        pnlParticles = new javax.swing.JPanel();
        pnlTypes = new javax.swing.JPanel();
        pnlAddTypes = new javax.swing.JPanel();
        btnAddType = new javax.swing.JButton();
        pnlSpace = new javax.swing.JPanel();
        pnlReactions = new javax.swing.JPanel();
        pnlReactionGraphHolder = new javax.swing.JPanel();
        chkBoxReactionMode = new javax.swing.JCheckBox();
        pnlActivationEnergy = new javax.swing.JPanel();
        lblForwardEnergy = new javax.swing.JLabel();
        lblReverseEnergy = new javax.swing.JLabel();
        txtFldReverseEnergy = new javax.swing.JTextField();
        txtFldForwardEnergy = new javax.swing.JTextField();
        chkBoxSuppressReaction = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        lblReactedColor = new javax.swing.JLabel();
        btnReactedColor = new javax.swing.JButton();
        pnlEnergyWells = new javax.swing.JPanel();
        chkBoxParticleInteractions = new javax.swing.JCheckBox();
        lblParticleWellWidth = new javax.swing.JLabel();
        txtFldRadiusMultiplier = new javax.swing.JTextField();
        chkBoxAttractiveWall = new javax.swing.JCheckBox();
        lblRadiusMultiplier = new javax.swing.JLabel();
        lblParticleWellDepth = new javax.swing.JLabel();
        cmbBoxDepthTypes = new javax.swing.JComboBox();
        txtFldWellDepth = new javax.swing.JTextField();
        lblReservoirTemperature = new javax.swing.JLabel();
        chkBoxHeatReservoir = new javax.swing.JCheckBox();
        txtFldReservoirTemperature = new javax.swing.JTextField();
        txtFldWallWellDepth = new javax.swing.JTextField();
        txtFldWallWellWidth = new javax.swing.JTextField();
        lblWallWellDepth = new javax.swing.JLabel();
        lblWallWellWidth = new javax.swing.JLabel();
        pnlStatistics = new javax.swing.JPanel();
        pnlRecordedStatistics = new javax.swing.JPanel();
        radBtnAllGroups = new javax.swing.JRadioButton();
        radBtnSpecificGroup = new javax.swing.JRadioButton();
        cmbBoxParticleGroups = new javax.swing.JComboBox();
        pnlStatisticChkBoxes = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        paneTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                paneTabsStateChanged(evt);
            }
        });
        paneTabs.addTab("Arena", pnlArenaSettings);

        pnlParticles.setLayout(new javax.swing.BoxLayout(pnlParticles, javax.swing.BoxLayout.Y_AXIS));

        pnlTypes.setLayout(new javax.swing.BoxLayout(pnlTypes, javax.swing.BoxLayout.Y_AXIS));
        pnlParticles.add(pnlTypes);

        pnlAddTypes.setPreferredSize(new java.awt.Dimension(100, 100));
        pnlAddTypes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAddType.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        btnAddType.setText("+");
        btnAddType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTypeActionPerformed(evt);
            }
        });
        pnlAddTypes.add(btnAddType);

        pnlParticles.add(pnlAddTypes);

        pnlSpace.setPreferredSize(new java.awt.Dimension(3200, 3200));
        pnlParticles.add(pnlSpace);

        paneTabs.addTab("Particles", pnlParticles);

        pnlReactionGraphHolder.setLayout(new javax.swing.BoxLayout(pnlReactionGraphHolder, javax.swing.BoxLayout.LINE_AXIS));

        chkBoxReactionMode.setText("Reaction Mode");
        chkBoxReactionMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxReactionModeActionPerformed(evt);
            }
        });

        pnlActivationEnergy.setBorder(javax.swing.BorderFactory.createTitledBorder("Activation Energy"));

        lblForwardEnergy.setText("Forward reation (kJ/mol):");

        lblReverseEnergy.setText("Reverse reaction (kJ/mol):");

        txtFldReverseEnergy.setText("4.0");
        txtFldReverseEnergy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFldReverseEnergyActionPerformed(evt);
            }
        });
        txtFldReverseEnergy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFldReverseEnergyFocusLost(evt);
            }
        });

        txtFldForwardEnergy.setText("8.0");
        txtFldForwardEnergy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFldForwardEnergyActionPerformed(evt);
            }
        });
        txtFldForwardEnergy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFldForwardEnergyFocusLost(evt);
            }
        });

        chkBoxSuppressReaction.setText("Supress Reverse Reaction");
        chkBoxSuppressReaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxSuppressReactionActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        lblReactedColor.setText("Reacted color:");
        jPanel2.add(lblReactedColor);

        btnReactedColor.setBackground(new java.awt.Color(255, 0, 0));
        btnReactedColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnReactedColor.setMaximumSize(new java.awt.Dimension(15, 30));
        btnReactedColor.setMinimumSize(new java.awt.Dimension(15, 30));
        btnReactedColor.setPreferredSize(new java.awt.Dimension(56, 20));
        btnReactedColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReactedColorActionPerformed(evt);
            }
        });
        jPanel2.add(btnReactedColor);

        org.jdesktop.layout.GroupLayout pnlActivationEnergyLayout = new org.jdesktop.layout.GroupLayout(pnlActivationEnergy);
        pnlActivationEnergy.setLayout(pnlActivationEnergyLayout);
        pnlActivationEnergyLayout.setHorizontalGroup(
            pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActivationEnergyLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblForwardEnergy)
                    .add(lblReverseEnergy))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtFldForwardEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtFldReverseEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkBoxSuppressReaction)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pnlActivationEnergyLayout.linkSize(new java.awt.Component[] {txtFldForwardEnergy, txtFldReverseEnergy}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlActivationEnergyLayout.setVerticalGroup(
            pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlActivationEnergyLayout.createSequentialGroup()
                .add(pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtFldForwardEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlActivationEnergyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblReverseEnergy)
                    .add(txtFldReverseEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(chkBoxSuppressReaction)))
            .add(pnlActivationEnergyLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(lblForwardEnergy))
        );

        org.jdesktop.layout.GroupLayout pnlReactionsLayout = new org.jdesktop.layout.GroupLayout(pnlReactions);
        pnlReactions.setLayout(pnlReactionsLayout);
        pnlReactionsLayout.setHorizontalGroup(
            pnlReactionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlReactionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlReactionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlReactionsLayout.createSequentialGroup()
                        .add(chkBoxReactionMode)
                        .add(44, 44, 44)
                        .add(pnlReactionGraphHolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlActivationEnergy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlReactionsLayout.setVerticalGroup(
            pnlReactionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlReactionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlReactionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkBoxReactionMode)
                    .add(pnlReactionGraphHolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 249, Short.MAX_VALUE)
                .add(pnlActivationEnergy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        paneTabs.addTab("Reactions", pnlReactions);

        chkBoxParticleInteractions.setText("Attractive particle interactions");
        chkBoxParticleInteractions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxParticleInteractionsActionPerformed(evt);
            }
        });

        lblParticleWellWidth.setText("Well width:");

        txtFldRadiusMultiplier.setText("3.0");

        chkBoxAttractiveWall.setText("Attractive wall");
        chkBoxAttractiveWall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxAttractiveWallActionPerformed(evt);
            }
        });

        lblRadiusMultiplier.setText("X sum of involved radii");

        lblParticleWellDepth.setText("Well depth (kJ/mol):");

        cmbBoxDepthTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Type 1", "Type 2" }));
        cmbBoxDepthTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxDepthTypesActionPerformed(evt);
            }
        });

        txtFldWellDepth.setText("1.0");
        txtFldWellDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFldWellDepthActionPerformed(evt);
            }
        });
        txtFldWellDepth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFldWellDepthFocusLost(evt);
            }
        });

        lblReservoirTemperature.setText("Initial temperature (K):");
        lblReservoirTemperature.setEnabled(false);

        chkBoxHeatReservoir.setText("Include heat reservoir");
        chkBoxHeatReservoir.setEnabled(false);
        chkBoxHeatReservoir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxHeatReservoirActionPerformed(evt);
            }
        });

        txtFldReservoirTemperature.setText("315");
        txtFldReservoirTemperature.setEnabled(false);

        txtFldWallWellDepth.setText("2.5");
        txtFldWallWellDepth.setEnabled(false);

        txtFldWallWellWidth.setText("10");
        txtFldWallWellWidth.setEnabled(false);

        lblWallWellDepth.setText("Well depth (kJ/mol):");
        lblWallWellDepth.setEnabled(false);

        lblWallWellWidth.setText("Well width (nm):");
        lblWallWellWidth.setEnabled(false);

        org.jdesktop.layout.GroupLayout pnlEnergyWellsLayout = new org.jdesktop.layout.GroupLayout(pnlEnergyWells);
        pnlEnergyWells.setLayout(pnlEnergyWellsLayout);
        pnlEnergyWellsLayout.setHorizontalGroup(
            pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEnergyWellsLayout.createSequentialGroup()
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlEnergyWellsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(chkBoxParticleInteractions))
                    .add(pnlEnergyWellsLayout.createSequentialGroup()
                        .add(46, 46, 46)
                        .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblParticleWellDepth)
                            .add(lblParticleWellWidth))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlEnergyWellsLayout.createSequentialGroup()
                                .add(txtFldWellDepth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cmbBoxDepthTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(pnlEnergyWellsLayout.createSequentialGroup()
                                .add(txtFldRadiusMultiplier, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblRadiusMultiplier))))
                    .add(pnlEnergyWellsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(chkBoxAttractiveWall)
                            .add(pnlEnergyWellsLayout.createSequentialGroup()
                                .add(29, 29, 29)
                                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(pnlEnergyWellsLayout.createSequentialGroup()
                                        .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(lblWallWellDepth)
                                            .add(lblWallWellWidth))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(txtFldWallWellWidth)
                                            .add(txtFldWallWellDepth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                    .add(pnlEnergyWellsLayout.createSequentialGroup()
                                        .add(29, 29, 29)
                                        .add(lblReservoirTemperature)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtFldReservoirTemperature, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(chkBoxHeatReservoir))))))
                .addContainerGap(151, Short.MAX_VALUE))
        );

        pnlEnergyWellsLayout.linkSize(new java.awt.Component[] {txtFldRadiusMultiplier, txtFldWellDepth}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        pnlEnergyWellsLayout.setVerticalGroup(
            pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEnergyWellsLayout.createSequentialGroup()
                .addContainerGap()
                .add(chkBoxParticleInteractions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblParticleWellWidth)
                    .add(txtFldRadiusMultiplier, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblRadiusMultiplier))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblParticleWellDepth)
                    .add(txtFldWellDepth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbBoxDepthTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(chkBoxAttractiveWall)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblWallWellWidth)
                    .add(txtFldWallWellWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblWallWellDepth)
                    .add(txtFldWallWellDepth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkBoxHeatReservoir)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEnergyWellsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblReservoirTemperature)
                    .add(txtFldReservoirTemperature, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(115, Short.MAX_VALUE))
        );

        paneTabs.addTab("Energy Wells", pnlEnergyWells);

        pnlRecordedStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder("Recorded Statistics"));

        btnGrpStatistics.add(radBtnAllGroups);
        radBtnAllGroups.setSelected(true);
        radBtnAllGroups.setText("All Groups");
        radBtnAllGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radBtnAllGroupsActionPerformed(evt);
            }
        });

        btnGrpStatistics.add(radBtnSpecificGroup);
        radBtnSpecificGroup.setText("Group:");
        radBtnSpecificGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radBtnSpecificGroupActionPerformed(evt);
            }
        });

        cmbBoxParticleGroups.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None" }));
        cmbBoxParticleGroups.setEnabled(false);
        cmbBoxParticleGroups.setMaximumSize(new java.awt.Dimension(200, 32767));
        cmbBoxParticleGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBoxParticleGroupsActionPerformed(evt);
            }
        });

        pnlStatisticChkBoxes.setMaximumSize(new java.awt.Dimension(10000, 10000));
        pnlStatisticChkBoxes.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlStatisticChkBoxes.setLayout(new javax.swing.BoxLayout(pnlStatisticChkBoxes, javax.swing.BoxLayout.PAGE_AXIS));

        org.jdesktop.layout.GroupLayout pnlRecordedStatisticsLayout = new org.jdesktop.layout.GroupLayout(pnlRecordedStatistics);
        pnlRecordedStatistics.setLayout(pnlRecordedStatisticsLayout);
        pnlRecordedStatisticsLayout.setHorizontalGroup(
            pnlRecordedStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRecordedStatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRecordedStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlRecordedStatisticsLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(pnlStatisticChkBoxes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 247, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(pnlRecordedStatisticsLayout.createSequentialGroup()
                        .add(radBtnAllGroups)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(radBtnSpecificGroup)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbBoxParticleGroups, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 282, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        pnlRecordedStatisticsLayout.setVerticalGroup(
            pnlRecordedStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRecordedStatisticsLayout.createSequentialGroup()
                .add(pnlRecordedStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(radBtnAllGroups)
                    .add(radBtnSpecificGroup)
                    .add(cmbBoxParticleGroups, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStatisticChkBoxes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(297, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlStatisticsLayout = new org.jdesktop.layout.GroupLayout(pnlStatistics);
        pnlStatistics.setLayout(pnlStatisticsLayout);
        pnlStatisticsLayout.setHorizontalGroup(
            pnlStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRecordedStatistics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlStatisticsLayout.setVerticalGroup(
            pnlStatisticsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRecordedStatistics, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        paneTabs.addTab("Statistics", pnlStatistics);

        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, paneTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnClose)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btnApply)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(paneTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnClose)
                    .add(btnApply))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTypeActionPerformed
        addTypeDescriptor();
}//GEN-LAST:event_btnAddTypeActionPerformed

    private void chkBoxReactionModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxReactionModeActionPerformed
        if (typeDescriptors.size() != 1) {
            tellUserOneTypeOnly();
            chkBoxReactionMode.setSelected(false);
        }
        syncReactionControlsEnabled();
        updateEnergyGraph();
}//GEN-LAST:event_chkBoxReactionModeActionPerformed

    private void syncReactionControlsEnabled() {
        boolean enabled = chkBoxReactionMode.isSelected();
        
        lblReactedColor.setEnabled(enabled);
        btnReactedColor.setEnabled(enabled);
        
        lblForwardEnergy.setEnabled(enabled);
        txtFldForwardEnergy.setEnabled(enabled);
        lblReverseEnergy.setEnabled(enabled);
        txtFldReverseEnergy.setEnabled(enabled);
        
        chkBoxSuppressReaction.setEnabled(enabled);
    }
    
    private void tellUserOneTypeOnly() {
        JOptionPane.showMessageDialog(
                this, 
                "Reaction mode can only be used when there is one particle type.", 
                "Reaction Mode", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void tellUserOneTypeNeeded() {
        JOptionPane.showMessageDialog(
                this, 
                "Reaction mode requires that there be a single particle type.", 
                "Reaction Mode", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void tellUserAtLeastOneTypeNeeded() {
        JOptionPane.showMessageDialog(
                this, 
                "There must be at least one particle type.", 
                "Need One or More Particle Types", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private String lastForwardEnergy = "";
    private String lastReverseEnergy = "";
    
    private void forwardEnergyChanged() {
        try {            
            String forwardEnergy = txtFldForwardEnergy.getText();
            double newForwardEnergy = Double.parseDouble(forwardEnergy);
            lastForwardEnergy = forwardEnergy;
            
        } catch (NumberFormatException ex) {
            txtFldForwardEnergy.setText(lastForwardEnergy);
        }        
    }
    
    private void reverseEnergyChanged() {
        try {            
            String reverseEnergy = txtFldReverseEnergy.getText();
            double newReverseEnergy = Double.parseDouble(reverseEnergy);
            lastReverseEnergy = reverseEnergy;
            
        } catch (NumberFormatException ex) {
            txtFldReverseEnergy.setText(lastReverseEnergy);
        }    
    }
    
    private void txtFldReverseEnergyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFldReverseEnergyActionPerformed
        reverseEnergyChanged();
        updateEnergyGraph();
}//GEN-LAST:event_txtFldReverseEnergyActionPerformed

    private void txtFldReverseEnergyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFldReverseEnergyFocusLost
        reverseEnergyChanged();
        updateEnergyGraph();
}//GEN-LAST:event_txtFldReverseEnergyFocusLost

    private void txtFldForwardEnergyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFldForwardEnergyActionPerformed
        forwardEnergyChanged();
        updateEnergyGraph();
}//GEN-LAST:event_txtFldForwardEnergyActionPerformed

    private void txtFldForwardEnergyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFldForwardEnergyFocusLost
        forwardEnergyChanged();
        updateEnergyGraph();
}//GEN-LAST:event_txtFldForwardEnergyFocusLost
    
    private void chkBoxSuppressReactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxSuppressReactionActionPerformed

}//GEN-LAST:event_chkBoxSuppressReactionActionPerformed

    private void chkBoxParticleInteractionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxParticleInteractionsActionPerformed
        syncParticleWellControlsEnabled();
}//GEN-LAST:event_chkBoxParticleInteractionsActionPerformed

    private void syncParticleWellControlsEnabled() {
        boolean enabled = chkBoxParticleInteractions.isSelected();
        
        lblParticleWellWidth.setEnabled(enabled);
        txtFldRadiusMultiplier.setEnabled(enabled);
        lblRadiusMultiplier.setEnabled(enabled);
        
        lblParticleWellDepth.setEnabled(enabled);
        txtFldWellDepth.setEnabled(enabled);
        cmbBoxDepthTypes.setEnabled(enabled);
    }
    
    private void chkBoxAttractiveWallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxAttractiveWallActionPerformed
        syncWallWellControlsEnabled();
}//GEN-LAST:event_chkBoxAttractiveWallActionPerformed

    private void syncWallWellControlsEnabled() {
        boolean enabled = chkBoxAttractiveWall.isSelected();
        
        lblWallWellWidth.setEnabled(enabled);
        txtFldWallWellWidth.setEnabled(enabled);
        
        lblWallWellDepth.setEnabled(enabled);
        txtFldWallWellDepth.setEnabled(enabled);
        
        chkBoxHeatReservoir.setEnabled(enabled);
        
        syncReservoirControlsEnabled();
    }
    
    private void radBtnAllGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radBtnAllGroupsActionPerformed
        setCheckBoxes();
        cmbBoxParticleGroups.setEnabled(false);
}//GEN-LAST:event_radBtnAllGroupsActionPerformed

    private void radBtnSpecificGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radBtnSpecificGroupActionPerformed
        setCheckBoxes();
        cmbBoxParticleGroups.setEnabled(true);
}//GEN-LAST:event_radBtnSpecificGroupActionPerformed

    private void cmbBoxParticleGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxParticleGroupsActionPerformed
        setCheckBoxes();
}//GEN-LAST:event_cmbBoxParticleGroupsActionPerformed

    private void paneTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_paneTabsStateChanged
        if (paneTabs.getSelectedComponent().equals(pnlStatistics)) {
            setupStatsPanel();
        } else if (paneTabs.getSelectedComponent().equals(pnlEnergyWells)) {
            setupWellsPanel();
        } else if (paneTabs.getSelectedComponent().equals(pnlReactions)) {
            if (chkBoxReactionMode.isSelected()) {
                updateEnergyGraph();
            }
        }
}//GEN-LAST:event_paneTabsStateChanged

    @SuppressWarnings("unchecked")
    private void setupStatsPanel() {
        cmbBoxParticleGroups.removeAllItems();

        SortedSet<Set<TypeDescriptorImpl>> typesForBox = new TreeSet<Set<TypeDescriptorImpl>>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        for (Set<TypeDescriptorImpl> types: Formulas.getAllCombinations(
                new HashSet<TypeDescriptorImpl>(typeDescriptors))) {

            Iterator<TypeDescriptorImpl> iterator = types.iterator();
            String boxNameTemp = iterator.next().toString();
            while (iterator.hasNext()) {
                boxNameTemp += " & " + iterator.next().toString();
            }
            final String boxName = boxNameTemp;

            Set<TypeDescriptorImpl> setForBox = new HashSet<TypeDescriptorImpl>(types) {
                @Override
                public String toString() {
                    return boxName;
                }
            };

            typesForBox.add(setForBox);
        }

        for (Set<TypeDescriptorImpl> types: typesForBox) {
            cmbBoxParticleGroups.addItem(types);
        }    
    }
    
    @SuppressWarnings({"unchecked"})
    private void setupWellsPanel() {
        cmbBoxDepthTypes.removeAllItems();

        for (TypeDescriptorImpl type: typeDescriptors) {
            cmbBoxDepthTypes.addItem(type);
        }  
        
        boolean canHaveAttractiveWall = 
                !(pnlArenaSettings.getArenaType() == ArenaType.PERIODIC_BOUNDARIES);
        chkBoxAttractiveWall.setEnabled(canHaveAttractiveWall);
        
        if (!canHaveAttractiveWall) {
            chkBoxAttractiveWall.setSelected(false);
        }
        
        syncWallWellControlsEnabled();
    }
    
    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        double maxParticleRadius = getMaximumAllowedRadius();
        for(int i = 0; i < typeDescriptors.size(); i++)
            typeDescriptors.get(i).updateMaxRadius(maxParticleRadius);
        
        //Setup a type that represents reacted particles if in reaction mode
        if (chkBoxReactionMode.isSelected()) {
            ParticleType reactingType = typeDescriptors.get(0).getTypeDescribed();
            TypeDescriptorImpl reactingDescriptor = addTypeDescriptor(new ParticleType(
                    reactingType.particleMass,
                    reactingType.particleRadius,
                    btnReactedColor.getBackground(),
                    "Reacted " + reactingType.displayName                    
                    ), 0);
            controller.applySimulationSettings();

            typeDescriptors.remove(reactingDescriptor);
            pnlTypes.remove(reactingDescriptor);
            validate();
            
        } else {        
            controller.applySimulationSettings();
        }
        
        controller.hideView();
}//GEN-LAST:event_btnApplyActionPerformed
    
    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        controller.hideView();
}//GEN-LAST:event_btnCloseActionPerformed

    private void txtFldWellDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFldWellDepthActionPerformed
        handleWellDepthChange();
    }//GEN-LAST:event_txtFldWellDepthActionPerformed

    private void txtFldWellDepthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFldWellDepthFocusLost
        handleWellDepthChange();
    }//GEN-LAST:event_txtFldWellDepthFocusLost

    private void cmbBoxDepthTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBoxDepthTypesActionPerformed
        TypeDescriptorImpl type = (TypeDescriptorImpl) cmbBoxDepthTypes.getSelectedItem();
        double depth = getWellDepth(type);
        txtFldWellDepth.setText(String.valueOf(depth));
    }//GEN-LAST:event_cmbBoxDepthTypesActionPerformed
        
    private void btnReactedColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReactedColorActionPerformed
        Color color = JColorChooser.showDialog(
                this, "Reacted particle color", btnReactedColor.getBackground());
        if (color != null) {
            btnReactedColor.setBackground(color);
        }
        updateEnergyGraph();
}//GEN-LAST:event_btnReactedColorActionPerformed

    private void chkBoxHeatReservoirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxHeatReservoirActionPerformed
        syncReservoirControlsEnabled();
    }//GEN-LAST:event_chkBoxHeatReservoirActionPerformed

    private void syncReservoirControlsEnabled() {
        boolean enabled = chkBoxHeatReservoir.isEnabled() && chkBoxHeatReservoir.isSelected();
        
        lblReservoirTemperature.setEnabled(enabled);
        txtFldReservoirTemperature.setEnabled(enabled);
    }
    
    private String lastWellDepth = "";
    private static final double DEFAULT_WELL_DEPTH = 1.0;
    
    private void handleWellDepthChange() {
        try {            
            String wellDepth = txtFldWellDepth.getText();
            double newWellDepth = Double.parseDouble(wellDepth);
            TypeDescriptorImpl currentType = 
                    (TypeDescriptorImpl) cmbBoxDepthTypes.getSelectedItem();
            wellDepths.put(currentType, newWellDepth);
            lastWellDepth = wellDepth;
            
        } catch (NumberFormatException ex) {
            txtFldWellDepth.setText(lastWellDepth);
        }
    }
    
    public double getWellDepth(TypeDescriptor typeRequested) {
        TypeDescriptorImpl type = (TypeDescriptorImpl) typeRequested;
        Double depth = wellDepths.get(type);
        if (depth != null) {
            return depth;
        } else if (chkBoxReactionMode.isSelected()) {                      
            Double reactingTypeDepth = wellDepths.get(typeDescriptors.get(0));
            if (reactingTypeDepth != null) {
                return reactingTypeDepth;
            } else {
                return DEFAULT_WELL_DEPTH;
            }
        }else {            
            return DEFAULT_WELL_DEPTH;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddType;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnClose;
    private javax.swing.ButtonGroup btnGroupArenaType;
    private javax.swing.ButtonGroup btnGrpDimension;
    private javax.swing.ButtonGroup btnGrpStatistics;
    private javax.swing.JButton btnReactedColor;
    private javax.swing.JCheckBox chkBoxAttractiveWall;
    private javax.swing.JCheckBox chkBoxHeatReservoir;
    private javax.swing.JCheckBox chkBoxParticleInteractions;
    private javax.swing.JCheckBox chkBoxReactionMode;
    private javax.swing.JCheckBox chkBoxSuppressReaction;
    private javax.swing.JComboBox cmbBoxDepthTypes;
    private javax.swing.JComboBox cmbBoxParticleGroups;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblForwardEnergy;
    private javax.swing.JLabel lblParticleWellDepth;
    private javax.swing.JLabel lblParticleWellWidth;
    private javax.swing.JLabel lblRadiusMultiplier;
    private javax.swing.JLabel lblReactedColor;
    private javax.swing.JLabel lblReservoirTemperature;
    private javax.swing.JLabel lblReverseEnergy;
    private javax.swing.JLabel lblWallWellDepth;
    private javax.swing.JLabel lblWallWellWidth;
    private javax.swing.JTabbedPane paneTabs;
    private javax.swing.JPanel pnlActivationEnergy;
    private javax.swing.JPanel pnlAddTypes;
    private edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.ArenaSettingsTab pnlArenaSettings;
    private javax.swing.JPanel pnlEnergyWells;
    private javax.swing.JPanel pnlParticles;
    private javax.swing.JPanel pnlReactionGraphHolder;
    private javax.swing.JPanel pnlReactions;
    private javax.swing.JPanel pnlRecordedStatistics;
    private javax.swing.JPanel pnlSpace;
    private javax.swing.JPanel pnlStatisticChkBoxes;
    private javax.swing.JPanel pnlStatistics;
    private javax.swing.JPanel pnlTypes;
    private javax.swing.JRadioButton radBtnAllGroups;
    private javax.swing.JRadioButton radBtnSpecificGroup;
    private javax.swing.JTextField txtFldForwardEnergy;
    private javax.swing.JTextField txtFldRadiusMultiplier;
    private javax.swing.JTextField txtFldReservoirTemperature;
    private javax.swing.JTextField txtFldReverseEnergy;
    private javax.swing.JTextField txtFldWallWellDepth;
    private javax.swing.JTextField txtFldWallWellWidth;
    private javax.swing.JTextField txtFldWellDepth;
    // End of variables declaration//GEN-END:variables

    @SuppressWarnings("unchecked")
    private void setCheckBoxes() {
        Set<TypeDescriptorImpl> descriptorsSet = new HashSet<TypeDescriptorImpl>(typeDescriptors);
        
        if (radBtnAllGroups.isSelected()) {
            
            for (StatisticID statisticID : statisticCheckBoxes.keySet()) {
                statisticCheckBoxes.get(statisticID).setSelected(
                        selectedStats.isSelectedForAll(descriptorsSet, statisticID));
            }
                        
        } else {
            Set<TypeDescriptorImpl> group = (Set<TypeDescriptorImpl>) cmbBoxParticleGroups.getSelectedItem();

            if (group != null) {
            
                for (StatisticID statisticID : statisticCheckBoxes.keySet()) {
                    statisticCheckBoxes.get(statisticID).setSelected(
                            selectedStats.isSelected(group, statisticID));
                }
                
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setStatistic(StatisticID statisticID, boolean selected) {
        if(radBtnAllGroups.isSelected()) {
            selectedStats.setSelectedForAll(new HashSet<TypeDescriptorImpl>(typeDescriptors), statisticID, selected);
        } else {
            Set<TypeDescriptorImpl> selectedTypes = (Set<TypeDescriptorImpl>) cmbBoxParticleGroups.getSelectedItem();
            selectedStats.setSelected(selectedTypes, statisticID, selected);
        }
    }

    private void addTypeDescriptor() {
        if(chkBoxReactionMode.isSelected())
            tellUserOneTypeNeeded();
        else{
            int nextTypeNumber = typeDescriptors.size();
            
            ParticleType nextDefaultType = DefaultParticleInfo.getDefaultType(nextTypeNumber);
   
            TypeDescriptorImpl newDescriptor = new TypeDescriptorImpl(
                typesHeader,
                nextDefaultType,
                DefaultParticleInfo.getDefaultNumParticles(nextTypeNumber),
                this);
            addDescriptorToFrame(newDescriptor);
        
            addCurrentStatisticsForType();
        }
    }
    
    private void addCurrentStatisticsForType() {
        if (radBtnAllGroups.isSelected()) {
            
            for (StatisticID statisticID : statisticCheckBoxes.keySet()) {
                setStatistic(statisticID, statisticCheckBoxes.get(statisticID).isSelected());
            }
            
        }
    }

    /**
     * Creates a new type descriptor for the given type and adds it to the particle settings
     * panel. The TypeDescriptor created is returned.
     */
    private TypeDescriptorImpl addTypeDescriptor(ParticleType type, int numParticles) {
        TypeDescriptorImpl newDescriptor = new TypeDescriptorImpl(typesHeader, type, numParticles, this);
        addDescriptorToFrame(newDescriptor);
        addCurrentStatisticsForType();
        
        return newDescriptor;
    }

    private void addDescriptorToFrame(TypeDescriptorImpl descriptor) {
        typeDescriptors.add(descriptor);
        pnlTypes.add(descriptor);
        validate();
    }

    public void removeTypeDescriptor(TypeDescriptorImpl descriptor) {
        if(chkBoxReactionMode.isSelected())
            tellUserOneTypeNeeded();
        else if(typeDescriptors.size() == 1)
            tellUserAtLeastOneTypeNeeded();
        else{
            typeDescriptors.remove(descriptor);
            pnlTypes.remove(descriptor);
            validate();
        }
    }

    private void removeAllTypeDesc() {
        for (TypeDescriptorImpl descriptor: typeDescriptors) {
            pnlTypes.remove(descriptor);
        }

        typeDescriptors.clear();
        validate();
    }

    @SuppressWarnings({"unchecked"})
    public List<TypeDescriptor> getTypeDescriptors() {
        return (List<TypeDescriptor>) 
               (List<? extends TypeDescriptor>) 
               typeDescriptors;
    }

    private void updateEnergyGraph() {
        double forwardEnergy = Double.parseDouble(txtFldForwardEnergy.getText());
        double reverseEnergy = Double.parseDouble(txtFldReverseEnergy.getText());

        energyGraphPanel.setActivationEnergies(forwardEnergy, reverseEnergy);
        
        ParticleType reactingType = typeDescriptors.get(0).getTypeDescribed();
        
        String reactingTypeName = reactingType.displayName;
        energyGraphPanel.setParticleNames(reactingTypeName,
                "Reacted " + reactingTypeName);
        
        energyGraphPanel.setParticleColors(
                reactingType.defaultColor, 
                btnReactedColor.getBackground());
    }

    /**
     * Makes the settings of this form match the given SimulationInfo.
     */
    @Override
    public void setSimulationInfo(SimulationInfo simInfo) {
        setArenaSettings(simInfo);
        Map<ParticleType, TypeDescriptorImpl> newDescriptors = setParticleSettings(simInfo);
        setReactionSettings(simInfo);
        setWellSettings(simInfo);
        setStatisticSettings(simInfo, newDescriptors);
        setupStatsPanel();
    }

    /**
     * Makes the settings for the arena tab match those of simInfo
     */
    private void setArenaSettings(SimulationInfo simInfo) {
        setDimension(simInfo.dimension);

        double xSize = simInfo.arenaXSize;
        double ySize = simInfo.arenaYSize;
        double zSize = simInfo.arenaZSize;
        setArenaSize(xSize, ySize, zSize, simInfo.dimension);

        setInitialTemperature(simInfo.initialTemperature);
        setArenaType(simInfo);
    }

    /**
     * Sets the radio buttons to select the given dimension
     */
    private void setDimension(int dimension) {
        pnlArenaSettings.setDimension(SimulationInfo.intToDimension(dimension));
    }

    /**
     * Sets the arena size text fields to match the given sizes
     */
    private void setArenaSize(double xSize, double ySize, double zSize, int dimension) {
        pnlArenaSettings.setArenaSize(xSize, ySize, zSize, SimulationInfo.intToDimension(dimension),
                Units.Length.METER);
    }

    /**
     * Sets the temperature spinner to match the given temperature
     */
    private void setInitialTemperature(double temperature) {
        pnlArenaSettings.setTemperature(temperature, Units.Temperature.KELVIN);
    }

    /**
     * Makes the arena type settings match those of simInfo
     */
    private void setArenaType(SimulationInfo simInfo) {
        pnlArenaSettings.setArenaType(simInfo.arenaType);
        if (simInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
            pnlArenaSettings.setDividedArenaHoleDiameter(simInfo.holeDiameter, 
                    Units.Length.METER);
            pnlArenaSettings.setMaxwellDemonModeSelected(simInfo.maxwellDemonMode);
        } else {
            pnlArenaSettings.setMaxwellDemonModeSelected(false);
        }
    }

    /**
     * Makes the settings for the particles tab match those of simInfo. A map is returned
     * that maps the particle types from simInfo to the descriptors that were created for them.
     */
    private Map<ParticleType, TypeDescriptorImpl> setParticleSettings(SimulationInfo simInfo) {
        List<ParticleType> allTypes = simInfo.getParticleTypes();
        
        if (simInfo.reactionMode) {
            allTypes.remove(1); //The second type is just the first one after reaction
        }

        Map<ParticleType, TypeDescriptorImpl> typeMap = new HashMap<ParticleType, TypeDescriptorImpl>(allTypes.size());
        removeAllTypeDesc();

        for (ParticleType type: simInfo.getParticleTypes()) {
            TypeDescriptorImpl newDescriptor = addTypeDescriptor(type, simInfo.getNumberOfParticles(type));
            typeMap.put(type, newDescriptor);
        }

        return typeMap;
    }

    /**
     * Makes the settings for the reactions tab match those of simInfo
     */
    private void setReactionSettings(SimulationInfo simInfo) {
        
        if (simInfo.reactionMode) {
            Set<ParticleType> reactingParticles = new HashSet<ParticleType>(simInfo.getParticleTypes());

            if (reactingParticles.size() == 2) {
                chkBoxReactionMode.setSelected(true);
                ReactionRelationship reaction = simInfo.getReactionRelationship(reactingParticles);
                chkBoxSuppressReaction.setSelected(reaction.suppressReverseReaction);
                txtFldForwardEnergy.setText(
                        ErrorHandler.format(reaction.forwardActivationEnergy, 4));
                txtFldReverseEnergy.setText(
                        ErrorHandler.format(reaction.reverseActivationEnergy, 4));

            } else {
                chkBoxReactionMode.setSelected(false);
            }
        } else {
            chkBoxReactionMode.setSelected(false);
        }
        
        syncReactionControlsEnabled();
        updateEnergyGraph();
    }

    /**
     * Makes the settings for the energy wells tab match those of simInfo
     */
    private void setWellSettings(SimulationInfo simInfo) {
        //TODO make this work - Derek Manwaring 4 June 2011
    }

    /**
     * Makes the settings for the statistics tab match those of simInfo.
     * @param descriptors Maps types in simInfo to TypeDescriptors created previously for those types.
     */
    private void setStatisticSettings(SimulationInfo simInfo, Map<ParticleType, TypeDescriptorImpl> descriptors) {
        SelectedStatistics<ParticleType> newSelectedStats = new SelectedStatistics<ParticleType>();

        for (Set<ParticleType> types: Formulas.getAllCombinations(
                new HashSet<ParticleType>(simInfo.getParticleTypes()))) {
            for (StatisticID selectedStat: simInfo.getStatistics(types)) {
                newSelectedStats.setSelected(types, selectedStat, true);
            }
        }

        selectedStats = SelectedStatistics.mapSelection(newSelectedStats, descriptors);
    }
    
    private void updateVelocityComponentsEnabled(){
        int dimension = SimulationInfo.dimensionToInt(pnlArenaSettings.getDimension());
        StatisticChkBox yVelChkBox = statisticCheckBoxes.get(StatisticID.Y_VELOCITY);
        StatisticChkBox zVelChkBox = statisticCheckBoxes.get(StatisticID.Z_VELOCITY);
        switch(dimension){
            case 1:
                if(yVelChkBox.isSelected())
                    yVelChkBox.doClick();
                yVelChkBox.setEnabled(false);
                if(zVelChkBox.isSelected())
                    zVelChkBox.doClick();
                zVelChkBox.setEnabled(false);
                break;
            case 2:
                yVelChkBox.setEnabled(true);
                if(zVelChkBox.isSelected())
                    zVelChkBox.doClick();
                zVelChkBox.setEnabled(false);
                break;
            default:
                yVelChkBox.setEnabled(true);
                zVelChkBox.setEnabled(true);
        }
    }

    public ArenaType getArenaType() {
        return pnlArenaSettings.getArenaType();
    }

    public int getDimension() {
        return SimulationInfo.dimensionToInt(pnlArenaSettings.getDimension());
    }

    public double getArenaXSize() {
        return pnlArenaSettings.getArenaXSize(Units.Length.METER);
    }

    public double getArenaYSize() {
        return pnlArenaSettings.getArenaYSize(Units.Length.METER);
    }

    public double getArenaZSize() {
        return pnlArenaSettings.getArenaZSize(Units.Length.METER);
    }

    @Override
    public double getHoleDiameter() {
        return pnlArenaSettings.getDividedArenaHoleDiameter(Units.Length.METER);
    }

    @Override
    public boolean isMaxwellDemonModeSelected() {
        return pnlArenaSettings.isMaxwellDemonModeSelected();
    }

    @Override
    public boolean isReactionModeSelected() {
        return chkBoxReactionMode.isSelected();
    }

    public boolean areAttractiveInteractionsAllowed() {
        return chkBoxParticleInteractions.isSelected();
    }

    public double getRadiusOfInteractionMultiplier() {
        return Double.parseDouble(txtFldRadiusMultiplier.getText());
    }

    public boolean includeAttractiveWall() {
        return chkBoxAttractiveWall.isSelected();
    }

    public WallEnum getAttractiveWall() {
        return WallEnum.BOTTOM;
    }

    public double getWallWellWidth() {
        double widthNanometers = Double.parseDouble(txtFldWallWellWidth.getText());
        return Units.convert("nm", "m", widthNanometers);
    }

    public double getWallWellDepth() {
        return Double.parseDouble(txtFldWallWellDepth.getText());
    }

    public boolean includeHeatReservoir() {
        return chkBoxHeatReservoir.isSelected();
    }

    public double getInitialReservoirTemperature() {
        return Double.parseDouble(txtFldReservoirTemperature.getText());
    }

    public double getInitialTemperature() {
        return pnlArenaSettings.getTemperature(Units.Temperature.KELVIN);
    }

    @SuppressWarnings({"unchecked"})
    public SelectedStatistics<TypeDescriptor> getSelectedStats() {
        return (SelectedStatistics<TypeDescriptor>) 
                (SelectedStatistics<? extends TypeDescriptor>) 
                selectedStats;
    }

    public double getForwardEnergy() {
        return Double.parseDouble(txtFldForwardEnergy.getText());
    }

    public double getReverseEnergy() {
        return Double.parseDouble(txtFldReverseEnergy.getText());
    }

    public boolean shouldSuppressRevReaction() {
        return chkBoxSuppressReaction.isSelected();
    }

    public SimSettingsController getController() {
        return controller;
    }
    
    private final StatisticChkBox.StatisticSelectionListener statisticSelectionListener = 
            new StatisticChkBox.StatisticSelectionListener() {

        @Override
        public void setStatistic(StatisticID statisticID, boolean selected) {
            SimSettingsFullView.this.setStatistic(statisticID, selected);
        }
    };

    private void setupStatisticCheckBoxes() {
        for (StatisticID statisticID : StatisticID.values()) {
            StatisticChkBox chkBoxForStat = new StatisticChkBox(statisticID, statisticSelectionListener);
            if(statisticID == StatisticID.Z_VELOCITY)
                chkBoxForStat.setEnabled(false);
            statisticCheckBoxes.put(statisticID, chkBoxForStat);
            pnlStatisticChkBoxes.add(chkBoxForStat);
        }
    }
    
    /*
     * the maximum allowed particle radius (in 0.1 nm increments) that wouldn't force
     * the SimSettingsController to reduce the number of that particle to zero for taking
     * up too much arena volume, which could set the total number of particles to zero
     * and thus set the temperature setting to zero, which would be true but annoying
     */
    private double getMaximumAllowedRadius() {
        double availableVolume = getAvailableVolume();
        double maxRadius;
        double arenaXSize = getArenaXSize();
        /*
         * derived with Mathematica to satisfy getTotalNeededVolume() = getAvailableVolume()
         * for one particle in the SimSettingsController
         */
        switch(getDimension()){
            case 1:
                maxRadius = availableVolume / 6;
                break;
            case 2:
                maxRadius = 0.5 * (Math.sqrt(Math.pow(arenaXSize, 2) + availableVolume) - arenaXSize);
                break;
            case 3:
                double freeVolume = arenaXSize * getArenaYSize();
                double magicNumber = 9 * availableVolume +
                     Math.sqrt(96 * Math.pow(freeVolume, 3) + 81 * Math.pow(availableVolume, 2));
                maxRadius = Math.cbrt(magicNumber / 144.) - Math.cbrt(2. / (3. * magicNumber)) * freeVolume;
                break;
            default:
                throw new RuntimeException("Invalid dimension. Who programmed this?");
        }
        return Math.floor(Units.convert("m", "nm", maxRadius) * 10) / 10;
    }
    
    //stolen from SimSettingsController
    private double getAvailableVolume() {
        double arenaX = getArenaXSize();
        ArenaType arenaType = getArenaType();
        if(arenaType == ArenaType.DIVIDED_ARENA || arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE)
            arenaX = arenaX / 2 - SimulationInfo.ARENA_DIVIDER_RADIUS;
        switch (getDimension()) {
            case 1:
                return arenaX;
            case 2:
                return arenaX * getArenaYSize();
            case 3:
                return arenaX * getArenaYSize() * getArenaZSize();
            default:
                return 0.0;
        }
    }
}
