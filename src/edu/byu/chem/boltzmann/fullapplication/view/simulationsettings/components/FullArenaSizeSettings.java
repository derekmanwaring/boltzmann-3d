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

import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeTextBoxStateHandler;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberCommitted;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.UnitTextField;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannListenerHandler;

/**
 *
 * @author Derek Manwaring
 * 16 Dec 2011
 */
public class FullArenaSizeSettings extends javax.swing.JPanel implements ArenaSizeSettings {
    
    public static class NoDimensionSelectorAssignedException extends 
            IllegalStateException {        
    }
    
    private BoltzmannListenerHandler<ArenaSizeChangedListener, ArenaSizeChangedEvent> listeners = 
            new BoltzmannListenerHandler<ArenaSizeChangedListener, ArenaSizeChangedEvent>();
    
    private DimensionSelector dimension;
    private ArenaSizeTextBoxStateHandler stateHandler;
    
    public NewNumberListener sizeChangedListener = new NewNumberListener() {
        public void notify(NewNumberCommitted event) {
            listeners.notifyListeners(new ArenaSizeChangedEvent());
        }
    };
    
    /**
     * Default setting for the length of each dimension of the arena in nanometers
     */
    private static final double DEFAULT_ARENA_SIZE = 40.0;
    
    UnitTextField<Length> arenaXSize = new UnitTextField<Length>(
            Length.NANOMETER, 
            DEFAULT_ARENA_SIZE, 
            0.0, 
            Units.convert("m", "nm", SimulationInfo.MAXIMUM_ARENA_SIDE_LENGTH), 
            4);
    
    UnitTextField<Length> arenaYSize = new UnitTextField<Length>(
            Length.NANOMETER, 
            DEFAULT_ARENA_SIZE, 
            0.0, 
            Units.convert("m", "nm", SimulationInfo.MAXIMUM_ARENA_SIDE_LENGTH), 
            4);
    
    UnitTextField<Length> arenaZSize = new UnitTextField<Length>(
            Length.NANOMETER, 
            DEFAULT_ARENA_SIZE, 
            0.0, 
            Units.convert("m", "nm", SimulationInfo.MAXIMUM_ARENA_SIDE_LENGTH), 
            4);
    
    /** Creates new form FullArenaSizeSettings */
    public FullArenaSizeSettings() {
        initComponents();
        
        pnlXSize.add(arenaXSize);
        pnlYSize.add(arenaYSize);
        pnlZSize.add(arenaZSize);
        
        arenaXSize.addListener(sizeChangedListener);
        arenaYSize.addListener(sizeChangedListener);
        arenaZSize.addListener(sizeChangedListener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlXSize = new javax.swing.JPanel();
        lblXSize = new javax.swing.JLabel();
        pnlYSize = new javax.swing.JPanel();
        lblYSize = new javax.swing.JLabel();
        pnlZSize = new javax.swing.JPanel();
        lblZsize = new javax.swing.JLabel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        pnlXSize.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblXSize.setText("X size (nm):");
        pnlXSize.add(lblXSize);

        add(pnlXSize);

        pnlYSize.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblYSize.setText("Y size (nm):");
        pnlYSize.add(lblYSize);

        add(pnlYSize);

        pnlZSize.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblZsize.setText("Z size (nm):");
        pnlZSize.add(lblZsize);

        add(pnlZSize);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblXSize;
    private javax.swing.JLabel lblYSize;
    private javax.swing.JLabel lblZsize;
    private javax.swing.JPanel pnlXSize;
    private javax.swing.JPanel pnlYSize;
    private javax.swing.JPanel pnlZSize;
    // End of variables declaration//GEN-END:variables

    public void setDimensionSelector(DimensionSelector dimension) {
        this.dimension = dimension;
        stateHandler = new ArenaSizeTextBoxStateHandler(
                lblXSize, arenaXSize, 
                lblYSize, arenaYSize, 
                lblZsize, arenaZSize, dimension);
    }
    
    public void updateState() {
        if (dimension == null) {
            throw new NoDimensionSelectorAssignedException();
        }
        stateHandler.updateState(dimension.getSelectedDimension());
    }
    
    public void addListener(ArenaSizeChangedListener listener) {
        listeners.addListener(listener);
    }

    public void setSize(double xSize, double ySize, double zSize, Length units) {
        setXSize(xSize, units);
        setYSize(ySize, units);
        setZSize(zSize, units);
    }

    public void setXSize(double xSize, Length units) {
        arenaXSize.setValueInUnits(xSize, units);
    }

    public void setYSize(double ySize, Length units) {
        arenaYSize.setValueInUnits(ySize, units);
    }

    public void setZSize(double zSize, Length units) {
        arenaZSize.setValueInUnits(zSize, units);
    }

    public double getXSize(Length units) {
        return arenaXSize.getValueInUnits(units);
    }

    public double getYSize(Length units) {
        return arenaYSize.getValueInUnits(units);
    }

    public double getZSize(Length units) {
        return arenaZSize.getValueInUnits(units);
    }
}
