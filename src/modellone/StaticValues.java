/*
n * JMAB - Java Macroeconomic Agent Based Modeling Toolkit
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
package modellone;

/**
 * @author Alessandro Caiani and Antoine Godin
 *
 */
public interface StaticValues {

	public static int TIC_COMPUTEEXPECTATIONS=0;
	public static int TIC_CAPITALPRICE=1;
	public static int TIC_CONSUMPTIONPRICE=2;
	public static int TIC_CAPITALMARKET1=3;
	public static int TIC_RDDECISION=4;
	public static int TIC_INVESTMENTDEMAND=5;
	public static int TIC_CREDITSUPPLY=6;
	public static int TIC_DEPOSITSUPPLY=7;
	public static int TIC_DEPINTERESTS=8;
	public static int TIC_CREDITDEMAND=9;
	public static int TIC_CREDITMARKET=10;	
	public static int TIC_LABORSUPPLY=11;
	public static int TIC_LABORDEMAND=12;
	public static int TIC_GOVERNMENTLABOR=13;
	public static int TIC_LABORMARKET=14;
	public static int TIC_PRODUCTION=15;
	public static int TIC_RDOUTCOME=16;
	public static int TIC_CONSUMPTIONDEMAND=17;
	public static int TIC_CONSUMPTIONMARKET=18;
	public static int TIC_CAPITALMARKET2=19;
	public static int TIC_CREDINTERESTS=20;
	public static int TIC_WAGEPAYMENT = 21;
	public static int TIC_BONDINTERESTS=22;
	public static int TIC_ADVINTERESTS=23;
	public static int TIC_TAXES=24;
	public static int TIC_DIVIDENDS=25;
	public static int TIC_DEPOSITDEMAND=26;
	public static int TIC_DEPOSITMARKET=27;
	public static int TIC_BANKRUPTCY=28;
	public static int TIC_BONDSUPPLY=29;
	public static int TIC_BONDMARKET1=30;
	public static int TIC_BONDDEMAND=31;
	public static int TIC_RESERVEDEMANDBOND=32;
	public static int TIC_RESERVEMARKETBOND=33;
	public static int TIC_CBBONDSPURCHASES=34;
	public static int TIC_BONDMARKET2=35;
	public static int TIC_RESERVEDEMANDBASEL=36;
	public static int TIC_RESERVEMARKETBASEL=37;
	public static int TIC_GDPREPORT=38;
	public static int TIC_INVREPORT=39;
	public static int TIC_UNEMPLOYMENTREPORT=40;
	public static int TIC_MICROLOANSKREPORT=41;
	public static int TIC_MICROLOANSCREPORT=42;
	public static int TIC_MICROCREDITDEGREEREPORT=43;
	public static int TIC_BANKSCREDITDEGREEREPORT=44;
	public static int TIC_MICROREALCONSREPORT=45;
	public static int TIC_MICRONOMINALCONSREPORT=46;
	public static int TIC_MICROREALINVREPORT=47;
	public static int TIC_MICRONOMINALINVREPORT=48;
	public static int TIC_MICROREALCINVENTREPORT=49;
	public static int TIC_MICROREALKINVENTREPORT=50;
	public static int TIC_MICROCGROWTHREPORT=51;
	public static int TIC_AGGHHBSREPORT=52;
	public static int TIC_AGGCFBSREPORT=53;
	public static int TIC_AGGKFBSREPORT=54;
	public static int TIC_AGGBBSREPORT=55;
	public static int TIC_AGGGBSREPORT=56;
	public static int TIC_AGGCBBSREPORT=57;
	public static int TIC_REALCDESOUTPUTREPORT=58;
	public static int TIC_REALKDESOUTPUTREPORT=59;
	public static int TIC_CEMPLOYEEREPORT=60;
	public static int TIC_KEMPLOYEEREPORT=61;
	public static int TIC_BANKSPROFITSREPORT=62;
	public static int TIC_CFIRMSPROFITSREPORT=63;
	public static int TIC_KFIRMSPROFITSREPORT=64;
	public static int TIC_HHWAGEREPORT=65;
	public static int TIC_HHNETINCOMEREPORT=66;
	public static int TIC_CFIRMSPRICESREPORT=67;
	public static int TIC_KFIRMSPRICESREPORT=68;
	public static int TIC_MICROREALDESCONSREPORT=69;
	public static int TIC_CFIRMSBANKRUPTCYREPORT=70;
	public static int TIC_KFIRMSBANKRUPTCYREPORT=71;
	public static int TIC_BANKSBANKRUPTCYREPORT=72;
	public static int TIC_CFIRMSIRREPORT=73;
	public static int TIC_KFIRMSIRREPORT=74;
	public static int TIC_HHIRREPORT=75;
	public static int TIC_CFIRMSINTERESTSTOPAYREPORT=76;
	public static int TIC_KFIRMSINTERESTSTOPAYREPORT=77;
	public static int TIC_CFIRMSDEBTSERVICEREPORT=78;
	public static int TIC_KFIRMSDEBTSERVICEREPORT=79;
	public static int TIC_CFIRMSWAGEBILLREPORT=80;
	public static int TIC_KFIRMSWAGEBILLREPORT=81;
	public static int TIC_CAGGREGATEDEBTREPORT=82;
	public static int TIC_KAGGREGATEDEBTREPORT=83;
	public static int TIC_CFIRMSLEVREPORT=84;
	public static int TIC_KFIRMSLEVREPORT=85;
	public static int TIC_CFIRMSCONSCREDREPORT=86;
	public static int TIC_KFIRMSCONSCREDREPORT=87;
	public static int TIC_BMAXEXPOSUREREPORT=88;
	public static int TIC_CCAPACITYREPORT=89;
	public static int TIC_CCAPACITYUTILIZATIONREPORT=90;
	public static int TIC_CNOMINALSALESREPORT=91;
	public static int TIC_CREALSALESREPORT=92;
	public static int TIC_KNOMINALSALESREPORT=93;
	public static int TIC_KREALSALESREPORT=94;
	public static int TIC_KOUTPUTREPORT=95;
	public static int TIC_COUTPUTREPORT=96;
	public static int TIC_KCREDITOBTAINEDREPORT=97;
	public static int TIC_CCREDITOBTAINEDREPORT=98;
	public static int TIC_MICROREALDESINVREPORT=99;
	public static int TIC_CEXPAVVARCOSTSREPORT=100;
	public static int TIC_KEXPAVVARCOSTSREPORT=101;
	public static int TIC_BANKSSPECIFICINTERESTREPORT=102;
	public static int TIC_BANKSDEPOSITINTERESTREPORT=103;
	public static int TIC_BANKSCRREPORT=104;
	public static int TIC_BANKSLRREPORT=105;
	public static int TIC_BANKSLOSSREPORT=106;
	public static int TIC_CFIRMSTAXESREPORT=107;
	public static int TIC_KFIRMSTAXESREPORT=108;
	public static int TIC_HHTAXESREPORT=109;
	public static int TIC_BANKSTAXESREPORT=110;
	public static int TIC_CDIVIDENDSREPORT=111;
	public static int TIC_KDIVIDENDSREPORT=112;
	public static int TIC_BANKSDIVIDENDSREPORT=113;
	public static int TIC_HHDEPOSITSREPORT=114;
	public static int TIC_BANKSDEPOSITSREPORT=115;
	public static int TIC_OPERATINGCASHFLOWREPORT=116;
	public static int TIC_GOVWAGEBILLREPORT=117;
	public static int TIC_GOVDOLEEXPENDITUREREPORT=118;
	public static int TIC_GOVINTERESTSBONDSREPORT=119;
	public static int TIC_GOVCBPROFITSREPORT=120;
	public static int TIC_CBAILOUTCOSTREPORT=121;
	public static int TIC_KBAILOUTCOSTREPORT=122;
	public static int TIC_BANKSBAILOUTCOSTREPORT=123;
	public static int TIC_TFMREPORT=124;
	public static int TIC_BSNETREPORT=125;
	public static int TIC_BANKSCREDITEXCESSREPORT=126;
	public static int TIC_BANKSCREDITSUPPLYREPORT=127;
	public static int TIC_BANKSTARGETCARREPORT=128;
	public static int TIC_FLOWNETREPORT=129;
	public static int TIC_AVCPRICEREPORT=130;
	public static int TIC_AVKPRICEREPORT=131;
	public static int TIC_AVLOANINTERESTREPORT=132;
	public static int TIC_AVDEPINTERESTREPORT=133;
	public static int TIC_AVWAGEREPORT=134;
	public static int TIC_AVNETINCOMEREPORT=135;
	public static int TIC_CAGGCONSCREDITREPORT=136;
	public static int TIC_KAGGCONSCREDITREPORT=137;
	public static int TIC_POPULATIONHANDLER=1000;
	public static int TIC_UPDATEEXPECTATIONS=1001;
	public static int TIC_SERIALIZATION=1002;
	
