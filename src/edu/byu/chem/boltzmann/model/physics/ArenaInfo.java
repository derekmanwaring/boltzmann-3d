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
package edu.byu.chem.boltzmann.model.physics;

import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Primarily for use in the Physics constructor so the GUI can specify the basic
 * arena setup.  Also used in communicating between classes inside Physics
 * @author Derek Manwaring
 */
public class ArenaInfo {
    
    public final double barrierLeftX;
    public final double barrierRightX;
    public final double holeRadius;
    public final double holeRadiusSquared;
    public final double edgeRadius;
    public final double edgeX;
    public final int numEdgePart;
    private final double[] edgeY;
    private final double[] edgeZ;

    private Map<ParticleType, Statistic[]> simulationStatistics;

    public ArenaInfo() {
        barrierLeftX = 0;
        barrierRightX = 0;
        holeRadius = 0;
        holeRadiusSquared = 0;
        edgeRadius = 0;
        edgeX = 0;
        edgeY = null;
        edgeZ = null;
        numEdgePart = 0;
    }

    /**
     *
     * @param xLen  x size in simulation units, not pixels or display units
     * @param yLen
     * @param zLen
     * @param periodic
     * @param divideStatus
     * @param dimension
     */
    public ArenaInfo(SimulationInfo simulationInfo) {
        // Stat Method to setup the arena variables

        //calculate boundary positions based on width and radius for performing and
        //predicting collisions

        barrierLeftX = 0.5 * (simulationInfo.arenaXSize) - SimulationInfo.ARENA_DIVIDER_RADIUS;
        barrierRightX = 0.5 * (simulationInfo.arenaXSize) + SimulationInfo.ARENA_DIVIDER_RADIUS;
        holeRadius = 0.5 * simulationInfo.holeDiameter + SimulationInfo.ARENA_DIVIDER_RADIUS;
        holeRadiusSquared = holeRadius * holeRadius;

        edgeRadius = SimulationInfo.ARENA_DIVIDER_RADIUS;
        edgeX = 0.5f*simulationInfo.arenaXSize;

        //setup the fixed particles that line the hole
        switch (simulationInfo.dimension) {
            case 2:
                numEdgePart = 2;
                edgeY = new double[numEdgePart];
                edgeZ = new double[numEdgePart];
                edgeY[0] = 0.5f * simulationInfo.arenaYSize + holeRadius;
                edgeY[1] = 0.5f * simulationInfo.arenaYSize - holeRadius;
                edgeZ[0] = 0;
                edgeZ[1] = 0;
                break;
            case 3:
                int numEdgePartCalculated = (int) Math.round(4*Math.PI*holeRadius/edgeRadius);
                if (numEdgePartCalculated < 0) {
                    numEdgePartCalculated = 0;
                }
                numEdgePart = numEdgePartCalculated;

                edgeY = new double[numEdgePart];
                edgeZ = new double[numEdgePart];

                double angStep = (2*Math.PI/numEdgePart);
                double ang=0;
                for (int i=0; i<numEdgePart; i++) {
                        //position relative to the hole center
                        edgeY[i] = (holeRadius*Math.cos(ang));
                        edgeZ[i] = (holeRadius*Math.sin(ang));
                        //adjust coord to match the arena's frame of reference
                        edgeY[i] += 0.5*simulationInfo.arenaYSize;
                        edgeZ[i] += 0.5*simulationInfo.arenaZSize;

                        ang += angStep;
                }
                break;
            default:
                numEdgePart = 0;
                edgeY = null;
                edgeZ = null;
                break;
        }
    }

//    public boolean CheckBoundaryOverlap(double x, double y, double z, double rad) {
//        // really only needed for reflecting conditions
//        if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
//            return false;
//        }
//
//        // reflecting conditions
//        boolean failed = false;
//
//        if (x<rad || (xLen-x)<rad) {
//            failed=true;
//        } else if ((dimension > 1) && (y < rad || (yLen - y) < rad)) {
//            failed=true;
//        } else if ((dimension > 2) && (z < rad || (zLen - z) < rad)) {
//            failed=true;
//        } else if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA ||
//                simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
//                //check overlap with the dividing barrier
//                if ((x+rad)>barrierLeftX && (x-rad)<barrierRightX)
//                        failed=true;
//
//                //double check that result if there's a hole in the barrier
//                if (failed && (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE)) {
//                        //1D - HOLE status is not allowed
//                        if (dimension > 1) {
//                                //first see if the particle is in the wall - if it is keep true
//                                double yPos=y-0.5f*yLen;
//                                double zPos=(dimension==2 ? 0 : z-0.5f*zLen);
//                                if (yPos*yPos+zPos*zPos >= holeRadiusSquared) { //in the wall
//                                        failed=true;
//                                }
//                                else { //possibly OK, check the edge particles
//                                        failed=false;
//                                        double dx2=(x-edgeX)*(x-edgeX), dy=0, dz=0, r2=(edgeRadius+rad)*(edgeRadius+rad);
//                                        for (int i=0; i<numEdgePart && !failed; i++) {
//                                                dy = y-edgeY[i];
//                                                dz = (dimension==2 ? 0 : z-edgeZ[i]);
//                                                if (dx2+dy*dy+dz*dz < r2)
//                                                        failed=true;
//                                        }
//                                }
//                        }
//                }
//        }
//
//        return failed;
//    }

    public double[] getEdgeY(){
        return Arrays.copyOf(edgeY, edgeY.length);
    }

    public double[] getEdgeZ() {
        return Arrays.copyOf(edgeZ, edgeZ.length);
    }

//    public Set<Statistic> getStatistics(ParticleType type) {
//        return new HashSet(Arrays.asList(simulationStatistics.get(type)));
//    }
}

