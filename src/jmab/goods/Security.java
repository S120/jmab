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

import jmab.agents.SimpleAbstractAgent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 * Class representing securities holding
 */
public class Security extends AbstractItem implements Item {

	private double price;
	
	/**
	 * 
	 */
	public Security() {
	}

	/**
	 * @param value the value of the securities
	 * @param quantity the number of securities held
	 * @param assetHolder the holder of securities
	 * @param liabilityHolder the emitter of securities
	 * @param price the price of the securities
	 */
	public Security(double value, double quantity, SimpleAbstractAgent assetHolder,
			SimpleAbstractAgent liabilityHolder, double price) {
		super(value, quantity, assetHolder, liabilityHolder);
		this.price=price;
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getAge()
	 */
	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

}
