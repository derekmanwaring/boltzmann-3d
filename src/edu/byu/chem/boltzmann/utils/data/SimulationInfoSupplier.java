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

import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import java.util.Set;


/**
 *
 * @author Derek Manwaring
 */
public interface SimulationInfoSupplier {

    public ArenaType getArenaType();
    public int getDimension();

    /**
     * @return X length of the arena in nanometers
     */
    public double getArenaXSize();

    /**
     * @return Y length of the arena in nanometers. Undefined behavior if one dimensional
     */
    public double getArenaYSize();

    /**
     * @return Z length of the arena in nanometers. Undefined behavior if <code>getDimension()</code> is less than 3
     */
    public double getArenaZSize();

    /**
     * @return Diameter of the hole in the dividing wall of the arena in nanometers. Undefined behavior
     * if there is no hole in the arena
     */
    public double getHoleDiameter();

    /**
     * @return Whether Maxwell-demon is selected. On means the boundary hole can be opened
     * and closed by the user.
     */
    public boolean isMaxwellDemonModeSelected();

    /**
     * @return The number of particle types described in this info getter's source
     */
    public int getNumberOfParticleTypes();

    /**
     * For retrieving the characteristics of a distinct group of particles
     * @param index Identifies the wanted particle type's index as if in an array.
     * @return A <code>ParticleType</code> object which describes a group of particles
     * of size <code>getNumberOfParticles(index)</code>
     */
    public ParticleType getParticleType(int index);
    
    /**
     * For retrieving the number of particles of a certain type
     * @param index Index of the particle type
     * @return The number of particles of type <code>getParticleType(index)</code>
     */
    public int getNumberOfParticles(int index);

    /**
     * For retrieving coordinates for a certain particle's position
     * @param particleTypeIndex Index of the particle type
     * @param particleIndex Index of the particle
     * @return x, y, z coordinates of the particle in nanometers. If the current arena setup is
     * one dimensional only an x coordinate is returned. Two dimensional returns x, y
     */
    public double[] getParticlePosition(int particleTypeIndex, int particleIndex);

    /**
     * For retrieving components for a certain particle's velocity
     * @param particleTypeIndex Index of the particle type
     * @param particleIndex Index of the particle
     * @return x, y, z components of the particle's velocity in meters/second. If the current arena
     * setup is one dimensional only an x component is returned. Two dimensional returns x, y
     */
    public double[] getParticleVelocity(int particleTypeIndex, int particleIndex);

    public boolean isReactionModeSelected();

    /**
     * @return The number of reaction relationships described in this getter's source
     */
    public int getNumberOfReactionRelationships();

    /**
     * For retrieving information about reactions described in this getter's source
     * @param index Index of the desired reaction relationship
     * @return 
     */
    public ReactionRelationship getReactionRelationship(int index);

    /**
     * @return <code>true</code> if this simulation should do attractive interactions
     * (well collisions between particles)
     */
    public boolean areAttractiveInteractionsAllowed();

    /**
     * @return The multiplier to find the radius of interaction for a pair of particles.
     * This is lambda in Dr. Shirt's papers describing square well interactions.
     * The radius of interaction (where well collisions occur) is λ(radius1 + radius2)
     */
    public double getRadiusOfInteractionMultiplier();

    /**
     * @param index Index of the particle type referred to
     * @return The energy well depth in kilojoules/mole for the particle type <code>
     * getParticleType(index)</code>
     */
    public double getEnergyWellDepth(int index);

    /**
     * @return <code>true</code> if this simulation should have an attractive wall
     */
    public boolean includeAttractiveWall();

    /**
     * @return The wall to be used as the attractive wall
     */
    public WallEnum getAttractiveWall();

    /**
     * @return The width of the well for the attractive wall in nanometers
     */
    public double getWallWellWidth();

    /**
     * @return The depth of the well for the attractive wall in kilojoules/mole
     */
    public double getWallWellDepth();

    /**
     * @return <code>true</code> if this simulation's attractive wall also has a heat reservoir
     */
    public boolean includeHeatReservoir();

    /**
     * @return The initial temperature in Kelvin of the attractive wall's heat reservoir
     */
    public double getInitialReservoirTemperature();

    /**
     * @return The statistics that should be recorded for the given particle type
     */
    public StatisticID[] getRecordedStatistics(Set<ParticleType> particleTypes);
}
