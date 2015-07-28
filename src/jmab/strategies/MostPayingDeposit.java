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
public class MostPayingDeposit extends AbstractStrategy implements
		SelectDepositSupplierStrategy {

	
	
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
		return maxDepositSupplier;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1];//TODO
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {}

}
