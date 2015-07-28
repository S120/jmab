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
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroLenderMaxExposureComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {
	
	private int populationId;	
	private int loansId;

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
	public int getLoansId() {
		return loansId;
	}

	/**
	 * @param loansId the loansId to set
	 */
	public void setLoansId(int loansId) {
		this.loansId = loansId;
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
			TreeMap<Long,Double> loansPerClient = new TreeMap<Long,Double>();
			double totalLoan=0;
			MacroAgent agent=(MacroAgent) i;
			if (!agent.isDead()){
				List<Item>loans=agent.getItemsStockMatrix(true, loansId);
				for (Item loan:loans){
					Double clientLoan = loansPerClient.remove(loan.getLiabilityHolder().getAgentId());
					if(clientLoan==null)
						clientLoan=loan.getValue();
					else
						clientLoan+=loan.getValue();
					loansPerClient.put(loan.getLiabilityHolder().getAgentId(), clientLoan);
					totalLoan+=loan.getValue();
				}
				double maxLoan = Double.NEGATIVE_INFINITY;
				for(Long key:loansPerClient.keySet()){
					double loan = loansPerClient.get(key);
					if(loan>maxLoan)
						maxLoan=loan;
				}
				result.put(agent.getAgentId(), maxLoan/totalLoan);
			}
			else{
				result.put(agent.getAgentId(), Double.NaN);
			}
			
		}
		return result;
	}
}
