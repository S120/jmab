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

import java.util.List;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import jmab.population.MarketPopulation;
import net.sourceforge.jabm.EventScheduler;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface MarketSimulation{

	public boolean closed();
	
	public int getMarketId();
	
	public void setMarketId(int marketId);

	public void subscribeToEvents(EventScheduler scheduler);
	
	public void setSimulation(MacroSimulation simulation);
	
	public void initialiseAgents();
	
	public void commit(MacroAgent agent, MacroAgent counterpart, int marketId);
	
	public void commit(MacroAgent agent, List<MacroAgent> counterpart, int marketId);
	
	public void populateFromBytes(byte[] content, MacroPopulation pop);
	
	public byte[] getBytes();

	/**
	 * @return
	 */
	public MarketPopulation getPopulation();
}
