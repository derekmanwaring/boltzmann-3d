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

/**
 * Defines the manner in which the Thermostat affects particle movement
 * @author Jared
 *
 */
public interface ThermostatController {

	/**
	 *  Calculates an adjustment factor for the event under consideration
	 * @param t the Thermostat
	 * @param event the Event (a collision)
	 * @param p the Particle in consideration
	 * @return the adjustment factor to be applied
	 */
	public double calculateAdjustment(Thermostat t, EventInfo event, Particle p);
	
}
