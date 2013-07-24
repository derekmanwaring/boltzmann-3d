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
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author Derek Manwaring
 * 06 Jun 2012
 */
public abstract class IndividualParticleStatisticTracker {
    
    private final int numParticles;
    private final Set<ParticleType> typesWatched;
    private final Set<Color> colorsWatched;
    private final boolean reactionMode;
    private final int eventTypeToTrack;
    
    private Map<Particle, Double> sums, lastCollisionTimes;
    private Map<Particle, Integer> collisionCounter;
    
    public IndividualParticleStatisticTracker(int totalNumParticles, Set<ParticleType> typesWatched, boolean reactionMode, int eventTypeToTrack) {
        numParticles = totalNumParticles;
        sums = new HashMap<Particle, Double>(numParticles);
        lastCollisionTimes = new HashMap<Particle, Double>(numParticles);
        collisionCounter = new HashMap<Particle, Integer>(numParticles);
        
        this.typesWatched = typesWatched;
        colorsWatched = new HashSet<Color>(typesWatched.size());
        for (ParticleType type : typesWatched)
            colorsWatched.add(type.defaultColor);
        this.reactionMode = reactionMode;
        this.eventTypeToTrack = eventTypeToTrack;
    }
    
    public abstract double updatedValue(double prevValue, double velocity, double timeElapsed, double mass);
    public abstract double binFunction(double sum, int numCollisions);

    public Map<Color, List<Double>> getBinData() {
        Map<Color, List<Double>> sumData = new HashMap<Color, List<Double>>();
        for(Particle particle : sums.keySet()){
            if(!sumData.containsKey(particle.getDisplayColor()))
                sumData.put(particle.getDisplayColor(), new ArrayList<Double>());
            sumData.get(particle.getDisplayColor()).add(binFunction(sums.get(particle), collisionCounter.get(particle)));
        }
        return sumData;
    }
    
    public void analyzeEvent(EventInfo event) {
        if (event.colType == eventTypeToTrack)
            for (Particle particle: event.getInvolvedParticles())
                if (particle != null && shouldTrackCollisions(particle.particleType)) {
                    if(!sums.containsKey(particle)){
                        sums.put(particle, 0.0);
                        lastCollisionTimes.put(particle, 0.0);
                        collisionCounter.put(particle, 0);
                    }
                    //If we're in reaction mode, check the color
                    if (shouldRecordStats(particle.getDisplayColor())) {
                        double timeElapsed = event.colTime - lastCollisionTimes.get(particle);
                        sums.put(particle, updatedValue(sums.get(particle), particle.getVel2(), timeElapsed, particle.getMass()));
                    }
                    lastCollisionTimes.put(particle, event.colTime);
                    collisionCounter.put(particle, collisionCounter.get(particle) + 1);
                }
    }
    
    public boolean shouldTrackCollisions(ParticleType particleType) {
        return typesWatched.contains(particleType) || reactionMode;
    }
    
    public boolean shouldRecordStats(Color particleColor) {
        return colorsWatched.contains(particleColor);
    }

    public double getAverage() {
        if (numParticles == 0)
            return 0.0;
        double totalValue = 0;
        for(Particle particle : sums.keySet())
            totalValue += binFunction(sums.get(particle), collisionCounter.get(particle));
        return totalValue / numParticles;
    }
    
    public double getWidth() {
        double average = getAverage();
        return Math.sqrt(getRMS() - average * average);
    }
    
    private double getRMS() {
        if (numParticles == 0)
            return 0.0;
        double totalValueSquares = 0;
        for(Particle particle : sums.keySet()){
            double value = binFunction(sums.get(particle), collisionCounter.get(particle));
            totalValueSquares += value * value;
        }
        return totalValueSquares / numParticles;
    }
    
    public void reset() {
        sums.clear();
        lastCollisionTimes.clear();
        collisionCounter.clear();
    }
}
