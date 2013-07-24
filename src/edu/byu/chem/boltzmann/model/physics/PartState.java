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
import java.awt.Color;

/**
 * @author Derek Manwaring
 */
public class PartState {

		public double x, y, z, rad;
                
		public Color color;

		/** Indicates boundary overlap: 0 or ORed values of Wall.LEFT, Wall.BOTTOM, Wall.BACK */
		public int bFlag;

		public PartState (double x, double y, double z, double rad, Color color, int boundaryFlag) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.rad = rad;
			this.color = color;
			this.bFlag = boundaryFlag;
                        this.position = new double[3];
                        this.velocity = new double[3];
		}

                public double[] position;
                public double[] velocity;
                public ParticleType particleType;

                public PartState(double[] position, double[] velocity, ParticleType particleType, Color color, int boundaryFlag) {
                    this.x = position[0];
                    this.y = position[1];
                    this.z = position[2];
                    this.rad = particleType.particleRadius;
                    this.position = position;
                    this.velocity = velocity;
                    this.particleType = particleType;
                    this.color = color;
                    this.bFlag = boundaryFlag;
                }
}
