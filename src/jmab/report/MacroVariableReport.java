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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jmab.events.MacroSimEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.XYReportVariables;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Class that implements the generic report variables 
 */
public class MacroVariableReport implements XYReportVariables{

	private String name;
	private String varName;
	private int time;
	private double varValue;
	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.ReportVariables#compute(net.sourceforge.jabm.event.SimEvent)
	 */
	@Override
	public void compute(SimEvent event) {
		eventOccurred(event);
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.ReportVariables#dispose(net.sourceforge.jabm.event.SimEvent)
	 */
	@Override
	public void dispose(SimEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.ReportVariables#initialise(net.sourceforge.jabm.event.SimEvent)
	 */
	@Override
	public void initialise(SimEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.ReportVariables#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	

	public void setName(String name){
		this.name=name;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.Report#getVariableBindings()
	 */
	@Override
	public Map<Object, Number> getVariableBindings() {
		LinkedHashMap<Object, Number> result = 
				new LinkedHashMap<Object, Number>();
			result.put(getName() + ".t", time);
			result.put(getName() + "." + varName , varValue);
			return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.event.EventListener#eventOccurred(net.sourceforge.jabm.event.SimEvent)
	 */
	@Override
	public void eventOccurred(SimEvent event) {
		if(event instanceof MacroSimEvent){
			MacroSimEvent ev = (MacroSimEvent)event;
			this.varName=ev.getVariableName();
			this.time=ev.getTime();
			this.varValue=ev.getVariableValue();
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.XYReportVariables#getX(int)
	 */
	@Override
	public Number getX(int seriesIndex) {
		return this.time;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.XYReportVariables#getY(int)
	 */
	@Override
	public Number getY(int seriesIndex) {
		return this.varValue;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.XYReportVariables#getNumberOfSeries()
	 */
	@Override
	public int getNumberOfSeries() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.XYReportVariables#getyVariableNames()
	 */
	@Override
	public List<Object> getyVariableNames() {
		LinkedList<Object> result = new LinkedList<Object>();
		result.add(getName() + "." + varName);
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.report.XYReportVariables#getxVariableName()
	 */
	@Override
	public String getxVariableName() {
		return getName() + ".t";
	}

}
