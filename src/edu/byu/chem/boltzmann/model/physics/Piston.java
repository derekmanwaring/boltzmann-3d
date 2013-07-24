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

import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;

/**
 * @author Jared
 *
 */
public class Piston {
    
	public static final int MASS = 1, PERCENT = 0;
        
        public static final PistonMode DEFAULT_MODE = PistonMode.ADIABATIC;

        public enum PistonMode {
            ADIABATIC,
            INSTANTANEOUS,
            ISOTHERMAL;
        }
	
	public static final double INIT_VEL_MULTIPLIER = 0.01;
	public static final double INIT_VEL_MODIFIER = 0.5;
        public static final double PISTON_SHAFT_RADIUS = 10.0;//35.0,
        public static final double PISTON_VALVE_RADIUS = 20.0;
	
	protected double movementMode;
	protected double curLevel, targetLevel, maxLevel;
	
	//Piston has only one relevant velocity and position component
	protected double shaftRadius, valveRadius;
	protected double t0;	
	//protected double velocityMultiplier;
	//protected double velocityModifier; //Velocity is multiplied by this value - default is 1
	
	//Used to track when the piston comes to a halt.
	private boolean hasMoved = false;
        
        private double speed = 200.0;

        private SimulationInfo simInfo = null;
	
	public Piston(double time, double curLevel, double maxLevel, SimulationInfo simInfo)
	{
		this.movementMode = PERCENT;
		this.curLevel = curLevel;
		this.targetLevel = curLevel;
		this.maxLevel = maxLevel;
		this.t0 = time;
                
                this.simInfo = simInfo;
                
		this.shaftRadius = Piston.PISTON_SHAFT_RADIUS;
		this.valveRadius = Piston.PISTON_VALVE_RADIUS;
	}	
	
	/**
	 * Indicates if the Piston is moving at a certain time
	 * @param time the time to check
	 * @return true if the piston is moving, false otherwise
	 */
	public boolean isMoving(double time) 
	{
		boolean isMoving = !isStopped() && (time < getStopTime());
		if (isMoving)
			hasMoved = true;
		return isMoving;
	}	
	
	/**
	 * Indicates if the Piston has arrived at its target level 
	 * @return true if so, false otherwise
	 */
	public boolean isStopped()
	{
		boolean isStopped = (getCurLevel() == getTargetLevel());
		if (isStopped && hasMoved)
		{
			hasMoved = false;
//			main.getPredictor().predictBluePercent();
			
			//Reset cumulative distributions.
//			if (main.getPhysics() != null)
////				main.getPhysics().ResetStats();
//			try {main.getPredictor().rescaleAbscissaLimits(true);}
//			catch (Exception e){}
		}
		return isStopped;
	}
	
	/**
	 * Indicates if the Piston is visible
	 * @return true if so, false otherwise
	 */
	public boolean isVisible() 
	{				
		return  (getPosition() >= getRelevantArenaDim() ||
						getCurLevel() <= 0.0 ||
						simInfo.arenaType != ArenaType.MOVABLE_PISTON) ? false : true;
	}	
	
	/**
	 * Advances the Piston to the given time, adjusting its level appropriately
	 * @param time the new time
	 */
	public void MoveToTime (double time)
	{		
		if( isMoving(time) ) {
			setCurLevel( getCurLevel() + (time - getT0()) * getLevelVelocity());
		} else if( isMoving(t0) && !isMoving(time) ) {
			setCurLevel( getTargetLevel() );
		}
                
//                System.out.println("velocity\"Level\" " + getLevelVelocity());
//                System.out.println("velocity " + getVelocity());
		
		setT0( time );
	}
	
	/**
	 * Sets the target level (as % of the arena dimension) of the piston
	 * @param targetLevel the target level
	 */
	public void setTargetLevel(double targetLevel) {
		this.targetLevel = targetLevel;
	}
	
	/**
	 * Gets the position of the piston in the arena in meters
	 * @return the piston's position
	 */
	public double getPosition()
	{
                double position = (1.0 - getCurLevel()) * getRelevantArenaDim();
		return position; //Length.convert(Length.METERS, Length.NANOMETERS, position);
	}
	
	public double getPositionAtTime(double t)
	{
            if( isMoving(t) ) {
                return getPosition() + getVelocity() * (t - getT0());
            } else {
                return getStopPosition();
            }
	}
	
	public double getVelocityAtTime(double t)
	{
		if ( isMoving(t) )
			return getVelocity();
		else
			return 0.0;
	}
	
	/**
	 * Gets the rate of change of the piston level 
	 * @return the velocity level
	 */
	public double getLevelVelocity()
	{
            double velocity = ((getTargetLevel() < getCurLevel()) ? -1.0 : 1.0)
					*  speed / getRelevantArenaDim();
		return velocity;
	}
	
	/**
	 * Gets the rate of the change of the piston position
	 * @return the piston's velocity
	 */
	public double getVelocity()
	{
//            return -1.0 * getLevelVelocity();
            return -1.0 * getLevelVelocity() * getRelevantArenaDim();
	}
	
	/**
	 * Gets the target resting position of the piston
	 * @return the stopping position
	 */
	public double getStopPosition()
	{
		return (1.0 - targetLevel) * getRelevantArenaDim();
	}	
	
	/**
	 * Gets the stopping time of the piston
	 * @return the stopping time if the piston is moving, otherwise the piston's 
	 * 		current time
	 */
	public double getStopTime()
	{
		return getT0() + 
				Math.abs((getTargetLevel() - getCurLevel()) / getLevelVelocity());
	}

//	public double getVelocityMultiplier() {
//		return velocityMultiplier;
//	}
//
//	public void setVelocityMultiplier(double velocityMultiplier) {
//		this.velocityMultiplier = velocityMultiplier;
//	}
        
        public void setSpeed(double speed) {
            this.speed = speed;
        }

	public double getCurLevel() {
		return curLevel;
	}

	public void setCurLevel(double curLevel) {
		this.curLevel = curLevel;
	}

	public double getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(double maxLevel) {
		this.maxLevel = maxLevel;
	}

	public double getMovementMode() {
		return movementMode;
	}

	public void setMovementMode(double movementMode) {
		this.movementMode = movementMode;
	}

	public double getT0() {
		return t0;
	}

	private void setT0(double t0) {
		this.t0 = t0;
	}

	public double getTargetLevel() {
		return targetLevel;
	}
	
	/**
	 * Get the State of the Piston (its position and visibility)
	 * @param divideStatus the current status of the simulation
	 * @return the PistonState with relevant information
	 */
	public PistonState GetState(){
		return new PistonState(getPosition(),isVisible());
	}

	public double getShaftRadius() {
		return shaftRadius;
	}

	public void setShaftRadius(double shaftRadius) {
		this.shaftRadius = shaftRadius;
	}

	public double getValveRadius() {
		return valveRadius;
	}

	public void setValveRadius(double valveRadius) {
		this.valveRadius = valveRadius;
	}
	
//	public void setVelocityModifier(double velocityModifier){
//		this.velocityModifier = velocityModifier;
//	}
//	
//	public double getVelocityModifier(){
//		return this.velocityModifier;
//	}
	
	public int getDimension() {
		return simInfo.dimension;
	}
	
	public double getRelevantArenaDim() {
		double dim = 0.0;
		switch(getDimension()) {
		case 1:				
			dim = simInfo.arenaXSize;
			break;
		case 2:
			dim = simInfo.arenaYSize;
			break;
		case 3:
			dim = simInfo.arenaYSize;
		}
		return dim;
	}
	
}

