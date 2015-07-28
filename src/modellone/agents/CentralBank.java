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
package modellone.agents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import jmab.agents.AbstractBank;
import jmab.agents.BondDemander;
import jmab.agents.BondSupplier;
import jmab.agents.CreditSupplier;
import jmab.agents.DepositSupplier;
import jmab.agents.MacroAgent;
import jmab.events.MacroTicEvent;
import jmab.goods.Bond;
import jmab.goods.Item;
import jmab.goods.Loan;
import jmab.population.MacroPopulation;
import modellone.StaticValues;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jabm.event.RoundFinishedEvent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 *
 */

//TODO to be set active in the configuration file using the constructor, the CB is always active.
//TODO here not considered the possibility that CB buys bonds.
@SuppressWarnings("serial")
public class CentralBank extends AbstractBank implements CreditSupplier, DepositSupplier, BondDemander {

	private double advancesInterestRate;
	private double reserveInterestRate;
	private int bondDemand;
	private double interestsOnAdvances;
	private double interestsOnBonds;
	private double bondInterestsReceived;
	
	/**
	 * @return the advancesInterestRate
	 */
	public double getAdvancesInterestRate() {
		return advancesInterestRate;
	}

	/**
	 * @param advancesInterestRate the advancesInterestRate to set
	 */
	public void setAdvancesInterestRate(double advancesInterestRate) {
		this.advancesInterestRate = advancesInterestRate;
	}

	/**
	 * @return the reserveInterestRate
	 */
	public double getReserveInterestRate() {
		return reserveInterestRate;
	}

