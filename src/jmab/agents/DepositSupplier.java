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
 * Interface to be implemented if an agent interacts on the credit market as a deposit supplier
 * (i.e offers deposits to agents).
 */
public interface DepositSupplier extends LiabilitySupplier {
	
	/**
	 * Computes the interest rate to be offered to the deposit demander
	 * @param depositDemander the deposit demander
	 * @param amount the amount of money to be stored on the deposit account
	 * @return the interest rate offered
	 */
	public double getDepositInterestRate(MacroAgent depositDemander, double amount);

}
