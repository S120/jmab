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
 * This strategy determines the real demand for capital good once defined the desired capacity growth ratio.
 * We adopt a "payback period rule" according to which firms can decide to replace some vintages in their 
 * capital stock even before they arrive to maturity age (i.e. the age after which they can no longer be used to produce)
 * if the advantage given by the new capital good in terms of lower expected variable unit costs over the residual life of
 * a vintage of capital already in stock is higher than the price of the new capital good.
 *
 */
@SuppressWarnings("serial")
public class RealCapitalDemandWithPayBackPeriod extends AbstractStrategy
		implements RealCapitalDemandStrategy {
	
	int capitalGoodId; //this should be set with the config file equal to the corresponding SM key in StaticValues
	int wagesExpectationId; // """
	int productionStockId; //TODO why do we need two values here???
 
	
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
	 * @return the wagesExpectationId
	 */
	public int getWagesExpectationId() {
		return wagesExpectationId;
	}


	/**
	 * @param wagesExpectationId the wagesExpectationId to set
	 */
	public void setWagesExpectationId(int wagesExpectationId) {
		this.wagesExpectationId = wagesExpectationId;
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


	/* (non-Javadoc)
	 * @see jmab.strategies.InvestmentStrategy#computeRealInvestment(jmab.agents.MacroAgent)
	 */
	@Override
	public double computeRealCapitalDemand(MacroAgent seller) {
		InvestmentAgent investor= (InvestmentAgent) this.getAgent();
		CapitalGood newCapital= (CapitalGood)seller.getItemStockMatrix(true, productionStockId);
		double laborProductivityNew=newCapital.getProductivity()*newCapital.getCapitalLaborRatio();
		List<Item> capitalStock=investor.getItemsStockMatrix(true, capitalGoodId);
		double currentCapacity=0;
		double residualCapacity=0;
		for (Item i:capitalStock){
			CapitalGood oldCapital=(CapitalGood) i;
			double expectedWages=investor.getExpectation(wagesExpectationId).getExpectation();
			double laborProductivityOld=oldCapital.getProductivity()*oldCapital.getCapitalLaborRatio();
			if (oldCapital.getAge()!=-1){
			currentCapacity+=oldCapital.getProductivity()*oldCapital.getQuantity();
			}
			if (((laborProductivityOld-laborProductivityNew)/expectedWages*(oldCapital.getCapitalDuration()-oldCapital.getAge()))-newCapital.getPrice()>0){
				oldCapital.setObsolete(true);
			}else {
				if (oldCapital.getCapitalDuration()- oldCapital.getAge()>1){ //i.e. the capital is still working in the next period
					residualCapacity+=oldCapital.getProductivity()*oldCapital.getQuantity();
				}
			}
		}
		
		double desiredCapacity= (1+investor.getDesiredCapacityGrowth())*currentCapacity;
		//int newCapitalRealDemand=(int) Math.round((desiredCapacity-residualCapacity)/newCapital.getProductivity());
		//return newCapitalRealDemand;
		if (desiredCapacity>=residualCapacity){
			int newCapitalRealDemand=(int) Math.round((desiredCapacity-residualCapacity)/newCapital.getProductivity());
			return newCapitalRealDemand;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [productionStockId][capitalGoodId][wagesExpectationId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.putInt(this.productionStockId);
		buf.putInt(this.capitalGoodId);
		buf.putInt(this.wagesExpectationId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [productionStockId][capitalGoodId][wagesExpectationId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.productionStockId = buf.getInt();
		this.capitalGoodId = buf.getInt();
		this.wagesExpectationId = buf.getInt();
	}

}
