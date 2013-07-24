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

import edu.byu.chem.boltzmann.model.physics.PartState;
import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.Units;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 */
public class Formulas {

    /**
     * @return A new vector equal to the vector (any size) multiplied by the scalar
     */
    public static double[] scaleVector(double[] vector, double scalar) {
        double[] scaledVector = Arrays.copyOf(vector, vector.length);
        for (int i = 0; i < vector.length; i++) {
            scaledVector[i] = scaledVector[i] * scalar;
        }

        return scaledVector;
    }
    
    /**
     * @return A double equal to the magnitude of the vector (any size)
     */
    public static double magnitude(double... vector) {
        double magnitude = 0.0;
        for (double component: vector) {
            magnitude += component * component;
        }

        return Math.sqrt(magnitude);
    }

    /**
     * @return A new vector equal to the two vectors added together
     */
    public static double[] addVectors(double[] vector1, double[] vector2) {
        int vectorLength = vector1.length;
        if (vectorLength != vector2.length) {
            throw new RuntimeException("Vectors have unequal lengths.");
        }

        double[] vectorSum = new double[vectorLength];
        for (int i = 0; i < vectorLength; i++) {
            vectorSum[i] = vector1[i] + vector2[i];
        }

        return vectorSum;
    }

    /**
     * @return A scalar representing the magnitude of the distance between two vectors
     */
    public static double distance(double[] vector1, double[] vector2) {
        double[] vectorDifference = addVectors(vector1, scaleVector(vector2, -1.0));
        return magnitude(vectorDifference);
    }

    /**
     * @return Kinentic energy possessed by and object with the given mass and velocity. No unit conversions
     * are applied so resulting units of energy depend wholly on the units of mass and velocity given.
     */
    public static double kineticEnergy(double mass, double speed) {
        return 0.5 * mass * speed * speed;
    }
    
    /**
     * @return Kinetic energy for this PartState in joules
     */
    public static double kineticEnergy(PartState state) {
        return kineticEnergy(
                Units.convert("amu", "kg", state.particleType.particleMass), 
                speed(state));
    }

    /**
     * @return Speed for a particle with the given mass and kinetic energy. No unit conversions are
     * employed
     */
    public static double speed(double kineticEnergy, double mass) {
        return Math.sqrt(2.0 * kineticEnergy / mass);
    }
    
    public static double speed(PartState state) {
        return Formulas.magnitude(state.velocity);
    }

    public final static double BOLTZMANN_CONST = 1.3806504E-23; // (J/K)
    public static double GAS_CONSTANT = 0.008314472; // Kilojoules per mole
    /**
     * See http://en.wikipedia.org/wiki/Boltzmann_constant for the equation
     * @return Average kinetic energy for a group of particles at the given temperature with
     * the given dimensions of freedom. The Boltzmann constant used in this equation has units
     * joules/kelvin so the temperature must be in kelvin and the return value will be in joules
     */
    public static double avgKineticEnergy(double temperature, int dimension) {
        return dimension * BOLTZMANN_CONST * temperature / 2.0;
    }

    public static double correctedKE(
            double mass, 
            double temperature, 
            int dimension, 
            int totalParticles,
            double totalMass) {
            
            return (dimension * BOLTZMANN_CONST * temperature / 2.0) * 
                    ((totalMass - mass) / totalMass) * 
                    (totalParticles / (double)(totalParticles - 1));
        
    }
    
    public static double KEWidth(double temperature, double mass, int dimension) {
        return BOLTZMANN_CONST * temperature * Math.sqrt(dimension / 2.0);
    }

    public static double correctedKEWidth(
            double temperature,
            double mass,
            int dimension,
            boolean periodic,
            int totalParticles,
            double totalMass) {
        switch (dimension) {
            case 1:
                return BOLTZMANN_CONST * temperature * Math.sqrt((totalParticles + 1) / (double)(2 * (totalParticles + 2)));
            case 2:
                return BOLTZMANN_CONST * temperature * Math.sqrt((totalParticles - 1) / (double)(totalParticles + 1));
            default:
                return 1.5 * BOLTZMANN_CONST * temperature * Math.sqrt(2 * (totalParticles - 1) / (double)(3 * (totalParticles + 2)));
        }
    }

