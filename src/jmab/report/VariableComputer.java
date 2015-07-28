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

import jmab.simulations.MacroSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Interface that defines the methods to be developed for any variable computer. Thats is the way to compute any 
 *  variable of interest.
 */
public interface VariableComputer {

	public double computeVariable(MacroSimulation sim);
	
}
