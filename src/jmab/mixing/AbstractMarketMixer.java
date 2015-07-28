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
package jmab.mixing;

import jmab.agents.MacroAgent;
import jmab.population.MarketPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.mixing.RandomRobinAgentMixer;

import org.apache.log4j.Logger;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public abstract class AbstractMarketMixer implements MarketMixer {

	static Logger logger = Logger.getLogger(RandomRobinAgentMixer.class);	
	
	
	
	/**
	 * 
	 */
	public AbstractMarketMixer() {}

	/* (non-Javadoc)
	 * @see jmab.mixing.MarketMixer#closed()
	 */
	
	//if a buyer in the market is active then check whether there is at least one seller
	// who is also active. In this case it return false (the market is not closed).
	//If there are no buyers or no sellers active return true (market closed).
	@Override
	public boolean closed(MarketPopulation population, MacroSimulation simulation) {
		int marketId=simulation.getActiveMarketId();
		for(Agent agent:population.getBuyers().getAgents()){
			MacroAgent buyer=(MacroAgent)agent;
			if(buyer.isActive(marketId)){
				for(Agent agent1:population.getSellers().getAgents()){
					MacroAgent seller=(MacroAgent)agent1;
					if(seller.isActive(marketId)){
						return false;
					}
				}
			}
		}		
		return true;
	}

}
