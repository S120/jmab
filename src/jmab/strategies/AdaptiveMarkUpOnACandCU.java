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
package jmab.strategies;

import java.nio.ByteBuffer;

import jmab.agents.GoodDemander;
import jmab.agents.PriceSetterWithTargets;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class AdaptiveMarkUpOnACandCU extends AbstractStrategy implements MarkupPricingStrategy {

	private double threshold; //to be set through the configuration file.
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution;
	private double markUp;
	private boolean capitalOwner;
	private int capacityId;
	private int productionId;
	private int realSalesId;
	/**
	 * 
	 */
	public AdaptiveMarkUpOnACandCU() {
		super();
		// TODO Auto-generated constructor stub
	}

	/** 
	 * This strategy changes in an adaptive way the price asked by producers on their output.
	 * If the value of a referenceVariable (say for example the ratio of inventories over past Sales amount of inventories) is higher than a threshold,
	 * then it lowers the price to make its output more attractive, and vice versa. 
	 * The adaptive change depends on the value of the adaptiveParamenter and it is stochastic 
	 * (i.e. a draw from a Uniform distribution).
	 * The price has a lower bound computed by the agent who uses the strategy (say for example expected
	 * average costs).
	 * N.B. in the case referenceVariable=inventories stock we are assuming that inventories are updated by 
	 * the amount of current production after the pricing tick has taken place.
	 */
	@Override
	public double computePrice() {
		PriceSetterWithTargets seller=(PriceSetterWithTargets) this.getAgent();
		double referenceVariable= seller.getReferenceVariableForPrice();
		double price = seller.getPrice();
		double previousLowerBound=price/(1+markUp);
		if (capitalOwner){
			double capacityUtilization=0.8;
			MacroSimulation macroSim = (MacroSimulation)((SimulationController)this.scheduler).getSimulation();
			if (macroSim.getRound()>1){
				double capacity = seller.getPassedValue(capacityId, 1);
				double production = seller.getPassedValue(productionId, 1);
				capacityUtilization=production/capacity;
			}
			if(referenceVariable>threshold||capacityUtilization<0.8){
				markUp-=(adaptiveParameter*markUp*distribution.nextDouble());
			}else{
				markUp+=(adaptiveParameter*markUp*distribution.nextDouble());

			}
		}
		else{
			double pastSales=seller.getPassedValue(realSalesId, 1);
			double pastpastSales=seller.getPassedValue(realSalesId, 2);
			double realSalesChange= (double)(pastSales-pastpastSales)/ (double) pastpastSales;
			if(referenceVariable>threshold || realSalesChange<-0.0){
				markUp-=(adaptiveParameter*markUp*distribution.nextDouble());
			}
			else{
				markUp+=(adaptiveParameter*markUp*distribution.nextDouble());

			}
		}

		if (seller.getPriceLowerBound()!=0){
			price=seller.getPriceLowerBound()*(1+markUp);
			if (price>seller.getPriceLowerBound()){
				return price;
			}
			else {
				return seller.getPriceLowerBound();
			}
		}
		else{
			price=previousLowerBound*(1+markUp);
			return price;
		}

	}

	/* (non-Javadoc)
	 * @see jmab.strategies.PricingStrategy#computePriceForSpecificBuyer(jmab.agents.GoodDemander, double, boolean)
	 */
	@Override
	public double computePriceForSpecificBuyer(GoodDemander buyer,
			double demand, boolean real) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the distribution
	 */
	public AbstractDelegatedDistribution getDistribution() {
		return distribution;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribution(AbstractDelegatedDistribution distribution) {
		this.distribution = distribution;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the adaptiveParameter
	 */
	public double getAdaptiveParameter() {
		return adaptiveParameter;
	}

	/**
	 * @param adaptiveParameter the adaptiveParameter to set
	 */
	public void setAdaptiveParameter(double adaptiveParameter) {
		this.adaptiveParameter = adaptiveParameter;
	}

	/**
	 * @return the markUp
	 */
	public double getMarkUp() {
		return markUp;
	}

	/**
	 * @param markUp the markUp to set
	 */
	public void setMarkUp(double markUp) {
		this.markUp = markUp;
	}

	/**
	 * @return the capitalOwner
	 */
	public boolean getCapitalOwner() {
		return capitalOwner;
	}

	/**
	 * @param capitalOwner the capitalOwner to set
	 */
	public void setCapitalOwner(boolean capitalOwner) {
		this.capitalOwner = capitalOwner;
	}

	/**
	 * @return the capacityId
	 */
	public int getCapacityId() {
		return capacityId;
	}

	/**
	 * @param capacityId the capacityId to set
	 */
	public void setCapacityId(int capacityId) {
		this.capacityId = capacityId;
	}

	/**
	 * @return the productionId
	 */
	public int getProductionId() {
		return productionId;
	}

	/**
	 * @param productionId the productionId to set
	 */
	public void setProductionId(int productionId) {
		this.productionId = productionId;
	}

	/**
	 * @return the realSalesId
	 */
	public int getRealSalesId() {
		return realSalesId;
	}

	/**
	 * @param realSalesId the realSalesId to set
	 */
	public void setRealSalesId(int realSalesId) {
		this.realSalesId = realSalesId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][markUp]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(24);
		buf.putDouble(threshold);
		buf.putDouble(adaptiveParameter);
		buf.putDouble(this.markUp);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][markUp]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.markUp = buf.getDouble();
	}


}
