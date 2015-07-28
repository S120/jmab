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

import java.util.List;

import jmab.expectations.Expectation;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.RoundFinishedEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface MacroAgent extends Agent, EventListener{
	
	public boolean isActive(int marketId);
	
	public void setActive(boolean active, int marketId);

	public void onRoundFinished(RoundFinishedEvent event);
	
	public void initialiseCounterpart(Agent counterpart, int marketID);
	
	public long getAgentId();
	
	public List<Item> getItemsStockMatrix(boolean asset, int idItem, MacroAgent holder);
	
	public List<Item> getItemsStockMatrix(boolean asset, int idItem);
	
	public Item getItemStockMatrix(boolean asset, int idStock, MacroAgent holder);
	
	public Item getItemStockMatrix(boolean b, int idGoodSM);
	
	public void addItemStockMatrix(Item item, boolean asset, int idStock);
	
	public void removeItemStockMatrix (Item item, boolean asset, int idStock);
	
	public List<Item> getAssets(int assetId);
	
	public double[][] getNumericBalanceSheet();
	
	public double getNetWealth();
	
	public Expectation getExpectation(Integer key);
	
	public double getPassedValue(int idValue,int lag);
	
	public void addValue(int idValue, double value);
	
	public void setAssetStock(List<List<Item>> assetStock);
	
	public List<List<Item>> getAssetStock();

	public boolean isDead();
	
	public double getAggregateValue(int idValue, int lag);
	
	public int getPopulationId ();
	
	public boolean isDefaulted ();
	
	public byte[] getPassedValuesBytes();
	
	/**
	 * Generate the byte array representing the passed values.
	 * @return
	 */
	public byte[] getExpectationsBytes();
	
	/**
	 * Generate the byte array representing the stock matrix.
	 * @return
	 */
	public byte[] getStockMatrixBytes();
	
	/**
	 * Generates the byte array representing the characteristics of the agent.
	 * @return the byte array
	 */
	public byte[] getAgentCharacteristicsBytes();
	
	/**
	 * Populates the characteristics of the agent using the byte array content.
	 * @param the byte array
	 */
	public void populateCharacteristics(byte[] content, MacroPopulation pop);
	
	public void populateAgent(byte[] content, MacroPopulation pop);
	
	public byte[] getBytes();
	
	public void populateStockMatrixBytes(byte[] content, MacroPopulation pop);
	
	public void populateExpectationsBytes(byte[] content);
	
	public void populatePassedValuesBytes(byte[] content);
	
	/**
	 * Generates the byte array containing all strategies byte arrays. 
	 * @return
	 */
	public byte[] getStrategiesBytes();
	
	/**
	 * Populates the Macro Strategy from the byte array content. 
	 */
	public void populateStrategies(byte[] content, MacroPopulation pop);

	/**
	 * @param agentId
	 */
	public void setAgentId(long agentId);

}
