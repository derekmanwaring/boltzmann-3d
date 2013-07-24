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
package edu.byu.chem.boltzmann.view.utils.events;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * 18 Nov 2011
 */
public class BoltzmannListenerHandler<ListenerType extends BoltzmannEventListener, 
        EventType extends BoltzmannEvent> {
    
    public static class DuplicateListenerException extends RuntimeException {
        public DuplicateListenerException(String message) {
            super(message);
        }
    }
    
    Set<ListenerType> listeners = new HashSet<ListenerType>(5);
    
    public void addListener(ListenerType listener) {
        if (listeners.contains(listener)) {
            throw new DuplicateListenerException("Listener " + listener + " has already been added to this handler");
        }        
        listeners.add(listener);
    }
    
    @SuppressWarnings("unchecked")
    public void notifyListeners(BoltzmannEvent event) {
        for (ListenerType listener : listeners) {
            listener.notify(event);
        }
    }
    
}
