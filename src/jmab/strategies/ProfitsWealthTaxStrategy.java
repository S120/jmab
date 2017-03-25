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
import jmab.agents.ProfitsTaxPayer;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class ProfitsWealthTaxStrategy extends AbstractStrategy implements TaxPayerStrategy {

	private double wealthTaxRate;
	private double profitTaxRate;
	private double maxProfitTaxRate;
	private double minProfitTaxRate;
	private int depositId;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.TaxPayerStrategy#computeTaxes()
	 */
	@Override
	public double computeTaxes() {
		ProfitsTaxPayer taxPayer = (ProfitsTaxPayer)this.getAgent();
		double profits = taxPayer.getPreTaxProfits();
		double wealth=taxPayer.getNetWealth();
		if (taxPayer instanceof AbstractBank){ 
			return Math.max(wealthTaxRate*wealth+profitTaxRate*profits, 0);
		}
		else{
			if (wealthTaxRate*wealth+profitTaxRate*profits>taxPayer.getItemStockMatrix(true, depositId).getValue()){
				return 0;
			}
			else{
				return Math.max(wealthTaxRate*wealth+profitTaxRate*profits, 0);
			}
		}
		
	}

	/**
	 * @return the wealthTaxRate
	 */
	public double getWealthTaxRate() {
		return wealthTaxRate;
	}

	/**
	 * @param wealthTaxRate the wealthTaxRate to set
	 */
	public void setWealthTaxRate(double wealthTaxRate) {
		this.wealthTaxRate = wealthTaxRate;
	}

	public double getMaxProfitTaxRate() {
		return maxProfitTaxRate;
	}

	public void setMaxProfitTaxRate(double maxProfitTaxRate) {
		this.maxProfitTaxRate = maxProfitTaxRate;
	}

	public double getMinProfitTaxRate() {
		return minProfitTaxRate;
	}

	public void setMinProfitTaxRate(double minProfitTaxRate) {
		this.minProfitTaxRate = minProfitTaxRate;
	}

	/**
	 * @return the profitTaxRate
	 */
	public double getProfitTaxRate() {
		return profitTaxRate;
	}

	/**
	 * @param profitTaxRate the profitTaxRate to set
	 */
	public void setProfitTaxRate(double profitTaxRate) {
		this.profitTaxRate = profitTaxRate;
	}

	/**
	 * @return the depositId
	 */
	public int getDepositId() {
		return depositId;
	}

	/**
	 * @param depositId the depositId to set
	 */
	public void setDepositId(int depositId) {
		this.depositId = depositId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [wealthTaxRate][profitTaxRate][depositId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.putDouble(wealthTaxRate);
		buf.putDouble(profitTaxRate);
		buf.putInt(depositId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [wealthTaxRate][profitTaxRate][depositId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.wealthTaxRate = buf.getDouble();
		this.profitTaxRate = buf.getDouble();
		this.depositId = buf.getInt();
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.TaxPayerStrategy#updateRates(double)
	 */
	@Override
	public void updateRates(double multiplier) {
		this.wealthTaxRate = this.wealthTaxRate*multiplier;
		if (this.profitTaxRate >= this.maxProfitTaxRate) {
			this.profitTaxRate = this.maxProfitTaxRate;
					}
		else if (this.profitTaxRate <= minProfitTaxRate){
			this.profitTaxRate = this.minProfitTaxRate;
		}
		else {
			this.profitTaxRate = this.profitTaxRate*multiplier;
		}
	}
	
	
}
