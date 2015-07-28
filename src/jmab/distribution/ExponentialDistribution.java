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
package jmab.distribution;

import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;

import org.springframework.beans.factory.annotation.Required;

import cern.jet.random.Exponential;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class ExponentialDistribution extends AbstractDelegatedDistribution {

	
	
	protected double lambda;
	/**
	 * 
	 */
	public ExponentialDistribution() {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.distribution.AbstractDelegatedDistribution#initialise()
	 */
	@Override
	public void initialise() {
		this.delegate=new Exponential(lambda,prng);
	}
	
	@Required
	public void setLambda(double lambda){
		this.lambda=lambda;
		reinitialise();
	}
	
	public double getLambda(){
		return lambda;
	}

}
