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
package modellone.report;

import java.util.Map;
import java.util.TreeMap;

import jmab.population.MacroPopulation;
import jmab.report.AbstractMicroComputer;
import jmab.report.MicroMultipleVariablesComputer;
import jmab.simulations.MacroSimulation;
import modellone.agents.CapitalFirm;
import modellone.agents.ConsumptionFirm;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroInterestToPayComputer extends AbstractMicroComputer implements
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
		TreeMap<Long,Double> result = new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			if(i instanceof CapitalFirm){
				CapitalFirm firm= (CapitalFirm) i;
				if (!firm.isDead()){
				result.put(firm.getAgentId(), firm.getDebtInterests());
				}
				else{
					result.put(firm.getAgentId(), Double.NaN);
				}
			}else if(i instanceof ConsumptionFirm){
				ConsumptionFirm firm= (ConsumptionFirm) i;
				if (!firm.isDead()){
				result.put(firm.getAgentId(), firm.getDebtInterests());
				}
				else{
					result.put(firm.getAgentId(), Double.NaN);
				}
			}
		}
		return result;
	}

}
