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
package edu.byu.chem.boltzmann.model.statistics;

import edu.byu.chem.boltzmann.model.physics.Collision;
import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import java.util.Set;

/**
 * Created 5 May 2011
 * @author Derek Manwaring
 */
public class VirialPressure extends Pressure {

    private static final String DISPLAY_NAME = "Virial Pressure";
    
    //  val = b*(Etot-(virialSum/T));
    private double b = 0;

    // Total energy of the system
    private double totalEnergy = 0;

    /** 
     * Sum over all collisions of reduced mass times the dot product of 
     * relative position and velocity. Each term is a magnitude of momentum directed
     * towards the center of the particles.
     */
    private double virialSum = 0;

    // When the averaging/collision counting started
    private double startTime;
    
    private double timeElapsed;
    
    @Override
    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        super.initiateStatistic(types, physics);
        
        setStartInfo(physics);
    }
    
    @Override
    protected void updateStatisticAvgs(FrameInfo frame) {
//        updateWithCollisions(frame); 
    }

    private void updateWithCollisions(FrameInfo frame) {
//        for (EventInfo event: frame.getEvents()) {
//            if (event.colType == Collision.PARTICLE) {
//                updateForParticles(event, frame);
//            }
//        }
//        
//        timeElapsed = frame.endTime - startTime;
    }
    
    @Override
    public void updateByEvent(EventInfo event) {
        if (event.colType == Collision.PARTICLE) {
            updateForParticles(event);
        }
    }

    private void updateForParticles(EventInfo event, FrameInfo frame) {
        Particle particles[] = event.getInvolvedParticles();
        
        Particle particle1 = particles[0];
        Particle particle2 = particles[1];
        
        double mass1 = particle1.particleType.particleMass;
        double mass2 = particle2.particleType.particleMass;
        
//        double virial = getVirial(frame.getPrevParticleState(particle1, event.colTime),
//                frame.getPrevParticleState(particle2, event.colTime));
        
        double reducedMass = (mass1 * mass2) / (mass1 + mass2);
        
        virialSum += Units.convert("amu", "kg", reducedMass) * 0.0;//virial;
        
    }
    
    private final PartState workingState1 = new PartState(0, 0, 0, 0, null, 0);
    private final PartState workingState2 = new PartState(0, 0, 0, 0, null, 0);
    
    private void updateForParticles(EventInfo event) {
        Particle particles[] = event.getInvolvedParticles();
        
        Particle particle1 = particles[0];
        Particle particle2 = particles[1];
        
        double mass1 = particle1.particleType.particleMass;
        double mass2 = particle2.particleType.particleMass;
        
        double virial = getVirial(particle1.getState(workingState1),
                particle2.getState(workingState2));
        
        double reducedMass = (mass1 * mass2) / (mass1 + mass2);
        
        virialSum += Units.convert("amu", "kg", reducedMass) * virial;  
    }
    
    private double getVirial(PartState particle1, PartState particle2) {
        double dX = particle2.position[0] - particle1.position[0];
        double dY = particle2.position[1] - particle1.position[1];
        double dZ = particle2.position[2] - particle1.position[2];
                
        if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
            if (dX > (simulationInfo.arenaXSize - 
                    particle2.particleType.particleRadius -
                    particle1.particleType.particleRadius)) {
                dX -= simulationInfo.arenaXSize;
            }
            
            if (dX < (-simulationInfo.arenaXSize + 
                    particle2.particleType.particleRadius + 
                    particle1.particleType.particleRadius)) {
                dX += simulationInfo.arenaXSize;
            }
            
            if (dY > (simulationInfo.arenaYSize - 
                    particle2.particleType.particleRadius - 
                    particle1.particleType.particleRadius)) {
                dY -= simulationInfo.arenaYSize;
            }
            
            if (dY < (-simulationInfo.arenaYSize + 
                    particle2.particleType.particleRadius + 
                    particle1.particleType.particleRadius)) {
                dY += simulationInfo.arenaYSize;
            }
            
            if (dZ > (simulationInfo.arenaZSize - 
                    particle2.particleType.particleRadius - 
                    particle1.particleType.particleRadius)) {
                dZ -= simulationInfo.arenaZSize;
            }
            
            if (dZ < (-simulationInfo.arenaZSize + 
                    particle2.particleType.particleRadius + 
                    particle1.particleType.particleRadius)) {
                dZ += simulationInfo.arenaZSize;	
            }
        }
        
        double xVal = (dX) * (particle2.velocity[0] - particle1.velocity[0]);
        double yVal = (dY) * (particle2.velocity[1] - particle1.velocity[1]);
        double zVal = (dZ) * (particle2.velocity[2] - particle1.velocity[2]);
        
        double virial = 0.0;
        switch (simulationInfo.dimension) {
            case 1:
                virial = xVal;
                break;
            case 2:
                virial = xVal + yVal;
                break;
            case 3:
                virial = xVal + yVal + zVal;
        }
        
        return virial;
        
//		double dX=other.x-this.x;
//		double dY=other.y-this.y;
//		if (arena.periodic) {
//			if(dX>(arena.xLen-other.rad-this.rad))
//				dX-=arena.xLen;
//			if(dX<(-arena.xLen+other.rad+this.rad))
//				dX+=arena.xLen;
//			if(dY>(arena.yLen-other.rad-this.rad))
//				dY-=arena.yLen;
//			if(dY<(-arena.yLen+other.rad+this.rad))
//				dY+=arena.yLen;
//		}
//
//	 	double xVal = (dX)*(other.xVel-this.xVel);
//	 	double yVal = (dY)*(other.yVel-this.yVel);
//	 	return (xVal+yVal);
    }

    @Override
    public double getAverage() {
        
        double pressureBadUnits = b * (totalEnergy - (virialSum / timeElapsed));
        double pressure = changePressureUnits(pressureBadUnits);
        
        return pressure;
    }

    @Override
    public double getInstAverage() {
        
        double pressureBadUnits = b * (totalEnergy - (virialSum / timeElapsed));
        double pressure = changePressureUnits(pressureBadUnits);
        
        return pressure;
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
    
    @Override
    public void reset(double newStartTime, Physics physics) {
        super.reset(newStartTime, physics);
        
        setStartInfo(physics);
    }

    private void setStartInfo(Physics physics) {
        // number of particles
        int numParticles = simulationInfo.totalNumParticles;
        
        Particle[] particles = physics.getParticles();

        totalEnergy = b = 0.0;
        virialSum = 0.0;
        
        if (numParticles > 1) {

            startTime = physics.getTime();
            
            totalEnergy = simulationInfo.totalInitialKE;
            
            b = (2.0 / simulationInfo.dimension);
                
            switch (simulationInfo.dimension) {
                case 1:
                    b /= simulationInfo.arenaXSize;
                    break;
                case 2:
                    b /= (simulationInfo.arenaXSize * simulationInfo.arenaYSize);
                    break;
                case 3:
                    b /= (simulationInfo.arenaXSize * 
                            simulationInfo.arenaYSize *
                            simulationInfo.arenaZSize);
            }
        }        
    }
}
