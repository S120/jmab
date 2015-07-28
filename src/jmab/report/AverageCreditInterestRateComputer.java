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
 * This computer computes the (weighted) average interest rate on newly created loans.
 */
public class AverageCreditInterestRateComputer implements VariableComputer {
	private int banksId;
	private int stockId;

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population banks=macroPop.getPopulation(banksId);
		double avInterests=0;
		double newLoans=0;
		for (Agent i:banks.getAgents()){
			MacroAgent bank= (MacroAgent) i;
			List<Item> stocks=bank.getItemsStockMatrix(true, stockId);
			for(Item h:stocks){
				Loan loan= (Loan) h;
				if (loan.getAge()==0){
					newLoans+=loan.getValue();	
				}		
			}
		}
		for (Agent i:banks.getAgents()){
			MacroAgent bank= (MacroAgent) i;
			List<Item> loans=bank.getItemsStockMatrix(true, stockId);
			for(Item h:loans){
				Loan loan= (Loan) h;
				if (loan.getAge()==0){
					avInterests+=(loan.getValue()/newLoans)*loan.getInterestRate();	
				}		
			}
		}
		return avInterests;
	}

	/**
	 * @return the banksId
	 */
	public int getBanksId() {
		return banksId;
	}

	/**
	 * @param banksId the banksId to set
	 */
	public void setBanksId(int banksId) {
		this.banksId = banksId;
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


	
	
	

}
