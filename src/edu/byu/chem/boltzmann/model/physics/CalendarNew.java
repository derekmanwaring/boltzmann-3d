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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

 /************
  * Event Calendar Class
  * 13 Aug 2004
  * Scott Burt
  * 
  * Last Updated: 14 Jul 2010
  * Update by: Derek Manwaring
  *
  * In order for the calendar
  * to work properly, the code that predicts events must supply the correct times and
  * call the sort method before pulling an event from the top of the heap.  
  ***********/

public class CalendarNew {
    
    private static final Comparator<EventInfo> EVENT_TIME_COMPARATOR = 
            new Comparator<EventInfo> () {
                public int compare(EventInfo e1, EventInfo e2) {
                    
                    if (e1.colTime < e2.colTime) {
                        return -1;
                    } else if (e2.colTime < e1.colTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            };
    
    public static final double MAXTIME = Double.MAX_VALUE;
    public static final double MINTIME = Double.MIN_VALUE;

    private Map<Set<Integer>, EventInfo> eventMatrix;
    
    private EventInfo nextEvent;

    /**
     * minRow - Which row contains the soonest event - set to -1 if no particles or if the calendar
     * needs to be 'sorted' first.
     */
    private int minRow;
    /**
     * The number of particles tracked by the Calendar.  This value can change if particles are added or removed.
     */
    private int currNumPart;
    /**
     * BOUNDARY_ROW - Which row stores Boundary events - use row 0 because no event for particle pair 0-0
     * i.e. the first element in the array list would be null so use it for this
     */
    private final int BOUNDARY_ROW = 0;



    public CalendarNew (int numParticles) {

        // set up some useful vars
        currNumPart = numParticles;

        eventMatrix = new HashMap<Set<Integer>, EventInfo>();
        nextEvent = new EventInfo(-1, MAXTIME, -1, -1, -1);

        //all events need to be updated at this point, so we don't know which row holds the next event
        minRow = -1;
    }

    /**
     * Return time of event on the top of the heap (assumes sorted)
     */
    public double NextEventTime() {
        // if the calendar is empty (i.e. # particles = 0) We expect a dummy event
        // to be on the top of the heap (position 1) that returns max time so that
        // the time advance loop is skipped in AdvanceToTime

        if (minRow == -1)
                return MAXTIME;
        else {
                return nextEvent.colTime;
        }
    }

    /**
     * Returns a copy of the event on the top of the heap (assumes sorted)
     * this will be the event with the smallest colTime - note, this means
     * that it is very important that the prediction code not place any events
     * on the calendar that occur in the "past"
     * @return
     * @throws CloneNotSupportedException
     */
    public EventInfo NextEvent() throws CloneNotSupportedException {

        EventInfo event;
        if (minRow == -1) {
                event = new EventInfo(-1, MAXTIME, -1, -1, -1);
                return event;
        }
        minRow = -1;

        event = (EventInfo) nextEvent.clone();

        return event; //return nextEvent;
    }

    public void MarkEventToUpdate(EventInfo event) {
        minRow = -1;
    }

    /**
     *  Update the information about a specific event in the calendar
     *  Note: using update assumes that the controlling code will then call Sort - otherwise
     *  the POT property will be violated.
     *
     * @param colType   The collision type, as enumerated in the Collision class
     * @param part1  Index of the first particle
     * @param part2  Index of the second particle (if any)
     * @param side
     * @param colTime
     */
    @SuppressWarnings({"unchecked"})
    public void Update(int colType, int part1, int part2, int side, double colTime) {

            // A particle can't have an event with itself
        if (colType == Collision.PARTICLE && part1 == part2)
                    return;

        Integer[] currentParticlesArray = {part1, part2};
        Set currentParticles = new HashSet(Arrays.asList(currentParticlesArray));
        EventInfo thisEvent = new EventInfo(colType, colTime, part1, part2, side);

        if((colType != Collision.PARTICLE) && (colType != Collision.ENTER_WELL) && (colType != Collision.EXIT_WELL)){
            thisEvent.part2 = -1;
            currentParticlesArray[1] = -1;
            currentParticles = new HashSet(Arrays.asList(currentParticlesArray));
        }
        
        eventMatrix.put(currentParticles, thisEvent);
    }


    /**
     * Find what should be the next event according to its predicted
     * collision time in this calendar. After this is called the NextEvent
     * and NextEventTime methods will give valid information.
     */
    public void FindMinimum() {
        //make sure it doesn't fail with no particles
        if (currNumPart == 0) {
                minRow = -1;
                return;
        }

        nextEvent = new EventInfo(-1, MAXTIME, -1, -1, -1);
        for(EventInfo loopEvent: eventMatrix.values()){
            if(loopEvent.colTime < nextEvent.colTime) {
                this.nextEvent = loopEvent;
            }
        }
        //start with the boundary row
        minRow = BOUNDARY_ROW;
    }
 }
 
 
