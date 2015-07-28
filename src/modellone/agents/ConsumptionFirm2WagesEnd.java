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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jmab.agents.CreditDemander;
import jmab.agents.DepositDemander;
import jmab.agents.FinanceAgent;
import jmab.agents.GoodDemander;
import jmab.agents.GoodSupplier;
import jmab.agents.InvestmentAgent;
import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.agents.PriceSetterWithTargets;
import jmab.agents.ProfitsTaxPayer;
import jmab.events.MacroTicEvent;
import jmab.expectations.Expectation;
import jmab.goods.Cash;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.strategies.DividendsStrategy;
import jmab.strategies.FinanceStrategy;
import jmab.strategies.SelectSellerStrategy;
import modellone.StaticValues;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class ConsumptionFirm2WagesEnd extends ConsumptionFirm implements GoodSupplier, GoodDemander, CreditDemander, 
LaborDemander, DepositDemander, PriceSetterWithTargets, ProfitsTaxPayer, FinanceAgent, InvestmentAgent {



	private double minWageDiscount;
	private double shareOfExpIncomeAsDeposit;



	@Override
	protected void onTicArrived(MacroTicEvent event) {
		switch(event.getTic()){
		case StaticValues.TIC_COMPUTEEXPECTATIONS:
			computeExpectations();
			determineOutput();
			break;
		case StaticValues.TIC_CONSUMPTIONPRICE:
			computePrice();
			break;
		case StaticValues.TIC_INVESTMENTDEMAND:
			SelectSellerStrategy buyingStrategy = (SelectSellerStrategy) this.getStrategy(StaticValues.STRATEGY_BUYING);
			computeDesiredInvestment(buyingStrategy.selectGoodSupplier(this.selectedCapitalGoodSuppliers, 0.0, true));
			break;
		case StaticValues.TIC_CREDITDEMAND:
			computeCreditDemand();
			break;
		case StaticValues.TIC_LABORDEMAND:
			computeLaborDemand();
			break;
		case StaticValues.TIC_PRODUCTION:
			produce();
			break;
		case StaticValues.TIC_WAGEPAYMENT:
			payWages();
			break;
		case StaticValues.TIC_CREDINTERESTS:
			payInterests();
			break;
		case StaticValues.TIC_DIVIDENDS:
			payDividends();
			break;
		case StaticValues.TIC_DEPOSITDEMAND:
			computeLiquidAssetsAmounts();
			break;
		case StaticValues.TIC_UPDATEEXPECTATIONS:
			updateExpectations();
		}
	}
	
	/**
	 * 
	 */
	private void payWages() {
		//If there are wages to pay
		if(employees.size()>0){
			//1. Have only one deposit paying wages, reallocate wealth
			List<Item> deposits = this.getItemsStockMatrix(true, StaticValues.SM_DEP);
			Deposit deposit = (Deposit)deposits.get(0);
			if(deposits.size()==2){
				Item deposit2 = deposits.get(1);
				LiabilitySupplier supplier = (LiabilitySupplier) deposit2.getLiabilityHolder();
				supplier.transfer(deposit2, deposit, deposit2.getValue());
			}
			//2. If cash holdings
			Cash cash = (Cash) this.getItemStockMatrix(true, StaticValues.SM_CASH);
			if(cash.getValue()>0){
				LiabilitySupplier bank = (LiabilitySupplier)deposit.getLiabilityHolder();
				Item bankCash = bank.getCounterpartItem(deposit, cash);
				bankCash.setValue(bankCash.getValue()+cash.getValue());
				deposit.setValue(deposit.getValue()+cash.getValue());
				cash.setValue(0);
			}
			double wageBill = this.getWageBill();
			double neededDiscount = 1;
			if(wageBill>deposit.getQuantity()){
				//System.out.println("discount");
				neededDiscount = deposit.getQuantity()/wageBill;
			}
			if(neededDiscount<this.minWageDiscount){
				this.bankruptcy();
			}else{
				//3. Pay wages
				Collections.shuffle(this.employees);
				for(int i=0;i<employees.size();i++){
					LaborSupplier employee = (LaborSupplier) employees.get(i);
					double wage = employee.getWage();
					if(wage<deposit.getValue()){
						Item payableStock = employee.getPayableStock(StaticValues.MKT_LABOR);
						LiabilitySupplier payingSupplier = (LiabilitySupplier) deposit.getLiabilityHolder();
						payingSupplier.transfer(deposit, payableStock, wage*neededDiscount);
					}
				}
			}
		}
		
	}

	/**
	 * Compute the labor demand by the firm. First it determine the total amount of workers required to produce
	 * the desiredOutput through the method getRequiredWorkers: if smaller than the number of current employees the firm 
	 * fires the last it had hired, otherwise it hires new workers. 
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
		
		int nbWorkers= this.getRequiredWorkers();	
		if(nbWorkers>currentWorkers){
			this.laborDemand=nbWorkers-currentWorkers;
		}else{
			this.laborDemand=0;
			Collections.shuffle(this.employees);
			this.setActive(false, StaticValues.MKT_LABOR);
			for(int i=0;i<currentWorkers-nbWorkers;i++){
				fireAgent(employees.get(i));
			}
		}
		if (laborDemand>0){
			this.setActive(true, StaticValues.MKT_LABOR);
		}
		cleanEmployeeList();
	}

	/**
	 *
	 */
	@Override
	protected void computeCreditDemand() {
		this.computeDebtPayments();
		Expectation expectation1=this.getExpectation(StaticValues.EXPECTATIONS_NOMINALSALES);
		double expRevenues=expectation1.getExpectation();
		int nbWorkers = this.getRequiredWorkers();
		Expectation expectation = this.getExpectation(StaticValues.EXPECTATIONS_WAGES);
		double expWages = expectation.getExpectation();
		DividendsStrategy strategyDiv=(DividendsStrategy)this.getStrategy(StaticValues.STRATEGY_DIVIDENDS);
		double profitShare=strategyDiv.getProfitShare();
		double totalFinancialRequirement=(nbWorkers*expWages)+
				this.desiredRealCapitalDemand*((CapitalFirm)this.selectedCapitalGoodSuppliers.get(0)).getPrice()+
				this.debtBurden - this.interestReceived + profitShare * this.getPassedValue(StaticValues.LAG_PROFITAFTERTAX, 1)+(this.shareOfExpIncomeAsDeposit-1)*expRevenues;
		FinanceStrategy strategy =(FinanceStrategy)this.getStrategy(StaticValues.STRATEGY_FINANCE);
		this.creditDemanded=strategy.computeCreditDemand(totalFinancialRequirement);
		if(creditDemanded>0){
			this.setActive(true, StaticValues.MKT_CREDIT);
		}
	}

	/**
	 * @return the minWageDiscount
	 */
	public double getMinWageDiscount() {
		return minWageDiscount;
	}

	/**
	 * @param minWageDiscount the minWageDiscount to set
	 */
	public void setMinWageDiscount(double minWageDiscount) {
		this.minWageDiscount = minWageDiscount;
	}

	/**
	 * @return the shareOfExpIncomeAsDeposit
	 */
	public double getShareOfExpIncomeAsDeposit() {
		return shareOfExpIncomeAsDeposit;
	}

	/**
	 * @param shareOfExpIncomeAsDeposit the shareOfExpIncomeAsDeposit to set
	 */
	public void setShareOfExpIncomeAsDeposit(double shareOfExpIncomeAsDeposit) {
		this.shareOfExpIncomeAsDeposit = shareOfExpIncomeAsDeposit;
	}
	
	/**
	 * Populates the agent characteristics using the byte array content. The structure is as follows:
	 *  [sizeMacroAgentStructure][MacroAgentStructure][targetStock][creditdDemanded][desiredCapacityGrowth][desiredRealCapitalDemand]
	 * [debtBurden][debtInterests][interestReceived][turnoverLabor][sizeDebtPayments][debtPayments]
	 * [sizeSuppliers][suppliersPopId and suppliersId][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public void populateAgent(byte[] content, MacroPopulation pop) {
		ByteBuffer buf = ByteBuffer.wrap(content);
		byte[] macroBytes = new byte[buf.getInt()];
		buf.get(macroBytes);
		super.populateCharacteristics(macroBytes, pop);
		targetStock = buf.getDouble();
		creditDemanded = buf.getDouble();
		desiredCapacityGrowth = buf.getDouble();
		desiredRealCapitalDemand = buf.getDouble();
		debtBurden = buf.getDouble();
		debtInterests = buf.getDouble();
		interestReceived = buf.getDouble();
		turnoverLabor = buf.getDouble();
		minWageDiscount = buf.getDouble();
		shareOfExpIncomeAsDeposit = buf.getDouble();
		int lengthDebtPayments = buf.getInt();
		debtPayments = new double[lengthDebtPayments][3];
		for(int i = 0 ; i < debtPayments.length ; i++){
			debtPayments[i][0] = buf.getDouble();
			debtPayments[i][1] = buf.getDouble();
			debtPayments[i][2] = buf.getDouble();
		}
		int nbSuppliers = buf.getInt();
		this.selectedCapitalGoodSuppliers = new ArrayList<Agent>();
		for(int i = 0 ; i < nbSuppliers ; i++){
			Collection<Agent> aHolders = pop.getPopulation(buf.getInt()).getAgents();
			long selSupplierId = buf.getLong(); 
			for(Agent a:aHolders){
				MacroAgent pot = (MacroAgent) a;
				if(pot.getAgentId()==selSupplierId){
					this.selectedCapitalGoodSuppliers.add(pot);
				}
			}
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
	 * Generates the byte array containing all relevant informations regarding the consumption firm agent. The structure is as follows:
	 * [sizeMacroAgentStructure][MacroAgentStructure][targetStock][creditdDemanded][desiredCapacityGrowth][desiredRealCapitalDemand]
	 * [debtBurden][debtInterests][interestReceived][turnoverLabor][minWageDiscount][shareOfExpIncomeAsDeposit][sizeDebtPayments][debtPayments]
	 * [sizeSuppliers][suppliersPopId and suppliersId][matrixSize][stockMatrixStructure][expSize][ExpectationStructure]
	 * [passedValSize][PassedValStructure][stratsSize][StrategiesStructure]
	 */
	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] charBytes = super.getAgentCharacteristicsBytes();
			out.write(ByteBuffer.allocate(4).putInt(charBytes.length).array());
			out.write(charBytes);
			ByteBuffer buf = ByteBuffer.allocate((9+3*debtPayments.length)*8+16);
			buf.putDouble(targetStock);
			buf.putDouble(creditDemanded);
			buf.putDouble(desiredCapacityGrowth);
			buf.putDouble(desiredRealCapitalDemand);
			buf.putDouble(debtBurden);
			buf.putDouble(debtInterests);
			buf.putDouble(interestReceived);
			buf.putDouble(turnoverLabor);
			buf.putDouble(minWageDiscount);
			buf.putDouble(shareOfExpIncomeAsDeposit);			
			buf.putInt(debtPayments.length);
			for(int i = 0 ; i < debtPayments.length ; i++){
				buf.putDouble(debtPayments[i][0]);
				buf.putDouble(debtPayments[i][1]);
				buf.putDouble(debtPayments[i][2]);
			}
			buf.putInt(this.selectedCapitalGoodSuppliers.size());
			for(Agent supplier:selectedCapitalGoodSuppliers){
				buf.putInt(((MacroAgent)supplier).getPopulationId());
				buf.putLong(((MacroAgent)supplier).getAgentId());
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
