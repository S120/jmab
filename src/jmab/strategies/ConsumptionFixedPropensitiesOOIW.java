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

import jmab.agents.AbstractHousehold;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class ConsumptionFixedPropensitiesOOIW extends AbstractStrategy
		implements ConsumptionStrategy {
	double propensityOOW;
	double propensityOOI;
	int consPriceExpectationID; //in the config file will be specified as equal to the corresponding static field. 

	
	/**
	 * @return the propensityOOW
	 */
	public double getPropensityOOW() {
		return propensityOOW;
	}


	/**
	 * @param propensityOOW the propensityOOW to set
	 */
	public void setPropensityOOW(double propensityOOW) {
		this.propensityOOW = propensityOOW;
	}


	/**
	 * @return the propensityOOI
	 */
	public double getPropensityOOI() {
		return propensityOOI;
	}


	/**
	 * @param propensityOOI the propensityOOI to set
	 */
	public void setPropensityOOI(double propensityOOI) {
		this.propensityOOI = propensityOOI;
	}


	/**
	 * @return the consPriceExpectationID
	 */
	public int getConsPriceExpectationID() {
		return consPriceExpectationID;
	}


	/**
	 * @param consPriceExpectationID the consPriceExpectationID to set
	 */
	public void setConsPriceExpectationID(int consPriceExpectationID) {
		this.consPriceExpectationID = consPriceExpectationID;
	}


	/* (non-Javadoc)
	 * @see jmab.strategies.ConsumptionStrategy#computeRealConsumptionDemand()
	 */
	@Override
	public double computeRealConsumptionDemand() {
		AbstractHousehold household= (AbstractHousehold) this.getAgent(); 
		double priceExpectation=household.getExpectation(consPriceExpectationID).getExpectation();
		return propensityOOI*(household.getNetIncome()/priceExpectation)+propensityOOW*(household.getNetWealth()/priceExpectation);
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [propensityOOW][propensityOOI][consPriceExpectationID]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.putDouble(this.propensityOOW);
		buf.putDouble(this.propensityOOI);
		buf.putInt(this.consPriceExpectationID);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [propensityOOW][propensityOOI][consPriceExpectationID]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.propensityOOW = buf.getDouble();
		this.propensityOOI = buf.getDouble();
		this.consPriceExpectationID = buf.getInt();
	}
	
}
