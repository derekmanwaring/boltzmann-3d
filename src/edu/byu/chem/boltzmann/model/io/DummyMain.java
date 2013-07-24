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
package edu.byu.chem.boltzmann.model.io;

import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.ReactionRelationship;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.WallEnum;
import edu.byu.chem.boltzmann.utils.data.SimulationInfoSupplier;
import edu.byu.chem.boltzmann.model.statistics.InstantaneousSpeed;
import edu.byu.chem.boltzmann.model.statistics.Path;
import edu.byu.chem.boltzmann.model.statistics.RMSVelocity;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import java.awt.Color;
import java.util.List;
import java.util.Set;

/**
 * Created August 2010 for temporary data storage for the load class
 * @author Derek Manwaring
 */
public class DummyMain implements SimulationInfoSupplier {
    public double xSize;
    public double ySize;
    public double zSize;
    public int holeSize;
    public boolean maxwellDemonMode = false;
    public boolean periodic;

    public boolean idealPressure = false;
    public boolean realPressure = false;
    public boolean traceMode = false;
    public int statisticParticleType;

    public double pressAvgTime;
    
    public int redNum;
    public int partCount;
    public int blueNum;

    public int dimension;
    public ParticleType redParticle;
    public ParticleType blueParticle;
    public List<double[]> redPositions;
    public List<double[]> bluePositions;
    public List<double[]> redVelocities;
    public List<double[]> blueVelocities;
    public ArenaType arenaType;
    
    public int tracePointSize;
    public Color red;
    public Color blue;
    public Color bg;
    public Color divColor;
    public Color left3DColor;
    public Color right3DColor;
    public Color top3DColor;
    public Color bottom3DColor;
    public Color front3DColor;
    public Color back3DColor;

    public int camAngleX;
    public int camAngleY;
    public boolean lighting;
    public boolean reactionMode;
    public double forgetMultiplier;

    public ArenaType getArenaType() {
        return arenaType;
    }

    public int getDimension() {
        return dimension;
    }

    public double getArenaXSize() {
        return Units.convert("nm", "m", xSize);
    }

    public double getArenaYSize() {
        return Units.convert("nm", "m", ySize);
    }

    public double getArenaZSize() {
        return Units.convert("nm", "m", zSize);
    }

    public double getHoleDiameter() {
        return Units.convert("nm", "m", holeSize);
    }
    
    public boolean isMaxwellDemonModeSelected() {
        return maxwellDemonMode;
    }

    public int getNumberOfParticleTypes() {
        if (!redPositions.isEmpty()) {
            if (!bluePositions.isEmpty()) {
                return 2;
            } else {
                return 1;
            }
        } else if (!bluePositions.isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public ParticleType getParticleType(int index) {
        ParticleType particleType = null;
        switch(index) {
            case 0:
                if (!redPositions.isEmpty()) {
                    particleType = redParticle;
                } else {
                    particleType = blueParticle;
                }
                break;
            case 1:
                particleType = blueParticle;
        }
        return particleType;
    }

    public int getNumberOfParticles(int index) {
        int particles = 0;
        switch(index) {
            case 0:
                if (!redPositions.isEmpty()) {
                    particles = redPositions.size();
                } else {
                    particles = bluePositions.size();
                }
                break;
            case 1:
                particles = bluePositions.size();
        }
        return particles;
    }

    public double[] getParticlePosition(int particleTypeIndex, int particleIndex) {
        double[] position = null;
        switch(particleTypeIndex) {
            case 0:
                if (!redPositions.isEmpty()) {
                    position = redPositions.get(particleIndex);
                } else {
                    position = bluePositions.get(particleIndex);
                }
                break;
            case 1:
                position = bluePositions.get(particleIndex);
        }
        return position;
    }

    public double[] getParticleVelocity(int particleTypeIndex, int particleIndex) {
        double[] velocity = null;
        switch(particleTypeIndex) {
            case 0:
                if (!redPositions.isEmpty()) {
                    velocity = redVelocities.get(particleIndex);
                } else {
                    velocity = blueVelocities.get(particleIndex);
                }
                break;
            case 1:
                velocity = blueVelocities.get(particleIndex);
        }
        return velocity;
    }

    public boolean isReactionModeSelected() {
        return false; //not working yet
    }

    public int getNumberOfReactionRelationships() {
        return 0; //not working yet
    }

    public ReactionRelationship getReactionRelationship(int index) {
        return null; //not working yet
    }

    public boolean areAttractiveInteractionsAllowed() {
        return false;
    }

    public double getRadiusOfInteractionMultiplier() {
        return -1.0;
    }

    public double getEnergyWellDepth(int index) {
        return -1.0;
    }

    public boolean includeAttractiveWall() {
        return false;
    }

    public WallEnum getAttractiveWall() {
        return null;
    }

    public double getWallWellWidth() {
        return -1.0;
    }

    public double getWallWellDepth() {
        return -1.0;
    }

    public boolean includeHeatReservoir() {
        return false;
    }

    public double getInitialReservoirTemperature() {
        return -1.0;
    }

    public StatisticID[] getRecordedStatistics(Set<ParticleType> particleTypes) {
        return new StatisticID[] { StatisticID.INSTANTANEOUS_SPEED };
    }
}
