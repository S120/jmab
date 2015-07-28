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

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This strategy computes R&D investment as a share of past sales.
 */
@SuppressWarnings("serial")
public class RandDInvestmentSharePastSales extends AbstractStrategy implements
		RandDInvestment {
	
	double researchShare;
	int pastNominalSalesId; //this should be set to equal to the key contained in staticValues corresponding to
	//PASTNOMINALSALES
	

	/* (non-Javadoc)
	 * @see jmab.strategies.RandDInvestment#computeRandDInvestment()
	 */
	@Override
	public double computeRandDInvestment() {
		MacroAgent innovator= (MacroAgent)this.getAgent();
		return innovator.getPassedValue(pastNominalSalesId, 1)*researchShare;
	}


	/**
	 * @return the researchShare
	 */
	public double getResearchShare() {
		return researchShare;
	}


	/**
	 * @param researchShare the researchShare to set
	 */
	public void setResearchShare(double researchShare) {
		this.researchShare = researchShare;
	}


	/**
	 * @return the pastSalesId
	 */
	public int getPastNominalSalesId() {
		return pastNominalSalesId;
	}


	/**
	 * @param pastNominalSalesId the pastNominalSalesId to set
	 */
	public void setPastNominalSalesId(int pastNominalSalesId) {
		this.pastNominalSalesId = pastNominalSalesId;
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [researchShare][pastNominalSalesId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.putDouble(researchShare);
		buf.putInt(pastNominalSalesId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [researchShare][pastNominalSalesId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.researchShare = buf.getDouble();
		this.pastNominalSalesId = buf.getInt();
	}

}
