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

import jmab.agents.BondDemander;
import jmab.agents.BondSupplier;
import jmab.agents.LiabilitySupplier;
import jmab.agents.MacroAgent;
import jmab.stockmatrix.Bond;
import jmab.stockmatrix.Item;


/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public class BondMechanism extends AbstractMechanism implements Mechanism {
	
	protected int idBondSM;
	
	

	/**
	 * @return the idBondSM
	 */
	public int getIdBondSM() {
		return idBondSM;
	}

	/**
	 * @param idBondSM the idBondSM to set
	 */
	public void setIdBondSM(int idBondSM) {
		this.idBondSM = idBondSM;
	}

	/* (non-Javadoc)
	 * @see jmab.mechanisms.Mechanism#execute(jmab.agents.MacroAgent, jmab.agents.MacroAgent, int)
	 */
	@Override
	public void execute(MacroAgent buyer, MacroAgent seller, int idMarket) {
		execute((BondDemander)buyer, (BondSupplier) seller, idMarket);
	}
	
	private void execute(BondDemander buyer, BondSupplier issuer, int idMarket){
		//0. Check first if there are bonds to be sold
		Bond bondsIssued = (Bond) issuer.getItemStockMatrix(false, idBondSM,issuer); 
		if (bondsIssued!=null){
			//1. Determine quantity, price and total costs
			double price=bondsIssued.getPrice();
			double interestRate=bondsIssued.getInterestRate();
			int maturity=bondsIssued.getMaturity();
			int bondsDemanded=buyer.getBondsDemand(price, issuer);
			int quantity=Math.min(bondsDemanded, (int) bondsIssued.getQuantity());
			if (bondsDemanded==quantity){
				buyer.setActive(false, idMarket);
			}
			double totalAmount=quantity*price;
			//2. Prepare the re-allocation of funds
			//2.1 Get the payable stock
			Item payableStock = issuer.getPayableStock(idBondSM);
			//2.2 Get the paying stocks
			List<Item> payingStocks = buyer.getPayingStocks(idBondSM,payableStock);
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
			//4. If not enough money was raised
			if (targetStock.getValue()<totalAmount){
				//Then the good demander is deactivated and quantities and total costs are updated accordingly
				quantity= (int) Math.floor(targetStock.getValue()/price);
				totalAmount=quantity*price;
				buyer.setActive(false, idMarket);
			}
			if(quantity>0){
				//5. Do the transfer from targetStock towards payable stock
				LiabilitySupplier payingSupplier = (LiabilitySupplier) targetStock.getLiabilityHolder();
				payingSupplier.transfer(targetStock, payableStock, totalAmount);
				//6. Do the transfer of the bonds
				Bond bondsPurchased = new Bond(price*quantity, (double)quantity, buyer, issuer, maturity, interestRate, price);
				bondsIssued.setQuantity(bondsIssued.getQuantity()-quantity);
				buyer.addItemStockMatrix(bondsPurchased, true, idBondSM);
				issuer.addItemStockMatrix(bondsPurchased, false, idBondSM);
				//7. If there are no more bonds to be sold, then the supplier is deactivated.
				if (bondsIssued.getQuantity()==0){
					issuer.removeItemStockMatrix(bondsIssued, false, idBondSM);
					issuer.setActive(false, idMarket);
				}
			}
		}
		
	}
	
//	private void executeOLD(BondDemander buyer, BondSupplier issuer, int idMarket){ //TODO REMOVE THIS METHOD
//		Bond bondsIssued = (Bond) issuer.getItemStockMatrix(false, idBondSM, null); 
//		if (bondsIssued!=null){
//			double price=bondsIssued.getPrice();
//			double interestRate=bondsIssued.getInterestRate();
//			int maturity=bondsIssued.getMaturity();
//			int bondsDemanded=buyer.getBondsDemand(price, issuer);
//			int quantity=Math.min(bondsDemanded, (int) bondsIssued.getQuantity());
//			if (bondsDemanded==quantity){
//				buyer.setActive(false, idMarket);
//			}
//			double totalAmount=quantity*price;
//			Item payableStock = issuer.getPayableStock(idBondSM);
//			List<Item> payingStocks = buyer.getPayingStocks(idBondSM,payableStock);
//			double disposableFunds=0;
//			for (int i=0; i<payingStocks.size();i++){
//				Item payingStock=payingStocks.get(i);
//				double thisAmount=payingStock.getValue();
//				disposableFunds+=disposableFunds+thisAmount;
//			}
//			if (disposableFunds<totalAmount){
//				quantity= (int) Math.floor(disposableFunds/price);
//				totalAmount=quantity*price;
//				buyer.setActive(false, idMarket);
//			}
//			double amountRaised=0;
//			for(int i =0; i<payingStocks.size()&&amountRaised<totalAmount;i++){
//				Item payingStock=payingStocks.get(i);
//				double thisAmount=payingStock.getValue();
//				amountRaised+=thisAmount;
//				double remainingValue=Math.max(0, amountRaised-totalAmount);
//				amountRaised-=remainingValue;
//				payingStock.setValue(remainingValue);
//				if(payingStock.getClass()!=payableStock.getClass()){
//					MacroAgent counterPart = payingStock.getLiabilityHolder();
//					List<Item> otherPayingStocks = buyer.getAssets(payableStock.getSMId());
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
//			
//			//what if constrained? if constrained vs if not constrained
//			bondsIssued.setQuantity(bondsIssued.getQuantity()-quantity);
//			if (bondsIssued.getQuantity()==0){
//				issuer.removeItemStockMatrix(bondsIssued, false, idBondSM);
//				issuer.setActive(false, idMarket);
//			}
//			Bond bondsPurchased = new Bond(price*quantity, ((double)quantity), buyer, issuer, maturity, interestRate, price);
//			buyer.addItemStockMatrix(bondsPurchased, true, idBondSM);
//			issuer.addItemStockMatrix(bondsPurchased, false, idBondSM);
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
