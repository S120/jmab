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

import jmab.agents.MacroAgent;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class AggregateOutstandingDebtComputer implements VariableComputer {
	
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
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		double result=0;
		for (Agent i:pop.getAgents()){
			MacroAgent agent=(MacroAgent) i;
			if (!agent.isDead()){
				List<Item>loans=agent.getItemsStockMatrix(false, loansId);
				double outstandingDebt=0;
				for (Item j:loans){
					Loan loan= (Loan)j;
					outstandingDebt+=loan.getValue();
					}
				result+=outstandingDebt;
			}	
		}
		return result;
	}

}
