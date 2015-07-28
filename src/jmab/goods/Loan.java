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
 * Class representing loans
 */
public class Loan extends AbstractItem implements Item {

	private double interestRate;
	private int amortization;
	private int length;
	private double initialAmount;
	
	public static final int FIXED_CAPITAL=0;
	public static final int FIXED_AMOUNT=1;
	public static final int ONLY_INTERESTS=2;
			
	/**
	 * 
	 */
	public Loan() {
	}

	/**
	 * @param value the amount of the loan
	 * @param assetHolder the borrower
	 * @param liabilityHolder the lender
	 * @param interestRate the interest rate charged on the loan
	 * @param maturity the maturity of the loan
	 * @param amortization
	 */
	public Loan(double value, MacroAgent assetHolder,
			MacroAgent liabilityHolder, double interestRate, int loanAge, int amortization, int length) {
		super(value, value, assetHolder, liabilityHolder);
		this.interestRate=interestRate;
		this.age=loanAge;
		this.amortization=amortization;
		this.length=length;
		this.initialAmount=value;
	}

	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Super Structure + 24bytes in total):
	 * [SuperStructure][amortization][length][interestRate][initialAmount]
	 * @param content
	 * @param population
	 */
	public Loan(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
		ByteBuffer reader = ByteBuffer.wrap(content);
		reader.position(content.length-24);
		this.amortization = reader.getInt();
		this.length = reader.getInt();
		this.interestRate = reader.getDouble();
		this.initialAmount = reader.getDouble();
		
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
	 * @return the maturity
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param maturity the maturity to set
	 */
	public void setAge(int maturity) {
		this.age = maturity;
	}

	/**
	 * @return the amortization
	 */
	public int getAmortization() {
		return amortization;
	}

	/**
	 * @param amortization the amortization to set
	 */
	public void setAmortization(int amortization) {
		this.amortization = amortization;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the initialAmount
	 */
	public double getInitialAmount() {
		return initialAmount;
	}

	/**
	 * @param initialAmount the initialAmount to set
	 */
	public void setInitialAmount(double initialAmount) {
		this.initialAmount = initialAmount;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#remove()
	 */
	@Override
	public boolean remove() {
		return this.value==0||this.age>this.length;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {
		this.age+=1;
	}

	
	@Override
	public void setValue(double value){
		this.value=value;
		this.quantity=value;
	}
	
	@Override
	public void setQuantity(double quantity){
		this.value=quantity;
		this.quantity=quantity;
	}
	
	/**
	 * generates the byte array representation of the item. The structure of the array is the following (Super structure + 24 bytes in total):
	 * [SuperStructure][amortization][length][interestRate][initialAmount]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		byte[] prevResult = super.getBytes();
		byte[] result = new byte[prevResult.length+24];
		ByteBuffer buffer = ByteBuffer.wrap(result).put(prevResult);
		buffer.putInt(this.amortization);
		buffer.putInt(this.length);
		buffer.putDouble(this.interestRate);
		buffer.putDouble(this.initialAmount);
		return result;
	}
}
