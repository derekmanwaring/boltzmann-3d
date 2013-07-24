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

import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.ReactionRelationship;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.Units.Energy;
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import java.awt.Color;
import java.util.HashSet;

/* Proposed package: boltzmann.physics */
/* Used by: Physics */

/**********
 * Particle Class
 * 13 Aug 2004
 * Scott Burt
 *
 * Last Updated: 13 Aug 2004
 * Update by: Scott Burt
 *
 * This class implements the dimension specific methods for the particle class.  See the
 * parent class for more details on the methods.
 **********/

public class Particle1D extends Particle {


        private ArenaInfo arena;

        private final double startX;
        private final double startXVel;
        private final double startTime;

        private double x;
	// State Variables
	private double xVel;

	/** Boundary mask: Identifies any dimensions where this particle overlaps
	 * periodic boundaries.
	 * Only Wall.LEFT, Wall.BOTTOM, Wall.BACK are used here, and the actual side
	 * should be checked based on the position of the particle.
	 */
	private int bFlag;

	private int binNum;
	/**
	 * Note: t0 is the simulation time at which the pos and vel were set -
	 * this is used to get the correct positions for the particles when
	 * performing a collision event.  It is also used in conjunction with
	 * cumTime (cumulative time) for properly weighting stats. i.e. when
	 * colliding with a wall or normalizing positions at the end of a frame,
	 * t0 gets reset, but the time weighted stats need the time between
	 * collisions - cumTime will be reset after a particle collision, but
	 * updated by wall/boundary collisions and AdvanceToTime
	 */
	private double t0;
	/** Time since the last particle collision for this particle. */
	private double cumTime;


