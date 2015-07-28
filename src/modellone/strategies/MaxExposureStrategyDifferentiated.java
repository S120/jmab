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

import java.nio.ByteBuffer;

import jmab.agents.AbstractBank;
import jmab.agents.MacroAgent;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.strategies.SpecificCreditSupplyStrategy;
import modellone.StaticValues;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MaxExposureStrategyDifferentiated extends AbstractStrategy
		implements SpecificCreditSupplyStrategy {

	double maxShareTotalLoans;

	int loansId;
	

	/**
	 * @return the maxShareTotalLoans
	 */
	public double getMaxShareTotalLoans() {
		return maxShareTotalLoans;
	}

	/**
	 * @param maxShareTotalLoans the maxShareTotalLoans to set
	 */
	public void setMaxShareTotalLoans(double maxShareTotalLoans) {
		this.maxShareTotalLoans = maxShareTotalLoans;
	}

	/**
	 * @return the loansId
	 */
	public int getLoansId() {
		return loansId;
	}

	/**
	 * @param loansId the loansId to set
	 */
	public void setLoansId(int loansId) {
		this.loansId = loansId;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SpecificCreditSupplyStrategy#computeSpecificSupply(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double computeSpecificSupply(MacroAgent creditDemander,
			double required) {
		AbstractBank creditSupplier= (AbstractBank) this.getAgent();
		double totalExposure=0;
		double exposureToCreditDemander=0;
		for (Item loan:creditSupplier.getItemsStockMatrix(true, loansId)){
			if (loan.getAge()!=0){
			totalExposure+=loan.getValue();
			}
			if (loan.getLiabilityHolder().getAgentId()==creditDemander.getAgentId()){
				exposureToCreditDemander+=loan.getValue();
			}
		}
		SimulationController sim=(SimulationController) this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population kPop = macroPop.getPopulation(StaticValues.CAPITALFIRMS_ID);
		Population cPop = macroPop.getPopulation(StaticValues.CONSUMPTIONFIRMS_ID);
		if (creditDemander.getPopulationId()==StaticValues.CAPITALFIRMS_ID){
			return Math.max(0,(totalExposure*maxShareTotalLoans*kPop.getSize()/cPop.getSize())-exposureToCreditDemander);
		}
		else{
		return Math.max(0,(totalExposure*maxShareTotalLoans)-exposureToCreditDemander);
		}
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [maxShareTotalLoans][loansId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(12);
		buf.putDouble(this.maxShareTotalLoans);
		buf.putInt(this.loansId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [maxShareTotalLoans][loansId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.maxShareTotalLoans = buf.getDouble();
		this.loansId = buf.getInt();
	}

}
