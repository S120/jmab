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

import jmab.agents.SimpleAbstractAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroFirmsLeverageComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {
	
	private int populationId;
	private int[] liabilityIds;

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long,Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			SimpleAbstractAgent agent= (SimpleAbstractAgent) i;
			if (!agent.isDead()){
			double[][] bs = agent.getNumericBalanceSheet();
			double assets=0;
			double liabilities=0;
			for(int j=0;j<bs[0].length;j++){
				assets+=bs[0][j];
			}
			for(int j=0;j<liabilityIds.length;j++){
				liabilities+=bs[1][liabilityIds[j]];
			}
			result.put(agent.getAgentId(), liabilities/assets);
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

	/**
	 * @return the liabilityIds
	 */
	public int[] getLiabilityIds() {
		return liabilityIds;
	}

	/**
	 * @param liabilityIds the liabilityIds to set
	 */
	public void setLiabilityIds(int[] liabilityIds) {
		this.liabilityIds = liabilityIds;
	}
	
}
