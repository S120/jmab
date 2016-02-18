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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import jmab.agents.CreditDemander;
import jmab.agents.CreditSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class CheapestLenderWithSwitching extends AbstractStrategy implements
		SelectLenderStrategy {

	private CreditSupplier previousLender;
	private SwitchingStrategy strategy;
	// created this private integer: 
	private int idLoanSM;
	
	/**
	 * 
	 */
	public CheapestLenderWithSwitching() {
		super();
		this.previousLender=null;
	}

	/**
	 * @param agent
	 */
	public CheapestLenderWithSwitching(Agent agent) {
		super(agent);
		this.previousLender=null;
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public CheapestLenderWithSwitching(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
		this.previousLender=null;
	}

	/**
	 * @return the strategy
	 */
	public SwitchingStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(SwitchingStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * @return the previousLender
	 */
	public CreditSupplier getPreviousLender() {
		return previousLender;
	}
	
	/* (non-Javadoc)
	 * @see jmab.strategies.BorrowingStrategyWithSwitching#setPreviousLender(jmab.agents.CreditSupplier)
	 */
	public void setPreviousLender(CreditSupplier counterpart) {
		this.previousLender=counterpart;

	}

	/* (non-Javadoc)
	 * @see jmab.strategies.BorrowingStrategy#selectLender(java.util.ArrayList, double, int)
	 */
	/* (non-Javadoc)
	 * @see jmab.strategy.BorrowingStrategy#selectLender(java.util.ArrayList)
	 */
	@Override
	public Agent selectLender(ArrayList<Agent> lenders,double amount, int length) {
		double minRate=Double.POSITIVE_INFINITY;
		CreditSupplier minLender=(CreditSupplier) lenders.get(0);
		CreditDemander CreditDemander = (CreditDemander) getAgent();
		for(Agent agent : lenders){
			CreditSupplier lender=(CreditSupplier)agent;
			double tempRate=lender.getInterestRate(idLoanSM, CreditDemander,amount, length);
			if(tempRate<minRate){
				minRate=tempRate;
				minLender=lender;
			}
		}
		double previousRate=0;
		if(!previousLender.isDead()&&
				previousLender.getLoanSupply(this.idLoanSM,CreditDemander, amount)>0
				&&previousLender.isActive(((MacroSimulation)((SimulationController)this.scheduler).getSimulation()).getActiveMarketId())){
			previousRate=previousLender.getInterestRate(idLoanSM, CreditDemander, amount, length);
		}else{
			previousRate=Double.POSITIVE_INFINITY;
		}
		if(previousRate>minRate){
			if(previousRate==Double.POSITIVE_INFINITY||strategy.switches(previousRate, minRate)){
				previousLender=minLender;
			}
		}
		return previousLender;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [popId][previousLenderId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(21);
		buf.putInt(this.previousLender.getPopulationId());
		buf.putLong(this.previousLender.getAgentId());
		return buf.array();
	}
	
	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [popId][previousGoodSupplierId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		Collection<Agent> aHolders = pop.getPopulation(buf.getInt()).getAgents();
		long lenderId = buf.getLong(); 
		for(Agent a:aHolders){
			MacroAgent pot = (MacroAgent) a;
			if(pot.getAgentId()==lenderId){
				this.previousLender=(CreditSupplier) pot;
				break;
			}
		}
		
	}

	public int getIdLoanSM() {
		return idLoanSM;
	}

	public void setIdLoanSM(int idLoanSM) {
		this.idLoanSM = idLoanSM;
	}

}
