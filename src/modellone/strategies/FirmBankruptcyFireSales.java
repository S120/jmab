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
package modellone.strategies;

import java.nio.ByteBuffer;
import java.util.List;

import jmab.agents.AbstractFirm;
import jmab.agents.CreditSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.goods.Cash;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import jmab.strategies.BankruptcyStrategy;
import modellone.StaticValues;
import modellone.agents.ConsumptionFirm;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class FirmBankruptcyFireSales extends AbstractStrategy implements
		BankruptcyStrategy {
	
	private double haircut;
	
	
	/**
	 * @return the haircut
	 */
	public double getHaircut() {
		return haircut;
	}


	/**
	 * @param haircut the haircut to set
	 */
	public void setHaircut(double haircut) {
		this.haircut = haircut;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.BankruptcyStrategy#bankrupt()
	 */
	@Override
	public void bankrupt() {

		AbstractFirm firm = (AbstractFirm) this.agent;

		//1. Move all money on one deposit if there were two of them
		List<Item> deposits = firm.getItemsStockMatrix(true, StaticValues.SM_DEP);
		Deposit deposit = (Deposit)deposits.get(0);
		if(deposits.size()==2){
			Item deposit2 = deposits.get(1);
			LiabilitySupplier supplier = (LiabilitySupplier) deposit2.getLiabilityHolder();
			supplier.transfer(deposit2, deposit, deposit2.getValue());
			deposit2.getLiabilityHolder().removeItemStockMatrix(deposit2, false, deposit2.getSMId());
		}

		//2. Transfer all cash on the deposit account
		Cash cash = (Cash)firm.getItemStockMatrix(true, StaticValues.SM_CASH);
		if(cash.getValue()>0){
			LiabilitySupplier bank = (LiabilitySupplier)deposit.getLiabilityHolder();
			Item bankCash = bank.getCounterpartItem(deposit, cash);
			bankCash.setValue(bankCash.getValue()+cash.getValue());
			deposit.setValue(deposit.getValue()+cash.getValue());
			cash.setValue(0);
		}

		//3. Compute total liquidity to be distributed to creditors
		double liquidity=deposit.getValue();
		

		//4. Compute each creditor's share of debt
		List<Item> loans=firm.getItemsStockMatrix(false, StaticValues.SM_LOAN);
		double[] debts = new double[loans.size()];
		double[] banksLosses = new double[loans.size()];
		double totalDebt=0;
		double totalBanksLoss=0;
		for(int i=0;i<loans.size();i++){
			Loan loan=(Loan)loans.get(i);
			debts[i]=loan.getValue();
			banksLosses[i]=loan.getValue();
			totalDebt+=loan.getValue();
			totalBanksLoss+=loan.getValue();
		}
		
		if (totalDebt!=0){
			//5. Distribute liquidity according to the share of debt of each creditor
			for(int i=0;i<loans.size();i++){
				Loan loan = (Loan) loans.get(i);
				double amountToPay=liquidity*(debts[i])/totalDebt;
				if (liquidity>=totalDebt){
					amountToPay=debts[i];
				}
				//lendingBank.setCurrentNonPerformingLoans(lendingBank.getCurrentNonPerformingLoans()+(debts[i]-amountToPay)); 
				deposit.setValue(deposit.getValue()-amountToPay);
				if(loan.getAssetHolder()!=deposit.getLiabilityHolder()){
					Item lBankRes = loan.getAssetHolder().getItemStockMatrix(true,StaticValues.SM_RESERVES);
					lBankRes.setValue(lBankRes.getValue()+amountToPay);
					Item dBankRes = deposit.getLiabilityHolder().getItemStockMatrix(true, StaticValues.SM_RESERVES);
					dBankRes.setValue(dBankRes.getValue()-amountToPay);
				}
				loan.setValue(loan.getValue()-amountToPay);
				banksLosses[i]-=amountToPay;
				totalBanksLoss-=amountToPay;
				//loan.setValue(0);
			}
			deposit.setValue(0.0);
			
			//compute the value of capital to be sold
			if (firm instanceof ConsumptionFirm){
				double capitalValue=0;
				List<Item> capital=firm.getItemsStockMatrix(true, StaticValues.SM_CAPGOOD);
				for (Item i:capital){
					capitalValue+=i.getValue()*haircut;
				}
				double ownersDisbursment=0;
				if (capitalValue>totalBanksLoss){
					
					ownersDisbursment=totalBanksLoss;
				}
				else{
					ownersDisbursment=capitalValue;
				}
				firm.setBailoutCost(ownersDisbursment);
				SimulationController controller = (SimulationController)firm.getScheduler();
				MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
				Population households = macroPop.getPopulation(StaticValues.HOUSEHOLDS_ID);
				double totalHouseholdsWealth=0;
				for(Agent h:households.getAgents()){
					totalHouseholdsWealth+=((MacroAgent)h).getNetWealth();
				}
				for (Agent h:households.getAgents()){
					MacroAgent hh = (MacroAgent) h;
					for(int i=0;i<loans.size();i++){
						Loan loan = (Loan) loans.get(i);
						//each owner (household contribute according to his share of net-wealth, each creditor is refunded according to his share of credit.
						double amountToPay=ownersDisbursment*hh.getNetWealth()/totalHouseholdsWealth*(banksLosses[i])/totalBanksLoss;
						CreditSupplier lendingBank= (CreditSupplier) loan.getAssetHolder();
						Deposit depositHH =(Deposit)hh.getItemStockMatrix(true, StaticValues.SM_DEP);
						if (depositHH.getLiabilityHolder()==lendingBank){
							depositHH.setValue(depositHH.getValue()-amountToPay);
						}
						else{
							depositHH.setValue(depositHH.getValue()-amountToPay);
							Item dBankReserve= (Item) depositHH.getLiabilityHolder().getItemStockMatrix(true,StaticValues.SM_RESERVES);
							dBankReserve.setValue(dBankReserve.getValue()-amountToPay);
							Item lendingBankReserves= (Item) lendingBank.getItemStockMatrix(true, StaticValues.SM_RESERVES);
							lendingBankReserves.setValue(lendingBankReserves.getValue()+amountToPay);
						}
						loan.setValue(loan.getValue()-amountToPay);
						banksLosses[i]-=amountToPay;
						totalBanksLoss-=amountToPay;
					}
				}
			} 
			//all banks recover the same share of their outstanding credit as the total available funds are residualDeposits plus K
			//discounted value and this sum is distributed across loans on the base of their weight on total outstanding loans. 
			//Abstracting from residual deposits (which in most cases would be negligible) the share recovered would be Kvalue/totLoans
			for(int i=0;i<loans.size();i++){
				Loan loan = (Loan) loans.get(i);
				CreditSupplier lendingBank= (CreditSupplier) loan.getAssetHolder();
				lendingBank.setCurrentNonPerformingLoans(StaticValues.SM_LOAN,lendingBank.getCurrentNonPerformingLoans(StaticValues.SM_LOAN)+banksLosses[i]);
				loan.setValue(0);
			} 
		}
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [haircut]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putDouble(this.haircut);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [haircut]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.haircut = buf.getDouble();
	}

}
