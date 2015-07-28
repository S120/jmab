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

import jmab.agents.DepositDemander;
import jmab.agents.DepositSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MostPayingDepositWithSwitching extends AbstractStrategy implements
		SelectDepositSupplierStrategy {

	private DepositSupplier previousDepositSupplier;
	private SwitchingStrategy strategy;
	
	/**
	 * @return the previousDepositSupplier
	 */
	public DepositSupplier getPreviousDepositSupplier() {
		return previousDepositSupplier;
	}

	/**
	 * @param previousDepositSupplier the previousDepositSupplier to set
	 */
	public void setPreviousDepositSupplier(DepositSupplier previousDepositSupplier) {
		this.previousDepositSupplier = previousDepositSupplier;
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

	/* (non-Javadoc)
	 * @see jmab.strategies.SelectDepositSupplierStrategy#selectDepositSupplier(java.util.ArrayList, double)
	 */
	@Override
	public MacroAgent selectDepositSupplier(ArrayList<Agent> suppliers, double amount) {
		double maxIR=Double.NEGATIVE_INFINITY;
		DepositSupplier maxDepositSupplier=(DepositSupplier) suppliers.get(0);
		DepositDemander depositDemander = (DepositDemander) getAgent();
		for(Agent agent : suppliers){
			DepositSupplier depSupplier=(DepositSupplier)agent;
			double tempIR=depSupplier.getDepositInterestRate(depositDemander, amount);
			if(tempIR>maxIR){
				maxIR=tempIR;
				maxDepositSupplier=depSupplier;
			}
		}
		
		double previousIR=0;
		if (!previousDepositSupplier.isDead()){
			previousIR=previousDepositSupplier.getDepositInterestRate(depositDemander, amount);
		}
		else{
			previousIR=Double.NEGATIVE_INFINITY;
		}
		
		if(previousIR<maxIR){
			if(previousIR==Double.NEGATIVE_INFINITY||strategy.switches(previousIR,maxIR)){
				previousDepositSupplier=maxDepositSupplier;
				//System.out.println("switching");
			}
		}
		return previousDepositSupplier;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [popId][previousDepositSupplierId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(21);
		buf.putInt(this.previousDepositSupplier.getPopulationId());
		buf.putLong(this.previousDepositSupplier.getAgentId());
		return buf.array();
	}
	
	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [popId][previousDepositSupplierId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		Collection<Agent> aHolders = pop.getPopulation(buf.getInt()).getAgents();
		long depositSupplierId = buf.getLong(); 
		for(Agent a:aHolders){
			MacroAgent pot = (MacroAgent) a;
			if(pot.getAgentId()==depositSupplierId){
				this.previousDepositSupplier=(DepositSupplier) pot;
				break;
			}
		}
		
	}
	
}
