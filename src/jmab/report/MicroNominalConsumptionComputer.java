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
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import jmab.stockmatrix.ConsumptionGood;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroNominalConsumptionComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {
	private int populationId;
	private int consGoodId;
	
	

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
	 * @return the loansId
	 */
	public int getConsGoodId() {
		return consGoodId;
	}



	/**
	 * @param loansId the loansId to set
	 */
	public void setConsGoodId(int consGoodId) {
		this.consGoodId = consGoodId;
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
			MacroAgent agent=(MacroAgent) i;
			if (!agent.isDead()){
			List<Item>loans=agent.getItemsStockMatrix(true, consGoodId);
			double consumption=0;
			for (Item j:loans){
				ConsumptionGood good= (ConsumptionGood)j;
				consumption+=good.getValue();
				}
			result.put(agent.getAgentId(), consumption);
			}
			else{
				result.put(agent.getAgentId(), Double.NaN);
			}
		}
		return result;
	}
}
