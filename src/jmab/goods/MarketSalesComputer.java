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
package jmab.goods;

import java.util.List;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.report.VariableComputer;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MarketSalesComputer implements VariableComputer {
	private int goodId; 
	private int producersId;

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		double totalSales=0;
		for(int i=0;i<macroPop.getSize();i++){
			Population pop = macroPop.getPopulation(i);
			if (i!= producersId){
			for(Agent j:pop.getAgents()){
				MacroAgent agent=(MacroAgent)j;
				List <Item> items= agent.getItemsStockMatrix(true, goodId);
				for (Item item:items){
					if (item.getAge()==0){
						totalSales+=item.getValue();
						}
					}	
				}
			}
		}
		return totalSales;
	}

}
