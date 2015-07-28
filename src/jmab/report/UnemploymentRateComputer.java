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
package jmab.report;

import jmab.agents.LaborSupplier;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This computer computes the aggregate unemployment rate.
 */
public class UnemploymentRateComputer implements VariableComputer {

	private int[] householdPopIds;
	
	
	
	/**
	 * @return the householdPopIds
	 */
	public int[] getHouseholdPopIds() {
		return householdPopIds;
	}

	/**
	 * @param householdPopIds the householdPopIds to set
	 */
	public void setHouseholdPopIds(int[] householdPopIds) {
		this.householdPopIds = householdPopIds;
	}

	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable()
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		int totPop=0;
		int employedPop=0;
		for(int i=0;i<householdPopIds.length;i++){
			Population hhPop = macroPop.getPopulation(householdPopIds[i]);
			totPop += hhPop.getSize();
			for(Agent agent:hhPop.getAgents()){
				LaborSupplier hh = (LaborSupplier) agent;
				if(hh.isEmployed()) employedPop+=1;
			}
		}
		double result = 1-(double)employedPop/(double)totPop; 
		return result;
	}

	/**
	 * @return the householdPopIds
	 */
	public int[] getHouseholdPopId() {
		return householdPopIds;
	}

	/**
	 * @param householdPopIds the householdPopId to set
	 */
	public void setHouseholdPopId(int[] householdPopIds) {
		this.householdPopIds = householdPopIds;
	}

	
	
}
