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

import jmab.goods.CapitalGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.strategies.InvestmentStrategy;
import modellone.agents.ConsumptionFirm;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class InvestmentCapacityOperatingCashFlowExpected extends AbstractStrategy implements
		InvestmentStrategy {
	
	double cashFlowRateWeight;
	double capacityWeight;
	int capitalStockId; //this parameter should be set in the config file equal to the the key corresponding to CapitalId in the staticValues interface.
	int pastCapitalId; //the id of past capital financial values in the matrix of passed values
	int pastCashFlowId;
	double targetCapacityUtlization;
	double targetCashFlow;
	

	/**
	 * @return the targetCashFlow
	 */
	public double getTargetCashFlow() {
		return targetCashFlow;
	}


	/**
	 * @param targetCashFlow the targetCashFlow to set
	 */
	public void setTargetCashFlow(double targetCashFlow) {
		this.targetCashFlow = targetCashFlow;
	}


	/**
	 * @return the pastCashFlowId
	 */
	public int getPastCashFlowId() {
		return pastCashFlowId;
	}


	/**
	 * @param pastCashFlowId the pastCashFlowId to set
	 */
	public void setPastCashFlowId(int pastCashFlowId) {
		this.pastCashFlowId = pastCashFlowId;
	}


	


	/**
	 * @return the cashFlowRateWeight
	 */
	public double getCashFlowRateWeight() {
		return cashFlowRateWeight;
	}


	/**
	 * @param cashFlowRateWeight the cashFlowRateWeight to set
	 */
	public void setCashFlowRateWeight(double cashFlowRateWeight) {
		this.cashFlowRateWeight = cashFlowRateWeight;
	}


	/**
	 * @return the capacityWeight
	 */
	public double getCapacityWeight() {
		return capacityWeight;
	}


	/**
	 * @param capacityWeight the capacityWeight to set
	 */
	public void setCapacityWeight(double capacityWeight) {
		this.capacityWeight = capacityWeight;
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
	 * @return the pastCapitalId
	 */
	public int getPastCapitalId() {
		return pastCapitalId;
	}


	/**
	 * @param pastCapitalId the pastCapitalId to set
	 */
	public void setPastCapitalId(int pastCapitalId) {
		this.pastCapitalId = pastCapitalId;
	}

	/**
	 * @return the loansId
	 */
	

	/* (non-Javadoc)
	 * @see jmab.strategies.InvestmentStrategy#computeDesiredGrowth()
	 */
	@Override
	public double computeDesiredGrowth() {
		ConsumptionFirm investor= (ConsumptionFirm) this.getAgent();
		List<Item> capitalStock=investor.getItemsStockMatrix(true, capitalStockId);
		double capacity=0;
		if(capitalStock.size()!=0){
		for (Item i:capitalStock){
			CapitalGood capital= (CapitalGood) i;
			capacity+=capital.getQuantity()*capital.getProductivity();
		}
		}
		double desiredOutput=investor.getDesiredOutput();
		double capitalValue=investor.getPassedValue(pastCapitalId, 1);
		double cashFlowRate= investor.getPassedValue(pastCashFlowId,1)/capitalValue;
		if (capacity==0||capitalValue==0){
			return -1;
		}
		else{
		double desiredRateOfGrowth=cashFlowRateWeight*(cashFlowRate-targetCashFlow)/targetCashFlow+capacityWeight*((desiredOutput/capacity)-targetCapacityUtlization)/targetCapacityUtlization;
		return Math.max(-1, desiredRateOfGrowth);
		}
		
	}


	/**
	 * @return the targetCapacityUtlization
	 */
	public double getTargetCapacityUtlization() {
		return targetCapacityUtlization;
	}
	
	


	/**
	 * @param targetCapacityUtlization the targetCapacityUtlization to set
	 */
	public void setTargetCapacityUtlization(double targetCapacityUtlization) {
		this.targetCapacityUtlization = targetCapacityUtlization;
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [cashFlowRateWeight][capacityWeight][targetCapacityUtlization][targetCashFlow][capitalStockId][pastCapitalId][pastCashFlowId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(44);
		buf.putDouble(cashFlowRateWeight);
		buf.putDouble(capacityWeight);
		buf.putDouble(targetCapacityUtlization);
		buf.putDouble(targetCashFlow);
		buf.putInt(capitalStockId);
		buf.putInt(pastCapitalId);
		buf.putInt(pastCashFlowId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:s
	 * [cashFlowRateWeight][capacityWeight][targetCapacityUtlization][targetCashFlow][capitalStockId][pastCapitalId][pastCashFlowId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.cashFlowRateWeight = buf.getDouble();
		this.capacityWeight = buf.getDouble();
		this.targetCapacityUtlization = buf.getDouble();
		this.targetCashFlow = buf.getDouble();
		this.capitalStockId = buf.getInt();
		this.pastCapitalId = buf.getInt();
		this.pastCashFlowId = buf.getInt();
	}

}
