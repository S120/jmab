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

import jmab.expectations.Expectation;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import jmab.strategies.BankruptcyStrategy;
import modellone.StaticValues;
import modellone.agents.Bank;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class BankBankruptcyBailoutCAR extends AbstractStrategy implements
		BankruptcyStrategy {
	
//	private int numberBailouts; 
	private int depositId;
	private int depositExpectationId; 

	/**
	 * @return the depositId
	 */
	public int getDepositId() {
		return depositId;
	}


	/**
	 * @param depositId the depositId to set
	 */
	public void setDepositId(int depositId) {
		this.depositId = depositId;
	}


	/**
	 * 
	 */
	public BankBankruptcyBailoutCAR() {
		super();
//		this. numberBailouts=0;
	}


	/**
	 * @return the depositExpectationId
	 */
	public int getDepositExpectationId() {
		return depositExpectationId;
	}


	/**
	 * @param depositExpectationId the depositExpectationId to set
	 */
	public void setDepositExpectationId(int depositExpectationId) {
		this.depositExpectationId = depositExpectationId;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.BankruptcyStrategy#bankrupt()
	 */
	@Override
	public void bankrupt() {
		Bank bank = (Bank) getAgent();
		Population banks = ((MacroPopulation)((SimulationController)this.scheduler).getPopulation()).getPopulation(StaticValues.BANKS_ID);
		double tot=0;
		for (Agent b:banks.getAgents()){
			Bank bank1 = (Bank) b;
			if (bank1.getAgentId()!=bank.getAgentId())
			tot+=bank1.getCapitalRatio();
			}
		double car=tot/(banks.getSize()-1)+Math.random()*(0.1);
		List<Item> loans=bank.getItemsStockMatrix(true, StaticValues.SM_LOAN);
		double loansValue=0;
		for (Item a:loans){
			Loan loan= (Loan)a;
			loansValue+=loan.getValue();
		}
		double targetNW=car*loansValue;
		double nw=bank.getNetWealth();
		bank.setBailoutCost(targetNW-nw);
		double totDeposits= bank.getNumericBalanceSheet()[1][depositId];
//		numberBailouts+=1;
		double newDepValue=0;
		for (Item deposit:bank.getItemsStockMatrix(false, depositId)){
			deposit.setValue(deposit.getValue()+(deposit.getValue()*(nw-targetNW)/totDeposits));
			newDepValue+=deposit.getValue();
		}
		Expectation exp =bank.getExpectation(depositExpectationId);
		double[][] expData = exp.getPassedValues();
		for(int j = 0; j<expData.length; j++){
			expData[j][0]=newDepValue;
			expData[j][1]=newDepValue;
		}
		exp.setPassedValues(expData);
		System.out.println("bank "+ bank.getAgentId() +" defaulted");
		//System.out.println(numberBailouts);
		
	}
	

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [depositId][depositExpectationId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putInt(this.depositId);
		buf.putInt(this.depositExpectationId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [depositId][depositExpectationId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.depositId = buf.getInt();
		this.depositExpectationId = buf.getInt();
	}

}
