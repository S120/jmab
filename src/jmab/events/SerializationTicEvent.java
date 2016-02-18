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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import jmab.report.MacroVariableComputer;

/**
 * @author Alessandro Caiani and Antoine Godin
 * An event fired in order to compute the value of a specific macro variable to be reported.
 */
@SuppressWarnings("serial")
public class SerializationTicEvent extends MacroTicEvent {

	/**
	 * @param computer the specific {@link MacroVariableComputer} to be used in order to compute the value of
	 * the variable to be reported.
	 * @param variableName the name of the variable to be reported.
	 * VariableId the Id of the variable to be computed.
	 */
	int round;
	String fileName;
	BufferedOutputStream output;

	/**
	 * @param tic
	 */
	public SerializationTicEvent() {
		super();
	}

	/**
	 * @return the round
	 */
	public int getRound() {
		return round;
	}

	/**
	 * @param round the round to set
	 */
	public void setRound(int round) {
		this.round = round;
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
	
	
	public void writeBytes(byte[] content){
		try {
			output = new BufferedOutputStream(new FileOutputStream(fileName));
			output.write(ByteBuffer.allocate(4).putInt(content.length).array());
			output.write(content);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}