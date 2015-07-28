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

import java.util.ArrayList;
import java.util.List;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.strategy.AbstractStrategy;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class SelectRandomWorkerStrategy extends AbstractStrategy implements
		SelectWorkerStrategy {

	protected RandomEngine prng;
	
	/* (non-Javadoc)
	 * @see jmab.strategies.SelectWorkerStrategy#selectWorker(java.util.ArrayList)
	 */
	@Override
	public MacroAgent selectWorker(List<Agent> workers) {
		AgentList agents = new AgentList(workers);
		agents.shuffle(prng);
		return (MacroAgent)agents.get(0);
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SelectWorkerStrategy#selectWorkers(java.util.List)
	 */
	@Override
	public List<MacroAgent> selectWorkers(List<Agent> workers, int n) {
		AgentList agents = new AgentList(workers);
		agents.shuffle(prng);
		List<MacroAgent> result = new ArrayList<MacroAgent>();
		for(int i = 0; i<Math.min(n, agents.size());i++){
			result.add((MacroAgent)agents.get(i));
		}
		return result;
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

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return new byte[1];//TODO
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SingleStrategy#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {}
	
}