    public static double averageVelocity(double temperature, double mass, int dimension) {
        double speed = 0.0;
        switch (dimension) {
            case 1:
                speed = Math.sqrt((2.0 * BOLTZMANN_CONST * temperature) / (Math.PI * mass));
                break;
            case 2:
                speed = Math.sqrt((Math.PI * BOLTZMANN_CONST * temperature) / (2.0 * mass));
                break;
            case 3:
                speed = Math.sqrt((8.0 * BOLTZMANN_CONST * temperature) / (Math.PI * mass));
        }
        return speed;
    }
                
    /** 
     * These calculations are based on equations (14) and (27) in the paper, "Periodic 
     * boundary condition induced breakdown of the equipartition principle and other 
     * kinetic effects of finite sample size in classical hard-sphere molecular dynamics
     * simulation", Shirts, Burt and Johnson 2006.
     */
    static double correctedAverageVelocity(
            double initialTemperature, 
            double mass,
            int dimension,
            boolean periodic,
            int totalParticles,
            double totalMass) {
        double value = 0.0;

        int numParticles = totalParticles;

        if (periodic) {
            numParticles--;
        }

        switch (dimension) {
            case 1:
                value = Math.sqrt((numParticles * BOLTZMANN_CONST * initialTemperature) /
                        (Math.PI * mass)) * 
                        gammaFrac(numParticles / 2.0, (numParticles + 1.0) / 2.0);
                break;
            case 2:
                value = Math.sqrt((Math.PI * numParticles * BOLTZMANN_CONST * initialTemperature) /
                        (2.0 * mass)) * 
                        gammaFrac(numParticles, numParticles + 0.5);
                break;
            case 3:
                value = Math.sqrt((12.0 * numParticles * BOLTZMANN_CONST * initialTemperature) /
                        (Math.PI * mass)) * 
                        gammaFrac(3.0 * numParticles / 2.0, (3.0 * numParticles + 1.0) / 2.0);
        }

        if(periodic) {
            value *= Math.sqrt(((totalMass - mass) / totalMass) * 
                    (totalParticles / (double)(totalParticles - 1)));
        }

        return value;
    }

    public static double rmsVelocity(double temperature, double mass, int dimension) {
        return Math.sqrt(dimension * BOLTZMANN_CONST * temperature / mass);
    }

    public static double correctedRMSVel(
            double temperature, 
            double mass, 
            int dimension,
            boolean periodic) {
        if (periodic)
            throw new UnsupportedOperationException();
        return Math.sqrt(dimension * BOLTZMANN_CONST * temperature / mass);
    }

    public static double velocityWidth(double temperature, double mass, int dimension) {
        double rmsVel = rmsVelocity(temperature, mass, dimension);
        double avgVel = averageVelocity(temperature, mass, dimension);
        return Math.sqrt(rmsVel * rmsVel - avgVel * avgVel);
    }
    
    public static double correctedVelocityWidth(
            double temperature,
            double mass,
            int dimension,
            boolean periodic,
            int totalParticles,
            double totalMass) {
        double rmsVel = correctedRMSVel(temperature, mass, dimension, periodic);
        double avgVel = correctedAverageVelocity(
                temperature, 
                mass,
                dimension,
                periodic,
                totalParticles,
                totalMass);
        return Math.sqrt(rmsVel * rmsVel - avgVel * avgVel);
    }

    public static double velocityComponentWidth(double temperature, double mass) {
        return Math.sqrt(BOLTZMANN_CONST * temperature / mass);
    }

