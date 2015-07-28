/*
 * JMAB - Java Macroeconomic Agent Based Modeling Toolkit
 * Copyright (C) 2013 Alessandro Caiani and Antoine Godin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package jmab.events;

import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Class that extends the SimEvent class of JABM in order to be able to carry any generic variable value. Because
 * the class is generic for any variable, there's a need for a variableId so that the MacroSimEventReport knows whether
 * it needs to update its reportVariables or not. 
 */
@SuppressWarnings("serial")
public class MacroSimEvent extends SimEvent{
	
	private String variableName;
	private int time;
	private double variableValue;
	private int variableId;
	
	/**
	 * @param variableName
	 * @param time
	 * @param variableValue
	 */
	public MacroSimEvent(String variableName, int time, double variableValue, int variableId) {
		super();
		this.variableName = variableName;
		this.time = time;
		this.variableValue = variableValue;
		this.variableId = variableId;
	}
	
	
	/**
	 * 
	 */
	public MacroSimEvent() {
		super();
	}


	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}
	/**
	 * @param variableName the variableName to set
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the variableValue
	 */
	public double getVariableValue() {
		return variableValue;
	}
	/**
	 * @param variableValue the variableValue to set
	 */
	public void setVariableValue(double variableValue) {
		this.variableValue = variableValue;
	}
	/**
	 * @return the variableId
	 */
	public int getVariableId() {
		return variableId;
	}
	/**
	 * @param variableId the variableId to set
	 */
	public void setVariableId(int variableId) {
		this.variableId = variableId;
	}
	
	

}
