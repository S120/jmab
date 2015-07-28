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

import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class BadDebtEvent extends SimEvent {

	private double amount;
	private int marketId;
	private int time;

	/**
	 * @param amount
	 * @param marketId
	 */
	public BadDebtEvent(double amount, int marketId, int time) {
		super();
		this.amount = amount;
		this.marketId = marketId;
		this.time=time;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the marketId
	 */
	public int getMarketId() {
		return marketId;
	}

	/**
	 * @param marketId the marketId to set
	 */
	public void setMarketId(int marketId) {
		this.marketId = marketId;
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
