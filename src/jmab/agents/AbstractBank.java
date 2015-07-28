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
package jmab.agents;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import jmab.goods.Cash;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractBank extends SimpleAbstractAgent implements CreditSupplier, 
	DepositSupplier{

	protected List<MacroAgent> employees;
	protected int depositCounterpartId;
	protected double currentNonPerformingLoans;

	/**
	 * 
	 */
	public AbstractBank() {
		super();
		this.employees = new ArrayList<MacroAgent>();
		
	}

	/**
	 * @return the employees
	 */
	public List<MacroAgent> getEmployees() {
		return employees;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setEmployees(List<MacroAgent> employees) {
		this.employees = employees;
	}
	
	/**
	 * @return the depositCounterpartId
	 */
	public int getDepositCounterpartId() {
		return depositCounterpartId;
	}

	/**
	 * @param depositCounterpartId the depositCounterpartId to set
	 */
	public void setDepositCounterpartId(int depositCounterpartId) {
		this.depositCounterpartId = depositCounterpartId;
	}

	@Override
	public void transfer(Item paying, Item receiving, double amount){
		MacroAgent otherBank = receiving.getLiabilityHolder();
		Item otherBalancingItem = otherBank.getItemStockMatrix(true, this.depositCounterpartId);
		Item balancingItem = this.getItemStockMatrix(true, this.depositCounterpartId);
		paying.setValue(paying.getValue()-amount);
		balancingItem.setValue(balancingItem.getValue()-amount);
		receiving.setValue(receiving.getValue()+amount);
		otherBalancingItem.setValue(otherBalancingItem.getValue()+amount);
	}
	
	@Override
	public Item getCounterpartItem(Item liability, Item otherLiability){
		if(liability instanceof Deposit){
			//Bank case
			if(otherLiability instanceof Cash){
				//Returns the cash holdings of this bank
				return this.getItemStockMatrix(true, otherLiability.getSMId());
			}else if(otherLiability instanceof Deposit){
				//Reserve to deposit or deposit to reserve case
				return this.getItemStockMatrix(true, otherLiability.getSMId());
			}
		}else if(liability instanceof Cash){
			//Central Bank case
			if(otherLiability instanceof Deposit){
				//Returns the cash holdings of the bank that emitted the deposit
				return this.getItemStockMatrix(false, liability.getSMId(), otherLiability.getLiabilityHolder());
			}
		}
		return null;
	}

	/**
	 * @return the currentNonPerformingLoans
	 */
	public double getCurrentNonPerformingLoans(int loansId) {
		return currentNonPerformingLoans;
	}

	/**
	 * @param currentNonPerformingLoans the currentNonPerformingLoans to set
	 */
	public void setCurrentNonPerformingLoans(int loansId, double currentNonPerformingLoans) {
		this.currentNonPerformingLoans = currentNonPerformingLoans;
	}
		
	/**
	 * Generates the byte array representing the characteristics of the agent. The structure is the following
	 * [superStructSize][superStruct][currentNonPerformingLoans][depositCounterPartId]
	 * @return the byte array
	 */
	public byte[] getAgentCharacteristicsBytes(){
		byte[] superStruct = super.getAgentCharacteristicsBytes();
		ByteBuffer buf = ByteBuffer.allocate(superStruct.length+16);
		buf.putInt(superStruct.length);
		buf.put(superStruct);
		buf.putDouble(this.currentNonPerformingLoans);
		buf.putInt(this.depositCounterpartId);
		return buf.array();
	}

	/**
	 * Populates the characteristics of the agent using the byte array content. The structure is the following
	 * [superStructSize][superStruct][currentNonPerformingLoans][depositCounterPartId]
	 * @param the byte array
	 */
	public void populateCharacteristics(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int superStrucSize = buf.getInt();
		byte[] superStruct = new byte[superStrucSize];
		buf.get(superStruct);
		super.populateCharacteristics(superStruct, pop);
		this.currentNonPerformingLoans = buf.getDouble();
		this.depositCounterpartId = buf.getInt();
	}
}
