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
package jmab.mechanisms;

import jmab.agents.CreditDemander;
import jmab.agents.CreditSupplier;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class CreditRelation {

	private CreditDemander CreditDemander;
	private CreditSupplier CreditSupplier;
	private double interestRate;
	private double amount;
	private int length;
	private int period;
	private int irSetting;
	private int amortization;
	
	//Different amortization methods
	public static final int FIXED_CAPITAL=0;
	public static final int FIXED_AMOUNT=1;
	public static final int ONLY_INTERESTS=2;
	
	//Different interest rate settings
	public static final int FIXED_IR=0;
	public static final int FLOATING_IR=1;
	/**
	 * @param CreditDemander
	 * @param CreditSupplier
	 * @param interestRate
	 * @param amount
	 * @param length
	 */
	
	public CreditRelation(CreditDemander CreditDemander, CreditSupplier CreditSupplier, double interestRate, 
			double amount, int length, int irSetting, int amortization) {
		super();
		this.CreditDemander = CreditDemander;
		this.CreditSupplier = CreditSupplier;
		this.interestRate = interestRate;
		this.amount = amount;
		this.length = length;
		this.period = 0;
		this.irSetting = irSetting;
		this.amortization = amortization;
	}
	/**
	 * @return the CreditDemander
	 */
	public CreditDemander getCreditDemander() {
		return CreditDemander;
	}
	/**
	 * @param CreditDemander the CreditDemander to set
	 */
	public void setCreditDemander(CreditDemander CreditDemander) {
		this.CreditDemander = CreditDemander;
	}
	/**
	 * @return the CreditSupplier
	 */
	public CreditSupplier getCreditSupplier() {
		return CreditSupplier;
	}
	/**
	 * @param CreditSupplier the CreditSupplier to set
	 */
	public void setCreditSupplier(CreditSupplier CreditSupplier) {
		this.CreditSupplier = CreditSupplier;
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
	 * @return the irSetting
	 */
	public int getIrSetting() {
		return irSetting;
	}
	/**
	 * @param irSetting the irSetting to set
	 */
	public void setIrSetting(int irSetting) {
		this.irSetting = irSetting;
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
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public void update(){
		this.period+=1;
	}
}
