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
public class SupplyCreditBaselIIIEndogenousTarget extends AbstractStrategy implements
SupplyCreditStrategy {

	private int depositsExpectationsId; //to be set equal to the key in static values corresponding to expectations
	//on deposits;
	private int depositsId; //"""
	private double mandatoryCAR;
	private double targetCAR;
	private int loansId;
	private int[] capitalIds;//in the simplest case cash, reserves to which we must add the net wealth
	private int[] assetsIds; //Ids of assets other than loans, if present
	private double[] capitalsWeights;
	private double[] assetsWeights;
	private int nbPeriods;
	private double threshold;
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution;
	private int lagNonPerformingLoanId;

	public void updateCARTarget (){
		CreditSupplier supplier=(CreditSupplier) this.getAgent();
		double nonPerformingLoans=0;
		double outstandingLoans=0;
		for (int i=1; i<=nbPeriods; i++){
			nonPerformingLoans+=supplier.getPassedValue(lagNonPerformingLoanId, i);
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
		for(int i=0;i<capitalIds.length;i++){
			double capitalValue=0;
			List<Item> caps = supplier.getItemsStockMatrix(true, capitalIds[i]);
			for(Item cap:caps){
				capitalValue+=cap.getValue();
			}
			capitalsValue+=capitalValue*capitalsWeights[i];
		} 
		double expectedDeposits=supplier.getExpectation(depositsExpectationsId).getExpectation();
		List<Item> currentDeposits= supplier.getItemsStockMatrix(false, depositsId);
		double currentDepositValue=0;
		for (Item i:currentDeposits){
			currentDepositValue+=i.getValue();
		}
		double expectedChangeReserves=expectedDeposits-currentDepositValue; //The variation of expected reserves equal to the variation of 
		//expected deposits.
		//if(-expectedChangeReserves>capitalsValue)expectedChangeReserves=0.01*expectedChangeReserves;
			
		capitalsValue+=expectedChangeReserves; //we consider the impact of expected variation in reserves
		//due to expected deposits variation on the capital value which enters in the CAR.

		double assetsValue=0;
		for(int i=0;i<assetsIds.length;i++){
			double assetValue=0;
			List<Item> assets = supplier.getItemsStockMatrix(true, capitalIds[i]);
			for(Item asset:assets){
				assetValue+=asset.getValue();
			}
			assetsValue+=assetValue*assetsWeights[i]; //NBnot including Loans
		}
		double desiredLoansStock=(capitalsValue/targetCAR)-assetsValue;

		List<Item> currentLoans=supplier.getItemsStockMatrix(true, loansId);
		double currentLoansStock=0;
		double expiringLoans=0;
		for (Item i:currentLoans){
			Loan loan=(Loan)i;
			currentLoansStock+=i.getValue();
			if (loan.getLength()-loan.getAge()<=1){ //the loan is going to be fully repaid
				expiringLoans+=loan.getValue();
			}
		}
		/*System.out.print(supplier.getAgentId());
		System.out.print(",");
		System.out.print(expectedDeposits);
		System.out.print(",");
		System.out.print(currentDepositValue);
		System.out.print(",");
		System.out.print(capitalsValue);
		System.out.print(",");
		System.out.print(desiredLoansStock);
		System.out.print(",");
		System.out.println(currentLoansStock);*/
		double newLoansSupply=desiredLoansStock-currentLoansStock+expiringLoans;
		return Math.max(0, newLoansSupply);
	}
	//TODO: Shouldn't we remove from the variation in reserve, the quantity of advances already taken that should disapear?


	/**
	 * @return the depositsExpectationsId
	 */
	public int getDepositsExpectationsId() {
		return depositsExpectationsId;
	}

	/**
	 * @param depositsExpectationsId the depositsExpectationsId to set
	 */
	public void setDepositsExpectationsId(int depositsExpectationsId) {
		this.depositsExpectationsId = depositsExpectationsId;
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
	 * @return the depositsId
	 */
	public int getDepositsId() {
		return depositsId;
	}


	/**
	 * @param depositsId the depositsId to set
	 */
	public void setDepositsId(int depositsId) {
		this.depositsId = depositsId;
	}


	/**
	 * @return the capitalAdequacyRatio
	 */
	public double getMandatoryCAR() {
		return mandatoryCAR;
	}


	/**
	 * @param capitalAdequacyRatio the capitalAdequacyRatio to set
	 */
	public void setMandatoryCAR(double mandatoryCAR) {
		this.mandatoryCAR = mandatoryCAR;
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
	 * @return the capitalIds
	 */
	public int[] getCapitalIds() {
		return capitalIds;
	}


	/**
	 * @param capitalIds the capitalIds to set
	 */
	public void setCapitalIds(int[] capitalIds) {
		this.capitalIds = capitalIds;
	}


	/**
	 * @return the assetsIds
	 */
	public int[] getAssetsIds() {
		return assetsIds;
	}


	/**
	 * @param assetsIds the assetsIds to set
	 */
	public void setAssetsIds(int[] assetsIds) {
		this.assetsIds = assetsIds;
	}


	/**
	 * @return the capitalsWeights
	 */
	public double[] getCapitalsWeights() {
		return capitalsWeights;
	}


	/**
	 * @param capitalsWeights the capitalsWeights to set
	 */
	public void setCapitalsWeights(double[] capitalsWeights) {
		this.capitalsWeights = capitalsWeights;
	}


	/**
	 * @return the assetsWeights
	 */
	public double[] getAssetsWeights() {
		return assetsWeights;
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
	 * @param assetsWeights the assetsWeights to set
	 */
	public void setAssetsWeights(double[] assetsWeights) {
		this.assetsWeights = assetsWeights;
	}
	
	
	/**
	 * @return the lagNonPerformingLoanId
	 */
	public int getLagNonPerformingLoanId() {
		return lagNonPerformingLoanId;
	}

	/**
	 * @param lagNonPerformingLoanId the lagNonPerformingLoanId to set
	 */
	public void setLagNonPerformingLoanId(int lagNonPerformingLoanId) {
		this.lagNonPerformingLoanId = lagNonPerformingLoanId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [mandatoryCAR][targetCAR][threshold][adaptiveParameter][depositsExpectationsId][depositsId][loansId][nbPeriods]
	 * [lagNonPerformingLoanId][nbAssets][assetsIds][assetsWeights][nbCapital][capitalIds][capitalWeights]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(60+12*(this.assetsIds.length+this.capitalsWeights.length));
		buf.putDouble(this.mandatoryCAR);
		buf.putDouble(this.targetCAR);
		buf.putDouble(this.threshold);
		buf.putDouble(this.adaptiveParameter);
		buf.putInt(this.depositsExpectationsId);
		buf.putInt(this.depositsId);
		buf.putInt(this.loansId);
		buf.putInt(this.nbPeriods);
		buf.putInt(this.lagNonPerformingLoanId);
		buf.putInt(this.assetsIds.length);
		for(int id:assetsIds)
			buf.putInt(id);
		for(double weight:assetsWeights)
			buf.putDouble(weight);
		buf.putDouble(this.capitalIds.length);
		for(int id:capitalIds)
			buf.putInt(id);
		for(double weight:capitalsWeights)
			buf.putDouble(weight);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [mandatoryCAR][targetCAR][threshold][adaptiveParameter][depositsExpectationsId][depositsId][loansId][nbPeriods]
	 * [lagNonPerformingLoanId][nbAssets][assetsIds][assetsWeights][nbCapital][capitalIds][capitalWeights]
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
		this.depositsExpectationsId = buf.getInt();
		this.depositsId = buf.getInt();
		this.loansId = buf.getInt();
		this.nbPeriods = buf.getInt();
		this.lagNonPerformingLoanId = buf.getInt();
		int nbAssets = buf.getInt();
		assetsIds=new int[nbAssets];
		assetsWeights = new double[nbAssets];
		for(int i = 0 ; i < nbAssets ; i++)
			assetsIds[i] = buf.getInt();
		for(int i = 0 ; i < nbAssets ; i++)
			assetsWeights[i] = buf.getDouble();
		int nbCapitals = buf.getInt();
		capitalIds=new int[nbCapitals];
		capitalsWeights = new double[nbCapitals];
		for(int i = 0 ; i < nbCapitals ; i++)
			capitalIds[i] = buf.getInt();
		for(int i = 0 ; i < nbCapitals ; i++)
			capitalsWeights[i] = buf.getDouble();
	}

}
