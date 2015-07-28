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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jmab.agents.MacroAgent;
import jmab.agents.SimpleAbstractAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class AggregateSectorBalanceSheetComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {
	
	private int populationId;
	
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

	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		List<String> bsNames = ((SimpleAbstractAgent)pop.getAgentList().get(0)).getStocksNames();
		double[][] aggBs = new double[2][bsNames.size()];
		for (Agent a:pop.getAgents()){
			MacroAgent agent=(MacroAgent) a;
			double[][] bs = agent.getNumericBalanceSheet();
			for (int i = 0 ; i<bsNames.size() ; i++){
				aggBs[0][i]+=bs[0][i];
				aggBs[1][i]+=bs[1][i];
			}
		}
		for (int i = 0 ; i<bsNames.size() ; i++){
			result.put((long) i, aggBs[0][i]-aggBs[1][i]);
		}
		return result;
	}
}
