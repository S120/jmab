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
package modellone.strategies;

import java.nio.ByteBuffer;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.strategies.InterestRateStrategy;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class FirmInterestRateStrategy extends AbstractStrategy implements
		InterestRateStrategy {

	private double riskPremium;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.InterestRateStrategy#computeInterestRate(jmab.agents.MacroAgent, double, int)
	 */
	@Override
	public double computeInterestRate(MacroAgent creditDemander, double amount,
			int length) {
		double[][] bs = creditDemander.getNumericBalanceSheet();
		double liability=0;
		double assets=0;
		for(int i=0;i<bs[0].length-1;i++){
			assets+=bs[0][i];
			liability+=bs[1][i];
		}
		return Math.pow(riskPremium, liability/assets)/100;
	}

	/**
	 * @return the riskPremium
	 */
	public double getRiskPremium() {
		return riskPremium;
	}

	/**
	 * @param riskPremium the riskPremium to set
	 */
	public void setRiskPremium(double riskPremium) {
		this.riskPremium = riskPremium;
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [riskPremium]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putDouble(this.riskPremium);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [riskPremium]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.riskPremium = buf.getDouble();
	}
	
}
