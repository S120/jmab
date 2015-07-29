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

import java.util.LinkedHashMap;
import java.util.List;

import jmab.agents.AbstractFirm;
import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.goods.AbstractGood;
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
public class NominalGDPComputer implements VariableComputer {
	
	private int governmentPopulationId; // the id of the government
	private LinkedHashMap<Integer,Integer> goodPassedValueMap;
	private int[] gdpPopulationIds;//These are all the populations ids of agents that have either bought or produced goods entering in GDP
	private int[] gdpGoodsIds;//These are all the stock matrix ids of goods that enter in GDP
	private int[] gdpGoodsAges;//These are all age limit of goods that enter in GDP
	private int priceIndexProducerId;//This is the population id of agents that produce the goods entering in the CPI
	private int priceGoodId;//This is the stock matrix if of the good entering in the CPI
	private int realSaleId;//This is the id of the lagged value of real sales

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		// calculate average price change
		Population pop = macroPop.getPopulation(priceIndexProducerId);
		double totalSales=0;
		double averagePrice=0;
		for (Agent a:pop.getAgents()){
			AbstractFirm firm= (AbstractFirm) a;
			totalSales+=firm.getPassedValue(realSaleId, 0);
			AbstractGood good = (AbstractGood)firm.getItemStockMatrix(true, priceGoodId);
			averagePrice+=good.getPrice()*firm.getPassedValue(realSaleId,0);
		}
		averagePrice=averagePrice/totalSales;

		// calculate nominal GDP
		double gdpGoodsComponent=0;
		double pastInventories=0;
		double publicServantsWages=0;
		double nominalGDP=0;
		for(int popId:gdpPopulationIds){
			pop = macroPop.getPopulation(popId);
			//Population pop = macroPop.getPopulation(i); GET RID OF THIS?
			for(Agent j:pop.getAgents()){
				MacroAgent agent=(MacroAgent) j;
				for(int k=0; k<gdpGoodsIds.length;k++){
					List<Item> items= agent.getItemsStockMatrix(true, gdpGoodsIds[k]);
					for(Item item:items){
						if(item.getAge()<gdpGoodsAges[k]){
							gdpGoodsComponent+=item.getValue();
						}
						AbstractGood good = (AbstractGood)item;
						if(good.getProducer().getAgentId()==agent.getAgentId()){
							int passedValueId = goodPassedValueMap.get(good.getSMId());
							pastInventories+=agent.getPassedValue(passedValueId, 1);
						}
					}
				}
			}
		}
		gdpGoodsComponent-=pastInventories;
		if(governmentPopulationId!=-1){
			LaborDemander govt = (LaborDemander)macroPop.getPopulation(governmentPopulationId).getAgentList().get(0);
			for(MacroAgent agent:govt.getEmployees()){
				LaborSupplier publicServant = (LaborSupplier)agent;
				publicServantsWages+=publicServant.getWage();
			}
			nominalGDP = gdpGoodsComponent+publicServantsWages;
		}else
			nominalGDP = gdpGoodsComponent;
		return nominalGDP;
		
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
	public void setGoodPassedValueMap(
			LinkedHashMap<Integer, Integer> goodPassedValueMap) {
		this.goodPassedValueMap = goodPassedValueMap;
	}

	/**
	 * @return the gdpPopulationIds
	 */
	public int[] getGdpPopulationIds() {
		return gdpPopulationIds;
	}

	/**
	 * @param gdpPopulationIds the gdpPopulationIds to set
	 */
	public void setGdpPopulationIds(int[] gdpPopulationIds) {
		this.gdpPopulationIds = gdpPopulationIds;
	}

	/**
	 * @return the gdpGoodsIds
	 */
	public int[] getGdpGoodsIds() {
		return gdpGoodsIds;
	}

	/**
	 * @param gdpGoodsIds the gdpGoodsIds to set
	 */
	public void setGdpGoodsIds(int[] gdpGoodsIds) {
		this.gdpGoodsIds = gdpGoodsIds;
	}

	/**
	 * @return the gdpGoodsAges
	 */
	public int[] getGdpGoodsAges() {
		return gdpGoodsAges;
	}

	/**
	 * @param gdpGoodsAges the gdpGoodsAges to set
	 */
	public void setGdpGoodsAges(int[] gdpGoodsAges) {
		this.gdpGoodsAges = gdpGoodsAges;
	}

	/**
	 * @return the priceIndexProducerId
	 */
	public int getPriceIndexProducerId() {
		return priceIndexProducerId;
	}

	/**
	 * @param priceIndexProducerId the priceIndexProducerId to set
	 */
	public void setPriceIndexProducerId(int priceIndexProducerId) {
		this.priceIndexProducerId = priceIndexProducerId;
	}

	/**
	 * @return the priceGoodId
	 */
	public int getPriceGoodId() {
		return priceGoodId;
	}

	/**
	 * @param priceGoodId the priceGoodId to set
	 */
	public void setPriceGoodId(int priceGoodId) {
		this.priceGoodId = priceGoodId;
	}

	/**
	 * @return the realSaleId
	 */
	public int getRealSaleId() {
		return realSaleId;
	}

	/**
	 * @param realSaleId the realSaleId to set
	 */
	public void setRealSaleId(int realSaleId) {
		this.realSaleId = realSaleId;
	}
	
}
