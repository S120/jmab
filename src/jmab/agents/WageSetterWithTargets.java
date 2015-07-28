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
package jmab.agents;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This interface must be implmeneted by the specific classes of producers if you want to use 
 * pricing strategies having a reference variable to look at in order to decide how to change the price
 * and a lower bound for the price (such as AdaptivePriceOnAC).
 */
public interface WageSetterWithTargets extends MacroAgent{
	
	public double getWage();
	public double getWageLowerBound();
	public double getWageUpperBound();
	public double getMicroReferenceVariableForWage();
	public double getMacroReferenceVariableForWage();

}
