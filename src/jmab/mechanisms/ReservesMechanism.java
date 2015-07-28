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

import java.util.List;

import jmab.agents.CreditDemander;
import jmab.agents.CreditSupplier;
import jmab.agents.MacroAgent;
import jmab.goods.Deposit;
import jmab.goods.Loan;

/**
 * @author Alessandro Caiani and Antoine Godin
 * TODO I don't like the hard coding of reserve length and interest rates
 */
public class ReservesMechanism extends AbstractMechanism implements Mechanism {
	
	private int idAdvancesSM;
	private int idDepositsCBSM;
	
	

	/**
	 * @return the idAdvancesSM
	 */
	public int getIdAdvancesSM() {
		return idAdvancesSM;
	}

	/**
	 * @param idAdvancesSM the idAdvancesSM to set
	 */
	public void setIdAdvancesSM(int idAdvancesSM) {
		this.idAdvancesSM = idAdvancesSM;
	}

	/**
	 * @return the idDepositsCBSM
	 */
	public int getIdDepositsCBSM() {
		return idDepositsCBSM;
	}

	/**
	 * @param idDepositsCBSM the idDepositsCBSM to set
	 */
	public void setIdDepositsCBSM(int idDepositsCBSM) {
		this.idDepositsCBSM = idDepositsCBSM;
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, jmab.agents.MacroAgent, int)
	 */
	@Override
	public void execute(MacroAgent buyer, MacroAgent seller, int idMarket) {
		execute ((CreditDemander)buyer, (CreditSupplier)seller, idMarket);
	}
	
	public void execute (CreditDemander bank, CreditSupplier centralBank, int idMarket){
		double advancesDemanded=bank.getLoanRequirement(this.idAdvancesSM);
		double repo=centralBank.getInterestRate(this.idAdvancesSM, bank, advancesDemanded, 1); //borrowing by banks from CB are usually on a short-term basis, hence lenght=1
		Loan advances = new Loan(advancesDemanded, centralBank, bank, repo,0, bank.decideLoanAmortizationType(this.idAdvancesSM), 1); //borrowing by banks from CB are usually on a short-term basis, hence lenght=1
		centralBank.addItemStockMatrix(advances, true, this.idAdvancesSM);
		bank.addItemStockMatrix(advances, false, this.idAdvancesSM);
		Deposit depositCB = (Deposit)bank.getItemStockMatrix(true, this.idDepositsCBSM, centralBank);
		if(depositCB==null){
			depositCB=new Deposit(advancesDemanded, bank,centralBank,0); //bank's deposits at the CB do not bring any interest
			centralBank.addItemStockMatrix(depositCB, false, idDepositsCBSM);
			bank.addItemStockMatrix(depositCB, true, idDepositsCBSM);
		}else{
			depositCB.setValue(depositCB.getValue()+advancesDemanded);
		}
		bank.setActive(false, idMarket);
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void execute(MacroAgent buyer, List<MacroAgent> seller, int idMarket) {
		// TODO Auto-generated method stub
		
	}

}
