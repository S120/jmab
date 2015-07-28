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
package jmab.agents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jmab.events.DeadAgentEvent;
import jmab.events.MacroTicEvent;
import jmab.expectations.Expectation;
import jmab.expectations.PassedValues;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.population.MarketPopulation;
import jmab.simulations.MacroSimulation;
import jmab.strategies.MacroStrategy;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.AbstractAgent;
import net.sourceforge.jabm.event.AgentArrivalEvent;
import net.sourceforge.jabm.event.EventListener;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.strategy.Strategy;

/**
 * @author Alessandro Caiani and Antoine Godin
 * 
 * Each agent is characterized by a stockMatrix which contains a list of list of the items hold by the 
 * agent (if not holding of an asset the corresponding list is empty), gathered per type, and subdivided between 
 * assets and liabilities. Hence the SM provides a snapshot of the agent's state in each particular moment of the 
 * simulation.
 * Each agent contains the following properties
 *  - stockMatrix a matrix containing all the items of the agent divided into assets and liabilities
 *  - assetNames the names of the assets in the SM
 *  - activeTicEvents a list containing the number of the tic events to which the agent subscribes
 *  - activeAgent true if agent is active, false otherwise.
 *  - dead false if agent still alive
 *  - populationId the Id of the population to which the agent belongs
 *  - agentId the id of the agent
 * 
 * TODO change names to getAsset, getAssetStock (actually it gets the whole asset side of the SM), getItemStockMatrix because misleading!!
 */
