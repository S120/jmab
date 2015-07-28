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

import jmab.agents.AbstractBank;
import jmab.agents.MacroAgent;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 *According to this strategy the bank is willing to supply credit to a specific borrower only up to a certain share (
 *(maxShareTotalLoans parameter) of its total exposure. That is, new loans are constrained to be nonnegative and are 
 *equal to maxShareTotalLoans*total loans by the banks (including past loans not completely repaid)-old loans granted to the borrower 
 *and not yet completely repaid.
 */
@SuppressWarnings("serial")
public class MaxExposureStrategy extends AbstractStrategy implements
		SpecificCreditSupplyStrategy {
	
	double maxShareTotalLoans;
	int loansId;

	/**
	 * @return the maxShareTotalLoans
	 */
	public double getMaxShareTotalLoans() {
		return maxShareTotalLoans;
	}

	/**
	 * @param maxShareTotalLoans the maxShareTotalLoans to set
	 */
	public void setMaxShareTotalLoans(double maxShareTotalLoans) {
		this.maxShareTotalLoans = maxShareTotalLoans;
	}

	/**
	 * @return the loansId
	 */
	public int getLoansId() {
		return loansId;
	}

	/**
	 * @param loansId the loansId to set
	 */
	public void setLoansId(int loansId) {
		this.loansId = loansId;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SpecificCreditSupplyStrategy#computeSpecificSupply(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double computeSpecificSupply(MacroAgent creditDemander,
			double required) {
		AbstractBank creditSupplier= (AbstractBank) this.getAgent();
		double totalExposure=0;
		double exposureToCreditDemander=0;
		for (Item loan:creditSupplier.getItemsStockMatrix(true, loansId)){
			if (loan.getAge()!=0){
			totalExposure+=loan.getValue();
			}
			if (loan.getLiabilityHolder().getAgentId()==creditDemander.getAgentId()){
				exposureToCreditDemander+=loan.getValue();
			}
		}
		return Math.max(0,(totalExposure*maxShareTotalLoans)-exposureToCreditDemander);
		
		
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [maxShareTotalLoans][loansId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.putDouble(maxShareTotalLoans);
		buf.putInt(loansId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [maxShareTotalLoans][loansId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.maxShareTotalLoans = buf.getDouble();
		this.loansId = buf.getInt();
	}

}
