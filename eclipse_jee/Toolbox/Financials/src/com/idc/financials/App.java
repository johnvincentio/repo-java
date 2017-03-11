package com.idc.financials;

public class App {
	public static void main (String[] args) {
		(new App()).doTest4();
	}
	private void doTest1() {
		double total_funds = 248292.67;
		System.out.println ("Total funds available: "+total_funds);

		FundInfo fundInfo = new FundInfo (total_funds);
		fundInfo.add (new FundItemInfo("SPY", 6.3f));
		fundInfo.add (new FundItemInfo("COFRX", 1.6f));
		fundInfo.add (new FundItemInfo("OAKMX", 1.2f));
		
		fundInfo.add (new FundItemInfo("IWF", 7.3f));
		fundInfo.add (new FundItemInfo("FDGRX", 2.2f));
		fundInfo.add (new FundItemInfo("FTQGX", 1.6f));
		fundInfo.add (new FundItemInfo("CGTRX", 1.4f));

		fundInfo.add (new FundItemInfo("IWD", 7.4f));
		fundInfo.add (new FundItemInfo("JVLTX", 1.2f));
		fundInfo.add (new FundItemInfo("PAVLX", 1.2f));

		fundInfo.add (new FundItemInfo("DMCVX", 1.5f));

		fundInfo.add (new FundItemInfo("TEGIX", 1.7f));
		fundInfo.add (new FundItemInfo("TMDPX", 1.7f));

		fundInfo.add (new FundItemInfo("FSLSX", 3.5f));
		fundInfo.add (new FundItemInfo("FLPSX", 1.5f));

		fundInfo.add (new FundItemInfo("GSSIX", 1.5f));

		fundInfo.add (new FundItemInfo("CIPSX", 1.5f));
		fundInfo.add (new FundItemInfo("RSYEX", 1.5f));

		fundInfo.add (new FundItemInfo("PAREX", 1.0f));
		fundInfo.add (new FundItemInfo("IGLAX", 1.0f));

		fundInfo.add (new FundItemInfo("XLF", 3.9f));

		fundInfo.add (new FundItemInfo("TROSX", 3.0f));			// foreign stock
		fundInfo.add (new FundItemInfo("OIGAX", 2.9f));
		fundInfo.add (new FundItemInfo("FIADX", 2.6f));
		fundInfo.add (new FundItemInfo("MGIAX", 2.6f));
		fundInfo.add (new FundItemInfo("TISVX", 1.9f));
		fundInfo.add (new FundItemInfo("HRINX", 1.7f));
		fundInfo.add (new FundItemInfo("OAKIX", 1.7f));
		fundInfo.add (new FundItemInfo("PRITX", 1.4f));
		fundInfo.add (new FundItemInfo("EXWAX", 1.3f));
		fundInfo.add (new FundItemInfo("MFMIX", 1.2f));
		fundInfo.add (new FundItemInfo("FEMKX", 1.1f));
		fundInfo.add (new FundItemInfo("AEMGX", 1.1f));

		fundInfo.add (new FundItemInfo("MDNLX", 7.4f));			// bonds
		fundInfo.add (new FundItemInfo("FKTIX", 6.0f));
		fundInfo.add (new FundItemInfo("NMBAX", 4.4f));
		fundInfo.add (new FundItemInfo("TPINX", 3.0f));
		fundInfo.add (new FundItemInfo("SMAIX", 2.5f));
		fundInfo.add (new FundItemInfo("FCSZX", 2.0f));

		fundInfo.add (new FundItemInfo("FDRXX", 0.5f, 1.0));			// short term
		
		double pc = fundInfo.getTotalPercent();
		System.out.println ("Total %age allocated "+pc);

		System.out.println ("Handle prices");
		fundInfo.handlePrices();

		System.out.println ("Calculate # of shares");
		fundInfo.calculateShares();

		double totalCost = fundInfo.calculateCost();
		System.out.println ("totalCost "+totalCost);

		System.out.println ("fundInfo "+fundInfo);
	}

	private void doTest2() {
		double total_funds = 46558;
		System.out.println ("Total funds available: "+total_funds);

		FundInfo fundInfo = new FundInfo (total_funds);
		fundInfo.add (new FundItemInfo("FUSPX", 13.3f));			// domestic stock funds
		fundInfo.add (new FundItemInfo("FDSSX", 6.1f));
		fundInfo.add (new FundItemInfo("FGRTX", 5.9f));
		fundInfo.add (new FundItemInfo("FLPSX", 4.9f));
		fundInfo.add (new FundItemInfo("FEQIX", 4.4f));
		fundInfo.add (new FundItemInfo("FDGRX", 4.3f));
		fundInfo.add (new FundItemInfo("FBGRX", 4.2f));
		fundInfo.add (new FundItemInfo("FINSX", 3.8f));
		fundInfo.add (new FundItemInfo("FDSCX", 1.5f));
		fundInfo.add (new FundItemInfo("FDVLX", 1.0f));

		fundInfo.add (new FundItemInfo("FUSIX", 23.4f));		// foreign stock funds
		fundInfo.add (new FundItemInfo("FEMKX", 1.5f));

		fundInfo.add (new FundItemInfo("FBNDX", 7.4f));			// bond funds
		fundInfo.add (new FundItemInfo("FTBFX", 6.0f));
		fundInfo.add (new FundItemInfo("FSHBX", 3.1f));
		fundInfo.add (new FundItemInfo("FCNVX", 2.1f));
		fundInfo.add (new FundItemInfo("FSRIX", 1.2f));
		fundInfo.add (new FundItemInfo("FHIFX", 1.0f));
		fundInfo.add (new FundItemInfo("FHIGX", 1.0f));
		fundInfo.add (new FundItemInfo("FHNIX", 1.0f));
		fundInfo.add (new FundItemInfo("FAGIX", 1.0f));
		fundInfo.add (new FundItemInfo("FFRHX", 0.9f));

		fundInfo.add (new FundItemInfo("FNSXX", 1.0f, 1.0));			// short term funds
		
		double pc = fundInfo.getTotalPercent();
		System.out.println ("Total %age allocated "+pc);

		System.out.println ("Handle prices");
		fundInfo.handlePrices();

		System.out.println ("Calculate # of shares");
		fundInfo.calculateShares();

		double totalCost = fundInfo.calculateCost();
		System.out.println ("totalCost "+totalCost);

		System.out.println ("fundInfo "+fundInfo);
	}