@SuppressWarnings("serial")
public abstract class SimpleAbstractAgent extends AbstractAgent implements
MacroAgent, EventListener {

	protected List<List<Item>>[] stockMatrix;
	protected List<String> stocksNames;
	protected List<Integer> activeTicEvents;
	protected Map<Integer,Expectation> expectations;
	protected Map<Integer,PassedValues> passedValues;
	protected boolean[] activeAgent;
	protected boolean dead=false;
	protected int populationId;
	protected long agentId;
	protected int numberMarkets;
	protected int numberStocks;

	protected boolean defaulted=false;



	public SimpleAbstractAgent(){
		this.agentId= AgentIdGenerator.nextId();
	};

	/**
	 * @return the populationId
	 */
	public int getPopulationId() {
		return populationId;
	}

	/**
	 * @return the activeAgent
	 */
	public boolean[] getActiveAgent() {
		return activeAgent;
	}
	/**
	 * @param activeAgent the activeAgent to set
	 */
	public void setActiveAgent(boolean[] activeAgent) {
		this.activeAgent = activeAgent;
	}
	/**
	 * @param populationId the populationId to set
	 */
	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}

	public boolean isDead(){
		return dead;
	}

	public boolean isDefaulted(){
		return defaulted;
	}
	/**
	 * @return the agentId
	 */
	public long getAgentId() {
		return agentId;
	}
	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}



	/**
	 * @param populationId the populationId to set
	 */
	public void setNumberMarkets(int numberMarkets) {
		this.numberMarkets = numberMarkets;
		this.activeAgent=new boolean [numberMarkets];
	}

	/**
	 * @return the numberStocks
	 */
	public int getNumberStocks() {
		return numberStocks;
	}
	/**
	 * @param numberStocks the numberStocks to set
	 */
	public void setNumberStocks(int numberStocks) {
		this.numberStocks = numberStocks;
		this.stockMatrix= new ArrayList[2];
		this.stockMatrix[0]=new ArrayList<List<Item>> ();
		this.stockMatrix[1]=new ArrayList<List<Item>> ();
		for (int i=0; i<this.numberStocks;i++){
			this.stockMatrix[0].add(new ArrayList<Item>());
			this.stockMatrix[1].add(new ArrayList<Item>());
		}
	}
	/**
	 * @param populationId the populationId to set
	 */
	public int getNumberMarkets() {
		return numberMarkets;
	}

	/**
	 * @return the activeTicEvents
	 */
	public List<Integer> getActiveTicEvents() {
		return activeTicEvents;
	}
	/**
	 * @param activeTicEvents the activeTicEvents to set
	 */
	public void setActiveTicEvents(List<Integer> activeTicEvents) {
		this.activeTicEvents = activeTicEvents;
	}

	@Override
	public void initialise() {
	}


	protected void dies(){
		this.dead=true;
		super.fireEvent(new DeadAgentEvent(this));
	}

	@Override
	public abstract void onAgentArrival(AgentArrivalEvent event);

	public boolean isActive (int idMarket){
		if(this.isDead())
			return false;
		else
			return activeAgent[idMarket];
	}

	public void setActive (boolean active, int idMarket ){
		this.activeAgent[idMarket]= active;
	}

	public int getMarketsAttended(){
		return activeAgent.length;
	}

	/**
	 * each agent defined as a specific listener to events of the classes AgentArrivalEvent, SimulationFinishedEvent, and 
	 * AgentTicEvent.
	 */
	@Override
	public void subscribeToEvents() {
		scheduler.addListener(AgentArrivalEvent.class, this);
		scheduler.addListener(MacroTicEvent.class, this);
		scheduler.addListener(SimulationFinishedEvent.class, this);
	}

	/**
	 * If the SimEvent event is of the class AgentTicEvent: if the agent is activated when this event occurred (i.e.
	 * its ActiveTicEvent contains the number identifying the specific tic event) then execute the method 
	 * onTicArrived using the integer parameter representing the specific tic event (else do nothing).
	 * If not of the class AgentTicEvent then use the eventOccurred method of the superior class.
	 * 
	 */
	@Override
	public void eventOccurred(SimEvent event) {
		if(event instanceof MacroTicEvent){
			MacroTicEvent tic = (MacroTicEvent)event; 
			if(!isDead()&&this.activeTicEvents.contains(tic.getTic())){
				onTicArrived(tic);
			}
		}else
			super.eventOccurred(event);

	}

	/**
	 * @param tic
	 */
	protected abstract void onTicArrived(MacroTicEvent event);

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * @return the assetNames
	 */
	public List<String> getStocksNames() {
		return stocksNames;
	}
	/**
	 * @param assetNames the assetNames to set
	 */
	public void setStocksNames(List<String> assetNames) {
		this.stocksNames = assetNames;
	}
	/**
	 * @return the assetStock
	 */
	public List<List<Item>> getAssetStock() {
		return stockMatrix[0];
	}

	/**
	 * @param assetStock the assetStock to set
	 */
	@Override
	public void setAssetStock(List<List<Item>> assetStock) {
		this.stockMatrix[0] = assetStock;
	}
	/**
	 * @return the liabilityStock
	 */
	public List<List<Item>> getLiabilityStock() {
		return stockMatrix[1];
	}
	/**
	 * @param liabilityStock the liabilityStock to set
	 */
	public void setLiabilityStock(List<List<Item>> liabilityStock) {
		this.stockMatrix[1] = liabilityStock;
	}
	/**
	 * add an asset to the corresponding list in the asset side of the stockMatrix
	 */
	public void addAsset(Item asset,int assetId){
		this.stockMatrix[0].get(assetId).add(asset);
	}
	/**
	 * remove an asset from the corresponding list in the asset side of the stockMatrix
	 */

	public void removeAsset(Item asset,int assetId){
		this.stockMatrix[0].get(assetId).remove(asset);
	}

	public List<Item> getAssets(int assetId){
		return this.stockMatrix[0].get(assetId);
	}

	/**
	 * add a liability to the corresponding list in the liability side of the stockMatrix
	 */
	public void addLiability(Item liability,int liabilityId){
		this.stockMatrix[1].get(liabilityId).add(liability);
	}
	/**
	 * remove a liability from the corresponding list in the liability side of the stockMatrix
	 */
	public void removeLiability(Item liability,int liabilityId){
		this.stockMatrix[1].get(liabilityId).remove(liability);
	}

	public List<Item> getLiabilies(int liabilityId){
		return this.stockMatrix[1].get(liabilityId);
	}

	/**
	 * Calculate the BS by aggregating the stocks per type (e.g. overall Lonas value, overall equities value etc.) on both the asset and liability sides 
	 * and then calculating the Net Wealth (which balance the two sides and it is represented as a 
	 * liability)
	 */
	public double[][] getNumericBalanceSheet(){
		int numAss=this.stocksNames.size();
		double[][] bs = new double[2][numAss+1];
		double aValue=0;
		double lValue=0;
		for(int i=0;i<numAss;i++){
			double aVal=0;
			double lVal=0;
			List<Item> assets = this.stockMatrix[0].get(i);
			for(int j=0;j<assets.size();j++){
				aVal+=assets.get(j).getValue();
			}
			List<Item> liabilities = this.stockMatrix[1].get(i);
			for(int j=0;j<liabilities.size();j++){
				lVal+=liabilities.get(j).getValue();
			}
			bs[0][i]=aVal;
			bs[1][i]=lVal;
			aValue+=aVal;
			lValue+=lVal;
		}
		bs[1][numAss]=aValue-lValue;
		return bs;
	}

	/**
	 * Calculate the BS and then represent it in String terms (showing also the names of assets) 
	 */
	public String[][] getStringBalanceSheet(){
		int numAss=this.stocksNames.size();
		String[][] bs = new String[3][numAss+1];
		double aValue=0;
		double lValue=0;
		for(int i=0;i<numAss;i++){
			double aVal=0;
			double lVal=0;
			List<Item> assets = this.stockMatrix[0].get(i);
			for(int j=0;j<assets.size();j++){
				aVal+=assets.get(j).getValue();
			}
			List<Item> liabilities = this.stockMatrix[1].get(i);
			for(int j=0;j<liabilities.size();j++){
				lVal+=liabilities.get(j).getValue();
			}
			bs[0][i]=this.stocksNames.get(i);
			bs[1][i]=String.valueOf(aVal);
			bs[2][i]=String.valueOf(lVal);
			aValue+=aVal;
			lValue+=lVal;
		}
		bs[0][numAss+1]="Net Worth";
		bs[1][numAss+1]="0";
		bs[2][numAss+1]=String.valueOf(aValue-lValue);
		return bs;
	}


	/**
	 * Method to add an item in the stock matrix
	 * @param item the item to be added in the type of asset
	 * @param asset whether the item is an asset or a liability
	 * @param idStock the type of stock item belongs to
	 */
	public void addItemStockMatrix(Item item, boolean asset, int idStock){
		item.setSMId(idStock);
		if(asset)
			this.stockMatrix[0].get(idStock).add(item);
		else
			this.stockMatrix[1].get(idStock).add(item);
	}

	public void removeItemStockMatrix(Item item, boolean asset, int idStock){
		if (asset)
			this.stockMatrix[0].get(idStock).remove(item);
		else
			this.stockMatrix[1].get(idStock).remove (item);
	}


	/**
	 * Method to obtain the first item of a particular type and held by a particular counterpart (holder) from the the stockMatrix .
	 * 
	 * @param asset whether the item is an asset or a liability
	 * @param idStock the type of stock item belongs to
	 * @param holder the counterpart of that item
	 */
	public Item getItemStockMatrix(boolean asset, int idStock, MacroAgent holder){
		List<Item> items;
		if(asset)
			items = this.stockMatrix[0].get(idStock);
		else
			items = this.stockMatrix[1].get(idStock);
		for(int i=0;i<items.size();i++){
			Item item=items.get(i);
			if(asset){
				if(item.getLiabilityHolder().getAgentId()==holder.getAgentId())
					return item;
			}else{
				if(item.getAssetHolder().getAgentId()==holder.getAgentId())
					return item;
			}	
		}
		return null;
	}

	/**
	 * Method to obtain the list of of items of a particular type and held by a specific counterpart (holder) from the stockMAtrix. 
	 * @param asset whether the items are assets or liabilities
	 * @param idItem the type of stock items belong to
	 * @param holder the counterpart of these items
	 */
	public List<Item> getItemsStockMatrix(boolean asset, int idItem){
		List<Item> result = new ArrayList<Item>();
		List<Item> items;
		if(asset)
			items = this.stockMatrix[0].get(idItem);
		else
			items = this.stockMatrix[1].get(idItem);
		for(int i=0;i<items.size();i++){
			Item item=items.get(i);
			result.add(item);
		}
		return result;
	}

	/**
	 * Method to obtain the first item of a particular type and held by a particular counterpart (holder) from the the stockMatrix .
	 * 
	 * @param asset whether the item is an asset or a liability
	 * @param idStock the type of stock item belongs to
	 * @param holder the counterpart of that item
	 */
	public Item getItemStockMatrix(boolean asset, int idStock){
		if(asset)
			return this.stockMatrix[0].get(idStock).get(0);
		else
			return this.stockMatrix[1].get(idStock).get(0);
	}

	/**
	 * Method to obtain the list of of items of a particular type and held by a specific counterpart (holder) from the stockMAtrix. 
	 * @param asset whether the items are assets or liabilities
	 * @param idItem the type of stock items belong to
	 * @param holder the counterpart of these items
	 */
	public List<Item> getItemsStockMatrix(boolean asset, int idItem, MacroAgent holder){
		List<Item> result = new ArrayList<Item>();
		List<Item> items;
		if(asset)
			items = this.stockMatrix[0].get(idItem);
		else
			items = this.stockMatrix[1].get(idItem);
		for(int i=0;i<items.size();i++){
			Item item=items.get(i);
			if(asset)
				if(item.getLiabilityHolder().getAgentId()==holder.getAgentId())
					result.add(item);
				else if(!asset)
					if(item.getAssetHolder().getAgentId()==holder.getAgentId())
						result.add(item);

		}
		return result;
	}

	/**
	 * Method to obtain the Id of a certain type of item within the StockMatrices (remember all StockMatrices
	 * contain all possible items in the economy, empty list if the agent doesn't hold a certain type of stocks)
	 * @param asset whether the items are assets or liabilities
	 * @param idItem the type of stock items belong to
	 * @param holder the counterpart of these items
	 */
	public int getStockMatrixId(Item item){
		int numAss=this.stocksNames.size();
		for(int i=0;i<numAss;i++){
			Item tempItem;
			if(!this.stockMatrix[0].get(i).isEmpty()){
				tempItem=this.stockMatrix[0].get(i).get(0);
				if(item.getClass()==tempItem.getClass())
					return i;
			}
			else if(!this.stockMatrix[1].get(i).isEmpty()){
				tempItem=this.stockMatrix[0].get(i).get(0);
				if(item.getClass()==tempItem.getClass())
					return i;
			}
		}
		return -1;
	}

	/**
	 * @return the expectations
	 */
	public Map<Integer, Expectation> getExpectations() {
		return expectations;
	}
	/**
	 * @param expectations the expectations to set
	 */
	public void setExpectations(Map<Integer, Expectation> expectations) {
		this.expectations = expectations;
	}

	/**
	 * Updates all expectation that an agent makes. All data have to have been updated before.
	 */
	public void computeExpectations(){
		Iterator<Integer> keys= this.expectations.keySet().iterator();
		while(keys.hasNext()){
			Integer key = (Integer)keys.next();
			Expectation expect = this.expectations.get(key);
			expect.updateExpectation();
		}
	}

	/**
	 * @param key
	 * @return the expectation related to key
	 */
	public Expectation getExpectation(Integer key){
		return this.expectations.get(key);
	}

	/** 
	 * NOT USED IN JMAB
	 */
	@Override
	public double getPayoff() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Strategy getStrategy(int idStrategy){
		MacroStrategy mStrategy = (MacroStrategy) this.strategy;
		return mStrategy.getStrategy(idStrategy);
	}
	/**
	 * @return the passedValues
	 */
	public Map<Integer, PassedValues> getPassedValues() {
		return passedValues;
	}
	/**
	 * @param passedValues the passedValues to set
	 */
	public void setPassedValues(Map<Integer, PassedValues> passedValues) {
		this.passedValues = passedValues;
	}

	/**
	 * Gets a lagged observation
	 * @param idValue the id of the observation
	 * @param lag the number of lag
	 */
	public double getPassedValue(int idValue,int lag){
		return this.passedValues.get(idValue).getObservation(Math.max(0,this.getRound()-lag));
	}

	/**
	 * Adds an observation
	 * @param idValue the id of the observation
	 * @param value the value of the observation
	 */
	public void addValue(int idValue, double value){
		int period = this.getRound();
		this.passedValues.get(idValue).addObservation(value, period);
	}

	/**
	 * @return
	 */
	private int getRound() {
		MacroSimulation macroSim = (MacroSimulation)((SimulationController)this.scheduler).getSimulation();
		return macroSim.getRound();
	}
	/**
	 * Methods that cleans the stock matrix by removing all items that are no longer useful.
	 */
	protected void cleanSM(){
		List<List<Item>> listOfItems= this.stockMatrix[0];
		for(int j=0;j<listOfItems.size();j++){
			List<Item> items=listOfItems.get(j);
			for(int k=items.size()-1;k>=0;k--){
				Item item=items.get(k);
				item.update();
				if(item.remove()){
					MacroAgent liabilityHolder = item.getLiabilityHolder();
					if(liabilityHolder!=null)
						liabilityHolder.removeItemStockMatrix(item, false, item.getSMId());
					items.remove(item);
				}
			}
		}
	}

	/**
	 * TODO
	 */
	public double getNetWealth(){
		double[][] bs = this.getNumericBalanceSheet();
		return bs[1][bs[1].length-1];
	}

	public double getAggregateValue(int idValue, int lag){
		SimulationController controller = (SimulationController)this.scheduler;
		MacroSimulation sim = (MacroSimulation)controller.getSimulation();
		return sim.getPassedValue(idValue, lag);
	}

	public void setAggregateValue(int idValue, double value){
		SimulationController controller = (SimulationController)this.scheduler;
		MacroSimulation sim = (MacroSimulation)controller.getSimulation();
		sim.addValue(idValue, value);
	}

	/**
	 * @return the stockMatrix
	 */
	public List<List<Item>>[] getStockMatrix() {
		return stockMatrix;
	}
	/**
	 * @param stockMatrix the stockMatrix to set
	 */
	public void setStockMatrix(List<List<Item>>[] stockMatrix) {
		this.stockMatrix = stockMatrix;
	}


	/**
	 * Generate the byte array representing the passed values. The structure is the following
	 * [nbPassedValues]
	 * for each type of stocks
	 * 	[idPassedValue][passedValueSize][passedValueStrucure]
	 * end for 	
	 * @return
	 */
	public byte[] getPassedValuesBytes(){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			if(passedValues!=null){
				output.write(ByteBuffer.allocate(4).putInt(passedValues.size()).array());
				Set<Integer> orderedKeys = passedValues.keySet();
				for(Integer key:orderedKeys){
					output.write(ByteBuffer.allocate(4).putInt(key).array());
					PassedValues val = passedValues.get(key);
					byte[] valBytes = val.getByteArray();
					output.write(ByteBuffer.allocate(4).putInt(valBytes.length).array());
					output.write(valBytes);

				}
			}else
				output.write(ByteBuffer.allocate(4).putInt(0).array());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	/**
	 * Generate the byte array representing the expectations. The structure is the following
	 * [nbExpectations]
	 * for each type of stocks
	 * 	[idExpectations][ExpectationsSize][ExpectationsStrucure]
	 * end for 	
	 * @return
	 */
	public byte[] getExpectationsBytes(){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			if(this.expectations!=null){
				output.write(ByteBuffer.allocate(4).putInt(expectations.size()).array());
				Set<Integer> orderedKeys = expectations.keySet();
				for(Integer key:orderedKeys){
					output.write(ByteBuffer.allocate(4).putInt(key).array());
					Expectation exp = expectations.get(key);
					byte[] expBytes = exp.getByteArray();
					output.write(ByteBuffer.allocate(4).putInt(expBytes.length).array());
					output.write(expBytes);
				}
			}
			else
				output.write(ByteBuffer.allocate(4).putInt(0).array());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	/**
	 * Generate the byte array representing the stock matrix. The structure is the following
	 * [nbStockTypes]
	 * for each type of stocks
	 * 	[IdStock][nbItems]
	 * 		for each Item
	 * 			[itemSize][itemStructure]
	 * 		end for
	 * end for 	
	 * @return
	 */
	public byte[] getStockMatrixBytes(){
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		List<List<Item>> listOfItems= this.stockMatrix[0];
		try {
			output.write(ByteBuffer.allocate(4).putInt(listOfItems.size()).array());
			for(int j=0;j<listOfItems.size();j++){
				List<Item> items=listOfItems.get(j);
				output.write(ByteBuffer.allocate(4).putInt(j).array());
				output.write(ByteBuffer.allocate(4).putInt(items.size()).array());
				for(Item item:items){
					byte[] itemByte = item.getBytes();
					output.write(ByteBuffer.allocate(4).putInt(itemByte.length).array());

					output.write(itemByte);

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	/**
	 * Generates the byte array representing the characteristics of the agent. The structure is the following
	 * [agentId][populationId][dead][defaulted][numberStocks][numberMarkets][activeAgents][numberTics][activeTicEvents]
	 * @return the byte array
	 */
	public byte[] getAgentCharacteristicsBytes(){
		int nbTics = activeTicEvents.size();
		byte[] result = new byte[26+numberMarkets+nbTics*4];
		ByteBuffer buf = ByteBuffer.wrap(result);
		buf.putLong(agentId);
		buf.putInt(populationId);
		if(dead)
			buf.put((byte)1);
		else
			buf.put((byte)0);
		if(defaulted)
			buf.put((byte)1);
		else
			buf.put((byte)0);
		buf.putInt(numberStocks);
		buf.putInt(numberMarkets);
		for(int i = 0 ; i < numberMarkets ; i++){
			if(activeAgent[i])
				buf.put((byte)1);
			else
				buf.put((byte)0);
		}
		buf.putInt(nbTics);
		for(Integer tic:activeTicEvents){
			buf.putInt(tic);
		}
		return result;
	}

	/**
	 * Populates the characteristics of the agent using the byte array content. The structure is the following
	 * [agentId][populationId][dead][defaulted][numberStocks][numberMarkets][activeAgents][numberTics][activeTicEvents]
	 * @param the byte array
	 */
	public void populateCharacteristics(byte[] content, MacroPopulation pop){
		ByteBuffer buf = ByteBuffer.wrap(content);
		this.agentId = buf.getLong();
		this.populationId = buf.getInt();
		this.dead = buf.get()==1;
		this.defaulted = buf.get()==1;
		this.numberStocks = buf.getInt();
		this.numberMarkets = buf.getInt();
		this.activeAgent = new boolean[this.numberMarkets];
		for(int i = 0 ; i < numberMarkets ; i++){
			this.activeAgent[i] = buf.get()==1;
		}
		int nbTics = buf.getInt();
		this.activeTicEvents = new ArrayList<Integer>();
		for(int i = 0 ; i < nbTics ; i++)
			this.activeTicEvents.add(buf.getInt());
	}

	/**
	 * Generates the byte array containing all strategies byte arrays. Structure is as follows:
	 * [size][MacroStrategyStructure]
	 * @return
	 */
	public byte[] getStrategiesBytes(){
		MacroStrategy strats = (MacroStrategy) this.strategy;
		return strats.getBytes();
	}

	/**
	 * Populates the Macro Strategy from the byte array content. The structure should be as follows:
	 * [MacroStrategyStructure]
	 */
	public void populateStrategies(byte[] content, MacroPopulation pop){
		MacroStrategy strats = (MacroStrategy) this.strategy;
		strats.populateFromBytes(content, pop);
	}

	abstract public void populateAgent(byte[] content, MacroPopulation pop);

	abstract public byte[] getBytes();

	abstract public void populateStockMatrixBytes(byte[] content, MacroPopulation pop);

	/**
	 * Populates the expectations from the byte array content. The structure is the following
	 * [nbExpectations]
	 * for each type of stocks
	 * 	[idExpectations][ExpectationsSize][ExpectationsStrucure]
	 * end for 	
	 * @return
	 */
	public void populateExpectationsBytes(byte[] content){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbExp = buf.getInt();
		for(int i = 0 ; i < nbExp ; i++){
			int key = buf.getInt();
			int expSize = buf.getInt();
			byte[] expBytes = new byte[expSize];
			buf.get(expBytes);
			Expectation exp = this.expectations.get(key);
			exp.populateExpectation(expBytes);
		}
	}

	/**
	 * Populates the passed values from the byte array content. The structure is the following
	 * [nbPassedValues]
	 * for each type of stocks
	 * 	[idPassedValue][passedValueSize][passedValueStrucure]
	 * end for 	
	 * @return
	 */
	public void populatePassedValuesBytes(byte[] content){
		ByteBuffer buf = ByteBuffer.wrap(content);
		int nbExp = buf.getInt();
		for(int i = 0 ; i < nbExp ; i++){
			int key = buf.getInt();
			int valSize = buf.getInt();
			byte[] valBytes = new byte[valSize];
			buf.get(valBytes);
			PassedValues val = this.passedValues.get(key);
			val.populateExpectation(valBytes);
		}
	}
	
	public void addToMarketPopulation(int mktId, boolean demandSide){
		SimulationController controller = (SimulationController)this.scheduler;
		MacroSimulation sim = (MacroSimulation)controller.getSimulation();
		MarketPopulation mktPop = sim.getMarket(mktId).getPopulation();
		if(demandSide)
			mktPop.getBuyers().add(this);
		else
			mktPop.getSellers().add(this);
	}

	/*OLD CODE
protected void buyGood(ArrayList<Agent> arrayListAgent,GoodMarketSimulation simulation, double demand, 
			int strategyID, int marketID, boolean real) {
		MacroStrategy strategies = (MacroStrategy) getStrategy();
		BuyingStrategy buyingStrategy = (BuyingStrategy) strategies.getStrategy(strategyID);
		Agent seller=buyingStrategy.selectSeller(arrayListAgent, demand, real);
		simulation.commitTransaction(this, seller,marketID);
	}

	protected void getLoan(ArrayList<Agent> lenders,CreditMarketSimulation simulation, double amount, int length, 
			int strategyID, int creditID) {
		MacroStrategy strategies = (MacroStrategy) getStrategy();
		BorrowingStrategy borrowingStrategy = (BorrowingStrategy) strategies.getStrategy(strategyID);
		Agent lender=borrowingStrategy.selectLender(lenders, amount, length);
		simulation.commitCredit(this, lender, creditID);
	}

	protected double getPrice(Buyer buyer, double demand, boolean real, int strategyID){
		if(isDead)
			return Double.POSITIVE_INFINITY;
		else{
			MacroStrategy strategies = (MacroStrategy) getStrategy();
			PricingStrategy buyingStrategy = (PricingStrategy) strategies.getStrategy(strategyID);
			return buyingStrategy.setPrice((Seller)this, buyer, demand, real);
		}
	}

	protected double getOutput(int strategyID){
		MacroStrategy strategies = (MacroStrategy) getStrategy();
		ProductionStrategy productionStrategy = (ProductionStrategy) strategies.getStrategy(strategyID);
		return productionStrategy.setOutput(this);
	}

	protected Strategy getStrategy(int strategyID){
		MacroStrategy strategies = (MacroStrategy) getStrategy();
		return strategies.getStrategy(strategyID);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof RoundFinishedEvent) {
			onRoundFinished((RoundFinishedEvent)event);
		} else {
			super.eventOccurred(event);
		}
	}

	public void onRoundFinished(RoundFinishedEvent event){
		if(isDead){
			MacroSimulation simulation = (MacroSimulation)event.getSimulation();
			simulation.agentDie(populationId, this);
		}else{
			activateAgent();
		}
	}*/
}
