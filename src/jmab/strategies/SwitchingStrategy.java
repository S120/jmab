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

import jmab.population.MacroPopulation;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface SwitchingStrategy{

	/**
	 * @param prng
	 */
	void setPrng(RandomEngine prng);

	/**
	 * @param params
	 * @return
	 */
	boolean switches(double previous, double potential);
	
	public byte[] getBytes();
	
	public void populateFromBytes(byte[] content, MacroPopulation pop);

}
