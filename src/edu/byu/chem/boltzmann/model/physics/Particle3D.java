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
 * This class implements the dimension-specific methods for the particle class.  See the
 * parent class for more details on the methods.
 **********/

public class Particle3D extends Particle
{

        private final double startX, startY, startZ;
        private final double startXVel, startYVel, startZVel;
        private final double startTime;

        private double x, y, z;
	// State Variables
	private double xVel, yVel, zVel;

        private ArenaInfo arena;

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

	public Particle3D(double[] position, double[] velocity, double initTime, ParticleType particleType, SimulationInfo simulationInfo) {
		//basic initialization
                super(particleType, simulationInfo);

                this.startX = position[0];
                this.startY = position[1];
                this.startZ = position[2];

		this.startXVel = velocity[0];
		this.startYVel = velocity[1];
		this.startZVel = velocity[2];

		xVel = startXVel;
		yVel = startYVel;
		zVel = startZVel;
                x = startX;
                y = startY;
                z = startZ;

                this.arena = simulationInfo.arenaInfo;

		//initialize the time and stat variables
		startTime = initTime;
		this.bFlag = 0;
		this.binNum=0;
		t0 = initTime;
		cumTime = 0f;
                
		//ensure that bFlag is set properly
	
		if ((simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
			//if touching the x boundary, ensure that it's on the left and mark
			//the boundary flag
			if (this.x<0)
				this.x+=simulationInfo.arenaXSize;
			if (this.y<0)
				this.y+=simulationInfo.arenaYSize;
			if (this.z<0)
				this.z+=simulationInfo.arenaZSize;
			if (x<radius) {
				this.bFlag |= Wall.LEFT;
			}
			else if (simulationInfo.arenaXSize-x<radius) {
				this.x -= simulationInfo.arenaXSize;
				if(this.x<radius)
					this.bFlag |= Wall.LEFT;
			}

			//same as above but for y boundary, ensure that it's on the bottom
			if (y<radius) {
				this.bFlag |= Wall.BOTTOM;
			}
			else if (simulationInfo.arenaYSize-y<radius) {
				this.y -= simulationInfo.arenaYSize;
				if(this.y<radius)
					this.bFlag |= Wall.BOTTOM;
			}

			//same as above but for y boundary, ensure that it's on the bottom
			if (z<radius) {
				this.bFlag |= Wall.BACK;
			}
			else if (simulationInfo.arenaZSize-z<radius) {
				this.z -= simulationInfo.arenaZSize;
				if(this.z<radius)
					this.bFlag |= Wall.BACK;
			}
		}	
                
                for (int i = 0; i < periodicBoundaryImage.length; i++) {
                    periodicBoundaryImage[i] = new EventInfo();
                }
	}

	public double getVirial(Particle otherParticle) {
                Particle3D other = (Particle3D) otherParticle;
		double dX=other.x-this.x;
		double dY=other.y-this.y;
		double dZ=other.z-this.z;
                double thisRadius = getRadius();
                double otherRadius = other.getRadius();
		if ((simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
			if(dX>(simulationInfo.arenaXSize-otherRadius-thisRadius))
				dX-=simulationInfo.arenaXSize;
			if(dX<(-simulationInfo.arenaXSize+otherRadius+thisRadius))
				dX+=simulationInfo.arenaXSize;
			if(dY>(simulationInfo.arenaYSize-otherRadius-thisRadius))
				dY-=simulationInfo.arenaYSize;
			if(dY<(-simulationInfo.arenaYSize+otherRadius+thisRadius))
				dY+=simulationInfo.arenaYSize;
			if(dZ>(simulationInfo.arenaZSize-otherRadius-thisRadius))
				dZ-=simulationInfo.arenaZSize;
			if(dZ<(-simulationInfo.arenaZSize+otherRadius+thisRadius))
				dZ+=simulationInfo.arenaZSize;		}

	 	double xVal = (dX)*(other.xVel-this.xVel);
	 	double yVal = (dY)*(other.yVel-this.yVel);
		double zVal = (dZ)*(other.zVel-this.zVel);
		return (xVal+yVal+zVal);
	}

	public void moveToTime (double time) {
		//Move particle to the given time
		double dt=time-t0;
		x += xVel*dt;
		y += yVel*dt;
		z += zVel*dt;
		t0 = time;
		cumTime += dt;
	}

        private EventInfo particleCollision = new EventInfo(Collision.PARTICLE, Calendar.MINTIME, 0, 0, 0);
        private EventInfo enterWellCollision = new EventInfo(Collision.ENTER_WELL, Calendar.MINTIME, 0, 0, 0);
        private EventInfo exitWellCollision = new EventInfo(Collision.EXIT_WELL, Calendar.MINTIME, 0, 0, 0);
        
        private EventInfo[] periodicBoundaryImage = new EventInfo[8];
        EventInfo normalParticle = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, 0);
        EventInfo xImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, Wall.LEFT);
        EventInfo yImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, Wall.BOTTOM);
        EventInfo zImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, Wall.BACK);
        EventInfo xyImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, (Wall.LEFT | Wall.BOTTOM));
        EventInfo xzImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, (Wall.LEFT | Wall.BACK));
        EventInfo yzImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, (Wall.BOTTOM | Wall.BACK));
        EventInfo xyzImage = new EventInfo(Collision.PARTICLE, Calendar.MAXTIME, 0, 0, (Wall.LEFT | Wall.BOTTOM | Wall.BACK));     
        
	public void predCol(Particle targetParticle, EventInfo lastCollision, EventInfo returnCollision) {
                Particle3D target = (Particle3D) targetParticle;
		//Returns time of collision with referenced particle and (possibly) a flag
		//indicating that the collision occurs with an image

		double dX, dY, dZ, dVx, dVy, dVz, Dc, currTime, dt;
    		//event			- holds the final event info that will be return
    		//dX,dY,dZ		- vector containing the difference in positions
    		//dVx,dVy,dVz	- vector containing the difference in velocities
    		//Dc			- distance between the centers at the time of collision
    		//currTime		- larger of the t0's between this and target

		//get the particles at the same time (the larger of the two t0's,
		//otherwise we might get a collision time in the past!)
		if (this.t0 > target.t0) {
			currTime = this.t0;
			dt = currTime-target.t0;
	    	dX = this.x - (target.x + target.xVel*dt);
	    	dY = this.y - (target.y + target.yVel*dt);
	    	dZ = this.z - (target.z + target.zVel*dt);
	    	dVx = this.xVel - target.xVel;
	    	dVy = this.yVel - target.yVel;
	    	dVz = this.zVel - target.zVel;
		}
		else { //use target.t0
			currTime = target.t0;
			dt = currTime-this.t0;
	    	dX = (this.x + this.xVel*dt) - target.x;
	    	dY = (this.y + this.yVel*dt) - target.y;
	    	dZ = (this.z + this.zVel*dt) - target.z;
	    	dVx = this.xVel - target.xVel;
	    	dVy = this.yVel - target.yVel;
	    	dVz = this.zVel - target.zVel;
		}

                double thisRadius = getRadius();
                double targetRadius = target.getRadius();
		Dc = thisRadius + targetRadius;

		if (!(simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
			//Simple prediction

                        //Predict collisions between these two particles, and for the times
                        //when they are at the right distance to enter or exit their energy well
                        double particleCollisionTime = PredCol(dX, dY, dZ, dVx, dVy, dVz, Dc, currTime);
			particleCollision.colTime = particleCollisionTime;
                        returnCollision.copy(particleCollision);
                        
                        //Well radius is the radius of influence of the potential energy well for the current two particles
                        double wellRadius = (thisRadius + targetRadius) * simulationInfo.radiusOfInteractionMultiplier;
                        
                        double enterWellCollisionTime = PredColEnterWell(dX, dY, dZ, dVx, dVy, dVz, wellRadius, currTime);
			enterWellCollision.colTime = enterWellCollisionTime;
                        
                        double exitWellCollisionTime = PredColExitWell(dX, dY, dZ, dVx, dVy, dVz, wellRadius, currTime);
			exitWellCollision.colTime = exitWellCollisionTime;

                        //Find out what the last collision was between these two particles
			int lastCollisionType;
                        //If these particles haven't collided yet, we can pretend their last collision was when
                        //they exited their potential energy well
			if (lastCollision == null){
				lastCollision = NULL_LAST_COLLISION;
			}
			lastCollisionType = lastCollision.colType; //Last collision between the two particles

                        if (simulationInfo.attractiveParticleInteractions) {
                            //An Enter well collision is only possible if the particle's last collision was not an enter well collision
                            if((enterWellCollision.colTime < returnCollision.colTime) && (lastCollisionType != Collision.ENTER_WELL)){
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

			//at most 8 images in 3D
			int numToCheck = 0;
			boolean checkX=false, checkY=false, checkZ=false;
			double xAdj=0, yAdj=0, zAdj=0;
			final int xSide=Wall.LEFT, ySide=Wall.BOTTOM, zSide=Wall.BACK;
			//Note: because the particle order can be swapped when placed in Calendar, using Left and Right
			//in side loses significance - to reduce logic code in collide algorithm, always use left and bottom
			//to indicate that an image is needed, then the actual bFlags can be checked to choose which image                   
                        
			//first image is the 'normal' particle
			//periodicBoundaryImage[numToCheck++] = new EventInfo(Collision.PARTICLE, PredCol(dX, dY, dZ, dVx, dVy, dVz, Dc, currTime), 0, 0, 0);
                        normalParticle.colTime = PredCol(dX, dY, dZ, dVx, dVy, dVz, Dc, currTime);
			periodicBoundaryImage[numToCheck++].copy(normalParticle);

			//images to be checked depend on boundary flags - if either this or target is on the boundary
			//(but not both!) check the image
			if (((this.bFlag & Wall.LEFT) ^ (target.bFlag & Wall.LEFT))==Wall.LEFT)
				checkX = true;
			if (((this.bFlag & Wall.BOTTOM) ^ (target.bFlag & Wall.BOTTOM))==Wall.BOTTOM)
				checkY = true;
			if (((this.bFlag & Wall.BACK) ^ (target.bFlag & Wall.BACK))==Wall.BACK)
				checkZ = true;

			//check x image
			if (checkX) {
				xAdj = ((this.bFlag & Wall.LEFT)==Wall.LEFT) ? simulationInfo.arenaXSize : -simulationInfo.arenaXSize;
                                xImage.colTime = PredCol(dX+xAdj, dY, dZ, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(xImage);
			}

			//check y image
			if (checkY) {
				yAdj = ((this.bFlag & Wall.BOTTOM)==Wall.BOTTOM) ? simulationInfo.arenaYSize : -simulationInfo.arenaYSize;
                                yImage.colTime = PredCol(dX, dY+yAdj, dZ, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(yImage);
			}

			//check z image
			if (checkZ) {
				zAdj = ((this.bFlag & Wall.BACK)==Wall.BACK) ? simulationInfo.arenaZSize : -simulationInfo.arenaZSize;
                                zImage.colTime = PredCol(dX, dY, dZ+zAdj, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(zImage);
			}

			//check combined image
			if (checkX && checkY) {
                                xyImage.colTime = PredCol(dX+xAdj, dY+yAdj, dZ, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(xyImage);
			}
			if (checkX && checkZ) {
                                xzImage.colTime = PredCol(dX+xAdj, dY, dZ+zAdj, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(xzImage);
			}
			if (checkY && checkZ) {
                                yzImage.colTime = PredCol(dX, dY+yAdj, dZ+zAdj, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(yzImage);
			}
			if (checkX && checkY && checkZ) {
                                xyzImage.colTime = PredCol(dX+xAdj, dY+yAdj, dZ+zAdj, dVx, dVy, dVz, Dc, currTime);
				periodicBoundaryImage[numToCheck++].copy(xyzImage);
			}

			//find which image collision occurs first
			returnCollision.copy(periodicBoundaryImage[0]);
			for (int i=1; i<numToCheck; i++) {
				if (periodicBoundaryImage[i].colTime < returnCollision.colTime)
					returnCollision.copy(periodicBoundaryImage[i]);
			}
		}

                returnCollision.particlesInvolved[0] = this;
                returnCollision.particlesInvolved[1] = targetParticle;
                
		if (returnCollision.colTime < currTime) {
			//misc.guiPtr.reportError("Particle-Particle collision predicted in the past", new RuntimeException("Particle3D:predCol, event.colTime<currTime"));
			throw new RuntimeException("Particle-Particle collision predicted in the past");
		}
		return;
	}

	private double PredCol(double dX, double dY, double dZ, double dVx, double dVy, double dVz, double Dc, double currt0) {
		//generic predict collision routine (this way, reflecting and periodic boundaries
		//can use the same code without lots of replication etc.
		//dX = this.x - target.x
		//dY = this.y - target.y
		//dVx = this.xVel - target.xVel
		//dVy = this.yVel - target.yVel
		//Dc = this.radius + target.radius (i.e. distance at collision)
		//Note: assumes that dX, dY etc were calculated at time = this.t0

		//the following calculation is the most condensed/simplified form possible
		//see my lab notes for an explanation
		double B = dX*dVx+dY*dVy+dZ*dVz; //i.e. dot product of position and velocity vectors

		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {
			double A = dVx*dVx+dVy*dVy+dVz*dVz;	//i.e. modulus squared of velocity vector
			double C = dX*dX+dY*dY+dZ*dZ-Dc*Dc;	//i.e. mod squared of pos vector - square of distance at collision
			double rad = B*B-A*C;
			if (rad < 0) //no collision occurs, at this point due to parallel paths
				return Calendar.MAXTIME;
			else {
				//collision occurs at time tc (result of solving quadratic to get time when first touching)
				double dt = - (B+Math.sqrt(rad))/A;
				return (dt >= 0)? currt0+dt : t0;
			}
		}
	}

	private double PredColEnterWell(double dX, double dY, double dZ, double dVx, double dVy, double dVz, double wellRadius, double currt0) {
		double B = dX*dVx + dY*dVy + dZ*dVz;

		if (B >= 0) //no collision occurs, not moving towards each other
			return Calendar.MAXTIME;
		else {	double A = dVx*dVx + dVy*dVy + dVz*dVz;
			double C = dX*dX + dY*dY + dZ*dZ - wellRadius*wellRadius;	//square of distance at collision


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

	private double PredColExitWell (double dX, double dY, double dZ, double dVx, double dVy, double dVz, double Dc, double currt0) {
		double B = dX*dVx + dY*dVy + dZ*dVz;

		double A = dVx*dVx + dVy*dVy + dVz*dVz;
		double C = dX*dX + dY*dY + dZ*dZ - Dc*Dc;	//square of distance at collision


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
		double dY =  y - piston.getPositionAtTime(this.t0);
		double dZ = 0.0;
		double dVx = 0.0;
		double dVy = yVel - piston.getVelocityAtTime(this.t0);
		double dVz = 0.0;
		
		colTime = PredCol(dX, dY, dZ, dVx, dVy, dVz, dC, t0);

		if(piston.isMoving(this.t0) && !piston.isMoving(colTime) && this.yVel > 0)
		{
			dVy = yVel;
			dY = y - piston.getStopPosition();
			colTime = PredCol(dX, dY, dZ, dVx, dVy, dVz, dC, t0);
		}
		event = new EventInfo(Collision.PISTON, colTime , 0, 0, side);
		
		return event;
	}

	public EventInfo predBoundaryCol(Piston piston, boolean holeOpen) {
		//Returns time of soonest collision with a wall, boundary or barrier as
		//well as what it is that is collided with

		EventInfo event = null;
                int numEdgePart = arena.numEdgePart;
		EventInfo boundary[] = new EventInfo[simulationInfo.dimension+1+numEdgePart]; //+1=barrier
		double colTime=Calendar.MAXTIME;
		int side=0, numToCheck = 0;

		if ((simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
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
					} else { //xVel > 0
						side = Wall.RIGHT;
						colTime = (radius-x)/xVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				}
				else { //check for boundary collision
					if (xVel < 0) {
						side = Wall.LEFT;
						colTime = (radius-x)/xVel; //=(x-radius)/(-xVel) don't need negate if we swap the top order
					} else { //xVel > 0
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
					} else { //yVel > 0
						side = Wall.TOP;
						colTime = (radius-y)/yVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				}
				else { //check for boundary collision
					if (yVel < 0) {
						side = Wall.BOTTOM;
						colTime = (radius-y)/yVel; //=(y-radius)/(-yVel) don't need negate if we swap the top order
					} else { //yVel > 0
						side = Wall.TOP;
						colTime = (simulationInfo.arenaYSize-radius-y)/yVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, t0+colTime, 0, 0, side);
				}
			}//yvel==0 check

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (zVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			} else {
				//z direction
				if ((bFlag & Wall.BACK)==Wall.BACK) { //check for EOB
					if (zVel < 0) {
						side = Wall.BACK;
						colTime = (z+radius)/(-zVel);
					} else { //zVel > 0
						side = Wall.FRONT;
						colTime = (radius-z)/zVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.EOB, t0+colTime, 0, 0, side);
				} else { //check for boundary collision
					if (zVel < 0) {
						side = Wall.BACK;
						colTime = (radius-z)/zVel; //=(z-radius)/(-zVel) don't need negate if we swap the top order
					} else { //zVel > 0
						side = Wall.FRONT;
						colTime = (simulationInfo.arenaZSize-radius-z)/zVel;
					}
					boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, t0+colTime, 0, 0, side);
				}
			}//zvel==0 check
		} else { //check for wall and barrier collisions
			//check the normal walls

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (xVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			} else {
				//x direction
				if (xVel < 0) {
					side = Wall.LEFT;
					colTime = (radius-x)/xVel; //=(x-radius)/(-xVel) don't need negate if we swap the top order
				} else { //xVel > 0
					side = Wall.RIGHT;
					colTime = (simulationInfo.arenaXSize-radius-x)/xVel;
				}
				boundary[numToCheck++] = new EventInfo(Collision.WALL, t0+colTime, 0, 0, side);
			}

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (yVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			} else {
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
				} else { //yVel > 0
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

			//it's possible for the vel component to be 0 which needs to trigger a MAXTIME
			if (zVel==0) {
				boundary[numToCheck++] = new EventInfo(Collision.BOUNDARY, Calendar.MAXTIME, 0, 0, 0);
			} else {
				//z direction
				if (zVel < 0) {
					side = Wall.BACK;
					colTime = (radius-z)/zVel; //=(z-radius)/(-zVel) don't need negate if we swap the top order
				}
				else { //zVel > 0
					side = Wall.FRONT;
					colTime = (simulationInfo.arenaZSize-radius-z)/zVel;
				}
				boundary[numToCheck++] = new EventInfo(Collision.WALL, t0+colTime, 0, 0, side);
			}

			
			if(simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)	{
				boundary[numToCheck++] = this.predPistonCol(piston);
                        //check the barrier
			} else if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
                                double barrierLeftX = arena.barrierLeftX;
                                double barrierRightX = arena.barrierRightX;
				side = -1;
				if (x <= barrierLeftX-radius && xVel > 0) {
					//collision with left of barrier
					side = Wall.LEFT;
					colTime = (barrierLeftX-radius-x)/xVel;
				} else if (x >= barrierRightX+radius && xVel < 0) {
					//collision with right of barrier
					side = Wall.RIGHT;
					colTime = (barrierRightX+radius-x)/xVel; //=(x-radius-barrierRightX)/-xVel
				}				
				
				//if we have a barrier collision, make sure it's not with the hole - and check
				//collision with the edges while we're here
				if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE && 
                                        (!simulationInfo.maxwellDemonMode || holeOpen)) {
					//first, see if we really have a barrier event or if it's really just passing
					//through the hole or interacting with the edge
					if (side != -1) {
						//get position at time of collision
						double 	yPos = y + yVel*(colTime) - 0.5f*simulationInfo.arenaYSize,
								zPos = z + zVel*(colTime) - 0.5f*simulationInfo.arenaZSize;
                                                double holeRad2 = arena.holeRadiusSquared;

						if (yPos*yPos+zPos*zPos > holeRad2)
							//we have a barrier collision
							boundary[numToCheck++] = new EventInfo(Collision.BARRIER, t0+colTime, 0, 0, side);
					}

					//now check for collisions with the fixed particles that make up the edge of the hole
                                        double edgeRad = arena.edgeRadius;
					double Dc = this.getRadius() + edgeRad;
					double dVx = xVel, dVy = yVel, dVz = zVel;
					double dX, dY, dZ;

					for (int i=0; i<numEdgePart; i++) {
						//check collision with current edge particle
                                                double edgeX = arena.edgeX;
                                                double[] edgeY = arena.getEdgeY();
                                                double[] edgeZ = arena.getEdgeZ();
						dX = x-edgeX;
                                                dY = y-edgeY[i];
                                                dZ = z-edgeZ[i];
                                                boundary[numToCheck++] = new EventInfo(Collision.EDGE, PredCol(dX, dY, dZ, dVx, dVy, dVz, Dc, t0), 0, 0, i);
                                        }

				} else if (side != -1) {//divideStatus=FULL so always create an event if moving towards barrier
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
			//misc.guiPtr.reportError("Boundary collision predicted in the past", new RuntimeException("Particle3D:predBoundaryCol, event.colTime<currTime"));
			throw new RuntimeException("Boundary collision predicted in the past");
		}
		return event;
	}

	public void collideWith(Particle targetParticle, EventInfo event) {
                Particle3D target = (Particle3D) targetParticle;
		//Moves this and referenced particle to given time and performs the collision

		//First, move the particles to the correct time
		double dt1=event.colTime-this.t0;
		double dt2=event.colTime-target.t0;
		this.x += this.xVel*dt1;
		this.y += this.yVel*dt1;
		this.z += this.zVel*dt1;
		target.x += target.xVel*dt2;
		target.y += target.yVel*dt2;
		target.z += target.zVel*dt2;

		//we need the distance between the particles, use this opportunity
		//to get the correct image if periodic boundaries
		double dX = target.x - this.x;
		double dY = target.y - this.y;
		double dZ = target.z - this.z;
		if ((simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
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

			//z image
			if ((event.side & Wall.BACK)==Wall.BACK) {
				if ((this.bFlag & Wall.BACK) == Wall.BACK)
					dZ -= simulationInfo.arenaZSize;
				else
					dZ += simulationInfo.arenaZSize;
			}
		}


		//Vectors for calculating the new velocity components
	    double nV1x, nV2x;		//x-comp of the new velocity vectors
		double nV1y, nV2y;		//y-comp of the new velocity vectors
		double nV1z, nV2z;		//z-comp of the new velocity vectors
		double Ax, Ay, Az;		//vector connecting centers of the particles
		double Bx, By, Bz;		//vector perpendicular to A
		double Cx, Cy, Cz;		//vecotr perpendicular to both A and B
		double V1a, V2a;			//Velocity vectors in the new coordinates
		double V1b, V2b;
		double V1c, V2c;
		double nV1a = 0.0, nV2a = 0.0;		//New velocity vector (only the a-comp of V changes)
		double realDc;			//real distance between the centers, if we
								//assume Dc=r1+r2 we get round large off errors

		realDc = Math.sqrt(dX*dX+dY*dY+dZ*dZ);

		//Normalize the A vector
		Ax = dX/realDc;
		Ay = dY/realDc;
		Az = dZ/realDc;


	    //Calculate the new velocity components after the collision occurs

		//First - calculate the vectors perpendicular to A, this is trickier than in
		//2D because we need to get a psuedo-random perpendicular vector that isn't
		//co-linear with the collision vector

		double dVx, dVy, dVz, Tx, Ty, Tz, Dot, Norm;

		//use the diff of vel vectors to form perpendicular
		//vector with a psuedo-random orientation
		dVx = target.xVel - xVel;
		dVy = target.yVel - yVel;
		dVz = target.zVel - zVel;
		Dot = dVx*Ax+dVy*Ay+dVz*Az; //dV*A

		//Temp vector needed to get B=(dV-A(dV*A))/|dV-A(dV*A)|
		//in other words, B = the portion of dV perpendicular to A (normalized)
		Tx = dVx - Ax*Dot;
		Ty = dVy - Ay*Dot;
		Tz = dVz - Az*Dot;
		Norm = Math.sqrt(Tx*Tx+Ty*Ty+Tz*Tz);

		if (Norm < 1e-10) {
		    //problem dV is collinear with A, try again

		    //initialize to 0, only one component will be set to 1
		    dVx=dVy=dVz=0;

		    //find which component has the smallest magnitude
		    //(in case A is approximately X,Y or Z)
		    Tx = Math.abs(Ax);
		    Ty = Math.abs(Ay);
		    Tz = Math.abs(Az);
		    if (Tx<=Ty && Tx<=Tz)
			dVx=1;
		    else if (Ty<=Tx && Ty<=Tz)
			dVy=1;
		    else //Tz smallest
			dVz=1;
		    Dot = dVx*Ax+dVy*Ay+dVz*Az; //dV*A
		    Tx = dVx - Ax*Dot;
		    Ty = dVy - Ay*Dot;
		    Tz = dVz - Az*Dot;
		    Norm = Math.sqrt(Tx*Tx+Ty*Ty+Tz*Tz);
		}


		//Now we have a well defined B (Norm != 0)
		Bx = Tx/Norm;
		By = Ty/Norm;
		Bz = Tz/Norm;
		//Now that we have B perp to A, we find C=AxB
		Cx = Ay*Bz-Az*By;
		Cy = Az*Bx-Ax*Bz;
		Cz = Ax*By-Ay*Bx;

		//Now transform the velocity vectors into A,B,C coordinates
		V1a = xVel*Ax+yVel*Ay+zVel*Az; //V1*A
		V1b = xVel*Bx+yVel*By+zVel*Bz; //V1*B
		V1c = xVel*Cx+yVel*Cy+zVel*Cz; //V1*C
		V2a = target.xVel*Ax+target.yVel*Ay+target.zVel*Az; //V2*A
		V2b = target.xVel*Bx+target.yVel*By+target.zVel*Bz; //V2*B
		V2c = target.xVel*Cx+target.yVel*Cy+target.zVel*Cz; //V2*C

                double targetMass = target.getMass();
		double dVa=V1a-V2a;
		double vRelSquared=dVa*dVa;
		double mu=((mass*targetMass)/(mass+targetMass));
		double eRel=0.5*mu*vRelSquared;

		double rmsVel = 0.0; //Units.toSim(Units.VELOCITY, getMain().getPredictor().getPrediction(Const.RMSVEL, getMain().statColor, false).avg, simulationInfo.dimension);
		double avgERel = 0.5*mu*(Math.pow(rmsVel, 2));
		//System.out.println("eRel = " + eRel);
		//System.out.println("avgERel = " + avgERel);

		double deltaE;
                Color thisColor = getDisplayColor();
                Color targetColor = target.getDisplayColor();

		// Reaction: blue to red
                if (simulationInfo.reactionMode && (event.colType == Collision.PARTICLE)) {
                    Color type0Color = simulationInfo.getParticleTypes().get(0).defaultColor;
                    Color type1Color = simulationInfo.getParticleTypes().get(1).defaultColor;
                    ReactionRelationship redToBlue = simulationInfo.getReactionRelationship(new HashSet<ParticleType>(simulationInfo.getParticleTypes()));

                    if (simulationInfo.reactionMode && (thisColor.equals(type0Color)) && (targetColor.equals(type0Color)) && (eRel > redToBlue.forwardActivationEnergy))
                    {
                            deltaE = redToBlue.forwardActivationEnergy - redToBlue.reverseActivationEnergy;
                            this.setColor(type1Color);
                            target.setColor(type1Color);
                            event.setDeltaBlue(+2); // update blue count cumulative statistics
                    } else if (simulationInfo.reactionMode && (thisColor.equals(type1Color)) && (targetColor.equals(type1Color)) && (eRel > redToBlue.reverseActivationEnergy) && !redToBlue.suppressReverseReaction)
                    {
                            deltaE = redToBlue.reverseActivationEnergy - redToBlue.forwardActivationEnergy;

                            this.setColor(type0Color);
                            target.setColor(type0Color);
                            event.setDeltaBlue(-2); // update blue count cumulative statistics
                    }
                    else
                            deltaE=0;
                } else {
                    deltaE = 0;
                }
                
		if(deltaE==0) { //No change in energy due to a reaction
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
                        double combinedMass = mass + targetMass;
                        double momentumTotal = mass * V1a + targetMass * V2a;
                        double velocityDifference = V2a - V1a;
                        double reducedMass = (mass * targetMass) / combinedMass;
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
                        double combinedMass = mass + targetMass;
                        double momentumTotal = mass * V1a + targetMass * V2a;
                        double velocityDifference = V2a - V1a;
                        double reducedMass = (mass * targetMass) / combinedMass;
                        double underRadical = velocityDifference * velocityDifference - energyWellPotential / reducedMass;

                        //If the expression to be square rooted is negative, there is not enough
                        //energy projecting the particles away from each other to get them out of the well.
                        //In this case a simple collision is done by conserving their momentum
                        if (underRadical < 0.0){
                            nV1a = (2 * targetMass * V2a + (mass - targetMass) * V1a) / combinedMass;
                            nV2a = (2 * mass * V1a + (targetMass - mass) * V2a) / combinedMass;
                            //The particles remain in the well as if this collision was an Enter well collision
                            event.colType = Collision.ENTER_WELL;
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
		}
		else
		{
			double rootVal=Math.sqrt(dVa*dVa-(2*deltaE/mu));
			double t=mass*V1a+targetMass*V2a;
			//Momentum transfer only occurs along the A vector
			nV1a = (t-targetMass*rootVal)/(mass+targetMass);
			nV2a = (t+mass*rootVal)/(mass+targetMass);
		}


		//Now transform the velocity vectors back into x,y,z coordinates
		//We really want nV1x = X*nV1, nv1y = Y*nV1, etc, where X,Y and Z
		//are the original axis in A,B and C coordinates.  Instead of wasting
		//time redeclaring new variables, we say that Xa = Ax, Xb = Bx, etc.
		nV1x = Ax*nV1a+Bx*V1b+Cx*V1c; //X*nV1
		nV1y = Ay*nV1a+By*V1b+Cy*V1c; //Y*nV1
		nV1z = Az*nV1a+Bz*V1b+Cz*V1c; //Z*nV1
		nV2x = Ax*nV2a+Bx*V2b+Cx*V2c; //X*nV2
		nV2y = Ay*nV2a+By*V2b+Cy*V2c; //Y*nV2
		nV2z = Az*nV2a+Bz*V2b+Cz*V2c; //Z*nV2


	    //Set the new velocity components
	    xVel = nV1x;
	    target.xVel = nV2x;
		yVel = nV1y;
		target.yVel = nV2y;
		zVel = nV1z;
		target.zVel = nV2z;


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
		double oldKE = this.getKE();

                
		if(thermostat.isEnabled() && thermostat.isActive())
		{
			velAdjust = thermostat.calcModifier(event, this);
			//resetStafts = true;
		}

                double barrierLeftX = arena.barrierLeftX;
                double barrierRightX = arena.barrierRightX;
		switch (event.colType) {
			case Collision.WALL:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						y += yVel*dt;
						z += zVel*dt;
						xVel = -xVel * velAdjust;
						break;
					case Wall.RIGHT:
						x = simulationInfo.arenaXSize-radius;
						y += yVel*dt;
						z += zVel*dt;
						xVel = -xVel * velAdjust;
						break;
					case Wall.BOTTOM:
						y = radius;
						x += xVel*dt;
						z += zVel*dt;
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
                                                            new double[] { xVel, yVel, zVel },
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
						z += zVel*dt;
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
						break;
					case Wall.BACK:
						z = radius;
						x += xVel*dt;
						y += yVel*dt;
						zVel = -zVel * velAdjust;
						//double check x position - round off error can cause it to overlap the boundary
						//i.e. essentially the particle hits the corner touching both boundaries at the same time
						if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
							if (xVel > 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierLeftX-radius;
								xVel = -xVel;
							}
							else if (xVel < 0 && (x >= barrierLeftX-radius && x <= barrierRightX+radius)) {
								x = barrierRightX+radius;
								xVel = -xVel;
							}
						}
						break;
					case Wall.FRONT:
						z = simulationInfo.arenaZSize-radius;
						x += xVel*dt;
						y += yVel*dt;
						zVel = -zVel * velAdjust;
						//double check x position - round off error can cause it to overlap the boundary
						//i.e. essentially the particle hits the corner touching both boundaries at the same time
						if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA || simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
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
			case Collision.PISTON:
				switch(event.side) 
				{
					case Wall.BOTTOM:
						//TODO: insert appropriate response for isothermal reaction
						//piston.moveToTime(event.colTime);

						y += yVel * dt;
						x += xVel * dt;
						z += zVel * dt;
						
						if(piston.isMoving(event.colTime)) {
							yVel = 2.0 * piston.getVelocity() - this.yVel;}
						else {
							yVel = -yVel * velAdjust;}
				}
				break;
			case Collision.BARRIER:
				switch (event.side) {
					case Wall.LEFT:
						x = barrierLeftX-radius;
						y += yVel*dt;
						z += zVel*dt;
						xVel = -xVel;
						break;
					case Wall.RIGHT:
						x = barrierRightX+radius;
						y += yVel*dt;
						z += zVel*dt;
						xVel = -xVel;
				}
				break;
			case Collision.EDGE:
				x += xVel*dt;
				y += yVel*dt;
				z += zVel*dt;
                                double edgeX = arena.edgeX;
                                double[] edgeY = arena.getEdgeY();
                                double[] edgeZ = arena.getEdgeZ();
				EdgeCollide(edgeX, edgeY[event.side], edgeZ[event.side]);
				break;
			case Collision.BOUNDARY:
				switch (event.side) {
					case Wall.LEFT:
						x = radius;
						y += yVel*dt;
						z += zVel*dt;
						bFlag |= Wall.LEFT; //set the boundary flag
						break;
					case Wall.RIGHT:
						x = -radius;
						y += yVel*dt;
						z += zVel*dt;
						bFlag |= Wall.LEFT; //set the boundary flag
						break;
					case Wall.BOTTOM:
						y = radius;
						x += xVel*dt;
						z += zVel*dt;
						bFlag |= Wall.BOTTOM; //set the boundary flag
						break;
					case Wall.TOP:
						y = -radius;
						x += xVel*dt;
						z += zVel*dt;
						bFlag |= Wall.BOTTOM; //set the boundary flag
						break;
					case Wall.BACK:
						z = radius;
						x += xVel*dt;
						y += yVel*dt;
						bFlag |= Wall.BACK; //set the boundary flag
						break;
					case Wall.FRONT:
						z = -radius;
						x += xVel*dt;
						y += yVel*dt;
						bFlag |= Wall.BACK; //set the boundary flag
				}
				break;
			case Collision.EOB:
				switch (event.side) {
					case Wall.LEFT:
						x = simulationInfo.arenaXSize-radius;
						y += yVel*dt;
						z += zVel*dt;
						bFlag &= (~Wall.LEFT); //remove the flag
						break;
					case Wall.RIGHT:
						x = radius;
						y += yVel*dt;
						z += zVel*dt;
						bFlag &= (~Wall.LEFT); //remove the flag
						break;
					case Wall.BOTTOM:
						y = simulationInfo.arenaYSize-radius;
						x += xVel*dt;
						z += zVel*dt;
						bFlag &= (~Wall.BOTTOM); //remove the flag
						break;
					case Wall.TOP:
						y = radius;
						x += xVel*dt;
						z += zVel*dt;
						bFlag &= (~Wall.BOTTOM); //remove the flag
						break;
					case Wall.BACK:
						z = simulationInfo.arenaZSize-radius;
						x += xVel*dt;
						y += yVel*dt;
						bFlag &= (~Wall.BACK); //remove the flag
						break;
					case Wall.FRONT:
						z = radius;
						x += xVel*dt;
						y += yVel*dt;
						bFlag &= (~Wall.BACK); //remove the flag
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
	}

	private void EdgeCollide(double edgeXPos, double edgeYPos, double edgeZPos) {
		//performs a collision with the edge 'particle' of infinite mass
		//(i.e. just reverse the component of velocity directed along the
		//vector connecting the centers of the particles)

		//we need the distance between the particles
		double dX = edgeXPos - this.x;
		double dY = edgeYPos - this.y;
		double dZ = edgeZPos - this.z;

		//Vectors for calculating the new velocity components
	    double nV1x; //, nV2x;		//x-comp of the new velocity vectors
		double nV1y; //, nV2y;		//y-comp of the new velocity vectors
		double nV1z; //, nV2z;		//z-comp of the new velocity vectors
		double Ax, Ay, Az;		//vector connecting centers of the particles
		double Bx, By, Bz;		//vector perpendicular to A
		double Cx, Cy, Cz;		//vecotr perpendicular to both A and B
		double V1a, V1b, V1c;	//Velocity vectors in the new coordinates
		double nV1a;				//New velocity vector (only the a-comp of V changes)
		double realDc;			//real distance between the centers, if we
								//assume Dc=r1+r2 we get round large off errors

		realDc = Math.sqrt(dX*dX+dY*dY+dZ*dZ);

		//Normalize the A vector
		Ax = dX/realDc;
		Ay = dY/realDc;
		Az = dZ/realDc;


	    //Calculate the new velocity components after the collision occurs

		//First - calculate the vectors perpendicular to A, this is trickier than in
		//2D because we need to get a psuedo-random perpendicular vector that isn't
		//co-linear with the collision vector

		double dVx, dVy, dVz, Tx, Ty, Tz, Dot, Norm;

		//use the diff of vel vectors to form perpendicular
		//vector with a psuedo-random orientation
		dVx = xVel;
		dVy = yVel;
		dVz = zVel;
		Dot = dVx*Ax+dVy*Ay+dVz*Az; //dV*A

		//Temp vector needed to get B=(dV-A(dV*A))/|dV-A(dV*A)|
		//in other words, B = the portion of dV perpendicular to A (normalized)
		Tx = dVx - Ax*Dot;
		Ty = dVy - Ay*Dot;
		Tz = dVz - Az*Dot;
		Norm = Math.sqrt(Tx*Tx+Ty*Ty+Tz*Tz);

		if (Norm < 1e-10) {
		    //problem dV is collinear with A, try again

		    //initialize to 0, only one component will be set to 1
		    dVx=dVy=dVz=0;

		    //find which component has the smallest magnitude
		    //(in case A is approximately X,Y or Z)
		    Tx = Math.abs(Ax);
		    Ty = Math.abs(Ay);
		    Tz = Math.abs(Az);
		    if (Tx<=Ty && Tx<=Tz)
			dVx=1;
		    else if (Ty<=Tx && Ty<=Tz)
			dVy=1;
		    else //Tz smallest
			dVz=1;
		    Dot = dVx*Ax+dVy*Ay+dVz*Az; //dV*A
		    Tx = dVx - Ax*Dot;
		    Ty = dVy - Ay*Dot;
		    Tz = dVz - Az*Dot;
		    Norm = Math.sqrt(Tx*Tx+Ty*Ty+Tz*Tz);
		}


		//Now we have a well defined B (Norm != 0)
		Bx = Tx/Norm;
		By = Ty/Norm;
		Bz = Tz/Norm;
		//Now that we have B perp to A, we find C=AxB
		Cx = Ay*Bz-Az*By;
		Cy = Az*Bx-Ax*Bz;
		Cz = Ax*By-Ay*Bx;

		//Now transform the velocity vectors into A,B,C coordinates
		V1a = xVel*Ax+yVel*Ay+zVel*Az; //V1*A
		V1b = xVel*Bx+yVel*By+zVel*Bz; //V1*B
		V1c = xVel*Cx+yVel*Cy+zVel*Cz; //V1*C

		//Momentum transfer only occurs along the A vector
		//nV1a = (2*target.mass*V2a+(mass-target.mass)*V1a)/(mass+target.mass);
		nV1a = -V1a;

		//Now transform the velocity vectors back into x,y,z coordinates
		//We really want nV1x = X*nV1, nv1y = Y*nV1, etc, where X,Y and Z
		//are the original axis in A,B and C coordinates.  Instead of wasting
		//time redeclaring new variables, we say that Xa = Ax, Xb = Bx, etc.
		nV1x = Ax*nV1a+Bx*V1b+Cx*V1c; //X*nV1
		nV1y = Ay*nV1a+By*V1b+Cy*V1c; //Y*nV1
		nV1z = Az*nV1a+Bz*V1b+Cz*V1c; //Z*nV1


	    //Set the new velocity components
	    xVel = nV1x;
		yVel = nV1y;
		zVel = nV1z;
	}
	

    public PartState getState(PartState returnState) {
		//This function is primarily used by UpdateQ to get info needed for display
//		PartState output = new PartState(x, y, z, radius, getDisplayColor(), bFlag);
//                output.position = new double[] { x, y, z };
//                output.velocity = new double[] { xVel, yVel, zVel };
//                output.particleType = particleType;       
                returnState.x = x;
                returnState.y = y;
                returnState.z = z;
                returnState.rad = radius;
                returnState.color = getDisplayColor();
                returnState.bFlag = bFlag;
                returnState.position[0] = x;
                returnState.position[1] = y;
                returnState.position[2] = z;
                returnState.velocity[0] = xVel;
                returnState.velocity[1] = yVel;
                returnState.velocity[2] = zVel;
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
		return z;
    }

    public double getTheta() {
		double theta = 0.0;
                // Special cases:
                // - if xVel == 0 and yVel != 0, the ratio is + or - infinity, which produces the correct result
                // - if xVel == 0 and yVel == 0, the ration is NaN, which produces NaN
                theta = Math.atan(yVel/xVel) + (xVel < 0.0 ? Math.PI : 0.0);

                if (Double.isNaN(theta))
                        theta = 0.0;

		return theta;
	}

    public double getPhi() {
		double phi = 0.0;
		double vel = Math.sqrt(xVel*xVel+yVel*yVel+zVel*zVel);
                if (vel == 0.0) { // avoid NaN outputs
                        phi = 0.0;
                } else {
                        phi = Math.acos(zVel/vel);
                }

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
		zVel = snapSubtract (zVel, zAdj / mass);
	}

    public void adjust(double fudgeFactor) {
		//fudgeFactor = sqrt(OriginalKE/CurrentKE)
		//note that KE is proportional to xVel^2+yVel^2+zVel^2 so multiplying each
		//component by fudgeFactor is the same as multiplying the total KE by
		//OrigKE/CurrKE, thus reproducing the original KE
		xVel *= fudgeFactor;
		yVel *= fudgeFactor;
		zVel *= fudgeFactor;
	}

    public double getXMom() {
                double mass = getMass();
		return xVel*mass;
	}
    public double getYMom() {
                double mass = getMass();
                return yVel*mass;
	}
    public double getZMom() {
                double mass = getMass();
                return zVel*mass;
	}

    public double getXVel() {
        return xVel;
    }

    public double getYVel() {
        return yVel;
    }

    public double getZVel() {
        return zVel;
    }

    public double getKE() {
        double energy = Formulas.kineticEnergy(mass, Formulas.magnitude(xVel, yVel, zVel));
        return Units.convert(Energy.AMU_JOULE, Energy.JOULE, energy);
    }

    public double getVel() {
		return Math.sqrt(xVel*xVel+yVel*yVel+zVel*zVel);
	}

    public double getVel2() {
		return xVel*xVel+yVel*yVel+zVel*zVel;
	}

    public void reverse() {
                xVel = -xVel;
		yVel = -yVel;
		zVel = -zVel;
    }

    public void setBinNum(int newBinNum) {
        binNum = newBinNum;
    }

    @Override
    public double[] getPosition() {
        return new double[] { x, y, z };
    }

    @Override
    public void reset() {
        xVel = startXVel;
        yVel = startYVel;
        zVel = startZVel;
        x = startX;
        y = startY;
        z = startZ;

        this.arena = simulationInfo.arenaInfo;

        this.bFlag = 0;
        this.binNum=0;
        t0 = startTime;
        cumTime = 0.0;
        
        //ensure that bFlag is set properly

        if ((simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES)) {
                //if touching the x boundary, ensure that it's on the left and mark
                //the boundary flag
                if (this.x<0)
                        this.x+=simulationInfo.arenaXSize;
                if (this.y<0)
                        this.y+=simulationInfo.arenaYSize;
                if (this.z<0)
                        this.z+=simulationInfo.arenaZSize;
                if (x<radius) {
                        this.bFlag |= Wall.LEFT;
                }
                else if (simulationInfo.arenaXSize-x<radius) {
                        this.x -= simulationInfo.arenaXSize;
                        if(this.x<radius)
                                this.bFlag |= Wall.LEFT;
                }

                //same as above but for y boundary, ensure that it's on the bottom
                if (y<radius) {
                        this.bFlag |= Wall.BOTTOM;
                }
                else if (simulationInfo.arenaYSize-y<radius) {
                        this.y -= simulationInfo.arenaYSize;
                        if(this.y<radius)
                                this.bFlag |= Wall.BOTTOM;
                }

                //same as above but for y boundary, ensure that it's on the bottom
                if (z<radius) {
                        this.bFlag |= Wall.BACK;
                }
                else if (simulationInfo.arenaZSize-z<radius) {
                        this.z -= simulationInfo.arenaZSize;
                        if(this.z<radius)
                                this.bFlag |= Wall.BACK;
                }
        }
    }
}
