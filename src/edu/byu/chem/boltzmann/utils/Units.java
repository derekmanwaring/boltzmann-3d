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
package edu.byu.chem.boltzmann.utils;

import edu.byu.chem.boltzmann.model.statistics.interfaces.Range;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains enums of different units to allow for simple conversion. To add
 * another unit type (time, mass, etc.) a new enum must be created that implements
 * the Unit interface. This enum must then be added to the unitEnums list in the static
 * method that maps the unit symbols to the unit enums. To add another unit to use in
 * conversions (joules, centimeters), just add another element to the enum with the
 * correct parameters.
 * @author Derek Manwaring
 */
@SuppressWarnings({"unchecked"})
public class Units {

    public static final Unit DIMENSIONLESS = new Unit() {
        public String getSymbol() {
            return "";
        }
        public Unit getBaseUnit() {
            return this;
        }
        public double getBaseUnitEquivalent() {
            return 1.0;
        }
    };

    public static final double AVOGADROS_NUM = 6.02214179E23;
    public enum Energy implements Unit<Energy> {
        JOULE("J", 1.0), //Base unit
        KILOJOULE("kJ", 1.0E3),
        AMU_JOULE("amu·m^2/s^2", Mass.ATOMIC_MASS_UNIT.kgEquivalent),
        JOULE_PER_MOLE("J/mol", 1.0 / AVOGADROS_NUM),
        KILOJOULE_PER_MOLE("kJ/mol",  1.0E3 / AVOGADROS_NUM);//Base unit is joule, not kJ (assumes other units of energy describe one particle)

        private final String unitSymbol;
        private final double jouleEquivalent;

        Energy(String symbol, double inJoules) {
            unitSymbol = symbol;
            jouleEquivalent = inJoules;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Energy getBaseUnit() {
            return JOULE;
        }

        public double getBaseUnitEquivalent() {
            return jouleEquivalent;
        }
    }

    public enum Length implements Unit<Length> {
        METER("m", 1.0), //Base unit
        NANOMETER("nm", 1.0E-9);

        public final String unitSymbol;
        private final double meterEquivalent;

        Length(String symbol, double inMeters) {
            this.unitSymbol = symbol;
            this.meterEquivalent = inMeters;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Length getBaseUnit() {
            return METER;
        }

        public double getBaseUnitEquivalent() {
            return meterEquivalent;
        }
    }

    public enum Time implements Unit<Time> {
        SECOND("s", 1.0), //Base unit
        MILLISECOND("ms", 1.0E-3),
        MICROSECOND("µs", 1.0E-6),
        NANOSECOND("ns", 1.0E-9),
        PICOSECOND("ps", 1.0E-12);

        public final String unitSymbol;
        private final double secondEquivalent;

        Time(String symbol, double inSeconds) {
            this.unitSymbol = symbol;
            this.secondEquivalent = inSeconds;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Time getBaseUnit() {
            return SECOND;
        }

        public double getBaseUnitEquivalent() {
            return secondEquivalent;
        }
    }

    public enum Mass implements Unit<Mass> {
        KILOGRAM("kg", 1.0), //Base unit
        ATOMIC_MASS_UNIT("amu", 1.660538782E-27);

        public final String unitSymbol;
        private final double kgEquivalent;

        Mass(String symbol, double inKg) {
            this.unitSymbol = symbol;
            this.kgEquivalent = inKg;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Mass getBaseUnit() {
            return KILOGRAM;
        }

        public double getBaseUnitEquivalent() {
            return kgEquivalent;
        }
    }

    /**
     * Currently, unit conversion will not work for Temperature units because
     * it assumes no offset (Fahrenheit/Celsius versus Kelvin.
     */
    public enum Temperature implements Unit<Temperature> {
        KELVIN("K", 1.0); //Base unit

        public final String unitSymbol;
        private final double kelvinEquivalent;

        Temperature(String symbol, double inKelvin) {
            this.unitSymbol = symbol;
            this.kelvinEquivalent = inKelvin;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Temperature getBaseUnit() {
            return KELVIN;
        }

        public double getBaseUnitEquivalent() {
            return kelvinEquivalent;
        }
    }

