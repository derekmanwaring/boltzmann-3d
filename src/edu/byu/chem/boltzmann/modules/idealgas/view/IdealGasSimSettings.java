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

import edu.byu.chem.boltzmann.model.statistics.WallPressure;
import edu.byu.chem.boltzmann.modules.sharedcomponents.LabeledAlignedRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.NumberOfParticlesRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.ParticleMassRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.TemperatureSettingRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings.ArenaSizeChangedEvent;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettings.ArenaSizeChangedListener;
import edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize.ArenaSizeSettingsRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedEvent;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelectorRow;
import edu.byu.chem.boltzmann.modules.sharedcomponents.dimensionselector.DimensionSelector.DimensionChangedListener;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.utils.Units.Mass;
import edu.byu.chem.boltzmann.utils.Units.Temperature;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SelectedStatistics;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.Dimension;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberCommitted;
import edu.byu.chem.boltzmann.view.maingui.components.textfields.NumericTextField.NewNumberListener;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsController;
import edu.byu.chem.boltzmann.view.simulationsettings.SimSettingsView;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypeDescriptor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author Derek Manwaring
 * 27 Oct 2011
 */
public class IdealGasSimSettings extends JPanel implements SimSettingsView {
    
    private static final double DEFAULT_ARENA_X_SIZE = 40.0; // nanometers    
    private static final double ARENA_DIMENSIONS_MIN = 10.0; // nanometers
    private static final double ARENA_DIMENSIONS_MAX = 300.0; // nanometers    
    private static final int ARENA_DIMENSION_FIELDS_COLUMNS = 4;
    
    private static final double DEFAULT_TEMPERATURE = 300.0; // Kelvin    
    private static final double TEMPERATURE_MIN = 0.0; // Kelvin
    private static final double TEMPERATURE_MAX = 1500.0; // Kelvin
    
    private static final int DEFAULT_NUM_PARTICLES = 50;    
    private static final int NUM_PARTICLES_MIN = 0;
    private static final int NUM_PARTICLES_MAX = 500;
    
    private static final int DEFAULT_MASS = 2; // AMU    
    private static final int MASS_MIN = 1; // AMU
    private static final int MASS_MAX = 300; // AMU
    
    private static final double DEFAULT_RADIUS_2D = Units.convert("nm", "m", 0.4);
    private static final double DEFAULT_RADIUS_3D = Units.convert("nm", "m", 1.2);
    private static final double ARENA_SIDE_LENGTH_MODIFICATION_2D = DEFAULT_RADIUS_2D * 2.0;
    private static final double ARENA_SIDE_LENGTH_MODIFICATION_3D = DEFAULT_RADIUS_3D * 2.0;
    
    private static final int DEFAULT_TEXT_FIELD_COLUMNS = 5;
    
    private static final int ALIGNED_ROW_WIDTH = 240; // pixels
    
    private SimSettingsController controller;
    
    public NewNumberListener numberEnteredListener = new NewNumberListener() {
        public void notify(NewNumberCommitted event) {
            applySettings();
        }
    };
    
    private DimensionChangedListener dimensionListener = new DimensionChangedListener() {
        public void notify(DimensionChangedEvent event) {
            applySettings();
        }
    };
    
    private ArenaSizeChangedListener arenaSizeListener = new ArenaSizeChangedListener() {
        public void notify(ArenaSizeChangedEvent event) {
            applySettings();
        }
    };
    
    private DimensionSelectorRow dimension = new DimensionSelectorRow(
            ALIGNED_ROW_WIDTH, 
            Dimension.TWO);
    
    private ArenaSizeSettingsRow arenaSize = new ArenaSizeSettingsRow(
            ALIGNED_ROW_WIDTH,
            DEFAULT_ARENA_X_SIZE, 
            ARENA_DIMENSIONS_MIN, 
            ARENA_DIMENSIONS_MAX, 
            ARENA_DIMENSION_FIELDS_COLUMNS,
            dimension);
    
