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
package jmab.simulations;

import jmab.events.MacroSimEvent;
import jmab.events.MacroTicEvent;
import jmab.events.MacroVariableTicEvent;
import jmab.events.MarketInteractionsFinishedEvent;
import jmab.events.MarketInteractionsStartingEvent;
import jmab.events.MicroMultiVariablesTicEvent;
import jmab.events.MicroSimEvent;
import jmab.events.SerializationTicEvent;
import jmab.population.MacroPopulation;
import modellone.StaticValues;

/**
 * @author Alessandro Caiani and Antoine Godin
 * This is the class in charge of managing the AB-SFC model simulation. The OrderedEventsSimulation class define the ordered of {@link MacroTicEvent} events to 
 * be sent during a round of the simulation, through the fireListEvents() method.
 *
 */
@SuppressWarnings("serial")
public class OrderedEventsSimulation extends AbstractMacroSimulation implements
		MacroSimulation {
	
//	private CSVWriter banksWriter;
//	private CSVWriter hhsWriter;
//	private CSVWriter cfirmsWriter;
//	private CSVWriter kfirmsWriter;
//	private CSVWriter cbWriter;
//	private CSVWriter govtWriter;
	
	/**
	 * 
	 */
	public OrderedEventsSimulation() {
		
//		String folderName = "data/";
//		try {
//			banksWriter = new CSVWriter(new FileOutputStream(folderName.concat("banks.csv")),',');
//			hhsWriter = new CSVWriter(new FileOutputStream(folderName.concat("hhs.csv")),',');
//			cfirmsWriter = new CSVWriter(new FileOutputStream(folderName.concat("cfirms.csv")),',');
//			kfirmsWriter = new CSVWriter(new FileOutputStream(folderName.concat("kfrims.csv")),',');
//			cbWriter = new CSVWriter(new FileOutputStream(folderName.concat("cb.csv")),',');
//			govtWriter = new CSVWriter(new FileOutputStream(folderName.concat("govt.csv")),',');
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	
	
	/** 
	 * This method fires sequentially the events contained in the arrayList events which are generically defined as
	 * {@link MacroTicEvent}. A specific MacroTicEvent is identified by an index and a name which are defined in the {@link StaticValues} interface. 
	 * Then, the class MacroTicEvent is further specified by two classes: {@link MacroVariableTicEvent}, and {@link MicroMultiVariablesTicEvent}  
	 * which call the computation of an aggregate or micro variable to be reported.
	 * First fires the MarktInteractionStartingEvent
	 * Then fires the list of ticEvents that must take place in each round of the simulation.
	 * Finally fire the MarketInteractionsFinishedEvent 
	 */
	@Override
	protected void fireListEvents() {
		System.out.println(this.round);
		super.fireEvent(new MarketInteractionsStartingEvent(this)); //TODO: Why necessary?
		for(MacroTicEvent event:events){
			
//			if ( event.getTic()==1001){
//			
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.HOUSEHOLDS_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					hhsWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					hhsWriter.newData(bs[1][i]);
//				}
//				hhsWriter.newData(((MacroAgent)agent).getAgentId());
//				hhsWriter.newData(event.getTic());
//				hhsWriter.newData(this.getRound());
//				if(((HouseholdsWithDole)agent).getEmployer()!=null)
//					hhsWriter.newData(((HouseholdsWithDole)agent).getEmployer().getAgentId());
//				else
//					hhsWriter.newData("-1");
//				hhsWriter.endRecord();
//			}
//			
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.BANKS_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					banksWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					banksWriter.newData(bs[1][i]);
//				}
//				banksWriter.newData(((MacroAgent)agent).getAgentId());
//				banksWriter.newData(event.getTic());
//				banksWriter.newData(this.getRound());
//				banksWriter.endRecord();
//			}
//			//*/
//			
//			//if (event.getTic()==1||event.getTic()==16||event.getTic()==19||event.getTic()==21){
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.CONSUMPTIONFIRMS_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					cfirmsWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					cfirmsWriter.newData(bs[1][i]);
//				}
//				cfirmsWriter.newData(((MacroAgent)agent).getAgentId());
//				cfirmsWriter.newData(event.getTic());
//				cfirmsWriter.newData(this.getRound());
//				cfirmsWriter.endRecord();
//			}
//			//}
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.CAPITALFIRMS_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					kfirmsWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					kfirmsWriter.newData(bs[1][i]);
//				}
//				kfirmsWriter.newData(((MacroAgent)agent).getAgentId());
//				kfirmsWriter.newData(event.getTic());
//				kfirmsWriter.newData(this.getRound());
//				kfirmsWriter.endRecord();
//			}
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.GOVERNMENT_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					govtWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					govtWriter.newData(bs[1][i]);
//				}
//				govtWriter.newData(((MacroAgent)agent).getAgentId());
//				govtWriter.newData(event.getTic());
//				govtWriter.newData(this.getRound());
//				govtWriter.endRecord();
//			}
//			for(Agent agent:((MacroPopulation)this.population).getPopulation(StaticValues.CB_ID).getAgents()){
//				double[][] bs = ((MacroAgent)agent).getNumericBalanceSheet();
//				for(int i=0;i<bs[0].length;i++){
//					cbWriter.newData(bs[0][i]);
//				}
//				for(int i=0;i<bs[0].length;i++){
//					cbWriter.newData(bs[1][i]);
//				}
//				cbWriter.newData(((MacroAgent)agent).getAgentId());
//				cbWriter.newData(event.getTic());
//				cbWriter.newData(this.getRound());
//				cbWriter.endRecord();
//			}
//			}
			//*/
			if(event instanceof MacroVariableTicEvent){
				MacroVariableTicEvent varEvent = (MacroVariableTicEvent) event;
				MacroSimEvent eventToSend = new MacroSimEvent(varEvent.getVariableName(), this.round, varEvent.getValue(this),varEvent.getVariableId());
				super.fireEvent(eventToSend);
			}else if(event instanceof MicroMultiVariablesTicEvent){
				MicroMultiVariablesTicEvent varEvent = (MicroMultiVariablesTicEvent) event;
				MicroSimEvent eventToSend = new MicroSimEvent(this.round, varEvent.getValues(this),varEvent.getVariableId());
				super.fireEvent(eventToSend);
			}else if(event instanceof SerializationTicEvent){
				SerializationTicEvent ev = (SerializationTicEvent) event;
				if(this.round==ev.getRound()){
					ev.writeBytes(this.getBytes());
				}
			}else{
				event.setSimulationController(getSimulationController());
				super.fireEvent(event);
			}
		}
		super.fireEvent(new MarketInteractionsFinishedEvent(this));
	}



	/* (non-Javadoc)
	 * @see jmab.simulations.MacroSimulation#populateFromBytes(byte[], jmab.population.MacroPopulation)
	 */
	@Override
	public void populateFromBytes(byte[] content, MacroPopulation pop) {
		this.populateSimulationCharacteristicsFromBytes(content, pop);
	}



	/* (non-Javadoc)
	 * @see jmab.simulations.MacroSimulation#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return this.getSimulationCharacteristicsBytes();
	}

}
