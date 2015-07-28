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
package jmab.init;

import jmab.agents.MacroAgent;
import jmab.population.MarketPopulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This class contains the methods to initialise the Market Population of eahc market
 */
public class RandomMarketInitialiser implements MarketAgentInitialiser {

	protected RandomEngine prng;
	protected int marketId;
	
	/**
	 * 
	 */
	public RandomMarketInitialiser() {
	}
	
	/**
	 * @param prng
	 */
	public RandomMarketInitialiser(RandomEngine prng) {
		super();
		this.prng = prng;
	}

	/**
	 * @return the prng
	 */
	public RandomEngine getPrng() {
		return prng;
	}

	/**
	 * @param prng the prng to set
	 */
	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}
	

	/**
	 * @return the marketId
	 */
	public int getMarketId() {
		return marketId;
	}

	/**
	 * @param marketId the marketId to set
	 */
	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.init.AgentInitialiser#initialise(net.sourceforge.jabm.Population)
	 */
	public void initialise(MarketPopulation population) {
		AgentList buyers = population.getBuyers();
		AgentList sellers = population.getSellers();
		buyers.shuffle(prng);
		for(Agent agent:buyers.getAgents()){
			MacroAgent buyer = (MacroAgent) agent;
			sellers.shuffle(prng);
			MacroAgent seller=(MacroAgent) sellers.getAgents().get(0);
			buyer.initialise();
			seller.initialise();
			buyer.initialiseCounterpart(seller,marketId);
			seller.initialiseCounterpart(buyer,marketId);
		}
	}

}
