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

import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import jmab.agents.CreditDemander;
import jmab.population.MacroPopulation;
import jmab.report.VariableComputer;
import jmab.simulations.MacroSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class ConstrainedCreditComputer implements VariableComputer {
	private int populationId;
	private int idLoanSM;
	/* (non-Javadoc)
	 * @see jmab.report.VariableComputer#computeVariable(jmab.simulations.MacroSimulation)
	 */
	@Override
	public double computeVariable(MacroSimulation sim) {
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population pop = macroPop.getPopulation(populationId);
		double consCredit=0;
		for (Agent i:pop.getAgents()){
			CreditDemander agent= (CreditDemander) i;
			consCredit+=agent.getLoanRequirement(this.idLoanSM);	
		}
		return consCredit;
	}
	/**
	 * @return the populationId
	 */
	public int getPopulationId() {
		return populationId;
	}
	/**
	 * @param populationId the populationId to set
	 */
	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}
	/**
	 * @return the idLoanSM
	 */
	public int getIdLoanSM() {
		return idLoanSM;
	}
	/**
	 * @param idLoanSM the idLoanSM to set
	 */
	public void setIdLoanSM(int idLoanSM) {
		this.idLoanSM = idLoanSM;
	}

}
