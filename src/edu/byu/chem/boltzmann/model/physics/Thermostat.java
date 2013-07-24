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

import edu.byu.chem.boltzmann.controller.Controller;
import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.utils.Units;
import edu.byu.chem.boltzmann.utils.Units.Energy;

public class Thermostat {

	public static final double THERMOSTATEPSILON = 
                Units.convert(Energy.AMU_JOULE, Energy.JOULE, 1.0);

	int mode;
	
	private ThermostatController thermostatController;
	
	private double dampFactor;

	private double deltaKE;
	
	protected boolean isEnabled;
	
	public Thermostat(boolean enabled)
	{
		reset(enabled);
	}
	
	public void reset(boolean enabled) 
	{
		setDampFactor(.1);
		setDeltaKE(0.0);
		setMode(ThermostatFactory.WALLTHERMOSTAT);
		setEnabled(enabled);
	}
	
	public double calcModifier(EventInfo event, Particle p)
	{
		double modifier = 1.0;
		
		if(this.isActive())
		{
			modifier = getThermostatController().calculateAdjustment(this, event, p);
		}
		
		return modifier;		
	}

	public boolean isActive()
	{
            
		return this.isEnabled && (Math.abs(this.getDeltaKE()) > THERMOSTATEPSILON) ? true : false;
	}
			
	public void adjustKE(double KE) {
		this.setDeltaKE(this.getDeltaKE() + KE);
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled)
	{	
//		try {main.getPredictor().predictBluePercent();}
//		catch (Exception e){}
		
		this.isEnabled = isEnabled;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}


	public void setThermostatController(ThermostatController thermostatController) {
		this.thermostatController = thermostatController;
	}

	ThermostatController getThermostatController() {
		return thermostatController;
	}

	/**
	 * @param deltaKE the deltaKE to set
	 */
	public void setDeltaKE(double deltaKE) {
		this.deltaKE = deltaKE;
	}

	/**
	 * @return the deltaKE
	 */
	public double getDeltaKE() {
		return deltaKE;
	}

	/**
	 * @param dampFactor the dampFactor to set
	 */
	public void setDampFactor(double dampFactor) {
		this.dampFactor = dampFactor;
	}

	/**
	 * @return the dampFactor
	 */
	public double getDampFactor() {
		return dampFactor;
	}
	
	
}
