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

import jmab.agents.GoodSupplier;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroFirmsProfitsComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {
	
	private int populationId;
	private int pastProfitId;

	/**
	 * @return the pastProfitId
	 */
	public int getPastProfitId() {
		return pastProfitId;
	}

	/**
	 * @param pastProfitId the pastProfitId to set
	 */
	public void setPastProfitId(int pastProfitId) {
		this.pastProfitId = pastProfitId;
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
			GoodSupplier agent= (GoodSupplier) i;
			if(!agent.isDead()){
				result.put(agent.getAgentId(), agent.getPassedValue(pastProfitId, 0));
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
