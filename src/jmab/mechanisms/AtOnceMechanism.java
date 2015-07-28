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

import jmab.agents.GoodDemander;
import jmab.agents.GoodSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.goods.CapitalGood;
import jmab.goods.ConsumptionGood;
import jmab.goods.Item;
import jmab.simulations.MarketSimulation;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */

//according to this mechanism GoodDemanders buy all their nominal demand at once
public class AtOnceMechanism extends AbstractGoodMechanism implements
		Mechanism {

	
	
	/**
	 * @param scheduler
	 * @param market
	 */
	
	public AtOnceMechanism (){}
	
	public AtOnceMechanism(MarketSimulation market) {
		super(market);
	}

	
	private void execute(GoodDemander goodDemander, GoodSupplier goodSupplier, int idGood) {
		//1. Determine quantity, price and total costs
		double demand=goodDemander.getDemand(idGood);
		Item output = goodSupplier.getItemStockMatrix(true, idGoodSM);
		double price=goodSupplier.getPrice(goodDemander, demand);
		double quantity=Math.min(demand,output.getQuantity());
		
		double totalAmount=quantity*price;
		//2. Prepare the re-allocation of funds
		//2.1 Get the payable stock
		Item payableStock = goodSupplier.getPayableStock(idGood);
		//2.2 Get the paying stocks
		List<Item> payingStocks = goodDemander.getPayingStocks(idGood,payableStock);
		//2.3 Get the first occurrence of an item of the same sort than the payable stock within the paying stocks
		Item targetStock=null;
		for(Item item:payingStocks){
			if(item.getSMId()==payableStock.getSMId()){
				targetStock=item;
				break;
			}
		}
		//3. Re-allocation of funds from paying stocks towards the targetStock
		this.reallocateLiquidity(totalAmount, payingStocks, targetStock);
//		if(idGoodSM==StaticValues.SM_CONSGOOD){
//			System.out.print("In the at once mechanism before");
//			System.out.println(targetStock.getValue());
//		}
		//4. If not enough money was raised
		if(targetStock.getValue()<totalAmount){
			//Then the good demander is deactivated and quantities and total costs are updated accordingly
			goodDemander.setActive(false, idGood);
			quantity=Math.floor(targetStock.getValue()/price);
			totalAmount=quantity*price;
		}
		if(quantity>0){
			
			//5. Do the transfer from targetStock towards payable stock
			LiabilitySupplier payingSupplier = (LiabilitySupplier) targetStock.getLiabilityHolder();
			payingSupplier.transfer(targetStock, payableStock, totalAmount);
			
			//6. Do the transfer of the consumption or capital goods
			Item good;
			if(output instanceof ConsumptionGood){	
				ConsumptionGood inventories =(ConsumptionGood) output;
				good = new ConsumptionGood(totalAmount, 
						quantity,goodDemander, goodSupplier,price, inventories.getConsumptionGoodDuration());
			}else{
				CapitalGood inventories =(CapitalGood) output;
				good = new CapitalGood(totalAmount, 
						quantity,goodDemander, goodSupplier,price, inventories.getProductivity(), 
						inventories.getCapitalDuration(), inventories.getCapitalAmortization(), inventories.getCapitalLaborRatio());
			}
			output.setQuantity(output.getQuantity()-quantity);
			
			goodDemander.addItemStockMatrix(good, true, idGoodSM);
			goodDemander.setDemand(demand-quantity, idGood);
			if(goodDemander.getDemand(idGood)==0||targetStock.getValue()==0)
				goodDemander.setActive(false, idGood);
			
			//7. If there are no more goods to be sold, then the supplier is deactivated.
			if (output.getQuantity()==0){
				goodSupplier.setActive(false, idGood);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jmab.mechanisms.TransactionMechanism#executeTransaction(jmab.agents.GoodDemander, jmab.agents.GoodSupplier, int)
	 */
	/**
	 * OLD VERSION:
	 * This method executes the transaction in the real good assuming that each agent buy at once all the stock of
	 * goods agreed with the GoodSupplier (or the stock available if not enough goods left). 
	 * It implements the SF consistency of the transaction:
	 * a)First it gets the total stock of items of the type to be sold from the SM of the GoodSupplier and determine the totalAmount 
	 * to be paid.
	 * b)It asks the GoodSupplier which kind of mean of payment he desires for the payment (fixed at the market level):deposits or cash
	 * c)It creates a list of items representing the stocks at disposal of the GoodDemander in order to pay.
	 * d)till the amount raised<required: It takes the first element of payingStocks, takes its value,
	 * add it to the amountRaised, calculates the remaining value of this paying stock (max(0,amountRaised-totalAmount)
	 * and set its new value (0 if not sufficient to fill the gap with totalAmount).
	 * e)If the above payingStock is off the class requested by the GoodSupplier then skip the if and:
	 *   e.1) realize the transfer of payable stock from GoodDemander to GoodSupplier. If the amountRaised still<required it buys only what it can (goods are
	 *   no integer units). e.2) create the new capital or consumption good to be transferred e.3) remove the quantity sold from GoodSupplier's inventories
	 *   and add the item bought to GoodDemander SM.
	 * d) If instead the payingStock is not off the class requested by the GoodSupplier, 2 cases: 
	 *    d.1) it's a deposits but GoodSupplier wants cash: first we have to draw money from that deposit and then update bank's reserve of cash
	 *    d.2) it's cash but GoodSupplier wants deposit: first we have to deposit the cash and then update the bank's reserve of money. 
	 *    ! In both cases we don't have to update the cash holdings/deposits respectively of the GoodDemander since this amount is immediately transferred to the GoodSupplier to buy the good.
	 * Then, follow e.1. to e.3
	 * @param goodDemander
	 * @param goodSupplier
	 * @param idGood
	 */
	

	/* (non-Javadoc)
	 * @see jmab.mechanisms.TransactionMechanism#execute(net.sourceforge.jabm.agent.Agent, net.sourceforge.jabm.agent.Agent, int)
	 */
	@Override
	public void execute(MacroAgent GoodDemander, MacroAgent GoodSupplier, int idMarket) {
		execute((GoodDemander) GoodDemander, (GoodSupplier) GoodSupplier, idMarket);
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, java.util.List, int)
	 */
	@Override
	public void execute(MacroAgent buyer, List<MacroAgent> seller, int idMarket) {
		// TODO Auto-generated method stub
		
	}

}
