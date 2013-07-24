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
package edu.byu.chem.boltzmann.model.statistics;

import edu.byu.chem.boltzmann.model.physics.Collision;
import edu.byu.chem.boltzmann.model.physics.EventInfo;
import edu.byu.chem.boltzmann.model.physics.FrameInfo;
import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.model.physics.Wall;
import edu.byu.chem.boltzmann.model.physics.Particle;
import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.model.physics.Piston;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created Feb 2011
 * @author Derek Manwaring
 */
public class WallPressure extends Pressure {

    private class PressureVal {
        private double time = 0.0;
        private double value = 0.0;

        PressureVal (double time, double value) {
            this.time = time;
            this.value = value;
        }
    }

    private static final String DISPLAY_NAME = "Wall Pressure";

    private LinkedList<PressureVal> recordedPressureValues = new LinkedList<PressureVal>();
    private double totalPressureValue = 0.0; //Over pressure averaging time
    private double cumulativePressureValue = 0.0; //Over entire simulation
    private double startRecordTime = 0.0;
    private double averagingTime = Units.convert("ps", "s", 60.0);
    private boolean enoughValuesRecorded = false; //True when recorded values span the averaging time
    
    private double effectiveYLen;

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public void initiateStatistic(Set<ParticleType> types, Physics physics) {
        super.initiateStatistic(types, physics);
        
        piston = physics.getPiston();
        
        startRecordTime = physics.getTime();
        
        effectiveYLen = simulationInfo.arenaYSize * 2.0; //WallPressure measured from left and right sides     
    }

    protected void updateStatisticAvgs(FrameInfo frame) {
//        double effectiveYLen = simulationInfo.arenaYSize * 2.0; //WallPressure measured from left and right sides
//
//        for (EventInfo event: frame.getEvents()) {
//            if (shouldRecordCollision(event)) {
//                if (simulationInfo.arenaType == ArenaType.MOVABLE_PISTON) {
//                    double pistonPos = frame.getPistonPosition(event.colTime);
//                    effectiveYLen = pistonPos * 2.0;
//                }
//                
//                Particle collisionPart = event.getInvolvedParticles()[0];
//                PartState collState = frame.getParticleState(collisionPart, event.colTime);
//
//                if (shouldRecordStats(collState.color)) {
//                    double momentumChange = Math.abs(
//                            collState.velocity[0] * Units.convert("amu", "kg", collisionPart.mass));
//
//                    recordCollision(momentumChange, collisionPart.radius, effectiveYLen, event.colTime);
//                }
//            }
//        }
    }
    
    private final PartState workingState = new PartState(0, 0, 0, 0, null, 0);
    
    private Piston piston;
    
    @Override
    public void updateByEvent(EventInfo event) {
        if (shouldRecordCollision(event)) {
            if (simulationInfo.arenaType == ArenaType.MOVABLE_PISTON) {
                double pistonPos = piston.getPosition();
                effectiveYLen = pistonPos * 2.0;
            }

            Particle collisionPart = event.getInvolvedParticles()[0];
            PartState collState = collisionPart.getState(workingState);

            if (shouldRecordStats(collState.color)) {
                double momentumChange = Math.abs(
                        collState.velocity[0] * Units.convert("amu", "kg", collisionPart.mass));

                recordCollision(momentumChange, collisionPart.radius, effectiveYLen, event.colTime);
            }
        }
    }

    private boolean shouldRecordCollision(EventInfo event) {
        boolean recordCollision = false;

        if (event.colType == Collision.WALL) {
            if (event.side == Wall.LEFT || event.side == Wall.RIGHT) {
                recordCollision = true;
            }
        }

        return recordCollision;
    }

    private void recordCollision(double momentumChange, double particleRadius, double effectiveYLen, double time) {
        double value = 0.0;
        double diam = 2.0 * particleRadius;
        
        switch (simulationInfo.dimension) {
            case 1:
                value = 2.0 * momentumChange;
                break;
            case 2:
                value = 2.0 * momentumChange / (effectiveYLen - diam);
                break;
            case 3:
                value = 2.0 * momentumChange / ((effectiveYLen - diam) * (simulationInfo.arenaZSize - diam));
        }

        updatePressureValues(value, time);
    }

    private void updatePressureValues(double newValue, double currentTime) {
        recordedPressureValues.add(new PressureVal(currentTime, newValue));
        totalPressureValue += newValue;
        cumulativePressureValue += newValue;

        if (!recordedPressureValues.isEmpty()) {
            PressureVal firstVal = recordedPressureValues.getFirst();

            //Remove values older than the averaging time from the list of values
            while (firstVal.time < (currentTime - averagingTime)) {
                enoughValuesRecorded = true;
                double removeValue = recordedPressureValues.removeFirst().value;
                totalPressureValue -= removeValue;

                if (recordedPressureValues.isEmpty()) {
                    break;
                } else {
                    firstVal = recordedPressureValues.getFirst();
                }
            }
        }
    }

    @Override
    public double getAverage() {

        double totalTime = lastFrameEndTime - startRecordTime;

        if (totalTime == 0.0) {
            return 0.0;
        }

        double pressureBadUnits = cumulativePressureValue / totalTime;
        double pressure = changePressureUnits(pressureBadUnits);
        return pressure;
    }

    @Override
    public double getInstAverage() {
        if (recordedPressureValues.size() == 0) {
            return 0.0;
        }

        double totalTime = 0.0;
        if (enoughValuesRecorded) {
            totalTime = averagingTime;
        } else {
            totalTime = recordedPressureValues.getLast().time -
                    recordedPressureValues.getFirst().time;
            if (totalTime == 0.0) {
                return 0.0;
            }
        }

        double pressureBadUnits = totalPressureValue / totalTime;
        double pressure = changePressureUnits(pressureBadUnits);
        return pressure;
    }

    @Override
    public void reset(double newStartTime, Physics physics) {
        super.reset(newStartTime, physics);

        enoughValuesRecorded = false;
        recordedPressureValues.clear();
        totalPressureValue = 0.0;
        cumulativePressureValue = 0.0;
        startRecordTime = newStartTime;
    }
}
