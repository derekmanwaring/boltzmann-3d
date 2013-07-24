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

// Classes needed for random positioning of the particles
import edu.byu.chem.boltzmann.controller.Controller;
import edu.byu.chem.boltzmann.controller.ErrorHandler;
// Classes needed for various data structures
import edu.byu.chem.boltzmann.model.physics.Piston.PistonMode;
import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.Pressure;
import edu.byu.chem.boltzmann.model.statistics.RadialDistribution;
import edu.byu.chem.boltzmann.model.statistics.interfaces.Statistic;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Time;
import java.util.HashMap;
import java.util.ArrayList;

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.data.StatSettingsInfo;
import java.awt.Color;
import java.util.*;

/**
 * Important Methods Deleted:
 * ResetStats
 * writeInstantaneousVelocityFooter
 * UpdateRunAvg
 * StopRunAvg
 * StartRunAvg
 * WriteHeader
 * statUpdateRDF
 * StatUpdate
 * updateInstantaneousDistribution
 * startPositionFile
 * UpdatePosFile
 * writeRunAvgTitles
 * refreshPistonPanel
 * SetActiveStat
 * Perform RDF update
 * setRDFUpdateIntervals
 * stopPositionFile
 *      Other Classes:
 * getEffectiveNumColors
 * getInstStat
 * updateThreadRun
 * setupStat
 * setPartCount
 * countParticles
 * checkOverlap
 * reportError
 * getHue
 * SetFileFilter from Utils (not used here, but I think it did something important :)
 */
public class Physics {
    private final SimulationInfo simulationInfo;

    //Hash map for particle pairs and the last collisions between those pairs
    private LastCollisions lastCollisions;
    private static final EventInfo DEFAULT_LAST_COLLISION = 
            new EventInfo(Collision.PARTICLE, 
                    Calendar.MINTIME, 
                    0, 
                    0, 
                    0
                    );
        
    // settings

    /** Contains the state of the arena (boundary conditions, barrier status, arena size, etc.) */
    protected ArenaInfo arena;

    /** cal - the event calendar contains all possible events and allows fast sorting to pull out the
     * next event to be performed */
    private Calendar cal;

    /** particles - arraylist of particle objects, used to perform and predict events */
    private ArrayList<Particle> particles;

    /** piston - piston object that controls the piston behavior */
    private Piston piston;

    /** thermostat - thermostat object that controls the thermostat behavior */
    public Thermostat thermostat;

    /** maxHoleDiam - yLen for 2D or the smaller of yLen and zLen in 3D */
    private double maxHoleDiam;
    
    /**
     * Whether the hole is open in Maxwell demon mode
     */
    private boolean holeOpen;
    private boolean nextFrameHoleOpen;
    
    public double currSimTime;

    /** predAvg/Wid - arrays containing the predicted values (in display units)
     * for all colors and stat types */
    double[][] predAvg, predWid;
    
    public static final double DEFAULT_FRAME_DURATION = 0.404706830433 * 1E-12; //Simulation time per frame in seconds
    private double frameDuration = DEFAULT_FRAME_DURATION;
    
    private Map<Set<ParticleType>, Map<StatisticID, Statistic>> statistics;
    
    private boolean stopProcessingCurrentFrame = false;
    private boolean runPhysics = true;
    private boolean processingFrame = false;

