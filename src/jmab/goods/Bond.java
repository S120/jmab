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
package jmab.goods;

import java.nio.ByteBuffer;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 * Class that represent a bond.
 */
public class Bond extends AbstractItem implements Item {

	private int maturity;
	private double interestRate;
	private double price;
	
	/**
	 * 
	 */
	public Bond() {
	}

	/**
	 * @param value the value of the bond
	 * @param quantity the quantity of bonds
	 * @param assetHolder the holder of the bond
	 * @param liabilityHolder the issuer of the bond
	 * @param maturity the maturity of the bond
	 * @param interestRate the interest rate of the bond
	 * @param price the price of the bond
	 */
	public Bond(double value, double quantity, MacroAgent assetHolder,
			MacroAgent liabilityHolder, int maturity, double interestRate, double price) {
		super(value, quantity, assetHolder, liabilityHolder);
		this.interestRate=interestRate;
		this.maturity=maturity;
		this.price=price;
		this.age=0;
	}
	
	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Super Structure + 20bytes in total):
	 * [SuperStructure][maturity][price][interestRate]
	 * @param content
	 * @param population
	 */
	public Bond(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
		ByteBuffer reader = ByteBuffer.wrap(content);
		reader.position(content.length-24);
		this.maturity = reader.getInt();
		this.price = reader.getDouble();
		this.interestRate = reader.getDouble();
	}

	@Override
	public double getValue(){
		if(this.liabilityHolder==this.assetHolder)
			return 0;
		else
			return this.quantity*this.price;
	}
	
	/**
	 * @return the maturity
	 */
	public int getMaturity() {
		return maturity;
	}

	/**
	 * @param maturity the maturity to set
	 */
	public void setMaturity(int maturity) {
		this.maturity = maturity;
	}

	/**
	 * @return the interestRate
	 */
	public double getInterestRate() {
		return interestRate;
	}

	/**
	 * @param interestRate the interestRate to set
	 */
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#remove()
	 */
	@Override
	public boolean remove() {
		return maturity<age;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {
		age+=1;
	}

	/**
	 * generates the byte array representation of the item. The structure of the array is the following (Super structure + 20 bytes in total):
	 * [SuperStructure][maturity][price][interestRate]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		byte[] prevResult = super.getBytes();
		byte[] result = new byte[prevResult.length+24];
		ByteBuffer buffer = ByteBuffer.wrap(result).put(prevResult);
		buffer.putInt(this.maturity);
		buffer.putDouble(this.price);
		buffer.putDouble(this.interestRate);
		return result;
	}
}
