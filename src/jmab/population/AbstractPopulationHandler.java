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


import java.util.List;

import jmab.events.MacroTicEvent;
import jmab.init.MarketAgentInitialiser;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public abstract class AbstractPopulationHandler implements PopulationHandler, EventListener{
	
	protected MarketAgentInitialiser initialiser;
	protected EventScheduler scheduler;
	protected List<Integer> activeTicEvents;
	protected MacroPopulation population;

	/**
	 * 
	 */
	
	
	public AbstractPopulationHandler() {
	}
	
	/**
	 * @return the scheduler
	 */
	public EventScheduler getScheduler() {
		return scheduler;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(EventScheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * @return the activeTicEvents
	 */
	public List<Integer> getActiveTicEvents() {
		return activeTicEvents;
	}

	/**
	 * @param activeTicEvents the activeTicEvents to set
	 */
	public void setActiveTicEvents(List<Integer> activeTicEvents) {
		this.activeTicEvents = activeTicEvents;
	}

	/**
	 * @return the population
	 */
	public MacroPopulation getPopulation() {
		return population;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(MacroPopulation population) {
		this.population = population;
	}

	public void agentDie(int populationId, Agent agent,MacroPopulation populations){
		AgentList agentList = populations.getPopulation(populationId).getAgentList();
		List<Agent> agents = agentList.getAgents();
		int id = agents.indexOf(agent);
		if(id>=0){//TODO
			handleAgent(agent, id, agents, populationId);
		}
	}
	
	public void subscribeToEvents() {
		scheduler.addListener(AgentArrivalEvent.class, this);
		scheduler.addListener(MacroTicEvent.class, this);
		scheduler.addListener(SimulationFinishedEvent.class, this);
	}
	
	@Override
	public void eventOccurred(SimEvent event) {
		if(event instanceof MacroTicEvent){
			MacroTicEvent tic = (MacroTicEvent)event; 
			if(this.activeTicEvents.contains(tic.getTic())){
				onTicArrived(tic);
			}
		}
	}
	
	/**
	 * @return the initialiser
	 */
	public MarketAgentInitialiser getInitialiser() {
		return initialiser;
	}

	/**
	 * @param initialiser the initialiser to set
	 */
	public void setInitialiser(MarketAgentInitialiser initialiser) {
		this.initialiser = initialiser;
	}

	/**
	 * @param agent
	 * @param id
	 * @param agents
	 */
	abstract protected void handleAgent(Agent agent, int id, List<Agent> agents, int populationId);
	
	/**
	 * @param tic
	 */
	protected abstract void onTicArrived(MacroTicEvent event);
}
