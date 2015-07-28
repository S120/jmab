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
import java.util.List;

import jmab.agents.InvestmentAgent;
import jmab.agents.MacroAgent;
import jmab.goods.CapitalGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class RealCapitalDemandSimple extends AbstractStrategy implements
		RealCapitalDemandStrategy {

	int productionStockId;
	int capitalGoodId;

	/* (non-Javadoc)
	 * @see jmab.strategies.RealCapitalDemandStrategy#computeRealCapitalDemand(jmab.agents.MacroAgent)
	 */
	@Override
	public double computeRealCapitalDemand(MacroAgent seller) {
		InvestmentAgent investor= (InvestmentAgent) this.getAgent();
		CapitalGood newCapital= (CapitalGood)seller.getItemStockMatrix(true, productionStockId);
		List<Item> capitalStock=investor.getItemsStockMatrix(true, capitalGoodId);
		double currentCapacity=0;
		double residualCapacity=0;
		for (Item i:capitalStock){
			CapitalGood oldCapital=(CapitalGood) i;
			currentCapacity+=oldCapital.getProductivity()*oldCapital.getQuantity();
			if (oldCapital.getCapitalDuration()- oldCapital.getAge()>1){ //i.e. the capital is still working in the next period
					residualCapacity+=oldCapital.getProductivity()*oldCapital.getQuantity();
				}
			}
		double desiredCapacity= (1+investor.getDesiredCapacityGrowth())*currentCapacity;
		int newCapitalRealDemand=(int) Math.ceil((desiredCapacity-residualCapacity)/newCapital.getProductivity());
		return newCapitalRealDemand;
	}
	
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
	 * @return the capitalGoodId
	 */
	public int getCapitalGoodId() {
		return capitalGoodId;
	}



	/**
	 * @param capitalGoodId the capitalGoodId to set
	 */
	public void setCapitalGoodId(int capitalGoodId) {
		this.capitalGoodId = capitalGoodId;
	}



	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [productionStockId][capitalGoodId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putInt(this.productionStockId);
		buf.putInt(this.capitalGoodId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [productionStockId][capitalGoodId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.productionStockId = buf.getInt();
		this.capitalGoodId = buf.getInt();
	}
	
	

}
