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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jmab.agents.GoodDemander;
import jmab.agents.GoodSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class CheapestGoodSupplier extends AbstractStrategy implements SelectSellerStrategy {

	/**
	 * 
	 */
	public CheapestGoodSupplier() {
	}

	/**
	 * @param agent
	 */
	public CheapestGoodSupplier(Agent agent) {
		super(agent);
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public CheapestGoodSupplier(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
	}

	/* (non-Javadoc)
	 * @see jmab.strategy.BuyingStrategy#selectGoodSupplier(java.util.ArrayList)
	 */
	@Override
	public MacroAgent selectGoodSupplier(ArrayList<Agent> goodSuppliers,double demand, boolean real) {
		double minPrice=Double.POSITIVE_INFINITY;
		GoodSupplier minGoodSupplier=(GoodSupplier) goodSuppliers.get(0);
		GoodDemander goodDemander = (GoodDemander) getAgent();
		for(Agent agent : goodSuppliers){
			GoodSupplier goodSupplier=(GoodSupplier)agent;
			double tempPrice=goodSupplier.getPrice(goodDemander, demand);
			if(tempPrice<minPrice){
				minPrice=tempPrice;
				minGoodSupplier=goodSupplier;
			}
		}
		return minGoodSupplier;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SelectSellerStrategy#selectMultipleGoodSupplier(java.util.ArrayList, double, boolean)
	 */
	@Override
	public List<MacroAgent> selectMultipleGoodSupplier(
			ArrayList<Agent> sellers, double demand, boolean real) {
		TreeMap<Double,ArrayList<MacroAgent>> orederedSellers = new TreeMap<Double,ArrayList<MacroAgent>>();
		GoodDemander buyer=(GoodDemander)this.getAgent();
		for (Agent agent:sellers){
			GoodSupplier seller=(GoodSupplier)agent;
			double price=seller.getPrice(buyer, demand);
			if(orederedSellers.containsKey(price)){
				ArrayList<MacroAgent> list = orederedSellers.get(price);
				list.add(seller);
			}else{
				ArrayList<MacroAgent> list = new ArrayList<MacroAgent>();
				list.add(seller);
				orederedSellers.put(price, list);
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

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1];//TODO cannot be null and probably not byte[0].
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
	}

}
