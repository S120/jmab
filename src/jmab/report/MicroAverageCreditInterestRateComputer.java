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
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This computer computes the (weighted) average interest rate on newly created loans.
 */
public class MicroAverageCreditInterestRateComputer extends AbstractMicroComputer implements MicroMultipleVariablesComputer {

	private int populationId;
	private int stockId;
	private boolean demand;

	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population banks=macroPop.getPopulation(populationId);
		double avInterests=0;
		double newLoans=0;
		for (Agent i:banks.getAgents()){
			MacroAgent bank= (MacroAgent) i;
			if (bank.isDead()){
				result.put(bank.getAgentId(), Double.NaN);
			}
			else{
				List<Item> stocks;
				if(demand)
					stocks=bank.getItemsStockMatrix(false, stockId);
				else
					stocks=bank.getItemsStockMatrix(true, stockId);
				if(stocks.size()>0){
					for(Item h:stocks){
						Loan loan= (Loan) h;
						if (loan.getAge()==0){
							newLoans+=loan.getValue();
							avInterests+=loan.getValue()*loan.getInterestRate();	
						}		
					}
					result.put(bank.getAgentId(),avInterests/newLoans);
				}else{
					result.put(bank.getAgentId(),0.0);
				}
			}
		}
		return result;
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
	 * @return the demand
	 */
	public boolean isDemand() {
		return demand;
	}

	/**
	 * @param demand the demand to set
	 */
	public void setDemand(boolean demand) {
		this.demand = demand;
	}

	public int getPopulationId() {
		return populationId;
	}

	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}



}