    private static double avgRelativeVelocity(
            double temperature, 
            double particle1Mass, 
            double particle2Mass, 
            int dimension) {
        double reducedMass = (particle1Mass * particle2Mass) / (particle1Mass + particle2Mass);
        double numerator = Math.PI * BOLTZMANN_CONST * temperature;
        double denominator = 2.0 * reducedMass;

        switch (dimension) {
            case 1:
                break;
            case 2:
                numerator = Math.PI * BOLTZMANN_CONST * temperature;
                denominator = 2.0 * reducedMass;
                break;
            case 3:
                numerator = 8.0 * BOLTZMANN_CONST * temperature;
                denominator = Math.PI * reducedMass;
        }

        return Math.sqrt(numerator / denominator);
    }

    public static double effectiveVolume(SimulationInfo simInfo, ParticleType type) {
        return effectiveVolume(simInfo.arenaXSize, 
                simInfo.arenaYSize,
                simInfo.arenaZSize,
                type.particleRadius,
                simInfo.dimension,
                simInfo.arenaType,
                simInfo.ARENA_DIVIDER_RADIUS);
    }
    
    public static double effectiveVolume(
            double arenaXSize,
            double arenaYSize,
            double arenaZSize,
            double typeRadius,
            int dimension,
            ArenaType arenaType,
            double dividerRadius) {
        
        double lossToRadius = 0.0;
        if (arenaType != ArenaType.PERIODIC_BOUNDARIES) {
            lossToRadius = 2.0 * typeRadius; // Area unusable because particles hit the arena walls
        }
        
        double effectiveArenaXSize = arenaXSize;
        if (arenaType == ArenaType.DIVIDED_ARENA) {
            effectiveArenaXSize = arenaXSize / 2 - dividerRadius;
        }

        double effectiveLength = effectiveArenaXSize - lossToRadius;
        double effectiveHeight = arenaYSize - lossToRadius;
        double effectiveDepth = arenaZSize - lossToRadius;
        double effectiveVolume = 0.0;

        switch (dimension) {
            case 1:
                effectiveVolume = effectiveLength;
                break;
            case 2:
                effectiveVolume = effectiveLength * effectiveHeight;
                break;
            case 3:
                effectiveVolume = effectiveLength * effectiveHeight * effectiveDepth;
        }

        return effectiveVolume;
    }

    private static double collisionSphere(double radius1, double radius2, int dimension) {
        double radiiSum = radius1 + radius2;

        double collisionSphere = 0.0;
        switch (dimension) {
            case 1:
                collisionSphere = 1.0;
                break;
            case 2:
                collisionSphere = 2.0 * radiiSum;
                break;
            case 3:
                collisionSphere = Math.PI * radiiSum * radiiSum;
        }
        
        return collisionSphere;
    }
    
    public static double realGasB2(ParticleType type1, ParticleType type2, int dimension){
		double sum = type1.particleRadius + type2.particleRadius;
		switch(dimension){
                    case 1:
                        return sum;
                    case 2:
			return Math.PI * sum*sum / 2;
                    default:
			return 2. / 3 * Math.PI * sum*sum*sum;
                }
    }
    
    //this is c() in the old program and B2_eff / B2 in the manual
    public static double realGasB2Correction(SimulationInfo simInfo, ParticleType type1, ParticleType type2){        
        if(simInfo.dimension == 1 || simInfo.isPeriodic())
            return 1;
        
        double sigma = type1.particleRadius + type2.particleRadius;
        double lx = simInfo.arenaXSize, ly = simInfo.arenaYSize, lz = simInfo.arenaZSize;
        if(simInfo.arenaType == ArenaType.DIVIDED_ARENA)
            lx = lx / 2 - simInfo.ARENA_DIVIDER_RADIUS;

        if(simInfo.dimension == 2)
            return (1 - (2/Math.PI)*(sigma/lx + sigma/ly) + (6/Math.PI)*((sigma/lx)*(sigma/ly)));
        else
            return (1 - (sigma/2)*(1/lx + 1/ly + 1/lz)
                + (11*sigma*sigma/(20*Math.PI))*(1/(lx*ly) + 1/(lx*lz) + 1/(ly*lz))
                + (15*Math.pow(sigma, 3)/(2*Math.PI*lx*ly*lz)));
    }
    