    public enum Velocity implements Unit<Velocity> {
        METER_PER_SECOND("m/s", 1.0), //Base unit
        NANOMETER_PER_PICOSECOND("nm/ps", 1000.0);

        public final String unitSymbol;
        private final double mpsEquivalent;

        Velocity(String symbol, double inMetersPerSecond) {
            this.unitSymbol = symbol;
            this.mpsEquivalent = inMetersPerSecond;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Velocity getBaseUnit() {
            return METER_PER_SECOND;
        }

        public double getBaseUnitEquivalent() {
            return mpsEquivalent;
        }
    }
    
    public enum Momentum implements Unit<Momentum> {
        KG_METER_PER_SECOND("kg·m/s", 1.0), //Base unit
        AMU_METER_PER_SECOND("amu·m/s", Mass.ATOMIC_MASS_UNIT.getBaseUnitEquivalent());

        public final String unitSymbol;
        private final double kgMPSEquivalent;

        Momentum(String symbol, double inKGMPS) {
            this.unitSymbol = symbol;
            this.kgMPSEquivalent = inKGMPS;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public double getBaseUnitEquivalent() {
            return kgMPSEquivalent;
        }

        public Momentum getBaseUnit() {
            return KG_METER_PER_SECOND;
        }
    }
    
    public enum PressureUnit implements Unit<PressureUnit> {
        NEWTON("N", 1.0), //1D Base unit
        PICONEWTON("pN", 1.0E-12),
        
        NEWTON_PER_METER("N/m", 1.0), //2D Base unit
        MICRONEWTON_PER_METER("µN/m", 1.0E-6),
        
        PASCAL("Pa", 1.0), //3D Base unit
        MEGAPASCAL("MPa", 1.0E6);

        public final String unitSymbol;
        private final double newtonEquivalent;
        private final int dimension;

        PressureUnit(String symbol, double inNewtons) {
            unitSymbol = symbol;
            newtonEquivalent = inNewtons;
            
            if(symbol.contains("/"))
                dimension = 2;
            else if(symbol.contains("P"))
                dimension = 3;
            else
                dimension = 1;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public PressureUnit getBaseUnit() {
            return getBaseUnit(dimension);
        }
        
        public static PressureUnit getBaseUnit(int pressureDimension) {
            switch(pressureDimension){
                case 1:
                    return NEWTON;
                case 2:
                    return NEWTON_PER_METER;
                case 3:
                    return PASCAL;
                default:
                    throw new RuntimeException("Invalid pressure dimension.");
            }
        }

        public double getBaseUnitEquivalent() {
            return newtonEquivalent;
        }
    }

//    //pressure in one dimension is force
//    public enum Pressure1D implements Unit<Pressure1D> {
//        NEWTON("N", 1.0), //Base unit
//        PICONEWTON("pN", 1.0E-12);
//
//        public final String unitSymbol;
//        private final double newtonEquivalent;
//
//        Pressure1D(String symbol, double inNewtons) {
//            this.unitSymbol = symbol;
//            this.newtonEquivalent = inNewtons;
//        }
//
//        public String getSymbol() {
//            return unitSymbol;
//        }
//
//        public Pressure1D getBaseUnit() {
//            return NEWTON;
//        }
//
//        public double getBaseUnitEquivalent() {
//            return newtonEquivalent;
//        }
//    }
//
//    //Two-dimensional pressure or force per unit length
//    public enum Pressure2D implements Unit<Pressure2D> {
//        NEWTON_PER_METER("N/m", 1.0), //Base unit
//        MICRONEWTON_PER_METER("µN/m", 1.0E-6);
//
//        public final String unitSymbol;
//        private final double npmEquivalent;
//
//        Pressure2D(String symbol, double inNewtons) {
//            this.unitSymbol = symbol;
//            this.npmEquivalent = inNewtons;
//        }
//
//        public String getSymbol() {
//            return unitSymbol;
//        }
//
//        public Pressure2D getBaseUnit() {
//            return NEWTON_PER_METER;
//        }
//
//        public double getBaseUnitEquivalent() {
//            return npmEquivalent;
//        }
//    }
//
//    public enum Pressure3D implements Unit<Pressure3D> {
//        PASCAL("Pa", 1.0), //Base unit
//        MEGAPASCAL("MPa", 1.0E6);
//
//        public final String unitSymbol;
//        private final double pascalEquivalent;
//
//        Pressure3D(String symbol, double inNewtons) {
//            this.unitSymbol = symbol;
//            this.pascalEquivalent = inNewtons;
//        }
//
//        public String getSymbol() {
//            return unitSymbol;
//        }
//
//        public double getBaseUnitEquivalent() {
//            return pascalEquivalent;
//        }
//
//        @Override
//        public Pressure3D getBaseUnit() {
//            return PASCAL;
//        }
//    }

