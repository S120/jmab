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
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface PassedValues {

	/**
	 * Return the observation for period period.
	 * @param period
	 * @return
	 */
	public double getObservation(int period);
	
	/**
	 * Adds the observation value for period period
	 * @param value
	 * @param period
	 */
	public void addObservation(double value, int period);
	
	/**
	 * Generate the byte array representation of the passed values
	 * @return
	 */
	public byte[] getByteArray();
	
	/**
	 * Populates the passed values with the byte array content
	 * @param content a byte array structure containing all relevant data necessary to populate the passed values
	 */
	public void populateExpectation(byte[] content);
}
