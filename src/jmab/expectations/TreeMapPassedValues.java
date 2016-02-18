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

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * @author Alessandro Caiani and Antoine Godin
 * Class that stores observed values for nbLags
 */
public class TreeMapPassedValues implements PassedValues {
	
	private SortedMap<Integer,Double> passedValues;
	private int nbLags;
	
	
	public TreeMapPassedValues(){
		passedValues = new TreeMap<Integer,Double>();
	}
	
	/**
	 * @return the number of lags stored
	 */
	public int getNbLags() {
		return nbLags;
	}
	/**
	 * @param nbLags the number of lags to set
	 */
	public void setNbLags(int nbLags) {
		this.nbLags = nbLags;
	}
	
	/**
	 * Adds an observation
	 * @param value the value of the observation
	 */
	public void addObservation(double value, int period){
		int size = passedValues.size();
		if(size==nbLags){
			passedValues.remove(passedValues.firstKey());
		}
		passedValues.put(period, value);
	}
	
	/**
	 * Gets the lagged observation
	 * @param lag the lag
	 * @return the lagged observation
	 */
	public double getObservation(int period){
		return passedValues.get(period);
	}

	/**
	 * Generates the byte array representing all the informations stored in this passed values. The structure is the following:
	 * [nbLags]
	 * for all passed values
	 * 	[lag][value]
	 * end for
	 */
	@Override
	public byte[] getByteArray() {
		byte[] result = new byte[4+12*nbLags];
		ByteBuffer buffer = ByteBuffer.wrap(result);
		buffer.putInt(nbLags);
		Set<Integer> orderedKeys = passedValues.keySet();
		for(Integer key:orderedKeys){
			buffer.putInt(key);
			buffer.putDouble(passedValues.get(key));
		}
		return result;
	}
	
	/**
	 * Populates the passed values with the byte array content, the structure is the following
	 * [nbLags]
	 * for all passed values
	 * 	[lag][value]
	 * end for
	 * @param content a byte array structure containing all relevant data necessary to populate the passed values
	 */
	public void populatePassedValues(byte[] content){
		passedValues = new TreeMap<Integer,Double>();
		ByteBuffer reader = ByteBuffer.wrap(content);
		this.nbLags = reader.getInt();
		for(int i = 0; i < nbLags ; i++){
			Integer key = reader.getInt();
			Double value = reader.getDouble();
			passedValues.put(key, value);
		}
	}
}
