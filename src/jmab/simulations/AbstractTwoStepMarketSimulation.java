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
import jmab.events.MarketTicEvent;
import jmab.init.MarketAgentInitialiser;
import jmab.mechanisms.Mechanism;
import jmab.mixing.TwoStepMarketMixer;
import jmab.population.MarketPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 */
public abstract class AbstractTwoStepMarketSimulation implements MarketSimulation,
		EventListener {

	protected Mechanism transaction;
	protected MacroSimulation simulation;
	protected int sellersId;
	protected int buyersId;
	protected int marketId;
	protected TwoStepMarketMixer mixer;
	protected MarketPopulation population;
	protected SimulationController scheduler;
	protected int firstTicId;
	protected int secondTicId;
	protected MarketAgentInitialiser initialiser;
	private boolean firstStep=false;
	private boolean secondStep=false;
	
	/**
	 * 
	 */
	public AbstractTwoStepMarketSimulation() {
		super();
	}

	/**
	 * @param transaction
	 * @param simulation
	 * @param sellersId
	 * @param buyersId
	 * @param marketId
	 * @param mixer
	 * @param population
	 * @param scheduler
	 * @param firstTicId
	 * @param secondTicId
	 * @param initialiser
	 */
	public AbstractTwoStepMarketSimulation(Mechanism transaction,
			MacroSimulation simulation, int sellersId, int buyersId,
			int marketId, TwoStepMarketMixer mixer, MarketPopulation population,
			SimulationController scheduler, int firstTicId, int secondTicId,
			MarketAgentInitialiser initialiser) {
		super();
		this.transaction = transaction;
		this.simulation = simulation;
		this.sellersId = sellersId;
		this.buyersId = buyersId;
		this.marketId = marketId;
		this.mixer = mixer;
		this.population = population;
		this.scheduler = scheduler;
		this.firstTicId = firstTicId;
		this.secondTicId = secondTicId;
		this.initialiser = initialiser;
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
	 * @return the sellersId
	 */
	public int getSellersId() {
		return sellersId;
	}

	/**
	 * @param sellersId the sellersId to set
	 */
	public void setSellersId(int sellersId) {
		this.sellersId = sellersId;
	}

	/**
	 * @return the buyersId
	 */
	public int getBuyersId() {
		return buyersId;
	}

	/**
	 * @param buyersId the buyersId to set
	 */
	public void setBuyersId(int buyersId) {
		this.buyersId = buyersId;
	}

	/**
	 * @return the mixer
	 */
	public TwoStepMarketMixer getMixer() {
		return mixer;
	}

	/**
	 * @param mixer the mixer to set
	 */
	public void setMixer(TwoStepMarketMixer mixer) {
		this.mixer = mixer;
	}

	/**
	 * @return the population
	 */
	public MarketPopulation getPopulation() {
		return population;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(MarketPopulation population) {
		this.population = population;
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
	 * @return the firstTicId
	 */
	public int getFirstTicId() {
		return firstTicId;
	}

	/**
	 * @param firstTicId the firstTicId to set
	 */
	public void setFirstTicId(int firstTicId) {
		this.firstTicId = firstTicId;
	}

	/**
	 * @return the secondTicId
	 */
	public int getSecondTicId() {
		return secondTicId;
	}

	/**
	 * @param secondTicId the secondTicId to set
	 */
	public void setSecondTicId(int secondTicId) {
		this.secondTicId = secondTicId;
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
	 * @return the simulation
	 */
	public MacroSimulation getSimulation() {
		return simulation;
	}

	/**
	 * @return the firstStep
	 */
	public boolean isFirstStep() {
		return firstStep;
	}

	/**
	 * @param firstStep the firstStep to set
	 */
	public void setFirstStep(boolean firstStep) {
		this.firstStep = firstStep;
	}

	/**
	 * @return the secondStep
	 */
	public boolean isSecondStep() {
		return secondStep;
	}

	/**
	 * @param secondStep the secondStep to set
	 */
	public void setSecondStep(boolean secondStep) {
		this.secondStep = secondStep;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.event.EventListener#eventOccurred(net.sourceforge.jabm.event.SimEvent)
	 */
	@Override
	public void eventOccurred(SimEvent event) {
		if(event instanceof MarketTicEvent){
			MarketTicEvent tic = (MarketTicEvent) event;
			if(this.firstTicId==tic.getTic()){
				MacroSimulation macroSim = (MacroSimulation)((MarketTicEvent) event).getSimulationController().getSimulation();
				macroSim.setActiveMarket(this);
				macroSim.setActiveMarketId(this.marketId);
				this.firstStep=true;
				this.secondStep=false;
				this.firstStep();
			}else if(this.secondTicId==tic.getTic()){
				MacroSimulation macroSim = (MacroSimulation)((MarketTicEvent) event).getSimulationController().getSimulation();
				macroSim.setActiveMarket(this);
				macroSim.setActiveMarketId(this.marketId);
				this.firstStep=false;
				this.secondStep=true;
				this.secondStep();
			}
		}
	}

	/**
	 * 
	 */
	public abstract void secondStep();

	/**
	 * 
	 */
	public abstract void firstStep();

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#getMarketId()
	 */
	@Override
	public int getMarketId() {
		return this.marketId;
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#setMarketId(int)
	 */
	@Override
	public void setMarketId(int marketId) {
		this.marketId=marketId;
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#subscribeToEvents(net.sourceforge.jabm.EventScheduler)
	 */
	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		this.scheduler=(SimulationController)scheduler;
		scheduler.addListener(MarketTicEvent.class, this);
		transaction.setMarketSimulation(this);
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#setSimulation(jmab.simulations.MacroSimulation)
	 */
	@Override
	public void setSimulation(MacroSimulation simulation) {
		this.simulation = simulation;
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MarketSimulation#initialiseAgents()
	 */
	@Override
	public void initialiseAgents() {
		initialiser.initialise(population);
	}

	/**
	 * Realize exchange based on a specific transactionMechanism
	 */
	public void commit(MacroAgent buyer, MacroAgent seller, int idMarket) {
		transaction.execute(buyer, seller, idMarket);
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
	 * @see jmab.simulations.MarketSimulation#closed()
	 */
	@Override
	public boolean closed() {
		return mixer.closed(population, simulation);
	}

	/**
	 * 
	 */
	public void invokeFirstAgentInteractions() {
		mixer.invokeFirstAgentInteractions(population, scheduler);
	}

	/**
	 * 
	 */
	public void invokeSecondAgentInteractions() {
		mixer.invokeSecondAgentInteractions(population, scheduler);
	}
}
