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
public class BrochureSelectionRandomRobinBuyerMixer extends AbstractTwoStepMarketMixer implements TwoStepMarketMixer {

	protected RandomEngine prng;
	protected int nbSellers;
	
	/**
	 * @param prng
	 */
	public BrochureSelectionRandomRobinBuyerMixer(){}
	public BrochureSelectionRandomRobinBuyerMixer(RandomEngine prng) {
		super();
		this.prng = prng;
	}

	private void invokeFirstInteractions(AgentList buyers, AgentList sellers, SimulationController model) {
		buyers.shuffle(prng);
		MacroSimulation sim = (MacroSimulation)model.getSimulation();
		for (Agent buyer : buyers.getAgents()) {
			sellers.shuffle(prng);
			ArrayList<Agent> allSellers = (ArrayList<Agent>) sellers.getAgents();
			ArrayList<Agent> selectedSellers = new ArrayList<Agent>();
			int nbSelectedSellers=0;
			for(int i=0;i<allSellers.size()&&nbSelectedSellers<nbSellers;i++){
				MacroAgent seller = (MacroAgent)allSellers.get(i);
				if(seller.isActive(sim.getActiveMarketId())){
					selectedSellers.add(seller);
					nbSelectedSellers++;
				}
			}
			if(selectedSellers.size()>0){
				AgentArrivalEvent event = 
						new AgentArrivalEvent(model,(Agent)buyer, selectedSellers);
				model.fireEvent(event);
			}
		}
	}

	private void invokeSecondInteractions(AgentList buyers, SimulationController model) {
		int marketId=((MacroSimulation)model.getSimulation()).getActiveMarketId();
		buyers.shuffle(prng);
		for (Agent buyer : buyers.getAgents()) {
			if(((MacroAgent)buyer).isActive(marketId)){
				AgentArrivalEvent event = 
						new AgentArrivalEvent (model, (Agent)buyer, null);
				model.fireEvent(event);
			}
		}
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
	 * @return the nbSellers
	 */
	public int getNbSellers() {
		return nbSellers;
	}
	
	/**
	 * @param nbSellers the nbSellers to set
	 */
	public void setNbSellers(int nbSellers) {
		this.nbSellers = nbSellers;
	}
	/* (non-Javadoc)
	 * @see jmab.mixing.TwoStepMarketMixer#invokeFirstAgentInteractions(jmab.population.MarketPopulation, net.sourceforge.jabm.SimulationController)
	 */
	@Override
	public void invokeFirstAgentInteractions(MarketPopulation population,
			SimulationController simulation) {
		this.invokeFirstInteractions(population.getBuyers(), population.getSellers(), simulation);
	}
	/* (non-Javadoc)
	 * @see jmab.mixing.TwoStepMarketMixer#invokeSecondAgentInteractions(jmab.population.MarketPopulation, net.sourceforge.jabm.SimulationController)
	 */
	@Override
	public void invokeSecondAgentInteractions(MarketPopulation population,
			SimulationController simulation) {
		this.invokeSecondInteractions(population.getBuyers(), simulation);
	}

}
