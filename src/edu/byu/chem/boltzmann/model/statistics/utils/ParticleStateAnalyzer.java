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

import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * 21 May 2012
 */
public class ParticleStateAnalyzer {
    
    /**
     * Used for call-backs to statistics from ParticleStateAnalyzer.
     */
    public static abstract class ParticleStateWatcher {
        
        private final Set<ParticleType> typesWatched;
        private final boolean reactionMode;
        private final Set<Color> colorsWatched;
        
        public ParticleStateWatcher(Set<ParticleType> typesWatched, boolean reactionMode) {
            this.typesWatched = typesWatched;
            this.reactionMode = reactionMode;
            colorsWatched = new HashSet<Color>(typesWatched.size());
            for (ParticleType type : typesWatched) {
                colorsWatched.add(type.defaultColor);
            }
        }
        
//        /**
//         * Notifies the watcher of a particle that was in a state for an amount
//         * of time. The time is the difference in time between two collisions
//         * tracked by the ParticleStateAnalyzer.
//         * @param state
//         * @param timeElapsedInState 
//         */
//        public abstract void update(PartState state, double timeElapsedInState);
        public abstract void update(Particle particle, double timeElapsedInState);
        
//        /**
//         * Notifies the watcher of the state a particle was in for a frame analyzed
//         * by the ParticleStateAnalyzer.
//         * @param particleState 
//         */
//        public abstract void update(PartState particleState);
        public abstract void update(Particle particle);
        
        /**
         * @param particleType
         * @return True if the collisions should be tracked for this particle type
         */
        public boolean shouldTrackCollisions(ParticleType particleType) {
            return (typesWatched.contains(particleType) || reactionMode);
        }
        
        /**
         * @param particleColor
         * @return True if update should be called for this particle color
         */
        public boolean shouldRecordStats(Color particleColor) {
            return (colorsWatched.contains(particleColor));
        }
        
    }
    
    private final ParticleStateWatcher watcher;
    private final boolean trackAllEvents;
    private final int eventTypeToTrack;
    
    private Map<Particle, Double> lastCollisionTimes = new HashMap<Particle, Double>();
    
    public ParticleStateAnalyzer(ParticleStateWatcher watcher) {
        this.watcher = watcher;
        trackAllEvents = true;
        eventTypeToTrack = -1;
    }
    
    public ParticleStateAnalyzer(ParticleStateWatcher watcher, int eventTypeToTrack) {
        this.watcher = watcher;
        trackAllEvents = false;
        this.eventTypeToTrack = eventTypeToTrack;
    }
    
    public double getLastCollisionTime(Particle particle) {
        if (lastCollisionTimes.containsKey(particle)) {
            return lastCollisionTimes.get(particle);
        } else {
            return 0.0;
        }
    }

    public void reset() {
        lastCollisionTimes.clear();
    }
    
//    private final PartState workingState = new PartState(0, 0, 0, 0, null, 0);
    
    public void analyzeEvent(EventInfo event) {
        if (trackAllEvents || event.colType == eventTypeToTrack) {
            for (Particle particle: event.getInvolvedParticles()) {
                if (watcher.shouldTrackCollisions(particle.particleType)) {
                    Double lastCollisionTime = getLastCollisionTime(particle);
                    if (lastCollisionTime == null)
                        lastCollisionTime = 0.0;
                    //PartState particleState = particle.getState(workingState);
                    
                    ////If we're in reaction mode, check the color
                    
                    //if (watcher.shouldRecordStats(particleState.color)) {
                    if (watcher.shouldRecordStats(particle.getDisplayColor())) {
                        double timeElapsed = event.colTime - lastCollisionTime;
                        //watcher.update(particleState, timeElapsed);
                        watcher.update(particle, timeElapsed);
                    }
                    lastCollisionTimes.put(particle, event.colTime);
                }
            }
        }
    }

    public void analyzeFrame(FrameInfo frame) {
        for (Particle particle: frame.getParticles()) {
            //PartState particleState = frame.getParticleState(particle);
            //if (watcher.shouldRecordStats(particleState.color)) {
            if (watcher.shouldRecordStats(particle.getDisplayColor())) {
                //watcher.update(particleState);
                watcher.update(particle);
            }
        }
    }
}
