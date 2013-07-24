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
package edu.byu.chem.boltzmann.model.statistics.parents;

import edu.byu.chem.boltzmann.model.physics.Collision;
import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.utils.data.ParticleType;

/**
 * Statistics whose values are recorded based on how long a particle was in a certain
 * state between collisions.
 * @author Derek Manwaring
 */
public abstract class TimeWeightedAllCollisions extends TimeWeightedStatistic {
    
    @Override
    public void updateByEvent(EventInfo event) {      
        updateForParticles(event);
    }
    
    private final PartState workingState = new PartState(0, 0, 0, 0, null, 0);

    private void updateForParticles(EventInfo event) {
        for (Particle particle: event.getInvolvedParticles()) {
            /**
             * particle is sometimes null when XVelocity etc. statistics are loaded
             */
            if (particle != null && collisionIsValidAndShouldBeTracked(particle.particleType, event)) {
                double lastCollisionTime = lastCollisionTimes.get(particle);
                PartState particleState = particle.getState(workingState);
                //If we're in reaction mode, check the color
                if (shouldRecordStats(particleState.color)) {
                    double timeElapsed = event.colTime - lastCollisionTime;
                    updateRecord(particleState, timeElapsed);
                }
                lastCollisionTimes.put(particle, event.colTime);
            }
        }
    }
    
    private boolean collisionIsValidAndShouldBeTracked(ParticleType type, EventInfo collision) {
        return (shouldTrackCollisions(type) &&
                (collision.colType == Collision.PARTICLE || simulationInfo.isPeriodic()));
                // We check if simulation is using periodic boundaries because we want
                // to update on all collisions, but not collisions that are used as
                // flags when particles cross boundaries in periodic mode
    }
    
    protected abstract void updateRecord(PartState particleState, double timeElapsed);

    protected abstract void updateInstRecord(PartState particleState);
}
