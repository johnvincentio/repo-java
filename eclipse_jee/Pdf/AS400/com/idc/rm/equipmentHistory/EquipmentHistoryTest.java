package com.idc.rm.equipmentHistory;


/*
 * do rentalHistory.jsp first; use EquipmentHistoryBean.
 */

public class EquipmentHistoryTest {
	public static void main (String[] args) {
		try {
			long start = System.currentTimeMillis();
			(new EquipmentHistoryTest()).doTest1();
			System.out.println("test total time : " + (System.currentTimeMillis() - start));
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}
	private void doTest1() throws Exception {
		System.out.println(">>> D1:doTest1 - 1");
		EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();
		String selectYear = "2007";

		String list_customer = "2821732";
		String accountNumber = "2821732";
		String accountName = "Padme";	// account name from RM
		equipmentHistoryInfo = doAccount (equipmentHistoryInfo, list_customer, accountNumber, 
				accountName, selectYear);

		list_customer = "8513422";
		accountNumber = "8513422";
		accountName = "SHADY GRADY CONSTRUCTION CO";	// account name from RM
		equipmentHistoryInfo = doAccount (equipmentHistoryInfo, list_customer, accountNumber, 
				accountName, selectYear);

		StringBuffer buf = equipmentHistoryInfo.getCSV (true);
		System.out.println("CSV: \n"+buf.toString());
		System.out.println("<<< D1:doTest1 - 1");
	}

	private EquipmentHistoryInfo doAccount (EquipmentHistoryInfo equipmentHistoryInfo,
			String list_customer, String accountNumber, 
			String accountName, String selectYear) throws Exception {
		System.out.println(">>> doAccount");
		EquipmentHistoryBean equipmentHistoryBean = new EquipmentHistoryBean();
		System.out.println("Connection to DB");
		equipmentHistoryBean.makeConnection();

		String str_company = "HG";
		String jobnumber = "";
		String str_datalib = "RMHCQDATA";
		String str_OrderBy = "";

		if (equipmentHistoryBean.getRows (list_customer, 
				Integer.valueOf(accountNumber).intValue(), 
				str_company, 
				str_datalib, 0, jobnumber, str_OrderBy, selectYear)) {
			while (equipmentHistoryBean.getNext()) {
				equipmentHistoryInfo.add (new EquipmentHistoryItemInfo (
						equipmentHistoryBean, accountNumber, accountName, selectYear));
			}
		}

		equipmentHistoryBean.cleanup();
		equipmentHistoryBean.endConnection();
		System.out.println("<<< doAccount");
		return equipmentHistoryInfo;
	}
}
