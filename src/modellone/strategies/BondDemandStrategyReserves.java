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
import java.util.List;

import jmab.agents.BondSupplier;
import jmab.agents.MacroAgent;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.strategies.BondDemandStrategy;
import modellone.StaticValues;
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
public class BondDemandStrategyReserves extends AbstractStrategy implements BondDemandStrategy{
	
	private double liquidityRatio;

	/**
	 * @return the liquidityRatio
	 */
	public double getLiquidityRatio() {
		return liquidityRatio;
	}

	/**
	 * @param liquidityRatio the liquidityRatio to set
	 */
	public void setLiquidityRatio(double liquidityRatio) {
		this.liquidityRatio = liquidityRatio;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.BondDemandStrategy#BondDemand(double)
	 */
	@Override
	public int bondDemand(BondSupplier supplier) {
		Bank bank = (Bank) getAgent();
		SimulationController controller = (SimulationController)bank.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(bank.getPopulationId());
		double employableReserves=0;
		double bankDeposits=0;
		for(Agent b:banks.getAgents()){
			double depositsValue=0;
			MacroAgent tempB = (MacroAgent) b;
			List<Item> deposits = bank.getItemsStockMatrix(false, StaticValues.SM_DEP);
			for(Item i:deposits){
				depositsValue+=i.getValue();
				}
			if (tempB==bank){
				bankDeposits=depositsValue;
			}
			employableReserves+=Math.max(0, tempB.getItemStockMatrix(true, StaticValues.SM_RESERVES).getValue()-liquidityRatio*depositsValue);
			}
		double bondsPrice= supplier.getBondPrice();
		if (employableReserves/bondsPrice>=supplier.getBondSupply()){
			return (int) Math.rint(supplier.getBondSupply()*Math.max(0, bank.getItemStockMatrix(true, StaticValues.SM_RESERVES).getValue()-liquidityRatio*bankDeposits)/employableReserves);
		}
		else{
			return (int) Math.rint(Math.max(0, bank.getItemStockMatrix(true, StaticValues.SM_RESERVES).getValue()-liquidityRatio*bankDeposits)/bondsPrice);
		}
	}
	
	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [liquidityRatio]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putDouble(this.liquidityRatio);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [liquidityRatio]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.liquidityRatio = buf.getDouble();
	}

}
