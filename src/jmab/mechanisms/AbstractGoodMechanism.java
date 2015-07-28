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
package jmab.mechanisms;

import jmab.agents.MacroAgent;
import jmab.simulations.MarketSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public abstract class AbstractGoodMechanism extends AbstractMechanism implements GoodMechanism{

	protected int idGoodSM;
	
	/**
	 * 
	 */
	public AbstractGoodMechanism() {
	}

	/**
	 * @param scheduler
	 * @param market
	 */
	public AbstractGoodMechanism(MarketSimulation market) {
		super(market);
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, jmab.agents.MacroAgent, int)
	 */
	@Override
	public abstract void execute(MacroAgent buyer, MacroAgent seller, int idMarket);

	/**
	 * @return the idGoodSM
	 */
	public int getIdGoodSM() {
		return idGoodSM;
	}

	/**
	 * @param idGoodSM the idGoodSM to set
	 */
	public void setIdGoodSM(int idGoodSM) {
		this.idGoodSM = idGoodSM;
	}
	
}
