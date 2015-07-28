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
import jmab.goods.CapitalGood;
import jmab.goods.ConsumptionGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This computer computes the Herfindahl-Hirschman Index for a particualr good market, based on producers' 
 * market shares calculated in terms of revenues (not units) so that it can be used also when goods are 
 * heterogeneous. First we compute total sales in the market, and then we compute the market shares for each producer.
 * 
 */
public class HerfindahlHirschmanIndexComputer implements VariableComputer {
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
				if (!agent.isDead()){
					List <Item> items= agent.getItemsStockMatrix(true, goodId);
					for (Item item:items){
						if (item.getAge()==0){
							totalSales+=item.getValue();
							}
						}
					}	
				}
			}
		}
		Population producers=macroPop.getPopulation(producersId);
		double hhi=0;
		for (Agent j:producers.getAgents()){
			double sales=0;
			for(int i=0;i<macroPop.getSize();i++){
				Population pop = macroPop.getPopulation(i);
					if (i!= producersId){
						for (Agent h:pop.getAgents()){
							MacroAgent agent=(MacroAgent)h;
							if (!agent.isDead()){
								List <Item> items= agent.getItemsStockMatrix(true, goodId);
								for (Item item:items){
									if (item.getAge()==0 & item instanceof ConsumptionGood){
										ConsumptionGood good = (ConsumptionGood) item;
										if (good.getProducer()==j){
											sales+=good.getValue();
										}
									}
									else if (item.getAge()==0 & item instanceof CapitalGood){
										CapitalGood capital=(CapitalGood) item;
										if (capital.getProducer()==j){
											sales+=capital.getValue();
										}
									}
								}
							}
							
						}
					}	
				}
			hhi+=Math.pow((sales/totalSales),2);
		}
		return hhi;
	}

}
