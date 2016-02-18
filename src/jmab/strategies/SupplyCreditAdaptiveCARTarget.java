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

import jmab.agents.CreditSupplier;
import jmab.population.MacroPopulation;
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class SupplyCreditAdaptiveCARTarget extends AbstractStrategy implements
		SupplyCreditStrategy {


	private double mandatoryCAR;
	private double targetCAR;
	private int loansId;
	private int nbPeriods;
	private double threshold;
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution;
	private int lagNonPerformingLoansId;
	private int lagBankTotalLoanSupplyId;
	
	public void updateCARTarget (){
		CreditSupplier supplier=(CreditSupplier) this.getAgent();
		double nonPerformingLoans=0;
		double outstandingLoans=0;
		for (int i=1; i<=nbPeriods; i++){
			nonPerformingLoans+=supplier.getPassedValue(lagNonPerformingLoansId, i);
		}
		List<Item> loans = supplier.getItemsStockMatrix(true, loansId);
		for (Item loan:loans){
			outstandingLoans+=loan.getValue();
		}
		if (nonPerformingLoans/outstandingLoans>threshold){
			targetCAR+=adaptiveParameter*targetCAR*distribution.nextDouble();
		}
		else {
			targetCAR-=adaptiveParameter*targetCAR*distribution.nextDouble();
		}
		if (targetCAR<mandatoryCAR){
			targetCAR=mandatoryCAR;
		}

	}
	
	/* (non-Javadoc)
	 * @see jmab.strategies.SupplyCreditStrategy#computeCreditSupply()
	 */
	@Override
	public double computeCreditSupply() {
		CreditSupplier supplier=(CreditSupplier) this.getAgent();
		updateCARTarget();
		
		double capitalsValue=supplier.getNetWealth();
		/*for(int i=0;i<capitalIds.length;i++){
			double capitalValue=0;
			List<Item> caps = supplier.getItemsStockMatrix(true, capitalIds[i]);
			for(Item cap:caps){
				capitalValue+=cap.getValue();
			}
			capitalsValue+=capitalValue*capitalsWeights[i];
		} 
	*/
		double assetsValue=0;
		List<Item> loans = supplier.getItemsStockMatrix(true, loansId);
		for(Item asset:loans){
			Loan loan=(Loan)asset;
			assetsValue+=loan.getValue();
		}
		
		 double currentCar=capitalsValue/assetsValue;
		 //System.out.println(currentCar);
		 double lastCreditSupply=supplier.getPassedValue(lagBankTotalLoanSupplyId, 1);
		 if (currentCar>=targetCAR){
			 return lastCreditSupply*(1+adaptiveParameter*distribution.nextDouble());
		 }
		 else {
			 return lastCreditSupply*(1-adaptiveParameter*distribution.nextDouble());
		 }
		/*for(int i=0;i<assetsIds.length;i++){
			double assetValue=0;
			List<Item> assets = supplier.getItemsStockMatrix(true, capitalIds[i]);
			for(Item asset:assets){
				assetValue+=asset.getValue();
			}
			assetsValue+=assetValue*assetsWeights[i]; 
		}
		*/
	}
	
	/**
	 * @return the mandatoryCAR
	 */
	public double getMandatoryCAR() {
		return mandatoryCAR;
	}


	/**
	 * @param mandatoryCAR the mandatoryCAR to set
	 */
	public void setMandatoryCAR(double mandatoryCAR) {
		this.mandatoryCAR = mandatoryCAR;
	}


	/**
	 * @return the targetCAR
	 */
	public double getTargetCAR() {
		return targetCAR;
	}


	/**
	 * @param targetCAR the targetCAR to set
	 */
	public void setTargetCAR(double targetCAR) {
		this.targetCAR = targetCAR;
	}


	/**
	 * @return the nbPeriods
	 */
	public int getNbPeriods() {
		return nbPeriods;
	}


	/**
	 * @param nbPeriods the nbPeriods to set
	 */
	public void setNbPeriods(int nbPeriods) {
		this.nbPeriods = nbPeriods;
	}


	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}


	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
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
	 * @return the distribution
	 */
	public AbstractDelegatedDistribution getDistribution() {
		return distribution;
	}


	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribution(AbstractDelegatedDistribution distribution) {
		this.distribution = distribution;
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
	 * @return the lagNonPerformingLoansId
	 */
	public int getLagNonPerformingLoansId() {
		return lagNonPerformingLoansId;
	}

	/**
	 * @param lagNonPerformingLoansId the lagNonPerformingLoansId to set
	 */
	public void setLagNonPerformingLoansId(int lagNonPerformingLoansId) {
		this.lagNonPerformingLoansId = lagNonPerformingLoansId;
	}

	/**
	 * @return the lagBankTotalLoanSupplyId
	 */
	public int getLagBankTotalLoanSupplyId() {
		return lagBankTotalLoanSupplyId;
	}

	/**
	 * @param lagBankTotalLoanSupplyId the lagBankTotalLoanSupplyId to set
	 */
	public void setLagBankTotalLoanSupplyId(int lagBankTotalLoanSupplyId) {
		this.lagBankTotalLoanSupplyId = lagBankTotalLoanSupplyId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [mandatoryCAR][targetCAR][threshold][adaptiveParameter][loansId][nbPeriods][lagNonPerformingLoansId][lagBankTotalLoanSupplyId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(48);
		buf.putDouble(this.mandatoryCAR);
		buf.putDouble(this.targetCAR);
		buf.putDouble(this.threshold);
		buf.putDouble(this.adaptiveParameter);
		buf.putInt(this.loansId);
		buf.putInt(this.nbPeriods);
		buf.putInt(this.lagNonPerformingLoansId);
		buf.putInt(this.lagBankTotalLoanSupplyId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [mandatoryCAR][targetCAR][threshold][adaptiveParameter][loansId][nbPeriods][lagNonPerformingLoansId][lagBankTotalLoanSupplyId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.mandatoryCAR = buf.getDouble();
		this.targetCAR = buf.getDouble();
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.loansId = buf.getInt();
		this.nbPeriods = buf.getInt();
		this.lagNonPerformingLoansId = buf.getInt();
		this.lagBankTotalLoanSupplyId = buf.getInt();
	}
	
}
