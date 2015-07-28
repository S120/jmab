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
import modellone.StaticValues;
import modellone.agents.Bank;
import modellone.agents.CapitalFirm;
import modellone.agents.ConsumptionFirm;
import modellone.agents.Households;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroTaxesComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {
	private int populationId;

	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			switch(populationId){
			case StaticValues.CONSUMPTIONFIRMS_ID:
				ConsumptionFirm cfirm= (ConsumptionFirm) i;
				result.put(cfirm.getAgentId(),cfirm.getPassedValue(StaticValues.LAG_TAXES, 0));
				break;
			case StaticValues.CAPITALFIRMS_ID:
				CapitalFirm kfirm= (CapitalFirm)i;
				result.put(kfirm.getAgentId(), kfirm.getPassedValue(StaticValues.LAG_TAXES, 0));
				break;
			case StaticValues.BANKS_ID:
				Bank bank=(Bank) i;
				result.put(bank.getAgentId(),bank.getPassedValue(StaticValues.LAG_TAXES, 0));
				break;
			case StaticValues.HOUSEHOLDS_ID:
				Households hh= (Households)i;
				result.put(hh.getAgentId(),hh.getPassedValue(StaticValues.LAG_TAXES, 0));	
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
