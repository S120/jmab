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
package modellone.init;

import jmab.agents.CreditSupplier;
import jmab.agents.DepositSupplier;
import jmab.agents.GoodSupplier;
import jmab.agents.MacroAgent;
import jmab.agents.SimpleAbstractAgent;
import jmab.expectations.Expectation;
import jmab.goods.Bond;
import jmab.goods.CapitalGood;
import jmab.goods.Cash;
import jmab.goods.ConsumptionGood;
import jmab.goods.Deposit;
import jmab.goods.Loan;
import jmab.init.AbstractMacroAgentInitialiser;
import jmab.init.MacroAgentInitialiser;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import jmab.strategies.BestQualityPriceCapitalSupplierWithSwitching;
import jmab.strategies.CheapestGoodSupplierWithSwitching;
import jmab.strategies.CheapestLenderWithSwitching;
import jmab.strategies.MostPayingDepositWithSwitching;
import modellone.StaticValues;
import modellone.agents.Bank;
import modellone.agents.CapitalFirm;
import modellone.agents.CentralBank;
import modellone.agents.ConsumptionFirm;
import modellone.agents.Government;
import modellone.agents.Households;
import net.sourceforge.jabm.Population;
import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class SFCSSMacroAgentInitialiser extends AbstractMacroAgentInitialiser implements MacroAgentInitialiser{

	//Stocks
	//Households
	private double hhsDep;
	private double hhsCash;
	//Cap Firms
	private double ksDep;
	private int ksInv;
	private double ksLoans;
	private double ksLoans0;
	//Cons Firms
	private double csDep;
	private int csInv;
	private double csLoans;
	private double csLoans0;
	
	private int csKap;
	//Banks
	private double bsBonds;
	private double bsRes;
	private double bsAdv;
	private double bsCash;

	//Flows
	//Households
	private double dividendsReceived;
	//private double hhsInc;
	private double hhWage;
	//CapFirms
	private int ksEmpl;
	private double ksSales;
	private double ksProfits;
	private double kPrice;
	private double kUnitCost;
	private double ksOCF;
	//ConsFirms
	private int csEmpl;
	private double csSales;
	private double csProfits;
	private double cPrice;
	private double cUnitCost;
	private double csOCF;
	//Banks
	private double iLoans;
	private double iDep;
	private double bsProfits;
	//Government
	private int gEmpl;
	private double iBonds;
	//Central Bank
	private double iAdv;
	private double cbBonds;
	//RandomEngine
	private RandomEngine prng;
	private double uniformDistr;
	private double gr;

	/* (non-Javadoc)
	 * @see jmab.init.MacroAgentInitialiser#initialise(jmab.population.MacroPopulation)
	 */
	@Override
	public void initialise(MacroPopulation population, MacroSimulation sim) {	
		
		Population households = population.getPopulation(StaticValues.HOUSEHOLDS_ID);
		Population banks = population.getPopulation(StaticValues.BANKS_ID);
		Population kFirms = population.getPopulation(StaticValues.CAPITALFIRMS_ID);
		Population cFirms = population.getPopulation(StaticValues.CONSUMPTIONFIRMS_ID);

		Government govt = (Government)population.getPopulation(StaticValues.GOVERNMENT_ID).getAgentList().get(0);
		CentralBank cb = (CentralBank)population.getPopulation(StaticValues.CB_ID).getAgentList().get(0);

		int hhSize=households.getSize();
		int bSize=banks.getSize();
		int kSize=kFirms.getSize();
		int cSize=cFirms.getSize();

		int hhPerBank = hhSize/bSize;
		int cFirmPerBank = cSize/bSize;
		int kFirmPerBank = kSize/bSize;
		int cFirmPerkFirm = cSize/kSize;
		int hhPercFirm = hhSize/cSize;

		Uniform distr = new Uniform(-uniformDistr,uniformDistr,prng);

		//Households
		double hhDep = this.hhsDep/hhSize;
		double hhCash = this.hhsCash/hhSize;
		double hhCons = this.csSales/(hhSize*cPrice);
		for(int i = 0; i<hhSize; i++){
			Households hh = (Households) households.getAgentList().get(i);
			hh.setDividendsReceived(this.dividendsReceived/hhSize);

			//Cash Holdings
			Cash cash = new Cash(hhCash,(SimpleAbstractAgent)hh,(SimpleAbstractAgent)cb);
			hh.addItemStockMatrix(cash, true, StaticValues.SM_CASH);
			cb.addItemStockMatrix(cash, false, StaticValues.SM_CASH);

			//Deposit Holdings
			int bankId = (int) i/hhPerBank;
			MacroAgent bank = (MacroAgent) banks.getAgentList().get(bankId);
			Deposit dep = new Deposit(hhDep, hh, bank, this.iDep);
			hh.addItemStockMatrix(dep, true, StaticValues.SM_DEP);
			bank.addItemStockMatrix(dep, false, StaticValues.SM_DEP);

			//Make sure there are no employer
			hh.setEmployer(null);
			
			//Set previous seller
			int sellerId= (int) i/hhPercFirm;
			GoodSupplier previousSeller= (GoodSupplier) cFirms.getAgentList().get(sellerId);
			CheapestGoodSupplierWithSwitching buyingStrategy= (CheapestGoodSupplierWithSwitching) hh.getStrategy(StaticValues.STRATEGY_BUYING);
			buyingStrategy.setPreviousSeller(previousSeller);
			
			//Set Previous Deposit Supplier
			MostPayingDepositWithSwitching depositStrategy= (MostPayingDepositWithSwitching) hh.getStrategy(StaticValues.STRATEGY_DEPOSIT);
			DepositSupplier previousBankDeposit= (DepositSupplier) banks.getAgentList().get(bankId);
			depositStrategy.setPreviousDepositSupplier(previousBankDeposit);
			
			//Expectations and Lagged values
			hh.setWage(hhWage);
			hh.addValue(StaticValues.LAG_NETWEALTH, hh.getNetWealth());
			Expectation cPriceExp = hh.getExpectation(StaticValues.EXPECTATIONS_CONSPRICE);
			int nbObs = cPriceExp.getNumberPeriod();
			double[][] passedcPrices = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedcPrices[j][0]=this.cPrice;
				passedcPrices[j][1]=this.cPrice;
			}
			cPriceExp.setPassedValues(passedcPrices);
			hh.addValue(StaticValues.LAG_EMPLOYED,0);
			hh.addValue(StaticValues.LAG_CONSUMPTION,hhCons*(1+distr.nextDouble()));
			hh.computeExpectations();
			
		}
		
		households.getAgentList().shuffle(prng);

		//Capital Firms
		int kInv = ksInv/kSize;
		double kDep = ksDep/kSize;
		double kLoan=ksLoans0/kSize;
		int hWorkerCounter = 0;
		int bankLoanIterator=0;
		int kEmpl = ksEmpl/kSize;
		double kProfit=this.ksProfits/kSize;
		double kSales=this.ksSales/kSize;
		double kOutput=kSales/kPrice;
		double kOCF=ksOCF/kSize;
		double lMat=0;
		for(int i = 0 ; i < kSize ; i++){
			CapitalFirm k = (CapitalFirm) kFirms.getAgentList().get(i);

			//Inventories
			CapitalGood kGood = new CapitalGood(kInv*this.kPrice, kInv, k, k, this.kPrice, k.getCapitalProductivity(), 
					k.getCapitalDuration(), k.getCapitalAmortization(), k.getCapitalLaborRatio());
			kGood.setUnitCost(kUnitCost);
			k.addItemStockMatrix(kGood, true, StaticValues.SM_CAPGOOD);

			//Workers
			for(int j=0;j<kEmpl;j++){
				Households hh = (Households) households.getAgentList().get(hWorkerCounter);
				hh.setEmployer(k);
				hh.addValue(StaticValues.LAG_EMPLOYED,1);
				k.addEmployee(hh);
				hWorkerCounter++;
			}

			//Deposit Holdings
			int bankId = (int) i/kFirmPerBank;
			MacroAgent bank = (MacroAgent) banks.getAgentList().get(bankId);
			Deposit dep = new Deposit(kDep, k, bank, this.iDep);
			k.addItemStockMatrix(dep, true, StaticValues.SM_DEP);
			bank.addItemStockMatrix(dep, false, StaticValues.SM_DEP);
			//Set Previous Deposit Supplier
			MostPayingDepositWithSwitching depositStrategy= (MostPayingDepositWithSwitching) k.getStrategy(StaticValues.STRATEGY_DEPOSIT);
			DepositSupplier previousBankDeposit= (DepositSupplier) banks.getAgentList().get(bankId);
			depositStrategy.setPreviousDepositSupplier(previousBankDeposit);
			

			//Cash
			Cash cash = new Cash(0,(SimpleAbstractAgent)k,(SimpleAbstractAgent)cb);
			k.addItemStockMatrix(cash, true, StaticValues.SM_CASH);
			cb.addItemStockMatrix(cash, false, StaticValues.SM_CASH);

			
			//Loans
			bankLoanIterator=bankId;
			lMat = k.getLoanLength();
			for(int j = 0 ; j <= lMat -1 ; j++){
				Bank loanBank = (Bank) banks.getAgentList().get(bankLoanIterator);
				bankLoanIterator++;
				if(bankLoanIterator>=bSize)bankLoanIterator=0;
				Loan loan = new Loan(kLoan*(1/(Math.pow((1+gr),j)))*((lMat-j)/lMat), loanBank, k, this.iLoans, j+1, k.getLoanAmortizationType(), (int)lMat);
				loan.setInitialAmount(kLoan/Math.pow((1+gr),j));
				k.addItemStockMatrix(loan, false, StaticValues.SM_LOAN);
				loanBank.addItemStockMatrix(loan, true, StaticValues.SM_LOAN);
				//Set last period lender as previous lender in the firm's borrowing strategy
				if (j==1){
					CheapestLenderWithSwitching borrowingStrategy = (CheapestLenderWithSwitching) k.getStrategy(StaticValues.STRATEGY_BORROWING);
					CreditSupplier previousCreditor= (CreditSupplier) bank;
					borrowingStrategy.setPreviousLender(previousCreditor);
				}
			}
			
			//Expectations and Lagged Values
			k.addValue(StaticValues.LAG_PROFITPRETAX, kProfit*(1+distr.nextDouble()));
			k.addValue(StaticValues.LAG_PROFITAFTERTAX, kProfit*(1+distr.nextDouble()));
			//double lagKInv=kInv*(1+distr.nextDouble());
			double lagKInv=kInv;
			k.addValue(StaticValues.LAG_INVENTORIES, lagKInv);
			k.addValue(StaticValues.LAG_PRODUCTION, kOutput*(1+distr.nextDouble()));
			k.addValue(StaticValues.LAG_REALSALES, kOutput*(1+distr.nextDouble()));
			k.addValue(StaticValues.LAG_PRICE, kPrice);
			k.addValue(StaticValues.LAG_NOMINALSALES, kSales*(1+distr.nextDouble()));
			k.addValue(StaticValues.LAG_NETWEALTH, k.getNetWealth()*(1+distr.nextDouble()));
			k.addValue(StaticValues.LAG_NOMINALINVENTORIES, lagKInv*kUnitCost);
			k.addValue(StaticValues.LAG_OPERATINGCASHFLOW,kOCF);
			Expectation kWageExp = k.getExpectation(StaticValues.EXPECTATIONS_WAGES);
			int nbObs = kWageExp.getNumberPeriod();
			double[][] passedWage = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedWage[j][0]=this.hhWage*(1+distr.nextDouble());
				passedWage[j][1]=this.hhWage*(1+distr.nextDouble());
			}
			kWageExp.setPassedValues(passedWage);
			Expectation kSalesExp = k.getExpectation(StaticValues.EXPECTATIONS_NOMINALSALES);
			nbObs = kSalesExp .getNumberPeriod();
			double[][] passedSales = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedSales[j][0]=kSales*(1+distr.nextDouble());
				passedSales[j][1]=kSales*(1+distr.nextDouble());
			}
			kSalesExp.setPassedValues(passedSales);
			Expectation kRSalesExp = k.getExpectation(StaticValues.EXPECTATIONS_REALSALES);
			nbObs = kRSalesExp .getNumberPeriod();
			double[][] passedRSales = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedRSales[j][0]=kOutput*(1+distr.nextDouble());
				passedRSales[j][1]=kOutput*(1+distr.nextDouble());
			}
			kRSalesExp.setPassedValues(passedRSales);
			k.computeExpectations();
		}

		//Consumption Firms
		int cInv = csInv/cSize;
		double cDep = csDep/cSize;
		double cLoan = csLoans0/cSize;
		int cEmpl = csEmpl/cSize;
		double cProfit=this.csProfits/cSize;
		double cSales=this.csSales/cSize;
		double cOutput=cSales/cPrice;
		double cOCF=csOCF/cSize;
		for(int i = 0 ; i < cSize ; i++){
			ConsumptionFirm c = (ConsumptionFirm) cFirms.getAgentList().get(i);

			//Inventories
			ConsumptionGood cGood = new ConsumptionGood(cInv*this.cPrice, cInv, c, c, this.cPrice, 0);
			cGood.setUnitCost(cUnitCost);
			cGood.setAge(-1);
			c.addItemStockMatrix(cGood, true, StaticValues.SM_CONSGOOD);

			//Capital Stock
			int kFirmId = (int) i/cFirmPerkFirm;
			CapitalFirm kFirm = (CapitalFirm) kFirms.getAgentList().get(kFirmId);
			int kMat = kFirm.getCapitalDuration();
			double kAm = kFirm.getCapitalAmortization();
			double cCap = this.csKap/cSize;
			double cCapPerPeriod=cCap/kMat;
			double capitalValue=0;//Changed this, because we assume the capital stock to work fine until it becomes obsolete
			for(int j = 0 ; j < kMat ; j++){
				CapitalGood kGood = new CapitalGood(this.kPrice*cCapPerPeriod*(1-j/kAm)/Math.pow((1+gr),j), cCapPerPeriod, c, kFirm, 
						this.kPrice/Math.pow((1+gr),j),kFirm.getCapitalProductivity(),kMat,(int)kAm,kFirm.getCapitalLaborRatio());
				kGood.setAge(j);
				kGood.setUnitCost(kUnitCost);
				capitalValue+=kGood.getValue();
				c.addItemStockMatrix(kGood, true, StaticValues.SM_CAPGOOD);
			}
			
			//Previous cpaital supplier
			BestQualityPriceCapitalSupplierWithSwitching buyingStrategy= (BestQualityPriceCapitalSupplierWithSwitching) c.getStrategy(StaticValues.STRATEGY_BUYING);
			buyingStrategy.setPreviousSupplier(kFirm);
			
			
			//Workers
			for(int j=0;j<cEmpl;j++){
				Households hh = (Households) households.getAgentList().get(hWorkerCounter);
				hh.setEmployer(c);
				hh.addValue(StaticValues.LAG_EMPLOYED,1);
				c.addEmployee(hh);
				hWorkerCounter++;
			}

			//Deposit Holdings
			int bankId = (int)i/cFirmPerBank;
			MacroAgent bank = (MacroAgent) banks.getAgentList().get(bankId);
			Deposit dep = new Deposit(cDep, c, bank, this.iDep);
			c.addItemStockMatrix(dep, true, StaticValues.SM_DEP);
			bank.addItemStockMatrix(dep, false, StaticValues.SM_DEP);
			//Set Previous DepositSupplier
			MostPayingDepositWithSwitching depositStrategy= (MostPayingDepositWithSwitching) c.getStrategy(StaticValues.STRATEGY_DEPOSIT);
			DepositSupplier previousDepositSupplier= (DepositSupplier) banks.getAgentList().get(bankId);
			depositStrategy.setPreviousDepositSupplier(previousDepositSupplier);

			//Cash
			Cash cash = new Cash(0,(SimpleAbstractAgent)c,(SimpleAbstractAgent)cb);
			c.addItemStockMatrix(cash, true, StaticValues.SM_CASH);
			cb.addItemStockMatrix(cash, false, StaticValues.SM_CASH);
			
			//Loans
			bankLoanIterator=bankId;
			lMat = c.getLoanLength();
			
			for(int j = 0 ; j <= lMat-1 ; j++){
				Bank loanBank = (Bank) banks.getAgentList().get(bankLoanIterator);
				bankLoanIterator++;
				if(bankLoanIterator>=bSize)bankLoanIterator=0;
				Loan loan = new Loan(cLoan*(1/(Math.pow((1+gr),j)))*((lMat-j)/lMat), loanBank, c, this.iLoans, j+1, c.getLoanAmortizationType(), (int)lMat);
				loan.setInitialAmount(cLoan/Math.pow((1+gr),j));
				c.addItemStockMatrix(loan, false, StaticValues.SM_LOAN);
				loanBank.addItemStockMatrix(loan, true, StaticValues.SM_LOAN);
				//Set last period lender as previous lender in the firm's borrowing strategy
				if (j==1){
					CheapestLenderWithSwitching borrowingStrategy = (CheapestLenderWithSwitching) c.getStrategy(StaticValues.STRATEGY_BORROWING);
					CreditSupplier previousCreditor= (CreditSupplier) bank;
					borrowingStrategy.setPreviousLender(previousCreditor);
				}
				
			}
			
			//Expectations and Lagged Values
			c.addValue(StaticValues.LAG_PROFITPRETAX, cProfit*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_PROFITAFTERTAX, cProfit*(1+distr.nextDouble()));
			//double lagCInv=cInv*(1+distr.nextDouble());
			double lagCInv=cInv;
			c.addValue(StaticValues.LAG_INVENTORIES, lagCInv);
			c.addValue(StaticValues.LAG_PRODUCTION, cOutput*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_REALSALES, cOutput*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_PRICE, cPrice);
			c.addValue(StaticValues.LAG_CAPACITY,cCap*kFirm.getCapitalProductivity()*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_CAPITALFINANCIALVALUE,capitalValue);
			c.addValue(StaticValues.LAG_NOMINALSALES, cOutput*cPrice*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_NETWEALTH, c.getNetWealth()*(1+distr.nextDouble()));
			c.addValue(StaticValues.LAG_NOMINALINVENTORIES, lagCInv*cUnitCost);
			c.addValue(StaticValues.LAG_OPERATINGCASHFLOW, cOCF);
			Expectation cWageExp = c.getExpectation(StaticValues.EXPECTATIONS_WAGES);
			int nbObs = cWageExp.getNumberPeriod();
			double[][] passedWage = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedWage[j][0]=this.hhWage*(1+distr.nextDouble());
				passedWage[j][1]=this.hhWage*(1+distr.nextDouble());
			}
			cWageExp.setPassedValues(passedWage);
			Expectation cSalesExp = c.getExpectation(StaticValues.EXPECTATIONS_NOMINALSALES);
			nbObs = cSalesExp .getNumberPeriod();
			double[][] passedSales = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedSales[j][0]=cSales*(1+distr.nextDouble());
				passedSales[j][1]=cSales*(1+distr.nextDouble());
			}
			cSalesExp.setPassedValues(passedSales);
			Expectation cRSalesExp = c.getExpectation(StaticValues.EXPECTATIONS_REALSALES);
			nbObs = cRSalesExp .getNumberPeriod();
			double[][] passedRSales = new double[nbObs][2];
			for(int j = 0; j<nbObs; j++){
				passedRSales[j][0]=cOutput*(1+distr.nextDouble());
				passedRSales[j][1]=cOutput*(1+distr.nextDouble());
			}
			cRSalesExp.setPassedValues(passedRSales);
			
			c.computeExpectations();
		}

		//Banks
		double bCash = this.bsCash/bSize;
		double bRes = this.bsRes/bSize;
		int bondMat = govt.getBondMaturity();
		double bBond=this.bsBonds/bSize;
		int bondPrice = (int)govt.getBondPrice();
		int nbBondsPerPeriod = (int) bBond/(bondMat*bondPrice);
		double bProfit = this.bsProfits/bSize;
		double bAdv = this.bsAdv/bSize;
		for(int i = 0; i<bSize; i++){
			Bank b = (Bank) banks.getAgentList().get(i);
			//b.setRiskAversion(3);
			//b.setRiskAversionC(distr1.nextDouble());
			//b.setRiskAversionK(distr1.nextDouble());
			//Cash Holdings
			Cash cash = new Cash(bCash,(SimpleAbstractAgent)b,(SimpleAbstractAgent)cb);
			b.addItemStockMatrix(cash, true, StaticValues.SM_CASH);
			cb.addItemStockMatrix(cash, false, StaticValues.SM_CASH);

			//Reserve Holdings
			Deposit res = new Deposit(bRes,(SimpleAbstractAgent)b,(SimpleAbstractAgent)cb,0);
			b.addItemStockMatrix(res, true, StaticValues.SM_RESERVES);
			cb.addItemStockMatrix(res, false, StaticValues.SM_RESERVES);

			//Bonds Holdings
			for(int j = 1 ; j<=bondMat; j++){
				Bond bond = new Bond(nbBondsPerPeriod*bondPrice, nbBondsPerPeriod, b, govt, govt.getBondMaturity(), this.iBonds, bondPrice);
				bond.setAge(j);
				b.addItemStockMatrix(bond, true, StaticValues.SM_BONDS);
				govt.addItemStockMatrix(bond, false, StaticValues.SM_BONDS);
			}
			
			//Advances			
			int aMat = b.getAdvancesLength();
			double aValue = bAdv/aMat;
			for(int j = 1 ; j <= aMat ; j++){
				Loan loan = new Loan(aValue, cb, b, this.iAdv, j, b.getAdvancesAmortizationType(), aMat);
				loan.setInitialAmount(aValue);
				b.addItemStockMatrix(loan, false, StaticValues.SM_ADVANCES);
				cb.addItemStockMatrix(loan, true, StaticValues.SM_ADVANCES);
			}

			//Expectations and Lagged Values
			
			b.addValue(StaticValues.LAG_PROFITPRETAX, bProfit*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_PROFITAFTERTAX, bProfit*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_NONPERFORMINGLOANS, 0*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_REMAININGCREDIT, 0*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_NETWEALTH, b.getNetWealth()*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_BANKTOTLOANSUPPLY, (((csLoans+ksLoans)/bSize))*2/(lMat+1)*(1+distr.nextDouble()));
			b.addValue(StaticValues.LAG_DEPOSITINTEREST,iDep);
			b.addValue(StaticValues.LAG_LOANINTEREST,iLoans);
			double[][] bs = b.getNumericBalanceSheet();
			Expectation bDepExp = b.getExpectation(StaticValues.EXPECTATIONS_DEPOSITS);
			int nbObs = bDepExp.getNumberPeriod();
			double[][] passedbDep = new double[nbObs][2];
			double passedDebValue = bs[1][StaticValues.SM_DEP]*(1+0.05*distr.nextDouble());
			for(int j = 0; j<nbObs; j++){
				passedbDep[j][0]=passedDebValue;
				passedbDep[j][1]=passedDebValue;
			}
			bDepExp.setPassedValues(passedbDep);
			
			b.computeExpectations();
		}
		
		//Government
		//Employment
		for(int i = 0 ; i < gEmpl ; i++){
			Households hh = (Households) households.getAgentList().get(hWorkerCounter);
			hh.setEmployer(govt);
			hh.addValue(StaticValues.LAG_EMPLOYED,1);
			govt.addEmployee(hh);
			hWorkerCounter++;
		}
		
		
		//Central Bank Deposit
		Deposit govtRes = new Deposit(0,(SimpleAbstractAgent)govt,(SimpleAbstractAgent)cb,0);
		govt.addItemStockMatrix(govtRes, true, StaticValues.SM_RESERVES);
		cb.addItemStockMatrix(govtRes, false, StaticValues.SM_RESERVES);
		
		//Central Bank
		int nbBondsPerPeriod1 = (int) this.cbBonds/(bondMat*bondPrice);
		for(int j = 1 ; j<=bondMat; j++){
			Bond bond = new Bond(nbBondsPerPeriod1*bondPrice, nbBondsPerPeriod1, cb, govt, govt.getBondMaturity(), this.iBonds, bondPrice);
			bond.setAge(j);
			cb.addItemStockMatrix(bond, true, StaticValues.SM_BONDS);
			govt.addItemStockMatrix(bond, false, StaticValues.SM_BONDS);
		}
		//TODO: Add Aggregate values, we could use the macrosimulation
		govt.setAggregateValue(StaticValues.LAG_AGGUNEMPLOYMENT, 0.08*(1+distr.nextDouble()));//TODO
	}

	/**
	 * @return the hhsDep
	 */
	public double getHhsDep() {
		return hhsDep;
	}

	/**
	 * @param hhsDep the hhsDep to set
	 */
	public void setHhsDep(double hhsDep) {
		this.hhsDep = hhsDep;
	}

	/**
	 * @return the hhsCash
	 */
	public double getHhsCash() {
		return hhsCash;
	}

	/**
	 * @param hhsCash the hhsCash to set
	 */
	public void setHhsCash(double hhsCash) {
		this.hhsCash = hhsCash;
	}

	/**
	 * @return the ksDep
	 */
	public double getKsDep() {
		return ksDep;
	}

	/**
	 * @param ksDep the ksDep to set
	 */
	public void setKsDep(double ksDep) {
		this.ksDep = ksDep;
	}

	/**
	 * @return the ksInv
	 */
	public int getKsInv() {
		return ksInv;
	}

	/**
	 * @param ksInv the ksInv to set
	 */
	public void setKsInv(int ksInv) {
		this.ksInv = ksInv;
	}

	/**
	 * @return the ksLoans
	 */
	public double getKsLoans() {
		return ksLoans;
	}

	/**
	 * @param ksLoans the ksLoans to set
	 */
	public void setKsLoans(double ksLoans) {
		this.ksLoans = ksLoans;
	}

	/**
	 * @return the csDep
	 */
	public double getCsDep() {
		return csDep;
	}

	/**
	 * @param csDep the csDep to set
	 */
	public void setCsDep(double csDep) {
		this.csDep = csDep;
	}

	/**
	 * @return the csInv
	 */
	public int getCsInv() {
		return csInv;
	}

	/**
	 * @param csInv the csInv to set
	 */
	public void setCsInv(int csInv) {
		this.csInv = csInv;
	}

	/**
	 * @return the csLoans
	 */
	public double getCsLoans() {
		return csLoans;
	}

	/**
	 * @param csLoans the csLoans to set
	 */
	public void setCsLoans(double csLoans) {
		this.csLoans = csLoans;
	}

	/**
	 * @return the csKap
	 */
	public int getCsKap() {
		return csKap;
	}

	/**
	 * @param csKap the csKap to set
	 */
	public void setCsKap(int csKap) {
		this.csKap = csKap;
	}

	/**
	 * @return the bsBonds
	 */
	public double getBsBonds() {
		return bsBonds;
	}

	/**
	 * @param bsBonds the bsBonds to set
	 */
	public void setBsBonds(double bsBonds) {
		this.bsBonds = bsBonds;
	}

	/**
	 * @return the bsRes
	 */
	public double getBsRes() {
		return bsRes;
	}

	/**
	 * @param bsRes the bsRes to set
	 */
	public void setBsRes(double bsRes) {
		this.bsRes = bsRes;
	}

	/**
	 * @return the bsAdv
	 */
	public double getBsAdv() {
		return bsAdv;
	}

	/**
	 * @param bsAdv the bsAdv to set
	 */
	public void setBsAdv(double bsAdv) {
		this.bsAdv = bsAdv;
	}

	/**
	 * @return the bsCash
	 */
	public double getBsCash() {
		return bsCash;
	}

	/**
	 * @param bsCash the bsCash to set
	 */
	public void setBsCash(double bsCash) {
		this.bsCash = bsCash;
	}

	/**
	 * @return the hhWage
	 */
	public double getHhWage() {
		return hhWage;
	}

	/**
	 * @param hhWage the hhWage to set
	 */
	public void setHhWage(double hhWage) {
		this.hhWage = hhWage;
	}

	/**
	 * @return the ksEmpl
	 */
	public int getKsEmpl() {
		return ksEmpl;
	}

	/**
	 * @param ksEmpl the ksEmpl to set
	 */
	public void setKsEmpl(int ksEmpl) {
		this.ksEmpl = ksEmpl;
	}

	/**
	 * @return the ksSales
	 */
	public double getKsSales() {
		return ksSales;
	}

	/**
	 * @param ksSales the ksSales to set
	 */
	public void setKsSales(double ksSales) {
		this.ksSales = ksSales;
	}

	/**
	 * @return the ksProfits
	 */
	public double getKsProfits() {
		return ksProfits;
	}

	/**
	 * @param ksProfits the ksProfits to set
	 */
	public void setKsProfits(double ksProfits) {
		this.ksProfits = ksProfits;
	}

	/**
	 * @return the kPrice
	 */
	public double getkPrice() {
		return kPrice;
	}

	/**
	 * @param kPrice the kPrice to set
	 */
	public void setkPrice(double kPrice) {
		this.kPrice = kPrice;
	}

	/**
	 * @return the csEmpl
	 */
	public int getCsEmpl() {
		return csEmpl;
	}

	/**
	 * @param csEmpl the csEmpl to set
	 */
	public void setCsEmpl(int csEmpl) {
		this.csEmpl = csEmpl;
	}

	/**
	 * @return the csSales
	 */
	public double getCsSales() {
		return csSales;
	}

	/**
	 * @param csSales the csSales to set
	 */
	public void setCsSales(double csSales) {
		this.csSales = csSales;
	}

	/**
	 * @return the csProfits
	 */
	public double getCsProfits() {
		return csProfits;
	}

	/**
	 * @param csProfits the csProfits to set
	 */
	public void setCsProfits(double csProfits) {
		this.csProfits = csProfits;
	}

	/**
	 * @return the cPrice
	 */
	public double getcPrice() {
		return cPrice;
	}

	/**
	 * @param cPrice the cPrice to set
	 */
	public void setcPrice(double cPrice) {
		this.cPrice = cPrice;
	}

	/**
	 * @return the iLoans
	 */
	public double getiLoans() {
		return iLoans;
	}

	/**
	 * @param iLoans the iLoans to set
	 */
	public void setiLoans(double iLoans) {
		this.iLoans = iLoans;
	}

	/**
	 * @return the iDep
	 */
	public double getiDep() {
		return iDep;
	}

	/**
	 * @param iDep the iDep to set
	 */
	public void setiDep(double iDep) {
		this.iDep = iDep;
	}

	/**
	 * @return the bsProfits
	 */
	public double getBsProfits() {
		return bsProfits;
	}

	/**
	 * @param bsProfits the bsProfits to set
	 */
	public void setBsProfits(double bsProfits) {
		this.bsProfits = bsProfits;
	}

	/**
	 * @return the iBonds
	 */
	public double getiBonds() {
		return iBonds;
	}

	/**
	 * @param iBonds the iBonds to set
	 */
	public void setiBonds(double iBonds) {
		this.iBonds = iBonds;
	}

	/**
	 * @return the iAdv
	 */
	public double getiAdv() {
		return iAdv;
	}

	/**
	 * @param iAdv the iAdv to set
	 */
	public void setiAdv(double iAdv) {
		this.iAdv = iAdv;
	}

	/**
	 * @return the gEmpl
	 */
	public int getgEmpl() {
		return gEmpl;
	}

	/**
	 * @param gEmpl the gEmpl to set
	 */
	public void setgEmpl(int gEmpl) {
		this.gEmpl = gEmpl;
	}
	
	/**
	 * @return the kUnitCost
	 */
	public double getkUnitCost() {
		return kUnitCost;
	}

	/**
	 * @param kUnitCost the kUnitCost to set
	 */
	public void setkUnitCost(double kUnitCost) {
		this.kUnitCost = kUnitCost;
	}

	/**
	 * @return the cUnitCost
	 */
	public double getcUnitCost() {
		return cUnitCost;
	}

	/**
	 * @param cUnitCost the cUnitCost to set
	 */
	public void setcUnitCost(double cUnitCost) {
		this.cUnitCost = cUnitCost;
	}
	

	/**
	 * @return the dividendsReceived
	 */
	public double getDividendsReceived() {
		return dividendsReceived;
	}

	/**
	 * @param dividendsReceived the dividendsReceived to set
	 */
	public void setDividendsReceived(double dividendsReceived) {
		this.dividendsReceived = dividendsReceived;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jabm.init.AgentInitialiser#initialise(net.sourceforge.jabm.Population)
	 */
	@Override
	public void initialise(Population population) {
		MacroPopulation macroPop = (MacroPopulation) population;
		this.initialise(macroPop);
	}

	/**
	 * @return the prng
	 */
	public RandomEngine getPrng() {
		return prng;
	}

	/**
	 * @param prng the prng to set
	 */
	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}

	/**
	 * @return the uniformDistr
	 */
	public double getUniformDistr() {
		return uniformDistr;
	}

	/**
	 * @param uniformDistr the uniformDistr to set
	 */
	public void setUniformDistr(double uniformDistr) {
		this.uniformDistr = uniformDistr;
	}
	/**
	 * @return the kOCF
	 */
	public double getKsOCF() {
		return ksOCF;
	}

	/**
	 * @param ksOCF the kOCF to set
	 */
	public void setKsOCF(double ksOCF) {
		this.ksOCF = ksOCF;
	}

	/**
	 * @return the cOCF
	 */
	public double getCsOCF() {
		return csOCF;
	}

	/**
	 * @param csOCF the cOCF to set
	 */
	public void setCsOCF(double csOCF) {
		this.csOCF = csOCF;
	}

	/**
	 * @return the gr
	 */
	public double getGr() {
		return gr;
	}

	/**
	 * @param gr the gr to set
	 */
	public void setGr(double gr) {
		this.gr = gr;
	}

	/**
	 * @return the cbBonds
	 */
	public double getCbBonds() {
		return cbBonds;
	}

	/**
	 * @param cbBonds the cbBonds to set
	 */
	public void setCbBonds(double cbBonds) {
		this.cbBonds = cbBonds;
	}

	/**
	 * @return the ksLoans0
	 */
	public double getKsLoans0() {
		return ksLoans0;
	}

	/**
	 * @param ksLoans0 the ksLoans0 to set
	 */
	public void setKsLoans0(double ksLoans0) {
		this.ksLoans0 = ksLoans0;
	}

	/**
	 * @return the csLoans0
	 */
	public double getCsLoans0() {
		return csLoans0;
	}

	/**
	 * @param csLoans0 the csLoans0 to set
	 */
	public void setCsLoans0(double csLoans0) {
		this.csLoans0 = csLoans0;
	}
	
	
	

	
}
