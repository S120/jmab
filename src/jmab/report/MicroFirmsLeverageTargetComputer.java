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
import jmab.strategies.DynamicTradeOffFinance;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroFirmsLeverageTargetComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {
	
	private int populationId;
	private int strategyId;

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
				DynamicTradeOffFinance strategy = (DynamicTradeOffFinance)agent.getStrategy(strategyId);
				result.put(agent.getAgentId(), strategy.getLeverageTarget());
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
	 * @return the strategyId
	 */
	public int getStrategyId() {
		return strategyId;
	}

	/**
	 * @param strategyId the strategyId to set
	 */
	public void setStrategyId(int strategyId) {
		this.strategyId = strategyId;
	}
	
}
