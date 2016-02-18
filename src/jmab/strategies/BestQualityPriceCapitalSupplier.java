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
package jmab.strategies;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jmab.agents.AbstractFirm;
import jmab.agents.MacroAgent;
import jmab.agents.SimpleAbstractAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.CapitalGood;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class BestQualityPriceCapitalSupplier extends AbstractStrategy implements
		SelectSellerStrategy {
	
	int productionStockId; //this should be set through the config file equal to the corresponding StaticValue key
	int wagesExpectationsId; // this should be set through the config file equal to key of Wages in StaticValue

	
	/**
	 * @return the productionStockId
	 */
	public int getProductionStockId() {
		return productionStockId;
	}


	/**
	 * @param productionStockId the productionStockId to set
	 */
	public void setProductionStockId(int productionStockId) {
		this.productionStockId = productionStockId;
	}


	/**
	 * @return the wagesExpectationsId
	 */
	public int getWagesExpectationsId() {
		return wagesExpectationsId;
	}


	/**
	 * @param wagesExpectationsId the wagesExpectationsId to set
	 */
	public void setWagesExpectationsId(int wagesExpectationsId) {
		this.wagesExpectationsId = wagesExpectationsId;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.SelectSellerStrategy#selectGoodSupplier(java.util.ArrayList, double, boolean)
	 */
	@Override
	public MacroAgent selectGoodSupplier(ArrayList<Agent> sellers,
			double demand, boolean real) {
		double maxAttractiveness=Double.NEGATIVE_INFINITY;
		AbstractFirm bestSupplier=(AbstractFirm) sellers.get(0);
		SimpleAbstractAgent buyer=(SimpleAbstractAgent)this.getAgent();
		double expectedWages= buyer.getExpectation(wagesExpectationsId).getExpectation();
		for (Agent agent:sellers){
			AbstractFirm seller=(AbstractFirm) agent;
			CapitalGood capitalOffered=(CapitalGood) seller.getItemStockMatrix(true, productionStockId);
			double laborProductivity=capitalOffered.getProductivity()*capitalOffered.getCapitalLaborRatio();
			double Attractiveness=((expectedWages/laborProductivity)*capitalOffered.getCapitalDuration())/capitalOffered.getPrice();
			if (Attractiveness>maxAttractiveness){
				bestSupplier=seller;
				maxAttractiveness=Attractiveness;
			}
		}
		return bestSupplier;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.SelectSellerStrategy#selectMultipleGoodSupplier(java.util.ArrayList, double, boolean)
	 */
	@Override
	public List<MacroAgent> selectMultipleGoodSupplier(
			ArrayList<Agent> sellers, double demand, boolean real) {
		TreeMap<Double,ArrayList<MacroAgent>> orederedSellers = new TreeMap<Double,ArrayList<MacroAgent>>();
		SimpleAbstractAgent buyer=(SimpleAbstractAgent)this.getAgent();
		double expectedWages= buyer.getExpectation(wagesExpectationsId).getExpectation();
		for (Agent agent:sellers){
			AbstractFirm seller=(AbstractFirm) agent;
			CapitalGood capitalOffered=(CapitalGood) seller.getItemStockMatrix(true, productionStockId);
			double laborProductivity=capitalOffered.getProductivity()*capitalOffered.getCapitalLaborRatio();
			double attractiveness=((expectedWages/laborProductivity)*capitalOffered.getCapitalDuration())/capitalOffered.getPrice();
			if(orederedSellers.containsKey(attractiveness)){
				ArrayList<MacroAgent> list = orederedSellers.get(attractiveness);
				list.add(seller);
			}else{
				ArrayList<MacroAgent> list = new ArrayList<MacroAgent>();
				list.add(seller);
				orederedSellers.put(attractiveness, list);
			}
		}
		ArrayList<MacroAgent> result = new ArrayList<MacroAgent>();
		for (Double key:orederedSellers.descendingKeySet()){
			for(MacroAgent agent:orederedSellers.get(key)){
				result.add(agent);
			}
		}
		return result;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [productionStockId][wagesExpectationsId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putInt(this.productionStockId);
		buf.putInt(this.wagesExpectationsId);
		return buf.array();
	}

	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [productionStockId][wagesExpectationsId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.productionStockId = buf.getInt();
		this.wagesExpectationsId = buf.getInt();
	}

	
}
