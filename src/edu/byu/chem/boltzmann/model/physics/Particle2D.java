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
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import java.awt.Color;
import java.util.HashSet;

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

public class Particle2D extends Particle {

        private final double startX, startY;
        private final double startXVel, startYVel;
        private final double startTime;

        private double x, y;
	// State Variables
	private double xVel, yVel;

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

	protected double origYVel = 0.0;
        
        private final ArenaInfo arena;

	public Particle2D(double[] position, double[] velocity, double initTime, ParticleType particleType, SimulationInfo simulationInfo) {
		//basic initialization

                super(particleType, simulationInfo);

                this.startX = position[0];
                this.startY = position[1];

		this.startXVel = velocity[0];
		this.startYVel = velocity[1];

                x = startX;
                y = startY;
                xVel = startXVel;
                yVel = startYVel;

                arena = simulationInfo.arenaInfo;

		//initialize the time and stat variables
		startTime = initTime;
		this.bFlag = 0;
		this.binNum=0;
		t0 = initTime;
		cumTime = 0f;
                
		//ensure that bFlag is set properly
		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			//if touching the x boundary, ensure that it's on the left and mark
			//the boundary flag
			if (this.x<0)
				this.x+=simulationInfo.arenaXSize;
			if (this.y<0)
				this.y+=simulationInfo.arenaYSize;
			if (this.x<radius) {
				this.bFlag |= Wall.LEFT;
			}
			else if (simulationInfo.arenaXSize-this.x<radius) {
				this.x -= simulationInfo.arenaXSize;
				if(this.x<radius)
					this.bFlag |= Wall.LEFT;
			}

			//same as above but for y boundary, ensure that it's on the bottom
			if (this.y<radius) {
				this.bFlag |= Wall.BOTTOM;
			}
			else if (simulationInfo.arenaYSize-this.y<radius) {
				this.y -= simulationInfo.arenaYSize;
				if(this.y<radius)
					this.bFlag |= Wall.BOTTOM;
			}
		}
	}

	public double getVirial(Particle otherParticle) {
                Particle2D other = (Particle2D) otherParticle;

		double dX=other.x-this.x;
		double dY=other.y-this.y;
                double thisRadius = getRadius();
                double otherRadius = other.getRadius();

		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			if(dX>(simulationInfo.arenaXSize-otherRadius-thisRadius))
				dX-=simulationInfo.arenaXSize;
			if(dX<(-simulationInfo.arenaXSize+otherRadius+thisRadius))
				dX+=simulationInfo.arenaXSize;
			if(dY>(simulationInfo.arenaYSize-otherRadius-thisRadius))
				dY-=simulationInfo.arenaYSize;
			if(dY<(-simulationInfo.arenaYSize+otherRadius+thisRadius))
				dY+=simulationInfo.arenaYSize;
		}

	 	double xVal = (dX)*(other.xVel-this.xVel);
	 	double yVal = (dY)*(other.yVel-this.yVel);
	 	return (xVal+yVal);
	}
	public void moveToTime (double time) {
		//Move particle to the given time
		double dt=time-t0;
		x += xVel * dt;
		y += yVel * dt;
		t0 = time;
		cumTime += dt;
	}

        private EventInfo particleCollision = new EventInfo(Collision.PARTICLE, Calendar.MINTIME, 0, 0, 0);
        private EventInfo enterWellCollision = new EventInfo(Collision.ENTER_WELL, Calendar.MINTIME, 0, 0, 0);
        private EventInfo exitWellCollision = new EventInfo(Collision.EXIT_WELL, Calendar.MINTIME, 0, 0, 0);
                
	public void predCol(Particle targetParticle, EventInfo lastCollision, EventInfo returnCollision) {
                Particle2D target = (Particle2D) targetParticle;
		//Returns time of collision with referenced particle and (possibly) a flag
		//indicating that the collision occurs with an image
		
		double dX, dY, dVx, dVy, Dc, currTime;
    		//event		- holds the final event info that will be return
    		//dX,dY		- vector containing the difference in positions
    		//dVx, dVy	- vector containing the difference in velocities
    		//energyWellDepth		- distance between the centers at the time of collision
    		//currTime	- larger of the t0's between this and target

		//get the particles at the same time (the larger of the two t0's,
		//otherwise we might get a collision time in the past!)
		if (this.t0 > target.t0) {
			currTime = this.t0;
	    	dX = this.x - (target.x + target.xVel*(currTime-target.t0));
	    	dY = this.y - (target.y + target.yVel*(currTime-target.t0));
	    	dVx = this.xVel - target.xVel;
	    	dVy = this.yVel - target.yVel;
		}
		else { //use target.t0
			currTime = target.t0;
	    	dX = (this.x + this.xVel*(currTime-this.t0)) - target.x;
	    	dY = (this.y + this.yVel*(currTime-this.t0)) - target.y;
	    	dVx = this.xVel - target.xVel;
	    	dVy = this.yVel - target.yVel;
		}
                double thisRadius = getRadius();
                double targetRadius = target.getRadius();
		Dc = thisRadius + targetRadius;

		if (!(simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
			//Simple prediction

                        //Predict collisions between these two particles, and for the times
                        //when they are at the right distance to enter or exit their energy well
                        double particleCollisionTime = PredCol(dX, dY, dVx, dVy, Dc, currTime);
			particleCollision.colTime = particleCollisionTime;
                        returnCollision.copy(particleCollision);
                                
                        //Well radius is the radius of influence of the potential energy well for the current two particles
                        double wellRadius = (thisRadius + targetRadius) * simulationInfo.radiusOfInteractionMultiplier;
                        
                        double enterWellCollisionTime = PredColEnterWell(dX, dY, dVx, dVy, wellRadius, currTime);
			enterWellCollision.colTime = enterWellCollisionTime;
                        
                        double exitWellCollisionTime = PredColExitWell(dX, dY, dVx, dVy, wellRadius, currTime);
			exitWellCollision.colTime = exitWellCollisionTime;

                        //If these particles haven't collided yet, we can pretend their last collision was with each other
                        //so 
			if (lastCollision == null){
				lastCollision = NULL_LAST_COLLISION;
			}
			int lastCollisionType = lastCollision.colType; //Last collision between the two particles

                        if (simulationInfo.attractiveParticleInteractions) {
                            //An Enter well collision is only possible if the particle's last collision was not an enter well collision
                            if((enterWellCollision.colTime < returnCollision.colTime) && (lastCollisionType != Collision.ENTER_WELL) &&(lastCollisionType != Collision.WELL_REFLECT)){
                                    //Sometimes Enter well collisions are predicted at the same moment in time as exit well collisions just
                                    //performed because the particles are still at the right distance - but this shouldn't happen
                                    if (lastCollision.colTime != enterWellCollision.colTime) {
                                            //The prediction is valid - the next collision between these two particles will
                                            //be the predicted enter well collision
                                            returnCollision.copy(enterWellCollision);
                                    }
                            //An exit well collision is only possible if the particle's last collision was not an exit well collision
                            } else if ((exitWellCollision.colTime < returnCollision.colTime) && (lastCollisionType != Collision.EXIT_WELL)) {
                                    //Don't let collisions be predicted on top of each other - see a few lines above
                                    if (lastCollision.colTime != exitWellCollision.colTime) {
                                            //The prediction is valid - the next collision between these two particles will
                                            //be the predicted exit well collision
                                            returnCollision.copy(exitWellCollision);
                                    }
                            }
                        }

		} else { //periodic boundaries - need to check images and then find the
			   //soonest collision of all the images

			//at most 4 images in 2D
			EventInfo image[] = new EventInfo[4];
			int numToCheck = 0;
			boolean checkX=false, checkY=false;
			double xAdj=0, yAdj=0;
			final int xSide=Wall.LEFT, ySide=Wall.BOTTOM;
			//Note: because the particle order can be swapped when placed in Calendar, using Left and Right
			//in side loses significance - to reduce logic code in collide algorithm, always use left and bottom
			//to indicate that an image is needed, then the actual bFlags can be checked to choose which image

			//first image is the 'normal' particle
			image[numToCheck++] = new EventInfo(Collision.PARTICLE, PredCol(dX, dY, dVx, dVy, Dc, currTime), 0, 0, 0);

			//images to be checked depend on boundary flags - if either this or target is on the boundary
			//(but not both!) check the image
			if (((this.bFlag & Wall.LEFT) ^ (target.bFlag & Wall.LEFT))==Wall.LEFT)
				checkX = true;
			if (((this.bFlag & Wall.BOTTOM) ^ (target.bFlag & Wall.BOTTOM))==Wall.BOTTOM)
				checkY = true;

			//check x image
			if (checkX) {
				xAdj = ((this.bFlag & Wall.LEFT)==Wall.LEFT) ? simulationInfo.arenaXSize : -simulationInfo.arenaXSize;
				image[numToCheck++] = new EventInfo(Collision.PARTICLE,
					PredCol(dX+xAdj, dY, dVx, dVy, Dc, currTime), 0, 0, xSide);
			}

			//check y image
			if (checkY) {
				yAdj = ((this.bFlag & Wall.BOTTOM)==Wall.BOTTOM) ? simulationInfo.arenaYSize : -simulationInfo.arenaYSize;
				image[numToCheck++] = new EventInfo(Collision.PARTICLE,
					PredCol(dX, dY+yAdj, dVx, dVy, Dc, currTime), 0, 0, ySide);
			}

			//check combined image
			if (checkX && checkY) {
				image[numToCheck++] = new EventInfo(Collision.PARTICLE,
					PredCol(dX+xAdj, dY+yAdj, dVx, dVy, Dc, currTime), 0, 0, (xSide | ySide));
			}

			//find which image collision occurs first
			returnCollision.copy(image[0]);
			for (int i=1; i<numToCheck; i++) {
				if (image[i].colTime < returnCollision.colTime)
					returnCollision.copy(image[i]);
			}
		}

                returnCollision.particlesInvolved[0] = this;
                returnCollision.particlesInvolved[1] = targetParticle;
                
		if (returnCollision.colTime < currTime) {
			//misc.guiPtr.reportError("Particle-Particle collision predicted in the past", new RuntimeException("Particle2D:predCol, event.colTime<currTime"));
			throw new RuntimeException("Particle-Particle collision predicted in the past");		}
		return;
	}

	private double PredCol(double dX, double dY, double dVx, double dVy, double Dc, double currt0) {
		//generic predict collision routine (this way, reflecting and periodic boundaries
		//can use the same code without lots of replication etc.
		//dX = this.x - target.x
		//dY = this.y - target.y
		//dVx = this.xVel - target.xVel
		//dVy = this.yVel - target.yVel
		//energyWellDepth = thisRadius + targetRadius (i.e. distance at collision)
		//Note: assumes that dX, dY etc were calculated at time = this.t0

		//the following calculation is the most condensed/simplified form possible
		//see my lab notes for an explanation
		double B = dX*dVx + dY*dVy; //i.e. dot product of position and velocity vectors
		
		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {
			double A = dVx*dVx + dVy*dVy; 		//i.e. modulus squared of velocity vector
			double C = dX*dX + dY*dY - Dc*Dc;	//i.e. mod squared of pos vector - square of distance at collision
			
			
			double rad = B*B-A*C;
			if (rad < 0) //no collision occurs, at this point due to parallel paths
				return Calendar.MAXTIME;
			else {
				//collision occurs at time tc (result of solving quadratic to get time when first touching)
				double dt = - (B+Math.sqrt(rad))/A;
				return (dt >= 0)? currt0+dt : currt0;
			}
		}
	}

	private double PredColEnterWell(double dX, double dY, double dVx, double dVy, double wellRadius, double currt0) {
		double B = dX*dVx + dY*dVy;

		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {	double A = dVx*dVx + dVy*dVy; 		
			double C = dX*dX + dY*dY - wellRadius*wellRadius;	//square of distance at collision


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
	
	private double PredColExitWell (double dX, double dY, double dVx, double dVy, double Dc, double currt0) {
		double B = dX*dVx + dY*dVy;
		
		double A = dVx*dVx + dVy*dVy;
		double C = dX*dX + dY*dY - Dc*Dc;	//square of distance at collision
		
		
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
		int side = Wall.BOTTOM;
		
		double dC = getRadius();
		double dX = 0.0;
		double dY =  y - piston.getPositionAtTime(this.t0);//piston.getPosition() + piston.getVelocity()*(this.t0 - piston.getT0());;
		double dVx = 0.0;
		double dVy = yVel - piston.getVelocityAtTime(this.t0);
		
		// Calculate the collision given the piston and particle's state at t0 
		colTime = PredCol(dX, dY, dVx, dVy, dC, t0);
		
		// If the piston was moving, but by the calculated collision time has stopped,
		//		and the particle was heading toward the piston, we need to recalculate
		if(piston.isMoving(this.t0) && !piston.isMoving(colTime) && this.yVel > 0)
		{
			dVy = yVel;
			dY = y - piston.getStopPosition();
			colTime = PredCol(dX, dY, dVx, dVy, dC, t0);	
		}
		
		event = new EventInfo(Collision.PISTON, colTime , 0, 0, side);
		
		return event;
	}

	public EventInfo predBoundaryCol(Piston piston, boolean holeOpen) {
		//Returns time of soonest collision with a wall, boundary or barrier as
		//well as what it is that is collided with

		EventInfo event = null;
		EventInfo boundary[] = new EventInfo[simulationInfo.dimension+1+arena.numEdgePart]; //+1=barrier
		double colTime=Calendar.MAXTIME;
		int side=-1, numToCheck = 0;

		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			//check for boundary collisions and (possibly) end of boundary events

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (xVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			}
			else {
				//x direction
				if ((bFlag & Wall.LEFT)==Wall.LEFT) { //check for EOB
					if (xVel < 0) {
						side = Wall.LEFT;
						colTime = (x+radius)/(-xVel);
					}
					else { //xVel > 0
						side = Wall.RIGHT;
						colTime = (radius-x)/xVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				}
				else { //check for boundary collision
					if (xVel < 0) {
						side = Wall.LEFT;
						colTime = (radius-x)/xVel; //=(x-radius)/(-xVel) don't need negate if we swap the top order
					}
					else { //xVel > 0
						side = Wall.RIGHT;
						colTime = (simulationInfo.arenaXSize-radius-x)/xVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, t0+colTime, 0, 0, side);
				}
			}//xvel==0 check

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (yVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			}
			else {
				//y direction
				
				if ((bFlag & Wall.BOTTOM)==Wall.BOTTOM) { //check for EOB
					if (yVel < 0) {
						side = Wall.BOTTOM;
						colTime = (y+radius)/(-yVel);
					}
					else { //yVel > 0
						side = Wall.TOP;
						colTime = (radius-y)/yVel;
						
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				}
				else { //check for boundary collision
					if (yVel < 0) {
						side = Wall.BOTTOM;
						colTime = (radius-y)/yVel; //=(y-radius)/(-yVel) don't need negate if we swap the top order
					}
					else { //yVel > 0
						side = Wall.TOP;
						colTime = (simulationInfo.arenaYSize-radius-y)/yVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, t0+colTime, 0, 0, side);
				}
			}//yvel==0 check
		}
		else { //check for wall and barrier collisions		
			//check the normal walls

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (xVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
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
			}

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (yVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			}
			else {
				//y direction
                                int colType = Collision.WALL;
				if (yVel < 0) {
					side = Wall.BOTTOM;
					colTime = (radius-y)/yVel; //=(y-radius)/(-yVel) don't need negate if we swap the top order
                                        if(simulationInfo.includeAttractiveWall){
                                            double wellColTime = (simulationInfo.wallWellWidth - y) / yVel;
                                            if (wellColTime > 0) {
                                                colTime = wellColTime;
                                                colType = Collision.ENTER_GRAVITY_WELL;
                                            }
                                        }
				}
				else { //yVel > 0
					side = Wall.TOP;
					colTime = (simulationInfo.arenaYSize-radius-y)/yVel;
                                        if(simulationInfo.includeAttractiveWall){
                                            double wellColTime = (simulationInfo.wallWellWidth - y) / yVel;
                                            if(wellColTime > 0){
                                                colTime = wellColTime;
                                                colType = Collision.EXIT_GRAVITY_WELL;
                                            }
                                        }
				}

				boundary[numToCheck++] = new EventInfo(colType, t0+colTime, 0, 0, side);
			}
                        
			if(simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)	{
				boundary[numToCheck++] = this.predPistonCol(piston);
			}
			//check the barrier
			else if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE ) {
				side = -1;
                                double barrierLeftX = 0.5 * (simulationInfo.arenaXSize) - SimulationInfo.ARENA_DIVIDER_RADIUS;
                                double barrierRightX = 0.5 * (simulationInfo.arenaXSize) + SimulationInfo.ARENA_DIVIDER_RADIUS;
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

				//if we have a barrier collision, make sure it's not with the hole - and check
				//collision with the edges while we're here
				if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE && 
                                        (!simulationInfo.maxwellDemonMode || holeOpen)) {
					//check the edges of the hole
                                        double[] edgeY = arena.getEdgeY();
					double Dc = radius + arena.edgeRadius;
					double dVx = xVel, dVy = yVel;
					double dX, dY;

					for (int i=0; i<arena.numEdgePart; i++) {
						//check collision with current edge particle
						dX = x-arena.edgeX;
                                                dY = y-edgeY[i];
                                                boundary[numToCheck++] = new EventInfo(Collision.EDGE, PredCol(dX, dY, dVx, dVy, Dc, t0), 0, 0, i);
                                        }

					//double check that barrier collision is valid
					if (side != -1) {
                                            /**
                                             * TODO possibly bad calculation for holeRad2 - Derek Manwaring Sep 2010
                                             */
                                                double holeRad2 = (simulationInfo.holeDiameter / 2.0) * (simulationInfo.holeDiameter / 2.0);
						//get y position at time of collision - if no in the hole, count it
						double yPos = (y + yVel*(colTime))-0.5f*simulationInfo.arenaYSize;
						if (yPos*yPos >= holeRad2)
							boundary[numToCheck++] = new EventInfo(Collision.BARRIER, t0+colTime, 0, 0, side);
					}
				} else if (side != -1) { //divideStatus=FULL so always create an event if moving towards barrier
					boundary[numToCheck++] = new EventInfo(Collision.BARRIER, t0+colTime, 0, 0, side);
                                }
			}
		}

		//Note: all colTime's need to be corrected since they currently only hold the time relative to t0
		event = boundary[0];
		for (int i=1; i<numToCheck; i++) {
			if (boundary[i].colTime < event.colTime)
				event = boundary[i];
		}

                event.particlesInvolved[0] = this;
                
		if (event.colTime < t0) {
			//misc.guiPtr.reportError("Boundary collision predicted in the past", new RuntimeException("Particle2D:predBoundaryCol, event.colTime<currTime"));
			throw new RuntimeException("Boundary collision predicted in the past");
		}
		return event;
	}

	public void collideWith(Particle targetParticle, EventInfo event)
	{
                Particle2D target = (Particle2D) targetParticle;
		//Moves this and referenced particle to given time and performs the collision

		//First, move the particles to the correct time
		double dt1=event.colTime-this.t0;		
		double dt2=event.colTime-target.t0;
		this.x += this.xVel*dt1;
		this.y += this.yVel*dt1;
		target.x += target.xVel*dt2;
		target.y += target.yVel*dt2;

		//we need the distance between the particles, use this opportunity
		//to get the correct image if periodic boundaries
		double dX = target.x - this.x;
		double dY = target.y - this.y;
		if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
			//if side contains a flag it means we need to use an image, check bFlag to determine which one

			//x image
			if ((event.side & Wall.LEFT)==Wall.LEFT) {
				if ((this.bFlag & Wall.LEFT) == Wall.LEFT)
					dX -= simulationInfo.arenaXSize;
				else
					dX += simulationInfo.arenaXSize;
			}

			//y image
			if ((event.side & Wall.BOTTOM)==Wall.BOTTOM) {
				if ((this.bFlag & Wall.BOTTOM) == Wall.BOTTOM)
					dY -= simulationInfo.arenaYSize;
				else
					dY += simulationInfo.arenaYSize;
			}

		}

		//Vectors for calculating the new velocity components
                double nV1x, nV2x;		//x-comp of the new velocity vectors
		double nV1y, nV2y;		//y-comp of the new velocity vectors
		double Ax, Ay; 			//vector connecting centers of the particles
		double Bx, By;			//vector perpendicular to A
		double V1a, V1b;		//Velocity vectors in the new coordinates
		double V2a, V2b;
		double nV1a = 0.0, nV2a = 0.0;		//New velocity vector (only the a-comp of V changes)
		double realDc;			//real distance between the centers, if we
								//assume energyWellDepth=r1+r2 we get large round off errors

		realDc = Math.sqrt(dX*dX+dY*dY);

		//Normalize the A vector
		Ax = dX/realDc;
		Ay = dY/realDc;

	    //Calculate the new velocity components after the collision occurs

		//The perpendicular vector in 2D is simple
		Bx = -Ay;
		By = Ax;
		
		//Transform velocities into the new coordinates
		V1a = xVel*Ax+yVel*Ay; //V1*A
		V1b = xVel*Bx+yVel*By; //V1*B
		V2a = target.xVel*Ax+target.yVel*Ay; //V2*A
		V2b = target.xVel*Bx+target.yVel*By; //V2*B

                double targetMass = target.getMass();
		double relativeVelocity=V1a-V2a;
                double combinedMass = mass + targetMass;
		double reducedMass=((mass * targetMass) / (combinedMass));
                double relativeEnergy = Formulas.kineticEnergy(reducedMass, relativeVelocity);
                double eRel = Units.convert(Energy.AMU_JOULE, Energy.KILOJOULE_PER_MOLE, relativeEnergy);
//		double eRel=0.5*reducedMass*vRelSquared * Units.AMU * Units.AVAG / 1.0E3;

//		double rmsVel = 0.0; //new Units(getMain()).toSim(Units.VELOCITY, getMain().getPredictor().getPrediction(Const.RMSVEL, getMain().statColor, false).avg, simulationInfo.dimension);
//		double avgERel = 0.5*reducedMass*(Math.pow(rmsVel, 2));
//		AudioCore.getInstance().generateNote(eRel, avgERel);
		
		double deltaE;
                Color thisColor = getDisplayColor();
                Color targetColor = target.getDisplayColor();

                if (simulationInfo.reactionMode && (event.colType == Collision.PARTICLE)) {
                    Color type0Color = simulationInfo.getParticleTypes().get(0).defaultColor;
                    Color type1Color = simulationInfo.getParticleTypes().get(1).defaultColor;
                    ReactionRelationship redToBlue = simulationInfo.getReactionRelationship(new HashSet<ParticleType>(simulationInfo.getParticleTypes()));
                    if (simulationInfo.reactionMode && (thisColor.equals(type0Color)) && (targetColor.equals(type0Color)) &&
                            (eRel > redToBlue.forwardActivationEnergy)) {
                            deltaE = redToBlue.forwardActivationEnergy - redToBlue.reverseActivationEnergy;
                            deltaE = Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, deltaE);
                            
                            this.setColor(type1Color);
                            target.setColor(type1Color);
                            event.setDeltaBlue(2); // update blue count cumulative statistics
                    }
                    // Reaction: blue to red
                    else if (simulationInfo.reactionMode && (thisColor.equals(type1Color)) && (targetColor.equals(type1Color)) &&
                            (eRel > redToBlue.reverseActivationEnergy) && !redToBlue.suppressReverseReaction) {
                            deltaE = redToBlue.reverseActivationEnergy - redToBlue.forwardActivationEnergy;
                            deltaE = Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, deltaE);

                            this.setColor(type0Color);
                            target.setColor(type0Color);
                            event.setDeltaBlue(-2); // update blue count cumulative statistics
                    }
                    else {
                            deltaE=0;
                    }
                } else {
                    deltaE = 0;
                }

		if (deltaE==0) { //No change in energy due to a reaction
                    //The energy in the particle's square well
                    double energyWellPotential = energyWellDepth;
                    energyWellPotential += target.energyWellDepth;
                    energyWellPotential = Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, energyWellPotential);

                    if (event.colType == Collision.PARTICLE) {
                        //Momentum transfer only occurs along the A vector
                        nV1a = (2*targetMass*V2a+(mass-targetMass)*V1a)/(mass+targetMass);
                        nV2a = (2*mass*V1a+(targetMass-mass)*V2a)/(mass+targetMass);
                    //The particles are entering each other's potential energy well
                    } else if (event.colType == Collision.ENTER_WELL) {
                        //Calculate the new particle velocities adding the energy of the well
                        //Equations described in document by Dr. Shirts
                        double momentumTotal = mass * V1a + targetMass * V2a;
                        double velocityDifference = V2a - V1a;
                        double rootFactor = Math.sqrt(velocityDifference * velocityDifference + energyWellPotential / reducedMass);
                        double rootFactor1 = rootFactor;
                        double rootFactor2 = rootFactor;

                        if (V1a < V2a) {
                                rootFactor1 = -rootFactor1;
                        } else {
                                rootFactor2 = -rootFactor2;
                        }
                        nV1a = (momentumTotal + targetMass * rootFactor1) / combinedMass; //new velocites for particles
                        nV2a = (momentumTotal + mass * rootFactor2) / combinedMass;
                    //The particles are exiting each other's potential energy well
                    } else if (event.colType == Collision.EXIT_WELL) {
                        //Calculate the new particle velocities subtracting the energy of the well
                        //Again, equations described in a document by Dr. Shirts
                        double momentumTotal = mass * V1a + targetMass * V2a;
                        double velocityDifference = V2a - V1a;
                        double underRadical = velocityDifference * velocityDifference - energyWellPotential / reducedMass;

                        //If the expression to be square rooted is negative, there is not enough
                        //energy projecting the particles away from each other to get them out of the well.
                        //In this case a simple collision is done by conserving their momentum
                        if (underRadical < 0.0){
                            nV1a = (2 * targetMass * V2a + (mass - targetMass) * V1a) / combinedMass;
                            nV2a = (2 * mass * V1a + (targetMass - mass) * V2a) / combinedMass;
                            //The particles remain in the well as if this collision was an Enter well collision
                            event.colType = Collision.WELL_REFLECT;
                        } else { //Enough energy to exit the well
                            double rootFactor = Math.sqrt(velocityDifference * velocityDifference - energyWellPotential / reducedMass);
                            double rootFactor1 = rootFactor;
                            double rootFactor2 = rootFactor;
                            if (V1a < V2a) {
                                    rootFactor1 = -rootFactor1;
                            } else {
                                    rootFactor2 = -rootFactor2;
                            }
                            //New velocites for both particles
                            nV1a = (momentumTotal + targetMass * rootFactor1) / combinedMass;
                            nV2a = (momentumTotal + mass * rootFactor2) / combinedMass;
                        }
                    }
		} else {
                    double rootVal=Math.sqrt(relativeVelocity*relativeVelocity-(2*deltaE/reducedMass));
                    double t=mass*V1a+targetMass*V2a;
                    //Momentum transfer only occurs along the A vector
                    nV1a = (t-targetMass*rootVal)/(mass+targetMass);
                    nV2a = (t+mass*rootVal)/(mass+targetMass);
		}


		//Transform back to normal coordinates, use Xa=Ax, Xb=Bx, etc.
		nV1x = nV1a*Ax+V1b*Bx; //X*nV1
		nV1y = nV1a*Ay+V1b*By; //Y*nV1
		nV2x = nV2a*Ax+V2b*Bx; //X*nV2
		nV2y = nV2a*Ay+V2b*By; //Y*nv2


	    //Set the new velocity components
	    xVel = nV1x;
	    target.xVel = nV2x;
		yVel = nV1y;
		target.yVel = nV2y;
		

	    //Any stats that are dependent on cumulative time should be set before
	    //this function is called since cumT is reset
	    cumTime = 0;
	    target.cumTime = 0;
	    t0 = event.colTime;
	    target.t0 = event.colTime;
	}

	public void boundaryCollide(EventInfo event, Piston piston, Thermostat thermostat) {
		//Moves this particle to the collision time and performs the appropriate
		//boundary collision

		//set the time variables
		double dt = event.colTime - t0;
		cumTime += dt;
		t0 = event.colTime;
		
		double velAdjust = 1.0;
		boolean resetStats = false;
		double oldKE = this.getKE();
			
                if (thermostat.isActive()) {
                    velAdjust = thermostat.calcModifier(event, this);
                }

                double radius = getRadius();
                double barrierLeftX = 0.5 * (simulationInfo.arenaXSize) - SimulationInfo.ARENA_DIVIDER_RADIUS;
                double barrierRightX = 0.5 * (simulationInfo.arenaXSize) + SimulationInfo.ARENA_DIVIDER_RADIUS;
		switch (event.colType) {
			case Collision.WALL:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						y += yVel*dt;
						xVel = -xVel * velAdjust;
						break;
					case Wall.RIGHT:
						x = simulationInfo.arenaXSize-radius;
						y += yVel*dt;
						xVel = -xVel * velAdjust;
						break;
					case Wall.BOTTOM:
						y = radius;
						x += xVel*dt;
						yVel = -yVel * velAdjust;
						//double check x position - round off error can cause it to overlap the boundary
						//i.e. essentially the particle hits the corner touching both boundaries at the same time
						if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE ) {
							if (xVel > 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierLeftX-radius;
								xVel = -xVel;
							}
							else if (xVel < 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierRightX+radius;
								xVel = -xVel;
							}
						}
                                                if (simulationInfo.includeAttractiveWall && simulationInfo.includeHeatReservoir) {
                                                    double velocityChange = PhysicsFormulas.calculateVelocityChange(
                                                            new double[] { xVel, yVel },
                                                            yVel,
                                                            mass, 
                                                            simulationInfo.getHeatReservoirTemperature(), 
                                                            simulationInfo.dimension);
                                                    
                                                    yVel = yVel - velocityChange;
                                                }
						break;
					case Wall.TOP:
						y = simulationInfo.arenaYSize-radius;
						x += xVel*dt;
						yVel = -yVel * velAdjust;
						//double check x position - round off error can cause it to overlap the boundary
						//i.e. essentially the particle hits the corner touching both boundaries at the same time
						if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE ) {
							if (xVel > 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierLeftX-radius;
								xVel = -xVel;
							}
							else if (xVel < 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierRightX+radius;
								xVel = -xVel;
							}
						}
				}
				break;
			case Collision.BARRIER:
				switch (event.side) {
					case Wall.LEFT:
						x = barrierLeftX-radius;
						y += yVel*dt;
						xVel = -xVel * velAdjust;
						break;
					case Wall.RIGHT:
						x = barrierRightX+radius;
						y += yVel*dt;
						xVel = -xVel * velAdjust;
				}
				break;
			case Collision.PISTON:
				switch(event.side) 
				{
					case Wall.BOTTOM:
						//TODO: insert appropriate response for isothermal reaction
						//piston.moveToTime(event.colTime);

						y += yVel * dt;
						x += xVel * dt;
						
						if(piston.isMoving(event.colTime))	{
							yVel = 2.0 * piston.getVelocity() - this.yVel;	
							resetStats = true;
						} else {
							yVel = -yVel * velAdjust;
						}				
				}
				break;
			case Collision.EDGE:
				x += xVel*dt;
				y += yVel*dt;
                                double edgeX = simulationInfo.arenaXSize / 2.0;
                                double[] edgeY = arena.getEdgeY();
				EdgeCollide(edgeX, edgeY[event.side]);
				break;
			case Collision.BOUNDARY:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						y += yVel*dt;
						bFlag |= Wall.LEFT; //set the boundary flag
						break;
					case Wall.RIGHT:
						x = -radius;
						y += yVel*dt;
						bFlag |= Wall.LEFT; //set the boundary flag
						break;
					case Wall.BOTTOM:
						y = radius;
						x += xVel*dt;
						bFlag |= Wall.BOTTOM; //set the boundary flag
						break;
					case Wall.TOP:
						y = -radius;
						x += xVel*dt;
						bFlag |= Wall.BOTTOM; //set the boundary flag
				}
				break;
			case Collision.EOB:
				switch (event.side) {
					case Wall.LEFT:
						x = simulationInfo.arenaXSize-radius;
						y += yVel*dt;
						bFlag &= (~Wall.LEFT); //remove the flag
						break;
					case Wall.RIGHT:
						x = radius;
						y += yVel*dt;
						bFlag &= (~Wall.LEFT); //remove the flag
						break;
					case Wall.BOTTOM:
						y = simulationInfo.arenaYSize-radius;
						x += xVel*dt;
						bFlag &= (~Wall.BOTTOM); //remove the flag
						break;
					case Wall.TOP:
						y = radius;
						x += xVel*dt;
						bFlag &= (~Wall.BOTTOM); //remove the flag
				}
				break;
                        case Collision.ENTER_GRAVITY_WELL:
                                y = simulationInfo.wallWellWidth;
                                double addedEnergy = 
                                        Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, 
                                        simulationInfo.wallWellDepth);
                                double addedVelocity = Math.sqrt(addedEnergy * 2 / getMass());
                                yVel -= addedVelocity;
                                break;
                        case Collision.EXIT_GRAVITY_WELL:
                                y = simulationInfo.wallWellWidth;
                                double energyLost = 
                                        Units.convert(Energy.KILOJOULE_PER_MOLE, Energy.AMU_JOULE, 
                                        simulationInfo.wallWellDepth);
                                double velocityLost = Math.sqrt(energyLost * 2 / getMass());
                                yVel -= velocityLost;
		}//end of collision type switch
		
		double deltaKE = this.getKE() - oldKE;
		if(deltaKE != 0.0 && thermostat.isEnabled()) {
			thermostat.adjustKE(deltaKE);
		}
		//if(resetStats)
		//	getMain().physics.resetStats();
	}

	private void EdgeCollide(double edgeXPos, double edgeYPos) {
		//performs a collision with the edge 'particle' of infinite mass
		//(i.e. just reverse the component of velocity directed along the
		//vector connecting the centers of the particles)

		//we need the distance between the particles
		double dX = edgeXPos - this.x ;
		double dY = edgeYPos - this.y;


		//Vectors for calculating the new velocity components

	    double nV1x; //, nV2x;		//x-comp of the new velocity vectors
		double nV1y; //, nV2y;		//y-comp of the new velocity vectors
		double Ax, Ay; 			//vector connecting centers of the particles
		double Bx, By;			//vector perpendicular to A
		double V1a, V1b;		//Velocity vectors in the new coordinates
		double nV1a;			//New velocity vector (only the a-comp of V changes)
		double realDc;			//real distance between the centers, if we
								//assume energyWellDepth=r1+r2 we get round large off errors

		realDc = Math.sqrt(dX*dX+dY*dY);

		//Normalize the A vector
		Ax = dX/realDc;
		Ay = dY/realDc;


	    //Calculate the new velocity components after the collision occurs

		//The perpendicular vector in 2D is simple
		Bx = -Ay;
		By = Ax;

		//Transform velocities into the new coordinates
		V1a = xVel*Ax+yVel*Ay; //V1*A
		V1b = xVel*Bx+yVel*By; //V1*B

		//Momentum transfer only occurs along the A vector
		//nV1a = (2*target.mass*V2a+(mass-target.mass)*V1a)/(mass+target.mass);
		nV1a = -V1a;

		//Transform back to normal coordinates, use Xa=Ax, Xb=Bx, etc.
		nV1x = nV1a*Ax+V1b*Bx; //X*nV1
		nV1y = nV1a*Ay+V1b*By; //Y*nV1

	    //Set the new velocity components
	    xVel = nV1x;
		yVel = nV1y;
	}

    public PartState getState(PartState returnState) {
		//This function is primarily used by UpdateQ to get info needed for display
//		PartState output = new PartState(x,y,0,getRadius(), getDisplayColor(), bFlag);
//                output.position = new double[] { x, y, 0.0 };
//                output.velocity = new double[] { xVel, yVel, 0.0 };
//                output.particleType = particleType;        
                returnState.x = x;
                returnState.y = y;
                returnState.z = 0.0;
                returnState.rad = radius;
                returnState.color = getDisplayColor();
                returnState.bFlag = bFlag;
                returnState.position[0] = x;
                returnState.position[1] = y;
                returnState.position[2] = 0.0;
                returnState.velocity[0] = xVel;
                returnState.velocity[1] = yVel;
                returnState.velocity[2] = 0.0;
                returnState.particleType = particleType;
		return returnState;
    }

    public double getX()  {
		return x;
    }

    public double getY()  {
		return y;
    }

    public double getZ() {
		return 0.0;
    }

    public double getTheta() {
		double theta = 0.0;

                // Special cases:
                // - if xVel == 0 and yVel != 0, the ratio is + or - infinity, which produces the correct result
                // - if xVel == 0 and yVel == 0, the ration is NaN, which produces NaN
                theta = Math.atan(yVel/xVel) + (xVel < 0.0 ? Math.PI : 0.0);

                if (Double.isNaN(theta)){
                        theta = 0.0;
                }
		
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
		yVel = snapSubtract (yVel, yAdj / mass);
	}

    public void adjust(double fudgeFactor) {
		//fudgeFactor = sqrt(OriginalKE/CurrentKE)
		//note that KE is proportional to xVel^2+yVel^2+zVel^2 so multiplying each
		//component by fudgeFactor is the same as multiplying the total KE by
		//OrigKE/CurrKE, thus reproducing the original KE
		xVel *= fudgeFactor;
		yVel *= fudgeFactor;
	}

    public double getXMom() {
		return xVel*mass;
	}
    public double getYMom() {
                return yVel*mass;
	}
    public double getZMom() {
                return 0.0;
	}

    public double getXVel() {
        return xVel;
    }

    public double getYVel() {
        return yVel;
    }

    public double getZVel() {
        return 0.0;
    }

    /**
     * @return Kinetic energy of the particle in joules (kg * m^2 / s^2)
     */
    public double getKE() {
        double energy = Formulas.kineticEnergy(mass, Formulas.magnitude(xVel, yVel));
        return Units.convert(Energy.AMU_JOULE, Energy.JOULE, energy);
    }

    public double getVel() {
		return Math.sqrt(xVel*xVel+yVel*yVel);
	}

    public double getVel2() {
		return xVel*xVel+yVel*yVel;
	}

    public void reverse() {
                xVel = -xVel;
		yVel = -yVel;
    }

    public void setBinNum(int newBinNum) {
        binNum = newBinNum;
    }

    private void updateCumulativeStatistic(Statistic statistic, EventInfo event) {

    }

    private void updateInstantaneousStatistic(Statistic statistic) {
        
    }

    @Override
    public double[] getPosition() {
        return new double[] { x, y };
    }

    @Override
    public void reset() {
        x = startX;
        y = startY;
        xVel = startXVel;
        yVel = startYVel;

        this.bFlag = 0;
        this.binNum=0;
        t0 = startTime;
        cumTime = 0.0;
        
        //ensure that bFlag is set properly
        if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) {
                //if touching the x boundary, ensure that it's on the left and mark
                //the boundary flag
                if (this.x<0)
                        this.x+=simulationInfo.arenaXSize;
                if (this.y<0)
                        this.y+=simulationInfo.arenaYSize;
                if (this.x<radius) {
                        this.bFlag |= Wall.LEFT;
                }
                else if (simulationInfo.arenaXSize-this.x<radius) {
                        this.x -= simulationInfo.arenaXSize;
                        if(this.x<radius)
                                this.bFlag |= Wall.LEFT;
                }

                //same as above but for y boundary, ensure that it's on the bottom
                if (this.y<radius) {
                        this.bFlag |= Wall.BOTTOM;
                }
                else if (simulationInfo.arenaYSize-this.y<radius) {
                        this.y -= simulationInfo.arenaYSize;
                        if(this.y<radius)
                                this.bFlag |= Wall.BOTTOM;
                }
        }
    }

}
