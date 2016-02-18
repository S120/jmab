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
package jmab.mechanisms;

import java.util.List;

import jmab.agents.LaborDemander;
import jmab.agents.LaborSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.stockmatrix.Item;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class LaborMechanism extends AbstractMechanism implements Mechanism {

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, jmab.agents.MacroAgent, int)
	 */
	public void execute( MacroAgent buyer, MacroAgent seller, int idMarket) {
		execute((LaborDemander) buyer, (LaborSupplier) seller, idMarket);
		}
	
	private void execute(LaborDemander employer, LaborSupplier worker, int idMarket){
		//1. Determine wage and total costs
		double wage=worker.getWage();
		//2. Prepare the re-allocation of funds
		//2.1 Get the payable stock
		Item payableStock = worker.getPayableStock(idMarket);
		//2.2 Get the paying stocks
		List<Item> payingStocks = employer.getPayingStocks(idMarket,payableStock);
		//2.3 Get the first occurrence of an item of the same sort than the payable stock within the paying stocks
		Item targetStock=null;
		for(Item item:payingStocks){
			if(item.getSMId()==payableStock.getSMId()){
				targetStock=item;
				break;
			}
		}
		//3. Re-allocation of funds from paying stocks towards the targetStock
		this.reallocateLiquidity(wage, payingStocks, targetStock);
		//4. If enough money was raised
		if (targetStock.getValue()>wage){
			//5. employ the worker
			worker.setEmployer(employer);
			employer.addEmployee(worker);
			worker.setActive(false, idMarket);
			if (employer.getLaborDemand()==0){
				employer.setActive(false, idMarket);
			}
			//6. Do the transfer from targetStock towards payable stock
			LiabilitySupplier payingSupplier = (LiabilitySupplier) targetStock.getLiabilityHolder();
			payingSupplier.transfer(targetStock, payableStock, wage);
		}
		else{
			//If the firm does not have enough funds to hire the cheapest worker returned by the matching mechanism, set it inactive.
			employer.setActive(false, idMarket); 
		}
		
	}
	
	/**
	 * 
	 * @param employer the employer
	 * @param worker the worker hired
	 * @param idMarket the id of the labor market
	 * The matching mechanism has taken place. If the wage is lower than employee's disposable 
	 * funds the transaction can take place. First the employer is set as employer of the worker.
	 * Then we add the worker to the employer's employees list (already employed workers do not interact 
	 * in the labor market through the matching mechanism).
	 * The method afterward is similar to the good market one (see 
	 * AtOnceMEchanism). The employee decides in which kind of liquid stock he want to be paid and 
	 * checks his liquid stocks and pay the employee in the asked type of liquid asset (eventually 
	 * transferring part of his liquid assets to the required type if the amount of this latter 
	 * not sufficient to pay the wage).
	 * 
	 */
//	private void executeOld(LaborDemander employer, LaborSupplier worker, int idMarket){
//		double wage=worker.getWage();
//		Item payableStock = worker.getPayableStock(idMarket);
//		List<Item> payingStocks = employer.getPayingStocks(idMarket,payableStock);
//		double disposablefunds = 0;
//		for (int i =0; i<payingStocks.size();i++){
//			Item payingStock=payingStocks.get(i);
//			double thisAmount=payingStock.getValue();
//			disposablefunds+=thisAmount;
//		}
//		if (disposablefunds>wage){
//			worker.setEmployer(employer);
//			employer.addEmployee(worker);
//			worker.setActive(false, idMarket);
//			if (employer.getLaborDemand()==0){
//				employer.setActive(false, idMarket);
//			}
//			double amountRaised=0;
//			for(int i =0; i<payingStocks.size()&&amountRaised<wage;i++){
//				Item payingStock=payingStocks.get(i);
//				double thisAmount=payingStock.getValue();
//				amountRaised+=thisAmount;
//				double remainingValue=Math.max(0, amountRaised-wage);
//				amountRaised-=remainingValue;
//				payingStock.setValue(remainingValue);
//				if(payingStock.getClass()!=payableStock.getClass()){
//					MacroAgent counterPart = payingStock.getLiabilityHolder();
//					List<Item> otherPayingStocks = employer.getAssets(payableStock.getSMId());
//					Item otherPayingStock = otherPayingStocks.get(0);
//					MacroAgent otherCounterPart = otherPayingStock.getLiabilityHolder();
//					Item counterPartStock = otherCounterPart.getItemStockMatrix(true, payingStock.getSMId(), counterPart);
//					if(counterPartStock==null){
//						counterPartStock=counterPart.getItemStockMatrix(true, otherPayingStock.getSMId(), otherCounterPart);
//						counterPartStock.setValue(counterPartStock.getValue()-thisAmount+remainingValue);
//					}else{
//						counterPartStock.setValue(counterPartStock.getValue()+thisAmount-remainingValue);
//					}
//				}
//			}
//			payableStock.setValue(payableStock.getValue()+amountRaised);
//		}
//		else{
//			employer.setActive(false, idMarket); //if the firm does not have enough funds to hire the cheapest worker returned by the matching mechanism, set it inactive.
//		}
//		
//	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void execute(MacroAgent buyer, List<MacroAgent> seller, int idMarket) {
		// TODO Auto-generated method stub
		
	}

}
