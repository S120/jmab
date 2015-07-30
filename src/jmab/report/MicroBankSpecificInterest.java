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

import java.util.Map;
import java.util.TreeMap;

import jmab.agents.InterestRateSetterWithTargets;
import jmab.population.MacroPopulation;
import jmab.report.AbstractMicroComputer;
import jmab.report.MicroMultipleVariablesComputer;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MicroBankSpecificInterest extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {

	private int banksId;
	private int mktId;
	
	
	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(banksId);
		TreeMap<Long,Double> result=new TreeMap<Long,Double>();
		for (Agent i:pop.getAgents()){
			InterestRateSetterWithTargets bank= (InterestRateSetterWithTargets) i;
			if (!bank.isDead()){
				result.put(bank.getAgentId(), (bank.getInterestRate(mktId)));
			}
			else{
				result.put(bank.getAgentId(), Double.NaN);
			}
		}
		return result;
	}

	/**
	 * @return the banksId
	 */
	public int getBanksId() {
		return banksId;
	}
	
	/**
	 * @param banksId the banksId to set
	 */
	public void setBanksId(int banksId) {
		this.banksId = banksId;
	}

	/**
	 * @return the mktId
	 */
	public int getMktId() {
		return mktId;
	}

	/**
	 * @param mktId the mktId to set
	 */
	public void setMktId(int mktId) {
		this.mktId = mktId;
	}

	
}