    private TemperatureSettingRow temperature = new TemperatureSettingRow(
            ALIGNED_ROW_WIDTH, 
            DEFAULT_TEMPERATURE, 
            TEMPERATURE_MIN,
            TEMPERATURE_MAX, 
            DEFAULT_TEXT_FIELD_COLUMNS, 
            numberEnteredListener);
    
    private NumberOfParticlesRow numParticles = new NumberOfParticlesRow(
            ALIGNED_ROW_WIDTH,
            DEFAULT_NUM_PARTICLES,
            NUM_PARTICLES_MIN,
            NUM_PARTICLES_MAX,
            DEFAULT_TEXT_FIELD_COLUMNS,
            numberEnteredListener);
    
    private ParticleMassRow particleMass = new ParticleMassRow(
            ALIGNED_ROW_WIDTH, 
            DEFAULT_MASS,
            MASS_MIN,
            MASS_MAX,
            DEFAULT_TEXT_FIELD_COLUMNS,
            numberEnteredListener);
    
    private Color particleColor = null;
            
    private TypeDescriptor singleParticleDescriptor = new TypeDescriptor() {

        public ParticleType getTypeDescribed() {
            double radiusForDimension = DEFAULT_RADIUS_2D;
            if (dimension.getSelectedDimension() == Dimension.THREE) {
                radiusForDimension = DEFAULT_RADIUS_3D;
            }
            
            return new ParticleType(
                    particleMass.getMass(Mass.ATOMIC_MASS_UNIT), 
                    radiusForDimension, 
                    particleColor, 
                    "Red");
        }

        public int getNumParticles() {
            return numParticles.getNumParticles();
        }
    };

    Set<TypeDescriptor> setForSingleParticleDesc = new HashSet<TypeDescriptor>();
    SelectedStatistics<TypeDescriptor> selectedStats = new SelectedStatistics<TypeDescriptor>();
    
    private double arenaSideLengthModification;
    