	private void doTest3() {
		double total_funds = 208662;		// ROLLOVER IRA
		System.out.println ("Total funds available: "+total_funds);

		FundInfo fundInfo = new FundInfo (total_funds);
		fundInfo.add (new FundItemInfo("FUSPX", 13.3f));			// domestic stock funds 49.4%
		fundInfo.add (new FundItemInfo("FDSSX", 6.1f));
		fundInfo.add (new FundItemInfo("FGRTX", 5.9f));
		fundInfo.add (new FundItemInfo("FLPSX", 4.9f));
		fundInfo.add (new FundItemInfo("FEQIX", 4.4f));
		fundInfo.add (new FundItemInfo("FDGRX", 4.3f));
		fundInfo.add (new FundItemInfo("FBGRX", 4.2f));
		fundInfo.add (new FundItemInfo("FINSX", 3.8f));
		fundInfo.add (new FundItemInfo("FDSCX", 1.5f));
		fundInfo.add (new FundItemInfo("FDVLX", 1.0f));

		fundInfo.add (new FundItemInfo("FUSIX", 23.4f));		// foreign stock funds 24.9%
		fundInfo.add (new FundItemInfo("FEMKX", 1.5f));

		fundInfo.add (new FundItemInfo("FBNDX", 7.4f));			// bond funds 24.7%
		fundInfo.add (new FundItemInfo("FTBFX", 6.0f));
		fundInfo.add (new FundItemInfo("FSHBX", 3.1f));
		fundInfo.add (new FundItemInfo("FCNVX", 2.1f));
		fundInfo.add (new FundItemInfo("FSRIX", 1.2f));
		fundInfo.add (new FundItemInfo("FHIFX", 1.0f));
		fundInfo.add (new FundItemInfo("FHIGX", 1.0f));
		fundInfo.add (new FundItemInfo("FHNIX", 1.0f));
		fundInfo.add (new FundItemInfo("FAGIX", 1.0f));
		fundInfo.add (new FundItemInfo("FFRHX", 0.9f));

		fundInfo.add (new FundItemInfo("FNSXX", 1.0f, 1.0));			// short term funds 1%
		
		double pc = fundInfo.getTotalPercent();
		System.out.println ("Total %age allocated "+pc);

		System.out.println ("Handle prices");
		fundInfo.handlePrices();

		System.out.println ("Calculate # of shares");
		fundInfo.calculateShares();

		double totalCost = fundInfo.calculateCost();
		System.out.println ("totalCost "+totalCost);

		System.out.println ("fundInfo "+fundInfo);
	}

	private void doTest4() {
		double total_funds = 225641.83;		// 
		System.out.println ("Total funds available: "+total_funds);

		FundInfo fundInfo = new FundInfo (total_funds);
		fundInfo.add (new FundItemInfo("IEFA", 42871.95));
		fundInfo.add (new FundItemInfo("MUB", 40615.53));
		fundInfo.add (new FundItemInfo("IVW", 27077.02));
		fundInfo.add (new FundItemInfo("IVV", 22564.18));
		fundInfo.add (new FundItemInfo("IVE", 22564.18));
		fundInfo.add (new FundItemInfo("FNCL", 9025.67));
		fundInfo.add (new FundItemInfo("IJH", 9025.67));
		fundInfo.add (new FundItemInfo("IEMG", 6769.25));
		fundInfo.add (new FundItemInfo("IJK", 6769.25));
		fundInfo.add (new FundItemInfo("IJJ", 6769.25));
		fundInfo.add (new FundItemInfo("SUB", 6769.25));
		fundInfo.add (new FundItemInfo("DHGAX", 6769.25));
		fundInfo.add (new FundItemInfo("FCVSX", 4512.84));
		fundInfo.add (new FundItemInfo("IJR", 4512.84));
		fundInfo.add (new FundItemInfo("IJT", 4512.85));
		fundInfo.add (new FundItemInfo("IYR", 4512.85));

		double totalCost = fundInfo.getTotalCost();
		System.out.println ("totalCost "+totalCost);

		System.out.println ("Handle prices");
		fundInfo.handlePrices();

		System.out.println ("Calculate # of shares");
		fundInfo.calculateShares();

		double pc = fundInfo.getTotalPercent();
		System.out.println ("Total %age allocated "+pc);

		System.out.println ("fundInfo "+fundInfo);
	}
}
