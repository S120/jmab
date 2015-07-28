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
package jmab.expectations;

/**
 * Interface that represent the expectation object. For now, we assume expectation to be of the
 * form Xe = Xebar + psi(Xebar-Xbar) where Xebar and Xbar are weighted sum of passed values of
 * expectations (Xebar) or observed values (Xbar). An expectation is thus composed of the 
 * parameter psi, the number of periods on which doing the weighted sum, the weights and of the 
 * past observations and expectations.
 * 
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface Expectation {
	
	/**
	 * @return the weights
	*/
	public double[][] getWeights();
	
	/**
	 * set the weights
	 * @param weights
	 */
	public void setWeights(double[][] weights);

	/**
	 * @return all passed values (expectations and observations)
	 */
	public double[][] getPassedValues();
	
	/**
	 * To be used only when initializing the object
	 * @param passedValues the passed values (expectations and observations)
	 */
	public void setPassedValues(double[][] passedValues);
	
	/**
	 * @return the number of periods over which is made the expectation
	 */
	public int getNumberPeriod();
	
	/**
	 * @param nbPeriod the number of period over which is made the expectation
	 */
	public void setNumberPeriod(int nbPeriod);
	
	/**
	 * @return the adaptive parameter
	 */
	
	public void updateExpectation();
	
	/**
	 * @return the epxectation
	 */
	public double getExpectation();
	
	
	/**
	 * @param observation the vector of observations to be added to the matrix of passed values
	 */
	public void addObservation(double[] observation);

	/**
	 * Generate the byte array representation of the expectation
	 * @return
	 */
	public byte[] getByteArray();
	
	/**
	 * Populates the expectation with the byte array content
	 * @param content a byte array structure containing all relevant data necessary to populate the expectation
	 */
	public void populateExpectation(byte[] content);
	
}
