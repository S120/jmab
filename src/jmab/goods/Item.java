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
package jmab.goods;

import jmab.agents.MacroAgent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 * This interface represent the basic object to be stored in Stock Matrices of each agent. The item can then
 * be detailed as a Financial Good or a Real Good. This interface is needed because of the usage of List of
 * List containing Items, in the StockMatrix of the agents. 
 *
 */
public interface Item {

	/**
	 * @return the value of the item
	 */
	public double getValue();
	
	/**
	 * @param value the value of the item
	 */
	public void setValue(double value);
	
	/**
	 * @return the value of the item
	 */
	public double getQuantity();
	
	/**
	 * @param value the value of the item
	 */
	public void setQuantity(double quantity);
	
	/**
	 * @return the assetHolder
	 */
	public MacroAgent getAssetHolder();

	/**
	 * @param assetHolder the assetHolder to set
	 */
	public void setAssetHolder(MacroAgent assetHolder);

	/**
	 * @return the liabilityHolder
	 */
	public MacroAgent getLiabilityHolder();

	/**
	 * @param liabilityHolder the liabilityHolder to set
	 */
	public void setLiabilityHolder(MacroAgent liabilityHolder);

	/**
	 * @return the id of the item in the StockMatrix
	 */
	public int getSMId();
	
	/**
	 * @param SMId the id in the StockMatrix
	 */
	public void setSMId(int SMId);
	
	/**
	 * Detrmines wether the item should be removed from the SM
	 * @return
	 */
	public boolean remove();
	
	/**
	 * Updates the state of the item
	 * @return
	 */
	public void update();
	
	public int getAge();
	
	public void setAge(int age);
	
	public byte[] getBytes();

}
