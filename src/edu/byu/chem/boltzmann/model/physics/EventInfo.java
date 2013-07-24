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

import java.util.Arrays;

/**
 * Used to store and retrieve info about an event in the Calendar.
 * @author Derek Manwaring
 */
public class EventInfo implements Cloneable {

    public int colType;			// type of event - from Collision enumeration
    public double colTime;				// when the event occurs - (presumably in ps??? TODO) <- No, this is in seconds.
    public int part1, part2, side;		// what particles are involved in the event
                                                            // and what side collision occurs
    public Particle[] particlesInvolved = new Particle[2];

    // This field is solely to allow Particle.CollideWith to return
    // reaction information to Physics.PerformEvent.
    // In reaction mode, return the number of blue particles changed
    // (+2 means there are two more blue particles; etc.)
    // I think I would prefer a callback (listener) but this is simpler.
    public int deltaBlue = 0;
    public double getDeltaBlue() { return deltaBlue; }
    public void setDeltaBlue(int value) { this.deltaBlue = value; }

    //Note: for boundary collisions part2 isn't used and side indicates
    //which boundary object (left, right, top, bottom, etc.)  For particle
    //collisions in periodic boundaries the side variable is used to indicate
    //which image to collide with.

    public EventInfo() {
            colType = Collision.EVERYTHING;
            colTime = 0.0;
            part1 = part2 = side = 0;
            particlesInvolved = new Particle[2];
    }

    public EventInfo(int updateFlag) {
            colType = updateFlag;
            colTime = 0.0;
            part1 = part2 = side = 0;
            particlesInvolved = new Particle[0];
    }

    public Particle[] getInvolvedParticles() {
        return Arrays.copyOf(particlesInvolved, particlesInvolved.length);
    }

    public EventInfo(int colType, double colTime, int part1Index, int part2Index, int whichSide, Particle[] particlesInvolved) {
            this.colType = colType;
            this.colTime = colTime;
            this.part1 = part1Index;
            this.part2 = part2Index;
            this.side = whichSide;
            this.particlesInvolved = particlesInvolved;
    }

    public EventInfo(int colType, double colTime, int part1Index, int part2Index, int whichSide) {
            this.colType = colType;
            this.colTime = colTime;
            this.part1 = part1Index;
            this.part2 = part2Index;
            this.side = whichSide;
    }

    /**
     * Copy constructor
     * @param other EventInfo object to copy
     */
    public EventInfo(EventInfo other) {
        copy(other);
    }

    /**
     * Copier
     * @param Deep copies other's event information 
     */
    public final void copy(EventInfo other) {
        this.colTime = other.colTime;
        this.colType = other.colType;
        this.deltaBlue = other.deltaBlue;
        this.part1 = other.part1;
        this.part2 = other.part2;
        this.side = other.side;

        // use existing array if it's big enough
        if (this.particlesInvolved != null && this.particlesInvolved.length > 1) {
            if (other.particlesInvolved != null) {
                this.particlesInvolved[0] = other.particlesInvolved[0];
                this.particlesInvolved[1] = other.particlesInvolved[1];
            } else {
                this.particlesInvolved = null;
            }              
        } else if (other.particlesInvolved != null) {
            this.particlesInvolved = Arrays.copyOf(other.particlesInvolved, 
                    other.particlesInvolved.length);
        } else {
            this.particlesInvolved = null;
        }                    
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.colType;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.colTime) ^ (Double.doubleToLongBits(this.colTime) >>> 32));
        hash = 71 * hash + this.part1;
        hash = 71 * hash + this.part2;
        hash = 71 * hash + this.side;
        hash = 71 * hash + Arrays.deepHashCode(this.particlesInvolved);
        hash = 71 * hash + this.deltaBlue;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        EventInfo other = null;

        if (!(obj instanceof EventInfo)) {
            return false;
        } else {
            other = (EventInfo) obj;
        }

        return ((this.colTime == other.colTime) &&
                (this.colType == other.colType) &&
                (this.deltaBlue == other.deltaBlue) &&
                (this.part1 == other.part1) &&
                (this.part2 == other.part2) &&
                (this.side == other.side) &&
                (Arrays.equals(this.particlesInvolved, other.particlesInvolved)));
    }

    public Object clone() throws CloneNotSupportedException {
            EventInfo copy=null;
            copy = (EventInfo) super.clone();
            return copy;
    }

    public String toString() {
            String out="";
            switch(colType) {
                    case Collision.PARTICLE:
                            out += "Particle, ";
                            break;
                    case Collision.ENTER_WELL:
                            out += "Enter well, ";
                            break;
                    case Collision.EXIT_WELL:
                            out += "Exit well, ";
                            break;
                    case Collision.WALL:
                            out += "Wall, ";
                            break;
                    case Collision.BOUNDARY:
                            out += "Boundary, ";
                            break;
                    case Collision.EOB:
                            out += "EOB, ";
                            break;
                    case Collision.BARRIER:
                            out += "Barrier, ";
                            break;
                    case Collision.EDGE:
                            out += "Edge, ";
                            break;
                    case Collision.PISTON:
                            out += "Piston, ";
                            break;
                    case Collision.PARTICLE_1:
                            out += "Update 1 Particle, ";
                            break;
                    case Collision.PARTICLE_2:
                            out += "Update 2 Particles, ";
                            break;
                    case Collision.PARTICLE_ALL:
                            out += "Update All Particle Events, ";
                            break;
                    case Collision.BOUNDARY_ALL:
                            out += "Update All Boundary Events, ";
                            break;
                    case Collision.PISTON_ALL:
                            out += "Update all Piston Events, ";
                            break;
                    case Collision.EVERYTHING:
                            out += "Update All Events, ";
                            break;
                    case Collision.RESORT:
                            out += "Sort Calendar, ";
                            break;
                    case Collision.WELL_REFLECT:
                            out += "Well Reflect, ";
                            break;
                    default:
                            out += "error, colType: "+colType+" ";
            }
            out += colTime+", ";
            out += "("+part1+","+part2+") ";
            switch(side) {
                    case 0:
                            out += "no side";
                            break;
                    case Wall.LEFT:
                            out += "Left";
                            break;
                    case Wall.RIGHT:
                            out += "Right";
                            break;
                    case Wall.BOTTOM:
                            out += "Bottom";
                            break;
                    case Wall.TOP:
                            out += "Top";
                            break;
                    case Wall.BACK:
                            out += "Back";
                            break;
                    case Wall.FRONT:
                            out += "Front";
                    case (Wall.LEFT | Wall.BOTTOM):
                            out += "x & y image";
                            break;
                    case (Wall.LEFT | Wall.BACK):
                            out += "x & z image";
                            break;
                    case (Wall.BOTTOM | Wall.BACK):
                            out += "y & z image";
                            break;
                    case (Wall.LEFT | Wall.BOTTOM | Wall.BACK):
                            out += "x & y & z image";
                            break;
                    default:
                            out += "error, side: "+colType+" ";
            }
            return out;
    }

}
