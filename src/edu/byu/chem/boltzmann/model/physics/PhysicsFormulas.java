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

import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Energy;

/**
 * Created 7 Jun 2011
 * @author Derek Manwaring
 */
public class PhysicsFormulas {
    
    //Maximum change to normal component of a particle's kinetic energy is 10% during
    //collisions with a heat reservoir.
    private static double MAX_NORMAL_KE_CHANGE = 0.1;
    
    /**
     * @return How much the normal component of velocity should changed 
     */
    public static double calculateVelocityChange(
            double[] velocity, 
            double normalComponent,
            double mass,
            double reservoirTemperature,
            int dimension) {
        
        double currentKE = Formulas.kineticEnergy(mass, Formulas.magnitude(velocity));
        double normalKE = Formulas.kineticEnergy(mass, normalComponent);
        
        double rightKEForTempJoules = Formulas.avgKineticEnergy(reservoirTemperature, dimension);
        double rightKEForTemp = Units.convert(Energy.JOULE, Energy.AMU_JOULE, rightKEForTempJoules);
        
        double energyDifference = Math.abs(rightKEForTemp - currentKE);
        double maxEnergyChange = normalKE * MAX_NORMAL_KE_CHANGE;
        
        double energyAdjustment = Math.min(energyDifference, maxEnergyChange);
        
        double velocityChange = Formulas.speed(energyAdjustment, mass);
        
        if (rightKEForTemp > currentKE) {
            velocityChange = -velocityChange;
        }
        
        return velocityChange;
    }
}
