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
package jmab.stockmatrix;

import java.nio.ByteBuffer;
import java.util.Collection;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 * Asbtract implementation of the interface Item. See Interface Item for more details.
 *
 */
public abstract class AbstractItem implements Item {

	protected double value;
	protected double quantity;
	protected int age;
	protected MacroAgent assetHolder;
	protected MacroAgent liabilityHolder;
	protected int SMId;
	
	/**
	 * 
	 */
	public AbstractItem() {
		value=0;
		quantity=0;
		age=0;
		assetHolder=null;
		liabilityHolder=null;
	}
	
	/**
	 * @param value
	 * @param assetHolder
	 * @param liabilityHolder
	 */
	public AbstractItem(double value, double quantity, MacroAgent assetHolder,
			MacroAgent liabilityHolder) {
		super();
		this.value = value;
		this.quantity= quantity;
		this.assetHolder = assetHolder;
		this.liabilityHolder = liabilityHolder;
	}
	
	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (48 bytes in total): 
	 * [SMId][age][value][quantity][popId of liabilityHolder][liabilityHolderId]
	 * @param content the byte array
	 * @param population the population of existing agents
	 */
	public AbstractItem(byte[] content, MacroPopulation population, MacroAgent aHolder){
		ByteBuffer reader = ByteBuffer.wrap(content);
		this.SMId = reader.getInt();
		this.age = reader.getInt();
		this.value = reader.getDouble();
		this.quantity = reader.getDouble();
		this.assetHolder = aHolder;
		Collection<Agent> lHolders = population.getPopulation(reader.getInt()).getAgents();
		long lHolderId = reader.getLong(); 
		for(Agent l:lHolders){
			MacroAgent pot = (MacroAgent) l;
			if(pot.getAgentId()==lHolderId){
				this.liabilityHolder=pot;
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getAssetHolder()
	 */
	@Override
	public MacroAgent getAssetHolder() {
		return assetHolder;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#setAssetHolder(SimpleAbstractAgent)
	 */
	@Override
	public void setAssetHolder(MacroAgent assetHolder) {
		this.assetHolder = assetHolder;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getLiabilityHolder()
	 */
	@Override
	public MacroAgent getLiabilityHolder() {
		return liabilityHolder;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#setLiabilityHolder(SimpleAbstractAgent)
	 */
	@Override
	public void setLiabilityHolder(MacroAgent liabilityHolder) {
		this.liabilityHolder = liabilityHolder;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getValue()
	 */
	@Override
	public double getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#setValue(double)
	 */
	@Override
	public void setValue(double value) {
		this.value=value;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#getQuantity()
	 */
	@Override
	public double getQuantity() {
		return quantity;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.Item#setQuantity(double)
	 */
	@Override
	public void setQuantity(double quantity) {
		this.quantity=quantity;
	}

	/**
	 * @return the sMId
	 */
	public int getSMId() {
		return SMId;
	}

	/**
	 * @param sMId the sMId to set
	 */
	public void setSMId(int sMId) {
		SMId = sMId;
	}
	
	public abstract boolean remove();
	
	public abstract void update();

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * generates the byte array representation of the item. The structure of the array is the following (36 bytes in total):
	 * [SMId][age][value][quantity][popId of liabilityHolder][liabilityHolderId]
	 * @return the byte array containing the relevant information of the item
	 */
	public byte[] getBytes(){
		ByteBuffer buffer = ByteBuffer.allocate(36);
		buffer.putInt(SMId);
		buffer.putInt(age);
		buffer.putDouble(value);
		buffer.putDouble(quantity);
		buffer.putInt(liabilityHolder.getPopulationId());
		buffer.putLong(liabilityHolder.getAgentId());
		return buffer.array();
	}
	
}
