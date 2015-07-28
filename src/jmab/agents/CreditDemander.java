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
 * Interface to be implemented if an agent interacts on the credit market as a credit demander 
 * (i.e demands a loan from a bank).
 */
public interface CreditDemander extends MacroAgent {
	
	/**
	 * @return the amount to be borrowed
	 */
	public double getLoanRequirement(int idLoanSM);

	/**
	 * @return the length of the required loan
	 */
	public int decideLoanLength(int idLoanSM);

	/**
	 * @return the tyoe of amortization. Should be one of the three values defined in class Loan:
	 * - Fixed Amount: i.e. repays a constant amount in each period
	 * - Fixed Capital: i.e. repay a fixed share of capital in each period
	 * - Only Interest" i.e. repays only interest in each period ad the principal in the last period
	 */
	public int decideLoanAmortizationType(int idLoanSM);

	/**
	 * @param d
	 */
	public void setLoanRequirement(int idLoanSM, double d);
	
}
