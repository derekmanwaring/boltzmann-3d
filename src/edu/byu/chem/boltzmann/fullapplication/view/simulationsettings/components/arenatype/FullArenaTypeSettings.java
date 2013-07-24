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
package edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.arenatype;

import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings.ArenaSizeChangedEvent;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings.ArenaSizeChangedListener;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.ButtonGroupWithValues;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.ButtonGroupWithValues.SelectionChangedListener;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedEvent;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedListener;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannListenerHandler;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 *
 * @author Derek Manwaring
 * 16 Dec 2011
 */
public class FullArenaTypeSettings extends javax.swing.JPanel implements ArenaTypeSettings {

    public static class ArenaSizeSettingsAlreadySpecifiedException extends
            IllegalStateException {        
    }
    
    private BoltzmannListenerHandler<ArenaTypeChangedListener, ArenaTypeChangedEvent> listeners = 
            new BoltzmannListenerHandler<ArenaTypeChangedListener, ArenaTypeChangedEvent>();

    private ButtonGroupWithValues<ArenaType> btnGrpArenaType = new ButtonGroupWithValues<ArenaType>(
            new SelectionChangedListener<ArenaType>() {
                @Override
                public void selectionChanged(ArenaType newSelectedValue) {
                    listeners.notifyListeners(new ArenaTypeChangedEvent(newSelectedValue));
                }         
            });
    
    private ArenaTypeChangedListener dividedHoleDiameterInputEnabler = 
            new ArenaTypeChangedListener() {
        @Override
        public void notify(ArenaTypeChangedEvent event) {
            setDividedHoleOptionsEnabled();
        }
    };
    
    private void setDividedHoleOptionsEnabled() {
        boolean optionsEnabled = btnGrpArenaType.getSelectedValue() == 
                ArenaType.DIVIDED_ARENA_WITH_HOLE;
        holeDiameter.setEnabled(optionsEnabled);
        lblHoleDiameter.setEnabled(optionsEnabled);
        chkBoxMaxwellDemonMode.setEnabled(optionsEnabled);
    }
    
    private ArenaSizeSettings sizeSettings = null;
    private DimensionSelector dimension = null;
    
    private ArenaSizeChangedListener dividedHoleDiameterRegulatorSizeListener = 
            new ArenaSizeChangedListener() {
        public void notify(ArenaSizeChangedEvent event) {
            regulateHoleOptions();
        }
    };
    
    private DimensionChangedListener dividedHoleDiameterRegulatorDimensionListener = 
            new DimensionChangedListener() {
        public void notify(DimensionChangedEvent event) {
            regulateHoleOptions();
        }
    };
    
    public void regulateHoleOptions() {
        SimulationInfo.Dimension dim = dimension.getSelectedDimension();
        if(dim == SimulationInfo.Dimension.ONE){
            if(btnGrpArenaType.getSelectedValue() == ArenaType.DIVIDED_ARENA_WITH_HOLE){
                btnGrpArenaType.setSelectedValue(ArenaType.DIVIDED_ARENA);
            }
            tglBtnDividedArenaWithHole.setEnabled(false);
            
        } else {
            tglBtnDividedArenaWithHole.setEnabled(true);
            
            if(dim == SimulationInfo.Dimension.TWO) {
                    holeDiameter.setMaximum(sizeSettings.getYSize(Length.NANOMETER));
            } else if(dim == SimulationInfo.Dimension.THREE) {
                    holeDiameter.setMaximum(Math.min(
                            sizeSettings.getYSize(Length.NANOMETER), 
                            sizeSettings.getZSize(Length.NANOMETER)));
            }
        }
    };
    
    /**
     * Default setting for the size of the divided arena hole in nanometers
     */
    private static final double DEFAULT_HOLE_SIZE = 5.0;    
    private UnitTextField<Length> holeDiameter = new UnitTextField<Length>(
            Length.NANOMETER, 
            DEFAULT_HOLE_SIZE, 
            0.0, 
            DEFAULT_HOLE_SIZE, 
            4);
        
