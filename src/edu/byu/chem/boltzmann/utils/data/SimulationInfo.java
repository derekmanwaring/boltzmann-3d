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
package edu.byu.chem.boltzmann.utils.data;

import edu.byu.chem.boltzmann.model.io.Load;
import edu.byu.chem.boltzmann.model.physics.ArenaInfo;
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.Units;
import java.util.*;

/**
 *
 * @author Derek Manwaring
 */
public class SimulationInfo {
    
    public enum Dimension {
        ONE,
        TWO,
        THREE
    }
    
    public static Dimension intToDimension(int dimension) {
        switch (dimension) {
            case 1:
                return Dimension.ONE;
            case 2:
                return Dimension.TWO;
            case 3:
                return Dimension.THREE;
            default:
                throw new RuntimeException("Invalid dimension " + dimension);
        }
    }
    
    public static int dimensionToInt(Dimension dimension) {
        switch (dimension) {
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            default:
                throw new RuntimeException("Invalid dimension " + dimension);
        }
    }

    public enum WallEnum {
        FRONT,
        BACK,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum ArenaType {
        PERIODIC_BOUNDARIES,
        REFLECTING_BOUNDARIES,
        DIVIDED_ARENA,
        DIVIDED_ARENA_WITH_HOLE,
        MOVABLE_PISTON
    }

    public static final double ARENA_DIVIDER_RADIUS = Units.convert("nm", "m", 1.0);
    public static final double PISTON_SHAFT_RADIUS = Units.convert("nm", "m", 1.0);
    public static final double PISTON_VALVE_RADIUS = Units.convert("nm", "m", 2.0);
    
    /**
     * Maximum allowed length of any dimension of the arena in meters
     */
    public static final double MAXIMUM_ARENA_SIDE_LENGTH = Units.convert("nm", "m", 500.0);
    
    /**
     * Maximum allowed initial temperature in Kelvin
     */
    public static final double MAXIMUM_INITIAL_TEMPERATURE = 1500.0;

    //Current arena/wall setup
    public final ArenaType arenaType;
    public final int dimension;
    //Arena sizes in meters. Set to -1.0 if not applicable in the current dimension
    public final double arenaXSize, arenaYSize, arenaZSize;
    //Diameter in meters of the hole in the dividing wall of the arena. Set to -1.0 if the
    //arena does not include a hole
    public final double holeDiameter;
    //Maxwell-demon mode - the arena hole can be opened and closed
    public final boolean maxwellDemonMode;

    //Array of particle types
    private final ParticleType[] particleTypes;
    //Datasets mapping particle types to a list containing information for each
    //individual particle.
    //x, y, z coords of position in meters (x only if one dimension, x and y if two)
    private final Map<ParticleType, List<double[]>> particlePositions;
    //x, y, z compontents in meters/second of velocity vector
    private final Map<ParticleType, List<double[]>> particleVelocities;
    
    public final int totalNumParticles;
    public final double totalInitialKE;
    public final double totalMassOfParticles;
    public final double minParticleMass;

    //True if reactions can be preformed in this simulation
    public final boolean reactionMode;
    //Dataset mapping pairs of particle types to their reaction relationship data
    private final Map<Set<ParticleType>, ReactionRelationship> reactionRelationships;

    public final boolean attractiveParticleInteractions;
    //The multiplier to find the radius of interaction for a pair of particles.
    //This is lambda in Dr. Shirt's papers describing square well interactions.
    //The radius of interaction (where well collisions occur) is λ(radius1 + radius2)
    public final double radiusOfInteractionMultiplier;
    //Dataset mapping particle types to their energy well depths in kilojoules/mole
    private final Map<ParticleType, Double> energyWellDepths;

    public final boolean includeAttractiveWall;
    public final WallEnum attractiveWall;
    public final double wallWellWidth;
    public final double wallWellDepth;
    public final boolean includeHeatReservoir;

    private final HashMap<Set<ParticleType>, StatisticID[]> recordedStatistics;

    public final double initialTemperature;
    
    //Used by the particles for easier collision prediction with precalculated boundary
    //info.
    public final ArenaInfo arenaInfo;

    public double maxPistonLevel;
    
    private double heatReservoirTemperature;

    public SimulationInfo(){
        arenaInfo = new ArenaInfo();
        this.initialTemperature = 0;
        this.maxPistonLevel = 0;

        dimension = 0;
        arenaXSize = 0;
        arenaYSize = 0;
        arenaZSize = 0;
        holeDiameter = 0;
        maxwellDemonMode = false;
        this.arenaType = null;
        this.particlePositions = null;
        this.particleVelocities = null;
        this.reactionMode = false;
        this.reactionRelationships = null;
        this.attractiveParticleInteractions = false;
        this.radiusOfInteractionMultiplier = 0.0;
        this.energyWellDepths = null;
        this.includeAttractiveWall = false;
        this.attractiveWall = null;
        this.wallWellWidth = 0.0;
        this.wallWellDepth = 0.0;
        this.includeHeatReservoir = false;
        this.heatReservoirTemperature = 0.0;

        this.particleTypes = new ParticleType[0];
        this.recordedStatistics = null;
        
        totalNumParticles = 0;
        totalInitialKE = 0.0;
        totalMassOfParticles = 0.0;
        minParticleMass = 0.0;
    }

