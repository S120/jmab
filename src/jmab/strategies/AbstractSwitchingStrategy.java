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

import cern.jet.random.Binomial;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public abstract class AbstractSwitchingStrategy implements SwitchingStrategy {

	protected Binomial distribution;
	protected RandomEngine prng;
	protected final int n=1;
	/**
	 * 
	 */
	public AbstractSwitchingStrategy() {
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SwitchingStrategy#setPrng(cern.jet.random.engine.RandomEngine)
	 */
	@Override
	public void setPrng(RandomEngine prng) {
		this.prng=prng;
		distribution=new Binomial(n, 0.5, prng);
	}

	/**
	 * @return the prng
	 */
	public RandomEngine getPrng() {
		return prng;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SwitchingStrategy#switches(double, double)
	 */
	@Override
	public boolean switches(double previous, double potential){
		/*if(previous==Double.POSITIVE_INFINITY){
			System.out.println(previous);
		}
		if(potential==Double.POSITIVE_INFINITY){
			System.out.println(potential);
		}
		if(potential<=0){
			System.out.println(potential);
		}
		if(previous<=0){
			System.out.println(previous);
		}
		*/
		double proba = getProbability(previous,potential);
		if(proba==1.0){
			return true;
		}else if(proba==0.0){
			return false;
		}else{
			return distribution.nextInt(n,proba)==1;
		}
	}

	/**
	 * @param previous
	 * @param potential
	 * @return
	 */
	abstract protected double getProbability(double previous, double potential);	
	
}
