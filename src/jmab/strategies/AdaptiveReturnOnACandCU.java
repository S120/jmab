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
import java.util.List;

import jmab.agents.GoodDemander;
import jmab.agents.PriceSetterWithTargets;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import jmab.stockmatrix.Item;
import jmab.stockmatrix.Loan;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.distribution.AbstractDelegatedDistribution;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class AdaptiveReturnOnACandCU extends AbstractStrategy implements
		MarkupPricingStrategy {
	
	private double threshold; //to be set through the configuration file.
	private double adaptiveParameter;
	private AbstractDelegatedDistribution distribution;
	private double returnRate;
	private int idCapacity;
	private int idCapitalValue;
	private int idLoans;
	private int idProduction;
	private double capacityUtilisation;
	private double markUp;
	private boolean capitalOwner;
	private int productionId;
	private int realSalesId;

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
		double capitalValue = seller.getPassedValue(idCapitalValue, 1);
		double capacity = seller.getPassedValue(idCapacity, 1);
		if(capacity<=0)
			return price;
		//double capacityUtilisation = seller.getPassedValue(idProduction,1)/(4*seller.getPassedValue(idCapacity,1))+seller.getPassedValue(idProduction,2)/(4*seller.getPassedValue(idCapacity,2))+seller.getPassedValue(idProduction,3)/(4*seller.getPassedValue(idCapacity,3))+seller.getPassedValue(idProduction,4)/(4*seller.getPassedValue(idCapacity,4));

		List<Item> loans=seller.getItemsStockMatrix(false, idLoans);
		double totInterests=0;
		for(int i=0;i<loans.size();i++){
			Loan loan=(Loan)loans.get(i);
			if(loan.getAge()>0){
				double iRate=loan.getInterestRate();
				double interests=iRate*loan.getValue();
				totInterests +=interests;
				
			}
		}
		double debtBurden = totInterests;
		
		double previousMarkUp;
		if(capacity>0)
			previousMarkUp=(returnRate*capitalValue+debtBurden)/(capacity*capacityUtilisation);
		else
			previousMarkUp = price/seller.getPriceLowerBound();
		
		if (capitalOwner){
			double capacityUtilization=0.8;
			MacroSimulation macroSim = (MacroSimulation)((SimulationController)this.scheduler).getSimulation();
			if (macroSim.getRound()>1){
				double production = seller.getPassedValue(productionId, 1);
				capacityUtilization=production/capacity;
			}
			if(referenceVariable>threshold||capacityUtilization<0.8){
				returnRate-=(adaptiveParameter*returnRate*distribution.nextDouble());
			}else{
				returnRate+=(adaptiveParameter*returnRate*distribution.nextDouble());
			}
		}
		else{
			double pastSales=seller.getPassedValue(realSalesId, 1);
			double pastpastSales=seller.getPassedValue(realSalesId, 2);
			double realSalesChange= (double)(pastSales-pastpastSales)/ (double) pastpastSales;
			
			if(referenceVariable>threshold || realSalesChange<-0.0){
				returnRate-=(adaptiveParameter*returnRate*distribution.nextDouble());
			}
			else{
				returnRate+=(adaptiveParameter*returnRate*distribution.nextDouble());	
			}
		}
		//double markUp;
		
		if(capacity>0)
			markUp=(returnRate*capitalValue+debtBurden)/(capacity*capacityUtilisation);
		else
			markUp = price/seller.getPriceLowerBound();
		
		if (seller.getPriceLowerBound()!=0){
			price=seller.getPriceLowerBound()*(1+markUp);
		}
		else{
			double previousLowerBound=price/(1+previousMarkUp);
			price=previousLowerBound*(1+markUp);
			return price;
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

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getAdaptiveParameter() {
		return adaptiveParameter;
	}

	public void setAdaptiveParameter(double adaptiveParameter) {
		this.adaptiveParameter = adaptiveParameter;
	}

	public AbstractDelegatedDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(AbstractDelegatedDistribution distribution) {
		this.distribution = distribution;
	}

	public double getReturnRate() {
		return returnRate;
	}

	public void setReturnRate(double returnRate) {
		this.returnRate = returnRate;
	}

	public int getIdCapacity() {
		return idCapacity;
	}

	public void setIdCapacity(int idCapacity) {
		this.idCapacity = idCapacity;
	}

	public int getIdCapitalValue() {
		return idCapitalValue;
	}

	public void setIdCapitalValue(int idCapitalValue) {
		this.idCapitalValue = idCapitalValue;
	}

	public int getIdLoans() {
		return idLoans;
	}

	public void setIdLoans(int idLoans) {
		this.idLoans = idLoans;
	}
	
	public int getIdProduction() {
		return idProduction;
	}

	public void setIdProduction(int idProduction) {
		this.idProduction = idProduction;
	}

	public double getCapacityUtilisation() {
		return capacityUtilisation;
	}

	public void setCapacityUtilisation(double capacityUtilisation) {
		this.capacityUtilisation = capacityUtilisation;
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
	public boolean isCapitalOwner() {
		return capitalOwner;
	}

	/**
	 * @param capitalOwner the capitalOwner to set
	 */
	public void setCapitalOwner(boolean capitalOwner) {
		this.capitalOwner = capitalOwner;
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
//		buf.putDouble(this.markUp);
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
//		this.markUp = buf.getDouble();
	}
	

}
