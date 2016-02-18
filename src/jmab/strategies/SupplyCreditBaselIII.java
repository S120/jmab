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
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 */
@SuppressWarnings("serial")
public class SupplyCreditBaselIII extends AbstractStrategy implements
		SupplyCreditStrategy {
	
	int depositsExpectationsId; //to be set equal to the key in static values corresponding to expectations on deposits;
	int depositsId; //"""
	double capitalAdequacyRatio;
	int loansId;
	private int[] capitalIds;//in the simplest case cash, reserves to which we must add the net wealth
	private int[] assetsIds; //Ids of assets other than loans, if present
	private double[] capitalsWeights;
	private double[] assetsWeights;

	
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
	public double getCapitalAdequacyRatio() {
		return capitalAdequacyRatio;
	}


	/**
	 * @param capitalAdequacyRatio the capitalAdequacyRatio to set
	 */
	public void setCapitalAdequacyRatio(double capitalAdequacyRatio) {
		this.capitalAdequacyRatio = capitalAdequacyRatio;
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
	 * @param assetsWeights the assetsWeights to set
	 */
	public void setAssetsWeights(double[] assetsWeights) {
		this.assetsWeights = assetsWeights;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.SupplyCreditStrategy#computeCreditSupply()
	 */
	@Override
	public double computeCreditSupply() {
		CreditSupplier supplier=(CreditSupplier) this.getAgent();
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
		List<Item> currentDeposits= supplier.getItemsStockMatrix(true, depositsId);
		double currentDepositValue=0;
		for (Item i:currentDeposits){
			currentDepositValue+=i.getValue();
		}
		double expectedChangeReserves=expectedDeposits-currentDepositValue; //The variation of expected reserves equal to the variation of 
		//expected deposits.
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
		double desiredLoansStock=(capitalsValue/capitalAdequacyRatio)-assetsValue;
		
		
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
		
	double newLoansSupply=desiredLoansStock-currentLoansStock+expiringLoans;
	return newLoansSupply;
	}
//TODO: Shouldn't we remove from the variation in reserve, the quantity of advances already taken that should disapear?
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [capitalAdequacyRatio][depositsExpectationsId][depositsId][loansId][nbAssets][assetsIds][assetsWeights]
	 * [nbCapital][capitalIds][capitalWeights]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(28+12*(this.assetsIds.length+this.capitalsWeights.length));
		buf.putDouble(this.capitalAdequacyRatio);
		buf.putInt(this.depositsExpectationsId);
		buf.putInt(this.depositsId);
		buf.putInt(this.loansId);
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
	 * [capitalAdequacyRatio][depositsExpectationsId][depositsId][loansId][nbAssets][assetsIds][assetsWeights]
	 * [nbCapital][capitalIds][capitalWeights]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.capitalAdequacyRatio = buf.getDouble();
		this.depositsExpectationsId = buf.getInt();
		this.depositsId = buf.getInt();
		this.loansId = buf.getInt();
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
