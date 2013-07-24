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

package edu.byu.chem.boltzmann.controller;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Derek Manwaring
 * 27 Apr 2012
 */
public class BinarySemaphore {

    private final Semaphore countingSemaphore;
    
    public BinarySemaphore(boolean available) {
        if (available) {
            countingSemaphore = new Semaphore(1, true);
        } else {
            countingSemaphore = new Semaphore(0, true);
        }
    }
    
    public void acquire() throws InterruptedException {
        countingSemaphore.acquire();
    }
    
    public synchronized void release() {
        if (countingSemaphore.availablePermits() != 1) {
            countingSemaphore.release();
        }
    }

    public boolean tryAcquire() {
        return countingSemaphore.tryAcquire();
    }
    
    /**
     * 
     * @return true if a resource was available, false otherwise
     */
    public boolean exhaust() {
        return (countingSemaphore.drainPermits() == 1 ? true : false);
    }
}
