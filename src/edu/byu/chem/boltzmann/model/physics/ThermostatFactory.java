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
 * Generates the appropriate ThermostatController for the Thermostat
 * @author Jared
 *
 */
public class ThermostatFactory {

	public static final int FRICTIONTHERMOSTAT = 1, WALLTHERMOSTAT = 0;

	/**
	 * Given the mode of the Thermostat, returns the appropriate ThermostatController
	 * @param thermostatMode the current mode of the Thermostat
	 * @return the appropriate ThermostatController
	 */
	public static ThermostatController createController(int thermostatMode) {
		
            switch(thermostatMode) {
                case WALLTHERMOSTAT:
                    return new WallController();
                default:
                    return new WallController();
            }
	}
}
