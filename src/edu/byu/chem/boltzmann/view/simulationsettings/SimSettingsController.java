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
package edu.byu.chem.boltzmann.view.simulationsettings;

import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.*;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import edu.byu.chem.boltzmann.view.simulationsettings.components.TypeDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A controller for the simulation settings view.
 * @author Derek Manwaring
 * April 2010
 */
public class SimSettingsController implements SimulationInfoSupplier {

    private final edu.byu.chem.boltzmann.controller.Controller rootController;
    private final SimSettingsView view;

    private ParticleType[] particleTypes;
    private ReactionRelationship[] reactionRelationships;
    int[] numberOfParticles; //By particle type index
    private double[][][] particlePositions;
    private double[][][] particleVelocities;

    private SelectedStatistics<ParticleType> statsByType = null;
    private Map<ParticleType, TypeDescriptor> typeMap = null;

    private ArenaType arenaType;
    
    private double arenaXSize;
    private double arenaYSize;
    private double arenaZSize;
    
    private int dimension;

    public SimSettingsController(edu.byu.chem.boltzmann.controller.Controller rootController, SimSettingsView view) {
        this.rootController = rootController;
        this.view = view;
    }

    private void setupSimulationInfo() {
        List<TypeDescriptor> typeDescriptors = view.getTypeDescriptors();

        List<ParticleType> types = getAllTypes(typeDescriptors);

        Map<TypeDescriptor, ParticleType> typeDescriptorMap =
                new HashMap<TypeDescriptor, ParticleType>(types.size());
        typeMap = new HashMap<ParticleType, TypeDescriptor>();
        
        for (int i = 0; i < types.size(); i++) {
            ParticleType type = types.get(i);
            TypeDescriptor typeDesc = typeDescriptors.get(i);
            typeDescriptorMap.put(typeDesc, type);
            typeMap.put(type, typeDesc);
        }

        SelectedStatistics<TypeDescriptor> selectedStats = view.getSelectedStats();
        statsByType = SelectedStatistics.mapSelection(selectedStats, typeDescriptorMap);

        particleTypes = types.toArray(new ParticleType[types.size()]);
        numberOfParticles = new int[types.size()];

        for (int i = 0; i < types.size(); i++) {
            numberOfParticles[i] = typeDescriptors.get(i).getNumParticles();
        }

        if (isReactionModeSelected()) {
            double particle0To1Energy = view.getForwardEnergy();
            double particle1To0Energy = view.getReverseEnergy();
            boolean suppressReverseReaction = view.shouldSuppressRevReaction();
            ReactionRelationship particle0To1Relationship = 
                    new ReactionRelationship(
                    particleTypes[0], 
                    particleTypes[1], 
                    particle0To1Energy, 
                    particle1To0Energy, 
                    false, 
                    suppressReverseReaction);
            reactionRelationships = new ReactionRelationship[] { particle0To1Relationship };
        } else {
            reactionRelationships = new ReactionRelationship[0];
        }

        arenaType = view.getArenaType();
        
        arenaXSize = view.getArenaXSize();
        arenaYSize = view.getArenaYSize();
        arenaZSize = view.getArenaZSize();

        dimension = view.getDimension();
        
        //Make sure there aren't too many particles for the occupiable volume
        limitTotalParticles();
        
        randomlyPlaceParticles();
        assignRandomVelocities();
    }
    
    private void limitTotalParticles() {
        double totalNeededVolume = getTotalNeededVolume();
        
        double availableVolume = getAvailableVolume();
        
        double excessParticleVolume = totalNeededVolume  - availableVolume;
        
        if (excessParticleVolume > 0.0) {
            reduceParticleVolume(excessParticleVolume);
        }
    }
    
    private void reduceParticleVolume(double excessParticleVolume) {
        double volumeEliminated = 0.0;
        int greatestRadiusType;
        
        while (true) {
            
            greatestRadiusType = findGreatestRadiusType();
            
            double particleVolume = 0.0;
            double particleDiameter = 2.0 * particleTypes[greatestRadiusType].particleRadius;
            switch (dimension) {
                case 1:
                    particleVolume = particleDiameter;
                    break;
                case 2:
                    particleVolume = particleDiameter * particleDiameter;
                    break;
                case 3:
                    particleVolume = particleDiameter * particleDiameter * particleDiameter;
            }
            
            while ((volumeEliminated < excessParticleVolume) &&
                    numberOfParticles[greatestRadiusType] > 0) {
                numberOfParticles[greatestRadiusType]--;
                volumeEliminated += particleVolume;
            }
            
            if (volumeEliminated >= excessParticleVolume) {
                break;
            }
        }
    }
    
    private int findGreatestRadiusType() {
        int greatestRadiusType = 0;
        double greatestRadius = 0.0;
        
        for (int i = 0; i < particleTypes.length; i++) {
            if ((particleTypes[i].particleRadius > greatestRadius) &&
                    (numberOfParticles[i] > 0)) {
                
                greatestRadiusType = i;
                greatestRadius = particleTypes[i].particleRadius;  
            }
        }
        
        return greatestRadiusType;
    }
    
