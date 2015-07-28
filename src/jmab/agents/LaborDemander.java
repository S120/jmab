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

import jmab.goods.Item;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface LaborDemander extends MacroAgent {

	/**
	 * @param idMarket
	 * @param payableStock
	 * @return
	 */
	public List<Item> getPayingStocks(int idMarket, Item payableStock);

	/**
	 * @param worker
	 */
	public void addEmployee(LaborSupplier worker);

	/**
	 * @return
	 */
	public List<MacroAgent> getEmployees();

	/**
	 * @return
	 */
	public int getLaborDemand();
	
	/**
	 * Obtains the wage bill of the employer
	 * @return
	 */
	public double getWageBill();
	
	


}