    public static double realGasX(SimulationInfo simInfo, ParticleType type){
	if(simInfo.getNumberOfParticles(type) == 0)
            return 0;
        
        double totalX = 0;
        for(ParticleType collisionType: simInfo.getParticleTypes()){
            double numParticlesForType = simInfo.getNumberOfParticles(collisionType)
                    - (type == collisionType && !simInfo.isPeriodic() ? 1 : 0);
            totalX += realGasB2(type, collisionType, simInfo.dimension) * realGasB2Correction(simInfo, type, collisionType)
                    * numParticlesForType / effectiveVolume(simInfo, collisionType);
        }
        
        return totalX;
    }
    
    public static double realGasQx(double x, int dimension){
        double e1, e2, e3, f1, f2, f3;
        switch(dimension){
            case 2:
                e1 = -0.25233539;
                e2 = 0.02704323;
                e3 = 0.00077137;
                f1 = -1.03433982;
                f2 = 0.30366975;
                f3 = -0.01974721;
                break;
            case 3:
                e1 = 1.30542183;
                e2 = 0.11627913;
                e3 = 0.02452331;
                f1 = 0.68042183;
                f2 = -0.59593402;
                f3 = 0.09148337;
                break;
            default:
                throw new RuntimeException("Invalid dimension for real gas correction factor.");
        }
        return (1 + e1 * x + e2 * x*x + e3 * x*x*x) / (1 + f1 * x + f2 * x*x + f3 * x*x*x);
    }

    private static double predictCollisionRate(
            SimulationInfo simInfo, 
            ParticleType type, 
            ParticleType collisionType) {
        int collisionParticles = simInfo.getNumberOfParticles(collisionType); //Number of particles that a type 1 particle can collide with
        if (type == collisionType)
            //If this is looking at a particle colliding with partilces of its own type, we subtract
            //one from the particles it can collide with because it cannot collide with itself
            collisionParticles -= 1;

        double collisionSphere = collisionSphere(type.particleRadius, collisionType.particleRadius, simInfo.dimension);
        double effectiveVolume = effectiveVolume(simInfo, collisionType);
        double type1Mass = Units.convert("amu", "kg", type.particleMass);
        double type2Mass = Units.convert("amu", "kg", collisionType.particleMass);
        double avgRelativeVelocity = avgRelativeVelocity(
                simInfo.initialTemperature, 
                type1Mass, 
                type2Mass, 
                simInfo.dimension);

        return (collisionParticles * collisionSphere * avgRelativeVelocity) / effectiveVolume;
    }

    public static double predictCollisionRate(SimulationInfo simInfo, ParticleType particleType, boolean realGasCorrections) {
        double collisionRate = 0.0;

        for (ParticleType collisionType: simInfo.getParticleTypes())
            collisionRate += predictCollisionRate(simInfo, particleType, collisionType);
        
        if(realGasCorrections && simInfo.dimension > 1){
            double x = realGasX(simInfo, particleType);
            collisionRate *= realGasQx(x, simInfo.dimension);
        }

        return collisionRate;
    }

    public static double predictCollisionRate(SimulationInfo simInfo, Set<ParticleType> particleTypes, boolean realGasCorrections) {
        double collisionRate = 0.0;
        int totalParticles = 0;

        for (ParticleType type: particleTypes) {
            int numParticles = simInfo.getNumberOfParticles(type);
            collisionRate += predictCollisionRate(simInfo, type, realGasCorrections) * numParticles;
            totalParticles += numParticles;
        }

        if (totalParticles == 0)
            return 0.0;
        else
            return collisionRate / totalParticles;
    }

    /**
     * @return Expected average time between collisions. (Called tao in the old program).
     */
    public static double collisionLifetime(double collisionRate) {
        return 1.0 / collisionRate;
    }

    public static double meanFreePath(double collisionLifetime, double averageVelocity) {//UNUSED, but equals Path.getLambda()
        return collisionLifetime * averageVelocity;
    }