    private double getAvailableVolume() {
        double arenaX = arenaXSize;
        if(arenaType == ArenaType.DIVIDED_ARENA || arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE)
            arenaX = arenaX / 2 - SimulationInfo.ARENA_DIVIDER_RADIUS;
        switch (dimension) {
            case 1:
                return arenaX;
            case 2:
                return arenaX * arenaYSize;
            case 3:
                return arenaX * arenaYSize * arenaZSize;
            default:
                return 0.0;
        }
    }
    
    /**
     * Needed volume is returned in units that depend on the dimension.
     * 1D = meters
     * 2D = meters^2
     * 3D = meters^3
     */
    private double getTotalNeededVolume() {
        double biggestDiameter = 0.0;
        double totalParticleVolume = 0.0;
        
        // For one dimension sum the diameters of all particles
        for(int i = 0; i < particleTypes.length; i++) {
            ParticleType type = particleTypes[i];
            
            double diameter = 2.0 * type.particleRadius;
            if (diameter > biggestDiameter) {
                biggestDiameter = diameter;
            }
            
            double additionForType = 0.0;
            switch (dimension) {
                case 1:
                    additionForType = diameter;
                    break;
                case 2:
                    additionForType = diameter * diameter;
                    break;
                case 3:
                    additionForType = diameter * diameter * diameter;
            }
            
            totalParticleVolume += additionForType * numberOfParticles[i];
        }
        
        //Make sure there's enough room for the particles to move
        switch (dimension) {
            case 1:
                totalParticleVolume += 2.0 * biggestDiameter;
                break;
            case 2:
                totalParticleVolume += 2.0 * biggestDiameter * arenaXSize;
                break;
            case 3:
                totalParticleVolume += 2.0 * biggestDiameter * arenaXSize * arenaYSize;
        }
        
        if (getTotalNumParticles() == 0) {
            return 0.0;
        }
        
        return totalParticleVolume;
    }
    
    private int getTotalNumParticles() {
        int totalParticles = 0;
        
        for (int i = 0; i < particleTypes.length; i++) {
            totalParticles += numberOfParticles[i];
        }
        
        return totalParticles;
    }

    private Map<TypeDescriptor, ParticleType> getTypeMap(List<TypeDescriptor> descriptors,
            List<ParticleType> types) {

        Map<TypeDescriptor, ParticleType> typeMap =
                new HashMap<TypeDescriptor, ParticleType>(types.size());

        for (int i = 0; i < types.size(); i++) {
            typeMap.put(descriptors.get(i), types.get(i));
        }

        return typeMap;
    }

    private List<ParticleType> getAllTypes(List<TypeDescriptor> typeDescriptors) {

        List<ParticleType> types = new ArrayList<ParticleType>(typeDescriptors.size());

        for (TypeDescriptor descriptor: typeDescriptors) {
            types.add(descriptor.getTypeDescribed());
        }

        return types;
    }

    private void randomlyPlaceParticles() {
        particlePositions = new double[particleTypes.length][][];
        
        double leftArenaMin = 0.0;
        double leftArenaMax = arenaXSize / 2.0 - SimulationInfo.ARENA_DIVIDER_RADIUS;
        double rightArenaMin = arenaXSize / 2.0 + SimulationInfo.ARENA_DIVIDER_RADIUS;
        double rightArenaMax = arenaXSize;
        
        double currentXMin = leftArenaMin;
        double currentXMax = rightArenaMax;
            
        //Put all particles on one side so they can leak out of the hole
        if (arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
            currentXMin = leftArenaMin;
            currentXMax = leftArenaMax;
        }

        
        for (int typeIndex = 0; typeIndex < particleTypes.length; typeIndex++) {
            particlePositions[typeIndex] = new double[numberOfParticles[typeIndex]][];

            //Split up particle types between the two sections of the arena
            if (arenaType == ArenaType.DIVIDED_ARENA) {
                if (isEven(typeIndex)) {
                    currentXMin = leftArenaMin;
                    currentXMax = leftArenaMax;
                } else {
                    currentXMin = rightArenaMin;
                    currentXMax = rightArenaMax;
                }
            }
            
            double xMin = currentXMin + particleTypes[typeIndex].particleRadius;
            double xMax = currentXMax - particleTypes[typeIndex].particleRadius;
            double xSpread = xMax - xMin;
            
            double yMin = particleTypes[typeIndex].particleRadius;
            double yMax = arenaYSize - particleTypes[typeIndex].particleRadius;
            double ySpread = yMax - yMin;
            
            double zMin = particleTypes[typeIndex].particleRadius;
            double zMax = arenaZSize - particleTypes[typeIndex].particleRadius;
            double zSpread = zMax - zMin;

            for (int particleIndex = 0; particleIndex < numberOfParticles[typeIndex]; particleIndex++) {
                double x = Math.random() * xSpread + xMin;
                double y = Math.random() * ySpread + yMin;
                double z = Math.random() * zSpread + zMin;

                switch (dimension) {
                    case 1:
                        y = 0;
                        z = 0;
                        break;
                    case 2:
                        z = 0;
                }
                
                particlePositions[typeIndex][particleIndex] = new double[] { x, y, z };
            }
        }
    }
    
