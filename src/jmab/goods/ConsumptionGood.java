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
package jmab.goods;

import java.nio.ByteBuffer;

import jmab.agents.MacroAgent;
import jmab.population.MacroPopulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class ConsumptionGood extends AbstractGood {
	
	private int consumptionGoodDuration;
	
	/**
	 * 
	 */
	public ConsumptionGood() {
		super();
	}

	/**
	 * @param value 
	 * @param quantity the quantity of Consumption Good
	 * @param assetHolder the agent who has the Consumption Good
	 * @param liabilityHolder will be empty since there is no liability side for a real good
	 * @param price the price of the Consumption Good
	 * @param consumptionGoodDuration the duration of the consumption good (0 if instantaneously consumed, not 0 if durable)
	 */
	public ConsumptionGood(double value, double quantity,
			MacroAgent assetHolder, MacroAgent producer, double price, int consumptionGoodDuration) {
		super(value, quantity, assetHolder, null);
		this.producer=producer;
		this.price=price;
		this.consumptionGoodDuration=consumptionGoodDuration;
		this.age=0;
	}

	/**
	 * Creates the item from an array of bytes, the structure for the bytes is the following (Super Structure + 4bytes in total):
	 * [SuperStructure][consumptionGoodDuration]
	 * @param content
	 * @param population
	 */
	public ConsumptionGood(byte[] content, MacroPopulation population, MacroAgent aHolder){
		super(content, population, aHolder);
		ByteBuffer reader = ByteBuffer.wrap(content);
		reader.position(content.length-4);
		this.consumptionGoodDuration = reader.getInt();
	}
	
	/**
	 * @return the consumptionGoodDuration
	 */
	public int getConsumptionGoodDuration() {
		return consumptionGoodDuration;
	}

	/**
	 * @param consumptionGoodDuration the consumptionGoodDuration to set
	 */
	public void setConsumptionGoodDuration(int consumptionGoodDuration) {
		this.consumptionGoodDuration = consumptionGoodDuration;
	}

	/* (non-Javadoc)
	 * @see jmab.goods.AbstractItem#remove()
	 */
	@Override
	public boolean remove() {
		return age>consumptionGoodDuration;
	}

	@Override
	public double getValue(){
		if(this.assetHolder.getAgentId()==this.producer.getAgentId()){
			return this.unitCost*this.quantity;
		}else
			return this.value;
	}
	
	/**
	 * generates the byte array representation of the item. The structure of the array is the following (Super structure + 4 bytes in total):
	 * [SuperStructure][consumptionGoodDuration]
	 * @return the byte array containing the relevant information of the item
	 */
	@Override
	public byte[] getBytes(){
		byte[] prevResult = super.getBytes();
		byte[] result = new byte[prevResult.length+4];
		ByteBuffer buffer = ByteBuffer.wrap(result).put(prevResult);
		buffer.putInt(this.consumptionGoodDuration);
		return result;
	}
	
}
