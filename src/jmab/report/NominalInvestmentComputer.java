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
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This computer computes nominal GDP using the expenditure approach: the values of goods and services produced and sold, plus
 * the change in inventories, plus the wages paid to public workers. Here we first calculate the value of all the goods and services 
 * produced (both sold and stored as inventories) using the stock matrix of agents. Then we subtract the past value of inventories.
 * Then we add public workers' wages.
 * 
 */
public class NominalInvestmentComputer implements VariableComputer {
	
	private int [] capitalSMIds; // the Ids of the good and services which enter in the GDP
	private int [] investorsPopIds; //the different types of households
	
	/**
	 * @return the capitalSMIds
	 */
	public int[] getCapitalSMIds() {
		return capitalSMIds;
	}

	/**
	 * @param capitalSMIds the capitalSMIds to set
	 */
	public void setCapitalSMIds(int[] capitalSMIds) {
		this.capitalSMIds = capitalSMIds;
	}

	/**
	 * @return the investorsPopIds
	 */
	public int[] getInvestorsPopIds() {
		return investorsPopIds;
	}

	/**
	 * @param investorsPopIds the investorsPopIds to set
	 */
	public void setInvestorsPopIds(int[] investorsPopIds) {
		this.investorsPopIds = investorsPopIds;
	}

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		double investments=0;
		for(int i=0;i<investorsPopIds.length;i++){
			Population pop = macroPop.getPopulation(investorsPopIds[i]);
			for(Agent j:pop.getAgents()){
				MacroAgent agent=(MacroAgent) j;
				if (!agent.isDead()){
				for (int h=0; h<capitalSMIds.length; h++){
					List <Item> items= agent.getItemsStockMatrix(true, capitalSMIds[h]);
					for (Item item:items){
						if (item.getAge()<0){
							investments+=item.getValue();
						}
					}
				}
			}
			}
		}
		return investments;
	}
}