    private static boolean isEven(int number) {
        return (number % 2 == 0);
    }

    private void assignRandomVelocities() {
        particleVelocities = new double[particleTypes.length][][];
        double initialTemperature = view.getInitialTemperature();

        for (int typeIndex = 0; typeIndex < particleTypes.length; typeIndex++) {
            particleVelocities[typeIndex] = new double[numberOfParticles[typeIndex]][];
            double energy = Formulas.avgKineticEnergy(initialTemperature, dimension);
            double speed = Formulas.speed(energy, Units.convert("amu", "kg", particleTypes[typeIndex].particleMass));

            for (int particleIndex = 0; particleIndex < numberOfParticles[typeIndex]; particleIndex++) {
                double theta = (Math.random() * 2.0 * Math.PI);
                double phi = Math.acos(1 - 2 * Math.random());

                double xVelocity = 0.0, yVelocity = 0.0, zVelocity = 0.0;
                switch (dimension) {
                    case 1:
                        double dir = Math.cos(theta);
                        if (dir < 0.0) {
                            xVelocity = -speed;
                        } else {
                            xVelocity = speed;
                        }
                        break;
                    case 2:
                        xVelocity = Math.cos(theta) * speed;
                        yVelocity = Math.sin(theta) * speed;
                        break;
                    case 3:
                        xVelocity = (Math.sin(phi) * Math.cos(theta) * speed);
                        yVelocity = (Math.sin(phi) * Math.sin(theta) * speed);
                        zVelocity = Math.cos(phi) * speed;
                }

               particleVelocities[typeIndex][particleIndex] = new double[] { xVelocity, yVelocity, zVelocity };
            }
        }
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public int getDimension() {
        return view.getDimension();
    }

    public double getArenaXSize() {
        return view.getArenaXSize();
    }

    public double getArenaYSize() {
        return view.getArenaYSize();
    }

    public double getArenaZSize() {
        return view.getArenaZSize();
    }

    public double getHoleDiameter() {
        return view.getHoleDiameter();
    }
    
    @Override
    public boolean isMaxwellDemonModeSelected() {
        return view.isMaxwellDemonModeSelected();
    }

    public int getNumberOfParticleTypes() {
        return particleTypes.length;
    }

    public ParticleType getParticleType(int index) {
        return particleTypes[index];
    }

    public int getNumberOfParticles(int index) {
        return numberOfParticles[index];
    }

    public double[] getParticlePosition(int particleTypeIndex, int particleIndex) {
        return particlePositions[particleTypeIndex][particleIndex];
    }

    public double[] getParticleVelocity(int particleTypeIndex, int particleIndex) {
        return particleVelocities[particleTypeIndex][particleIndex];
    }

    public boolean isReactionModeSelected() {
        return view.isReactionModeSelected();
    }

    public int getNumberOfReactionRelationships() {
        return reactionRelationships.length;
    }

    public ReactionRelationship getReactionRelationship(int index) {
        return reactionRelationships[index];
    }

    public boolean areAttractiveInteractionsAllowed() {
        return view.areAttractiveInteractionsAllowed();
    }

    public double getRadiusOfInteractionMultiplier() {
        return view.getRadiusOfInteractionMultiplier();
    }

    public double getEnergyWellDepth(int index) {
        ParticleType type = particleTypes[index];
        TypeDescriptor descriptorForType = typeMap.get(type);
        return view.getWellDepth(descriptorForType);
    }

    public boolean includeAttractiveWall() {
        return view.includeAttractiveWall();
    }

    public WallEnum getAttractiveWall() {
        return view.getAttractiveWall();
    }

    public double getWallWellWidth() {
        return view.getWallWellWidth();
    }

    public double getWallWellDepth() {
        return view.getWallWellDepth();
    }

    public boolean includeHeatReservoir() {
        return view.includeHeatReservoir();
    }

    public double getInitialReservoirTemperature() {
        return view.getInitialReservoirTemperature();
    }

    public StatisticID[] getRecordedStatistics(Set<ParticleType> particleTypes) {
        Set<StatisticID> recordedStats = 
                statsByType.getSelectedStatistics(particleTypes);

        StatisticID[] recStatsArray = new StatisticID[recordedStats.size()];
        
        recordedStats.toArray(recStatsArray);
        
        return recStatsArray;
    }

    public void applySimulationSettings() {
        setupSimulationInfo();
        final SimulationInfo simInfo = new SimulationInfo(SimSettingsController.this);
        rootController.setSimulationInfo(simInfo);
    }

    public void hideView() {
        rootController.hideSimSettings();
    }
    
    public void setViewInfo(SimulationInfo simInfo) {
        view.setSimulationInfo(simInfo);
    }
}
