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

import jmab.agents.WageSetterWithTargets;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class AdaptiveWageStrategy extends AbstractStrategy implements
		WageStrategy {
	
	private double microThreshold; //to be set through the configuration file.
	private double macroThreshold;
	private double microAdaptiveParameter;
	private double macroAdaptiveParameter;
	protected AbstractDelegatedDistribution distribution;

	/* (non-Javadoc)
	 * @see jmab.strategies.WageStrategy#computeWage()
	 */
	@Override
	public double computeWage() {
		WageSetterWithTargets worker = (WageSetterWithTargets)getAgent();
		double microReferenceVariable= worker.getMicroReferenceVariableForWage();
		double wage = worker.getWage();
		if(microReferenceVariable>microThreshold){
			wage-=(microAdaptiveParameter*wage*distribution.nextDouble());
		}else{
			double macroReferenceVariable= worker.getMacroReferenceVariableForWage();
			if(macroReferenceVariable<=macroThreshold)
				wage+=(macroAdaptiveParameter*wage*distribution.nextDouble());
		}
		return Math.max(wage, worker.getWageLowerBound());
	}

	/**
	 * @return the microThreshold
	 */
	public double getMicroThreshold() {
		return microThreshold;
	}

	/**
	 * @param microThreshold the microThreshold to set
	 */
	public void setMicroThreshold(double microThreshold) {
		this.microThreshold = microThreshold;
	}

	/**
	 * @return the macroThreshold
	 */
	public double getMacroThreshold() {
		return macroThreshold;
	}

	/**
	 * @param macroThreshold the macroThreshold to set
	 */
	public void setMacroThreshold(double macroThreshold) {
		this.macroThreshold = macroThreshold;
	}

	/**
	 * @return the microAdaptiveParameter
	 */
	public double getMicroAdaptiveParameter() {
		return microAdaptiveParameter;
	}

	/**
	 * @param microAdaptiveParameter the microAdaptiveParameter to set
	 */
	public void setMicroAdaptiveParameter(double microAdaptiveParameter) {
		this.microAdaptiveParameter = microAdaptiveParameter;
	}

	/**
	 * @return the macroAdaptiveParameter
	 */
	public double getMacroAdaptiveParameter() {
		return macroAdaptiveParameter;
	}

	/**
	 * @param macroAdaptiveParameter the macroAdaptiveParameter to set
	 */
	public void setMacroAdaptiveParameter(double macroAdaptiveParameter) {
		this.macroAdaptiveParameter = macroAdaptiveParameter;
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
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [macroThreshold][microThreshold][macroAdaptiveParameter][microAdaptiveParameter]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(32);
		buf.putDouble(this.macroThreshold);
		buf.putDouble(this.microThreshold);
		buf.putDouble(this.macroAdaptiveParameter);
		buf.putDouble(this.microAdaptiveParameter);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [macroThreshold][microThreshold][macroAdaptiveParameter][microAdaptiveParameter]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.macroThreshold = buf.getDouble();
		this.microThreshold = buf.getDouble();
		this.macroAdaptiveParameter= buf.getDouble();
		this.microAdaptiveParameter= buf.getDouble();
	}

}