	public static int EXPECTATIONS_NOMINALSALES=0;
	public static int EXPECTATIONS_WAGES=1;
	public static int EXPECTATIONS_CONSPRICE=2;
	public static int EXPECTATIONS_DEPOSITS=3;
	public static int EXPECTATIONS_REALSALES = 4;
	
	public static int LAG_INVENTORIES=0;
	public static int LAG_REALSALES=1;
	public static int LAG_PRODUCTION=2;
	public static int LAG_PROFITPRETAX=3;
	public static int LAG_PROFITAFTERTAX=4;
	public static int LAG_OPERATINGCASHFLOW=5;
	public static int LAG_NETWEALTH=6;
	public static int LAG_INCOME=7;
	public static int LAG_REMAININGCREDIT = 8;
	public static int LAG_NONPERFORMINGLOANS=9;
	public static int LAG_EMPLOYED = 10;
	public static int LAG_AGGUNEMPLOYMENT = 11;
	public static int LAG_NOMINALSALES = 12;
	public static int LAG_CONSUMPTION = 13;
	public static int LAG_CAPACITY=14;
	public static int LAG_CAPITALFINANCIALVALUE=15;
	public static int LAG_NOMINALINVENTORIES=16;
	public static int LAG_BANKTOTLOANSUPPLY=17;
	public static int LAG_TAXES = 18;
	public static int LAG_CAPITALAMORTIZATION = 19;
	public static int LAG_DEPOSITINTEREST= 20;
	public static int LAG_LOANINTEREST= 21;
	public static int LAG_PRICE=22;
	
