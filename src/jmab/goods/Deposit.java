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
 * Class representing a deposit (current) account
 */
public class Deposit extends AbstractItem implements Item {

	private double interestRate;
	
	/**
	 * 
	 */
	public Deposit() {
	}

	/**
	 * @param value the amount of money stored in the account
	 * @param assetHolder the holder of the account
	 * @param liabilityHolder the bank supplying the account
	 * @param interestRate the interest rate paid on the account
	 */
	public Deposit(double value, MacroAgent  assetHolder,
			MacroAgent liabilityHolder, double interestRate ) {
		super(value, value, assetHolder, liabilityHolder);
		this.interestRate=interestRate;
	}
	
	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Super Structure + 8bytes in total):
	 * [SuperStructure][interestRate]
	 * @param content
	 * @param population
	 */
	public Deposit(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
		ByteBuffer reader = ByteBuffer.wrap(content);
		reader.position(content.length-8);
		this.interestRate = reader.getDouble();
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

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#setValue(double)
	 */
	@Override
	public void setValue(double value) {
		this.quantity=value;
		this.value=value;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#setQuantity(double)
	 */
	@Override
	public void setQuantity(double quantity) {
		this.quantity=quantity;
		this.value=quantity;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#remove()
	 */
	@Override
	public boolean remove() {
		return false;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getAge()
	 */
	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * generates the byte array representation of the item. The structure of the array is the following (Super structure + 24 bytes in total):
	 * [SuperStructure][interestRate]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		byte[] prevResult = super.getBytes();
		byte[] result = new byte[prevResult.length+8];
		ByteBuffer buffer = ByteBuffer.wrap(result).put(prevResult);
		buffer.putDouble(this.interestRate);
		return result;
	}
	
}
