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

import jmab.agents.TaxPayer;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This computer computes consumtpion firms' labor productivity as the ratio between production and workers employed.
 */
public class MicroTaxPayerComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {
	
	private int agentId;
	private int lagTaxId;

	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(agentId);
		TreeMap<Long,Double> result = new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			TaxPayer taxPayer = (TaxPayer) i;
			if (!taxPayer.isDead()){
				result.put(taxPayer.getAgentId(), taxPayer.getPassedValue(lagTaxId, 0));
			}
			else{
				result.put(taxPayer.getAgentId(), Double.NaN);
			}
		}
		return result;
	}

	/**
	 * @return the consumptionFirmsId
	 */
	public int getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId the consumptionFirmsId to set
	 */
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	
	
}
