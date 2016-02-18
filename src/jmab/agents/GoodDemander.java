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

import java.util.List;

import jmab.stockmatrix.Item;



/**
 * @author Alessandro Caiani and Antoine Godin
 * Interface to be implemented if an agent interacts on the good market as a good demander
 * (i.e offers credit to agents).
 */
public interface GoodDemander extends MacroAgent{
	
	/**
	 * @return the demand for good idGood
	 */
	public double getDemand(int idMarket);

	/**
	 * @param idGood
	 * @return the list of stock to be used to pay for the goods demanded
	 */
	public List<Item> getPayingStocks(int idGood, Item payableStock);

	/**
	 * @param d
	 */
	public void setDemand(double d, int idMarket);
	
}
