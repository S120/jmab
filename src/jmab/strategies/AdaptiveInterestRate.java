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

import jmab.agents.InterestRateSetterWithTargets;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class AdaptiveInterestRate extends AbstractStrategy implements
		InterestRateStrategy {

	private double threshold; //to be set through the configuration file.
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution; 
	private boolean increase;
	private int mktId;

	/* (non-Javadoc)
	 * @see jmab.strategies.InterestRateStrategy#computeInterestRate(jmab.agents.MacroAgent, double, int)
	 */
	@Override
	public double computeInterestRate(MacroAgent creditDemander, double amount,
			int length) {
		InterestRateSetterWithTargets lender=(InterestRateSetterWithTargets) this.getAgent();
		double referenceVariable= lender.getReferenceVariableForInterestRate(mktId);
		double iR = lender.getInterestRate(mktId);
		if(referenceVariable>threshold){
			if(increase)
				iR+=(adaptiveParameter*iR*distribution.nextDouble());
			else
				iR-=(adaptiveParameter*iR*distribution.nextDouble());
		}else{
			if(increase)
				iR-=(adaptiveParameter*iR*distribution.nextDouble());
			else
				iR+=(adaptiveParameter*iR*distribution.nextDouble());
		}
		return Math.min(Math.max(iR, lender.getInterestRateLowerBound(mktId)),lender.getInterestRateUpperBound(mktId));
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
	 * @return the increase
	 */
	public boolean isIncrease() {
		return increase;
	}

	/**
	 * @param increase the increase to set
	 */
	public void setIncrease(boolean increase) {
		this.increase = increase;
	}


	/**
	 * @return the mkId
	 */
	public int getMktId() {
		return mktId;
	}


	/**
	 * @param mkId the mkId to set
	 */
	public void setMktId(int mktId) {
		this.mktId = mktId;
	}


	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][mktId][increase]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(21);
		buf.putDouble(threshold);
		buf.putDouble(adaptiveParameter);
		buf.putInt(mktId);
		if(increase)
			buf.put((byte)1);
		else
			buf.put((byte)0);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][mktId][increase]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.mktId = buf.getInt();
		this.increase=buf.get()==(byte)1;
	}
}
