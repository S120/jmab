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

import java.nio.ByteBuffer;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.event.SimulationControllerEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This is the general class from which the tic events taking place within 
 * a single round of the simulation are instantiated.
 * Each tic event is identified by a specific tic index (integer) and a name defined in the StaticValues interface
 * of the specific model, see for example {@link StaticValues}.
 */
@SuppressWarnings("serial")
public class MacroTicEvent extends SimulationControllerEvent {

	private int tic;
	
	public MacroTicEvent(){
		super(null);
	}
	
	public MacroTicEvent(byte[] content, SimulationController simulationController){
		super(null);
		this.simulationController = simulationController;
		this.tic = ByteBuffer.wrap(content).getInt();
	}
	
	public void setSimulationController(SimulationController simulationController){
		this.simulationController=simulationController;
	}

	/**
	 * @return the tic
	 */
	public int getTic() {
		return tic;
	}

	/**
	 * @param tic the tic to set
	 */
	public void setTic(int tic) {
		this.tic = tic;
	}
	
	public void populateFromBytes(byte[] content, MacroPopulation pop){
		
	}
	
	public byte[] getBytes(){
		return ByteBuffer.allocate(4).putInt(this.tic).array();
	}
	
	public void initialise(){}
	
	public void dispose(){}
}
