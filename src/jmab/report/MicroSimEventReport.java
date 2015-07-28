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

import jmab.events.MicroSimEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.SimEventReport;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Class that extends the SimEventReport in order to account for the fact that we have one generic MacroSimEvent class that 
 * can contain any variable of interest. In order to determine wether the instance of MacroSimEventReport needs to update 
 * its reportVariables via the onEventPrototype method (from the mother class), the MacroSimEventReport class checks if
 * its variable id matches the variable Id of the event.
 */
public class MicroSimEventReport extends SimEventReport {

	private int variableId;
	
	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting(event);
		} else if (event instanceof SimulationFinishedEvent) {
			onSimulationFinished(event);
		} else if (event instanceof MicroSimEvent) {
			MicroSimEvent ev = (MicroSimEvent) event;
			if(ev.getVariableId()==this.variableId){
				onEventPrototype(event);
			}
		} else if (event.getClass().isAssignableFrom(eventPrototype.getClass())) {
			onEventPrototype(event);
		}
	}
	
	/**
	 * @return the variableId
	 */
	public int getVariableId() {
		return variableId;
	}

	/**
	 * @param variableId the variableId to set
	 */
	public void setVariableId(int variableId) {
		this.variableId = variableId;
	}
	
}
