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

import java.io.Serializable;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.SeriesReportVariables;
import net.sourceforge.jabm.report.Timeseries;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class MicroSeriesReportVariables extends SeriesReportVariables 
			implements Serializable, InitializingBean, Timeseries{
	
	@Override
	public void compute(SimEvent event) {
		this.reportVariables.compute(event);
		super.compute(event);
	}

}