    /** Creates new form IdealGasSimSettings */
    public IdealGasSimSettings() {  
        initComponents();
                
        LabeledAlignedRow arenaHeader = new LabeledAlignedRow(ALIGNED_ROW_WIDTH);
        arenaHeader.addLabel("Arena Size (nm)");
        
        add(dimension, 1);
        add(arenaHeader, 2);
        add(arenaSize, 3);
        add(temperature, 4);        
        add(numParticles, 5);
        add(particleMass, 6);
        
        setForSingleParticleDesc.add(singleParticleDescriptor);
        selectedStats.setSelected(setForSingleParticleDesc, WallPressure.class, true);
        
        dimension.addListener(dimensionListener);
        arenaSize.addListener(arenaSizeListener);
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
        pnlSettingsHeader = new javax.swing.JPanel();
        lblSettings = new javax.swing.JLabel();
        pnlApplySettings = new javax.swing.JPanel();
        btnApplySettings = new javax.swing.JButton();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        pnlSettingsHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 13, 8));

        lblSettings.setText("Settings:");
        pnlSettingsHeader.add(lblSettings);

        add(pnlSettingsHeader);

        btnApplySettings.setText("Apply Settings");
        btnApplySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplySettingsActionPerformed(evt);
            }
        });
        pnlApplySettings.add(btnApplySettings);

        add(pnlApplySettings);
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplySettingsActionPerformed
        applySettings();
    }//GEN-LAST:event_btnApplySettingsActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplySettings;
    private javax.swing.ButtonGroup btnGrpDimension;
    private javax.swing.JLabel lblSettings;
    private javax.swing.JPanel pnlApplySettings;
    private javax.swing.JPanel pnlSettingsHeader;
    // End of variables declaration//GEN-END:variables

    private void applySettings() {
        arenaSideLengthModification = ARENA_SIDE_LENGTH_MODIFICATION_2D;
        if (dimension.getSelectedDimension() == Dimension.THREE) {
            arenaSideLengthModification = ARENA_SIDE_LENGTH_MODIFICATION_3D;
        }
        controller.applySimulationSettings();        
    }
    
    public void attachController(SimSettingsController controller) {
        this.controller = controller;
    }

    public ArenaType getArenaType() {
        return ArenaType.REFLECTING_BOUNDARIES;
    }

    public int getDimension() {
        if (dimension.getSelectedDimension() == Dimension.TWO) {
            return 2;
        } else {
            return 3;
        }
    }

    public double getArenaXSize() {
        return arenaSize.getXSize(Units.Length.METER) + arenaSideLengthModification;
    }

    public double getArenaYSize() {
        return arenaSize.getYSize(Units.Length.METER) + arenaSideLengthModification;
    }

    public double getArenaZSize() {
        return arenaSize.getZSize(Units.Length.METER) + arenaSideLengthModification;
    }

    public double getHoleDiameter() {
        throw new UnsupportedOperationException("No hole in module.");
    }

    public boolean isReactionModeSelected() {
        return false;
    }

    public boolean areAttractiveInteractionsAllowed() {
        return false;
    }

    public double getWellDepth(TypeDescriptor type) {
        throw new UnsupportedOperationException("No square wells in module.");
    }

    public double getRadiusOfInteractionMultiplier() {
        throw new UnsupportedOperationException("No square wells in module.");
    }

    public boolean includeAttractiveWall() {
        return false;
    }

    public WallEnum getAttractiveWall() {
        throw new UnsupportedOperationException("No square wells in module.");
    }

    public double getWallWellWidth() {
        throw new UnsupportedOperationException("No square wells in module.");
    }

    public double getWallWellDepth() {
        throw new UnsupportedOperationException("No square wells in module.");
    }

    public boolean includeHeatReservoir() {
        return false;
    }

    public double getInitialReservoirTemperature() {
        throw new UnsupportedOperationException("No heat reservoir in module.");
    }

    public double getInitialTemperature() {
        return temperature.getTemperature(Temperature.KELVIN);
    }

    public SelectedStatistics<TypeDescriptor> getSelectedStats() {
        return selectedStats;
    }

    public List<TypeDescriptor> getTypeDescriptors() {
        return new ArrayList<TypeDescriptor>(setForSingleParticleDesc);
    }

    public double getForwardEnergy() {
        throw new UnsupportedOperationException("No reactions in module.");
    }

    public double getReverseEnergy() {
        throw new UnsupportedOperationException("No reactions in module.");
    }

    public boolean shouldSuppressRevReaction() {
        throw new UnsupportedOperationException("No reactions in module.");
    }

    public void setSimulationInfo(SimulationInfo simInfo) {
        arenaSideLengthModification = ARENA_SIDE_LENGTH_MODIFICATION_2D;
        
        switch (simInfo.dimension) {
            case 1:
                throw new UnsupportedOperationException("Module supports only 2D and 3D");
            case 2:
                dimension.setSelectedDimension(Dimension.TWO);
                break;
            case 3:
                dimension.setSelectedDimension(Dimension.THREE);
                arenaSideLengthModification = ARENA_SIDE_LENGTH_MODIFICATION_3D;
                arenaSize.setZSize(simInfo.arenaZSize - arenaSideLengthModification, Length.METER);
        }
        
        arenaSize.setXSize(simInfo.arenaXSize - arenaSideLengthModification, Length.METER);
        arenaSize.setYSize(simInfo.arenaYSize - arenaSideLengthModification, Length.METER);
        
        arenaSize.updateState();        
        
        temperature.setTemperature(simInfo.initialTemperature, Temperature.KELVIN);
        
        List<ParticleType> types = simInfo.getParticleTypes();        
        if (types.size() != 1) {
            throw new UnsupportedOperationException("Module supports only one particle type (given " + types.size() + ")");
        }
        
        ParticleType type = types.get(0);        
        numParticles.setNumParticles(simInfo.getNumberOfParticles(type));
        particleMass.setMass(type.particleMass, Mass.ATOMIC_MASS_UNIT);
        if (type.particleRadius != DEFAULT_RADIUS_2D  && simInfo.dimension == 2) {
            System.err.println("Radius loaded for ideal gas module does not match default (expected " + 
                    DEFAULT_RADIUS_2D + ", was " + type.particleRadius + ")");
        }
        particleColor = type.defaultColor;        
    }
}
