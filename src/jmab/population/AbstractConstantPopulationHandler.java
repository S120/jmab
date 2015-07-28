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

import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public abstract class AbstractConstantPopulationHandler extends AbstractPopulationHandler
		implements PopulationHandler {

	/**
	 * 
	 */
	public AbstractConstantPopulationHandler() {}

	/* (non-Javadoc)
	 * @see jmab.population.AbstractPopulationHandler#handleAgent(net.sourceforge.jabm.agent.Agent, int, java.util.List)
	 */
	@Override
	protected void handleAgent(Agent agent, int id, List<Agent> agents, int populationId) {
		Agent newAgent=createAgent(agent, populationId);
		initialiseAgent(newAgent,agent);
		agents.remove(id);
		agents.add(newAgent);
	}

	/**
	 * @param agent
	 * @return
	 */
	public abstract Agent createAgent(Agent agent, int populationId);

}