    public Physics(SimulationInfo simulationInfo, StatSettingsInfo statSettingsInfo) {
        forgetMultiplier = statSettingsInfo.forgetTime;
        useFiniteSystemCorrections = statSettingsInfo.useFinSysCorrections;
        useRealGasCorrections = statSettingsInfo.useRealGasCorrections;
        holeOpen = false;

        this.simulationInfo = simulationInfo;
        // 1) Copy standard variables that provide state information and
        // set/initialize other variables      

        arena = simulationInfo.arenaInfo;
        currSimTime = 0;

        maxHoleDiam = simulationInfo.arenaYSize;
        if (simulationInfo.dimension > 2 && simulationInfo.arenaZSize < maxHoleDiam) {
                maxHoleDiam = simulationInfo.arenaZSize;
        }

        // 3) Create the piston and thermostat
        piston = new Piston(currSimTime, 0.0, simulationInfo.maxPistonLevel, simulationInfo);
        thermostat = new Thermostat(false);

        // 4) Instantiate the array list of particle objects and then create the
        // particles
        List<ParticleType> particleTypes = simulationInfo.getParticleTypes();
        particles = new ArrayList<Particle>();

        for (ParticleType currentType: particleTypes) {
            for (int i = 0; i < simulationInfo.getNumberOfParticles(currentType); i++) {
                Particle newParticle = null;
                double[] position = simulationInfo.getParticlePosition(currentType, i);
                double[] velocity = simulationInfo.getParticleVelocity(currentType, i);
                switch(simulationInfo.dimension) {
                    case 1:
                        newParticle = new Particle1D(position, velocity, 0.0, currentType, simulationInfo);
                        break;
                    case 2:
                        newParticle = new Particle2D(position, velocity, 0.0, currentType, simulationInfo);
                        break;
                    case 3:
                        newParticle = new Particle3D(position, velocity, 0.0, currentType, simulationInfo);
                }
                particles.add(newParticle);
            }
        }
        
        initializeLastCollisions();
        
        // 6) Instantiate the calendar, predict initial events and sort
        cal = new Calendar(particles.size());

        CalUpdate(new EventInfo(Collision.EVERYTHING));
        
        if (simulationInfo.isPeriodic()) {
            SetTotalMom(0.0, 0.0, 0.0);
        }
        
        allocatedStates1 = new PartState[particles.size()];
        allocatedStates2 = new PartState[particles.size()];
        for (int particleIndex = 0; particleIndex < particles.size(); particleIndex++) {
            allocatedStates1[particleIndex] = new PartState(0, 0, 0, 0, null, 0);
            allocatedStates2[particleIndex] = new PartState(0, 0, 0, 0, null, 0);
        }        
        workingStates = allocatedStates1;
        
        double collisionRate = Formulas.predictCollisionRate(
                simulationInfo, 
                new HashSet<ParticleType>(simulationInfo.getParticleTypes()),
                useRealGasCorrections);
        
        collisionLifetime = Formulas.collisionLifetime(collisionRate);
        statisticsReset = false;
        
        initiateStatistics(statSettingsInfo);
    }

    private PartState[] workingStates;
    private final PartState[] allocatedStates1;
    private final PartState[] allocatedStates2;
    
    private FrameInfo currentFrame = null;

    public void createFirstFrame() {
        FrameInfo firstFrame = new FrameInfo(currSimTime, currSimTime);
        firstFrame.setEndingStates(getParticleStates());
        currentFrame = firstFrame;
    }
    
    private boolean firstRun = true;
    
    public void advanceToNextFrame() {
        if (firstRun) {
            firstRun = false;
            createFirstFrame();
        } else {
            FrameInfo frameInfo = advanceToTime(currSimTime + frameDuration);
            frameInfo.setEndingStates(getParticleStates());
            
            currentFrame = frameInfo;
            notifyStatisticsOfTime(currentFrame.endTime);
        }
    }
    
    public FrameInfo getCurrentFrame() {
        return currentFrame;
    }

    public void AdjustPistonLevel(double targetLevel) {
        piston.setTargetLevel(targetLevel);
        CalUpdate(new EventInfo(Collision.PISTON_ALL));
    }
    
    public void AdjustPistonLevel(double targetLevel,
            PistonMode mode,
            double speed)
    {
        piston.setSpeed(speed);
        //piston.setT0(currSimTime);
        //piston.moveToTime(currSimTime);
        //piston.setTargetLevel(targetLevel);
        if (mode == PistonMode.INSTANTANEOUS) {
            piston.setTargetLevel(targetLevel);
            piston.setCurLevel(targetLevel);
        } else {
        
            piston.setTargetLevel(targetLevel);
            CalUpdate(new EventInfo(Collision.PISTON_ALL));
            ThermostatController controller = ThermostatFactory.createController(ThermostatFactory.WALLTHERMOSTAT);
            thermostat.setThermostatController(controller);
            // if the piston is in isothermal mode, use the thermostat to regulate temperature
            if (mode == PistonMode.ISOTHERMAL) {
                thermostat.setEnabled(true);
            } else if (mode == PistonMode.ADIABATIC) {
                thermostat.reset(false);
            }
        }

//		ResetStats();
    }

    public Particle[] getParticles() {
            Particle[] returnVal = new Particle[particles.size()];
            returnVal = (Particle[]) particles.toArray(returnVal);
            return returnVal;
    }

