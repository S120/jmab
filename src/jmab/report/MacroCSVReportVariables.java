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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sourceforge.jabm.report.CSVReportVariables;
import net.sourceforge.jabm.report.CSVWriter;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class MacroCSVReportVariables extends CSVReportVariables {
	
	protected char sep=',';
	
	public void createWriter() {
		try {
			String fileName = getFileName();
			writer = new CSVWriter(new FileOutputStream(fileName),sep);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the sep
	 */
	public char getSep() {
		return sep;
	}

	/**
	 * @param sep the sep to set
	 */
	public void setSep(char sep) {
		this.sep = sep;
	}

}
