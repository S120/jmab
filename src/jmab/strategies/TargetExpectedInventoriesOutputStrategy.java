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
import jmab.expectations.Expectation;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Represents a production strategy based on expected sales and inventories being a share of expected sales.
 * The desired output is thus equal to expected sales plus the difference between current level of inventories
 * and desired level of inventories, based on expected sales.
 * There are three parameters related to the production strategy:
 * - inventoryShare: the share of sales that is desired to be stocked as inventories
 * - inventorySMId: the id the inventories in the StockMatrix
 * - salesExId: the id of the expectation on sales in the Expectation Hash Map.
 */
@SuppressWarnings("serial")
public class TargetExpectedInventoriesOutputStrategy extends AbstractStrategy implements
		ProductionStrategy {

	private double inventoryShare;
	private int inventorySMId;
	private int salesExpId;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.ProductionStrategy#computeOutput()
	 */
	@Override
	public double computeOutput() {
		MacroAgent producer = (MacroAgent) this.getAgent();
		Expectation salesExp = producer.getExpectation(this.salesExpId);
		double expSales = salesExp.getExpectation();
		Item inventories = producer.getItemStockMatrix(true, this.inventorySMId);
		double invQuantity = inventories.getQuantity();
		double expInv=expSales*this.inventoryShare;
		return Math.max(0,expSales+(expInv-invQuantity));
	}

	/**
	 * @return the inventoryShare
	 */
	public double getInventoryShare() {
		return inventoryShare;
	}

	/**
	 * @param inventoryShare the inventoryShare to set
	 */
	public void setInventoryShare(double inventoryShare) {
		this.inventoryShare = inventoryShare;
	}

	/**
	 * @return the inventorySMId
	 */
	public int getInventorySMId() {
		return inventorySMId;
	}

	/**
	 * @param inventorySMId the inventorySMId to set
	 */
	public void setInventorySMId(int inventorySMId) {
		this.inventorySMId = inventorySMId;
	}

	/**
	 * @return the salesExpId
	 */
	public int getSalesExpId() {
		return salesExpId;
	}

	/**
	 * @param salesExpId the salesExpId to set
	 */
	public void setSalesExpId(int salesExpId) {
		this.salesExpId = salesExpId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [inventoryShare][inventorySMId][salesExpId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putDouble(inventoryShare);
		buf.putInt(inventorySMId);
		buf.putInt(salesExpId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [inventoryShare][inventorySMId][salesExpId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.inventoryShare = buf.getDouble();
		this.inventorySMId = buf.getInt();
		this.salesExpId = buf.getInt();
	}
	
}
