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
 */
public abstract class AbstractGood extends AbstractItem {

	protected MacroAgent producer;
	protected double price;
	protected double unitCost;
	
	public AbstractGood(){
		super();
		this.price=0;
		this.unitCost=0;
	}
	
	public AbstractGood(double value, double quantity, MacroAgent assetHolder,
			MacroAgent liabilityHolder){
		super(value,quantity,assetHolder,liabilityHolder);
	}
	
	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Item Structure + 28bytes in total):
	 * [age][value][quantity][price][unitCost][producer populationId][producerId]
	 * @param content
	 * @param population
	 */
	public AbstractGood(byte[] content, MacroPopulation population, MacroAgent aHolder){
		ByteBuffer reader = ByteBuffer.wrap(content);
		this.age = reader.getInt();
		this.value = reader.getDouble();
		this.quantity = reader.getDouble();
		this.assetHolder = aHolder;
		this.price = reader.getDouble();
		this.unitCost = reader.getDouble();
		Collection<Agent> producers = population.getPopulation(reader.getInt()).getAgents();
		long producerId = reader.getLong(); 
		for(Agent p:producers){
			MacroAgent pot = (MacroAgent) p;
			if(pot.getAgentId()==producerId){
				this.producer=pot;
				break;
			}
		}
		
	}
	
	/**
	 * @return the producer
	 */
	public MacroAgent getProducer() {
		return producer;
	}

	/**
	 * @param producer the producer to set
	 */
	public void setProducer(MacroAgent producer) {
		this.producer = producer;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the unitCost
	 */
	public double getUnitCost() {
		return unitCost;
	}

	/**
	 * @param unitCost the unitCost to set
	 */
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#update()
	 */
	@Override
	public void update() {
		this.age+=1;
	}

	/**
	 * generates the byte array representation of the item. The structure of the array is the following (48 bytes in total):
	 * [age][value][quantity][price][unitCost][producer populationId][producerId]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		ByteBuffer buffer = ByteBuffer.allocate(48);
		buffer.putInt(age);
		buffer.putDouble(value);
		buffer.putDouble(quantity);
		buffer.putDouble(price);
		buffer.putDouble(unitCost);
		buffer.putInt(producer.getPopulationId());
		buffer.putLong(producer.getAgentId());
		return buffer.array();
	}
	
}
