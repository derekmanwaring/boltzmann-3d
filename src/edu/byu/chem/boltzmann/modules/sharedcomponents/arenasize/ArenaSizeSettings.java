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
package edu.byu.chem.boltzmann.modules.sharedcomponents.arenasize;

import edu.byu.chem.boltzmann.utils.Units.Length;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEvent;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEventListener;

/**
 *
 * @author Derek Manwaring
 * 17 Nov 2011
 */
public interface ArenaSizeSettings {
    
    public class ArenaSizeChangedEvent extends BoltzmannEvent {        
    }
    
    public interface ArenaSizeChangedListener extends BoltzmannEventListener<ArenaSizeChangedEvent> {
    }
    
    public void addListener(ArenaSizeChangedListener listener);
    
    public void setSize(double xSize, double ySize, double zSize, Length units);
    
    public void setXSize(double xSize, Length units);
    public void setYSize(double ySize, Length units);
    public void setZSize(double zSize, Length units);
    
    public double getXSize(Length units);
    public double getYSize(Length units);
    public double getZSize(Length units);
    
}
