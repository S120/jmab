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
package jmab.report;

import java.util.Map;
import java.util.TreeMap;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroExpectationsErrorComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {
	
	private int populationId;
	private Integer expectationKey;
	private Integer nbVariables; //number of different variables employed in forecasting. If expectations 
	//Should be equal to the value of the corresponding field in the variable expectation object.
	private boolean percentage; // if true computed in %, else in levels


	/**
	 * @return the expectationKey
	 */
	public Integer getExpectationKey() {
		return expectationKey;
	}

	/**
	 * @param expectationKey the expectationKey to set
	 */
	public void setExpectationKey(Integer expectationKey) {
		this.expectationKey = expectationKey;
	}

	/**
	 * @return the nbVariables
	 */
	public Integer getNbVariables() {
		return nbVariables;
	}

	/**
	 * @param nbVariables the nbVariables to set
	 */
	public void setNbVariables(Integer nbVariables) {
		this.nbVariables = nbVariables;
	}

	/**
	 * @return the percentage
	 */
	public boolean isPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(boolean percentage) {
		this.percentage = percentage;
	}

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long,Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		
		for (Agent i:pop.getAgents()){
			MacroAgent agent= (MacroAgent) i;
			if(!agent.isDead()){
				double realized=agent.getExpectation(expectationKey).getPassedValues()[0][0];
				double expected=agent.getExpectation(expectationKey).getPassedValues()[0][nbVariables];
				if (percentage){
					result.put(agent.getAgentId(), (realized-expected)/realized);

				}
				else{
					result.put(agent.getAgentId(), (realized-expected));
				}
			}
			else{
				result.put(agent.getAgentId(), Double.NaN);

			}
			
		}
		return result;
	}

	/**
	 * @return the populationId
	 */
	public int getPopulationId() {
		return populationId;
	}

	/**
	 * @param populationId the populationId to set
	 */
	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}

	
	
}
