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
package jmab.mechanisms;

import jmab.simulations.MarketSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 * T
 */
public abstract class AbstractCreditMechanism extends AbstractMechanism implements
		Mechanism {
	
	/**
	* idLoansSM and idDepositsSM are the positions of loans and deposits inside the StockMatrix (remember the SM has the same structure for
	* each agent, so these indexes will be equal for each agent)
	*/
	protected int idLoansSM;
	protected int idDepositsSM;
	
	
	public AbstractCreditMechanism(){
		super();
	}

	public AbstractCreditMechanism(MarketSimulation market) {
		super(market);
	}

	/**
	 * @return the idLoansSM
	 */
	public int getIdLoansSM() {
		return idLoansSM;
	}

	/**
	 * @param idLoansSM the idLoansSM to set
	 */
	public void setIdLoansSM(int idLoansSM) {
		this.idLoansSM = idLoansSM;
	}

	/**
	 * @return the idDepositsSM
	 */
	public int getIdDepositsSM() {
		return idDepositsSM;
	}

	/**
	 * @param idDepositsSM the idDepositsSM to set
	 */
	public void setIdDepositsSM(int idDepositsSM) {
		this.idDepositsSM = idDepositsSM;
	}

	
	
}
