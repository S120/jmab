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

import java.nio.ByteBuffer;
import java.util.Collection;

import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;


/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractHousehold extends SimpleAbstractAgent implements LaborSupplier {

	protected double wage; 
	protected MacroAgent employer;
	
	/**
	 * 
	 */
	public AbstractHousehold() {}


	/**
	 * @return the wage
	 */
	public double getWage() {
		return wage;
	}

	/**
	 * @param wage the wage to set
	 */
	public void setWage(double wage) {
		this.wage = wage;
	}
	/**
	 * @return the employer
	 */
	public MacroAgent getEmployer() {
		return employer;
	}

	/**
	 * @param employer the employer to set
	 */
	public void setEmployer(LaborDemander employer) {
		this.employer = employer;
	}
	
	public double getGrossIncome(){
		if (this.employer==null){
			return 0;
		}
		else {
			return this.getWage();
		}
	}
	
	public double getNetIncome(){
		if (this.employer==null){
			return 0;
		}
		else {
			return this.getWage();
		}
	}
	
	public boolean isEmployed(){
		return this.employer!=null;
	}
	
	/**
	 * Generates the byte array representing the characteristics of the agent. The structure is the following
	 * [superStructSize][superStruct][wage][employed][employerPopId][employerId]
	 * @return the byte array
	 */
	public byte[] getAgentCharacteristicsBytes(){
		byte[] superStruct = super.getAgentCharacteristicsBytes();
		ByteBuffer buf;
		if(this.isEmployed())
			buf = ByteBuffer.allocate(superStruct.length+25);
		else
			buf = ByteBuffer.allocate(superStruct.length+13);
		buf.putInt(superStruct.length);
		buf.put(superStruct);
		buf.putDouble(wage);
		if(this.isEmployed()){
			buf.put((byte)1);
			buf.putInt(this.employer.getPopulationId());
			buf.putLong(this.employer.getAgentId());
		}else
			buf.put((byte)0);
		return buf.array();
	}

	/**
	 * Populates the characteristics of the agent using the byte array content. The structure is the following
	 * [superStructSize][superStruct][wage]
	 * @param the byte array
	 */
	public void populateCharacteristics(byte[] content,MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int superStrucSize = buf.getInt();
		byte[] superStruct = new byte[superStrucSize];
		buf.get(superStruct);
		super.populateCharacteristics(superStruct, pop);
		this.wage = buf.getDouble();
		boolean empl = buf.get()==(byte)1;
		if(empl){
			int popId = buf.getInt();
			long employerId = buf.getLong();
			Collection<Agent> potEmployers = pop.getPopulation(popId).getAgents();
			for(Agent a:potEmployers){
				MacroAgent potEmployer = (MacroAgent) a;
				if(potEmployer.getAgentId()==employerId){
					this.employer=potEmployer;
					((LaborDemander)this.employer).addEmployee(this);
					break;
				}
			}
		}
	}
	
}
