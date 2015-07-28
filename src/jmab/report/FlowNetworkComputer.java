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
package jmab.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jmab.agents.MacroAgent;
import jmab.goods.AbstractGood;
import jmab.goods.Item;
import jmab.population.MacroPopulation;
import jmab.simulations.MacroSimulation;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.report.CSVWriter;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 * Generates the network of balance sheet connections among the selected populations for the selected assets. Each NxN matrix is represented 
 * as one 1x(N+2) row, where the two first fields (with key "-1" and "-2") contain 1 the number of agents (i.e. N) and 2 the selection of assets (where each 
 * number is separated by a hyphen "-". Each cell of the row is given by the concatenation of the following information (separated by 
 * "-"): 1. agent type, 2 list of all the issuers' id of the assets belonging to assetsId.
 */
public class FlowNetworkComputer extends AbstractMicroComputer implements
MicroMultipleVariablesComputer {

	private int[] populationsId;
	private int[] assetsId;
	private boolean weighted;
	private Object fileNamePrefix;
	private String fileName;
	private String fileNameExtension;
	private CSVWriter csvWriter;
	private boolean firstRound = true;
	private int fileNumber;

	//TODO: MAKE SURE THAT THE ASSET ISSUER IS IN THE POPULATIONS YOU WANT TO SEE
	public FlowNetworkComputer(){}
	
	/**
	 * @param populationsId
	 * @param assetsId
	 * @param weighted
	 * @param fileName
	 */
	public FlowNetworkComputer(int[] populationsId, int[] assetsId,
			boolean weighted, String fileName) {
		super();
		this.populationsId = populationsId;
		this.assetsId = assetsId;
		this.weighted = weighted;
		this.fileName = fileName;
	}


	/* (non-Javadoc)
	 * @see jmab.report.MicroMultipleVariablesComputer#computeVariables(jmab.simulations.MacroSimulation)
	 */
	@Override
	public Map<Long, Double> computeVariables(MacroSimulation sim) {

		MacroPopulation macroPop = (MacroPopulation) sim.getPopulation();
		TreeMap<Long,String> result=new TreeMap<Long,String>();
		ArrayList<Integer> populations = new ArrayList<Integer>();
		for(int populationId:populationsId)
			populations.add(populationId);
		int popSize = 0;
		for(int populationId:populationsId){
			Population pop = macroPop.getPopulation(populationId);
			popSize+=pop.getSize();
			for (Agent i:pop.getAgents()){
				String entry = "";
				MacroAgent agent=(MacroAgent) i;
				entry = entry.concat(String.valueOf(populationId));
				if (!agent.isDead()){
					TreeMap<Long,TreeMap<Integer, Double>> agentList=new TreeMap<Long,TreeMap<Integer, Double>>();
					for(int assetId:assetsId){
						List<Item> assets=agent.getItemsStockMatrix(true, assetId);
						for (Item j:assets){
							if(j.getAge()==0){
								long liabId;
								if(j instanceof AbstractGood){
								AbstractGood good = (AbstractGood) j;
								liabId = good.getProducer().getAgentId();
								}else{
									liabId = j.getLiabilityHolder().getAgentId();
								}
								TreeMap<Integer,Double> liabEntry;
								if(agentList.containsKey(liabId))
									liabEntry = agentList.remove(liabId);
								else
									liabEntry = new TreeMap<Integer,Double>();
								double val = j.getValue();
								if(liabEntry.containsKey(assetId))
									val+=liabEntry.remove(assetId);
								liabEntry.put(assetId, val);
								agentList.put(liabId, liabEntry);
							}
						}
					}
					Set<Long> orderedKeys =  agentList.keySet();
					for(Long key:orderedKeys){
						TreeMap<Integer,Double> liabEntry = agentList.get(key);
						entry = entry.concat("|");
						entry = entry.concat(String.valueOf(key));
						Set<Integer> orderedAssetsKeys =  liabEntry.keySet();
						for(Integer assetKey:orderedAssetsKeys){
							entry = entry.concat("&");
							entry = entry.concat(String.valueOf(assetKey));
							if(weighted){
								entry = entry.concat("&");
								entry = entry.concat(String.valueOf(liabEntry.get(assetKey)));
							}
						}
					}
				}
				else{
					entry = entry.concat("|");
					entry = entry.concat(String.valueOf(Double.NaN));
				}
				result.put(agent.getAgentId(), entry);
			}
		}
		
		result.put((long) -1, String.valueOf(popSize));
		
		String entry = String.valueOf(sim.getRound());
		for(int assetId:assetsId){
				entry = entry.concat("|");
				entry = entry.concat(String.valueOf(assetId));
		}
		result.put((long) -2, entry);
		
		Set<Long> orderedKeys =  result.keySet();
		
		if(firstRound){
			for(Long key:orderedKeys){
				csvWriter.newData(key);
			}
			csvWriter.endRecord();
			firstRound=false;
		}
		
		for(Long key:orderedKeys){
			csvWriter.newData(result.get(key));
		}
		csvWriter.endRecord();
		
		return new TreeMap<Long,Double>();
	}


	/**
	 * @return the populationsId
	 */
	public int[] getPopulationsId() {
		return populationsId;
	}


	/**
	 * @param populationsId the populationsId to set
	 */
	public void setPopulationsId(int[] populationsId) {
		this.populationsId = populationsId;
	}


	/**
	 * @return the marketId
	 */
	public int[] getAssetsId() {
		return assetsId;
	}


	/**
	 * @param assetsId the marketId to set
	 */
	public void setAssetsId(int[] assetsId) {
		this.assetsId = assetsId;
	}


	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}


	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * @return the weighted
	 */
	public boolean isWeighted() {
		return weighted;
	}


	/**
	 * @param weighted the weighted to set
	 */
	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}

	/**
	 * @return the fileNamePrefix
	 */
	public Object getFileNamePrefix() {
		return fileNamePrefix;
	}

	/**
	 * @param fileNamePrefix the fileNamePrefix to set
	 */
	public void setFileNamePrefix(Object fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
	
	/**
	 * @return the fileNameExtension
	 */
	public String getFileNameExtension() {
		return fileNameExtension;
	}

	/**
	 * @param fileNameExtension the fileNameExtension to set
	 */
	public void setFileNameExtension(String fileNameExtension) {
		this.fileNameExtension = fileNameExtension;
	}

	@Override
	public void dispose(){
		if (csvWriter != null) {
			csvWriter.close();
		}
		fileNumber++;
	}
	
	@Override
	public void initialise(){
		try {
			csvWriter = new CSVWriter(new FileOutputStream(this.fileNamePrefix + fileName + getNumberingSuffix() + fileNameExtension),',');
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getNumberingSuffix() {
		return Integer.toString(fileNumber + 1);
	}
	

}
