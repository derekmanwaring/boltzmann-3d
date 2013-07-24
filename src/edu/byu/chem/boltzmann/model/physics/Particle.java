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
import java.awt.Color;
import java.util.Map;


/**********
 * Particle Class
 * 13 Aug 2004
 * Scott Burt
 *
 * Last Updated: 13 Aug 2004
 * Update by: Scott Burt
 *
 * This is the parent class for the dimension dependant particle classes.
 *
 * Generic methods that are available to all dimensions
 * - Constructor(x,y,z,xVel,yVel,zVel,radius,mass,getColor,initTime): basic initialization.
 *		Note that bFlag is dimension dependent and needs to be set in the child constructor.
 *		Also not that initTime is necessary for adding particles later in the simulation.
 * - genericOverlap(dx,dy,dz,Dc): simple overlap check, used by checkOverlap to reduce
 *		code redundancy
 * - getState(): returns particle information that is needed to build the queue - position,
 *		radius, getColor and boundary flag (i.e. attributes that affect how the particle is drawn)
 * - adjust(x,y,z) or (f): these functions are used in periodic boundaries to zero (or set)
 *		the total momentum and fix the kinetic energy.
 * - getXMom(), getYMom(), getZMom(): returns the component of momentum.  Used in determine
 *		component offsets to pass into adjust(x,y,z)
 * - getKE(): returns the total kinetic energy of the particle.  Used to determine the KE before
 *		and after the momentum adjust to determine the factor to pass into adjust(f) to restore
 *		the original kinetic energy
 * - getColor(): returns the getColor of the particle.  Used when updating statistics.
 * - radius(): returns the radius of the particle.  Used for updating the pressure.
 * - getStat(whichStat, whichType): returns the value of the desired stat and the appropriate
 *		weight.  The stat type is used to ensure that a weight of 1 is return for instantaneous
 *		stats.
 * - resetStats(currTime): Clears out all of the interal stat info and sets the initial time variable
 *		to the currTime (this is mostly to reset all of the time weighted and cumulative stat info)
 *
 * Abstract methods that depend on dimension
 * - moveToTime(time): Moves the particle to the given time and updates t0 and cumTime
 * - predCol(target): Predicts the collision between the current and target particle objects and
 *		returns the info in an EventInfo object
 * - predBoundaryCol(): Predicts all possible boundary collisions and returns the soonest one
 *		in an EventInfo object
 * - collideWith(target, event): Performs the physics of a particle-particle collision.
 * - boundaryCollide(event): Performs the physics of the specified boundary collision
 *		(wall, barrier, periodic edge, etc.)
 * - updateStats(event): Calculates the value and weight of every stat and stores these in an
 *		internal array for retrieval (getStat) for updating the cumulative stats.  All of the
 *		cumulative stats need to be updated regardless of which stat is active, so it's faster
 *		to calculate all of the stats in one fell swoop to avoid wasting computation time on
 *		repetitive calculations.
 * - getInstStat(whichStat, currTime): This method is called in Physics:UpdateQ when the active
 *		stat is an instantaneous stat.  In this case the stat needs to be calculated using the
 *		current distribution (not the distribution previous to the last particle collision, which
 *		is what would be stored in the interal arrays).  But, we only want to calculate the specific
 *		stat that is active, not all of the stats.
 **********/



public abstract class Particle {
	
	public final double mass;
	public final double radius;
        public final double energyWellDepth; //In kilojoules/mole
        public final SimulationInfo simulationInfo;
        public final ParticleType particleType;

        public Map<Class<? extends Statistic>, String> statisticUpdateMethods;

        private Color color;
        
        public Particle() {
            this.particleType = null;

            this.radius = 0.0;
            this.mass = 0.0;
            this.color = null;
            this.energyWellDepth = 0.0;

            this.simulationInfo = null;            
        }

        public Particle(ParticleType particleType, SimulationInfo simulationInfo) {
            this.particleType = particleType;

            this.radius = particleType.particleRadius;
            this.mass = particleType.particleMass;
            this.color = particleType.defaultColor;

            if (simulationInfo.attractiveParticleInteractions) {
                this.energyWellDepth = simulationInfo.getEnergyWellDepth(particleType);
            } else {
                this.energyWellDepth = 0.0;
            }

            this.simulationInfo = simulationInfo;

        }
		
