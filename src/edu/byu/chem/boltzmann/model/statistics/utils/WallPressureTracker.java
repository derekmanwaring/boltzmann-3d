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

package edu.byu.chem.boltzmann.model.statistics.utils;

import edu.byu.chem.boltzmann.model.physics.Collision;
import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.physics.Wall;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.PressureUnit;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Joshua Olson
 * July 19, 2012
 */
public class WallPressureTracker {
    
    private class CollisionEvent{
        public final double momentum, time;
        public CollisionEvent(double m, double t){
            momentum = m;
            time = t;
        }
    }
    
    private List<Map<ParticleType, LinkedList<CollisionEvent>>> wallCollisions;//list has two maps, one for each wall (left or right)
    private final int LEFT = 0, RIGHT = 1, SIDES[] = {0, 1};//for left and right wall collisions
    
    private double simulationTime, trackedTime, averagingTime;
    private final double wallHeight, wallDepth;
    private final boolean reactionMode;
    private final Set<ParticleType> typesWatched;
    private final Set<Color> colorsWatched;
    private final PressureUnit baseUnit;

    public WallPressureTracker(PressureUnit defaultUnit, double avgTime, Set<ParticleType> types, SimulationInfo simInfo){
        baseUnit = defaultUnit;
        simulationTime = 0;
        trackedTime = 0;
        averagingTime = Units.convert("ps", "s", avgTime);
        wallHeight = simInfo.arenaYSize;
        wallDepth = simInfo.arenaZSize;
        reactionMode = simInfo.reactionMode;
        typesWatched = types;
        colorsWatched = new HashSet<Color>(typesWatched.size());
        for(ParticleType type: typesWatched)
            colorsWatched.add(type.defaultColor);
        
        wallCollisions = new ArrayList<Map<ParticleType, LinkedList<CollisionEvent>>>(2);
        for(int side: SIDES){
            wallCollisions.add(new HashMap<ParticleType, LinkedList<CollisionEvent>>(types.size()));
            for(ParticleType type: types)
                wallCollisions.get(side).put(type, new LinkedList<CollisionEvent>());
        }
    }
    
    public void setSimulationTime(double simTime){
        //increase trackedTime by the increase in simulation time unless the cap at averagingTime is reached
        trackedTime = Math.min(averagingTime, trackedTime + simTime - simulationTime);
        simulationTime = simTime;
    }
    
    public void setAveragingTime(double avgTime){
        trackedTime = Math.min(avgTime, trackedTime);
        averagingTime = avgTime;
    }
    
    public void analyzeEvent(EventInfo event){
        if(event.colType == Collision.WALL){
            Particle particle = event.getInvolvedParticles()[0];
            if(shouldTrackCollisions(particle.particleType) && shouldRecordStats(particle.getDisplayColor()) && shouldTrackSide(event.side)){
                double momentum = 2 * Units.convert("amu", "kg", particle.mass) * Math.abs(particle.getXVel());
                if(wallDepth > 0)//three dimensions
                    momentum /= (wallHeight - 2 * particle.radius) * (wallDepth - 2 * particle.radius);
                else if(wallHeight > 0)//two dimensions
                        momentum /= wallHeight - 2 * particle.radius;
                CollisionEvent colEvent = new CollisionEvent(momentum, event.colTime);
                wallCollisions.get(event.side == Wall.LEFT ? LEFT : RIGHT).get(particle.particleType).addLast(colEvent);
            }
        }
    }
    
    public boolean shouldTrackCollisions(ParticleType particleType){
        return typesWatched.contains(particleType) || reactionMode;
    }

    //If we're in reaction mode, check the color
    public boolean shouldRecordStats(Color particleColor){
        return colorsWatched.contains(particleColor);
    }
    
    public boolean shouldTrackSide(int side){
        return side == Wall.LEFT || side == Wall.RIGHT;
    }

    public double getAverage(PressureUnit unit){
        //double leftPressure = 0, rightPressure = 0;
        double pressure = 0;
        for(int side: SIDES)
            for(ParticleType type: typesWatched){
                LinkedList<CollisionEvent> collisions = wallCollisions.get(side).get(type);
                while(!collisions.isEmpty() && collisions.getFirst().time < simulationTime - trackedTime)
                    collisions.removeFirst();
                double totalMomentum = 0;
                for(CollisionEvent event: collisions)
                    totalMomentum += event.momentum;
//                double pressure = totalMomentum / trackedTime;
//                if(side == LEFT)
//                    leftPressure += pressure;
//                else
//                    rightPressure += pressure;
                pressure += totalMomentum / trackedTime;
            }
        //return Units.convert(baseUnit, unit, (leftPressure + rightPressure) / 2);
        return Units.convert(baseUnit, unit, 0.5 * pressure);
    }
    
    public void reset(){
        for(int side: SIDES)
            for(ParticleType type: typesWatched)
                wallCollisions.get(side).get(type).clear();
        simulationTime = 0;
        trackedTime = 0;
    }
}
