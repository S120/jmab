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

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This strategies says that borrowers first use internal resources and only when this is not sufficient 
 * to cover the expected financial requirement they seek for credit.
 */
@SuppressWarnings("serial")
public class PeckingOrderTheory extends AbstractStrategy implements
		FinanceStrategy {
	
	int[] stocksId; 
	
	/* (non-Javadoc)
	 * @see jmab.strategies.FinanceStrategy#computeCreditDemand()
	 */
	@Override
	public double computeCreditDemand(double expectedFinancialRequirement) {
		double totalLiq=0;
		MacroAgent borrower = (MacroAgent) this.getAgent();
		for(int i = 0; i < stocksId.length ; i++){
			List<Item> stocks= borrower.getItemsStockMatrix (true, stocksId[i]);
			for(Item stock:stocks){
				totalLiq+=stock.getValue();
			}
		}		
		double creditDemand= Math.max(expectedFinancialRequirement-totalLiq,0);
		return creditDemand;
	}

	/**
	 * @return the stocksId
	 */
	public int[] getStocksId() {
		return stocksId;
	}

	/**
	 * @param stocksId the stocksId to set
	 */
	public void setStocksId(int[] stocksId) {
		this.stocksId = stocksId;
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [stockSize][stocksId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(4+4*(stocksId.length));
		buf.putInt(this.stocksId.length);
		for(int id:stocksId)
			buf.putInt(id);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [stockSize][stocksId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbStocks = buf.getInt();
		stocksId=new int[nbStocks];
		for(int i = 0 ; i < nbStocks ; i++)
			stocksId[i] = buf.getInt();
	}	
}