    /**
     * Creates a new experiment info object based on data supplied by <code>infoSource</code>
     * @param infoSource implementation of <code>SimulationInfoSupplier</code> with
     * access to needed information
     */
    public SimulationInfo(SimulationInfoSupplier infoSource){
        //Get info about the arena
        this.arenaType = infoSource.getArenaType();
        //Get the hole size if there is a hole
        if (arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
            this.holeDiameter = infoSource.getHoleDiameter();
            this.maxwellDemonMode = infoSource.isMaxwellDemonModeSelected();
        } else {
            this.holeDiameter = -1.0;
            this.maxwellDemonMode = false;
        }
        
        this.dimension = infoSource.getDimension();
        
        //Get arena measurments depending on the dimension
        this.arenaXSize = infoSource.getArenaXSize();
        if (dimension > 1) {
            this.arenaYSize = infoSource.getArenaYSize();
            if (dimension > 2) {
                this.arenaZSize = infoSource.getArenaZSize();
            } else {
                this.arenaZSize = -1.0;
            }
        } else {
            this.arenaYSize = -1.0;
            this.arenaZSize = -1.0;
        }
        
        this.arenaInfo = new ArenaInfo(this);

        //Setup the maps for positions and velocities after finding out how many
        //types of particles there are
        int numberOfParticleTypes = infoSource.getNumberOfParticleTypes();
        this.particleTypes = new ParticleType[numberOfParticleTypes];
        this.particlePositions = new HashMap<ParticleType, List<double[]>>(numberOfParticleTypes);
        this.particleVelocities = new HashMap<ParticleType, List<double[]>>(numberOfParticleTypes);

        //Loop through the different particle types adding their characteristics
        //and lists of the particles of that type to the maps created
        for (int typeIndex = 0; typeIndex < numberOfParticleTypes; typeIndex++) {
            ParticleType thisType = infoSource.getParticleType(typeIndex);
            particleTypes[typeIndex] = thisType;
            int numberOfParticles = infoSource.getNumberOfParticles(typeIndex);

            //Gather info for each particle's position and velocity
            List<double[]> positions = new ArrayList<double[]>(numberOfParticles);
            List<double[]> velocities = new ArrayList<double[]>(numberOfParticles);
            for (int particleIndex = 0; particleIndex < numberOfParticles; particleIndex++) {
                double[] position = infoSource.getParticlePosition(typeIndex, particleIndex);
                positions.add(position);

                double[]velocity = infoSource.getParticleVelocity(typeIndex, particleIndex);
                velocities.add(velocity);
            }

            //Add completed particle lists to the maps
            particlePositions.put(thisType, positions);
            particleVelocities.put(thisType, velocities);
        }

        // Calculate what the initial temperature is
        double totalKE = 0.0;
        int totalParticles = 0;
        for (int i = 0; i < particleTypes.length; i++) {
            ParticleType type = particleTypes[i];
            List<double[]> velocities = particleVelocities.get(type);
            totalKE += Load.calculateTotalKE(type, velocities);
            totalParticles += velocities.size();
        }
        initialTemperature = Formulas.temperature(totalKE / totalParticles, dimension);
        totalNumParticles = totalParticles;
        totalInitialKE = totalKE;

        //Store information for reactions if reaction mode is enabled
        this.reactionMode = infoSource.isReactionModeSelected();
        if (reactionMode) {
            int numberOfReactionRelationships = infoSource.getNumberOfReactionRelationships();
            this.reactionRelationships = 
                    new HashMap<Set<ParticleType>, ReactionRelationship>(numberOfReactionRelationships);

            //Loop through provided reaction relationships and map them to their involved particle types
            for (int relationshipIndex = 0; relationshipIndex < numberOfReactionRelationships; relationshipIndex++) {
                ReactionRelationship reactionRelationship = infoSource.getReactionRelationship(relationshipIndex);
                ParticleType[] involvedTypes = new ParticleType[] { reactionRelationship.particleType1, reactionRelationship.particleType2 };
                Set<ParticleType> invovledTypesSet = new HashSet<ParticleType>(Arrays.asList(involvedTypes));
                reactionRelationships.put(invovledTypesSet, reactionRelationship);
            }
        } else { //No reactions
            this.reactionRelationships = null;
        }

        //Establish conditions for particle interactions (well collisions)
        this.attractiveParticleInteractions = infoSource.areAttractiveInteractionsAllowed();
        if (attractiveParticleInteractions) {
            this.radiusOfInteractionMultiplier = infoSource.getRadiusOfInteractionMultiplier();

            //Store well depth (kilojoules/mole) for each particle type
            this.energyWellDepths = new HashMap<ParticleType, Double>(numberOfParticleTypes);
            for (int typeIndex = 0; typeIndex < numberOfParticleTypes; typeIndex++) {
                ParticleType particleType = infoSource.getParticleType(typeIndex);
                double energyWellDepth = infoSource.getEnergyWellDepth(typeIndex);
                energyWellDepths.put(particleType, energyWellDepth);
            }
        } else { //No particle interactions
            this.radiusOfInteractionMultiplier = -1.0;
            this.energyWellDepths = null;
        }

        //Store information about the attractive wall if there is one
        this.includeAttractiveWall = infoSource.includeAttractiveWall();
        if (includeAttractiveWall) {
            this.attractiveWall = infoSource.getAttractiveWall();
            this.wallWellWidth = infoSource.getWallWellWidth();
            this.wallWellDepth = infoSource.getWallWellDepth();

            //Add the info about the attractive wall's heat reservoir if active
            this.includeHeatReservoir = infoSource.includeHeatReservoir();
            if (includeHeatReservoir) {
                this.heatReservoirTemperature = infoSource.getInitialReservoirTemperature();
            } else { //No heat reservoir
                this.heatReservoirTemperature = -1.0;
            }
        } else { //No attractive wall
            this.attractiveWall = null;
            this.wallWellWidth = -1.0;
            this.wallWellDepth = -1.0;
            this.includeHeatReservoir = false;
            this.heatReservoirTemperature = -1.0;
        }

        //Store which statistics should be calculated
        recordedStatistics = new HashMap<Set<ParticleType>, StatisticID[]>(numberOfParticleTypes);
        for (Set<ParticleType> currentTypes: Formulas.getAllCombinations(
                new HashSet<ParticleType>(Arrays.asList(particleTypes)))) {
            recordedStatistics.put(currentTypes, infoSource.getRecordedStatistics(currentTypes));
        }
        
        double minMass = Double.MAX_VALUE;
        double totalMass = 0.0;
        for (ParticleType type: particleTypes) {
            totalMass += (type.particleMass * particlePositions.get(type).size());
            
            if (type.particleMass < minMass) {
                minMass = type.particleMass;
            }
        }
        totalMassOfParticles = totalMass;
        minParticleMass = minMass;
    }

