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
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Statistics whose values are recorded based on how long a particle was in a certain
 * state between particle-to-particle collisions.
 * @author Derek Manwaring
 */
public abstract class RMSTimeWeightedStatistic extends RMSWeightedValueStatistic {

    protected Map<Particle, Double> lastCollisionTimes = new HashMap<Particle, Double>();

    @Override
    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        super.initiateStatistic(types, physics);
        initCollisionTimes(physics.getParticles());
    }

    @Override
    protected void updateStatisticAvgs(FrameInfo frame) {
//        updateWeightedValues(frame);
    }

//    private void updateWeightedValues(FullFrameInfo frame) {
//        for (EventInfo event: frame.getEvents()) {
//            if (event.colType == Collision.PARTICLE) {
//                updateForParticles(event, frame);
//            }
//        }
//    }
//
//    private void updateForParticles(EventInfo event, FullFrameInfo frame) {
//        for (Particle particle: event.getInvolvedParticles()) {
//            if (shouldTrackCollisions(particle.particleType)) {
//                double lastCollisionTime = lastCollisionTimes.get(particle);
//                PartState particleState = frame.getParticleState(particle, Math.max(lastCollisionTime, frame.startTime));
//                //If we're in reaction mode, check the color
//                if (shouldRecordStats(particleState.color)) {
//                    double timeElapsed = event.colTime - lastCollisionTime;
//                    updateRecord(particleState, timeElapsed);
//                }
//                lastCollisionTimes.put(particle, event.colTime);
//            }
//        }
//    }

    protected boolean shouldTrackCollisions(ParticleType type) {
        return (types.contains(type) || simulationInfo.reactionMode);
    }

    private void initCollisionTimes(Particle[] particles) {
        for (Particle particle: particles) {
            if (!simulationInfo.reactionMode) {
                if (types.contains(particle.particleType)) {
                    lastCollisionTimes.put(particle, lastFrameEndTime);
                }
            } else { //Keep track of all particles because they can change colors
                lastCollisionTimes.put(particle, lastFrameEndTime);
            }
        }
    }

    @Override
    public void prepareInstCalculations(FrameInfo frame) {
        resetInstData();
        for (Particle particle: frame.getParticles()) {
            if (types.contains(particle.particleType) || simulationInfo.reactionMode) {
                PartState particleState = frame.getParticleState(particle);
                if (colorsRecorded.contains(particleState.color)) {
                    updateInstRecord(particleState);
                }
            }
        }
    }

    @Override
    public void reset(double newStartTime, Physics physics) {
        super.reset(newStartTime, physics);

        lastCollisionTimes = new HashMap<Particle, Double>();
        initCollisionTimes(physics.getParticles());
    }
    
    protected abstract void updateRecord(PartState particleState, double timeElapsed);

    protected abstract void updateInstRecord(PartState particleState);
}
