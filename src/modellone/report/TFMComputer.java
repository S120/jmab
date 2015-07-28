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
package modellone.report;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jmab.goods.CapitalGood;
import jmab.goods.ConsumptionGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.report.AbstractMicroComputer;
import jmab.report.MicroMultipleVariablesComputer;
import jmab.simulations.MacroSimulation;
import modellone.StaticValues;
import modellone.agents.Bank;
import modellone.agents.CapitalFirm;
import modellone.agents.CentralBank;
import modellone.agents.ConsumptionFirm;
import modellone.agents.GovernmentAntiCyclical;
import modellone.agents.Households;
import modellone.strategies.FixedShareOfProfitsToPopulationAsShareOfWealthDividends;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class TFMComputer extends AbstractMicroComputer implements
		MicroMultipleVariablesComputer {

	private int consumptionFirmsId;
	private int capitalFirmsId;
	private int householdsId;
	private int banksId;
	private int governmentId;
	private int centralBankId;
	
	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	
	
	/*
	 * TFM flows: Consumption, wages (CF, KF, G), Dole, Inv, Capital Amortization, Taxes (HH,CF, KF, B), Deposits Interests (HH,CF,KF)
	 * Bonds interests (B, CB), loans interests (CF, KF), Retained Earnings (CF, KF, B), Dividends (CF,KF,B), CB Profits
	 * 
	 * (non-Javadoc)@see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {
		//TFM flows
		
		//Households flows
		double consHH = 0;
		double wHH = 0;
		double doleHH = 0;
		double tHH = 0;
		double iDHH = 0;
		double divHH = 0;
		
		//CFirms flows
		double consCF = 0;
		double wCF = 0;
		double tCF = 0;
		double invCF = 0;
		double caCF = 0;
		double iDCF = 0;
		double iLCF = 0;
		double reCF = 0;
		double divCF = 0;
		double dInventCF = 0;
		
		//KFirms flows
		double wKF = 0;
		double tKF = 0;
		double invKF = 0;
		double iDKF = 0;
		double iLKF = 0;
		double reKF = 0;
		double divKF = 0;
		double dInventKF = 0;
		
		//Banks flows
		double tB = 0;
		double iBB = 0;
		double iDB = 0;
		double iLB = 0;
		double iAB = 0;
		double reB= 0;
		double divB = 0;

		//Govt flows
		double wG = 0;
		double doleG = 0;
		double tG = 0;
		double iBG = 0;
		double fCBG = 0;
		
		//CB flows
		double iBCB = 0;
		double iACB = 0;
		double fCB = 0;

		TreeMap<Long,Double> result = new TreeMap<Long,Double>();
				
		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		Population cfpop = macroPop.getPopulation(consumptionFirmsId);
		Population kfpop = macroPop.getPopulation(capitalFirmsId);
		Population hhpop = macroPop.getPopulation(householdsId);
		Population bpop = macroPop.getPopulation(banksId);
		Population gpop = macroPop.getPopulation(governmentId);
		Population cbpop = macroPop.getPopulation(centralBankId);
		
		for (Agent i:cfpop.getAgents()){
			ConsumptionFirm firm= (ConsumptionFirm) i;
			if (!firm.isDead()){;
				wCF+=firm.getWageBill();
				iDCF+=firm.getInterestReceived();
				iLCF+=firm.getDebtInterests();
				tCF+=firm.getPassedValue(StaticValues.LAG_TAXES, 0);
				caCF+=firm.getPassedValue(StaticValues.LAG_CAPITALAMORTIZATION, 0);
				List<Item>capGoods=firm.getItemsStockMatrix(true, StaticValues.SM_CAPGOOD);
				for (Item j:capGoods){
					CapitalGood good= (CapitalGood)j;
					if(good.getAge()<0)
						invCF+=good.getValue();
				}
				double f = firm.getPassedValue(StaticValues.LAG_PROFITAFTERTAX, 0);
				double div = f*((FixedShareOfProfitsToPopulationAsShareOfWealthDividends) 
						firm.getStrategy(StaticValues.STRATEGY_DIVIDENDS)).getProfitShare();
				reCF += f-div;
				divCF += div;
				dInventCF+=firm.getPassedValue(StaticValues.LAG_NOMINALINVENTORIES, 0)-firm.getPassedValue(StaticValues.LAG_NOMINALINVENTORIES, 1);
			}
		}
		
		for (Agent i:kfpop.getAgents()){
			CapitalFirm firm= (CapitalFirm) i;
			if (!firm.isDead()){
				wKF+=firm.getWageBill();
				iDKF+=firm.getInterestReceived();
				iLKF+=firm.getDebtInterests();
				tKF+=firm.getPassedValue(StaticValues.LAG_TAXES, 0);
				invKF+=firm.getPassedValue(StaticValues.LAG_NOMINALSALES, 0);
				double f = firm.getPassedValue(StaticValues.LAG_PROFITAFTERTAX, 0);
				double div = f*((FixedShareOfProfitsToPopulationAsShareOfWealthDividends) 
						firm.getStrategy(StaticValues.STRATEGY_DIVIDENDS)).getProfitShare();
				reKF += f-div;
				divKF += div;
				
				dInventKF+=firm.getPassedValue(StaticValues.LAG_NOMINALINVENTORIES, 0)-firm.getPassedValue(StaticValues.LAG_NOMINALINVENTORIES, 1);
			}
		}
		
		double unemployed=0;
		for (Agent i:hhpop.getAgents()){
			Households hh= (Households) i;
			if (!hh.isDead()){				
				iDHH +=hh.getInterestReceived();
				tHH+=hh.getPassedValue(StaticValues.LAG_TAXES, 0);
				
				List<Item>loans=hh.getItemsStockMatrix(true, StaticValues.SM_CONSGOOD);
				for (Item j:loans){
					ConsumptionGood good= (ConsumptionGood)j;
					consHH +=good.getValue();
				}
				if(!hh.isEmployed()){
					unemployed+=1;
				}					
			}
		}
		
		for (Agent i:bpop.getAgents()){
			Bank b= (Bank) i;
			if (!b.isDead()){
				tB+=b.getPassedValue(StaticValues.LAG_TAXES, 0);
				double f = b.getPassedValue(StaticValues.LAG_PROFITAFTERTAX, 0);
				double div = f*((FixedShareOfProfitsToPopulationAsShareOfWealthDividends) 
						b.getStrategy(StaticValues.STRATEGY_DIVIDENDS)).getProfitShare();
				reB += f-div;
				divB += div;
				iBB += b.getBondInterestReceived();
				iAB += b.getAdvancesInterests();
			}
		}
		
		GovernmentAntiCyclical gov = (GovernmentAntiCyclical) gpop.getAgentList().get(0);
		wG = gov.getWageBill();
		doleG = unemployed*(wCF+wKF+wG)/(hhpop.getSize()-unemployed)*gov.getUnemploymentBenefit();
		
		CentralBank cb = (CentralBank) cbpop.getAgentList().get(0);
		iBCB = cb.getBondInterestsReceived();
		fCB = cb.getCBProfits();
		
		//Residuals
		consCF = consHH;
		wHH=wCF+wKF+wG;
		doleHH=doleG;
		tG=tHH+tCF+tKF+tB;
		iDB=iDHH+iDKF+iDCF;
		iLB=iLKF+iLCF;
		iBG = iBB+iBCB;
		fCBG = fCB;
		divHH = divCF+divKF+divB;
		
		
		result.put((long) StaticValues.TFM_CONS, consHH);
		result.put((long) StaticValues.TFM_WHH, wHH);
		result.put((long) StaticValues.TFM_DOLEHH, doleHH);
		result.put((long) StaticValues.TFM_THH, tHH);
		result.put((long) StaticValues.TFM_IDHH, iDHH);
		result.put((long) StaticValues.TFM_DIVHH, divHH);
		result.put((long) StaticValues.TFM_CONSCF, consCF);
		result.put((long) StaticValues.TFM_WCF, wCF);
		result.put((long) StaticValues.TFM_TCF, tCF);
		result.put((long) StaticValues.TFM_INVCF, invCF);
		result.put((long) StaticValues.TFM_CACF, caCF);
		result.put((long) StaticValues.TFM_IDCF, iDCF);
		result.put((long) StaticValues.TFM_ILCF, iLCF);
		result.put((long) StaticValues.TFM_DIVCF, divCF);
		result.put((long) StaticValues.TFM_RECF, reCF);
		result.put((long) StaticValues.TFM_DINVENTCF, dInventCF);
		result.put((long) StaticValues.TFM_WKF, wKF);
		result.put((long) StaticValues.TFM_TKF, tKF);
		result.put((long) StaticValues.TFM_INVKF, invKF);
		result.put((long) StaticValues.TFM_IDKF, iDKF);
		result.put((long) StaticValues.TFM_ILKF, iLKF);
		result.put((long) StaticValues.TFM_DIVKF, divKF);
		result.put((long) StaticValues.TFM_REKF, reKF);
		result.put((long) StaticValues.TFM_DINVENTKF, dInventKF);
		result.put((long) StaticValues.TFM_TB, tB);
		result.put((long) StaticValues.TFM_IDB, iDB);
		result.put((long) StaticValues.TFM_IBB, iBB);
		result.put((long) StaticValues.TFM_IAB, iAB);
		result.put((long) StaticValues.TFM_ILB, iLB);
		result.put((long) StaticValues.TFM_DIVB, divB);
		result.put((long) StaticValues.TFM_REB, reB);
		result.put((long) StaticValues.TFM_WG, wG);
		result.put((long) StaticValues.TFM_DOLEG, doleG);
		result.put((long) StaticValues.TFM_TG, tG);
		result.put((long) StaticValues.TFM_IBG, iBG);
		result.put((long) StaticValues.TFM_FCBG, fCBG);
		result.put((long) StaticValues.TFM_IBCB, iBCB);
		result.put((long) StaticValues.TFM_IACB, iACB);
		result.put((long) StaticValues.TFM_FCB, fCB);
		return result;
	}


	/**
	 * @return the consumptionFirmsId
	 */
	public int getConsumptionFirmsId() {
		return consumptionFirmsId;
	}


	/**
	 * @param consumptionFirmsId the consumptionFirmsId to set
	 */
	public void setConsumptionFirmsId(int consumptionFirmsId) {
		this.consumptionFirmsId = consumptionFirmsId;
	}


	/**
	 * @return the capitalFirmsId
	 */
	public int getCapitalFirmsId() {
		return capitalFirmsId;
	}


	/**
	 * @param capitalFirmsId the capitalFirmsId to set
	 */
	public void setCapitalFirmsId(int capitalFirmsId) {
		this.capitalFirmsId = capitalFirmsId;
	}


	/**
	 * @return the householdsId
	 */
	public int getHouseholdsId() {
		return householdsId;
	}


	/**
	 * @param householdsId the householdsId to set
	 */
	public void setHouseholdsId(int householdsId) {
		this.householdsId = householdsId;
	}


	/**
	 * @return the banksId
	 */
	public int getBanksId() {
		return banksId;
	}


	/**
	 * @param banksId the banksId to set
	 */
	public void setBanksId(int banksId) {
		this.banksId = banksId;
	}


	/**
	 * @return the governmentId
	 */
	public int getGovernmentId() {
		return governmentId;
	}


	/**
	 * @param governmentId the governmentId to set
	 */
	public void setGovernmentId(int governmentId) {
		this.governmentId = governmentId;
	}


	/**
	 * @return the centralBankId
	 */
	public int getCentralBankId() {
		return centralBankId;
	}


	/**
	 * @param centralBankId the centralBankId to set
	 */
	public void setCentralBankId(int centralBankId) {
		this.centralBankId = centralBankId;
	}

	
}
