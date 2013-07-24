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

import edu.byu.chem.boltzmann.model.statistics.*;
import edu.byu.chem.boltzmann.utils.Units.Unit;
import edu.byu.chem.boltzmann.utils.Units.Velocity;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * 18 May 2012
 */
public enum StatisticID {
        
    INSTANTANEOUS_SPEED("Instantaneous Speed") {
        @Override
        public InstantaneousSpeed createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new InstantaneousSpeed(simInfo, types);
        }
        
        @Override
        protected Class<InstantaneousSpeed> getStatisticClass() {
            return InstantaneousSpeed.class;
        }
    },
    
    AVERAGE_SPEED("Average Speed") {
        @Override
        public AverageSpeed createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new AverageSpeed(simInfo, types);
        }
        
        @Override
        protected Class<AverageSpeed> getStatisticClass() {
            return AverageSpeed.class;
        }
    },
    
    RMS_VELOCITY("RMS Velocity") {
        public RMSVelocity createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new RMSVelocity(simInfo, types);
        }
        
        protected Class<RMSVelocity> getStatisticClass() {
            return RMSVelocity.class;
        }
    },
    
    AVERAGE_RMS_VELOCITY("Average RMS Velocity") {
        public AverageRMSVelocity createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new AverageRMSVelocity(simInfo, types);
        }
        
        protected Class<AverageRMSVelocity> getStatisticClass() {
            return AverageRMSVelocity.class;
        }
    },
    
    X_VELOCITY("X Velocity") {
        @Override
        public XVelocity createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new XVelocity(simInfo, types);
        }
        
        @Override
        protected Class<XVelocity> getStatisticClass() {
            return XVelocity.class;
        }
    },
    
    Y_VELOCITY("Y Velocity") {
        @Override
        public YVelocity createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new YVelocity(simInfo, types);
        }
        
        @Override
        protected Class<YVelocity> getStatisticClass() {
            return YVelocity.class;
        }
    },
    
    Z_VELOCITY("Z Velocity") {
        @Override
        public ZVelocity createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new ZVelocity(simInfo, types);
        }
        
        @Override
        protected Class<ZVelocity> getStatisticClass() {
            return ZVelocity.class;
        }
    },
    
    KINETIC_ENERGY("Kinetic Energy") {
        @Override
        public KineticEnergy createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new KineticEnergy(simInfo, types);
        }
        
        @Override
        protected Class<KineticEnergy> getStatisticClass() {
            return KineticEnergy.class;
        }
    },
    
    AVERAGE_ENERGY("Average Energy") {
        @Override
        public AverageEnergy createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new AverageEnergy(simInfo, types);
        }
        
        @Override
        protected Class<AverageEnergy> getStatisticClass() {
            return AverageEnergy.class;
        }
    },
    
    PATH("Path") {
        @Override
        public Path createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new Path(simInfo, types);
        }
        
        @Override
        protected Class<Path> getStatisticClass() {
            return Path.class;
        }
    },
    
    AVERAGE_PATH("Average Path") {
        @Override
        public AveragePath createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new AveragePath(simInfo, types);
        }
        
        @Override
        protected Class<AveragePath> getStatisticClass() {
            return AveragePath.class;
        }
    },
    
    TIME("Time/Collision") {
        @Override
        public TimePerCollision createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new TimePerCollision(simInfo, types);
        }
        
        @Override
        protected Class<TimePerCollision> getStatisticClass() {
            return TimePerCollision.class;
        }
    },
    
    COLLISION_RATE("Collision Rate") {
        @Override
        public CollisionRate createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new CollisionRate(simInfo, types);
        }
        
        @Override
        protected Class<CollisionRate> getStatisticClass() {
            return CollisionRate.class;
        }
    },
    
    PRESSURE("Pressure") {
        @Override
        public Pressure createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new Pressure(simInfo, types);
        }
        
        @Override
        protected Class<Pressure> getStatisticClass() {
            return Pressure.class;
        }
    }/*,
    
    RADIAL_DISTRIBUTION("Radial Distribution") {
        @Override
        public RadialDistribution createStatistic(SimulationInfo simInfo, Set<ParticleType> types) {
            return new RadialDistribution(simInfo, types);
        }
        
        @Override
        protected Class<RadialDistribution> getStatisticClass() {
            return RadialDistribution.class;
        }
    }*/;

    private final String displayName;
    
    private StatisticID(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean implementsInterface(Class<? extends Statistic> interfaze) {
        if (!interfaze.isInterface()) {
            throw new IllegalArgumentException(interfaze.getSimpleName() + " is not an interface");
        }
        
        return interfaze.isAssignableFrom(getStatisticClass());
    }
    
    public abstract Statistic createStatistic(SimulationInfo simInfo, Set<ParticleType> types);    
    protected abstract Class<? extends Statistic> getStatisticClass(); 
}