    /**
     * Advances to the specified simulation time.
     *
     * This consists of two steps:
     * (1) Handle all calculated collisions that occur before maxTime
     * (2) From here, move all particles in a straight line to where they should
     * be at maxTime
     *
     * @param maxTime  The time to advance to
     */
    private FrameInfo advanceToTime (double maxTime) {
        processingFrame = true;
        
        if (nextFrameHoleOpen != holeOpen) {
            holeOpen = nextFrameHoleOpen;
            CalUpdate(new EventInfo(Collision.BOUNDARY_ALL));
        }
        
        // Check frameTime of top event on the calendar heap - this assumes that
        // the calendar has been correctly sorted (either in initialization
        // code or at the end of the last AdvanceToTime)
        double nextEventTime = cal.NextEventTime();
        double pistonStart = piston.getPosition();
        double frameStart = currSimTime;

        //As long as there are events to perform, pull them from the calendar.
        EventInfo event = null;
        while (nextEventTime <= maxTime) {       
            //Perform event.
            // pull the next event from the calendar
            event = cal.NextEvent();
            
            updateStatistics(event);
            
            // Carry out the next event and tell CalUpdate what action to
            // take when predicting new events
            EventInfo calAction = PerformEvent(event);

            // predict new events and sort the calendar
            CalUpdate(calAction);
            
            if (!runPhysics || stopProcessingCurrentFrame) {
                maxTime = nextEventTime;
                stopProcessingCurrentFrame = false;
                break;
            }
            
            nextEventTime = cal.NextEventTime();
        }
        
        FrameInfo newFrame = new FrameInfo(frameStart, maxTime);        
        newFrame.setPistonStart(pistonStart);

        // At this point, the next event occurs in the next frame, so move
        // all the particles to the maximum simulation frameTime associated with
        // this frame
        if (simulationInfo.arenaType == ArenaType.MOVABLE_PISTON) {
            // piston.calcVars();
            piston.MoveToTime(maxTime);
            //Currently this doesn't accurately report piston movements that don't take an entire frame
            newFrame.setPistonEnd(piston.getPosition(), maxTime);
            //CalUpdate(new EventInfo(Collision.PISTON));
        } else if (simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE) {
            newFrame.setArenaHoleOpen(holeOpen);
        }
        
        for (int i = 0; i < particles.size(); i++) {
            ((Particle) particles.get(i)).moveToTime(maxTime);
        }

        currSimTime = maxTime;
        
        processingFrame = false;
        
        return newFrame;
    }

    private EventInfo PerformEvent(EventInfo event) {
            // carry out the event and be sure to set event.colType to the
            // appropriate flag for CalUpdate
            EventInfo calAction = null;
                        
            try {
                    calAction = (EventInfo) event.clone();
            } catch (CloneNotSupportedException err) {
                    //misc.guiPtr
                    throw new RuntimeException(
                                                    "Could not create a copy of the event in Physics:PerformEvent",
                                                    err);
            }
            Particle p1 = null, p2 = null;

            // Take care of the event
            switch (event.colType)
            {
                    case Collision.PARTICLE:
                            p1 = (Particle) particles.get(event.part1);
                            p2 = (Particle) particles.get(event.part2);
                            p1.moveToTime(event.colTime);
                            p2.moveToTime(event.colTime);
                            p1.collideWith(p2, event);
                            calAction.colType = Collision.PARTICLE_2;
                            lastCollisions.setLastCollision(event.part1, event.part2, event);
                            break;

                    case Collision.ENTER_WELL:
                            p1 = (Particle) particles.get(event.part1);
                            p2 = (Particle) particles.get(event.part2);
                            p1.moveToTime(event.colTime);
                            p2.moveToTime(event.colTime);
                            p1.collideWith(p2, event);
                            calAction.colType = Collision.PARTICLE_2;
                            lastCollisions.setLastCollision(event.part1, event.part2, event);
                            break;

                    case Collision.EXIT_WELL:
                            p1 = (Particle) particles.get(event.part1);
                            p2 = (Particle) particles.get(event.part2);
                            p1.moveToTime(event.colTime);
                            p2.moveToTime(event.colTime);
                            p1.collideWith(p2, event);
                            calAction.colType = Collision.PARTICLE_2;
                            lastCollisions.setLastCollision(event.part1, event.part2, event);
                            break;

                    case Collision.WELL_REFLECT:
                            p1 = (Particle) particles.get(event.part1);
                            p2 = (Particle) particles.get(event.part2);
                            p1.moveToTime(event.colTime);
                            p2.moveToTime(event.colTime);
                            p1.collideWith(p2, event);
                            calAction.colType = Collision.PARTICLE_2;
                            lastCollisions.setLastCollision(event.part1, event.part2, event);
                            break;

                    default: // all boundary events (Wall, Boundary, EOB, Barrier,
                                            // Edge)
                            Particle currentParticle = particles.get(event.part1);
                            currentParticle.boundaryCollide(event, piston, thermostat);
                            calAction.colType = Collision.PARTICLE_1;
            }// end switch(event type)


            return calAction;
    }

