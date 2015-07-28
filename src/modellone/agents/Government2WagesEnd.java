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

import jmab.agents.BondSupplier;
import jmab.agents.LaborDemander;
import jmab.events.MacroTicEvent;
import jmab.goods.Deposit;
import modellone.StaticValues;

/**
 * @author Alessandro Caiani and Antoine Godin
 * Note that the government uses a reserve account in the central bank rather than a deposit account due to
 * the bond market.
 */
@SuppressWarnings("serial")
public class Government2WagesEnd extends Government implements LaborDemander, BondSupplier{


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
			determineBondsInterestRate();
			emitBonds();
			break;
		case StaticValues.TIC_WAGEPAYMENT:
			payWages();
			break;
		case StaticValues.TIC_UPDATEEXPECTATIONS:
			this.updateAggregateVariables();
			break;
		}
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
}
