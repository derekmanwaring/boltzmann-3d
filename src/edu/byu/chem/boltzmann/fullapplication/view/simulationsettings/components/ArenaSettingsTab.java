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
package edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components;

import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.Units.Temperature;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.Dimension;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;

/**
 *
 * @author Derek Manwaring
 * 14 Dec 2011
 */
public class ArenaSettingsTab extends javax.swing.JPanel {
    
    /**
     * Default setting for the initial temperature in Kelvin
     */
    private static final double DEFAULT_INITIAL_TEMPERATURE = 300.0;

    UnitTextField<Temperature> temperature = new UnitTextField<Temperature>(
            Temperature.KELVIN, 
            DEFAULT_INITIAL_TEMPERATURE, 
            0.0, 
            SimulationInfo.MAXIMUM_INITIAL_TEMPERATURE, 
            4);
    
    /** Creates new form NewJPanel */
    public ArenaSettingsTab() {
        initComponents();
        
        arenaSizeSettings.setDimensionSelector(dimensionSelector);
        
        pnlTemperature.add(temperature);
        
        arenaTypeSettings.setArenaSizeSettings(arenaSizeSettings, dimensionSelector);
    }
    
    public void setDimension(Dimension dimension) {
        dimensionSelector.setSelectedDimension(dimension);
        arenaSizeSettings.updateState();
        arenaTypeSettings.regulateHoleOptions();
    }
    
    public Dimension getDimension() {
        return dimensionSelector.getSelectedDimension();
    }
    
    public void addListener(DimensionSelector.DimensionChangedListener listener) {
        dimensionSelector.addListener(listener);
    }
    
    /**
     * Sets the text boxes to reflect the given sizes, depending on the dimension.
     */
    public void setArenaSize(
            double xSize, double ySize, double zSize, Dimension dimension, Length units) {
        arenaSizeSettings.setXSize(xSize, units);
        
        if (dimension != Dimension.ONE) {
            arenaSizeSettings.setYSize(ySize, units);
            if (dimension == Dimension.THREE) {
                arenaSizeSettings.setZSize(zSize, units);
            }
        }
        
        arenaTypeSettings.regulateHoleOptions();
    }
    
    public double getArenaXSize(Length units) {
        return arenaSizeSettings.getXSize(units);
    }
    
    public double getArenaYSize(Length units) {
        return arenaSizeSettings.getYSize(units);
    }
    
    public double getArenaZSize(Length units) {
        return arenaSizeSettings.getZSize(units);
    }
    
    public void setTemperature(double temperature, Temperature units) {
        this.temperature.setValueInUnits(temperature, units);
    }
    
    public double getTemperature(Temperature units) {
        return temperature.getValueInUnits(units);
    }
    
    public void setArenaType(ArenaType type) {
        arenaTypeSettings.setType(type);
    }
    
    public ArenaType getArenaType() {
        return arenaTypeSettings.getType();
    }
    
    public void setDividedArenaHoleDiameter(double diameter, Length units) {
        arenaTypeSettings.setDividedHoleDiameter(diameter, units);
    }
    
    public double getDividedArenaHoleDiameter(Length units) {
        return arenaTypeSettings.getDividedHoleDiameter(units);
    }
    
    public void setMaxwellDemonModeSelected(boolean maxwellDemonMode) {
        arenaTypeSettings.setMaxwellDemonModeSelected(maxwellDemonMode);
    }
    
