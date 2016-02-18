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

import jmab.agents.MacroAgent;
import jmab.agents.SimpleAbstractAgent;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 * Class that represent cash
 */
public class Cash extends AbstractItem implements Item {

	/**
	 * 
	 */
	public Cash() {
	}
	
	/**
	 * @param value the amount of cash held
	 * @param assetHolder the agent holding cash
	 * @param liabilityHolder the emitter of cash (usually the central bank)
	 */
	public Cash(double value, SimpleAbstractAgent assetHolder,
			SimpleAbstractAgent liabilityHolder) {
		super(value, value, assetHolder, liabilityHolder);
	}

	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following:
	 * [SuperStructure]
	 * @param content
	 * @param population
	 */
	public Cash(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
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
	public void update() {
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getAge()
	 */
	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * generates the byte array representation of the item. The structure of the array is the following:
	 * [SuperStructure]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		return super.getBytes();
	}
}
