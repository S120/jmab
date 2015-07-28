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

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class DeterministicLogisticLeverageDefaultProbabilityComputer implements
		DefaultProbilityComputer {
	
	private double slopeParameter;
	private double centerParameter;

	/* (non-Javadoc)
	 * @see jmab.strategies.DefaultProbilityComputer#getDefaultProbability()
	 */
	@Override
	public double getDefaultProbability(MacroAgent creditDemander, MacroAgent creditSupplier, double demanded) {
		
		double leverage = 0;
		double[][] bs = creditDemander.getNumericBalanceSheet();
		double assets = 0;
		double liabilities = 0;
		for(int i =0 ; i<bs[0].length-1;i++){
			assets+=bs[0][i];
			liabilities+=bs[0][i];
		}
		leverage = liabilities/assets;
		return 1/(1+Math.exp(-slopeParameter*(leverage-centerParameter)));
	}

	/**
	 * @return the slopeParameter
	 */
	public double getSlopeParameter() {
		return slopeParameter;
	}

	/**
	 * @param slopeParameter the slopeParameter to set
	 */
	public void setSlopeParameter(double slopeParameter) {
		this.slopeParameter = slopeParameter;
	}

	/**
	 * @return the centerParameter
	 */
	public double getCenterParameter() {
		return centerParameter;
	}

	/**
	 * @param centerParameter the centerParameter to set
	 */
	public void setCenterParameter(double centerParameter) {
		this.centerParameter = centerParameter;
	}

	/**
	 * Generate the byte array structure of the computer. The structure is as follow:
	 * [slopeParameter][centerParameter]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putDouble(slopeParameter);
		buf.putDouble(centerParameter);
		return buf.array();
	}

	/**
	 * Populates the computer from the byte array content. The structure should be as follows:
	 * [slopeParameter][centerParameter]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.slopeParameter = buf.getDouble();
		this.centerParameter = buf.getDouble();
	}

	
}
