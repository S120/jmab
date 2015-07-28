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
package jmab.init;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;


/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class SerializationInitialiser extends AbstractMacroAgentInitialiser implements MacroAgentInitialiser{

	String fileName;
	BufferedInputStream input;
	
	/* (non-Javadoc)
	 * @see jmab.init.AbstractMacroAgentInitialiser#initialise(jmab.population.MacroPopulation)
	 */
	@Override
	public void initialise(MacroPopulation population, MacroSimulation sim) {
		try {
			input = new BufferedInputStream(new FileInputStream(fileName));
			byte[] contentLength = new byte[4]; 
			input.read(contentLength);
			byte[] content = new byte[ByteBuffer.wrap(contentLength).getInt()];
			input.read(content);
			sim.populateFromBytes(content, population);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	

	
}
