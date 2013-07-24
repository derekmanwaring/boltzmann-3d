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
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.PressureUnit;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Joshua Olson
 * August 6, 2012
 */
public class VirialPressureTracker {
    
    private double simulationTime;
    private double virialSum, innerConstant, outerConstant;
    private final double arenaX, arenaY, arenaZ;
    private final int dimension;
    private final boolean reactionMode;
    private final Set<ParticleType> typesWatched;
    private final Set<Color> colorsWatched;
    private final PressureUnit baseUnit;

    public VirialPressureTracker(PressureUnit defaultUnit, Set<ParticleType> types, SimulationInfo simInfo){
        baseUnit = defaultUnit;
        simulationTime = 0;
        reactionMode = simInfo.reactionMode;
        typesWatched = types;
        colorsWatched = new HashSet<Color>(typesWatched.size());
        for(ParticleType type: typesWatched)
            colorsWatched.add(type.defaultColor);
        
        dimension = simInfo.dimension;
        arenaX = simInfo.arenaXSize;
        arenaY = simInfo.arenaYSize;
        arenaZ = simInfo.arenaZSize;
        
        virialSum = 0;
        //the inner and outer constants are used in calculating total pressure from the virial sum, inside and outside parentheses respectively
        innerConstant = 2 * simInfo.totalInitialKE;
        switch(dimension){
            case 1:
                outerConstant = 1. / arenaX;
                break;
            case 2:
                outerConstant = 1. / (2 * arenaX * arenaY);
                break;
            default:
                outerConstant = 1. / (3 * arenaX * arenaY * arenaZ);
        }
    }
    
    public void setSimulationTime(double simTime){
        simulationTime = simTime;
    }
    
    public void analyzeEvent(EventInfo event){
        if(event.colType == Collision.PARTICLE){
            Particle[] particles = event.getInvolvedParticles();
            double reducedMass = Units.convert("amu", "kg", particles[0].mass * particles[1].mass / (particles[0].mass + particles[1].mass));
            for(int partIndex: new int[]{0, 1})
                if(shouldTrackCollisions(particles[partIndex].particleType) && shouldRecordStats(particles[partIndex].getDisplayColor()))
                    virialSum += reducedMass * getVirial(particles[partIndex], particles[1 - partIndex]);
        }
    }
    
    public boolean shouldTrackCollisions(ParticleType particleType){
        return typesWatched.contains(particleType) || reactionMode;
    }

    //If we're in reaction mode, check the color
    public boolean shouldRecordStats(Color particleColor){
        return colorsWatched.contains(particleColor);
    }
    
    private double getVirial(Particle particle1, Particle particle2){
        double dX = particle2.getX() - particle1.getX();
        
        if(dX > arenaX - particle2.radius - particle1.radius)
            dX -= arenaX;
        if(dX < -arenaX + particle2.radius + particle1.radius)
            dX += arenaX;
        
        double virial = dX * (particle2.getXVel() - particle1.getXVel());
        
        if(dimension > 1){
            double dY = particle2.getY() - particle1.getY();
            
            if(dY > arenaY - particle2.radius - particle1.radius)
                dY -= arenaY;
            if(dY < -arenaY + particle2.radius + particle1.radius)
                dY += arenaY;
            
            virial += dY * (particle2.getYVel() - particle1.getYVel());
            
            if(dimension == 3){
                double dZ = particle2.getZ() - particle1.getZ();
                
                if(dZ > arenaZ - particle2.radius - particle1.radius)
                    dZ -= arenaZ;
                if(dZ < -arenaZ + particle2.radius + particle1.radius)
                    dZ += arenaZ;
                
                virial += dZ * (particle2.getZVel() - particle1.getZVel());
            }
        }
        
        return virial;
    }

    public double getAverage(PressureUnit unit){
        return Units.convert(baseUnit, unit, outerConstant * (innerConstant + virialSum / simulationTime));
    }
    
    public void reset(){
        simulationTime = 0;
        virialSum = 0;
    }
}
