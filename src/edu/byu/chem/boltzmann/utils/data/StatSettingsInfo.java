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
package edu.byu.chem.boltzmann.utils.data;

/**
 *
 * @author joshuao2
 * July 16, 2012
 */

//this class is used to pass the current options in StatSettingsFullView to new Physics objects
public class StatSettingsInfo {
    public double forgetTime = 4.0;
    public boolean useFinSysCorrections = false;
    public boolean useRealGasCorrections = false;
    
    public double pressureAveragingTime = 60.0;
    public double rdfUpdateFreq = 0.5;
    public boolean exhausiveRDFCalcs = false;

    public StatSettingsInfo(){
    }
    
    public StatSettingsInfo(double time, boolean corrections, boolean realGas,
            double avgTime, double rdfFreq, boolean exhausiveRDF){
        forgetTime = time;
        useFinSysCorrections = corrections;
        useRealGasCorrections = realGas;
        
        pressureAveragingTime = avgTime;
        rdfUpdateFreq = rdfFreq;
        exhausiveRDFCalcs = exhausiveRDF;
    }
}
