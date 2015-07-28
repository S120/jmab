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
package jmab.events;

import jmab.agents.GoodDemander;
import jmab.agents.GoodSupplier;
import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class TransactionOccuredEvent extends SimEvent {

	private double quantity;
	private double price;
	private int idMarket;
	private int time;
	private GoodDemander GoodDemander;
	private GoodSupplier GoodSupplier;
	
	/**
	 * @param j 
	 * @param i 
	 * @param price2 
	 * @param quantity2 
	 
	 * 
	 */
	public TransactionOccuredEvent(double quantity2, double price2, int i, int j) {
		super();
	}

	/**
	 * @param quantity
	 * @param price
	 * @param idMarket
	 * @param GoodSupplier 
	 * @param GoodDemander
	 */
	public TransactionOccuredEvent(GoodDemander GoodDemander, GoodSupplier GoodSupplier, double quantity, double price, int idMarket, int time) {
		super();
		this.quantity = quantity;
		this.price = price;
		this.idMarket = idMarket;
		this.time=time;
		this.GoodDemander=GoodDemander;
		this.GoodSupplier=GoodSupplier;
		
	}

	/**
	 * @return the GoodDemander
	 */
	public GoodDemander getGoodDemander() {
		return GoodDemander;
	}

	/**
	 * @param GoodDemander the GoodDemander to set
	 */
	public void setGoodDemander(GoodDemander GoodDemander) {
		this.GoodDemander = GoodDemander;
	}

	/**
	 * @return the GoodSupplier
	 */
	public GoodSupplier getGoodSupplier() {
		return GoodSupplier;
	}

	/**
	 * @param GoodSupplier the GoodSupplier to set
	 */
	public void setGoodSupplier(GoodSupplier GoodSupplier) {
		this.GoodSupplier = GoodSupplier;
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
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

	/**
	 * @return the idMarket
	 */
	public int getIdMarket() {
		return idMarket;
	}

	/**
	 * @param idMarket the idMarket to set
	 */
	public void setIdMarket(int idMarket) {
		this.idMarket = idMarket;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	
	
}