    /** Creates new form FullArenaTypeSettings */
    public FullArenaTypeSettings() {
        initComponents();
        
        btnGrpArenaType.addButtonWithValue(tglBtnPeriodicWalls, ArenaType.PERIODIC_BOUNDARIES);
        btnGrpArenaType.addButtonWithValue(tglBtnReflectingWalls, ArenaType.REFLECTING_BOUNDARIES);
        btnGrpArenaType.addButtonWithValue(tglBtnDividedArena, ArenaType.DIVIDED_ARENA);
        btnGrpArenaType.addButtonWithValue(tglBtnDividedArenaWithHole, ArenaType.DIVIDED_ARENA_WITH_HOLE);
        btnGrpArenaType.addButtonWithValue(tglBtnMovablePiston, ArenaType.MOVABLE_PISTON);
                
        pnlDividedHoleSize.add(holeDiameter);
        addListener(dividedHoleDiameterInputEnabler);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPeriodic = new javax.swing.JPanel();
        tglBtnPeriodicWalls = new javax.swing.JToggleButton();
        lblPeriodic = new javax.swing.JLabel();
        pnlReflecting = new javax.swing.JPanel();
        tglBtnReflectingWalls = new javax.swing.JToggleButton();
        lblReflecting = new javax.swing.JLabel();
        pnlDivided = new javax.swing.JPanel();
        tglBtnDividedArena = new javax.swing.JToggleButton();
        lblDivided = new javax.swing.JLabel();
        pnlDividedWithHole = new javax.swing.JPanel();
        pnlDividedWithHoleButton = new javax.swing.JPanel();
        tglBtnDividedArenaWithHole = new javax.swing.JToggleButton();
        lblDividedWithHole = new javax.swing.JLabel();
        pnlDividedHoleSize = new javax.swing.JPanel();
        pnlSpacer = new javax.swing.JPanel();
        lblHoleDiameter = new javax.swing.JLabel();
        pnlMaxwellDemonMode = new javax.swing.JPanel();
        pnlSpacer1 = new javax.swing.JPanel();
        chkBoxMaxwellDemonMode = new javax.swing.JCheckBox();
        pnlPiston = new javax.swing.JPanel();
        tglBtnMovablePiston = new javax.swing.JToggleButton();
        lblPiston = new javax.swing.JLabel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        pnlPeriodic.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        tglBtnPeriodicWalls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/divide_periodic.gif"))); // NOI18N
        pnlPeriodic.add(tglBtnPeriodicWalls);

        lblPeriodic.setText("Periodic walls");
        pnlPeriodic.add(lblPeriodic);

        add(pnlPeriodic);

        pnlReflecting.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        tglBtnReflectingWalls.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/divide_none.gif"))); // NOI18N
        tglBtnReflectingWalls.setSelected(true);
        pnlReflecting.add(tglBtnReflectingWalls);

        lblReflecting.setText("Reflecting walls");
        pnlReflecting.add(lblReflecting);

        add(pnlReflecting);

        pnlDivided.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        tglBtnDividedArena.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/divide_middle.gif"))); // NOI18N
        pnlDivided.add(tglBtnDividedArena);

        lblDivided.setText("Divided arena");
        pnlDivided.add(lblDivided);

        add(pnlDivided);

        pnlDividedWithHole.setLayout(new javax.swing.BoxLayout(pnlDividedWithHole, javax.swing.BoxLayout.PAGE_AXIS));

        pnlDividedWithHoleButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        tglBtnDividedArenaWithHole.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/divide_hole.gif"))); // NOI18N
        pnlDividedWithHoleButton.add(tglBtnDividedArenaWithHole);

        lblDividedWithHole.setText("Divided arena with hole");
        pnlDividedWithHoleButton.add(lblDividedWithHole);

        pnlDividedWithHole.add(pnlDividedWithHoleButton);

        pnlDividedHoleSize.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        pnlDividedHoleSize.add(pnlSpacer);

        lblHoleDiameter.setText("Hole diameter (nm):");
        pnlDividedHoleSize.add(lblHoleDiameter);

        pnlDividedWithHole.add(pnlDividedHoleSize);

        pnlMaxwellDemonMode.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        pnlMaxwellDemonMode.add(pnlSpacer1);

        chkBoxMaxwellDemonMode.setText("Maxwell demon mode");
        pnlMaxwellDemonMode.add(chkBoxMaxwellDemonMode);

        pnlDividedWithHole.add(pnlMaxwellDemonMode);

        add(pnlDividedWithHole);

        pnlPiston.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        tglBtnMovablePiston.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/byu/chem/boltzmann/resources/images/piston.gif"))); // NOI18N
        pnlPiston.add(tglBtnMovablePiston);

        lblPiston.setText("Movable piston");
        pnlPiston.add(lblPiston);

        add(pnlPiston);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkBoxMaxwellDemonMode;
    private javax.swing.JLabel lblDivided;
    private javax.swing.JLabel lblDividedWithHole;
    private javax.swing.JLabel lblHoleDiameter;
    private javax.swing.JLabel lblPeriodic;
    private javax.swing.JLabel lblPiston;
    private javax.swing.JLabel lblReflecting;
    private javax.swing.JPanel pnlDivided;
    private javax.swing.JPanel pnlDividedHoleSize;
    private javax.swing.JPanel pnlDividedWithHole;
    private javax.swing.JPanel pnlDividedWithHoleButton;
    private javax.swing.JPanel pnlMaxwellDemonMode;
    private javax.swing.JPanel pnlPeriodic;
    private javax.swing.JPanel pnlPiston;
    private javax.swing.JPanel pnlReflecting;
    private javax.swing.JPanel pnlSpacer;
    private javax.swing.JPanel pnlSpacer1;
    private javax.swing.JToggleButton tglBtnDividedArena;
    private javax.swing.JToggleButton tglBtnDividedArenaWithHole;
    private javax.swing.JToggleButton tglBtnMovablePiston;
    private javax.swing.JToggleButton tglBtnPeriodicWalls;
    private javax.swing.JToggleButton tglBtnReflectingWalls;
    // End of variables declaration//GEN-END:variables

    public final void addListener(ArenaTypeChangedListener listener) {
        listeners.addListener(listener);
    }

    public void setType(ArenaType type) {
        btnGrpArenaType.setSelectedValue(type);
        setDividedHoleOptionsEnabled();
    }

    public ArenaType getType() {
        return btnGrpArenaType.getSelectedValue();
    }
    
    public void setArenaSizeSettings(ArenaSizeSettings sizeSettings,
            DimensionSelector dimension) {
        if (this.sizeSettings != null) {
            throw new ArenaSizeSettingsAlreadySpecifiedException();
        }
        this.sizeSettings = sizeSettings;
        this.dimension = dimension;
        sizeSettings.addListener(dividedHoleDiameterRegulatorSizeListener);
        dimension.addListener(dividedHoleDiameterRegulatorDimensionListener);
        regulateHoleOptions();
    }
    
    public void setDividedHoleDiameter(double diameter, Length units) {
        holeDiameter.setValueInUnits(diameter, units);
    }

    public double getDividedHoleDiameter(Length units) {
        return holeDiameter.getValueInUnits(units);
    }

    public void setMaxwellDemonModeSelected(boolean maxwellDemonMode) {
        chkBoxMaxwellDemonMode.setSelected(maxwellDemonMode);
    }
    
    public boolean isMaxwellDemonModeSelected() {
        return chkBoxMaxwellDemonMode.isSelected();
    }
}