	public static int SM_CASH=0;
	public static int SM_DEP=1;
	public static int SM_CONSGOOD=2;
	public static int SM_CAPGOOD=3;
	public static int SM_LOAN=4;
	public static int SM_BONDS=5;
	public static int SM_RESERVES=6;
	public static int SM_ADVANCES = 7;
	
	public static int MKT_CAPGOOD=0;
	public static int MKT_CONSGOOD=1;
	public static int MKT_CREDIT=2;
	public static int MKT_DEPOSIT=3;
	public static int MKT_LABOR=4;
	public static int MKT_BONDS=5;
	public static int MKT_ADVANCES=6;
	
	public static int STRATEGY_BUYING=0;
	public static int STRATEGY_BORROWING=1;
	public static int STRATEGY_FINANCE=2;
	public static int STRATEGY_INVESTMENT=3;
	public static int STRATEGY_CAPITALDEMAND=4;
	public static int STRATEGY_PRICING=5;
	public static int STRATEGY_PRODUCTION=6;
	public static int STRATEGY_TAXES=7;
	public static int STRATEGY_CONSUMPTION=8;
	public static int STRATEGY_CREDITSUPPLY=9;
	public static int STRATEGY_SPECIFICCREDITSUPPLY=10;
	public static int STRATEGY_DEPOSIT = 11;
	public static int STRATEGY_LABOR = 12;
	public static int STRATEGY_BONDDEMAND = 13;
	public static int STRATEGY_ADVANCES = 14;
	public static int STRATEGY_LOANBANKINTERESTRATE = 15;
	public static int STRATEGY_LOANAGENTINTERESTRATE = 16;
	public static int STRATEGY_DEPOSITINTERESTRATE = 17;
	public static int STRATEGY_RANDDEVELOPMENTINVESTMENT=18;
	public static int STRATEGY_RANDDEVELOPMENTOUTCOME=19;
	public static int STRATEGY_UPDATEINVPRODUCTIVITY=20;
	public static int STRATEGY_BONDINTERESTRATE = 21;
	public static int STRATEGY_BANKRUPTCY = 22;
	public static int STRATEGY_WAGE = 23;
	public static int STRATEGY_DIVIDENDS = 24;
	
