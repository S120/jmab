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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import jmab.agents.MacroAgent;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
//A MacroPopulation is an object containing a list of populations

@SuppressWarnings("serial")
public class MacroPopulation extends Population implements EventListener{

	protected ArrayList<Population> populations;

	public static final Comparator<Agent> ascendingId = 
			new Comparator<Agent>() {

		public int compare(Agent o1, Agent o2) {
			MacroAgent a1 = (MacroAgent)o1;
			MacroAgent a2 = (MacroAgent)o2;
			if (a1.getAgentId() > a2.getAgentId()) {
				return 1;
			} else if (a1.getAgentId() < a2.getAgentId()) {
				return -1;
			} else {
				return 0;
			}
		}

	};

	/**
	 * 
	 */
	public MacroPopulation() {
		super();
	}

	/**
	 * @param populations
	 */

	public MacroPopulation(ArrayList<Population> populations) {
	}



	//get single Population based on its ID (position in list of lists)
	public Population getPopulation(int populationId){
		return populations.get(populationId);
	}

	/**
	 * @return the populations
	 */
	//return all populations
	public ArrayList<Population> getPopulations() {
		return populations;
	}

	/**
	 * @param populations the populations to set
	 */

	public void setPopulations(ArrayList<Population> populations) {
		this.populations = populations;
	}

	public void reset(){
		for(Population pop:populations){
			pop.reset();
		}
	}

	//set an AgentList composed of the agents belonging to a specific population
	public void setAgentList(AgentList agentList, int populationId) {
		Population pop=populations.get(populationId);
		pop.setAgentList(agentList);
	}

	//add an agent to a specific population
	public void add(Agent agent, int populationId) {
		Population pop=populations.get(populationId);
		pop.add(agent);
	}

	public void setPrng(RandomEngine prng) {
		for(Population pop:populations){
			pop.setPrng(prng);
		}
	}

	@Override
	public void eventOccurred(SimEvent event) {
	}

	@Override
	public int getSize(){
		return populations.size();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.Population#getAgents()
	 */
	@Override
	public Collection<Agent> getAgents() {
		ArrayList<Agent> completeListOfAgents = new ArrayList<Agent>();
		for (Population pop: populations) {
			completeListOfAgents.addAll(pop.getAgents());	
		}
		return completeListOfAgents;

	}

	/**
	 * Generates the byte array representing the MacroPopulation and all the agents it contains. The structure is as follows:
	 * [nbPopulations]
	 * for each population
	 *  [sizeStructure][popId][nbAgents][agentIds]
	 * end for
	 * for each population
	 * 	[sizePop][popId][popStructure]
	 * end for
	 * @return
	 */
	public byte[] getBytes(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write(ByteBuffer.allocate(4).putInt(this.populations.size()).array());
			for(int i = 0 ; i < populations.size() ; i++){
				Population pop = populations.get(i);
				byte[] popBytes = this.getPopulationAgentIdsBytes(pop);
				out.write(ByteBuffer.allocate(4).putInt(popBytes.length).array());
				out.write(ByteBuffer.allocate(4).putInt(i).array());
				out.write(popBytes);
			}
			for(int i = 0 ; i < populations.size() ; i++){
				Population pop = populations.get(i);
				byte[] popBytes = this.getPopulationBytes(pop);
				out.write(ByteBuffer.allocate(4).putInt(popBytes.length).array());
				out.write(ByteBuffer.allocate(4).putInt(i).array());
				out.write(popBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Generates the byte array for all the agent Ids of one population, the structure should be as follow:
	 * [nbAgents][agentIds]
	 * @return
	 */
	private byte[] getPopulationAgentIdsBytes(Population pop) {
		AgentList al = pop.getAgentList();
		al.sortAgents(ascendingId);
		ByteBuffer buf = ByteBuffer.allocate(8*al.getSize()+4);
		buf.putInt(al.getSize());
		for(Agent a:al.getAgents()){
			MacroAgent agent = (MacroAgent)a;
			buf.putLong(agent.getAgentId());
		}
		return buf.array();
	}

	/**
	 * Generates the byte array for one population, the structure should be as follow:
	 * [nbAgents]
	 * for each agent
	 * 	[sizeAgent][agentStructure]
	 * end for 
	 * @return
	 */
	private byte[] getPopulationBytes(Population pop) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			AgentList al = pop.getAgentList();
			al.sortAgents(ascendingId);
			out.write(ByteBuffer.allocate(4).putInt(al.getSize()).array());
			for(Agent a:al.getAgents()){
				MacroAgent agent = (MacroAgent)a;
				byte[] agentBytes = agent.getBytes();
				out.write(ByteBuffer.allocate(4).putInt(agentBytes.length).array());
				out.write(agentBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the MacroPopulation from the byte array content. The structure should be as follows:
	 * [nbPopulations]
	 * for each population
	 *  [sizeStructure][popId][nbAgents][agentIds]
	 * end for
	 * for each population
	 * 	[sizePop][popId][popStructure]
	 * end for
	 * @param content
	 * @param pop
	 */
	public void populateFromBytes(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbPop = buf.getInt();
		for(int i = 0 ; i < nbPop ; i++){
			int sizePop = buf.getInt();
			int popId = buf.getInt();
			Population currentPop = this.populations.get(popId);
			byte[] popStruct = new byte[sizePop];
			buf.get(popStruct);
			this.populatePopulationAgentIdsFromBytes(popStruct,currentPop);
		}
		for(int i = 0 ; i < nbPop ; i++){
			int sizePop = buf.getInt();
			int popId = buf.getInt();
			Population currentPop = this.populations.get(popId);
			byte[] popStruct = new byte[sizePop];
			buf.get(popStruct);
			this.populatePopulationFromBytes(popStruct,currentPop);
		}
	}

	/**
	 * Populates a given population from the bytes, the structure should be as follows:
	 * [nbAgents][agentIds]
	 * @param content
	 * @param pop
	 */
	private void populatePopulationAgentIdsFromBytes(byte[] content, Population currentPop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbAgent = buf.getInt();
		for(int i = 0 ; i < nbAgent ; i++){
			long agentId = buf.getLong();
			MacroAgent agent = (MacroAgent)currentPop.getAgentList().get(i);
			agent.setAgentId(agentId);
		}
	}
	
	/**
	 * Populates a given population from the bytes, the structure should be as follows:
	 * [nbAgents]
	 * for each agent
	 * 	[sizeAgent][agentStructure]
	 * end for
	 * @param content
	 * @param pop
	 */
	private void populatePopulationFromBytes(byte[] content, Population currentPop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbAgent = buf.getInt();
		for(int i = 0 ; i < nbAgent ; i++){
			int agentSize = buf.getInt();
			byte[] agentBytes = new byte[agentSize];
			buf.get(agentBytes);
			MacroAgent agent = (MacroAgent)currentPop.getAgentList().get(i);
			agent.populateAgent(agentBytes, this);
		}
	}



}
