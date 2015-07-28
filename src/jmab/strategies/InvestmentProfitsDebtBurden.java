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

import jmab.agents.InvestmentAgent;
import jmab.goods.CapitalGood;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This strategy determine the desired rate of growth for a firm's production capacity (constrained to be non negative)
 * as a a positive function of the profit rate -computed as gross profits over capital stock value at historical costs- and a
 * negative function of the debt burden, computed as total interest to be paid on the current stock of loans divided by 
 * gross profits.
 */
@SuppressWarnings("serial")
public class InvestmentProfitsDebtBurden extends AbstractStrategy implements
		InvestmentStrategy {
	
	double profitRateWeight;
	double debtBurdenWeight;
	int capitalStockId; //this parameter should be set in the config file equal to the the key corresponding to CapitalId in the staticValues interface.
	int loansId; // see above
	int pastProfitId;

	
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
	 * @return the profitRateWeight
	 */
	public double getProfitRateWeight() {
		return profitRateWeight;
	}


	/**
	 * @param profitRateWeight the profitRateWeight to set
	 */
	public void setProfitRateWeight(double profitRateWeight) {
		this.profitRateWeight = profitRateWeight;
	}


	/**
	 * @return the debtBurdenWeight
	 */
	public double getDebtBurdenWeight() {
		return debtBurdenWeight;
	}


	/**
	 * @param debtBurdenWeight the debtBurdenWeight to set
	 */
	public void setDebtBurdenWeight(double debtBurdenWeight) {
		this.debtBurdenWeight = debtBurdenWeight;
	}


	/**
	 * @return the capitalStockId
	 */
	public int getCapitalStockId() {
		return capitalStockId;
	}


	/**
	 * @param capitalStockId the capitalStockId to set
	 */
	public void setCapitalStockId(int capitalStockId) {
		this.capitalStockId = capitalStockId;
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


	/* (non-Javadoc)
	 * @see jmab.strategies.InvestmentStrategy#computeDesiredGrowth()
	 */
	@Override
	public double computeDesiredGrowth() {
		InvestmentAgent investor= (InvestmentAgent) this.getAgent();
		List<Item> capitalStock=investor.getItemsStockMatrix(true, capitalStockId);
		double capitalValue=0;
		for (Item i:capitalStock){
			CapitalGood capital= (CapitalGood) i;
			capitalValue+=capital.getValue();
		}
		double totalInterest=0;
		List<Item> loansStock=investor.getItemsStockMatrix(false, loansId);
		for (Item i:loansStock){
			Loan loan=(Loan)i;
			totalInterest+=loan.getValue()*loan.getInterestRate();	//TODO check if loan.getValue actually is the field updated after loan principal repayment	
		}
		
		double profitRate= investor.getPassedValue(pastProfitId, 1)/capitalValue;
		double debtBurden= totalInterest/investor.getPassedValue(pastProfitId, 1);
		double desiredRateOfGrowth=profitRateWeight*profitRate-debtBurdenWeight*debtBurden;
		return Math.max(-1, desiredRateOfGrowth);
		//if(investor.getExpectedProfits()>0){
			//double profitRate= investor.getExpectedProfits()/capitalValue;
			//double debtBurden= totalInterest/investor.getExpectedProfits();
			//double desiredRateOfGrowth=profitRateWeight*profitRate-debtBurdenWeight*debtBurden;
			//if(desiredRateOfGrowth>1){
				//int blah=1;
			//}
			//return Math.max(-1, desiredRateOfGrowth);
		//}else{
			//return 0;
		//}
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [profitRateWeight][debtBurdenWeight][capitalStockId][loansId][pastCapitalId][pastProfitId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(28);
		buf.putDouble(profitRateWeight);
		buf.putDouble(debtBurdenWeight);
		buf.putInt(capitalStockId);
		buf.putInt(loansId);
		buf.putInt(pastProfitId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [profitRateWeight][debtBurdenWeight][capitalStockId][loansId][pastCapitalId][pastProfitId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.profitRateWeight = buf.getDouble();
		this.debtBurdenWeight = buf.getDouble();
		this.capitalStockId = buf.getInt();
		this.loansId = buf.getInt();
		this.pastProfitId = buf.getInt();
	}

}