	public static int CAPITALFIRMS_ID=0;
	public static int CONSUMPTIONFIRMS_ID=1;
	public static int BANKS_ID=2;
	public static int HOUSEHOLDS_ID=3;
	public static int CB_ID=4;
	public static int GOVERNMENT_ID=5;
	
	public static int GDPREPORT_ID=1;
	public static int INVREPORT_ID=2;
	public static int UNEMPLOYMENTREPORT_ID=3;
	public static int MICROLOANSKREPORT_ID=4;
	public static int MICROLOANSCREPORT_ID=5;
	public static int MICROREALCONSREPORT_ID=6;
	public static int MICRONOMINALCONSREPORT_ID=7;
	public static int MICROREALINVREPORT_ID=8;
	public static int MICRONOMINALINVREPORT_ID=9;
	public static int MICROREALCINVENTREPORT_ID=10;
	public static int MICROREALKINVENTREPORT_ID=11;
	public static int MICROCGROWTHREPORT_ID=12;
	public static int AGGHHBSREPORT_ID=13;
	public static int AGGCFBSREPORT_ID=14;
	public static int AGGKFBSREPORT_ID=15;
	public static int AGGBBSREPORT_ID=16;
	public static int AGGGBSREPORT_ID=17;
	public static int AGGCBBSREPORT_ID=18;
	public static int REALCDESOUTPUTREPORT_ID=19;
	public static int REALKDESOUTPUTREPORT_ID=20;
	public static int CEMPLOYEEREPORT_ID=21;
	public static int KEMPLOYEEREPORT_ID=22;
	public static int BANKSPROFITSREPORT_ID=23;
	public static int CFIRMSPROFITSREPORT_ID=24;
	public static int KFIRMSPROFITSREPORT_ID=25;
	public static int HHWAGEREPORT_ID=26;
	public static int HHNETINCOMEREPORT_ID=27;
	public static int CFIRMSPRICESREPORT_ID=28;
	public static int KFIRMSPRICESREPORT_ID=29;
	public static int MICROREALDESCONSREPORT_ID=30;
	public static int MICROCREDITDEGREEREPORT_ID=31;
	public static int MICROBANKCREDITREPORT_ID=32;
	public static int CFIRMSTARGLEVREPORT_ID=33;
	public static int KFIRMSTARGLEVREPORT_ID=34;
	public static int CFIRMSBANKRUPTCYREPORT_ID=35;
	public static int KFIRMSBANKRUPTCYREPORT_ID=36;
	public static int BANKSBANKRUPTCYREPORT_ID=37;
	public static int CFIRMSIRREPORT_ID=38;
	public static int KFIRMSIRREPORT_ID=39;
	public static int HHIRREPORT_ID=40;
	public static int CFIRMSINTERESTSTOPAYREPORT_ID=41;
	public static int KFIRMSINTERESTSTOPAYREPORT_ID=42;
	public static int CFIRMSDEBTSERVICEREPORT_ID=43;
	public static int KFIRMSDEBTSERVICEREPORT_ID=44;
	public static int CFIRMSWAGEBILLREPORT_ID=45;
	public static int KFIRMSWAGEBILLREPORT_ID=46;
	public static int CAGGREGATEDEBTREPORT=47;
	public static int KAGGREGATEDEBTREPORT=48;
	public static int CFIRMSLEVREPORT_ID=49;
	public static int KFIRMSLEVREPORT_ID=50;
	public static int CFIRMSCONSCREDREPORT_ID=51;
	public static int KFIRMSCONSCREDREPORT_ID=52;
	public static int BMAXEXPOSUREREPORT_ID=53;
	public static int MICROCCAPACITYREPORT_ID=54;
	public static int MICROCCAPACITYUTILIZATIONREPORT_ID=55;
	public static int BANKSCREDITEXCESSREPORT_ID=56;
	public static int BANKSSPECIFICINTERESTREPORT_ID=57;
	public static int BANKSDEPOSITINTERESTREPORT_ID=58;
	public static int BANKSCREDITSUPPLYREPORT_ID=59;
	public static int BANKSTARGETCARREPORT_ID=60;
	public static int BANKSDEPOSITSREPORT_ID=61;
	public static int KOPERATINGCASHFLOWREPORT_ID=62;
	public static int COPERATINGCASHFLOWREPORT_ID=63;
	public static int CDIVIDENDSREPORT_ID=631;
	public static int KDIVIDENDSREPORT_ID=632;
	public static int BANKSDIVIDENDSREPORT_ID=633;
	public static int TFMREPORT_ID=64;
	public static int BSNETREPORT_ID=65;
	public static int CNOMINALSALESREPORT_ID=66;
	public static int CREALSALESREPORT_ID=67;
	public static int KNOMINALSALESREPORT_ID=68;
	public static int KREALSALESREPORT_ID=69;
	public static int KOUTPUTREPORT_ID=70;
	public static int COUTPUTREPORT_ID=71;
	public static int KCREDITOBTAINEDREPORT_ID=72;
	public static int CCREDITOBTAINEDREPORT_ID=73;
	public static int MICROREALDESINVREPORT_ID=74;
	public static int KEXPAVVARCOSTSREPORT_ID=75;
	public static int CEXPAVVARCOSTSREPORT_ID=76;
	public static int BANKSCRREPORT_ID=77;
	public static int BANKSLRREPORT_ID=78;
	public static int CFIRMSTAXESREPORT_ID=79;
	public static int KFIRMSTAXESREPORT_ID=80;
	public static int HHTAXESREPORT_ID=81;
	public static int BANKSTAXESREPORT_ID=82;
	public static int BANKSLOSSREPORT_ID=83;
	public static int GOVWAGEBILLREPORT_ID=84;
	public static int GOVDOLEEXPENDITUREREPORT_ID=85;
	public static int GOVINTERESTSBONDS_ID=86;
	public static int GOVCBPROFITSREPORT_ID=87;
	public static int HHDEPOSTSREPORT_ID=88;
	public static int CBAILOUTCOSTREPORT_ID=89;
	public static int KBAILOUTCOSTREPORT_ID=90;
	public static int BANKSBAILOUTCOSTREPORT_ID=91;
	public static int FLOWNETREPORT_ID=92;
	public static int AVCPRICEREPORT_ID=93;
	public static int AVKPRICEREPORT_ID=94;
	public static int AVLOANINTERESTREPORT_ID=95;
	public static int AVDEPINTERESTREPORT_ID=96;
	public static int AVWAGEREPORT_ID=97;
	public static int AVNETINCOMEREPORT_ID=98;
	public static int CAGGCONSCREDITREPORT_ID=99;
	public static int KAGGCONSCREDITREPORT_ID=100;







