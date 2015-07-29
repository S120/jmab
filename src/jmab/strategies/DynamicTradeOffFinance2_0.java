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

import jmab.agents.FinanceAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This strategy implements the dynamic trad-off theroy of firms' capital structure. 
 * Firms base their credit demand on a target leverage which is revised in every period according to an
 * adaptive rule: if past profits/past sales> than a certain threshold they increase the leverage target and viceversa.
 * Notice that profits are computed net of interests so that the debt burden is already included.
 */
@SuppressWarnings("serial")
public class DynamicTradeOffFinance2_0 extends DynamicTradeOffFinance implements
		FinanceStrategy {
	
	private int pastSalesId;
	protected AbstractDelegatedDistribution distribution;
	
	@Override
	public void updateLeverageTarget (){
		FinanceAgent borrower= (FinanceAgent)this.getAgent();
		double pastProfits=borrower.getPassedValue(pastProfitId, 1); 
		double pastSales=borrower.getPassedValue(pastSalesId, 1);
		if (pastProfits/pastSales>threshold){
			leverageTarget+=adaptiveParameter*leverageTarget*distribution.nextDouble();
		}
		//else if (returnOnLoans==averageInterestRate && borrower.getReferenceVariableForFinance()<thresholdRefVariable){
			//leverageTarget=leverageTarget;
		//}
		else {
			leverageTarget-=adaptiveParameter*leverageTarget*distribution.nextDouble();
		}
	}
	
	/**
	 * @return the pastSalesId
	 */
	public int getPastSalesId() {
		return pastSalesId;
	}

	/**
	 * @param pastSalesId the pastSalesId to set
	 */
	public void setPastSalesId(int pastSalesId) {
		this.pastSalesId = pastSalesId;
	}
	
	/**
	 * @return the distribution
	 */
	public AbstractDelegatedDistribution getDistribution() {
		return distribution;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribution(AbstractDelegatedDistribution distribution) {
		this.distribution = distribution;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][leverageTarget][loansId][pastProfitId][pastSalesId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(36);
		buf.putDouble(threshold);
		buf.putDouble(adaptiveParameter);
		buf.putDouble(this.leverageTarget);
		buf.putInt(loansId);
		buf.putInt(pastProfitId);
		buf.putInt(pastSalesId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][leverageTarget][loansId][pastProfitId][pastSalesId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.leverageTarget = buf.getDouble();
		this.loansId = buf.getInt();
		this.pastProfitId = buf.getInt();
		this.pastSalesId = buf.getInt();
	}
	
}
