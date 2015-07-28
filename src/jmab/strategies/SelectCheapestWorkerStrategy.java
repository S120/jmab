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

import java.util.List;
import java.util.TreeMap;

import jmab.agents.LaborSupplier;
import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class SelectCheapestWorkerStrategy extends AbstractStrategy implements SelectWorkerStrategy {

	/* (non-Javadoc)
	 * @see jmab.strategies.SelectWorkerStrategy#selectWorker(java.util.ArrayList)
	 */
	@Override
	public MacroAgent selectWorker(List<Agent> workers) {
		double minWage=Double.POSITIVE_INFINITY;
		MacroAgent cheapestWorker=(MacroAgent)workers.get(0);
		for(Agent agent:workers){
			LaborSupplier worker=(LaborSupplier)agent;
			if(worker.getWage()<minWage){
				minWage=worker.getWage();
				cheapestWorker=worker;
			}
		}
		return cheapestWorker;
	}

	/* (non-Javadoc)
	 * @see jmab.strategies.SelectWorkerStrategy#selectWorkers(java.util.List)
	 */
	@Override
	public List<MacroAgent> selectWorkers(List<Agent> workers, int n) {
		double maxWage=Double.POSITIVE_INFINITY;
		TreeMap<Double, MacroAgent> tree = new TreeMap<Double, MacroAgent>(); 
		for(Agent agent:workers){
			LaborSupplier worker=(LaborSupplier)agent;
			if(tree.size()<n){
				maxWage=Math.max(maxWage,worker.getWage());
				tree.put(worker.getWage(), worker);
			}else if(worker.getWage()<maxWage){
				tree.remove(maxWage);
				tree.put(worker.getWage(),worker);
				maxWage = tree.lastKey();
			}
		}
		return (List<MacroAgent>)tree.values();
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
