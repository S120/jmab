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
package modellone.strategies;

import jmab.agents.BondSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.strategies.BondDemandStrategy;
import modellone.agents.Bank;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class FullBondDemandStrategy extends AbstractStrategy implements BondDemandStrategy{

	/* (non-Javadoc)
	 * @see jmab.strategies.BondDemandStrategy#BondDemand(double)
	 */
	@Override
	public int bondDemand(BondSupplier supplier) {
		Bank bank = (Bank) getAgent();
		SimulationController controller = (SimulationController)bank.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(bank.getPopulationId());
		double totalWealth=0;
		for(Agent b:banks.getAgents()){
			MacroAgent tempB = (MacroAgent) b;
			totalWealth+=tempB.getNetWealth();
		}
		return (int) Math.rint(supplier.getBondSupply()*bank.getNetWealth()/totalWealth);
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
