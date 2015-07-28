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
 * Interface to be implemented if an agent interacts on the credit market as a credit supplier
 * (i.e offers credit to agents).
 */
public interface CreditSupplier extends MacroAgent {
	
	/**
	 * Computes the interest rate to be charged on the credit requested by the credit demander
	 * @param creditDemander the credit demander
	 * @param amount the amount to be borrowed
	 * @param length the length of the credit
	 * @return the interest rate
	 */
	public double getInterestRate(int idLoanSM, MacroAgent creditDemander, double amount, int length);

	/**
	 * @param creditDemander the credit demander
	 * @param required the amount of the loan requested by the credit demander
	 * @return the amount the credit supplier is willing to offer to the credit demander
	 */
	public double getLoanSupply(int idLoanSM, MacroAgent creditDemander, double required);
	
	/**
	 * Computes the interest rate offered on the deposit account created when a credit is granted
	 * @param creditDemander the credit demander
	 * @param amount the amount of the loan
	 * @return the interest rate offered on the deposit where is stored the money obtained through the loan
	 */
	public double getDepositInterestRate(MacroAgent creditDemander, double amount);

	/**
	 * @return
	 */
	public double getTotalLoansSupply(int idLoanSM);

	/**
	 * @param d
	 */
	public void setTotalLoansSupply(int idLoanSM, double d);
	
	public double getCurrentNonPerformingLoans(int idLoanSM);
	public void setCurrentNonPerformingLoans(int idLoanSM, double amount);
	
}
