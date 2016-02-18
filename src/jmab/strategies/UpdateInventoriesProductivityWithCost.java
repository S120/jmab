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

import jmab.agents.AbstractFirm;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.CapitalGood;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class UpdateInventoriesProductivityWithCost extends AbstractStrategy
		implements UpdateInventoriesProductivityStrategy {
	
	double inventoriesLostShare;

	
	/**
	 * @return the inventoriesLostShare
	 */
	public double getInventoriesLostShare() {
		return inventoriesLostShare;
	}


	/**
	 * @param inventoriesLostShare the inventoriesLostShare to set
	 */
	public void setInventoriesLostShare(double inventoriesLostShare) {
		this.inventoriesLostShare = inventoriesLostShare;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.UpdateInventoriesProductivityStrategy#updateInventoriesProductivity(double)
	 */
	@Override
	public void updateInventoriesProductivity(double productivity) {
		AbstractFirm producer= (AbstractFirm) this.getAgent();
		CapitalGood inventories = (CapitalGood)producer.getItemStockMatrix(true, producer.getProductionStockId());
		inventories.setQuantity(inventories.getQuantity()*(1-inventoriesLostShare));
		inventories.setProductivity(productivity);
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [inventoriesLostShare]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putDouble(inventoriesLostShare);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [inventoriesLostShare]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.inventoriesLostShare = buf.getDouble();
	}

}
