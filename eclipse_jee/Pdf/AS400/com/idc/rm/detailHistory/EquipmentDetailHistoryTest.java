package com.idc.rm.detailHistory;

/*
 * do detailHistory.jsp; use eqdetailhistoryBean, renamed to EquipmentDetailHistoryBean.
 */

public class EquipmentDetailHistoryTest {
	public static void main (String[] args) {
		try {
			long start = System.currentTimeMillis();
			(new EquipmentDetailHistoryTest()).doTest1();
			System.out.println("test total time : " + (System.currentTimeMillis() - start));
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}
	private void doTest1() throws Exception {
		System.out.println(">>> doTest1 - 1");
		EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = new EquipmentDetailHistoryInfo();

		String selectYear = "2007";
		String category = "975";
		String classification = "0001";

		String list_customer = "2628275";
		String accountNumber = "2628275";
		String accountName = "xxxxxxxxxx";	// account name from RM
		equipmentDetailHistoryInfo = doAccount (equipmentDetailHistoryInfo, list_customer, accountNumber, 
				accountName, category, classification, selectYear);

		list_customer = "804716";
		accountNumber = "804716";
		accountName = "yyyyyyyyyyyyyyyyyy";	// account name from RM
		equipmentDetailHistoryInfo = doAccount (equipmentDetailHistoryInfo, list_customer, accountNumber, 
				accountName, category, classification, selectYear);

		StringBuffer buf = equipmentDetailHistoryInfo.getCSV (true);
		System.out.println("CSV: \n"+buf.toString());
		System.out.println("<<< doTest1 - 1");
	}

	private EquipmentDetailHistoryInfo doAccount (EquipmentDetailHistoryInfo equipmentDetailHistoryInfo,
			String list_customer, String accountNumber, String accountName,
			String category, String classification, String selectedYear) throws Exception {
		System.out.println(">>> doAccount");
		EquipmentDetailHistoryBean equipmentDetailHistoryBean = new EquipmentDetailHistoryBean();
		System.out.println("Connection to DB");
		equipmentDetailHistoryBean.makeConnection();

		String str_company = "HG";
		String jobnumber = "";
		String str_datalib = "RMHCQDATA";
		String str_OrderBy = "";

		EquipmentDetailHistoryItemInfo equipmentDetailHistoryItemInfo;
		if (equipmentDetailHistoryBean.getRows (list_customer, str_company, str_datalib, category, classification, 
							jobnumber, str_OrderBy, selectedYear)) {
			while (equipmentDetailHistoryBean.getNext()) {
				equipmentDetailHistoryItemInfo = new EquipmentDetailHistoryItemInfo (equipmentDetailHistoryBean, 
						accountNumber, accountName, category, classification, selectedYear);

				if (str_company.equals("HG") && equipmentDetailHistoryItemInfo.isReRentItem()) {
					String str_contract = equipmentDetailHistoryItemInfo.getContractNumber();
					String str_dseq = equipmentDetailHistoryItemInfo.getDetailSequence();
					String str_sequence = equipmentDetailHistoryItemInfo.getSequence();

					String str_itemcomments = equipmentDetailHistoryBean.getItemComments (str_company, str_datalib, 
							str_contract, str_sequence, str_dseq);
					equipmentDetailHistoryItemInfo.setItemComments (str_itemcomments);
				}
				equipmentDetailHistoryInfo.add (equipmentDetailHistoryItemInfo);
			}
		}

		equipmentDetailHistoryBean.cleanup();
		equipmentDetailHistoryBean.endConnection();
		System.out.println("<<< doAccount");
		return equipmentDetailHistoryInfo;
	}
}
