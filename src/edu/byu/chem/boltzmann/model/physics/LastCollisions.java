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

/**
 * 
 * @author Derek Manwaring
 * 20 Sep 2011
 */
public class LastCollisions {
        
    private final EventInfo[][] lastCollisionsByParticlePair;
    private final int totalNumberOfParticles;
    private final EventInfo defaultEvent;
    
    public LastCollisions(int numParticles, EventInfo defaultEvent) {
        if (numParticles < 0) {
            throw new IllegalArgumentException("Negative number of particles specified");
        }
        
        totalNumberOfParticles = numParticles;
        this.defaultEvent = defaultEvent;
        
        if (totalNumberOfParticles != 0) {
            // We use one less row because particle 0 has now row - his collisions
            // are recorded in the other rows
            int numberOfRows = totalNumberOfParticles - 1;
            lastCollisionsByParticlePair = new EventInfo[numberOfRows][];

            for (int row = 0; row < (numberOfRows); row++) {
                lastCollisionsByParticlePair[row] = makeRow(row);
            }
        } else {
            lastCollisionsByParticlePair = new EventInfo[0][];
        }
        
        return;
    }
    
    /**
     * 
     * @param rowNumber The index of the row that will contain collisions between
     * the particle with index (rowNumber + 1) and (rowNumber + 1) other particles
     * @return 
     */
    private EventInfo[] makeRow(int rowNumber) {
        EventInfo[] thisRow = new EventInfo[rowNumber + 1];
        
        int thisRowParticleIndex = rowNumber + 1;
        for (int otherParticleIndex = 0; 
                otherParticleIndex < thisRowParticleIndex;
                otherParticleIndex++) {
            thisRow[otherParticleIndex] = new EventInfo(defaultEvent);
        }
        
        return thisRow;
    }
    
    public EventInfo getLastCollision(int particleIndex1, int particleIndex2) {
        checkParticleIndices(particleIndex1, particleIndex2);
        
        return getEventForParticlePair(particleIndex1, particleIndex2);
    }
    
    public void setLastCollision(int particleIndex1, int particleIndex2, EventInfo collision) {
        checkParticleIndices(particleIndex1, particleIndex2);
        
        if (collision == null) {
            throw new IllegalArgumentException("Last collision cannot be null");
        }
        
        EventInfo eventForParticlePair = getEventForParticlePair(particleIndex1,
                particleIndex2);
        
        eventForParticlePair.copy(collision);
        
        return;
    }
    
    private EventInfo getEventForParticlePair(int particleIndex1, int particleIndex2) {
        int rowParticle = Math.max(particleIndex1, particleIndex2);
        int columnParticle = Math.min(particleIndex1, particleIndex2);
        int rowIndex = rowParticle - 1;
        
        return lastCollisionsByParticlePair[rowIndex][columnParticle];
    }
    
    private void checkParticleIndices(int particleIndex1, int particleIndex2) {
        if (particleIndex1 == particleIndex2) {
            throw new IllegalArgumentException("Particle indices cannot be equal");
        }
        
        if (particleIndex1 < 0) {
            throw new IllegalArgumentException("First particle index is negative: " + 
                    particleIndex1);
        }
        
        if (particleIndex2 < 0) {
            throw new IllegalArgumentException("Second particle index is negative: " + 
                    particleIndex2);
        }
        
        if (particleIndex1 > (totalNumberOfParticles - 1)) {
            throw new IndexOutOfBoundsException("First particle index is too large: " + 
                    particleIndex1 + "\n"
                    + "(There are only " + totalNumberOfParticles + " particles");
        }
        
        if (particleIndex2 > (totalNumberOfParticles - 1)) {
            throw new IndexOutOfBoundsException("Second particle index is too large: " + 
                    particleIndex2 + "\n"
                    + "(There are only " + totalNumberOfParticles + " particles");
        }
    }
}
