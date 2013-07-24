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
 * Conatians constants used to refer to sides of the arena, sides of the dividing wall or to
 * particle images (periodic boundaries).  Basically used to make the physics logic more readable
 * Note: these constants are used as bit-flags with particle images so the constants are powers of two
 * @author Derek Manwaring
 */
public class Wall {
		//Used in dealing with events to indicate which side of a boundary needs to
		//be dealt with - wall, boundary and eob (end of boundary) events need all of
		//these (in 3D), barrier only needs left and right and edge (in 2D) needs
		//top and bottom.  The numbers are powers of two to allow periodic boundaries
		//to keep track of which boundaries the particle is sitting on (i.e. uses them
		//as flags)

		public static final int
			LEFT	= 1,
			RIGHT	= 2,
			BOTTOM	= 4,
			TOP		= 8,
			BACK	= 16,
			FRONT	= 32;
}
