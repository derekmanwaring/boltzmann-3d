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
package edu.byu.chem.boltzmann.fullapplication.view.simulationsettings;

import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import java.awt.Color;
import java.util.Random;

/**
 * Created 10 May 2011
 * @author Derek Manwaring
 * Provides default information to the simulation settings view for new particle
 * types.
 */
public class DefaultParticleInfo {

    //Default number of particles for new types
    private static final int DEFAULT_NUM_PARTICLES = 10;

    //Default info for a new particle type
    private static final double DEFAULT_MASS = 2; //AMU
    private static final double DEFAULT_RADIUS = 
            Units.convert("nm", "m", 1); //meters

    public static final Color RED = new Color(255, 51, 51);
    public static final Color BLUE = new Color(16, 92, 191);
    public static final Color GREEN = new Color(122, 232, 25);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color PURPLE = new Color(142, 129, 188);

    private static final Color[] DEFAULT_COLORS = new Color[] {
        RED,
        BLUE,
        GREEN,
        YELLOW,
        PURPLE
    };

    private static final String[] DEFAULT_NAMES = new String[] {
        "Red",
        "Blue",
        "Green",
        "Yellow",
        "Purple"
    };

    private static final Random randomNums = new Random();

    /**
     * @return A particle type with default information for the nth particle type.
     * (Where n is typeNumber). N starts at zero.
     */
    public static ParticleType getDefaultType(int typeNumber) {
        Color typeColor = null;
        if (typeNumber < DEFAULT_COLORS.length) {
            typeColor = DEFAULT_COLORS[typeNumber];
        } else {
            typeColor = randomColor();
        }

        String typeName = null;
        if (typeNumber < DEFAULT_NAMES.length) {
            typeName = DEFAULT_NAMES[typeNumber];
        } else {
            typeName = "Type " + (typeNumber + 1);
        }

        return new ParticleType(DEFAULT_MASS, DEFAULT_RADIUS, typeColor, typeName);
    }

    private static Color randomColor() {
        return new Color (
                randomRGBValue(),
                randomRGBValue(),
                randomRGBValue()
                );
    }

    private static int randomRGBValue() {
        return randomNums.nextInt(224) + 32; //Make it at least a little bright.
    }

    protected static int getDefaultNumParticles(int typeNumber) {
        return DEFAULT_NUM_PARTICLES;
    }
}
