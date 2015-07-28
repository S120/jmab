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
import java.util.ArrayList;
import java.util.List;

import jmab.agents.AbstractBank;
import jmab.agents.MacroAgent;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.strategies.BankruptcyStrategy;
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
public class BankBankruptcy extends AbstractStrategy implements
		BankruptcyStrategy {
	
	private double[] haircut;
	private int liabilityHaircutId; 
	
	/* (non-Javadoc)
	 * @see jmab.strategies.BankruptcyStrategy#bankrupt()
	 */
	@Override
	public void bankrupt() {
		
		AbstractBank bank = (AbstractBank) getAgent();
		SimulationController controller = (SimulationController)bank.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(bank.getPopulationId());
		AbstractBank richestBank = bank;
		double highestNW = bank.getNetWealth();
		for(Agent b:banks.getAgents()){
			MacroAgent tempB = (MacroAgent) b;
			if(highestNW<tempB.getNetWealth()){
				highestNW=tempB.getNetWealth();
				richestBank=(Bank)tempB;
			}
		}
		double assetsValue=0;
		double liabilitiesValue=0;
		double liabHaircutTotalValue=0;
		for(int i = 0; i<bank.getStocksNames().size();i++){
			List<Item> assets = bank.getItemsStockMatrix(true, i);
			for(Item asset:assets){
				assetsValue+=asset.getValue()*haircut[i];
				if(i!=StaticValues.SM_CASH&&i!=StaticValues.SM_RESERVES){
						asset.setAssetHolder(richestBank);
						richestBank.addItemStockMatrix(asset, true, i);
				}else{
					Item rAsset = richestBank.getItemStockMatrix(true, i);
					rAsset.setValue(rAsset.getValue()+asset.getValue()*haircut[i]);
					asset.getLiabilityHolder().removeItemStockMatrix(asset, false, i);
					asset.setValue(0);
				}
			}
			List<Item> liabilities = bank.getItemsStockMatrix(false, i);
			if(i!=this.liabilityHaircutId){
				for(Item liability:liabilities){
					liabilitiesValue+=liability.getValue();
					liability.setLiabilityHolder(richestBank);
					richestBank.addItemStockMatrix(liability, false, i);
				}
			}else{
				for(Item liability:liabilities){
					liabilitiesValue+=liability.getValue();
					liabHaircutTotalValue+=liability.getValue();
				}
			}
		}
		double loss = liabilitiesValue-assetsValue;
		List<Item> liabilities = bank.getItemsStockMatrix(false, this.liabilityHaircutId);
		for(Item liability:liabilities){
			liability.setValue(liability.getValue()-loss*liability.getValue()/liabHaircutTotalValue);
			liability.setLiabilityHolder(richestBank);
			richestBank.addItemStockMatrix(liability, false, this.liabilityHaircutId);
		}
		List<List<Item>> assets = new ArrayList<List<Item>>();
		for(int i=0;i<bank.getAssetStock().size();i++){
			assets.add(new ArrayList<Item>());
		}
		bank.setAssetStock(assets);  
		bank.setLiabilityStock(assets);
	}

	/**
	 * @return the haircut
	 */
	public double[] getHaircut() {
		return haircut;
	}

	/**
	 * @param haircut the haircut to set
	 */
	public void setHaircut(double[] haircut) {
		this.haircut = haircut;
	}

	/**
	 * @return the liabilityHaricutId
	 */
	public int getLiabilityHaircutId() {
		return liabilityHaircutId;
	}

	/**
	 * @param liabilityHaircutId the liabilityHaricutId to set
	 */
	public void setLiabilityHaircutId(int liabilityHaircutId) {
		this.liabilityHaircutId = liabilityHaircutId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [liabilityHaircutId][nbhaircut][haircut]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8+8*(this.haircut.length));
		buf.putInt(this.liabilityHaircutId);
		buf.putInt(this.haircut.length);
		for(double id:haircut)
			buf.putDouble(id);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [liabilityHaircutId][nbhaircut][haircut]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.liabilityHaircutId = buf.getInt();
		int nbhaircut = buf.getInt();
		haircut = new double[nbhaircut];
		for(int i = 0 ; i < nbhaircut ; i++)
			haircut[i] = buf.getDouble();
	}
	
	
}