	/**
	 * @param reserveInterestRate the reserveInterestRate to set
	 */
	public void setReserveInterestRate(double reserveInterestRate) {
		this.reserveInterestRate = reserveInterestRate;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.MacroAgent#onRoundFinished(net.sourceforge.jabm.event.RoundFinishedEvent)
	 */
	@Override
	public void onRoundFinished(RoundFinishedEvent event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jmab.agents.MacroAgent#initialiseCounterpart(net.sourceforge.jabm.agent.Agent, int)
	 */
	@Override
	public void initialiseCounterpart(Agent counterpart, int marketID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getInterestRate(jmab.agents.MacroAgent, double, int)
	 */
	@Override
	public double getInterestRate(int idLoanSM, MacroAgent creditDemander, double amount,
			int length) {
		return this.advancesInterestRate;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getLoanSupply(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double getLoanSupply(int loansId, MacroAgent creditDemander, double required) {
		return Double.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getDepositInterestRate(jmab.agents.MacroAgent, double)
	 */
	@Override
	public double getDepositInterestRate(MacroAgent creditDemander,
			double amount) {
		return 0;
	}
	

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onTicArrived(jmab.events.AgentTicEvent)
	 */
	@Override
	protected void onTicArrived(MacroTicEvent event) {
		if(event.getTic()==StaticValues.TIC_COMPUTEEXPECTATIONS){ 
			this.interestsOnAdvances=0;
			this.interestsOnBonds=0;
			if (this.getItemsStockMatrix(true, StaticValues.SM_ADVANCES).size()!=0){
				List<Item> advances=this.getItemsStockMatrix(true,StaticValues.SM_ADVANCES);
				double advancesValue=0;
				for(int i=0;i<advances.size();i++){
					Loan advance=(Loan)advances.get(i);
					advancesValue+=advance.getValue();
				}
				this.interestsOnAdvances= advancesValue*this.advancesInterestRate;
			}
			if (this.getItemsStockMatrix(true, StaticValues.SM_BONDS).size()!=0){
				Bond bonds= (Bond) this.getItemStockMatrix(true, StaticValues.SM_BONDS);
				this.interestsOnBonds=bonds.getValue()*bonds.getInterestRate();
			}
			this.setActive(true, StaticValues.MKT_ADVANCES);
		}
		else if(event.getTic()==StaticValues.TIC_UPDATEEXPECTATIONS)
			this.cleanSM();
		else if (event.getTic()==StaticValues.TIC_CBBONDSPURCHASES)
			this.determineCBBondsPurchases();
	}
	
	public double getCBProfits(){
		return this.interestsOnAdvances+this.interestsOnBonds;
	}
	
	private void determineCBBondsPurchases() {
		SimulationController controller = (SimulationController)this.getScheduler();
		MacroPopulation macroPop = (MacroPopulation) controller.getPopulation();
		Population banks = macroPop.getPopulation(StaticValues.BANKS_ID);
		int banksBondDemand=0;
		for(Agent b:banks.getAgents()){
			Bank tempB= (Bank) b;
			if(tempB.isActive(StaticValues.MKT_BONDS))
				banksBondDemand+=tempB.getBondDemand();
		}
		Population government=macroPop.getPopulation(StaticValues.GOVERNMENT_ID);
		
		Government gov= (Government) government.getAgentList().get(0);
		int bondsSupply=gov.getBondSupply();
		this.bondDemand=bondsSupply-banksBondDemand;
		Bond bondsIssued = (Bond) gov.getItemStockMatrix(false, StaticValues.SM_BONDS, gov); 
		if (bondsIssued!=null && bondDemand>0){
			//1. Determine quantity, price and total costs
			double price=bondsIssued.getPrice();
			double interestRate=bondsIssued.getInterestRate();
			int maturity=bondsIssued.getMaturity();
			Bond bondsPurchased = new Bond(price*bondDemand, (double)bondDemand, this, gov, maturity, interestRate, price);
			bondsIssued.setQuantity(bondsIssued.getQuantity()-bondDemand);
			this.addItemStockMatrix(bondsPurchased, true, StaticValues.SM_BONDS);
			gov.addItemStockMatrix(bondsPurchased, false, StaticValues.SM_BONDS);
			Item govRes= (Item) gov.getItemStockMatrix(true, StaticValues.SM_RESERVES);
			govRes.setValue(govRes.getValue()+price*bondDemand);
			//7. If there are no more bonds to be sold, then the supplier is deactivated.
			if (bondsIssued.getQuantity()==0){
				gov.removeItemStockMatrix(bondsIssued, false, StaticValues.SM_BONDS);
				gov.setActive(false, StaticValues.MKT_BONDS);
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#getTotalLoansSupply()
	 */
	@Override
	public double getTotalLoansSupply(int loansId) {
		return Double.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.CreditSupplier#setTotalLoansSupply(double)
	 */
	@Override
	public void setTotalLoansSupply(int loansId, double d) {}

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onAgentArrival(net.sourceforge.jabm.event.AgentArrivalEvent)
	 */
	@Override
	public void onAgentArrival(AgentArrivalEvent event) {
		
	}

	/**
	 * This is overriden because the central bank is having special cases.
	 * a. In the case of a transfer of reserves between two banks
	 * b. In the case of doing Government transfers
	 */
	@Override
	public void transfer(Item paying, Item receiving, double amount){
		AbstractBank otherBank =  (AbstractBank)receiving.getLiabilityHolder();
		//If the central bank is both the payer and the receiver
		if(otherBank.getAgentId()==this.getAgentId()){
			paying.setValue(paying.getValue()-amount);
			receiving.setValue(receiving.getValue()+amount);
		//If the central bank is doing Government transfers, then it needs to update the reserve account of the government,
		// the deposit account of the receiver and the reserve account of the bank holding the deposit
		}else if(paying.getAssetHolder().getPopulationId()==StaticValues.GOVERNMENT_ID){
			paying.setValue(paying.getValue()-amount);
			receiving.setValue(receiving.getValue()+amount);
			Item oBankRes = otherBank.getCounterpartItem(receiving, paying);
			oBankRes.setValue(oBankRes.getValue()+amount);
		}else{
			super.transfer(paying, receiving, amount);
		}
	}
	
	
	
	@Override
	public Item getCounterpartItem(Item liability, Item otherLiability){
		return otherLiability;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#getBondsDemand(double, jmab.agents.BondSupplier)
	 */
	@Override
	public int getBondsDemand(double price, BondSupplier issuer) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#getPayingStocks(int, jmab.goods.Item)
	 */
	@Override
	public List<Item> getPayingStocks(int idBondSM, Item payableStock) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.BondDemander#setBondInterestsReceived(double)
	 */
	@Override
	public void setBondInterestsReceived(double interests) {
		this.bondInterestsReceived = interests;
		
	}

	/**
	 * @return the bondInterestsReceived
	 */
	public double getBondInterestsReceived() {
		return bondInterestsReceived;
	}
	
	/**
	 * Populates the agent characteristics using the byte array content. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][advancesInterestRate][reserveInterestRate][interestsOnAdvances][interestsOnBonds]
	 * [bondInterestsReceived][bondDemand][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public void populateAgent(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] macroBytes = new byte[buf.getInt()];
		buf.get(macroBytes);
		super.populateCharacteristics(macroBytes, pop);
		advancesInterestRate = buf.getDouble();
		reserveInterestRate = buf.getDouble();
		interestsOnAdvances = buf.getDouble();
		interestsOnBonds = buf.getDouble();
		bondInterestsReceived = buf.getDouble();
		bondDemand = buf.getInt();
		int matSize = buf.getInt();
		if(matSize>0){
			byte[] smBytes = new byte[matSize];
			buf.get(smBytes);
			this.populateStockMatrixBytes(smBytes, pop);
		}
		int expSize = buf.getInt();
		if(expSize>0){
			byte[] expBytes = new byte[expSize];
			buf.get(expBytes);
			this.populateExpectationsBytes(expBytes);
		}
		int lagSize = buf.getInt();
		if(lagSize>0){
			byte[] lagBytes = new byte[lagSize];
			buf.get(lagBytes);
			this.populatePassedValuesBytes(lagBytes);
		}
		int stratSize = buf.getInt();
		if(stratSize>0){
			byte[] stratBytes = new byte[stratSize];
			buf.get(stratBytes);
			this.populateStrategies(stratBytes, pop);
		}
	}

	/**
	 * Generates the byte array containing all relevant informations regarding the central bank agent. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][advancesInterestRate][reserveInterestRate][interestsOnAdvances][interestsOnBonds]
	 * [bondInterestsReceived][bondDemand][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] charBytes = super.getAgentCharacteristicsBytes();
			out.write(ByteBuffer.allocate(4).putInt(charBytes.length).array());
			out.write(charBytes);
			ByteBuffer buf = ByteBuffer.allocate(44);
			buf.putDouble(advancesInterestRate);
			buf.putDouble(reserveInterestRate);
			buf.putDouble(interestsOnAdvances);
			buf.putDouble(interestsOnBonds);
			buf.putDouble(bondInterestsReceived);
			buf.putInt(bondDemand);
			out.write(buf.array());
			byte[] smBytes = super.getStockMatrixBytes();
			out.write(ByteBuffer.allocate(4).putInt(smBytes.length).array());
			out.write(smBytes);
			byte[] expBytes = super.getExpectationsBytes();
			out.write(ByteBuffer.allocate(4).putInt(expBytes.length).array());
			out.write(expBytes);
			byte[] passedValBytes = super.getPassedValuesBytes();
			out.write(ByteBuffer.allocate(4).putInt(passedValBytes.length).array());
			out.write(passedValBytes);
			byte[] stratsBytes = super.getStrategiesBytes();
			out.write(ByteBuffer.allocate(4).putInt(stratsBytes.length).array());
			out.write(stratsBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * Populates the stockMatrix with the byte array content. The structure of the stock matrix is the following:
	 * [nbStockTypes]
	 * for each type of stocks
	 * 	[IdStock][nbItems]
	 * 		for each Item
	 * 			[itemSize][itemStructure]
	 * 		end for
	 * end for 	
	 */
	@Override
	public void populateStockMatrixBytes(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbStockTypes = buf.getInt();
		for(int i = 0 ; i < nbStockTypes ; i++){
			int stockId = buf.getInt();
			int nbStocks = buf.getInt();
			for(int j = 0 ; j < nbStocks ; j++){
				int itemSize = buf.getInt();
				byte[] itemData = new byte[itemSize];
				buf.get(itemData);
				Item it;
				switch(stockId){
				case StaticValues.SM_ADVANCES:
					it = new Loan(itemData, pop, this);
					break;
				default:
					it = new Bond(itemData, pop, this);
					break;
				}
				this.addItemStockMatrix(it, true, stockId);
				MacroAgent liabHolder = it.getLiabilityHolder();
				liabHolder.addItemStockMatrix(it, false, stockId);
			}
		}	
	}
	
}