    public enum Frequency implements Unit<Frequency> {
        HERTZ("Hz", 1.0), //Base unit
        TERAHERTZ("THz", 1.0E12);

        public final String unitSymbol;
        private final double hertzEquivalent;

        Frequency(String symbol, double inHertz) {
            this.unitSymbol = symbol;
            this.hertzEquivalent = inHertz;
        }

        public String getSymbol() {
            return unitSymbol;
        }

        public Frequency getBaseUnit() {
            return HERTZ;
        }

        public double getBaseUnitEquivalent() {
            return hertzEquivalent;
        }
    }

    public interface Unit<UnitType extends Unit<UnitType>> {
        public String getSymbol();
        public UnitType getBaseUnit();
        public double getBaseUnitEquivalent();
    }

    public static <UnitType extends Unit<UnitType>> double convert(UnitType fromUnit, UnitType toUnit, double value) {
        if (fromUnit == toUnit) {
            return value;
        }
        
        String fromUnitBase = fromUnit.getBaseUnit().getSymbol();
        String toUnitBase = toUnit.getBaseUnit().getSymbol();
        if (!fromUnitBase.equals(toUnitBase)) {
            throw new RuntimeException("Attempted conversion of units of different types.");
        }

        double baseUnitValue = value * fromUnit.getBaseUnitEquivalent();
        return baseUnitValue / toUnit.getBaseUnitEquivalent();
    }

    public static <UnitType extends Unit<UnitType>> Range convert(UnitType fromUnit, UnitType toUnit, Range range) {
        if (fromUnit == toUnit) {
            return range;
        }
        
        return new Range(convert(fromUnit, toUnit, range.min), 
                convert(fromUnit, toUnit, range.max));
    }

    public static double convert(String fromUnit, String toUnit, double value) {
        return convert(allUnits.get(fromUnit), allUnits.get(toUnit), value);
    }

    public static double convert(String fromUnit, Unit toUnit, double value) {
        return convert(allUnits.get(fromUnit), toUnit, value);
    }

    public static double convert(Unit fromUnit, String toUnit, double value) {
        return convert(fromUnit, allUnits.get(toUnit), value);
    }
    
    public static Unit getUnitWithSymbol(String symbol) {
        if (!allUnits.containsKey(symbol)) {
            throw new RuntimeException("No unit with symbol: " + symbol);
        }
        
        return allUnits.get(symbol);
    }

    private static final Map<String, Unit> allUnits = new HashMap<String, Unit>();

    //This method is called only once at program initialization to set up the mapping
    //of unit symbol strings to their respective Unit enum types.
    static {
        //List of all the types of units defined by the enums above
        List<Class> unitEnums = Arrays.asList(new Class[] { Mass.class, Time.class,
            Length.class, Velocity.class, Energy.class, Temperature.class, Momentum.class,
            PressureUnit.class, Frequency.class
        });

        //Loop through each type of unit
        for (Class unitEnum: unitEnums) {
            //Loop through each unit of this type and map it to its symbol
            for (Unit unit: (Set<Unit>) EnumSet.allOf(unitEnum)) {
                if (allUnits.containsKey(unit.getSymbol())) {
                    throw new RuntimeException("Shared unit symbol by " + allUnits.get(unit.getSymbol()) + " and " + unit);
                }
                allUnits.put(unit.getSymbol(), unit);
            }
        }
    }
}
