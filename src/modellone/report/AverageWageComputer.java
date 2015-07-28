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
package modellone.report;

import java.util.TreeMap;

import modellone.agents.Households;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import jmab.population.MacroPopulation;
import jmab.report.VariableComputer;
import jmab.simulations.MacroSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class AverageWageComputer implements VariableComputer {
	
	private int householdId;

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(householdId);
		double wages=0;
		for (Agent i:pop.getAgents()){
			Households household= (Households) i;
			wages+=household.getWage();
		}
		return wages/pop.getSize();
	}

	/**
	 * @return the householdsId
	 */
	public int getHouseholdId() {
		return householdId;
	}

	/**
	 * @param householdsId the householdsId to set
	 */
	public void setHouseholdId(int householdsId) {
		this.householdId = householdsId;
	}
	
	

}
