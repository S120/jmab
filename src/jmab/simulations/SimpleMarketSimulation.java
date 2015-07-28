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

import java.util.ArrayList;
import java.util.List;

import jmab.agents.MacroAgent;
import jmab.events.MarketTicEvent;
import jmab.init.MarketAgentInitialiser;
import jmab.mechanisms.Mechanism;
import jmab.mixing.MarketMixer;
import jmab.population.MacroPopulation;
import jmab.population.MarketPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class SimpleMarketSimulation implements
		MarketSimulation,EventListener {
	
	protected Mechanism transaction;
	protected MacroSimulation simulation;
	protected int[] sellersId;
	protected int[] buyersId;
	protected int marketId;
	protected MarketMixer mixer;
	protected SimulationController scheduler;
	protected int ticId;
	protected MarketAgentInitialiser initialiser;
	protected int nbRuns;
	protected MarketPopulation population;
	
	
	/**
	 * @param simulationController
	 * @param population
	 */
	public SimpleMarketSimulation (){
		this.buyersId = new int[0];
		this.sellersId = new int[0];
	}
	
	public SimpleMarketSimulation(SimulationController simulationController,MacroSimulation simulation, 
			int[] sellersId, int[] buyersId, Mechanism transaction) {
		this.scheduler=simulationController;
		this.simulation=simulation;
		this.sellersId=sellersId;
		this.buyersId=buyersId;
		this.transaction=transaction;
		this.population = getPopulation();
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
	 * @return the nbRuns
	 */
	public int getNbRuns() {
		return nbRuns;
	}

	/**
	 * @param nbRuns the nbRuns to set
	 */
	public void setNbRuns(int nbRuns) {
		this.nbRuns = nbRuns;
	}

	/**
	 * @return the sellersId
	 */
	public int[] getSellersId() {
		return sellersId;
	}

	/**
	 * @param sellersId the sellersId to set
	 */
	public void setSellersId(int[] sellersId) {
		this.sellersId = sellersId;
	}

	/**
	 * @return the buyersId
	 */
	public int[] getBuyersId() {
		return buyersId;
	}

	/**
	 * @param buyersId the buyersId to set
	 */
	public void setBuyersId(int[] buyersId) {
		this.buyersId = buyersId;
	}

	/**
	 * The market is closed when there are either no buyers or no sellers active.
	 * close methods declared in Interface MarketMixer extending (AgentMixer) and implemented by 
	 * AbstractMarketMixer.
	 * @return boolean whether the market is closed or not
	 */
	@Override
	public boolean closed() {
		return mixer.closed(population, simulation);
	}
	
	/**
	 * @return the transaction
	 */
	public Mechanism getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(Mechanism transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the simulation
	 */
	public MacroSimulation getSimulation() {
		return simulation;
	}
	
	public void setSimulation(MacroSimulation simulation) {
		this.simulation = simulation;
		this.population = getPopulation();
	}
	
	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#getMarketId()
	 */
	public int getMarketId() {
		return marketId;
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#setMarketId(int)
	 */
	public void setMarketId(int marketId) {
		this.marketId=marketId;
	}
	
	/**
	 * @return the mixer
	 */
	public MarketMixer getMixer() {
		return mixer;
	}

	/**
	 * @param mixer the mixer to set
	 */
	public void setMixer(MarketMixer mixer) {
		this.mixer = mixer;
	}

	/**
	 * @return the scheduler
	 */
	public SimulationController getScheduler() {
		return scheduler;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	public void setScheduler(SimulationController scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * @return the ticId
	 */
	public int getTicId() {
		return ticId;
	}

	/**
	 * @param ticId the ticId to set
	 */
	public void setTicId(int ticId) {
		this.ticId = ticId;
	}

	/**
	 * @return the population
	 */
	public MarketPopulation getPopulation() {
		MarketPopulation population = new MarketPopulation();
		MacroPopulation macroPop = (MacroPopulation)simulation.getPopulation();
		ArrayList<Agent> buyers = new ArrayList<Agent>();
		for(int id:buyersId){
			buyers.addAll(macroPop.getPopulation(id).getAgents());
		}
		population.setBuyersList(new AgentList(buyers));
		ArrayList<Agent> sellers = new ArrayList<Agent>();
		for(int id:sellersId){
			sellers.addAll(macroPop.getPopulation(id).getAgents());
		}
		population.setSellersList(new AgentList(sellers));
		return population;
	}
	
	
	/**
	 * Realize exchange based on a specific transactionMechanism
	 */
	public void commit(MacroAgent buyer, MacroAgent seller, int idMarket) {
		transaction.execute(buyer, seller, idMarket);
	}

	public void invokeAgentInteractions() {
		mixer.invokeAgentInteractions(population, scheduler);	
	}
	
	/**
	 * This method adds the current market simulation to the specific listener of the MarketTicEvent
	 * events class.  Then it also tells the transaction mechanism in which marketSimulation is being used.
	 */
	public void subscribeToEvents(EventScheduler scheduler){
		this.scheduler=(SimulationController)scheduler;
		scheduler.addListener(MarketTicEvent.class, this);
		transaction.setMarketSimulation(this);
	}
	
	/** (non-Javadoc)
	 * If the event occurred and listened by the marketsimulation is of the type MarketTicEvent and the ticId of this event is 
	 * the one assigned to the current MarketSimulation, then run the market simulation (otherwise it means that another marketSimulation has been 
	 * activated by the MarketTic event)
	 */
	
	@Override
	public void eventOccurred(SimEvent event) {
		if(event instanceof MarketTicEvent){
			MarketTicEvent tic = (MarketTicEvent) event;
			if(this.ticId==tic.getTic()){
				MacroSimulation macroSim = (MacroSimulation)((MarketTicEvent) event).getSimulationController().getSimulation();
				macroSim.setActiveMarket(this);
				macroSim.setActiveMarketId(this.marketId);
				this.run();
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		begin();
		step();
		end();
	}
	
	public void step() {
		for(int i=0;i<this.nbRuns&&!this.mixer.closed(population, simulation);i++){
			invokeAgentInteractions();
		}
	}
	
	/**
	 * 
	 */
	private void end() {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	private void begin() {
		// TODO Auto-generated method stub
	}
	
	public void initialiseAgents(){
		initialiser.initialise(population);
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#commit(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void commit(MacroAgent buyer, List<MacroAgent> sellers,
			int marketId) {
		transaction.execute(buyer, sellers, marketId);
		
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1] ;// TODO 
	}
}
