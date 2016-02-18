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
package jmab.stockmatrix;

import java.nio.ByteBuffer;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */

/**
 * @param value the value of the capital good in the BS
 * @param assetHolder the agent who has the capital good
 * @param liabilityHolder null since there is no liability side for real good
 * @param price the price of the capital good 
 * @param productivity the productivity of the capital good
 * @param capitalDuration the number of periods after which the real capital good disappear (stop working in the production process)
 * @param capitalAmortization the number of periods in which the capital is fully depreciated from an accounting point of view (affects the value in the BS)
 */
public class CapitalGood extends AbstractGood{
	
	private double productivity;
	private int capitalDuration;
	private int capitalAmortization;
	private double capitalLaborRatio;
	private boolean obsolete;


	/**
	 * 
	 */
	public CapitalGood() {
		super();
	}

    /**
     * 
     * @param value
     * @param quantity
     * @param assetHolder
     * @param producer the producer of the good; substitutes liabilityHolder: being a real good we don't have a liability side
     * @param price
     * @param productivity
     * @param capitalDuration
     * @param capitalAmortization
     * @param capitalLaborRatio TODO
     */
	public CapitalGood(double value, double quantity, MacroAgent assetHolder, 
			MacroAgent producer, double price, double productivity, 
			int capitalDuration, int capitalAmortization, double capitalLaborRatio) {
		super(value, quantity, assetHolder, null);
		this.producer=producer;
		this.price=price;
		this.productivity=productivity;
		this.capitalDuration=capitalDuration;
		this.capitalAmortization=capitalAmortization;
		this.capitalLaborRatio=capitalLaborRatio;
		this.age=-1;
	}	
	
	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Super Structure + 24bytes in total):
	 * [SuperStructure][capitalDuration][capitalAmortization][productivity][capitalLaborRatio]
	 * @param content
	 * @param population
	 */
	public CapitalGood(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
		ByteBuffer reader = ByteBuffer.wrap(content);
		reader.position(content.length-24);
		this.capitalDuration= reader.getInt();
		this.capitalAmortization = reader.getInt();
		this.productivity = reader.getDouble();
		this.capitalLaborRatio = reader.getDouble();
		this.obsolete = false;
	}
	
	/**
	 * @return the obsolete
	 */
	public boolean isObsolete() {
		return obsolete;
	}

	/**
	 * @param obsolete the obsolete to set
	 */
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}

	/**
	 * @return the productivity
	 */
	public double getProductivity() {
		return productivity;
	}

	/**
	 * @param productivity the productivity to set
	 */
	public void setProductivity(double productivity) {
		this.productivity = productivity;
	}

	/**
	 * @return the capitalDuration
	 */
	public int getCapitalDuration() {
		return capitalDuration;
	}

	/**
	 * @param capitalDuration the capitalDuration to set
	 */
	public void setCapitalDuration(int capitalDuration) {
		this.capitalDuration = capitalDuration;
	}


	/**
	 * @return the capitalAmortization
	 */
	public int getCapitalAmortization() {
		return capitalAmortization;
	}


	/**
	 * @param capitalAmortization the capitalAmortization to set
	 */
	public void setCapitalAmortization(int capitalAmortization) {
		this.capitalAmortization = capitalAmortization;
	}

	/**
	 * @return the capitalLaborRatio
	 */
	public double getCapitalLaborRatio() {
		return capitalLaborRatio;
	}

	/**
	 * @param capitalLaborRatio the capitalLaborRatio to set
	 */
	public void setCapitalLaborRatio(double capitalLaborRatio) {
		this.capitalLaborRatio = capitalLaborRatio;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#remove()
	 */
	@Override
	public boolean remove() {
		return obsolete||age==capitalDuration;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {
		super.update();
		updateValue();
	}
	
	@Override
	public void setQuantity(double quantity) {
		super.setQuantity(quantity);
		updateValue();
	}

	private void updateValue(){
		double ageFactor = age;
		ageFactor=ageFactor/((double) this.capitalAmortization);
		this.value=this.price*this.quantity*(1-ageFactor);
	}
	
	@Override
	public double getValue(){
		if(this.assetHolder.getAgentId()==this.producer.getAgentId()){
			return this.unitCost*this.quantity;
		}else
			return this.value;
	}
	

	/**
	 * generates the byte array representation of the item. The structure of the array is the following (Super structure + 24 bytes in total):
	 * [SuperStructure][capitalDuration][capitalAmortization][productivity][capitalLaborRatio]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		byte[] prevResult = super.getBytes();
		byte[] result = new byte[prevResult.length+24];
		ByteBuffer buffer = ByteBuffer.wrap(result).put(prevResult);
		buffer.putInt(this.capitalDuration);
		buffer.putInt(this.capitalAmortization);
		buffer.putDouble(this.productivity);
		buffer.putDouble(this.capitalLaborRatio);
		return result;
	}
	
}
