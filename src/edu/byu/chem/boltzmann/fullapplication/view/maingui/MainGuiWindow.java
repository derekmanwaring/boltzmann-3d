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

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.fullapplication.Main;
import edu.byu.chem.boltzmann.model.io.Load;
import edu.byu.chem.boltzmann.resources.ResourceLoader;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import edu.byu.chem.boltzmann.view.maingui.MainGuiView;
import edu.byu.chem.boltzmann.view.maingui.components.GLPanel.ColorMode;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Derek Manwaring
 */
public class MainGuiWindow extends javax.swing.JFrame {


    private final MainGuiFullView view;

    /** Creates new form Window */
    public MainGuiWindow() {
        super("Boltzmann 3D");
        view = new MainGuiFullView();
        this.add(view);

        setLocation(100, 50);
        initComponents();

        setIconImage(ResourceLoader.getBoltzmannImage());

        validate();
    }

    public MainGuiView getView() {
        return view;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrpColoring = new javax.swing.ButtonGroup();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuItmReloadDefaultSettings = new javax.swing.JMenuItem();
        mnuItmLoad = new javax.swing.JMenuItem();
        mnuItmSave = new javax.swing.JMenuItem();
        mnuItmExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuRadNormalColor = new javax.swing.JRadioButtonMenuItem();
        mnuRadSpeedColor = new javax.swing.JRadioButtonMenuItem();
        mnuRadKEColor = new javax.swing.JRadioButtonMenuItem();
        mnuBtnPistonControls = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnuBtnSimSettings = new javax.swing.JMenuItem();
        mnuBtnStatSettings = new javax.swing.JMenuItem();
        mnuBtnResTemp = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuItmBoltzmannWebpage = new javax.swing.JMenuItem();
        mnuItmLwjgl = new javax.swing.JMenuItem();
        mnuItmLicenses = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(227, 227, 227));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jMenu1.setText("File");

        mnuItmReloadDefaultSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mnuItmReloadDefaultSettings.setText("Reload default settings");
        mnuItmReloadDefaultSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmReloadDefaultSettingsActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItmReloadDefaultSettings);

        mnuItmLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuItmLoad.setText("Load");
        mnuItmLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmLoadActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItmLoad);

        mnuItmSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuItmSave.setText("Save");
        mnuItmSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmSaveActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItmSave);

        mnuItmExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuItmExit.setText("Exit");
        mnuItmExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuItmExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");

        btnGrpColoring.add(mnuRadNormalColor);
        mnuRadNormalColor.setSelected(true);
        mnuRadNormalColor.setText("Normal Coloring");
        mnuRadNormalColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRadNormalColorActionPerformed(evt);
            }
        });
        jMenu2.add(mnuRadNormalColor);

        btnGrpColoring.add(mnuRadSpeedColor);
        mnuRadSpeedColor.setText("Color by Speed");
        mnuRadSpeedColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRadSpeedColorActionPerformed(evt);
            }
        });
        jMenu2.add(mnuRadSpeedColor);

        btnGrpColoring.add(mnuRadKEColor);
        mnuRadKEColor.setText("Color by Kinetic Energy");
        mnuRadKEColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRadKEColorActionPerformed(evt);
            }
        });
        jMenu2.add(mnuRadKEColor);

        mnuBtnPistonControls.setText("Piston Control Panel");
        mnuBtnPistonControls.setEnabled(false);
        mnuBtnPistonControls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBtnPistonControlsActionPerformed(evt);
            }
        });
        jMenu2.add(mnuBtnPistonControls);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Simulation");

        mnuBtnSimSettings.setText("Simulation Settings");
        mnuBtnSimSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBtnSimSettingsActionPerformed(evt);
            }
        });
        jMenu3.add(mnuBtnSimSettings);

        mnuBtnStatSettings.setText("Statistics Settings");
        mnuBtnStatSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBtnStatSettingsActionPerformed(evt);
            }
        });
        jMenu3.add(mnuBtnStatSettings);

        mnuBtnResTemp.setText("Reservoir Temperature");
        mnuBtnResTemp.setEnabled(false);
        mnuBtnResTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBtnResTempActionPerformed(evt);
            }
        });
        jMenu3.add(mnuBtnResTemp);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Help");

        mnuItmBoltzmannWebpage.setText("Boltzmann 3D Webpage");
        mnuItmBoltzmannWebpage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmBoltzmannWebpageActionPerformed(evt);
            }
        });
        jMenu4.add(mnuItmBoltzmannWebpage);

        mnuItmLwjgl.setText("LWJGL Website");
        mnuItmLwjgl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmLwjglActionPerformed(evt);
            }
        });
        jMenu4.add(mnuItmLwjgl);

        mnuItmLicenses.setText("View Licenses");
        mnuItmLicenses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmLicensesActionPerformed(evt);
            }
        });
        jMenu4.add(mnuItmLicenses);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuBtnSimSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBtnSimSettingsActionPerformed
        view.getController().newSimulation();
    }//GEN-LAST:event_mnuBtnSimSettingsActionPerformed

    private void mnuBtnStatSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBtnStatSettingsActionPerformed
        view.getController().showStatisticSettings();
    }//GEN-LAST:event_mnuBtnStatSettingsActionPerformed

    private void mnuItmLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmLoadActionPerformed
        Load.getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
        //Load.getFileChooser().setFileFilter(new SetFileFilter());
        FileFilter fileFilter = new FileFilter() {
            /** Still supports the transitional .set2 format for compatibility. */
            @Override
            public boolean accept(File f)
            {
                    int index = f.getName().lastIndexOf(".");
                    if (f.isFile() && (index < 0 ||
                                    ( !f.getName().substring(index).toLowerCase().equals(".set") &&
                                                            !(f.getName().substring(index).toLowerCase().equals(".set2"))
                                                    )
                                    ))
                            return false;
                    return true;

            }

            @Override
            public String getDescription() {
                return "Settings Files (.set)";
            }
        };

        Load.getFileChooser().setFileFilter(fileFilter);
        int choice = Load.getFileChooser().showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION)
        {
            //See Utils, SetFileFilter, and Load.
            File file = Load.getFileChooser().getSelectedFile();
            Load loader = new Load(null);
            if (file != null)
            try {
                SimulationInfo newSimInfo = loader.loadFile(file);
                view.getController().applySimulationInfo(newSimInfo);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }//GEN-LAST:event_mnuItmLoadActionPerformed

    private void mnuBtnPistonControlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBtnPistonControlsActionPerformed
        view.getController().showPistonControls();
    }//GEN-LAST:event_mnuBtnPistonControlsActionPerformed

    private void mnuRadNormalColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRadNormalColorActionPerformed
        coloringChanged();
    }//GEN-LAST:event_mnuRadNormalColorActionPerformed

    private void mnuRadSpeedColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRadSpeedColorActionPerformed
        coloringChanged();
    }//GEN-LAST:event_mnuRadSpeedColorActionPerformed

    private void mnuRadKEColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRadKEColorActionPerformed
        coloringChanged();
    }//GEN-LAST:event_mnuRadKEColorActionPerformed

    private void mnuBtnResTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBtnResTempActionPerformed
        view.getController().showReservoirControls();
    }//GEN-LAST:event_mnuBtnResTempActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        view.getController().exitBoltzmann();
    }//GEN-LAST:event_formWindowClosing

    private void mnuItmExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmExitActionPerformed
        view.getController().exitBoltzmann();
    }//GEN-LAST:event_mnuItmExitActionPerformed

    private void mnuItmSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmSaveActionPerformed
        view.getController().saveSimulationInfo();
    }//GEN-LAST:event_mnuItmSaveActionPerformed

    private void mnuItmReloadDefaultSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmReloadDefaultSettingsActionPerformed
        view.getController().applySimulationInfo(Main.getDefaultSimulationInfo());
    }//GEN-LAST:event_mnuItmReloadDefaultSettingsActionPerformed

    private void mnuItmBoltzmannWebpageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmBoltzmannWebpageActionPerformed
        view.getController().boltzmannWebpageClicked();
    }//GEN-LAST:event_mnuItmBoltzmannWebpageActionPerformed

    private void mnuItmLwjglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmLwjglActionPerformed
        view.getController().lwjglSiteClicked();
    }//GEN-LAST:event_mnuItmLwjglActionPerformed

    private void mnuItmLicensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmLicensesActionPerformed
        view.getController().licensesClicked();
    }//GEN-LAST:event_mnuItmLicensesActionPerformed

    private void coloringChanged() {
        if (mnuRadNormalColor.isSelected()) {
            view.setParticleColorMode(ColorMode.NORMAL_COLORING);
        } else if (mnuRadSpeedColor.isSelected()) {
            view.setParticleColorMode(ColorMode.COLOR_BY_SPEED);
        } else if (mnuRadKEColor.isSelected()) {
            view.setParticleColorMode(ColorMode.COLOR_BY_KE);
        }
    }
    
    public void setSimulationInfo(SimulationInfo simInfo) {
        mnuBtnPistonControls.setEnabled(simInfo.isPiston());
        
        mnuBtnResTemp.setEnabled(simInfo.includeHeatReservoir);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGrpColoring;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem mnuBtnPistonControls;
    private javax.swing.JMenuItem mnuBtnResTemp;
    private javax.swing.JMenuItem mnuBtnSimSettings;
    private javax.swing.JMenuItem mnuBtnStatSettings;
    private javax.swing.JMenuItem mnuItmBoltzmannWebpage;
    private javax.swing.JMenuItem mnuItmExit;
    private javax.swing.JMenuItem mnuItmLicenses;
    private javax.swing.JMenuItem mnuItmLoad;
    private javax.swing.JMenuItem mnuItmLwjgl;
    private javax.swing.JMenuItem mnuItmReloadDefaultSettings;
    private javax.swing.JMenuItem mnuItmSave;
    private javax.swing.JRadioButtonMenuItem mnuRadKEColor;
    private javax.swing.JRadioButtonMenuItem mnuRadNormalColor;
    private javax.swing.JRadioButtonMenuItem mnuRadSpeedColor;
    // End of variables declaration//GEN-END:variables
}
