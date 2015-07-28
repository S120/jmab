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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import jmab.events.MacroTicEvent;
import jmab.expectations.PassedValues;
import jmab.init.MacroAgentInitialiser;
import jmab.population.MacroPopulation;
import jmab.population.PopulationHandler;
import net.sourceforge.jabm.AbstractSimulation;
import net.sourceforge.jabm.SimulationTime;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.RoundStartingEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * The abstract class containing the general methods and fields required to manage a the simulation of the 
 * macro AB-SFC model. It is extended by the {@link OrderedEventsSimulation} class.
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractMacroSimulation extends AbstractSimulation implements
MacroSimulation {

	/**
	 * passedValues a map of the variables which must be stored from previous rounds of the simulation
	 * markets the markets of the economy
	 * events an array defining the sequence of events {@link MacroTicEvent} taking place in each round of the simulation 
	 * activeMarketId the Id of the market activated in a certain moment of the simulation
	 * activeMarket the {@link MarketSimulation} activated in a certain moment of the simulation
	 * handler the population handler, which manages the death and possible replacement of agents.
	 * round the current round (a.k.a. period) of the simulation
	 * maximumRounds the maximum number of rounds.
	 */

	protected Map<Integer,PassedValues> passedValues;
	protected ArrayList<MarketSimulation> markets;
	protected ArrayList<MacroTicEvent> events;
	protected int activeMarketId;
	protected MarketSimulation activeMarket;
	protected PopulationHandler handler; 
	protected int round = 0;
	protected int initRound = 1;
	protected int maximumRounds = Integer.MAX_VALUE;


	public AbstractMacroSimulation() {
		super();
	}



	/**
	 * @return the handler
	 */
	public PopulationHandler getHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(PopulationHandler handler) {
		this.handler = handler;
	}

	/**
	 * @return the round
	 */
	public int getRound() {
		return round;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.Simulation#getSimulationTime()
	 */
	@Override
	public SimulationTime getSimulationTime() {
		return simulationController.getSimulationTime();
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MacroSimulation#getActivatedMarketID()
	 */
	@Override
	public int getActiveMarketId() {
		return activeMarketId;
	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MacroSimulation#setActivatedMarketID(int)
	 */
	@Override
	public void setActiveMarketId(int activatedMarketId) {
		this.activeMarketId=activatedMarketId;

	}

	/* (non-Javadoc)
	 * @see jmab.simulations.MacroSimulation#getActiveMarket()
	 */
	@Override
	public MarketSimulation getActiveMarket() {
		return activeMarket;
	}

	/**
	 * @param activeMarket the activeMarket to set
	 */
	public void setActiveMarket(MarketSimulation activeMarket) {
		this.activeMarket = activeMarket;
	}

	/**
	 * @return the markets
	 */
	public ArrayList<MarketSimulation> getMarkets() {
		return markets;
	}

	/**
	 * @param markets the markets to set
	 */
	public void setMarkets(ArrayList<MarketSimulation> markets) {
		this.markets = markets;
	}

	public MarketSimulation getMarket(int marketId){
		return this.markets.get(marketId);
	}
	
	/*
	 * No Sure we need this method
	 * */
	public void agentDie(int populationId, Agent agent){
		handler.agentDie(populationId, agent, (MacroPopulation)this.population);
	}

	protected void begin() {
		setListeners();
		initialiseAgents();
		initialiseEvents();
		fireEvent(new SimulationStartingEvent(this));
	}

	@Override
	public void initialiseAgents() {
		MacroAgentInitialiser macroInit = (MacroAgentInitialiser) this.agentInitialiser;
		macroInit.initialise((MacroPopulation)population, this);
		for(MarketSimulation sim:markets){
			sim.initialiseAgents();
		}
	}

	private void initialiseEvents(){
		for(MacroTicEvent event:events){
			event.initialise();
		}
	}
	
	/**
	 * 
	 */
	private void setListeners() {
		for(MarketSimulation sim:markets){
			sim.setSimulation(this);
			sim.subscribeToEvents(this.simulationController);
		}

	}

	protected void end() {
		fireEvent(new SimulationFinishedEvent(this));
		disposeEvenst();
	}

	/**
	 * 
	 */
	private void disposeEvenst() {
		for(MacroTicEvent event:events){
			event.dispose();
		}
	}



	/**
	 * For each round of the simulation make a step
	 */
	@Override
	public void run() {
		begin();
		for(round = initRound; round <= maximumRounds && isRunning; round++) {
			step();
		}
		end();
	}

	/**
	 * The step method first fires the {@link RoundStartingEvent} event, then invokes the fireListEvents method, which determine the sequence of events fired 
	 * of the type {@link MacroTicEvent} to be sent during the round, and finally, fires the {@link RoundFinishedEvent} event.
	 * determining the sequence of actions in that round.
	 */
	@Override
	public void step(){
		super.step();
		fireEvent(new RoundStartingEvent(this));
		fireListEvents();
		fireEvent(new RoundFinishedEvent(this));
	}

	protected abstract void fireListEvents();

	public int getMaximumRounds() {
		return maximumRounds;
	}

	/**
	 * Configure the maximum number of rounds this simulation will run before
	 * being automatically terminated.
	 * 
	 * @param maximumRounds
	 */
	public void setMaximumRounds(int maximumRounds) {
		this.maximumRounds = maximumRounds;
	}

	/**
	 * @return the events
	 */
	public ArrayList<MacroTicEvent> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(ArrayList<MacroTicEvent> events) {
		this.events = events;
	}

	/**
	 * @param round the round to set
	 */
	public void setRound(int round) {
		this.round = round;
	}

	/**
	 * @param event
	 */
	public void fireSpecificEvent(MacroTicEvent event) {
		this.simulationController.notifySpecificListeners(event);
	}


	/**
	 * @param event
	 */
	public void fireGenericEvent(MacroTicEvent event) {
		this.simulationController.notifyGenericListeners(event);
	}

	/**
	 * @return the passedValues
	 */
	public Map<Integer, PassedValues> getPassedValues() {
		return passedValues;
	}
	/**
	 * @param passedValues the passedValues to set
	 */
	public void setPassedValues(Map<Integer, PassedValues> passedValues) {
		this.passedValues = passedValues;
	}


	/**
	 * Gets a lagged observation
	 * @param idValue the id of the variable
	 * @param lag the number of lag
	 */
	public double getPassedValue(int idValue,int lag){
		return this.passedValues.get(idValue).getObservation(Math.max(0,this.getRound()-lag));
	}

	/**
	 * Adds an observation
	 * @param idValue the id of the variable
	 * @param value the value of the observation
	 */
	public void addValue(int idValue, double value){
		this.passedValues.get(idValue).addObservation(value, this.getRound());
	}

	/**
	 * Generate the byte array representing the passed values. The structure is the following
	 * [nbPassedValues]
	 * for each type of stocks
	 * 	[idPassedValue][passedValueSize][passedValueStrucure]
	 * end for 	
	 * @return
	 */
	public byte[] getPassedValuesBytes(){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			output.write(ByteBuffer.allocate(4).putInt(passedValues.size()).array());
			Set<Integer> orderedKeys = passedValues.keySet();
			for(Integer key:orderedKeys){
				output.write(ByteBuffer.allocate(4).putInt(key).array());
				PassedValues val = passedValues.get(key);
				byte[] valBytes = val.getByteArray();
				output.write(ByteBuffer.allocate(4).putInt(valBytes.length).array());
				output.write(valBytes);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	/**
	 * Populates the passed values from the byte array content. The structure is the following
	 * [nbPassedValues]
	 * for each type of stocks
	 * 	[idPassedValue][passedValueSize][passedValueStrucure]
	 * end for 	
	 * @return
	 */
	public void populatePassedValuesBytes(byte[] content){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbExp = buf.getInt();
		for(int i = 0 ; i < nbExp ; i++){
			int key = buf.getInt();
			int valSize = buf.getInt();
			byte[] valBytes = new byte[valSize];
			buf.get(valBytes);
			PassedValues val = this.passedValues.get(key);
			val.populateExpectation(valBytes);
		}
	}

	/**
	 * Generates the bye array containing the ArrayList markets. The structure is as follows:
	 * [nbMarkets]
	 * for each market
	 * 	[marketSize][marketStructure]
	 * end for
	 * @return
	 */
	protected byte[] getMarketsByte(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ByteBuffer.allocate(4).putInt(markets.size()).array());
			for(MarketSimulation market:markets){
				byte[] marketBytes = market.getBytes();
				out.write(ByteBuffer.allocate(4).putInt(marketBytes.length).array());
				out.write(marketBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the markets from the byte array content. The structure is the following
	 * [nbMarkets]
	 * for each type of stocks
	 * 	[marketSize][marketStructure]
	 * end for 	
	 * @return
	 */
	public void populateMarketsBytes(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbMkts = buf.getInt();
		for(int i = 0 ; i < nbMkts ; i++){
			int mktSize = buf.getInt();
			byte[] mktBytes = new byte[mktSize];
			buf.get(mktBytes);
			MarketSimulation mkt = this.markets.get(i);
			mkt.populateFromBytes(content, pop);
		}
	}

	/**
	 * Generates the bye array containing the ArrayList events. The structure is as follows:
	 * [nbEvents]
	 * for each event
	 * 	[eventSize][eventStructure]
	 * end for
	 * @return
	 */
	protected byte[] getEventsByte(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ByteBuffer.allocate(4).putInt(events.size()).array());
			for(MacroTicEvent event:events){
				byte[] eventBytes = event.getBytes();
				out.write(ByteBuffer.allocate(4).putInt(eventBytes.length).array());
				out.write(eventBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the events from the byte array content. The structure is the following
	 * [nbEvents]
	 * for each event
	 * 	[eventSize][eventStructure]
	 * end for	
	 * @return
	 */
	public void populateEventsBytes(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbEvents = buf.getInt();
		for(int i = 0 ; i < nbEvents ; i++){
			int eventSize = buf.getInt();
			byte[] eventBytes = new byte[eventSize];
			buf.get(eventBytes);
			MacroTicEvent event = this.events.get(i);
			event.populateFromBytes(content, pop);
		}
	}

	/**
	 * Generates the byte array of the characteristics of the AbstractMacroSimulation. The structure is as follows:
	 * [round][passedValuesSize][passedValuesStructure][marketsSize][marketsStructure][eventsSize][eventsStructure][popSize][populationStructure]	
	 */
	protected byte[] getSimulationCharacteristicsBytes(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ByteBuffer.allocate(4).putInt(this.round).array());
			byte[] valuesByte = this.getPassedValuesBytes();
			out.write(ByteBuffer.allocate(4).putInt(valuesByte.length).array());
			out.write(valuesByte);
			byte[] marketsByte = this.getMarketsByte();
			out.write(ByteBuffer.allocate(4).putInt(marketsByte.length).array());
			out.write(marketsByte);
			byte[] eventsByte = this.getEventsByte();
			out.write(ByteBuffer.allocate(4).putInt(eventsByte.length).array());
			out.write(eventsByte);
			byte[] popByte = ((MacroPopulation)this.population).getBytes();
			out.write(ByteBuffer.allocate(4).putInt(popByte.length).array());
			out.write(popByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the AbstractMacroSimulation from the byte array content. The structure should be as follows:
	 * [round][passedValuesSize][passedValuesStructure][marketsSize][marketsStructure][eventsSize][eventsStructure][popSize][populationStructure]
	 */
	protected void populateSimulationCharacteristicsFromBytes(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int roundInit = buf.getInt();
		this.initRound = roundInit;
		this.maximumRounds += roundInit;
		int valSize = buf.getInt();
		byte[] valBytes = new byte[valSize];
		buf.get(valBytes);
		this.populatePassedValuesBytes(valBytes);
		int mktSize = buf.getInt();
		byte[] mktBytes = new byte[mktSize];
		buf.get(mktBytes);
		this.populateMarketsBytes(mktBytes, pop);
		int eventSize = buf.getInt();
		byte[] eventBytes = new byte[eventSize];
		buf.get(eventBytes);
		this.populateEventsBytes(eventBytes, pop);
		int popSize = buf.getInt();
		byte[] popBytes = new byte[popSize];
		buf.get(popBytes);
		((MacroPopulation)pop).populateFromBytes(popBytes,pop);
	}
}
