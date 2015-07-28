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
public class FixedShareOfColateralLossComputer implements
		ShareOfColateralLossComputer {

	private double shareLoss;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.ShareOfColateralLossComputer#getShareLoss(jmab.agents.MacroAgent)
	 */
	@Override
	public double getShareLoss(MacroAgent cresitSupplier) {
		return shareLoss;
	}

	/**
	 * @return the shareLoss
	 */
	public double getShareLoss() {
		return shareLoss;
	}

	/**
	 * @param shareLoss the shareLoss to set
	 */
	public void setShareLoss(double shareLoss) {
		this.shareLoss = shareLoss;
	}
	
	/**
	 * Generate the byte array structure of the computer. The structure is as follow:
	 * [shareLoss]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.putDouble(shareLoss);
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
		this.shareLoss = buf.getDouble();
	}

}
