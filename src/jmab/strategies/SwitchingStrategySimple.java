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

import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 * According to this strategy the probability of switching (e.g. to a new lender, to a new bank deposit etc.)is a non linear 
 * function of the difference between the two prices (or rate of interests) charged/paid by the former counterpart and the 
 * new potential counterpart.
 */
public class SwitchingStrategySimple extends AbstractSwitchingStrategy {
	
	double lambda; // should be set positive regardless is a payment (e.g. loans) or a receipt (e.g. deposits).
	boolean payOrReceive; //should be set true if is a payment (loans, goods) and false if a receipt (deposits)

	
	/**
	 * @return the lambda
	 */
	public double getLambda() {
		return lambda;
	}


	/**
	 * @param lambda the lambda to set
	 */
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	
	public void setThresholdMean(double threshold){
		this.lambda = -Math.log(0.5)/threshold;
	}
	
	public double getThresholdMean(){
		return -Math.log(0.5)/this.lambda;
	}

	/**
	 * @return the payOrReceive
	 */
	public boolean isPayOrReceive() {
		return payOrReceive;
	}


	/**
	 * @param payOrReceive the payOrReceive to set
	 */
	public void setPayOrReceive(boolean payOrReceive) {
		this.payOrReceive = payOrReceive;
	}

	
	/* (non-Javadoc)
	 * @see jmab.strategies.AbstractSwitchingStrategy#getProbability(double, double)
	 */
	@Override
	protected double getProbability(double previous, double potential) {
		if (payOrReceive==true){
			if(potential<previous)
				return 1-Math.exp(lambda*(potential-previous)/potential);
			else
				return 0;
		}
		else{
			if(previous<potential)
				return 1-Math.exp(lambda*(previous-potential)/previous);
			else
				return 0;
		}
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [lambda][payOrReceive]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.putDouble(lambda);
		if(payOrReceive)
			buf.put((byte)1);
		else
			buf.put((byte)0);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [wealthTaxRate][profitTaxRate][depositId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.lambda = buf.getDouble();
		this.payOrReceive = buf.get()==((byte)1);
	}
	
}
