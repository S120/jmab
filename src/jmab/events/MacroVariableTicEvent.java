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

import jmab.report.VariableComputer;
import jmab.simulations.MacroSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 * An event fired in order to compute the value of a specific macro variable to be reported.
 */
@SuppressWarnings("serial")
public class MacroVariableTicEvent extends MacroTicEvent {

	/**
	 * @param computer the specific {@link VariableComputer} to be used in order to compute the value of
	 * the variable to be reported.
	 * @param variableName the name of the variable to be reported.
	 * VariableId the Id of the variable to be computed.
	 */
	private VariableComputer computer;
	private String variableName;
	private int VariableId;

	/**
	 * @param tic
	 */
	public MacroVariableTicEvent() {
		super();
	}
	
	public double getValue(MacroSimulation sim){
		return computer.computeVariable(sim);
	}

	/**
	 * @return the computer
	 */
	public VariableComputer getComputer() {
		return computer;
	}

	/**
	 * @param computer the computer to set
	 */
	public void setComputer(VariableComputer computer) {
		this.computer = computer;
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
	 * @return the variableId
	 */
	public int getVariableId() {
		return VariableId;
	}

	/**
	 * @param variableId the variableId to set
	 */
	public void setVariableId(int variableId) {
		VariableId = variableId;
	}
	

}