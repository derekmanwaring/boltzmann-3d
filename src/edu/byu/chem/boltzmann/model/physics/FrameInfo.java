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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 */
public class FrameInfo {

    public final double startTime;
    public final double endTime;

    private double pistonStartPosition;
    private double pistonFinalPosition;
    private double pistonMovingTime;
    
    private boolean arenaHoleOpen = false;

    public FrameInfo(double startTime, double endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    private final Map<Particle, PartState> particleStates = new HashMap<Particle, PartState>();
        
    public void setEndingStates(Map<Particle, PartState> allParticles) {
        particleStates.putAll(allParticles);  
    }

    public Map<Particle, PartState> getParticleStates() {
        return particleStates;
    }

    public void setPistonStart(double pistonPosition) {
        this.pistonStartPosition = pistonPosition;
    }

    public void setPistonEnd(double pistonPosition, double time) {
        this.pistonFinalPosition = pistonPosition;
        this.pistonMovingTime = time - startTime;
    }

    public double getPistonPosition(double time) {
        if (time > (pistonMovingTime + startTime)) {
            return pistonFinalPosition;
        } else {
            double pistonVelocity = (pistonFinalPosition - pistonStartPosition) / pistonMovingTime;
                return pistonStartPosition + pistonVelocity * (time - startTime);
        }
    }

    public PartState getParticleState(Particle particle) {
        return particleStates.get(particle);
    }

    public Set<Particle> getParticles() {
        return particleStates.keySet();
    }

    public void setArenaHoleOpen(boolean holeOpen) {
        arenaHoleOpen = holeOpen;
    }
    
    public boolean isArenaHoleOpen() {
        return arenaHoleOpen;
    }
}
