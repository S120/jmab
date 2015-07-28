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

import java.util.ArrayList;
import java.util.List;

import jmab.agents.MacroAgent;
import jmab.population.MarketPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class DynamicRandomRobinBuyerMixer extends AbstractMarketMixer implements MarketMixer {

	protected RandomEngine prng;
	
	/**
	 * @param prng
	 */
	public DynamicRandomRobinBuyerMixer(){}
	public DynamicRandomRobinBuyerMixer(RandomEngine prng) {
		super();
		this.prng = prng;
	}

	private void invokeInteractions(AgentList buyers, AgentList sellers, SimulationController model) {
		buyers.shuffle(prng);
		for (Agent buyer : buyers.getAgents()) {
			MacroAgent b = (MacroAgent)buyer;
			MacroSimulation sim = (MacroSimulation)model.getSimulation();
			if(b.isActive(sim.getActiveMarketId())){
				ArrayList<Agent> allSellers = (ArrayList<Agent>) sellers.getAgents();
				ArrayList<Agent> activeSellers = new ArrayList<Agent>();
				for(int i=0;i<allSellers.size();i++){
					MacroAgent seller = (MacroAgent)allSellers.get(i);
					if(seller.isActive(sim.getActiveMarketId())){
						activeSellers.add(seller);
					}
				}
				AgentArrivalEvent event = 
						new AgentArrivalEvent(model, buyer, (ArrayList<Agent>) activeSellers);
				model.fireEvent(event);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jmab.mixing.AbstractMacroMIxer#invokeAgentInteractions(net.sourceforge.jabm.Population, net.sourceforge.jabm.SimulationController)
	 */
	@Override
	public void invokeAgentInteractions(MarketPopulation population,
			SimulationController simulation) {
		invokeInteractions(population.getBuyers(),population.getSellers(),simulation);
		population.getBuyers().setAgents(new ArrayList<Agent>());
		population.getSellers().setAgents(new ArrayList<Agent>());	
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

}
