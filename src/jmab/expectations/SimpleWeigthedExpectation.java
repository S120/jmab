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

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class SimpleWeigthedExpectation implements Expectation {

	private double[][] passedValues;
	private int nbPeriod;
	private int nbVariables;
	private double expectation;
	private double[][] weights;



	/**
	 * 
	 */
	public SimpleWeigthedExpectation() {
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#getWeights()
	 */
	@Override
	public double[][] getWeights() {
		return this.weights;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#setWeights(double[][])
	 */
	@Override
	public void setWeights(double[][] weights) {
		this.weights=weights;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#getPassedValues()
	 */
	@Override
	public double[][] getPassedValues() {
		return this.passedValues;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#setPassedValues(double[][])
	 */
	@Override
	public void setPassedValues(double[][] passedValues) {
		this.passedValues=passedValues;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#getNumberPeriod()
	 */
	@Override
	public int getNumberPeriod() {
		return this.nbPeriod;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#setNumberPeriod(int)
	 */

	@Override
	public void setNumberPeriod(int nbPeriod) {
		this.nbPeriod=nbPeriod;
		this.passedValues= new double [nbPeriod][];
		// The weights matrix instead is injected thrhough the .xml configuration file
	}


	/**
	 * @return the nbVariables
	 */
	public int getNbVariables() {
		return nbVariables;
	}

	/**
	 * @param nbVariables the nbVariables to set
	 */
	public void setNbVariables(int nbVariables) {
		this.nbVariables=nbVariables;
		for (int i=0; i<nbPeriod; i++){
			this.passedValues[i]= new double [this.nbVariables+1]; //+1 because we also put past expectations
			//  The weights matrix instead is injected thrhough the .xml configuration file
		}


	}

	/**
	 * @param expectation the expectation to set
	 */
	public void setExpectation(double expectation) {
		this.expectation = expectation;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#getAdaptiveParam()
	 */


	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#updateExpectation()
	 */
	@Override
	public void updateExpectation() {
		double result=0;
		for(int i=0; i<nbPeriod-1;i++){
			for(int j=0;j<nbVariables;j++){
				result+=weights[i][j]*passedValues[i][j];
			}
		}
		this.expectation=result;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#getExpectation()
	 */
	@Override
	public double getExpectation() {
		return this.expectation;
	}

	/* (non-Javadoc)
	 * @see jmab.expectations.Expectation#addObservation(double[])
	 */
	@Override
	public void addObservation(double[] observation) {
		for(int i=0;i<nbPeriod-1;i++){
			for(int j=0;j<nbVariables+1;j++){
				this.passedValues[i][j]=this.passedValues[i+1][j];
			}
		}
		for(int j=0;j<nbVariables;j++){
			this.passedValues[0][j]=observation[j];
		}
		this.passedValues[0][nbVariables]=this.expectation;
	}
	
	/**
	 * Generates the byte array representing all the informations stored in this expectation. The structure is the following:
	 * [nbPeriod][nbVariables][expectation][passedValues][weights]
	 */
	@Override
	public byte[] getByteArray() {
		byte[] result = new byte[16+16*(nbPeriod+(nbVariables+1))];
		ByteBuffer buffer = ByteBuffer.wrap(result);
		buffer.putInt(nbPeriod);
		buffer.putInt(nbVariables);
		buffer.putDouble(expectation);
		for(int i = 0; i<passedValues.length ; i++){
			for(int j = 0; j<=passedValues[i].length ; j++){
				buffer.putDouble(passedValues[i][j]);
			}
		}
		for(int i = 0; i<weights.length ; i++){
			for(int j = 0; j<=weights[i].length ; j++){
				buffer.putDouble(weights[i][j]);
			}
		}
		return result;
	}

	/**
	 * Populates the expectation with the byte array content, the structure is the following
	 * [nbPeriod][nbVariables][expectation][passedValues][weights]
	 * @param content a byte array structure containing all relevant data necessary to populate the expectation
	 */
	public void populateExpectation(byte[] content){
		ByteBuffer reader = ByteBuffer.wrap(content);
		this.nbPeriod = reader.getInt();
		this.nbVariables = reader.getInt();
		this.expectation = reader.getDouble();
		this.passedValues = new double[nbPeriod][nbVariables+1];
		this.weights = new double[nbPeriod][nbVariables+1];
		for(int i = 0; i<passedValues.length ; i++){
			for(int j = 0; j<=passedValues[i].length ; j++){
				passedValues[i][j]=reader.getDouble();
			}
		}
		for(int i = 0; i<weights.length ; i++){
			for(int j = 0; j<=weights[i].length ; j++){
				weights[i][j]=reader.getDouble();
			}
		}
	}

}
