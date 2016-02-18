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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import jmab.stockmatrix.AbstractGood;
import jmab.stockmatrix.Item;
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
public class RealOutputComputer implements MacroVariableComputer {
	
	private int [] componentsGDPIds; // the Ids of the good and services which enter in the GDP
	private int [] householdsPopIds; //the different types of households
	private int [] goodsSMIds; //the firm producing the goods and services
	private int governmentPopulationId; // the id of the government
	private LinkedHashMap<Integer,Integer> goodPassedValueMap;
	

	/**
	 * @return the componentsGDPIds
	 */
	public int[] getComponentsGDPIds() {
		return componentsGDPIds;
	}

	/**
	 * @param componentsGDPIds the componentsGDPIds to set
	 */
	public void setComponentsGDPIds(int[] componentsGDPIds) {
		this.componentsGDPIds = componentsGDPIds;
	}

	/**
	 * @return the householdsPopIds
	 */
	public int[] getHouseholdsPopIds() {
		return householdsPopIds;
	}

	/**
	 * @param householdsPopIds the householdsPopIds to set
	 */
	public void setHouseholdsPopIds(int[] householdsPopIds) {
		this.householdsPopIds = householdsPopIds;
	}
	

	/**
	 * @return the goodsSMIds
	 */
	public int[] getGoodsSMIds() {
		return goodsSMIds;
	}

	/**
	 * @param goodsSMIds the goodsSMIds to set
	 */
	public void setGoodsSMIds(int[] goodsSMIds) {
		this.goodsSMIds = goodsSMIds;
	}

	/**
	 * @return the governmentPopulationId
	 */
	public int getGovernmentPopulationId() {
		return governmentPopulationId;
	}

	/**
	 * @param governmentPopulationId the governmentPopulationId to set
	 */
	public void setGovernmentPopulationId(int governmentPopulationId) {
		this.governmentPopulationId = governmentPopulationId;
	}

	/**
	 * @return the goodPassedValueMap
	 */
	public LinkedHashMap<Integer, Integer> getGoodPassedValueMap() {
		return goodPassedValueMap;
	}

	/**
	 * @param goodPassedValueMap the goodPassedValueMap to set
	 */
	public void setGoodPassedValueMap(LinkedHashMap<Integer, Integer> goodPassedValueMap) {
		this.goodPassedValueMap = goodPassedValueMap;
	}

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		double gdpGoodsComponent=0;
		double pastInventories=0;
		double publicServantsWages=0;
		for(int i=0;i<macroPop.getSize();i++){
			Population pop = macroPop.getPopulation(i);
			for(Agent j:pop.getAgents()){
				MacroAgent agent=(MacroAgent) j;
				if (!agent.isDead()){
				for (int h=0; h<componentsGDPIds.length; h++){
					List <Item> items= agent.getItemsStockMatrix(true, componentsGDPIds[h]);
					for (Item item:items){
						if (item.getAge()==0){
							gdpGoodsComponent+=item.getValue();
						}
						if (Arrays.asList(goodsSMIds).contains(item.getSMId())){
							AbstractGood good = (AbstractGood)item;
							if(good.getProducer().getAgentId()==agent.getAgentId()){
								int passedValueId = goodPassedValueMap.get(h);
								pastInventories+=agent.getPassedValue(passedValueId, 1);
							}
						}
					}
				}
				}
			}
		}
		gdpGoodsComponent-=pastInventories;
		LaborDemander govt = (LaborDemander)macroPop.getPopulation(governmentPopulationId).getAgentList().get(0);
		for(MacroAgent agent:govt.getEmployees()){
			LaborSupplier publicServant = (LaborSupplier)agent;
			publicServantsWages+=publicServant.getWage();
		}
		return gdpGoodsComponent+publicServantsWages;
	}
}