	public static int TFM_CONS=0;
	public static int TFM_WHH=1;
	public static int TFM_DOLEHH=2;
	public static int TFM_THH=3;
	public static int TFM_IDHH=4;
	public static int TFM_DIVHH=5;
	public static int TFM_CONSCF=6;
	public static int TFM_WCF=7;
	public static int TFM_TCF=8;
	public static int TFM_INVCF=9;
	public static int TFM_CACF=10;
	public static int TFM_IDCF=11;
	public static int TFM_ILCF=12;
	public static int TFM_DIVCF=13;
	public static int TFM_RECF=14;
	public static int TFM_WKF=15;
	public static int TFM_TKF=16;
	public static int TFM_INVKF=17;
	public static int TFM_IDKF=18;
	public static int TFM_ILKF=19;
	public static int TFM_DIVKF=20;
	public static int TFM_REKF=21;
	public static int TFM_TB=22;
	public static int TFM_IDB=23;
	public static int TFM_IBB=24;
	public static int TFM_IAB=25;
	public static int TFM_ILB=26;
	public static int TFM_DIVB=27;
	public static int TFM_REB=28;
	public static int TFM_WG=29;
	public static int TFM_DOLEG=30;
	public static int TFM_TG=31;
	public static int TFM_IBG=32;
	public static int TFM_FCBG=33;
	public static int TFM_IBCB=34;
	public static int TFM_IACB=35;
	public static int TFM_FCB=36;
	public static long TFM_DINVENTCF = 37;
	public static long TFM_DINVENTKF = 38;
	public static int TFM_SIZE=39;
	
	
	

}
