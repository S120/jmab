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

/**
 * @author Alessandro Caiani and Antoine Godin
 * Interface to be implemented if an agent interacts on the deposit market as a deposit demander 
 * (i.e demands a deposit account from a bank).
 */
public interface DepositDemander extends MacroAgent {
	
	
	/**
	 * @return the amount of money to be held in deposits.
	 */
	public double getDepositAmount();
	
	/**
	 * @return the amount of money to be held in cash.
	 */
	public double getCashAmount();

	public void interestPaid(double interests);


}