    /**
     * Depending on what actions needs to be taken, predict various events
     * (either for 1 particle, 2 particles, or all particles) and sort the
     * calendar
     */
    EventInfo eventForCalUpdate = new EventInfo();
    protected void CalUpdate(EventInfo action) {
        cal.MarkEventToUpdate(action); // this helps optimize the calendar
                                             
        switch (action.colType) {
        case Collision.PARTICLE_2: {
                // predict the particle-particle events for both particles
                int part1Index = action.part1;
                int part2Index = action.part2;
                Particle part1 = (Particle) particles.get(part1Index);
                Particle part2 = (Particle) particles.get(part2Index);
                for (int i = 0; i < particles.size(); i++) {
                        if (true) {//i != action.part2 && i != action.part1) {
                                // Note: no collision with self and we don't want to
                                // predict a collision between
                                // part1 and part2 either - i.e. they just collided and
                                // are moving away from each other
                                // 27 Sep 2011 - Yes we do because there should be an exit well collision
                                Particle part_i = (Particle) particles.get(i);
                                if (i != part1Index) { // But we still don't check for collisions with self.
                                    EventInfo lastCollision = lastCollisions.getLastCollision(part1Index, i);
                                    part1.predCol(part_i, lastCollision, eventForCalUpdate);
                                    cal.Update(eventForCalUpdate.colType, action.part1, i,
                                                    eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                                }

                                if (i != part2Index) {
                                    EventInfo lastCollision = lastCollisions.getLastCollision(i, part2Index);
                                    part2.predCol(part_i, lastCollision, eventForCalUpdate);
                                    cal.Update(eventForCalUpdate.colType, action.part2, i,
                                                    eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                                }
                        }
                }

                // BEGIN Code added for the overlap issue (APS)
                // In periodic boundary mode, it is possible that a particle will collide again with
                // an image of the particle it just collidied with...  This is checked AFTER we clear
                // the collision that just took place.
                if (true) {
                if (simulationInfo.arenaType == ArenaType.PERIODIC_BOUNDARIES) { // if periodic boundary mode...
                        // Exception: In periodic boundary mode, we need to check
                        // for collisions with other images of the same particle
                        //System.err.println("Checking for re-collision in periodic boundary mode");
                        EventInfo lastCollision = lastCollisions.getLastCollision(part1Index, part2Index);
                        part1.predCol(part2, lastCollision, eventForCalUpdate);
                        cal.Update(Collision.PARTICLE, action.part1, action.part2,
                                eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                }
                }
                // END Code added for the overlap issue (APS)


                // get the soonest boundary event as well
                eventForCalUpdate = part1.predBoundaryCol(getPiston(), holeOpen);
                cal.Update(eventForCalUpdate.colType, action.part1, 0, eventForCalUpdate.side,
                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                eventForCalUpdate = part2.predBoundaryCol(getPiston(), holeOpen);
                cal.Update(eventForCalUpdate.colType, action.part2, 0, eventForCalUpdate.side,
                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);

        }
                break;
        case Collision.PARTICLE_1: {
                // this updates just a single particle

                // predict the particle-particle events
                int part1Index = action.part1;
                Particle part1 = (Particle) particles.get(part1Index);
                for (int i = 0; i < particles.size(); i++) {
                        if (i != action.part1) {
                                // Note: no collision with self is allowed
                                Particle part_i = (Particle) particles.get(i);
                                EventInfo lastCollision = lastCollisions.getLastCollision(part1Index, i);
                                part1.predCol(part_i, lastCollision, eventForCalUpdate);
                                cal.Update(eventForCalUpdate.colType, action.part1, i,
                                                eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                        }
                }

                // get the soonest boundary event as well
                eventForCalUpdate = part1.predBoundaryCol(getPiston(), holeOpen);
                cal.Update(eventForCalUpdate.colType, action.part1, 0, eventForCalUpdate.side,
                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);

        }
                break;
        case Collision.PARTICLE_ALL: {
                // update all particle-particle pairs (but no boundary events)

                for (int p1 = 0; p1 < particles.size(); p1++) {
                        Particle part1 = (Particle) particles.get(p1);
                        for (int p2 = 0; p2 < p1; p2++) {
                                Particle part2 = (Particle) particles.get(p2);
                                EventInfo lastCollision = lastCollisions.getLastCollision(p1, p2);
                                part1.predCol(part2, lastCollision, eventForCalUpdate);
                                cal.Update(eventForCalUpdate.colType, p1, p2, eventForCalUpdate.side,
                                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                        }
                }
        }
                break;
        case Collision.BOUNDARY_ALL: {
                // update all the boundary events for all particles (but no
                // particle-
                // particle events)

                for (int i = 0; i < particles.size(); i++) {
                        eventForCalUpdate = ((Particle) particles.get(i)).predBoundaryCol(getPiston(), holeOpen);
                        cal.Update(eventForCalUpdate.colType, i, 0, eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                }
        }
                break;
        // TODO: create code for handling piston-particle interaction
        case Collision.PISTON_ALL: {
                for (int i = 0; i < particles.size(); i++) {
                        // customize for only collision detection with piston
                        // event = ((Particle)particles.get(i)).predBoundaryCol();
                        eventForCalUpdate = ((Particle) particles.get(i)).predBoundaryCol(getPiston(), holeOpen);
                        if (eventForCalUpdate != null)
                                cal.Update(eventForCalUpdate.colType, i, 0, eventForCalUpdate.side,
                                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                }
        }
                break;
//				 TODO: create code for handling piston-particle interaction

        case Collision.EVERYTHING: {
                // update all events for all particles
                for (int p1 = 0; p1 < particles.size(); p1++) {
                        Particle part1 = (Particle) particles.get(p1);
                        for (int p2 = 0; p2 < p1; p2++) {
                                Particle part2 = (Particle) particles.get(p2);
                                EventInfo lastCollision = lastCollisions.getLastCollision(p1, p2);
                                part1.predCol(part2, lastCollision, eventForCalUpdate);
                                cal.Update(eventForCalUpdate.colType, p1, p2, eventForCalUpdate.side,
                                                eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                        }
                        eventForCalUpdate = part1.predBoundaryCol(getPiston(), holeOpen);
                        cal.Update(eventForCalUpdate.colType, p1, 0, eventForCalUpdate.side, eventForCalUpdate.colTime, eventForCalUpdate.particlesInvolved);
                }
        }
        // case Collision.RESORT:
        // nothing to do for re-sort, the sorting occurs below
        }

        // always sort the calendar after updating
        cal.FindMinimum();
    }

    public Piston getPiston() {
            return piston;
    }

    public Thermostat getThermostat() {
            return thermostat;
    }

    /**
     * Calculates the percent of length in the piston's direction that is the
     * minimum length required to have enough space to fit all the particles
     * without overloading collision detection code
     *
     * @author Derek Manwaring
     *
     * @return Percent of length in the piston's direction
     */
    public static double calculateCompressionLimit(SimulationInfo simInfo) {

        // Volume all the partricles require just to be there when modeled as cubes
        // instead of spheres
        double totalParticleVolume = 0.0;
        // Product of arena lengths not in the piston's direction
        double independentArenaSize = 0.0;
        // Biggest particle's diameter
        double biggestDiameter = 0.0;
        // Size of arena in piston's direction
        double pistonArenaSize = 0.0;

        switch(simInfo.dimension){
        case 1:
            pistonArenaSize = simInfo.arenaXSize;
            // For one dimension sum the diameters of all particles
            for(ParticleType type: simInfo.getParticleTypes()) {
                double diameter = 2.0 * type.particleRadius;
                if (diameter > biggestDiameter) {
                    biggestDiameter = diameter;
                }
                totalParticleVolume += diameter * simInfo.getNumberOfParticles(type);
            }
            
            // The only arena dimension is in the direction of the piston
            independentArenaSize = 1.0;
            break;
        case 2:
            pistonArenaSize = simInfo.arenaYSize;
            // For two dimensions sum the squares of the particles' diameters
            for(ParticleType type: simInfo.getParticleTypes()) {
                double diameter = 2.0 * type.particleRadius;
                if (diameter > biggestDiameter) {
                    biggestDiameter = diameter;
                }
                totalParticleVolume += diameter * diameter * simInfo.getNumberOfParticles(type);
            }
            // Piston comes from the top so the arena size that is unchanged by the
            // piston is the x size
            independentArenaSize = simInfo.arenaXSize;
            break;
        case 3:
            pistonArenaSize = simInfo.arenaYSize;
            // For three dimensions sum the cubes of the particles' diameters
            for(ParticleType type: simInfo.getParticleTypes()) {
                double diameter = 2.0 * type.particleRadius;
                if (diameter > biggestDiameter) {
                    biggestDiameter = diameter;
                }
                totalParticleVolume += diameter * diameter * diameter * simInfo.getNumberOfParticles(type);
            }
            // Piston comes from the top so the arena sizes that are unchanged by
            // the piston are the x and z sizes
            independentArenaSize = simInfo.arenaXSize * simInfo.arenaZSize;
        }

        // Minimum length required in the piston's direction to create a volume big enough
        // to fit totalParticleVolume
        double minimumLength = totalParticleVolume / independentArenaSize;
        // Buffer zone of the biggest particle's diameter for two reasons:
        // 1. To keep particles far enough apart to not overload collision code
        // 2. In reality the particles will likely not be able to fit in this minimum
        //    dimension because they require a certain amount of height to fit in a space
        //    For example, in 2D if there is a line of particles along the bottom, just one
        //    extra particle will require about a diameter of space to fit on the next row
        minimumLength += 2.0 * biggestDiameter;
        // Cutoff minimum length at arena size in piston's direction
        if (minimumLength > pistonArenaSize) {
            minimumLength = pistonArenaSize;
        }

        return 100.0 * minimumLength / pistonArenaSize;
    }

    private Map<Particle, PartState> getParticleStates() {
        Map<Particle, PartState> particleStates = new HashMap<Particle, PartState>();
        int particleIndex = 0;
        for (Particle p: particles) {
            particleStates.put(p, p.getState(workingStates[particleIndex]));
            particleIndex++;
        }
        //Switch which array we're using so particle states are not overwritten
        //before frame is drawn
        switchWorkingStatesArray();        
        
        return particleStates;
    }
    
    private void switchWorkingStatesArray() {
        if (workingStates == allocatedStates1) {
            workingStates = allocatedStates2;
        } else {
            workingStates = allocatedStates1;
        }
    }

    public double getTime() {
        return currSimTime;
    }

    public SimulationInfo getSimulationInfo() {
        return simulationInfo;
    }

    public void setFrameDuration(double frameDuration, Time durationUnits) {
        this.frameDuration = Units.convert(durationUnits, Time.SECOND, frameDuration);
    }

    public void reset() {
        // 1) Copy standard variables that provide state information and
        // set/initialize other variables

        arena = simulationInfo.arenaInfo;
        holeOpen = false;
        currSimTime = 0;

        // 3) Create the piston and thermostat
        piston = new Piston(currSimTime, 0.0, simulationInfo.maxPistonLevel, simulationInfo);
        thermostat = new Thermostat(false);

        for (Particle particle: particles) {
            particle.reset();
        }

        initializeLastCollisions();

        // 6) Instantiate the calendar, predict initial events and sort
        cal = new Calendar(particles.size());

        CalUpdate(new EventInfo(Collision.EVERYTHING));
        
        resetStatistics(0.0);
        statisticsReset = false;    
        
        firstRun = true;
    }
    
    private void initializeLastCollisions() {
        lastCollisions = new LastCollisions(simulationInfo.totalNumParticles, DEFAULT_LAST_COLLISION);  
        
        if (simulationInfo.attractiveParticleInteractions) {
        
            for (int particleIndex1 = 0; particleIndex1 < particles.size(); particleIndex1++) {
                Particle part1 = (Particle) particles.get(particleIndex1);
                for (int particleIndex2 = 0; particleIndex2 < particleIndex1; particleIndex2++) {
                    Particle part2 = (Particle) particles.get(particleIndex2);
                    double distance = Formulas.distance(part1.getPosition(), part2.getPosition());
                    double wellSize = simulationInfo.radiusOfInteractionMultiplier * 
                            (part1.radius + part2.radius);
                    if (distance < wellSize) {                                
                        lastCollisions.setLastCollision(particleIndex1, particleIndex2, 
                                new EventInfo(Collision.ENTER_WELL, currSimTime, -1, -1, -1,
                                        null));
                    }
                }
            }
        }
    }

    /** 
     * Sets the total momentum of the system - only valid in periodic boundaries.
     * 
     * Special cases:
     * (1) If there are fewer than two particles, it does nothing
     * (2) If the original kinetic energy is zero (i.e., all particles stationary), 
     * 		it does nothing
     * (3) If the adjusted kinetic energy is zero, (i.e., all particles are 
     * 		drifting the same direction with no relative motion, and they are adjusted to zero),
     * 		it adjusts the momentum but does not try to adjust the kinetic energy
     */
    public void SetTotalMom(double xMom, double yMom, double zMom) {

        if (simulationInfo.isPeriodic()) {

//            // Pause the simulation to avoid threading conflicts while adjusting...
//            boolean pausedHere = false;
//            if (! this.paused) {
//                    pausedHere = true;
//                    this.Pause();
//            }


            double origKE = 0, currKE = 0, f = 0;
            // Current x, y, z total momentum
            double currX = 0, currY = 0, currZ = 0;
            // Momentum adjustments per particle
            double xAdj = 0, yAdj = 0, zAdj = 0;
            int numPart = particles.size();

            // only valid if there are two or more particles...
            if (numPart >= 2) {

                for (int i = 0; i < numPart; i++) {
                    origKE += ((Particle) particles.get(i)).getKE();
                    currX += ((Particle) particles.get(i)).getXMom();
                    if (simulationInfo.dimension > 1)
                        currY += ((Particle) particles.get(i)).getYMom();
                    if (simulationInfo.dimension > 2)
                        currZ += ((Particle) particles.get(i)).getZMom();
                }

                // only valid if particles already have some energy...
                if (origKE > 0.0) {

                    xAdj = (currX - xMom) / numPart;
                    if (simulationInfo.dimension > 1)
                        yAdj = (currY - yMom) / numPart;
                    if (simulationInfo.dimension > 2)
                        zAdj = (currZ - zMom) / numPart;

                    // adjust the total momentum to zero and gather KE information
                    for (int i = 0; i < numPart; i++) {
                        ((Particle) particles.get(i)).adjust(xAdj, yAdj, zAdj);
                        currKE += ((Particle) particles.get(i)).getKE();
                    }
                    if (currKE > 0.0) {
                        // correct the KE to re-establish the old average
                        // (unless it is a unified drift with no relative motion)
                        f = Math.sqrt(origKE / currKE);
                        for (int i = 0; i < numPart; i++)
                            ((Particle) particles.get(i)).adjust(f);
                    }

                    // update the calendar
                    CalUpdate(new EventInfo(Collision.EVERYTHING));
                    
                } // end if (origKE > 0.0)
            } // end if (numpart >= 2)

//            if (pausedHere) {
//                this.Pause(); // unpause
//            }
//
//            if (paused) {
//                activeStat.update = true;
//                AsyncUpdateQ();
//            }
        }
    }

    public double getFrameDuration() {
        return frameDuration;
    }
    
    private boolean useFiniteSystemCorrections, useRealGasCorrections;

    private void initiateStatistics(StatSettingsInfo statInfo) {
        statistics = new HashMap<Set<ParticleType>, Map<StatisticID, Statistic>>();
        for (Set<ParticleType> currentTypes: Formulas.getAllCombinations(
                new HashSet<ParticleType>(simulationInfo.getParticleTypes()))) {
            Map<StatisticID, Statistic> currentStatistics = new EnumMap<StatisticID, Statistic>(StatisticID.class);
            for (StatisticID currentStatisticClass: simulationInfo.getStatistics(currentTypes)) {
                Statistic currentStatistic = currentStatisticClass.createStatistic(simulationInfo, currentTypes);
                currentStatistics.put(currentStatisticClass, currentStatistic);
            }
            statistics.put(currentTypes, currentStatistics);
        }
        
        setCorrectionsForStats(useFiniteSystemCorrections, useRealGasCorrections);
        
        setPressureAveragingTime(statInfo.pressureAveragingTime);
        setRDFUpdateMultiplier(statInfo.rdfUpdateFreq);
        setExhaustiveRDFCalcs(statInfo.exhausiveRDFCalcs);
    }

    public void setFiniteSysCorrections(boolean corrections) {
        useFiniteSystemCorrections = corrections;
        setCorrectionsForStats(corrections, useRealGasCorrections);
    }
    
    public void setRealGasCorrections(boolean corrections) {
        useRealGasCorrections = corrections;
        setCorrectionsForStats(useFiniteSystemCorrections, corrections);
    }
    
    private void setCorrectionsForStats(boolean finiteCorrections, boolean realGasCorrections) {
        if (statistics != null) {
            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
                for (Statistic statistic: statsByClass.values()){
                    statistic.setFiniteSysCorrections(finiteCorrections);
                    statistic.setRealGasCorrections(realGasCorrections);
                }
            }
        }
    }
    
    public void setPressureAveragingTime(double avgTime){
        if (statistics != null)
            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
                Statistic pressureStat = statsByClass.get(StatisticID.PRESSURE);
                if(pressureStat != null)
                    ((Pressure)pressureStat).setAveragingTime(avgTime);
            }
    }
    
    public void setRDFUpdateMultiplier(double rdfMult) {
//        if (statistics != null)
//            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
//                Statistic rdfStat = statsByClass.get(StatisticID.RADIAL_DISTRIBUTION);
//                if(rdfStat != null)
//                    ((RadialDistribution)rdfStat).setRDFUpdateMultiplier(rdfMult);
//            }
    }
    
    public void setExhaustiveRDFCalcs(boolean exhaustiveRDF) {
//        if (statistics != null)
//            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
//                Statistic rdfStat = statsByClass.get(StatisticID.RADIAL_DISTRIBUTION);
//                if(rdfStat != null)
//                    ((RadialDistribution)rdfStat).setExhaustiveRDFCalcs(exhaustiveRDF);
//            }
    }

    public Statistic getStatistic(Set<ParticleType> types, StatisticID statisticID) {
        Map<StatisticID, Statistic> statMap = statistics.get(types);

        if (statMap != null) {
            return statistics.get(types).get(statisticID);
        } else {
            throw new IllegalArgumentException("No statistic " + statisticID.getDisplayName() + " for " + types);
        }
    }

    private void resetStatistics(double newStartTime) {
        if (newStartTime == 0.0) {
            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
                for (Statistic statistic: statsByClass.values()) {
                    statistic.reset();
                }
            }
        } else {
            for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
                for (Statistic statistic: statsByClass.values()) {
                    statistic.clear();
                }
            }
            
        }
    }
    
    private double forgetMultiplier;
    private double collisionLifetime = 0.0;
    private boolean statisticsReset = false;
    
    public void updateStatistics(EventInfo event) {
        if (!statisticsReset && 
                event.colTime > (forgetMultiplier * collisionLifetime)) {
            resetStatistics(event.colTime);
            statisticsReset = true;
        }
        
        for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
            for (Statistic statistic: statsByClass.values()) {
                statistic.notifyOfEvent(event);
            }
        }
    }
    
    public void stopProcessingCurrentFrame() {
        if (processingFrame) {
            stopProcessingCurrentFrame = true;
        }
    }

    public void stop() {
        runPhysics = false;
    }

    public void continueRunning() {
        runPhysics = true;
    }

    private void notifyStatisticsOfTime(double simulationTime) {
        for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
            for (Statistic statistic: statsByClass.values()) {
                statistic.notifyOfSimulationTime(simulationTime);
            }
        }
    }
    
    public void setForgetMultiplier(double mult) {
        forgetMultiplier = mult;
    }

    public void notifyStatisticsOfCurrentFrame() {
        for (Map<StatisticID, Statistic> statsByClass: statistics.values()) {
            for (Statistic statistic: statsByClass.values()) {
                statistic.useFrameForCurrentCalculations(currentFrame);
            }
        }
    }

    public void setArenaHoleOpen(boolean arenaHoleOpen) {
        /**
         * Don't change the hole's state while the current frame is being processed.
         */
        nextFrameHoleOpen = arenaHoleOpen;
    }

}
