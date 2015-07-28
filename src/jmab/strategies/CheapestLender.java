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
package jmab.strategies;

import java.util.ArrayList;

import jmab.agents.CreditDemander;
import jmab.agents.CreditSupplier;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class CheapestLender extends AbstractStrategy implements
		SelectLenderStrategy {

	private int loansId;

	/**
	 * 
	 */
	public CheapestLender() {
	}

	/**
	 * @param agent
	 */
	public CheapestLender(Agent agent) {
		super(agent);
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public CheapestLender(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
	}

	/* (non-Javadoc)
	 * @see jmab.strategy.BorrowingStrategy#selectLender(java.util.ArrayList)
	 */
	@Override
	public Agent selectLender(ArrayList<Agent> lenders,double amount, int length) {
		double minRate=Double.POSITIVE_INFINITY;
		CreditSupplier minLender=(CreditSupplier) lenders.get(0);
		for(Agent lender : lenders){
			double tempRate=((CreditSupplier)lender).getInterestRate(loansId, (CreditDemander)agent,amount, length);
			if(tempRate<minRate){
				minRate=tempRate;
				minLender=(CreditSupplier)lender;
			}
		}
		return minLender;
	}
	
	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1];//TODO cannot be null and probably not byte[0].
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
	}

	public int getLoansId() {
		return loansId;
	}

	public void setLoansId(int loansId) {
		this.loansId = loansId;
	}

}
