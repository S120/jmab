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

import jmab.agents.AbstractFirm;
import jmab.agents.MacroAgent;
import jmab.goods.Deposit;
import jmab.population.MacroPopulation;
import jmab.strategies.DividendsStrategy;
import modellone.agents.Bank;
import modellone.agents.Households;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.strategy.AbstractStrategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class FixedShareOfProfitsToPopulationAsShareOfWealthDividends extends AbstractStrategy implements
DividendsStrategy {

	int profitsLagId;
	double profitShare;
	int receiversId;
	int depositId;
	int reservesId;


	/* (non-Javadoc)
	 * @see jmab.strategies.DividendsStrategy#payDividends()
	 */
	@Override
	public void payDividends() {
		MacroAgent dividendPayer = (MacroAgent)this.agent;
		double profits = dividendPayer.getPassedValue(profitsLagId, 0);	
		if (profits>0){
			Population receivers = ((MacroPopulation)((SimulationController)this.scheduler).getPopulation()).getPopulation(receiversId);
			double totalNW = 0;
			for(Agent receiver:receivers.getAgents()){
				totalNW+=((MacroAgent)receiver).getNetWealth();
			}
			if (dividendPayer instanceof Bank){
				Deposit payerDep = (Deposit)dividendPayer.getItemStockMatrix(true, reservesId);
				//if(profits>payerDep.getValue()){
					//profits=payerDep.getValue();
				//}
				Bank bank= (Bank) dividendPayer;
				bank.setDividends(profits*profitShare);
				for(Agent rec:receivers.getAgents()){
					Households receiver =(Households) rec; 
					double nw = receiver.getNetWealth();
					Deposit recDep = (Deposit)receiver.getItemStockMatrix(true, depositId);
					double toPay=profits*profitShare*nw/totalNW;			
					recDep.setValue(recDep.getValue()+toPay);
					payerDep.setValue(payerDep.getValue()-toPay);
					Deposit otherBankReserves = (Deposit)((Bank)recDep.getLiabilityHolder()).getItemStockMatrix(true, reservesId);
					otherBankReserves.setValue(otherBankReserves.getValue()+toPay);
					receiver.setDividendsReceived(receiver.getDividendsReceived()+toPay);
				}
			}
			else{
				if (dividendPayer.getItemStockMatrix(true, depositId).getValue()>profits*profitShare){
					Deposit payerDep = (Deposit)dividendPayer.getItemStockMatrix(true, depositId);
					if(profits>payerDep.getValue()){
						profits=payerDep.getValue();
					}
					AbstractFirm firm= (AbstractFirm) dividendPayer;
					firm.setDividends(profits*profitShare);
					for(Agent rec:receivers.getAgents()){
						Households receiver =(Households) rec; 
						double nw = receiver.getNetWealth();
						double toPay=profits*profitShare*nw/totalNW;
						Deposit recDep = (Deposit)receiver.getItemStockMatrix(true, depositId);
						((Bank)payerDep.getLiabilityHolder()).transfer(payerDep, recDep,toPay);
						receiver.setDividendsReceived(receiver.getDividendsReceived()+toPay);
					}
				}
				
			}
		}


	}


	/**
	 * @return the reservesId
	 */
	public int getReservesId() {
		return reservesId;
	}


	/**
	 * @param reservesId the reservesId to set
	 */
	public void setReservesId(int reservesId) {
		this.reservesId = reservesId;
	}


	/**
	 * @return the profitsLagId
	 */
	public int getProfitsLagId() {
		return profitsLagId;
	}

	/**
	 * @param profitsLagId the profitsLagId to set
	 */
	public void setProfitsLagId(int profitsLagId) {
		this.profitsLagId = profitsLagId;
	}

	/**
	 * @return the profitShare
	 */
	public double getProfitShare() {
		return profitShare;
	}

	/**
	 * @param profitShare the profitShare to set
	 */
	public void setProfitShare(double profitShare) {
		this.profitShare = profitShare;
	}

	/**
	 * @return the receiversId
	 */
	public int getReceiversId() {
		return receiversId;
	}

	/**
	 * @param receiversId the receiversId to set
	 */
	public void setReceiversId(int receiversId) {
		this.receiversId = receiversId;
	}

	/**
	 * @return the depositId
	 */
	public int getDepositId() {
		return depositId;
	}

	/**
	 * @param depositId the depositId to set
	 */
	public void setDepositId(int depositId) {
		this.depositId = depositId;
	}

	/**
	 * Generate the byte array structure of the strategy. The structure is as follow:
	 * [profitShare][profitsLagId][receiversId][depositId][reservesId]
	 * @return the byte array content
	 */
	@Override
	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(24);
		buf.putDouble(this.profitShare);
		buf.putInt(this.profitsLagId);
		buf.putInt(this.receiversId);
		buf.putInt(this.depositId);
		buf.putInt(this.reservesId);
		return buf.array();
	}


	/**
	 * Populates the strategy from the byte array content. The structure should be as follows:
	 * [profitShare][profitsLagId][receiversId][depositId][reservesId]
	 * @param content the byte array containing the structure of the strategy
	 * @param pop the Macro Population of agents
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.profitShare = buf.getDouble();
		this.profitsLagId = buf.getInt();
		this.receiversId = buf.getInt();
		this.depositId = buf.getInt();
		this.reservesId = buf.getInt();
	}
	
}
