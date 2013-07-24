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
package edu.byu.chem.boltzmann.utils.data;

import java.awt.Color;

/**
 *
 * @author Derek Manwaring
 */
public class ParticleType {

    public final String displayName;

    /** Mass in AMU's */
    public final double particleMass;
    
    /*
     * Radius in meters (usually on the order of 1.0E-9)
     * 
     * this used to be final, but SimSettingsController needs to be
     * able to shrink particle types when no particle is small enough
     * to fit in the arena (otherwise, there are no particles, and
     * the temperature setting becomes zero, which is annoying)
     */
    public double particleRadius;
    public final Color defaultColor;

    /**
     * @param mass Mass in AMU's
     * @param radius Radius in meters (usually on the order of 1.0E-9)
     * @param defaultColor
     * @param displayName
     */
    public ParticleType(double mass, double radius, Color defaultColor, String displayName){
        particleMass = mass;
        particleRadius = radius;
        this.defaultColor = defaultColor;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
