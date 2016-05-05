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
public class MicroLaggedValueComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {

	private int populationId;
	private int lagId;
	private int lagNb;
	
	/**
	 * @return the banksId
	 */
	public int getPopulationId() {
		return populationId;
	}

	/**
	 * @param populationId the banksId to set
	 */
	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}
	
	public int getLagId() {
		return lagId;
	}

	public void setLagId(int lagId) {
		this.lagId = lagId;
	}
	
	public int getLagNb() {
		return lagNb;
	}

	public void setLagNb(int lagNb) {
		this.lagNb = lagNb;
	}

	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			MacroAgent agent= (MacroAgent ) i;
			if (!agent.isDead()){
				result.put(agent.getAgentId(), agent.getPassedValue(lagId, lagNb));
			}
			else{
				result.put(agent.getAgentId(), Double.NaN);
			}
		}
		return result;
	}


}
