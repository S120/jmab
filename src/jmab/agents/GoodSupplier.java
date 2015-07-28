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
package jmab.agents;

import jmab.goods.Item;


/**
 * @author Alessandro Caiani and Antoine Godin
 * Interface to be implemented if an agent interacts on the good market as a good supplier
 */
public interface GoodSupplier extends GoodAgent{
	
	/**
	 * Computes the price of the good for the good demander and the quantity demanded
	 * @param goodDemander the buyer of the good
	 * @param demand the quantity demanded
	 * @return the price of the demanded good for buyer and quantity demanded 
	 */
	public double getPrice(GoodDemander goodDemander, double demand);
	
	/**
	 * @param idGood
	 * @return the item in the stock matrix where the payment has to be made
	 */
	public Item getPayableStock(int idGood);
	
}
