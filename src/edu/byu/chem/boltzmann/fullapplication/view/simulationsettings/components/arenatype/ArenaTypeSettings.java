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
package edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.components.arenatype;

import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEvent;
import edu.byu.chem.boltzmann.view.utils.events.BoltzmannEventListener;

/**
 *
 * @author Derek Manwaring
 * 16 Dec 2011
 */
public interface ArenaTypeSettings {    
    
    public class ArenaTypeChangedEvent extends BoltzmannEvent {    
        public final ArenaType newType;        
        public ArenaTypeChangedEvent(ArenaType newType) {
            this.newType = newType;
        }
    }
    
    public interface ArenaTypeChangedListener extends BoltzmannEventListener<ArenaTypeChangedEvent> {
    }
    
    public void addListener(ArenaTypeChangedListener listener);
    
    public void setType(ArenaType type);
    
    public ArenaType getType();
}
