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

import jmab.agents.DepositDemander;
import jmab.agents.DepositSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.simulations.MarketSimulation;
import jmab.stockmatrix.Cash;
import jmab.stockmatrix.Deposit;
import jmab.stockmatrix.Item;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class DepositMechanism extends AbstractMechanism implements Mechanism {

	private int idDepositsSM;
	private int idCashSM;

	/**
	 * 
	 */
	public DepositMechanism() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param market
	 */
	public DepositMechanism(MarketSimulation market) {
		super(market);
		// TODO Auto-generated constructor stub
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

	/**
	 * @return the idCashSM
	 */
	public int getIdCashSM() {
		return idCashSM;
	}

	/**
	 * @param idCashSM the idCashSM to set
	 */
	public void setIdCashSM(int idCashSM) {
		this.idCashSM = idCashSM;
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, jmab.agents.MacroAgent, int)
	 */
	@Override
	public void execute(MacroAgent buyer, MacroAgent seller, int idMarket) {
		execute((DepositDemander) buyer, (DepositSupplier) seller, idMarket);
	}
		
	/**
	 * @param depositor the agent who deposits.
	 * @param bank the bank chosen by the agent through the matching mechanism.
	 * @param idMarket the id of the deposit market.
	 * cashAmount is the amount of cash that the agent wants to hold and depositAmount is the
	 * amount of deposits the agent wants to hold (their sum being thus equal to the stock of 
	 * liquid assets). Interest rate is the interest paid by the bank chosen through the matching 
	 * mechanism to the depositor (possibly dependent on the amount of deposits).
	 * The method first check whether the agent already possesses a deposit at that bank (if he
	 * does not, it creates a deposit at the bank). Then we transfer all the previous deposits to 
	 * the chosen one and we delete the previous deposits from the SM of the agent. Then, we check
	 * whether the amount of deposits/cash is equal to the desired one (generally no) and when 
	 * this is not the case we withdraw/deposit cash from/into the deposit in order to reach the target.
	 * 
	 * 
	 */
	
	public void execute(DepositDemander depositor, DepositSupplier bank, int idMarket) {
		double cashAmount=depositor.getCashAmount();
		double depositAmount=depositor.getDepositAmount();
		double interestRate=bank.getDepositInterestRate(depositor, depositAmount); 
		List<Item> previousDeposits=depositor.getItemsStockMatrix(true, idDepositsSM);
		Deposit deposit = (Deposit)depositor.getItemStockMatrix(true, this.idDepositsSM, bank);
		if (deposit==null){
			deposit=new Deposit(0, depositor, bank, interestRate);
			bank.addItemStockMatrix(deposit, false, idDepositsSM);
			depositor.addItemStockMatrix(deposit, true, idDepositsSM);
			for(Item dep:previousDeposits)  {
				double amount=dep.getValue();
				LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
				payingSupplier.transfer(dep, deposit, amount);
				MacroAgent previousCounterPart = dep.getLiabilityHolder ();
				depositor.removeItemStockMatrix (dep, true, idDepositsSM);
				previousCounterPart.removeItemStockMatrix (dep, false, idDepositsSM);
				}
			}
		else{
			deposit.setInterestRate(interestRate);
			for(Item dep:previousDeposits){
				if (dep!=deposit){
					double amount=dep.getValue();
					LiabilitySupplier payingSupplier = (LiabilitySupplier) dep.getLiabilityHolder();
					payingSupplier.transfer(dep, deposit, amount);
					MacroAgent previousCounterPart = dep.getLiabilityHolder ();
					depositor.removeItemStockMatrix (dep, true, idDepositsSM);
					previousCounterPart.removeItemStockMatrix (dep, false, idDepositsSM);
				}	
			}
		}
		Cash cashHeld=(Cash) depositor.getItemStockMatrix(true, idCashSM);
		if (cashHeld.getValue()!=cashAmount){
			double cashBias=cashHeld.getValue()-cashAmount;
			deposit.setValue(deposit.getValue()+cashBias);
			cashHeld.setValue(cashHeld.getValue()-cashBias);
			LiabilitySupplier depBank = (LiabilitySupplier) deposit.getLiabilityHolder();
			Item depBankCash=depBank.getCounterpartItem(deposit, cashHeld);
			depBankCash.setValue(depBankCash.getValue()+cashBias);
		}
		depositor.setActive(false, idMarket);
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void execute(MacroAgent buyer, List<MacroAgent> seller, int idMarket) {
		// TODO Auto-generated method stub
		
	}
		
}		
		


