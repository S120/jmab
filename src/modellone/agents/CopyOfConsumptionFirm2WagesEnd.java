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
import jmab.agents.PriceSetterWithTargets;
import jmab.agents.ProfitsTaxPayer;
import jmab.events.MacroTicEvent;
import jmab.expectations.Expectation;
import jmab.goods.CapitalGood;
import jmab.goods.Cash;
import jmab.goods.ConsumptionGood;
import jmab.goods.Deposit;
import jmab.goods.Item;
import jmab.strategies.DividendsStrategy;
import jmab.strategies.FinanceStrategy;
import jmab.strategies.ProfitsWealthTaxStrategy;
import jmab.strategies.SelectSellerStrategy;
import jmab.strategies.TargetExpectedInventoriesOutputStrategy;
import modellone.StaticValues;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
@SuppressWarnings("serial")
public class CopyOfConsumptionFirm2WagesEnd extends ConsumptionFirm implements GoodSupplier, GoodDemander, CreditDemander, 
LaborDemander, DepositDemander, PriceSetterWithTargets, ProfitsTaxPayer, FinanceAgent, InvestmentAgent {



	private double minWageDiscount;
	private double shareOfExpIncomeAsDeposit;



	@Override
	protected void onTicArrived(MacroTicEvent event) {
		switch(event.getTic()){
		case StaticValues.TIC_COMPUTEEXPECTATIONS:
			bailoutCost=0;
			this.defaulted=false;
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
				Collections.shuffle(this.employees);
				for(int i=0;i<employees.size();i++){
					LaborSupplier employee = (LaborSupplier) employees.get(i);
					Item payableStock = employee.getPayableStock(StaticValues.MKT_LABOR);
					LiabilitySupplier payingSupplier = (LiabilitySupplier) deposit.getLiabilityHolder();
					payingSupplier.transfer(deposit, payableStock, wageBill*neededDiscount/employees.size());
				}
				deposit.setValue(0);
				System.out.println("Deafult "+ this.getAgentId() + " due to wages");
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
		Expectation expectation2=this.getExpectation(StaticValues.EXPECTATIONS_REALSALES);
		double expRealSales=expectation2.getExpectation();
		ConsumptionGood inventories = (ConsumptionGood)this.getItemStockMatrix(true, StaticValues.SM_CONSGOOD); 
		double uc=inventories.getUnitCost();
		int inv = (int)inventories.getQuantity();
		double expRevenues=expectation1.getExpectation();
		int nbWorkers = this.getRequiredWorkers();
		Expectation expectation = this.getExpectation(StaticValues.EXPECTATIONS_WAGES);
		double expWages = expectation.getExpectation();
		DividendsStrategy strategyDiv=(DividendsStrategy)this.getStrategy(StaticValues.STRATEGY_DIVIDENDS);
		double profitShare=strategyDiv.getProfitShare();
		TargetExpectedInventoriesOutputStrategy strategyProd= (TargetExpectedInventoriesOutputStrategy) this.getStrategy(StaticValues.STRATEGY_PRODUCTION);
		ProfitsWealthTaxStrategy taxStrategy= (ProfitsWealthTaxStrategy) this.getStrategy(StaticValues.STRATEGY_TAXES);
		double profitTaxRate=taxStrategy.getProfitTaxRate();
		double shareInvenstories=strategyProd.getInventoryShare();
		List<Item> capStocks = this.getItemsStockMatrix(true, StaticValues.SM_CAPGOOD);
		double capitalAmortization = 0;
		for(Item c:capStocks){
			CapitalGood cap = (CapitalGood)c;
			if(cap.getAge()>=0 && cap.getAge()<cap.getCapitalAmortization())
				capitalAmortization+=cap.getQuantity()*cap.getPrice()/cap.getCapitalAmortization();
		}
		
		double expectedProfits=expRevenues-(nbWorkers*expWages)+this.interestReceived-this.debtInterests+(shareInvenstories*expRealSales-inv)*uc-capitalAmortization;
		double expectedTaxes=expectedProfits*profitTaxRate;
		double expectedDividends=expectedProfits*(1-profitTaxRate)*profitShare;
		double Inv=this.desiredRealCapitalDemand*((CapitalFirm)this.selectedCapitalGoodSuppliers.get(0)).getPrice();
		double totalFinancialRequirement=(nbWorkers*expWages)+
				Inv+
				this.debtBurden - this.interestReceived + expectedTaxes + expectedDividends-expRevenues+this.shareOfExpIncomeAsDeposit*(nbWorkers*expWages);
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
	
	
}
