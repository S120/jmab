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

import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Class that extends the SimEvent class of JABM in order to be able to carry any generic variable value. Because
 * the class is generic for any variable, there's a need for a variableId so that the MacroSimEventReport knows whether
 * it needs to update its reportVariables or not. 
 */
@SuppressWarnings("serial")
public class MicroSimEvent extends SimEvent{
	
	private int time;
	private int variableId;
	private Map<Long,Double> varValues;
	
	
	/**
	 * 
	 */
	public MicroSimEvent() {
		super();
	}
	/**
	 * @param variableName
	 * @param time
	 * @param variableValue
	 */
	public MicroSimEvent(int time, Map<Long,Double> varValues, int variableId) {
		super();
		this.time = time;
		this.varValues = varValues;
		this.variableId = variableId;
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
	/**
	 * @return the varValues
	 */
	public Map<Long, Double> getVarValues() {
		return varValues;
	}
	/**
	 * @param varValues the varValues to set
	 */
	public void setVarValues(Map<Long, Double> varValues) {
		this.varValues = varValues;
	}

}
