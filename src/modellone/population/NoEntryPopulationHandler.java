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
package modellone.population;

import java.util.List;

import jmab.agents.MacroAgent;
import jmab.events.MacroTicEvent;
import jmab.population.AbstractPopulationHandler;
import jmab.population.PopulationHandler;
import modellone.StaticValues;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class NoEntryPopulationHandler extends AbstractPopulationHandler implements
		PopulationHandler {
	
	
	

	/* (non-Javadoc)
	 * @see jmab.population.PopulationHandler#initialiseAgent(net.sourceforge.jabm.agent.Agent, net.sourceforge.jabm.agent.Agent)
	 */
	@Override
	public void initialiseAgent(Agent newAgent, Agent oldAgent) {
		// TODO Auto-generated method stub
		//For the moment, not sure we need this method
	}

	/* (non-Javadoc)
	 * @see jmab.population.AbstractPopulationHandler#handleAgent(net.sourceforge.jabm.agent.Agent, int, java.util.List, int)
	 */
	@Override
	protected void handleAgent(Agent agent, int id, List<Agent> agents,
			int populationId) {
		// TODO Auto-generated method stub
		//For the moment, not sure we need this method
	}

	/* (non-Javadoc)
	 * @see jmab.population.AbstractPopulationHandler#onTicArrived(jmab.events.MacroTicEvent)
	 */
	@Override
	protected void onTicArrived(MacroTicEvent event) {
		switch(event.getTic()){
		case StaticValues.TIC_POPULATIONHANDLER:
			updatePopulations();
			break;
		}
	}
	
	/**
	 * 
	 */
	private void updatePopulations() {
		removeDeadAgents();
	}
	
	/**
	 * 
	 */
	private void removeDeadAgents() {
		List<Population> populations=  population.getPopulations();
		for (Population population:populations){
			List<Agent> populationAgents= population.getAgentList().getAgents();
			for (int i=0;i<populationAgents.size();i++){
				MacroAgent agent=(MacroAgent)populationAgents.get(i);
				if (agent.isDead()){
				populationAgents.remove(i);
				}
			}
		}	
	}

	
}