    /**
     * @return The temperature in Kelvin for a group of particles with the given dimensions of freedom
     * and average kinetic energy (in joules)
     */
    public static double temperature(double avgKineticEnergy, int dimension) {
        return 2.0 * avgKineticEnergy / (dimension * BOLTZMANN_CONST);
    }

    /**
     * @return Pressure for particles with the given temperature and volume. No unit conversions
     * are employed
     */
    public static double pressure(int numParticles, double temperature, double volume) {
        return (numParticles * BOLTZMANN_CONST * temperature) / volume;
    }
    
    /**
     * @return A double representing the numerator of <code>fraction</code> divided by the denominator
     * if there is one. It is assumed that if both are contained they will be separated by one '/' character
     */
    public static double evaluateFraction(String fraction) {
        int dividerPos = fraction.indexOf('/');
        double numerator;
        double denominator = 1.0;
        if (dividerPos == -1) { //Not found
            numerator = Double.valueOf(fraction);
        } else {
            numerator = Double.valueOf(fraction.substring(0, dividerPos));
            denominator = Double.valueOf(fraction.substring(dividerPos + 1, fraction.length()));
        }

        return numerator / denominator;
    }
	
    //Calculates the gamma of x over the gamma of y
    public static double gammaFrac(double x, double y) {
        if(x==0||y==0) {
            return 0;
        }
        
        double value=Math.exp(lnGamma(x)-lnGamma(y));
        
        if(Double.isNaN(value)||Double.isInfinite(value)) {
            value=0;
        }
        
        return value;
    }
	
    private static HashMap<Double,Double> gammaMap = new HashMap<Double,Double>();
    /** 
     * Calculates the natural log of the gamma of x 
     */
    private static double lnGamma(double x) {
        Double d=new Double(x);
        if(gammaMap.containsKey(d)) {
            return ((Double)gammaMap.get(d)).doubleValue();
        }
        
        if(x>25) {
            Double val = new Double((x - 0.5) * Math.log(x) - x +
                    0.5*  Math.log(2 * Math.PI) + 
                    1 / (12 * x) - 
                    1 / (360 * Math.pow(x, 3)) + 
                    1 / (1260 * Math.pow(x, 5)));
            gammaMap.put(d, val);
            return val.doubleValue();
        } else {
            Double val=new Double(Math.log(gamma(x)));
            gammaMap.put(d, val);
            return val.doubleValue();
        }
    }

    //calculates the gamma of x (recursively)
    // gamma(n) = (n-1)! for integer n, gamma(n+1) = n!
    public static double gamma(double x) {
        if(x <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        
        if(x == 1 || x == 2) {
            return 1;
        }
        
        if(x == 0.5) {
            return Math.sqrt(Math.PI);
        }
        
        return (x - 1) * gamma(x - 1);
    }
    
    /**
     * @return A set of sets that represent all possible combinations of the given particle types.
     * Elements of the returned set may contain only one particle type or up to as many particle types
     * as were given.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Set<Set<T>> getAllCombinations(Set<T> allElements) {
        Set<Set<T>> combinations = new HashSet<Set<T>>();
        for (T element: allElements) {
            combinations.add(new HashSet(Arrays.asList(new Object[] { element })));

            Set<T> elementSubSet = new HashSet<T>(allElements);
            elementSubSet.remove(element);
            if (!elementSubSet.isEmpty()) {
                for (Set<T> combination: getAllCombinations(elementSubSet)) {
                    combination.add(element);
                    combinations.add(combination);
                }
            }
        }
        
        return combinations;
    }

    /**
     * @return A set containing all unique pairs that can be created from the given
     * collection of objects
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Set<Set<T>> getAllPairs(Collection<T> objects) {
        T[] objectArray = (T[]) objects.toArray();
        Set<Set<T>> objectPairs = new HashSet<Set<T>>();

        for (int i = 0; i < objectArray.length; i++) {
            for (int j = i + 1; j < objectArray.length; j++) {
                Set<T> thisPair = new HashSet<T>();
                thisPair.add(objectArray[i]);
                thisPair.add(objectArray[j]);
                objectPairs.add(thisPair);
            }
        }

        return objectPairs;
    }
}
