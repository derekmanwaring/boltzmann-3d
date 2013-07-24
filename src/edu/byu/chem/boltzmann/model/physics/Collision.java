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
 * Contains constants used to refer to different types of collision.  Basically used
 * to make the logic in Physics more readable
 * @author Derek Manwaring
 */
public class Collision {

		// Used in dealing with events to indicate type of collision
		/** Denotes a normal particle-particle collision */
		public static final int PARTICLE 	= 0;	// normal particle-particle collision
		public static final int
			WALL 		= 1, 	// reflecting boundary conditions
			BOUNDARY 	= 2, 	// periodic boundaries - entering overlap region
			EOB 		= 3,	// periodic boundaries - leaving overlap region
			BARRIER 	= 4,	// dividing wall (with reflecting boundaries)
			EDGE 		= 5,	// edge of hole in a dividing wall
			PISTON		= 6,	// piston collision
			ENTER_WELL 	= 7,
			EXIT_WELL       = 8,
                        ENTER_GRAVITY_WELL = 9,
                        EXIT_GRAVITY_WELL = 10,
                        WELL_REFLECT    = 18;

		//update flags used to tell CalUpdate what needs to be done
		//(start at 10 to help trap errors if not initialized properly - i.e. a collision
		//type won't be confused with an update flag)
		public static final int
			PARTICLE_1	= 17,	//only 1 particle needs updating
			PARTICLE_2	= 11,	//2 particles need updating
			PARTICLE_ALL= 12,	//update all particle-particle interactions
			BOUNDARY_ALL= 13,	//update all particle-boundary interactions
			EVERYTHING	= 14,	//complete prediction of all events
			RESORT		= 15,	//just re-sort the calendar
			PISTON_ALL  = 16;   //update all particle-piston interactions
		//Note: these flags are placed here to avoid too much redundancy - the CalUpdate
		//method uses the particle indices and boundary info (i.e. left, right, etc)
		//to correctly predict new events, etc, but some user events need to set mass
		//prediction flags
}
