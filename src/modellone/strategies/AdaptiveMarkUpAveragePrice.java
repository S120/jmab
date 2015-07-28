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
package modellone.strategies;

import java.nio.ByteBuffer;

import jmab.agents.GoodDemander;
import jmab.agents.PriceSetterWithTargets;
import jmab.population.MacroPopulation;
import jmab.strategies.PricingStrategy;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class AdaptiveMarkUpAveragePrice extends AbstractStrategy implements
		PricingStrategy {
	
	private double threshold; //to be set through the configuration file.
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution;
	private double markUp;
	private int mktId;

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
//		SimulationController controller = (SimulationController)this.getScheduler();
//		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
//	    Population firms;
//		if (mktId==StaticValues.MKT_CONSGOOD){
//			 firms = macroPop.getPopulation(StaticValues.CONSUMPTIONFIRMS_ID);
//		}
//		else{
//			 firms= macroPop.getPopulation(StaticValues.CAPITALFIRMS_ID);
//		}
//		double rSales=0;
//		for (Agent f:firms.getAgents()){
//			AbstractFirm firm= (AbstractFirm) f;
//			rSales+=firm.getPassedValue(StaticValues.LAG_REALSALES, 1);
//		}

		//		double avMktPrice=0;
//		for (Agent f:firms.getAgents()){
//			AbstractFirm firm= (AbstractFirm) f;
//			avMktPrice+=(firm.getPassedValue(StaticValues.LAG_REALSALES, 1)/rSales)*firm.getPassedValue(StaticValues.LAG_PRICE, 1);
//		}
		
		PriceSetterWithTargets seller=(PriceSetterWithTargets) this.getAgent();
		double referenceVariable= seller.getReferenceVariableForPrice();
		double price = seller.getPrice();
		double previousLowerBound=price/(1+markUp);
		if(referenceVariable>threshold){
			markUp-=(adaptiveParameter*markUp*distribution.nextDouble());
		}else{
			markUp+=(adaptiveParameter*markUp*distribution.nextDouble());
			
		}
		if (seller.getPriceLowerBound()!=0){
			price=seller.getPriceLowerBound()*(1+markUp);
		}
		else{
			price=previousLowerBound*(1+markUp);
		}
		if (price>seller.getPriceLowerBound()){
			return price;
		}
		else {
			return seller.getPriceLowerBound();
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
	 * @return the mktId
	 */
	public int getMktId() {
		return mktId;
	}

	/**
	 * @param mktId the mktId to set
	 */
	public void setMktId(int mktId) {
		this.mktId = mktId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [threshold][adaptiveParameter][markup][mktId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(28);
		buf.putDouble(this.threshold);
		buf.putDouble(this.adaptiveParameter);
		buf.putDouble(this.markUp);
		buf.putInt(this.mktId);
		return buf.array();
	}

	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [threshold][adaptiveParameter][markup][mktId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.threshold = buf.getDouble();
		this.adaptiveParameter = buf.getDouble();
		this.markUp = buf.getDouble();
		this.mktId = buf.getInt();
	}

	

}