    public boolean isMaxwellDemonModeSelected() {
        return arenaTypeSettings.isMaxwellDemonModeSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSpacer = new javax.swing.JPanel();
        pnlDimSizeAndType = new javax.swing.JPanel();
        pnlSpacer2 = new javax.swing.JPanel();
        pnlDimensionAndSize = new javax.swing.JPanel();
        pnlDimension = new javax.swing.JPanel();
        pnlSpacer3 = new javax.swing.JPanel();
        pnlDimensionButtons = new javax.swing.JPanel();
        pnlSpacer4 = new javax.swing.JPanel();
        dimensionSelector = new edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.FullDimensionSelector();
        pnlFiller = new javax.swing.JPanel();
        pnlSpacer6 = new javax.swing.JPanel();
        pnlSize = new javax.swing.JPanel();
        pnlSpacer7 = new javax.swing.JPanel();
        pnlSizeFields = new javax.swing.JPanel();
        pnlSpacer8 = new javax.swing.JPanel();
        arenaSizeSettings = new edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.FullArenaSizeSettings();
        pnlFiller2 = new javax.swing.JPanel();
        pnlSpacer9 = new javax.swing.JPanel();
        pnlSpacer10 = new javax.swing.JPanel();
        pnlArenaType = new javax.swing.JPanel();
        pnlSpacer11 = new javax.swing.JPanel();
        pnlTypeButtons = new javax.swing.JPanel();
        pnlSpacer12 = new javax.swing.JPanel();
        arenaTypeSettings = new edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.arenatype.FullArenaTypeSettings();
        pnlFiller3 = new javax.swing.JPanel();
        pnlSpacer13 = new javax.swing.JPanel();
        pnlTemperature = new javax.swing.JPanel();
        pnlSpacer14 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlFiller4 = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
        add(pnlSpacer);

        pnlDimSizeAndType.setLayout(new javax.swing.BoxLayout(pnlDimSizeAndType, javax.swing.BoxLayout.LINE_AXIS));

        pnlSpacer2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 5));
        pnlDimSizeAndType.add(pnlSpacer2);

        pnlDimensionAndSize.setLayout(new javax.swing.BoxLayout(pnlDimensionAndSize, javax.swing.BoxLayout.PAGE_AXIS));

        pnlDimension.setBorder(javax.swing.BorderFactory.createTitledBorder("Dimension"));
        pnlDimension.setLayout(new javax.swing.BoxLayout(pnlDimension, javax.swing.BoxLayout.PAGE_AXIS));

        pnlSpacer3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));
        pnlDimension.add(pnlSpacer3);

        pnlDimensionButtons.setLayout(new javax.swing.BoxLayout(pnlDimensionButtons, javax.swing.BoxLayout.LINE_AXIS));
        pnlDimensionButtons.add(pnlSpacer4);
        pnlDimensionButtons.add(dimensionSelector);

        pnlFiller.setLayout(new java.awt.CardLayout());
        pnlDimensionButtons.add(pnlFiller);

        pnlDimension.add(pnlDimensionButtons);

        pnlSpacer6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 65, 10));
        pnlDimension.add(pnlSpacer6);

        pnlDimensionAndSize.add(pnlDimension);

        pnlSize.setBorder(javax.swing.BorderFactory.createTitledBorder("Size"));
        pnlSize.setLayout(new javax.swing.BoxLayout(pnlSize, javax.swing.BoxLayout.PAGE_AXIS));

        pnlSpacer7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));
        pnlSize.add(pnlSpacer7);

        pnlSizeFields.setLayout(new javax.swing.BoxLayout(pnlSizeFields, javax.swing.BoxLayout.LINE_AXIS));
        pnlSizeFields.add(pnlSpacer8);
        pnlSizeFields.add(arenaSizeSettings);

        pnlFiller2.setLayout(new java.awt.CardLayout());
        pnlSizeFields.add(pnlFiller2);

        pnlSize.add(pnlSizeFields);

        pnlSpacer9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));
        pnlSize.add(pnlSpacer9);

        pnlDimensionAndSize.add(pnlSize);

        pnlDimSizeAndType.add(pnlDimensionAndSize);

        pnlSpacer10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 5));
        pnlDimSizeAndType.add(pnlSpacer10);

        pnlArenaType.setBorder(javax.swing.BorderFactory.createTitledBorder("Type"));
        pnlArenaType.setLayout(new javax.swing.BoxLayout(pnlArenaType, javax.swing.BoxLayout.PAGE_AXIS));

        pnlSpacer11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));
        pnlArenaType.add(pnlSpacer11);

        pnlTypeButtons.setLayout(new javax.swing.BoxLayout(pnlTypeButtons, javax.swing.BoxLayout.LINE_AXIS));

        pnlSpacer12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));
        pnlTypeButtons.add(pnlSpacer12);

        arenaTypeSettings.setPreferredSize(new java.awt.Dimension(250, 259));
        pnlTypeButtons.add(arenaTypeSettings);

        pnlFiller3.setLayout(new java.awt.CardLayout());
        pnlTypeButtons.add(pnlFiller3);

        pnlArenaType.add(pnlTypeButtons);

        pnlSpacer13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 115, 20));
        pnlArenaType.add(pnlSpacer13);

        pnlDimSizeAndType.add(pnlArenaType);

        add(pnlDimSizeAndType);

        pnlTemperature.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pnlSpacer14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 13, 5));
        pnlTemperature.add(pnlSpacer14);

        jLabel2.setText("Initial Temperature (K):");
        pnlTemperature.add(jLabel2);

        add(pnlTemperature);

        pnlFiller4.setPreferredSize(new java.awt.Dimension(0, 5000));
        pnlFiller4.setLayout(new javax.swing.BoxLayout(pnlFiller4, javax.swing.BoxLayout.LINE_AXIS));
        add(pnlFiller4);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.FullArenaSizeSettings arenaSizeSettings;
    private edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.arenatype.FullArenaTypeSettings arenaTypeSettings;
    private edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.FullDimensionSelector dimensionSelector;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel pnlArenaType;
    private javax.swing.JPanel pnlDimSizeAndType;
    private javax.swing.JPanel pnlDimension;
    private javax.swing.JPanel pnlDimensionAndSize;
    private javax.swing.JPanel pnlDimensionButtons;
    private javax.swing.JPanel pnlFiller;
    private javax.swing.JPanel pnlFiller2;
    private javax.swing.JPanel pnlFiller3;
    private javax.swing.JPanel pnlFiller4;
    private javax.swing.JPanel pnlSize;
    private javax.swing.JPanel pnlSizeFields;
    private javax.swing.JPanel pnlSpacer;
    private javax.swing.JPanel pnlSpacer10;
    private javax.swing.JPanel pnlSpacer11;
    private javax.swing.JPanel pnlSpacer12;
    private javax.swing.JPanel pnlSpacer13;
    private javax.swing.JPanel pnlSpacer14;
    private javax.swing.JPanel pnlSpacer2;
    private javax.swing.JPanel pnlSpacer3;
    private javax.swing.JPanel pnlSpacer4;
    private javax.swing.JPanel pnlSpacer6;
    private javax.swing.JPanel pnlSpacer7;
    private javax.swing.JPanel pnlSpacer8;
    private javax.swing.JPanel pnlSpacer9;
    private javax.swing.JPanel pnlTemperature;
    private javax.swing.JPanel pnlTypeButtons;
    // End of variables declaration//GEN-END:variables

}
