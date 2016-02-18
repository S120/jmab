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

import jmab.agents.AbstractBank;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Item;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MaxLossCreditStrategy extends AbstractStrategy implements
		SpecificCreditSupplyStrategy {

	private DefaultProbilityComputer defaultComputer;
	private ShareOfColateralLossComputer lossComputer;
	private double maxLoss;
	private int loansId;
	private boolean lowerSupply;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.SpecificCreditSupplyStrategy#computeSpecificSupply(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double computeSpecificSupply(MacroAgent creditDemander,
			double required) {
		AbstractBank creditSupplier= (AbstractBank) this.getAgent();
		double netWealth = creditSupplier.getNetWealth();
		double totalExposure=0;
		for (Item loan:creditSupplier.getItemsStockMatrix(true, loansId)){
			if (loan.getLiabilityHolder().getAgentId()==creditDemander.getAgentId()){
				totalExposure+=loan.getValue();
			}
		}
		totalExposure+=required;
		double probability = defaultComputer.getDefaultProbability(creditDemander, creditSupplier, required);
		double shareLoss = lossComputer.getShareLoss(creditSupplier);
		double creditGranted = 0;
		if(probability*totalExposure*shareLoss<maxLoss*netWealth)
			creditGranted=required;
		else{
			if(lowerSupply)
				creditGranted=Math.max(0, (maxLoss*netWealth)/(probability*shareLoss)-totalExposure);
		}
		return creditGranted;
	}

	/**
	 * @return the maxLoss
	 */
	public double getMaxLoss() {
		return maxLoss;
	}

	/**
	 * @param maxLoss the maxLoss to set
	 */
	public void setMaxLoss(double maxLoss) {
		this.maxLoss = maxLoss;
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
	 * @return the defaultComputer
	 */
	public DefaultProbilityComputer getDefaultComputer() {
		return defaultComputer;
	}

	/**
	 * @param defaultComputer the defaultComputer to set
	 */
	public void setDefaultComputer(DefaultProbilityComputer defaultComputer) {
		this.defaultComputer = defaultComputer;
	}

	/**
	 * @return the lossComputer
	 */
	public ShareOfColateralLossComputer getLossComputer() {
		return lossComputer;
	}

	/**
	 * @param lossComputer the lossComputer to set
	 */
	public void setLossComputer(ShareOfColateralLossComputer lossComputer) {
		this.lossComputer = lossComputer;
	}

	/**
	 * @return the lowerSupply
	 */
	public boolean isLowerSupply() {
		return lowerSupply;
	}

	/**
	 * @param lowerSupply the lowerSupply to set
	 */
	public void setLowerSupply(boolean lowerSupply) {
		this.lowerSupply = lowerSupply;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [maxLoss][loansId][lowerSupply][defaultComputerSize][defaultComputerStructure][lossComputerSize][lossComputerStructure]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		byte[] defComputerStructure = this.defaultComputer.getBytes();
		byte[] lossComputerStructure = this.lossComputer.getBytes();
		ByteBuffer buf = ByteBuffer.allocate(21+defComputerStructure.length+lossComputerStructure.length);
		buf.putDouble(maxLoss);
		buf.putInt(loansId);
		if(lowerSupply)
			buf.put((byte)1);
		else
			buf.put((byte)0);
		buf.putInt(defComputerStructure.length);
		buf.put(defComputerStructure);
		buf.putInt(lossComputerStructure.length);
		buf.put(lossComputerStructure);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [maxLoss][loansId][lowerSupply][defaultComputerSize][defaultComputerStructure][lossComputerSize][lossComputerStructure]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.maxLoss = buf.getDouble();
		this.loansId = buf.getInt();
		this.lowerSupply=buf.get()==(byte)1;
		int sizeDefComputer = buf.getInt();
		byte[] defComputerStructure = new byte[sizeDefComputer];
		buf.get(defComputerStructure);
		this.defaultComputer.populateFromBytes(defComputerStructure, pop);
		int sizeLossComputer = buf.getInt();
		byte[] lossComputerStructure = new byte[sizeLossComputer];
		buf.get(lossComputerStructure);
		this.lossComputer.populateFromBytes(lossComputerStructure, pop);
	}
}
