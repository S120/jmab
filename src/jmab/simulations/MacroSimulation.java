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
package jmab.simulations;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface MacroSimulation extends Simulation {
	
	public int getActiveMarketId();
	
	public void setActiveMarketId(int activeMarketId);
	
	public MarketSimulation getActiveMarket();
	
	public void setActiveMarket(MarketSimulation marketSim);
	
	public void agentDie(int populationId, Agent agent);

	public int getRound();

	public double getPassedValue(int idValue,int lag);
	
	public void addValue(int idValue,double lag);
	
	public void populateFromBytes(byte[] content, MacroPopulation pop);
	
	public byte[] getBytes();
	
	public MarketSimulation getMarket(int marketId);
}
