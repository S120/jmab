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

import jmab.agents.BaselIIIAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * The CAR of Basel III is defined as capital/weighted assets. In most simple cases capital is reserves (cash + desposits at the CB)
 * plus net wealth. Assets are instead Loans (whose weight is 100%), Mortgage Loans (weight 50%)and other assets (weight 100%) 
 * different from cash (weight 0), government bonds (weight 0). 
 */
@SuppressWarnings("serial")
public class BaselIIIReserveRequirementsBLR extends AbstractStrategy implements
		FinanceStrategy {

	private int[] depositIds; //in the simplest case cash, reserves to which we must add the net wealth
	private int[] reserveIds; //in the simplest case only loans (bonds, reserves and cash are weighted 0)
	
	/* (non-Javadoc)
	 * @see jmab.strategies.FinanceStrategy#computeCreditDemand(double)
	 */
	@Override
	public double computeCreditDemand(double expectedFinancialRequirement) {
		BaselIIIAgent bank = (BaselIIIAgent)this.agent; 
		double depositsValue=0;
		for(int i=0;i<depositIds.length;i++){
			List<Item> deposits = bank.getItemsStockMatrix(false, depositIds[i]);
			for(Item deposit:deposits){
				depositsValue+=deposit.getValue();
			}
		}
		double reservesValue=0;
		for(int i=0;i<reserveIds.length;i++){
			List<Item> reserves = bank.getItemsStockMatrix(true, reserveIds[i]);
			for(Item reserve:reserves){
				reservesValue+=reserve.getValue();
			}
		}
		double liquidityRatio = bank.getTargetedLiquidityRatio();
		return liquidityRatio*depositsValue-reservesValue;
	}

	
	/**
	 * @return the depositIds
	 */
	public int[] getDepositIds() {
		return depositIds;
	}

	/**
	 * @param depositIds the depositIds to set
	 */
	public void setDepositIds(int[] depositIds) {
		this.depositIds = depositIds;
	}

	/**
	 * @return the reserveIds
	 */
	public int[] getReserveIds() {
		return reserveIds;
	}

	/**
	 * @param reserveIds the reserveIds to set
	 */
	public void setReserveIds(int[] reserveIds) {
		this.reserveIds = reserveIds;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [liquidityRatio][depositSize][depositIds][reserveSize][reserveIds]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8+4*(depositIds.length+reserveIds.length));
		buf.putInt(this.depositIds.length);
		for(int id:depositIds)
			buf.putInt(id);
		buf.putInt(this.reserveIds.length);
		for(int id:reserveIds)
			buf.putInt(id);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [liquidityRatio][depositSize][depositIds][reserveSize][reserveIds]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbDeposits = buf.getInt();
		depositIds=new int[nbDeposits];
		for(int i = 0 ; i < nbDeposits ; i++)
			depositIds[i] = buf.getInt();
		int nbReserve = buf.getInt();
		reserveIds=new int[nbReserve];
		for(int i = 0 ; i < nbReserve ; i++)
			reserveIds[i] = buf.getInt();
	}	
	
}
