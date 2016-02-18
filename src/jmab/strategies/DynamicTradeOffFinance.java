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
package jmab.strategies;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import jmab.agents.FinanceAgent;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This strategies determines the credit demanded as the product of NetWealth for a target leverage level.
 * The target leverage is endogenously updated by comparing the return on past loans, defined as the ratio between gross 
 * profits and loans, with the (weighted) average interest rate paid on loan (notice that this is equal to say that they increase
 * the leverage target whenever the inverse of the debt burden is above 1, that is past profits higher than the flow of interests),
 *  and by comparing the current level of a reference variable (e.g. ratio inventories to past sales) with the threshold 
 *  (e.g. the firms' target inventories/sales ratio)  set for it.
 * NB Classes of Agents using this strategy must implement the FinanceAgent Interface.
 */
@SuppressWarnings("serial")
public class DynamicTradeOffFinance extends AbstractStrategy implements
		FinanceStrategy {
	
	protected double leverageTarget;
	protected double adaptiveParameter;
	protected int loansId; 
	protected double threshold; 
	protected int pastProfitId;
	
	/**
	 * @return the pastProfitId
	 */
	public int getPastProfitId() {
		return pastProfitId;
	}

	/**
	 * @param pastProfitId the pastProfitId to set
	 */
	public void setPastProfitId(int pastProfitId) {
		this.pastProfitId = pastProfitId;
	}

	/**
	 * @return the leverageTarget
	 */
	public double getLeverageTarget() {
		return leverageTarget;
	}

	/**
	 * @param leverageTarget the leverageTarget to set
	 */
	public void setLeverageTarget(double leverageTarget) {
		this.leverageTarget = leverageTarget;
	}

	/**
	 * @return the adaptiveParameter
	 */
	public double getAdaptiveParameter() {
		return adaptiveParameter;
	}

	/**
	 * @param adaptiveParameter the adaptiveParameter to set
	 */
	public void setAdaptiveParameter(double adaptiveParameter) {
		this.adaptiveParameter = adaptiveParameter;
	}

	/**
	 * @return the loansId
	 */
	public int getLoansId() {
		return loansId;
	}

	/**
	 * @param loansId the loansId to set
	 */
	public void setLoansId(int loansId) {
		this.loansId = loansId;
	}

	/**
	 * @return the thresholdRefVariable
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param thresholdRefVariable the thresholdRefVariable to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.FinanceStrategy#computeCreditDemand(double)
	 */
	public void updateLeverageTarget (){
		FinanceAgent borrower= (FinanceAgent)this.getAgent();
		double pastProfits=borrower.getPassedValue(pastProfitId, 1); 
		double totalPastLoans=0;
		double weightedSum=0;
		for (Item i: borrower.getItemsStockMatrix(false, loansId)){			
			totalPastLoans+=i.getValue();
			weightedSum+=i.getValue()*((Loan)i).getInterestRate();	
		}
		double averageInterestRate=weightedSum/totalPastLoans;
		double returnOnLoans=pastProfits/totalPastLoans;
		Random random = new Random();
		if (returnOnLoans>averageInterestRate && borrower.getReferenceVariableForFinance()<threshold){
			leverageTarget+=adaptiveParameter*leverageTarget*random.nextDouble();
		}
		//else if (returnOnLoans==averageInterestRate && borrower.getReferenceVariableForFinance()<thresholdRefVariable){
			//leverageTarget=leverageTarget;
		//}
		else if (returnOnLoans<averageInterestRate || borrower.getReferenceVariableForFinance()>threshold){
			leverageTarget-=adaptiveParameter*leverageTarget*random.nextDouble();
		}
	}
	
	
	@Override
	public double computeCreditDemand(double expectedFinancialRequirement) {
		MacroAgent borrower= (MacroAgent)this.getAgent();
		updateLeverageTarget();
		double[][] bs = borrower.getNumericBalanceSheet();
		double wealthValue=0;
		for(int i = 0 ; i<bs[0].length;i++){
			wealthValue+=bs[0][i];
		}
		double actualCredit=bs[1][this.loansId];
		List<Item> loans = borrower.getItemsStockMatrix(false, loansId);
		double capitalPayments = 0;
		for(int i=0;i<loans.size();i++){
			Loan loan=(Loan)loans.get(i);
			int length = loan.getLength();
			double amount=loan.getInitialAmount();
			switch(loan.getAmortization()){
			case Loan.FIXED_AMOUNT:
				double iRate=loan.getInterestRate();
				double amortization = amount*(iRate*Math.pow(1+iRate, length))/(Math.pow(1+iRate, length)-1);
				capitalPayments+=amortization-iRate*amount;
				break;
			case Loan.FIXED_CAPITAL:
				capitalPayments+=amount/length;
				break;
			case Loan.ONLY_INTERESTS:
				if(length==loan.getAge())
					capitalPayments+=amount;
				break;
			}
		}
		
		return Math.max((wealthValue*leverageTarget+capitalPayments*(1-leverageTarget)-actualCredit)/(1-leverageTarget),expectedFinancialRequirement);
	
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][leverageTarget][loansId][pastProfitId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(32);
		buf.putDouble(threshold);
		buf.putDouble(adaptiveParameter);
		buf.putDouble(this.leverageTarget);
		buf.putInt(loansId);
		buf.putInt(pastProfitId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][leverageTarget][loansId][pastProfitId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.leverageTarget = buf.getDouble();
		this.loansId = buf.getInt();
		this.pastProfitId = buf.getInt();
	}
	
}
