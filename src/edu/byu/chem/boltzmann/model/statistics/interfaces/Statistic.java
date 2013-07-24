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
package edu.byu.chem.boltzmann.model.statistics.interfaces;

import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;

/**
 * The most general interface for statistics. Statistics must implement this
 * interface to be updated by the physics code.
 * 
 * @author Derek Manwaring
 * 16 May 2012
 */
public interface Statistic {
    
    /**
     * Lets the statistic know about an event that occurred in the simulation.
     * @param event 
     */
    public void notifyOfEvent(EventInfo event);
    
    /**
     * Lets the statistic know what time it currently is in the simulation. This
     * is called chronologically by the physics thread at the end of every frame
     * it processes.
     * @param simTime 
     */
    public void notifyOfSimulationTime(double simTime);
    
    /**
     * Clears the statistic's collected data and restarts it as if from time zero.
     */
    public void reset();
    
    /**
     * Clears the statistic's collected data.
     */
    public void clear();

    /**
     * Notifies the statistic to use finite system corrections in its calculations
     * @param corrections 
     */
    public void setFiniteSysCorrections(boolean corrections);

    /**
     * Notifies the statistic to use real gas corrections in its calculations
     * @param corrections 
     */
    public void setRealGasCorrections(boolean corrections);
    
    /**
     * Tells the statistic about the frame to be used for the next non-cumulative
     * calculations.
     * @param frame
     */
    public void useFrameForCurrentCalculations(FrameInfo frame);
    
}
