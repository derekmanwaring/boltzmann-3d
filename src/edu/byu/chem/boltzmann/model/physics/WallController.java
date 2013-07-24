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
import edu.byu.chem.boltzmann.utils.Units.Energy;

/**
 * Concrete implementation of a ThermostatController that 
 * 	uses wall collisions to adjust the Thermostat
 * @author Jared
 *
 */
public class WallController implements ThermostatController {

	public static double MINGAMMA2 = .5;
	public static double MAXGAMMA2 = 1.5;
	

	public double calculateAdjustment(Thermostat t, EventInfo event, Particle p) {
		
		double normalKE, gammaSquared;
		
		normalKE = getNormalKE(event, p);
		
		gammaSquared = calcGammaSquared(t, normalKE);			
		
		return Math.sqrt(gammaSquared);
	}

	/**
	 * @param t
	 * @param normalKE
	 * @return
	 */
	private double calcGammaSquared(Thermostat t, double normalKE) {
		double gammaSquared;
		
		if(t.getDeltaKE() < normalKE) {
			gammaSquared = 1.0 - (t.getDeltaKE() / normalKE);
		} else {
			gammaSquared = 1.0 - (t.getDampFactor() * t.getDeltaKE() / normalKE);
		}
		
		return clipGammaSquared(gammaSquared);
	}
	
	private double clipGammaSquared(double gammaSquared)
	{
		if(gammaSquared < MINGAMMA2)
			gammaSquared = MINGAMMA2;
		else if(gammaSquared > MAXGAMMA2)
			gammaSquared = MAXGAMMA2;
		return gammaSquared;
	}

	/**
	 * @param event
	 * @param p
	 * @return
	 */
	private double getNormalKE(EventInfo event, Particle p) {
            double mass = p.mass;
            double normalComponent = getNormalVelocityComponent(event, p);
            double energy = 0.5 * mass * normalComponent * normalComponent;
            return Units.convert(Energy.AMU_JOULE, Energy.JOULE, energy);
	}
	
	/**
	 * Get the component of the velocity perpendicular to the collision surface
	 * @param event the event
	 * @param p the particle
	 * @return the normal component of the velocity
	 */
	private double getNormalVelocityComponent(EventInfo event, Particle p) {
		double normalComponent = 0.0;	
		
		if(event.colType == Collision.WALL) {
			if(event.side == Wall.LEFT || event.side == Wall.RIGHT)
				normalComponent = p.getXVel();
			else if(event.side == Wall.BACK || event.side == Wall.FRONT)
				normalComponent = p.getYVel();
			else
				normalComponent = p.getYVel();
		} else if(event.colType == Collision.PISTON) {
			normalComponent = p.getYVel();
		}
		
		return normalComponent;			
	}

}
