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
package jmab.population;

import java.util.Collection;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This is the class to instantiate the Market Population pertaining to each market: that is an object
 * containing two agentsList: buyers and seller on that specific market.
 */

//a MarketPopulation is an object containing two fields representing a list of buyers and 
//a list of sellers; the agentList field inherited from Population is a buyersList. The MarketPopulation
//class adds a new field representing the list sellersList.
public class MarketPopulation{

	protected AgentList sellersList;
	protected AgentList buyersList;
	
	/**
	 * 
	 */
	public MarketPopulation() {}

	
	/**
	 * @param agents
	 * @param prng
	 */
	
	//constructor when arguments are collections of agents
	public MarketPopulation(Collection<Agent> buyers,Collection<Agent> sellers, RandomEngine prng) {
		this.buyersList=new AgentList(buyers);
		this.sellersList=new AgentList(sellers);
	}

	/**
	 * @param agentList
	 * @param prng
	 */
	//construsctor when arguments are already agentList.
	public MarketPopulation(AgentList buyersList,AgentList sellersList, RandomEngine prng) {
		this.buyersList=buyersList;
		this.sellersList=sellersList;
	}
		
	
	
	//as alreaday mentioned the AgentList field inherited from Population class is composed of buyers
	public AgentList getBuyers(){
		return this.buyersList;
	}
	
	public AgentList getSellers(){
		return sellersList;
	}
	
	public void setBuyersList(AgentList buyersList){
		this.buyersList=buyersList;
	}
	
	public void setSellersList(AgentList sellersList){
		this.sellersList=sellersList;
	}

}
