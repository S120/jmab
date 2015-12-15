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

import jmab.agents.BaselIIIAgent;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * The CAR of Basel III is defined as capital/weighted assets. In most simple cases capital is reserves (cash + desposits at the CB)
 * plus net wealth. Assets are instead Loans (whose weight is 100%), Mortgage Loans (weight 50%)and other assets (weight 100%) 
 * different from cash (weight 0), government bonds (weight 0). 
 */
@SuppressWarnings("serial")
public class BaselIIIReserveRequirements extends AbstractStrategy implements
		FinanceStrategy {

	private int[] capitalIds; //in the simplest case cash, reserves to which we must add the net wealth
	private double[] capitalsWeights;
	private int[] assetsIds; //in the simplest case only loans (bonds, reserves and cash are weighted 0)
	private double[] assetsWeights;
	private int reserveId;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.FinanceStrategy#computeCreditDemand(double)
	 */
	@Override
	public double computeCreditDemand(double expectedFinancialRequirement) {
		BaselIIIAgent bank = (BaselIIIAgent)this.agent;
		double capitalAdequacyRatio = bank.getTargetedCapitalAdequacyRatio();
		double capitalsValue=bank.getNetWealth();
		/*for(int i=0;i<capitalIds.length;i++){
			double capitalValue=0;
			List<Item> caps = bank.getItemsStockMatrix(true, capitalIds[i]);
			for(Item cap:caps){
				capitalValue+=cap.getValue();
			}
			capitalsValue+=capitalValue*capitalsWeights[i];
		}
		*/ 
		double assetsValue=0;
		for(int i=0;i<assetsIds.length;i++){
			double assetValue=0;
			List<Item> assets = bank.getItemsStockMatrix(true, assetsIds[i]);
			for(Item asset:assets){
				assetValue+=asset.getValue();
			}
			assetsValue+=assetValue*assetsWeights[i];
		}
		return capitalAdequacyRatio*assetsValue-capitalsValue;
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
	 * @return the reserveId
	 */
	public int getReserveId() {
		return reserveId;
	}

	/**
	 * @param reserveId the reserveId to set
	 */
	public void setReserveId(int reserveId) {
		this.reserveId = reserveId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [capitalAdequacyRatio][reserveId][assetsSize][assetsIds][assetsWeights][capitalSize][capitalIds][capitalWeights]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(12+12*(assetsIds.length+capitalIds.length));
		buf.putDouble(this.reserveId);
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
	 * [capitalAdequacyRatio][reserveId][assetsSize][assetsIds][assetsWeights][capitalSize][capitalIds][capitalWeights]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.reserveId = buf.getInt();
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
