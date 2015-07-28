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
package jmab.events;

import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.event.SimulationEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MarketInteractionsFinishedEvent extends SimulationEvent {

	/**
	 * 
	 */
	public MarketInteractionsFinishedEvent() {}

	/**
	 * @param model
	 */
	public MarketInteractionsFinishedEvent(Simulation model) {
		super(model);
	}

}
