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
package jmab.strategies;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;
import net.sourceforge.jabm.strategy.Strategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MacroMultipleStrategy extends AbstractStrategy implements
MacroStrategy {

	protected HashMap<Integer,SingleStrategy> strategies;

	/**
	 * 
	 */
	public MacroMultipleStrategy() {
	}

	/**
	 * @param agent
	 */
	public MacroMultipleStrategy(Agent agent) {
		super(agent);
	}

	/**
	 * @param scheduler
	 * @param agent
	 */
	public MacroMultipleStrategy(EventScheduler scheduler, Agent agent) {
		super(scheduler, agent);
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.MacroStrategy#getStrategy(int)
	 */
	@Override
	public Strategy getStrategy(int strategyID) {
		return strategies.get(strategyID);
	}

	/**
	 * This method can be used to assign to each one of the strategies contained in the map MacroStrategy in the strategy field 
	 * of a particular agent, that very agent. So in the definition of specific strategies (see for example 
	 * ConsumptionFixedPropensitiesOOIW) we can use this.getAgent avoiding having to define ex ante the type of agent that will use
	 * the strategy in the strategy's method parameters.
	 */


	@Override
	public void setAgent(Agent agent){
		Iterator<Integer> keys = strategies.keySet().iterator();
		while(keys.hasNext()){
			Integer key = (Integer) keys.next();
			Strategy strategy=(Strategy) strategies.get(key);
			strategy.setAgent(agent);
		}
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler){
		Iterator<Integer> keys = strategies.keySet().iterator();
		while(keys.hasNext()){
			Integer key = (Integer) keys.next();
			Strategy strategy=(Strategy) strategies.get(key);
			strategy.subscribeToEvents(scheduler);
		}
	}

	@Override
	public void unsubscribeFromEvents(){
		Iterator<Integer> keys = strategies.keySet().iterator();
		while(keys.hasNext()){
			Integer key = (Integer) keys.next();
			Strategy strategy=(Strategy) strategies.get(key);
			strategy.unsubscribeFromEvents();
		}
	}

	/**
	 * @return the strategies
	 */
	public HashMap<Integer, SingleStrategy> getStrategies() {
		return strategies;
	}

	/**
	 * @param strategies the strategies to set
	 */
	public void setStrategies(HashMap<Integer,SingleStrategy> strategies) {
		this.strategies = strategies;
	}

	
	/**
	 * Generates the byte array containing all the SingleStrategies. The structure of the byte array is the following:
	 * [nbStrategies]
	 * for each strategy
	 * 	[size][strategyKey][StretagyStructure]
	 * end for
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ByteBuffer.allocate(4).putInt(strategies.size()).array());
			for(Integer key:strategies.keySet()){
				SingleStrategy strat = strategies.get(key);
				out.write(ByteBuffer.allocate(4).putInt(key).array());
				byte[] stratBytes = strat.getBytes();
				out.write(ByteBuffer.allocate(4).putInt(stratBytes.length).array());
				out.write(stratBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates all single strategies by using the content type. The structure is the following:
	 * [nbStrategies]
	 * for each strategy
	 * 	[size][strategyKey][StretagyStructure]
	 * end for
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbStrategies = buf.getInt();
		for(int i = 0 ; i < nbStrategies ; i++){
			int key = buf.getInt();
			int size = buf.getInt();
			byte[] stratContent = new byte[size];
			buf.get(stratContent);
			SingleStrategy strat = strategies.get(key);
			strat.populateFromBytes(stratContent, pop);
		}
	}
}
