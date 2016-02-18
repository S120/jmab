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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroDegreeDistributionBipartiteComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {
	private int upperPopulationId; //e.g. banks, firms, housholds etc.
	private int stockId; //e.g. loansId or depositId
	private boolean asset; //e.g. if populationId refers to banks: true (asset) if loans, false (liability) if deposits
	




	/**
	 * @return the upperPopulationId
	 */
	public int getUpperPopulationId() {
		return upperPopulationId;
	}


	/**
	 * @param upperPopulationId the upperPopulationId to set
	 */
	public void setUpperPopulationId(int upperPopulationId) {
		this.upperPopulationId = upperPopulationId;
	}


	/**
	 * @return the stockId
	 */
	public int getStockId() {
		return stockId;
	}


	/**
	 * @param stockId the stockId to set
	 */
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}


	/**
	 * @return the asset
	 */
	public boolean isAsset() {
		return asset;
	}


	/**
	 * @param asset the asset to set
	 */
	public void setAsset(boolean asset) {
		this.asset = asset;
	}


	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(upperPopulationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			MacroAgent agent=(MacroAgent) i;
			List<Item>stocks=agent.getItemsStockMatrix(asset, stockId);
			HashSet<Long> linkedAgents= new HashSet <Long>();
			for (Item j:stocks){
				if (asset){
					linkedAgents.add(j.getLiabilityHolder().getAgentId());
				}
				else{
					linkedAgents.add(j.getAssetHolder().getAgentId());
				}
			
			}
			double agentDegree=linkedAgents.size();
			result.put(agent.getAgentId(), agentDegree);
		}
		return result;
	}

}