	public Color getDisplayColor() {
            return color;
	}
        
	public double getRadius() {
            return radius;
	}

        public double getMass() {
            return mass;
        }

        public void setColor(Color newColor){
            color = newColor;
        }


	//Methods that are dimension dependent - these methods are used in the main
	//physics code of predicting/performing events and need to be optimized for
	//speed, thus we don't want to check arena.dimension all over the place, nor
	//do we want to perform calculations on velocity components that aren't needed


	public abstract double getX();
	public abstract double getY();
	public abstract double getZ();
        public abstract double getXVel();
        public abstract double getYVel();
        public abstract double getZVel();

	public abstract double getTheta();

	public abstract double getPhi();

	public abstract double getVel();
	public abstract double getVel2();

	public abstract void adjust(double fudgeFactor);

	//These following 6 methods are used to zero the momentum in periodic boundary conditions
	public abstract void adjust(double xAdj, double yAdj, double zAdj);

	public abstract double getXMom();
	public abstract double getYMom();
	public abstract double getZMom();

	/**
	 * Returns (specific) kinetic energy (per particle) in simulation units.
	 */
	public abstract double getKE();

	public abstract PartState getState(PartState returnState);

	public abstract void moveToTime (double time);
		//This function simply moves the particle from the current position
		//at t0 to the new position at time newTime following the classical
		//trajectory
		//Note: no collisions with particles or walls are checked for or
		//carried out, this assumes that the controlling code has ensured
		//that no events will occur before newTime - this is primarily used
		//to ensure that all particles are updated to the same time before
		//updating the queue with the display frame

	public abstract void predCol(Particle target, EventInfo lastCollision, EventInfo predictedCollision);
		//Returns time of collision with referenced particle
		//Note: if using periodic boundaries, collisions with nearby images are also
		//calculated and the time of the soonest collision is returned as well as
		//which image being stored in EventInfo.side
		//Note: only the colTime and side are set in EventInfo, the rest of the
		//variables must be set by the prediction code, this is primarily due to
		//the fact that the particles don't know their index in the particle array

	public abstract EventInfo predPistonCol(Piston piston);
	
	public abstract EventInfo predBoundaryCol(Piston piston, boolean holeOpen);
		//Returns time of soonest collision with a wall, boundary or barrier as
		//well as what type of boundary and which side is collided with

	public abstract void collideWith(Particle target, EventInfo event);
		//Moves this and referenced particle to the time stored in event.colTime
		//and performs the collision.  In periodic boundaries, the info in event.side
		//is used to collide with the correct image

	public abstract void boundaryCollide(EventInfo event, Piston piston, Thermostat thermostat);
		//Moves this particle to the collision time and performs the appropriate
		//boundary collision

	public abstract double getVirial(Particle other);

        public abstract void reverse();

        public abstract void setBinNum(int newBinNum);

	public static boolean genericOverlap(double dx, double dy, double dz, double Dc) {
		//used by checkOverlap, mainly to reduce repeated code in periodic case
		return (dx*dx+dy*dy+dz*dz <= Dc*Dc);
	}

	public static double snapSubtract(double value1, double value2) {
		double max = Math.max(Math.abs(value1), Math.abs(value2));
		double diff = value1 - value2;
		double relativeError = Math.abs(diff) / max;
		// if it's more than about 14-15 digits, we are stretching the limits of
		// double-precision numbers!  (Allow a range of several ulps)
		//final double ulp =
		// (experimentally, 1E-15 should be adequate...)
		final double doublePrecisionEpsilonRegion = 1.0E-14;
		if (relativeError < doublePrecisionEpsilonRegion && relativeError != 0.0) {
			// snap to zero
			return 0.0;
		}
		else
			return diff;
	}

        public abstract double[] getPosition();
        public abstract void reset();

        public double[] getVelocity() {
            throw new UnsupportedOperationException();
        }
        
        public static final EventInfo NULL_LAST_COLLISION = new EventInfo(Collision.PARTICLE, Calendar.MINTIME, 0, 0, 0);
}
