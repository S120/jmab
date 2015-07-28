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
import jmab.simulations.MarketSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *This class contains the methods required to realize a transaction in the credit market in case there might be supply constraints, once the matching mechanism and
 * the agents' strategies have defined the terms of the transaction (GoodSupplier, GoodDemander, quantity, price/interest, maturity etc.)
 * 
 */
//with this mechanism borrowers' demand for credit can be constrained by lenders' supply
public class ConstrainedCreditMechanism extends AbstractCreditMechanism implements
		Mechanism {

	/**
	 * @param scheduler
	 * @param market
	 */
	public ConstrainedCreditMechanism(){}
	
	public ConstrainedCreditMechanism(MarketSimulation market) {
		super(market);
	}
	
/**
 * This methods executes the credit transaction and ensures the SF consistency:
 * a)create the new object "Loan" and add it to the SM of the CreditDemander/CreditSupplier as a liability/asset
 * b)it update the deposits: b.1) if the CreditDemander has no deposit at the lending bank it create a new one and it add it 
 * to the SM of the borrower (asset)/CreditSupplier(liability). b.2) else, if the borrower has already a depoist at the lending bank
 * it add the value of the loan to the deposit.
 * The actual value of the loan is the minimum between asked and supplied.
 * 
 */
	private void execute(CreditDemander creditDemander, CreditSupplier creditSupplier, int idMarket) {
		double required=creditDemander.getLoanRequirement(this.idLoansSM); 
		int length=creditDemander.decideLoanLength(this.idLoansSM);
		int amortization=creditDemander.decideLoanAmortizationType(this.idLoansSM);
		double totalLoansSupply=creditSupplier.getTotalLoansSupply(this.idLoansSM);
		double offered=creditSupplier.getLoanSupply(this.idLoansSM, creditDemander,required);
		double amount=Math.min(required, Math.min(offered, totalLoansSupply));
		double interestRate=creditSupplier.getInterestRate(this.idLoansSM, creditDemander, amount, length);
		Loan loan = new Loan(amount, creditSupplier, creditDemander, interestRate, 0, amortization, length);
		creditSupplier.addItemStockMatrix(loan, true, this.idLoansSM);
		creditDemander.addItemStockMatrix(loan, false, this.idLoansSM);
		Deposit deposit = (Deposit)creditDemander.getItemStockMatrix(true, this.idDepositsSM, creditSupplier);
		if(deposit==null){
			deposit=new Deposit(amount, creditDemander,creditSupplier,creditSupplier.getDepositInterestRate(creditDemander, amount));
			creditSupplier.addItemStockMatrix(deposit, false, idDepositsSM);
			creditDemander.addItemStockMatrix(deposit, true, idDepositsSM);
		}else{
			deposit.setValue(deposit.getValue()+amount);
		}
		creditDemander.setLoanRequirement(this.idLoansSM,required-amount);
		if(Math.min(required, amount)==required){
			creditDemander.setActive(false, idMarket);
		}
		creditSupplier.setTotalLoansSupply(this.idLoansSM,(creditSupplier.getTotalLoansSupply(this.idLoansSM)-amount));
		if (creditSupplier.getTotalLoansSupply(this.idLoansSM)==0){
			creditSupplier.setActive(false, idMarket);
		} //TODO
	}
	
	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(net.sourceforge.jabm.agent.Agent, net.sourceforge.jabm.agent.Agent, int)
	 */
	/**
	 * This method makes a casting of the MacroAgent to CreditDemander and CreditSupplier and invokes the method
	 * "execute(CreditDemander CreditDemander, CreditSupplier CreditSupplier, int idMarket)".
	 */
	@Override
	public void execute(MacroAgent GoodDemander, MacroAgent GoodSupplier, int idMarket) {
			execute((CreditDemander) GoodDemander, (CreditSupplier) GoodSupplier, idMarket);

	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void execute(MacroAgent buyer, List<MacroAgent> seller, int idMarket) {
		// TODO Auto-generated method stub
		
	}
}