    public boolean isPeriodic() {
        return (arenaType == ArenaType.PERIODIC_BOUNDARIES);
    }
    
    public boolean isPiston() {
        return (arenaType == ArenaType.MOVABLE_PISTON);
    }

    public List<ParticleType> getParticleTypes() {
        return new ArrayList<ParticleType>(Arrays.asList(particleTypes));
    }

    public int getNumberOfParticles(ParticleType particleType) {
        return particlePositions.get(particleType).size();
    }

    public double[] getParticlePosition(ParticleType type, int particleIndex) {
        double[] position = particlePositions.get(type).get(particleIndex);
        return Arrays.copyOf(position, position.length);
    }

    public double[] getParticleVelocity(ParticleType type, int particleIndex) {
        double[] velocity = particleVelocities.get(type).get(particleIndex);
        return Arrays.copyOf(velocity, velocity.length);
    }

    /** 
     * @param particleType The type of particle referred to
     * @return The established energy well depth in kilojoules/mole for the given
     * particle type.
     */
    public double getEnergyWellDepth(ParticleType particleType) {
        return energyWellDepths.get(particleType);
    }

    /**
     * For retrieving information about the reaction relationship between two particle types
     * @param reactingType1
     * @param reactingType2
     * @return
     */
    public ReactionRelationship getReactionRelationship(ParticleType reactingType1, ParticleType reactingType2) {
        Set<ParticleType> reactingTypes = new HashSet<ParticleType>(Arrays.asList(reactingType1, reactingType2));
        return reactionRelationships.get(reactingTypes);
    }

    public ReactionRelationship getReactionRelationship(Set<ParticleType> reactingTypes) {
        return reactionRelationships.get(reactingTypes);
    }

    /**
     * @return Statistics that should be recorded for this type
     */
    public Set<StatisticID> getStatistics(Set<ParticleType> types) {
        Set<StatisticID> statisticsForType = EnumSet.noneOf(StatisticID.class);
        statisticsForType.addAll(Arrays.asList(recordedStatistics.get(types)));
        return statisticsForType;
    }

    /**
     * @return Statistics that should be recorded for at least one of the sets of types
     */
    public Set<StatisticID> getAllStatistics() {
        Set<StatisticID> allStatistics = EnumSet.noneOf(StatisticID.class);

        for (StatisticID[] statistics: recordedStatistics.values()) {
            allStatistics.addAll(Arrays.asList(statistics));
        }
        
        return allStatistics;
    }

    public synchronized double getHeatReservoirTemperature() {
        return heatReservoirTemperature;
    }

    public synchronized void setHeatReservoirTemperature(double heatReservoirTemperature) {
        this.heatReservoirTemperature = heatReservoirTemperature;
    }
}