	/**
	 * 
	 * @param x  X position (e.g., 23.5 nm)
	 * @param xVel
	 * @param radius
	 * @param mass
	 * @param thisColor
	 * @param initTime
	 */
	public Particle1D(double[] position, double[] velocity, double initTime, ParticleType particleType, SimulationInfo simulationInfo) {
		// basic initialization
		super(particleType, simulationInfo);
		this.startXVel = velocity[0];
                this.startX = position[0];

                x = startX;
                xVel = startXVel;

                this.arena = simulationInfo.arenaInfo;

		//initialize the time and stat variables
		startTime = initTime;
		this.bFlag = 0;
		this.binNum=0;
		t0 = initTime;
		cumTime = 0f;
                
		// ensure that bFlag is set properly
		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			// if touching the x boundary, ensure that it's on the left and mark
			// the boundary flag
			if (this.x < 0)
				this.x += simulationInfo.arenaXSize;
			if (x < radius) {
				this.bFlag |= Wall.LEFT;
			}
			else if (simulationInfo.arenaXSize-x < radius) {
				// TODO: if it is overlapping the right boundary it could be set negative here...
				this.x -= simulationInfo.arenaXSize;
				if (this.x < radius)
					this.bFlag |= Wall.LEFT;
			}
		}
	}

	public double getVirial(Particle otherParticle) {
                Particle1D other = (Particle1D)otherParticle;

                double otherRadius = other.getRadius();
		double dX=other.x-this.x;
		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			if(dX > (simulationInfo.arenaXSize-otherRadius-radius))
				dX -= simulationInfo.arenaXSize;
			if(dX < (-simulationInfo.arenaXSize + otherRadius + radius))
				dX += simulationInfo.arenaXSize;
		}

	 	double xVal = (dX)*(other.xVel-this.xVel);
		return (xVal);
	}

	/** Move particle to the given time */
	public void moveToTime (double time) {
		
		double dt = time - t0;
		x += xVel * dt;
		t0 = time;
		cumTime += dt;
	}


	
	/** 
	 * Temporary output counter for debugging.  Shows how many times an error has taken place; useful when the same 
	 * error has occurred many times, to see exactly when it occurs again.  APS 
	 */
	int errorCount = 0;
        
        private EventInfo particleCollision = new EventInfo(Collision.PARTICLE, Calendar.MINTIME, 0, 0, 0);
        private EventInfo enterWellCollision = new EventInfo(Collision.ENTER_WELL, Calendar.MINTIME, 0, 0, 0);
        private EventInfo exitWellCollision = new EventInfo(Collision.EXIT_WELL, Calendar.MINTIME, 0, 0, 0);
                
	/** 
	 * Returns time of collision with referenced particle and (possibly) a flag
	 * indicating that the collision occurs with an image (in periodic boundary
	 * mode)
	 */
	public void predCol(Particle other, EventInfo lastCollision, EventInfo predictedCollision) {
		Particle1D target = (Particle1D) other;
		//Utils.debugEnter("predCol(this=" + this.thisColor + ", target=" + target.thisColor + ")");

		double dX, dVx, Dc, currTime;
    		// event		- holds the final event info that will be returned
    		// dX		- vector containing the difference in positions
    		// dVx		- vector containing the difference in velocities
    		// Dc		- distance between the centers at the time of collision
    		// currTime	- larger of the t0's between this and target

		// Get the particles at the same time (the larger of the two t0's,
		// otherwise we might get a collision time in the past!)
		
		// (APS note:) The t0 values can differ because particles may have already had collisions
		// during this event frame.  At the end of each frame, all particle t0 values are brought 
		// up to the same value.
		
		if (this.t0 > target.t0) {
			currTime = this.t0;
	    	dX = this.x - (target.x + target.xVel*(currTime-target.t0));
	    	dVx = this.xVel - target.xVel;
		}
		else { //use target.t0
			currTime = target.t0;
	    	dX = (this.x + this.xVel*(currTime-this.t0)) - target.x;
	    	dVx = this.xVel - target.xVel;
		}
		

                double thisRadius = this.getRadius();
                double targetRadius = target.getRadius();
		Dc = thisRadius + targetRadius;

		if (simulationInfo.arenaType != ArenaType.PERIODIC_BOUNDARIES) {
			//Simple prediction

                        //Prediect collisions between these two particles, and for the times
                        //when they are at the right distance to enter or exit their energy well
                        double particleCollisionTime = PredCol(dX, dVx, Dc, currTime);                     
			particleCollision.colTime = particleCollisionTime;
                        predictedCollision.copy(particleCollision);
                        
                        double enterWellCollisionTime = PredColEnterWell(dX, dVx, simulationInfo.radiusOfInteractionMultiplier * (thisRadius + targetRadius), currTime);
			enterWellCollision.colTime = enterWellCollisionTime;
                        
                        double exitWellCollisionTime = PredColExitWell(dX, dVx, simulationInfo.radiusOfInteractionMultiplier * (thisRadius + targetRadius), currTime);
			exitWellCollision.colTime = exitWellCollisionTime;

                        //Find out what the last collision was between these two particles
			int lastCollisionType;
                        //If these particles haven't collided yet, we can pretend their last collision was when
                        //they exited their potential energy well
			if (lastCollision == null){
				lastCollision = new EventInfo(Collision.EXIT_WELL, Calendar.MINTIME, 0, 0, 0);
			}
			lastCollisionType = lastCollision.colType; //Last collision between the two particles

                        //An Enter well collision is only possible if the particle's last collision was not an enter well collision
			if((enterWellCollision.colTime < predictedCollision.colTime) && (lastCollisionType != Collision.ENTER_WELL)){
                                //Sometimes Enter well collisions are predicted at the same moment in time as exit well collisions just
                                //performed because the particles are still at the right distance - but this shouldn't happen
				if (lastCollision.colTime != enterWellCollision.colTime) {
                                        //The prediction is valid - the next collision between these two particles will
                                        //be the predicted enter well collision
					predictedCollision.copy(enterWellCollision);
				}
                        //An exit well collision is only possible if the particle's last collision was not an exit well collision
			} else if ((exitWellCollision.colTime < predictedCollision.colTime) && (lastCollisionType != Collision.EXIT_WELL)) {
                                //Don't let collisions be predicted on top of each other - see a few lines above
				if (lastCollision.colTime != exitWellCollision.colTime) {
                                        //The prediction is valid - the next collision between these two particles will
                                        //be the predicted exit well collision
					predictedCollision.copy(exitWellCollision);
				}
			}
		}
		else { // periodic boundaries - need to check images and then find the
			   // soonest collision of all the images

			// at most 2 images in 1D
			EventInfo image[] = new EventInfo[2];
			int numToCheck = 0;
			boolean checkX = false;
			// x adjustment for image
			double xAdj = 0.0;
			final int xSide = Wall.LEFT;

			// Note: because the particle order can be swapped when placed in Calendar, using Left and Right
			// in side loses significance - to reduce logic code in collide algorithm, always use left and bottom
			// to indicate that an image is needed, then the actual bFlags can be checked to choose which image

			// first image is the 'normal' particle
			image[numToCheck++] = new EventInfo(Collision.PARTICLE, PredCol(dX, dVx, Dc, currTime), 0, 0, 0);

			// images to be checked depend on boundary flags - if either this or target is on the boundary
			// (but not both!) check the image
			if (((this.bFlag & Wall.LEFT) ^ (target.bFlag & Wall.LEFT)) == Wall.LEFT)
				checkX = true;
			
			// check x image
			if (checkX) {
				xAdj = ((this.bFlag & Wall.LEFT) == Wall.LEFT) ? simulationInfo.arenaXSize : -simulationInfo.arenaXSize;
				
				image[numToCheck++] = new EventInfo(Collision.PARTICLE,
					PredCol(dX+xAdj, dVx, Dc, currTime), 0, 0, xSide);
			}

			// find which image collision occurs first
			predictedCollision.copy(image[0]);
			if (numToCheck == 2) {
				
				// Original code:
				if (image[1].colTime < predictedCollision.colTime)
					predictedCollision.copy(image[1]);
			}
		}

                predictedCollision.particlesInvolved[0] = this;
                predictedCollision.particlesInvolved[1] = other;
                
		if (predictedCollision.colTime < currTime) {
			// formerly misc.guiPtr.reportError
			throw new RuntimeException("Particle-Particle collision predicted in the past");
		}
		return;
	}

	private double PredCol(double dX, double dVx, double Dc, double currt0) {
		// generic predict collision routine (this way, reflecting and periodic boundaries
		// can use the same code without lots of replication etc.
		// dX = this.x - target.x
		// dVx = this.xVel - target.xVel
		// Dc = this.radius + target.radius (i.e. distance at collision)
		// Note: assumes that dX, dY etc were calculated at time = this.t0

		// the following calculation is the most condensed/simplified form possible
		// see my lab notes for an explanation
		double B = dX*dVx; //i.e. dot product of position and velocity vectors

		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {
			double A = dVx*dVx; 			//i.e. modulus squared of velocity vector
			double C = dX*dX - Dc*Dc;	//i.e. mod squared of pos vector - square of distance at collision
			double radical = B*B-A*C;
			if (radical < 0) //no collision occurs, at this point due to parallel paths
				return Calendar.MAXTIME;
			else {
				//collision occurs at time tc (result of solving quadratic to get time when first touching)
				double dt = - (B+Math.sqrt(radical))/A;
				return (dt >= 0)? currt0+dt : currt0;
			}
		}
	}

	private double PredColEnterWell(double dX, double dVx, double wellRadius, double currt0) {
		double B = dX*dVx; //i.e. dot product of position and velocity vector

		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {	double A = dVx*dVx;
			double C = dX*dX - wellRadius*wellRadius;	//square of distance at collision


			double rad = B*B-A*C;
			if (rad < 0) {//no collision occurs, at this point due to parallel paths
				return Calendar.MAXTIME;
			} else {
				//collision occurs at time tc (result of solving quadratic to get time when first touching)
				double dt = - (B+Math.sqrt(rad))/A;
				return (dt >= 0)? currt0+dt : Calendar.MAXTIME;
			}
		}

	}

	private double PredColExitWell (double dX, double dVx, double Dc, double currt0) {
		double B = dX*dVx; //i.e. dot product of position and velocity vectors

		double A = dVx*dVx;
		double C = dX*dX - Dc*Dc;	// square of distance at collision


		double rad = B*B-A*C;

                double dt = - (B+Math.sqrt(rad))/A;
                double dt2 = - (B-Math.sqrt(rad))/A;
                if(dt2 > dt){
                        dt = dt2;
                }
                return (dt >= 0)? currt0+dt : Calendar.MAXTIME;
	}
	
	public EventInfo predPistonCol(Piston piston)
	{
		EventInfo event = null;
		double colTime = Calendar.MAXTIME;
		int side = Wall.RIGHT;
		
		double dC = getRadius();
		double dX = x - piston.getPositionAtTime(this.t0);
		double dVx = xVel - piston.getVelocityAtTime(this.t0);
		
		colTime = PredCol(dX, dVx, dC, t0);
		
		if(piston.isMoving(this.t0) && !piston.isMoving(colTime) && this.xVel > 0)
		{
			dVx = xVel;
			dX = x - piston.getStopPosition();
			colTime = PredCol(dX, dVx, dC, t0);	
		}
		event = new EventInfo(Collision.PISTON, colTime , 0, 0, side);
		
		return event;	
	}

	public EventInfo predBoundaryCol(Piston piston, boolean arenaHoleOpen) {
		// Returns time of soonest collision with a wall, boundary or barrier as
		// well as what it is that is collided with

		EventInfo event = null;
		EventInfo boundary[] = new EventInfo[2];
		double colTime=Calendar.MAXTIME;
		int side=0, numToCheck = 0;

		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			// check for boundary collisions and (possibly) end of boundary events

			// it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			// this is easy in 1D
			if (xVel == 0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			}
			else {
				// x direction
				if ((bFlag & Wall.LEFT) == Wall.LEFT) { //check for EOB
					if (xVel < 0) {
						side = Wall.LEFT;
						colTime = (x+radius) / (-xVel);
					}
					else { // xVel > 0
						side = Wall.RIGHT;
						colTime = (radius-x) / xVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				}
				else { //check for boundary collision
					if (xVel < 0) {
						side = Wall.LEFT;
						colTime = (radius-x) / xVel; //=(x-radius)/(-xVel) don't need negate if we swap the top order
					}
					else { //xVel > 0
						side = Wall.RIGHT;
						colTime = (simulationInfo.arenaXSize-radius-x)/xVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, t0+colTime, 0, 0, side);
				}
			}//xvel==0 check
		}
		else { //check for wall and barrier collisions
			//check the normal walls

			// it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			// this is easy in 1D
			if (xVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.WALL, Calendar.MAXTIME, 0, 0, 0);
			}
			else {
				//x direction
				if (xVel < 0) {
					side = Wall.LEFT;
					colTime = (radius-x)/xVel; //=(x-radius)/(-xVel) don't need negate if we swap the top order
				}
				else { //xVel > 0
					side = Wall.RIGHT;
					colTime = (simulationInfo.arenaXSize-radius-x)/xVel;
				}
				boundary[numToCheck++] = new EventInfo(Collision.WALL, t0+colTime, 0, 0, side);

				if(simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)	{
					boundary[numToCheck++] = this.predPistonCol(piston);
				}
				
				//check the barrier
                                double barrierLeftX = arena.barrierLeftX;
                                double barrierRightX = arena.barrierRightX;
				if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA ||
                                        simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
					side = -1;
					if (x <= barrierLeftX-radius && xVel > 0) {
						//collision with left of barrier
						side = Wall.LEFT;
						colTime = (barrierLeftX-radius-x)/xVel;
					}
					else if (x >= barrierRightX+radius && xVel < 0) {
						//collision with right of barrier
						side = Wall.RIGHT;
						colTime = (barrierRightX+radius-x)/xVel; //=(x-radius-barrierRightX)/-xVel
					}

					// hole in a 1D barrier is invalid, so no need to check that case
					if (side != -1) //divideStatus=FULL so always create an event if moving towards barrier
						boundary[numToCheck++] = new EventInfo(Collision.BARRIER, t0+colTime, 0, 0, side);
				}
			}//xvel==0 check
		}

		//Note: all colTime's need to be corrected since they currently only hold the time relative to t0
		event = boundary[0];
		if (numToCheck==2) {
			if (boundary[1].colTime < event.colTime)
				event = boundary[1];
		}

                event.particlesInvolved[0] = this;
                
		if (event.colTime < t0) {
			//misc.guiPtr.reportError("Boundary collision predicted in the past", new RuntimeException("Particle1D:predBoundaryCol, event.colTime<currTime"));
			throw new RuntimeException("Boundary collision predicted in the past");
		}
		return event;
	}

	/** Moves this and referenced particle to given time and performs the collision */
	public void collideWith(Particle other, EventInfo event) {
                Particle1D target = (Particle1D) other;

		// First, move the particles to the correct time
		double dt1 = event.colTime - this.t0;
		double dt2 = event.colTime - target.t0;
		this.x += this.xVel * dt1;
		target.x += target.xVel * dt2;

		// all momentum is transferred on the x-axis, much simpler than higher dimensions

		double nV1x = 0.0, nV2x = 0.0;
		double relativeVelocity=xVel-target.xVel;
                double targetMass = target.getMass();
                double combinedMass = mass + targetMass;
		double reducedMass=((mass * target.mass) / (combinedMass));
                double relativeEnergy = Formulas.kineticEnergy(reducedMass, relativeVelocity);
                double eRel = Units.convert(Energy.AMU_JOULE, Energy.KILOJOULE_PER_MOLE, relativeEnergy);
//		double eRel=0.5*reducedMass*vRelSquared * Units.AMU * Units.AVAG / 1.0E3;

		double rmsVel = 0.0; //Units.toSim(Units.VELOCITY, getMain().getPredictor().getPrediction(Const.RMSVEL, getMain().statColor, false).avg, simulationInfo.dimension);
		double avgERel = 0.5*reducedMass*(Math.pow(rmsVel, 2));
		//System.out.println("eRel = " + eRel);
		//System.out.println("avgERel = " + avgERel);
		
		double deltaE;
		// Reaction: red to blue
                Color thisColor = getDisplayColor();
                Color targetColor = target.getDisplayColor();
                
                if (simulationInfo.reactionMode && (event.colType == Collision.PARTICLE)) {
                    ReactionRelationship redToBlue = simulationInfo.getReactionRelationship(new HashSet<ParticleType>(simulationInfo.getParticleTypes()));
                    Color type0Color = simulationInfo.getParticleTypes().get(0).defaultColor;
                    Color type1Color = simulationInfo.getParticleTypes().get(1).defaultColor;

                    if (simulationInfo.reactionMode && (thisColor.equals(type0Color)) && (targetColor.equals(type0Color)) && (eRel > redToBlue.forwardActivationEnergy)) {
                            deltaE = redToBlue.forwardActivationEnergy - redToBlue.reverseActivationEnergy;
                            deltaE = Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, deltaE);

                            this.setColor(type1Color);
                            target.setColor(type1Color);
                            event.setDeltaBlue(+2); // update blue count cumulative statistics
                    } else if (simulationInfo.reactionMode && (thisColor.equals(type1Color)) && (targetColor.equals(type1Color)) && (eRel > redToBlue.reverseActivationEnergy) && !redToBlue.suppressReverseReaction) {
                            deltaE = redToBlue.reverseActivationEnergy - redToBlue.forwardActivationEnergy;
                            deltaE = Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, deltaE);

                            this.setColor(type0Color);
                            target.setColor(type0Color);
                            event.setDeltaBlue(-2); // update blue count cumulative statistics
                    } else {
                            deltaE = 0;
                    }
                } else {
                    deltaE = 0;
                }

		if (deltaE == 0) { //No change in energy due to a reaction
                    //The energy in the particle's square well
                    double energyWellPotential = energyWellDepth;
                    energyWellPotential += target.energyWellDepth;

                    if (event.colType == Collision.PARTICLE) {
                        //Momentum transfer only occurs along the A vector
                        nV1x = (2*targetMass*target.xVel+(mass-targetMass)*xVel)/(mass+targetMass);
                        nV2x = (2*mass*xVel+(targetMass-mass)*target.xVel)/(mass+targetMass);
                    //The particles are entering each other's potential energy well
                    } else if (event.colType == Collision.ENTER_WELL) {
                        //Calculate the new particle velocities adding the energy of the well
                        //Equations described in document by Dr. Shirts
                        double momentumTotal = mass * xVel + targetMass * target.xVel;
                        double velocityDifference = target.xVel - xVel;
                        double rootFactor = Math.sqrt(velocityDifference * velocityDifference + energyWellPotential / reducedMass);
                        double rootFactor1 = rootFactor;
                        double rootFactor2 = rootFactor;

                        if (xVel < target.xVel) {
                                rootFactor1 = -rootFactor1;
                        } else {
                                rootFactor2 = -rootFactor2;
                        }
                        nV1x = (momentumTotal + targetMass * rootFactor1) / combinedMass; //new velocites for particles
                        nV2x = (momentumTotal + mass * rootFactor2) / combinedMass;
                    //The particles are exiting each other's potential energy well
                    } else if (event.colType == Collision.EXIT_WELL) {
                        //Calculate the new particle velocities subtracting the energy of the well
                        //Again, equations described in a document by Dr. Shirts
                        double momentumTotal = mass * xVel + targetMass * target.xVel;
                        double velocityDifference = target.xVel - xVel;
                        double underRadical = velocityDifference * velocityDifference - energyWellPotential / reducedMass;

                        //If the expression to be square rooted is negative, there is not enough
                        //energy projecting the particles away from each other to get them out of the well.
                        //In this case a simple collision is done by conserving their momentum
                        if (underRadical < 0.0){
                            nV1x = (2 * targetMass * target.xVel + (mass - targetMass) * xVel) / combinedMass;
                            nV2x = (2 * mass * xVel + (targetMass - mass) * target.xVel) / combinedMass;
                            //The particles remain in the well as if this collision was an Enter well collision
                            event.colType = Collision.ENTER_WELL;
                        } else { //Enough energy to exit the well
                            double rootFactor = Math.sqrt(velocityDifference * velocityDifference - energyWellPotential / reducedMass);
                            double rootFactor1 = rootFactor;
                            double rootFactor2 = rootFactor;
                            if (xVel < target.xVel) {
                                    rootFactor1 = -rootFactor1;
                            } else {
                                    rootFactor2 = -rootFactor2;
                            }
                            //New velocites for both particles
                            nV1x = (momentumTotal + targetMass * rootFactor1) / combinedMass;
                            nV2x = (momentumTotal + mass * rootFactor2) / combinedMass;
                        }
                    }
		} else {
                    System.out.println("doing calculation with deltaE " + deltaE);
			double rootVal=Math.sqrt(relativeVelocity*relativeVelocity-(2*deltaE/reducedMass));
			double t=mass*xVel+targetMass*target.xVel;
			//Momentum transfer only occurs along the A vector
			nV1x = (t-targetMass*rootVal)/(mass+targetMass);
			nV2x = (t+mass*rootVal)/(mass+targetMass);
		}



	    //Set the new velocity components
	    xVel = nV1x;
	    target.xVel = nV2x;

	    //Any stats that are dependent on cumulative time should be set before
	    //this function is called since cumT is reset
	    cumTime = 0;
	    target.cumTime = 0;
	    t0 = event.colTime;
	    target.t0 = event.colTime;

	}

	/** 
	 * Moves this particle to the collision time and performs the appropriate
	 * boundary collision
	 */
	public void boundaryCollide(EventInfo event, Piston piston, Thermostat thermostat) {

		// set the time variables
  		double dt = event.colTime - t0;
		cumTime += dt;
		t0 = event.colTime;
		
		double velAdjust = 1.0;
		boolean resetStats = false;
		double oldKE = this.getKE();

		if(thermostat.isEnabled() && thermostat.isActive())
		{			
			velAdjust = thermostat.calcModifier(event, this);
			//resetStats = true;
		}
                double radius = getRadius();

		switch (event.colType) {
			case Collision.WALL:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						xVel = -xVel * velAdjust;
						break;
					case Wall.RIGHT:
						x = simulationInfo.arenaXSize-radius;
						xVel = -xVel * velAdjust;
				}
				break;
			case Collision.BARRIER:
				switch (event.side) {
					case Wall.LEFT:
                                                double barrierLeftX = arena.barrierLeftX;
						x = barrierLeftX-radius;
						xVel = -xVel * velAdjust;
						break;
					case Wall.RIGHT:
                                                double barrierRightX = arena.barrierRightX;
						x = barrierRightX+radius;
						xVel = -xVel * velAdjust;
				}
				break;
			case Collision.EDGE:
				//hole in 1D barrier not valid, so no edge event should occur
				break;
			case Collision.PISTON:
				
				//TODO: insert appropriate response for isothermal reaction
				//piston.moveToTime(event.colTime);

				x += xVel * dt;
				
				if(piston.isMoving(event.colTime))	{
					xVel = 2.0 * piston.getVelocity() - this.xVel;	
					resetStats = true;
				} else {
					xVel = -xVel * velAdjust;
				}					

				break;
				
			case Collision.BOUNDARY:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						bFlag |= Wall.LEFT; //set the boundary flag
						//System.err.println("boundaryCollide(" + thisColor + "): entering left side");
						break;
					case Wall.RIGHT:
						x = -radius;
						bFlag |= Wall.LEFT; //set the boundary flag
						//System.err.println("boundaryCollide(" + thisColor + "): entering right side");
				}
				break;

			case Collision.EOB:
				switch (event.side) {
					case Wall.LEFT:
						x = simulationInfo.arenaXSize - radius;
						bFlag &= (~Wall.LEFT); //remove the flag
						//System.err.println("boundaryCollide(" + thisColor + "): leaving left side");
						break;
					case Wall.RIGHT:
						x = radius;
						bFlag &= (~Wall.LEFT); //remove the flag
						//System.err.println("boundaryCollide(" + thisColor + "): leaving right side");
				}
		}//end of collision type switch
		
		double deltaKE = this.getKE() - oldKE;
		if(deltaKE != 0.0 && thermostat.isEnabled()) {
			thermostat.adjustKE(deltaKE);
		}
	}
        
    public PartState getState(PartState returnState) {
		//This function is primarily used by UpdateQ to get info needed for display
//		PartState output = new PartState(x,0,0, getRadius(), getDisplayColor(), bFlag);
//                output.position = new double[] { x, 0.0, 0.0 };
//                output.velocity = new double[] { xVel, 0.0, 0.0 };
//                output.particleType = particleType;
                returnState.x = x;
                returnState.y = 0.0;
                returnState.z = 0.0;
                returnState.rad = radius;
                returnState.color = getDisplayColor();
                returnState.bFlag = bFlag;
                returnState.position[0] = x;
                returnState.position[1] = 0.0;
                returnState.position[2] = 0.0;
                returnState.velocity[0] = xVel;
                returnState.velocity[1] = 0.0;
                returnState.velocity[2] = 0.0;
                returnState.particleType = particleType;
		return returnState;
    }

    public double getX()  {
		return x;
    }

    public double getY()  {
		return 0.0;
    }

    public double getZ() {
		return 0.0;
    }

    public double getTheta() {
		double theta = 0.0;
		theta = (xVel < 0 ? Math.PI : 0);
		return theta;
    }

    public double getPhi() {
		double phi = 0.0;
		return phi;
    }

    public void adjust(double xAdj, double yAdj, double zAdj) {
		// x,y and zAdj are the components of momentum (per particle) that need to
		// be subtracted from each particle to negate the momentum of a particle just added
		// Note: this will change the total kinetic energy - use the next adjust to fix that

		//xVel -= xAdj/mass;
		//yVel -= yAdj/mass;
		//zVel -= zAdj/mass;
                double mass = getMass();

		xVel = snapSubtract (xVel, xAdj / mass);
	}

    public void adjust(double fudgeFactor) {
		//fudgeFactor = sqrt(OriginalKE/CurrentKE)
		//note that KE is proportional to xVel^2+yVel^2+zVel^2 so multiplying each
		//component by fudgeFactor is the same as multiplying the total KE by
		//OrigKE/CurrKE, thus reproducing the original KE
		xVel *= fudgeFactor;
	}

    public double getXMom() {
                double mass = getMass();
		return xVel*mass;
	}
    public double getYMom() {
                return 0.0;
	}
    public double getZMom() {
                return 0.0;
	}

    public double getXVel() {
        return xVel;
    }

    public double getYVel() {
        return 0.0;
    }

    public double getZVel() {
        return 0.0;
    }

    public double getKE() {
        return Units.convert(Energy.AMU_JOULE, Energy.JOULE, Formulas.kineticEnergy(mass, xVel));
    }

    public double getVel() {
		return Math.sqrt(xVel*xVel);
	}

    public double getVel2() {
		return xVel*xVel;
	}

    public void reverse() {
                xVel = -xVel;
    }

    public void setBinNum(int newBinNum) {
        binNum = newBinNum;
    }

    @Override
    public double[] getPosition() {
        return new double[] { x, 0.0, 0.0 };
    }

    @Override
    public void reset() {
        x = startX;
        xVel = startXVel;

        this.arena = simulationInfo.arenaInfo;

        this.bFlag = 0;
        this.binNum=0;
        t0 = startTime;
        cumTime = 0f;
        
        // ensure that bFlag is set properly
        if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
                // if touching the x boundary, ensure that it's on the left and mark
                // the boundary flag
                if (this.x < 0)
                        this.x += simulationInfo.arenaXSize;
                if (x < radius) {
                        this.bFlag |= Wall.LEFT;
                }
                else if (simulationInfo.arenaXSize-x < radius) {
                        // TODO: if it is overlapping the right boundary it could be set negative here...
                        this.x -= simulationInfo.arenaXSize;
                        if (this.x < radius)
                                this.bFlag |= Wall.LEFT;
                }
        }
    }
}
