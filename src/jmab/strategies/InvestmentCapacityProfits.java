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
import jmab.population.MacroPopulation;
import jmab.stockmatrix.CapitalGood;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class InvestmentCapacityProfits extends AbstractStrategy implements
		InvestmentStrategy {
	
	double profitRateWeight;
	double capacityWeight;
	int capitalStockId; //this parameter should be set in the config file equal to the the key corresponding to CapitalId in the staticValues interface.
	int pastProductionid; //the id of past production values in the matrix of passed values
	int pastCapitalId; //the id of past capital financial values in the matrix of passed values
	int pastCapacityId; //the id of past capacity values in the matrix of passed values
	int pastProfitId;
	double targetCapacityUtlization;
	
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
	 * @return the pastProductionid
	 */
	public int getPastProductionid() {
		return pastProductionid;
	}


	/**
	 * @param pastProductionid the pastProductionid to set
	 */
	public void setPastProductionid(int pastProductionid) {
		this.pastProductionid = pastProductionid;
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
	 * @return the pastCapacityId
	 */
	public int getPastCapacityId() {
		return pastCapacityId;
	}


	/**
	 * @param pastCapacityId the pastCapacityId to set
	 */
	public void setPastCapacityId(int pastCapacityId) {
		this.pastCapacityId = pastCapacityId;
	}


	/**
	 * @return the loansId
	 */
	

	/* (non-Javadoc)
	 * @see jmab.strategies.InvestmentStrategy#computeDesiredGrowth()
	 */
	@Override
	public double computeDesiredGrowth() {
		InvestmentAgent investor= (InvestmentAgent) this.getAgent();
		List<Item> capitalStock=investor.getItemsStockMatrix(true, capitalStockId);
		double capitalValue=investor.getPassedValue(pastCapitalId, 1);
		for (Item i:capitalStock){
			CapitalGood capital= (CapitalGood) i;
			if (capital.getAge()>1)
			capitalValue+=capital.getValue();
		}
		double profitRate= investor.getPassedValue(pastProfitId,1)/capitalValue;
		double pastProduction=investor.getPassedValue(pastProductionid, 1);
		double pastCapacity=investor.getPassedValue(pastCapacityId, 1);
		double desiredRateOfGrowth=profitRateWeight*profitRate+capacityWeight*((pastProduction/pastCapacity)-targetCapacityUtlization);
		return Math.max(-1, desiredRateOfGrowth);
		
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
	 * [profitRateWeight][capacityWeight][targetCapacityUtlization][capitalStockId][pastProductionid][pastCapitalId]
	 * [pastCapacityId][pastProfitId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(44);
		buf.putDouble(profitRateWeight);
		buf.putDouble(capacityWeight);
		buf.putDouble(targetCapacityUtlization);
		buf.putInt(capitalStockId);
		buf.putInt(pastProductionid);
		buf.putInt(pastCapitalId);
		buf.putInt(pastCapacityId);
		buf.putInt(pastProfitId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [profitRateWeight][capacityWeight][targetCapacityUtlization][capitalStockId][pastProductionid][pastCapitalId]
	 * [pastCapacityId][pastProfitId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.profitRateWeight = buf.getDouble();
		this.capacityWeight = buf.getDouble();
		this.targetCapacityUtlization = buf.getDouble();
		this.capitalStockId = buf.getInt();
		this.pastProductionid = buf.getInt();
		this.pastCapitalId = buf.getInt();
		this.pastCapacityId = buf.getInt();
		this.pastProfitId = buf.getInt();
	}

}
