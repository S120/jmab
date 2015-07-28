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

import jmab.report.MicroMultipleVariablesComputer;
import jmab.simulations.MacroSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 * A method fired in order to compute a given micro variable of agents (e.g. agents' net wealth, agents'
 * prices, agents' productivity etc.) to be reported.
 */
@SuppressWarnings("serial")
public class MicroMultiVariablesTicEvent extends MacroTicEvent {

	/**
	 * @param computer the specific {@link MicroMultipleVariablesComputer} used to compute the variable to be reported.
	 * @param variableName the name of the variable to be computed.
	 * @param VariableId the Id of the variable to be computed.
	 */
	private MicroMultipleVariablesComputer computer;
	private String variableName;
	private int VariableId;

	
	public MicroMultiVariablesTicEvent() {
		super();
	}
	
	public Map<Long,Double> getValues(MacroSimulation sim){
		return computer.computeVariables(sim);
	}

	/**
	 * @return the computer
	 */
	public MicroMultipleVariablesComputer getComputer() {
		return computer;
	}

	/**
	 * @param computer the computer to set
	 */
	public void setComputer(MicroMultipleVariablesComputer computer) {
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
	
	@Override
	public void dispose(){
		computer.dispose();
	}
	
	@Override
	public void initialise(){
		computer.initialise();
	}

}