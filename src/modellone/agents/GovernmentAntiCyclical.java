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
import java.util.Collections;

import jmab.agents.BondSupplier;
import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.events.MacroTicEvent;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import modellone.StaticValues;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Note that the government uses a reserve account in the central bank rather than a deposit account due to
 * the bond market.
 */
/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class GovernmentAntiCyclical extends Government implements LaborDemander, BondSupplier{
	
	double unemploymentBenefit;
	protected double doleExpenditure;
	protected double profitsFromCB;
	


	

	/**
	 * @return the unemploymentBenefit
	 */
	public double getUnemploymentBenefit() {
		return unemploymentBenefit;
	}

	/**
	 * @param unemploymentBenefit the unemploymentBenefit to set
	 */
	public void setUnemploymentBenefit(double unemploymentBenefit) {
		this.unemploymentBenefit = unemploymentBenefit;
	}

	/* (non-Javadoc)
	 * @see jmab.agents.SimpleAbstractAgent#onTicArrived(AgentTicEvent)
	 */
	@Override
	protected void onTicArrived(MacroTicEvent event) {
		switch(event.getTic()){
		case StaticValues.TIC_GOVERNMENTLABOR:
			computeLaborDemand();
			break;
		case StaticValues.TIC_TAXES:
			collectTaxes(event.getSimulationController());
			break;
		case StaticValues.TIC_BONDINTERESTS:
			payInterests();
			break;
		case StaticValues.TIC_BONDSUPPLY:
			receiveCBProfits();
			determineBondsInterestRate();
			emitBonds();
			break;
		case StaticValues.TIC_WAGEPAYMENT:
			payWages();
			payUnemploymentBenefits(event.getSimulationController());
			break;
		case StaticValues.TIC_UPDATEEXPECTATIONS:
			this.updateAggregateVariables();
			break;
		}
	}

	/**
	 * 
	 */
	private void receiveCBProfits() {
		Item deposit=this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
		CentralBank cb=(CentralBank) deposit.getLiabilityHolder();
		deposit.setValue(deposit.getValue()+cb.getCBProfits());
		profitsFromCB=cb.getCBProfits();
	}

	/**
	 * 
	 */
	private void payUnemploymentBenefits(SimulationController simulationController) {
		MacroPopulation macroPop = (MacroPopulation) simulationController.getPopulation();
		Population households= (Population) macroPop.getPopulation(StaticValues.HOUSEHOLDS_ID);
		double averageWage=0;
		double employed=0;
		for(Agent agent:households.getAgents()){
			Households worker= (Households) agent;
			if (worker.getEmployer()!=null){
				averageWage+=worker.getWage();
				employed+=1;
			}
		}
		averageWage=averageWage/employed;
		double unemploymentBenefit=averageWage*this.unemploymentBenefit;
		double doleAmount=0;
		for(Agent agent:households.getAgents()){
			Households worker= (Households) agent;
			
			if (worker.getEmployer()==null){
				LaborSupplier unemployed = (LaborSupplier) worker;
				Deposit depositGov = (Deposit) this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
				Item payableStock = unemployed.getPayableStock(StaticValues.MKT_LABOR);
				LiabilitySupplier payingSupplier = (LiabilitySupplier) depositGov.getLiabilityHolder();
				payingSupplier.transfer(depositGov, payableStock, unemploymentBenefit);
				doleAmount+=unemploymentBenefit;
			}
		}
		this.doleExpenditure=doleAmount;
	}

	/**
	 * Sets the labor demand equal to the fixed labor demand
	 */
	@Override
	protected void computeLaborDemand() {
		int currentWorkers = this.employees.size();
		Collections.shuffle(employees);
		for(int i=0;i<this.turnoverLabor*currentWorkers;i++){
			fireAgent(employees.get(i));
		}
		cleanEmployeeList();
		currentWorkers = this.employees.size();
		int nbWorkers = this.fixedLaborDemand;
		if(nbWorkers>currentWorkers){
			this.setActive(true, StaticValues.MKT_LABOR);
			this.laborDemand=nbWorkers-currentWorkers;
		}else{
			this.setActive(false, StaticValues.MKT_LABOR);
			this.laborDemand=0;
			Collections.shuffle(this.employees);
			for(int i=0;i<currentWorkers-nbWorkers;i++){
				fireAgent(employees.get(i));
			}
		}
		cleanEmployeeList();	
	}

	protected void payWages(){
		if(employees.size()>0){
			Deposit deposit = (Deposit) this.getItemStockMatrix(true, StaticValues.SM_RESERVES);
			payWages(deposit,StaticValues.MKT_LABOR);
		}
	}

	/**
	 * @return the doleExpenditure
	 */
	public double getDoleExpenditure() {
		return doleExpenditure;
	}

	/**
	 * @param doleExpenditure the doleExpenditure to set
	 */
	public void setDoleExpenditure(double doleExpenditure) {
		this.doleExpenditure = doleExpenditure;
	}


	/**
	 * @return the profitsFromCB
	 */
	public double getProfitsFromCB() {
		return profitsFromCB;
	}

	/**
	 * @param profitsFromCB the profitsFromCB to set
	 */
	public void setProfitsFromCB(double profitsFromCB) {
		this.profitsFromCB = profitsFromCB;
	}	

	
	
	/**
	 * Populates the agent characteristics using the byte array content. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][bondPrice][bondInterestRate][turnoverLabor][unemploymentBenefit][laborDemand]
	 * [fixedLaborDemand][bondMaturity][sizeTaxedPop][taxedPopulations][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public void populateAgent(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] macroBytes = new byte[buf.getInt()];
		buf.get(macroBytes);
		super.populateCharacteristics(macroBytes, pop);
		bondPrice = buf.getDouble();
		bondInterestRate = buf.getDouble();
		turnoverLabor = buf.getDouble();
		unemploymentBenefit = buf.getDouble();
		laborDemand = buf.getInt();
		fixedLaborDemand = buf.getInt();
		bondMaturity = buf.getInt();
		int lengthTaxedPopulatiobns = buf.getInt();
		taxedPopulations = new int[lengthTaxedPopulatiobns];
		for(int i = 0 ; i < lengthTaxedPopulatiobns ; i++){
			taxedPopulations[i] = buf.getInt();
		}
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
	 * protected ArrayList<MacroAgent> employees;
	protected UnemploymentRateComputer uComputer; 
	 * Generates the byte array containing all relevant informations regarding the household agent. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][bondPrice][bondInterestRate][turnoverLabor][unemploymentBenefit][laborDemand]
	 * [fixedLaborDemand][bondMaturity][sizeTaxedPop][taxedPopulations][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] charBytes = super.getAgentCharacteristicsBytes();
			out.write(ByteBuffer.allocate(4).putInt(charBytes.length).array());
			out.write(charBytes);
			ByteBuffer buf = ByteBuffer.allocate(48+4*taxedPopulations.length);
			buf.putDouble(bondPrice);
			buf.putDouble(bondInterestRate);
			buf.putDouble(turnoverLabor);
			buf.putDouble(unemploymentBenefit);
			buf.putInt(laborDemand);
			buf.putInt(fixedLaborDemand);
			buf.putInt(bondMaturity);
			buf.putInt(taxedPopulations.length);
			for(int i = 0 ; i < taxedPopulations.length ; i++){
				buf.putInt(taxedPopulations[i]);
			}
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
}
