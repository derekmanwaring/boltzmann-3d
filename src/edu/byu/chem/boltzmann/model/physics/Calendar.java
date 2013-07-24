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
 
 import java.util.ArrayList;
 import java.util.Set;
 import java.util.HashSet;

 /************
  * Event Calendar Class
  * 13 Aug 2004
  * Scott Burt
  * 
  * Last Updated: 13 Aug 2004
  * Update by: Scott Burt
  *
  * The calendar is built from two structures - a two dimensional matrix of all possible
  * events and a heap of pointers to these events.  The matrix is actually a triangular
  * array to avoid duplicating events (events occur between _pairs_ of objects).  This
  * allows fast access to the events themselves for updating or retrieving event info.
  * The heap allows the fastest possible sorting of the event times in order to return
  * the event with the smallest time.  Making it a heap of pointers allows the heap to
  * be instantaneously updated whenever the matrix is modified.  In order for the calendar
  * to work properly, the code that predicts events must supply the correct times and
  * call the sort method before pulling an event from the top of the heap.  Note that
  * ArrayLists are used to provide the ability for the calendar to grow and shrink as
  * particles are added to or removed from the simulation.
  ***********/
 public class Calendar {

	 // Matrix organization:
	 // column, row: particles to interact (starting with number: 0? 1?)
	 // row 0: boundary interactions... (?)
	 // 
	 // 0: P0 P1 P2 P3 P4 ... PN (Boundary row)
	 // 1: P0
	 // 2: P0 P1
	 // 3: P0 P1 P2
	 // 4: P0 P1 P2 P3
	 // ...
	 // N: P0 P1 P2 P3 ... P(N-1)
	 //
	 // For example: 
	 // 	Row 1 stores events between particles 0 and 1
	 // 	Row 0 stores events between all particles and the boundaries
	 // 	Row 2 stores events between particles (0,1) and between (0,2)
	 // There is no need to store events of a particle with itself.
     
        public static final double MAXTIME = Double.MAX_VALUE;
        public static final double MINTIME = Double.MIN_VALUE;
	 
	/**
	 * Represents a row in the matrix.
	 * ArrayList doesn't allow creating lists of primitive types, so we make an element that contains
	 * the int and booleans that we need (this also reduces the number of get() methods we need to call)
	 */
	private class calElem {

		/** A list of column entries (fields) within a single row of the matrix */
		ArrayList<EventInfo> col;
		int minPos;
	 	boolean updateFlag;
	 	
	 	public calElem() {
	 		col = null;
	 		minPos = 0;
	 		updateFlag = true;
	 	}

	 	/**
	 	 * Creates a new calElem with the specified number of columns
	 	 */
	 	public calElem(int numCol) {
	 		this.col = new ArrayList<EventInfo>(numCol);
	 		this.minPos = 0;
	 		this.updateFlag = true;
	 	}
	}
 
	/** Matrix - 2 dimensional matrix of all possible events */
	private ArrayList<calElem> matrix;
	
	private Set<EventInfo> eventMatrix;
	private EventInfo nextEvent = null;
	
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


	
 	public Calendar (int numParticles) {
	
	    // set up some useful vars
	    currNumPart = numParticles;
	    
    	// numExpPart: How many particles we expect after the user adds some blue particles
    	// This allocates adequate buffers in the the ArrayLists for faster growing
	    int numExpPart = (int) (numParticles * 2.0);
	    
	    
	    // Create the event matrix (triangular array + 1 row)
	    matrix = new ArrayList<calElem>(1+numExpPart);
	    	//The matrix grows down, so buffer for future rows added
	    	
		// Set up the row containing Boundary events
	    matrix.add(BOUNDARY_ROW, new calElem(numExpPart));
	    	//The boundary row grows out (in columns), so buffer the arraylist
	    for (int i = 0; i < numParticles; i ++)
	    	((calElem)matrix.get(BOUNDARY_ROW)).col.add(i, new EventInfo());
	    	//Create each event data type
	    	
		// Create the particle-particle events (no row 0, events start at P1 + P0, etc.)
	    for (int i = 1; i < numParticles; i ++) {
			matrix.add(i, new calElem(i));
			// these rows do not grow so no need to buffer the array list
			// Note the size of each row, we use a triangular event matrix to avoid
			// duplicating events, we only need to track each _pair_ of particles
			for (int i2 = 0; i2 < i; i2 ++)
				((calElem)matrix.get(i)).col.add(i2, new EventInfo());
				//Create each event data type
		}
	    
	    // Initialize the event matrix and the heap
	    for (int row = 0; row < numParticles; row ++) {
			for (int col=0; col<row || (row==BOUNDARY_ROW && col<numParticles); col++) {
			    if (row != BOUNDARY_ROW) {	//Particle Particle event
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = MAXTIME;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = Collision.PARTICLE;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).part1 = col;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).part2 = row;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = 0;
			    }
			    else { //Boundary event
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = MAXTIME;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = Collision.WALL;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).part1 = col;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).part2 = 0;
				((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = 0;
					//Note: colType and side are dummy initializations, they
					//need to be set by the prediction code before they have meaning
			    }
			} //next col
	    }//next row
	    
	    eventMatrix = new HashSet<EventInfo>();
	    
		//all events need to be updated at this point, so we don't know which row holds the next event
    	minRow = -1;
 	}//end of constructor

 	/**
 	 * Add elements to calendar and heap to accommodate new particles
 	 * @param numPartToAdd   Number of particles to add
 	 */
 	public void Grow(int numPartToAdd) {
 		
 		// make sure that the array lists are large enough
 		matrix.ensureCapacity(currNumPart+numPartToAdd+1);
 		((calElem)matrix.get(BOUNDARY_ROW)).col.ensureCapacity(currNumPart+numPartToAdd);
 		
 		// add the new elements and initialize their values (see logic in constructor)
 		int totalNumPart = currNumPart+numPartToAdd;
 		for (int col=currNumPart; col<totalNumPart; col++) {
 			((calElem)matrix.get(BOUNDARY_ROW)).col.add(col, new EventInfo(Collision.WALL, MAXTIME, col, 0, 0));
 		}
 		for (int row=(currNumPart>1 ? currNumPart : 1); row<totalNumPart; row++) {
 			matrix.add(row, new calElem(row));
 			for (int col=0; col<row; col++) {
 				((calElem)matrix.get(row)).col.add(col, new EventInfo(Collision.PARTICLE, MAXTIME, col, row, 0));
 			}
 		}
 		//as in the constructor, much of the initialization is dummy values and the prediction
 		//code must be run to update these new event elements

 		minRow = -1;
 		currNumPart += numPartToAdd;
 	}
 	
 	/**
 	 * Remove elements associated with the particles to remove (always the
 	 * particles at the end of the list) - Note: the logic must remove the
 	 * particles in reverse order, otherwise removing an element from the
 	 * middle of a list renumbers all the succeeding elements
 	 * @param numPartToRemove   Number of particles to remove
 	 */
 	public void Shrink(int numPartToRemove) {

 		int lowerIndex = currNumPart-numPartToRemove;
 		//ensure that we aren't trying to remove too many
 		if (lowerIndex <0)
 			lowerIndex = 0;
 		
 		//remove boundary events first
 		for (int col=currNumPart-1; col>=lowerIndex; col--) {
 			((calElem)matrix.get(BOUNDARY_ROW)).col.remove(col);
 		}
 		
 		//now remove particle pair events
 		for (int row=currNumPart-1; row>=lowerIndex && row!=BOUNDARY_ROW; row--) {
 			//odd bit of logic with BOUNDARY_ROW is to prevent the boundary row from being
 			//deleted when removing the last particle
 			for (int col=row-1; col>=0; col--) {
 				((calElem)matrix.get(row)).col.remove(col);
 			}
 			matrix.remove(row);
 		}
 		
 		minRow = -1;
 		currNumPart = lowerIndex;
 		// TODO (APS): Do we need to do anything else to ensure that collisions are recalculated?
 		// Should we also remove outlying columns from lower-numbered rows, and set updateFlag and 
 		// maybe minCol to -1 if it is part of this area? (APS 25 Oct 2008)
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
 			calElem row = (calElem) matrix.get(minRow);
 			return ((EventInfo)row.col.get(row.minPos)).colTime; //return nextEvent.colTime;
 		}
 	}
 	
 	/**
 	 * Returns a copy of the event on the top of the heap (assumes sorted)
 	 * this will be the event with the smallest colTime - note, this means
 	 * that it is very important that the prediction code not place any events
 	 * on the calendar that occur in the "past"
 	 * @return
 	 */
 	public EventInfo NextEvent() {
 		
 		EventInfo event = new EventInfo();
 		if (minRow == -1) {
 			event = new EventInfo(-1,MAXTIME,-1,-1,-1);
 			return event;
 		}
 		// otherwise pull out the soonest event from the matrix
		calElem row = (calElem) matrix.get(minRow);
		//event = (EventInfo) ((EventInfo)row.col.get(row.minPos)).clone();
                event.copy((EventInfo)row.col.get(row.minPos));
 		
 		//Note: my old code also 'deleted' the top event just to be safe
 		//But it's a waste of computation time doing this if we always
 		//predict and sort the calendar after an event, to avoid an infinite
 		//loop of pulling the same event out, it is a good idea to at least
 		//set the colTime to a no-collision marker
 		((EventInfo)row.col.get(row.minPos)).colTime = MAXTIME;
 		minRow = -1;
 		
 		return event; //return nextEvent;
 	}
 	
 	public void MarkEventToUpdate(EventInfo event) {
		
		minRow = -1;
		switch (event.colType) {
			case Collision.PARTICLE_2:
				((calElem)matrix.get(event.part1)).updateFlag = true;
				((calElem)matrix.get(event.part2)).updateFlag = true;
				break;
			case Collision.PARTICLE_1:
				((calElem)matrix.get(event.part1)).updateFlag = true;
				break;
			case Collision.PARTICLE_ALL:
				for (int i=1; i<currNumPart; i++)
					((calElem)matrix.get(i)).updateFlag = true;
				break;
			case Collision.BOUNDARY_ALL:
				((calElem)matrix.get(BOUNDARY_ROW)).updateFlag = true;
				break;
			case Collision.PISTON_ALL:
				((calElem)matrix.get(BOUNDARY_ROW)).updateFlag = true;
				break;
			case Collision.EVERYTHING:
			case Collision.RESORT:
				((calElem)matrix.get(BOUNDARY_ROW)).updateFlag = true;
				for (int i=1; i<currNumPart; i++)
					((calElem)matrix.get(i)).updateFlag = true;
				break;
		}
 	}
 	
	/**
	 *  Update the information about a specific event in the calendar - because the
	 *  heap is just a list of pointers to the calendar events these changes are
	 *  immediately reflected in the heap;
	 *  Note: using update assumes that the controlling code will then call Sort - otherwise
	 *  the POT property will be violated.
	 *  
	 * @param colType   The collision type, as enumerated in the Collision class
	 * @param part1  Index of the first particle
	 * @param part2  Index of the second particle (if any)
	 * @param side
	 * @param colTime
	 */
 	// TODO: The comment about Sort appears to be old... I don't see any Sort method here!
 	// and yet it seems likely that it is the problem! - APS 24 Oct 2008
 	public void Update(int colType, int part1, int part2, int side, double colTime, Particle[] particlesInvolved) {
 		
 		// A particle can't have an event with itself
	    if (colType == Collision.PARTICLE && part1 == part2)
			return;
		
	    int row, col;
	    switch (colType) {
			case Collision.PARTICLE:
			    // because of the way the triangular array is setup, 
			    // the largest index must always be the row
			    row = (part1 > part2) ? part1 : part2;
			    col = (part1 < part2) ? part1 : part2;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = colTime;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = side;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = colType;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[0] = particlesInvolved[0];
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[1] = particlesInvolved[1];
                            
			    	// Note: by pulling the event with the correct row and column we only
			    	// need to set the value for collision time (and possibly side if periodic
			    	// boundaries) - the other values were set by the constructor or the grow method
			    // TODO
			    // BEGIN debug code APS
			    /*
			    if (((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType != colType) {
			    	System.err.println("Calendar.java: incorrect colType");
			    	throw new RuntimeException("Calendar.java: incorrect colType");
			    }
			    */
			    // END debug code APS
			    break;
			case Collision.ENTER_WELL:
			    row = (part1 > part2) ? part1 : part2;
			    col = (part1 < part2) ? part1 : part2;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = colType;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = colTime;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = side;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[0] = particlesInvolved[0];
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[1] = particlesInvolved[1];
				break;
			case Collision.EXIT_WELL:
			    row = (part1 > part2) ? part1 : part2;
			    col = (part1 < part2) ? part1 : part2;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = colType;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = colTime;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = side;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[0] = particlesInvolved[0];
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[1] = particlesInvolved[1];
				break;
			default: //all other types are boundary events (Wall, Boundary, EOB, Barrier, Edge)
			    row = BOUNDARY_ROW;
			    col = part1;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colType = colType; //Boundary, Wall, etc. this is important!
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).side = side; 		//which side (left, right, etc)
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).colTime = colTime;
			    ((EventInfo)((calElem)matrix.get(row)).col.get(col)).particlesInvolved[0] = particlesInvolved[0];
	    } //end switch

		// set minimum values where applicable
		calElem mRow = (calElem) matrix.get(row);
		if (!mRow.updateFlag) {
			//nothing to do if row marked as complete update - otherwise we have two options:
			if (mRow.minPos != col) {
				//simple check
				if (colTime<((EventInfo)mRow.col.get(mRow.minPos)).colTime)
					mRow.minPos = col;
			}
			else { //we updated a current minimum, re-sort
				mRow.minPos = 0;
				for (int i=1; i<((calElem)matrix.get(row)).col.size(); i++)
					if (((EventInfo)mRow.col.get(i)).colTime < ((EventInfo)mRow.col.get(mRow.minPos)).colTime)
						mRow.minPos = i;
			}
		}
 	}
 	
	public void FindMinimum() {
		//reset the update flags and find the row with the minimum event time
		
		//make sure it doesn't fail with no particles
		if (currNumPart == 0) {
			minRow = -1;
			return;
		}
		
		//start with the boundary row
		minRow = BOUNDARY_ROW;
		
		//if this row needs updating, find the column containing the minimum event time
		calElem row = (calElem) matrix.get(minRow);
		if (row.updateFlag) {
			row.minPos = 0;
			for (int col=1; col<row.col.size(); col++)
				if (((EventInfo)row.col.get(col)).colTime < ((EventInfo)row.col.get(row.minPos)).colTime)
					row.minPos = col;
		}
		row.updateFlag = false;
		
		//now check the particles
		for (int i=1; i<matrix.size(); i++) {
			//if this row needs updating, find the minimum event time
			row = (calElem) matrix.get(i);
			if (row.updateFlag) {
				row.minPos = 0;
				for (int col=1; col<row.col.size(); col++)
					if (((EventInfo)row.col.get(col)).colTime < ((EventInfo)row.col.get(row.minPos)).colTime)
						row.minPos = col;
			}
			row.updateFlag = false;
			
			calElem minElem = (calElem) matrix.get(minRow);
			if (((EventInfo)row.col.get(row.minPos)).colTime < ((EventInfo)minElem.col.get(minElem.minPos)).colTime)
				minRow = i;
		}
	}


 }
 
 
