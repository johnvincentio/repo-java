package com.idc.rm.equipmentOnRent;

import java.util.Calendar;

/*
 * equipOnRent.jsp; use eqdetailhistoryBean, renamed to EquipmentDetailHistoryBean. Also uses PendingTransactionsBean.
 */

public class EquipmentOnRentTest {
	public static void main (String[] args) {
		try {
			long start = System.currentTimeMillis();
			(new EquipmentOnRentTest()).doTest1();
			System.out.println("test total time : " + (System.currentTimeMillis() - start));
		}
		catch (Exception ex) {
			System.out.println("Exception; "+ex.getMessage());
		}
	}
	private void doTest1() throws Exception {
		System.out.println(">>> doTest1 - 1");

		boolean bEquipmentOnRent = true;		// Overdue = false;

		Calendar Today = Calendar.getInstance();
		int todayDate = Today.get(Calendar.YEAR)*10000 + (Today.get(Calendar.MONTH)+1)*100 + Today.get(Calendar.DAY_OF_MONTH);

		int countryCode = 1;		// US
		EquipmentOnRentInfo equipmentOnRentInfo = new EquipmentOnRentInfo (countryCode);

		boolean bEquipmentChangeAuthorization = true;
		boolean bAllowReleases = true;
		boolean bAllowExtend = true;

		String list_customer = "2628275";
		String accountNumber = "2628275";
		String accountName = "xxxxxxxxxx";	// account name from RM
		equipmentOnRentInfo = doAccount (bEquipmentOnRent, bEquipmentChangeAuthorization, bAllowReleases, bAllowExtend, todayDate, equipmentOnRentInfo, list_customer, accountNumber, accountName, countryCode);

		list_customer = "804716";
		accountNumber = "804716";
		accountName = "yyyyyyyyyyyyyyyyyy";	// account name from RM
		equipmentOnRentInfo = doAccount (bEquipmentOnRent, bEquipmentChangeAuthorization, bAllowReleases, bAllowExtend, todayDate, equipmentOnRentInfo, list_customer, accountNumber, accountName, countryCode);

		StringBuffer buf = equipmentOnRentInfo.getCSV (true);
		System.out.println("CSV: \n"+buf.toString());
		System.out.println("<<< doTest1 - 1");
	}

	private EquipmentOnRentInfo doAccount (boolean bEquipmentOnRent, 
			boolean bEquipmentChangeAuthorization, boolean bAllowReleases, boolean bAllowExtend, 
			int todayDate, EquipmentOnRentInfo equipmentOnRentInfo,
			String list_customer, String accountNumber, String accountName, int countryCode) throws Exception {
		System.out.println(">>> doAccount");

		String jobnumber = "";
		String str_OrderBy = "";

		PendingTransactionsBean pendingTransactionsBean = new PendingTransactionsBean();
		pendingTransactionsBean.makeConnection();
		PendingTransactionsInfo pendingTransactionsInfo = new PendingTransactionsInfo();
		if (bEquipmentChangeAuthorization && (bAllowReleases || bAllowExtend)) {
			if (countryCode == 1) {
				if (pendingTransactionsBean.getPendingTrans(accountNumber, countryCode)) {
					while (pendingTransactionsBean.getNext()) {
						pendingTransactionsInfo.add (pendingTransactionsBean);
					}
				}
			}
		}
		pendingTransactionsBean.cleanup();
		pendingTransactionsBean.endConnection();

		EquipmentOnRentBean equipmentOnRentBean = new EquipmentOnRentBean();
		equipmentOnRentBean.makeConnection();

		EquipmentOnRentPickupInfo equipmentOnRentPickupInfo = new EquipmentOnRentPickupInfo();
		if (equipmentOnRentBean.getPickup (list_customer, accountNumber, countryCode)) {
			while (equipmentOnRentBean.getNext3()) {
				equipmentOnRentPickupInfo.add (new EquipmentOnRentPickupItemInfo(equipmentOnRentBean));
			}
		}

		if (equipmentOnRentBean.getRows (list_customer, countryCode, jobnumber, str_OrderBy)) {
			while (equipmentOnRentBean.getNext()) {
				EquipmentOnRentItemInfo equipmentOnRentItemInfo = 
					new EquipmentOnRentItemInfo (countryCode, accountNumber, accountName, todayDate, 
							equipmentOnRentBean, equipmentOnRentPickupInfo, pendingTransactionsInfo);
				if (equipmentOnRentItemInfo.isReRentItem()) {
					equipmentOnRentItemInfo.setItemComments (equipmentOnRentBean.getItemComments (countryCode, 
							equipmentOnRentItemInfo.getContractNumber(), equipmentOnRentItemInfo.getDetailSequence()));
				}
				if (bEquipmentOnRent || equipmentOnRentItemInfo.isOverdueContract())
					equipmentOnRentInfo.add (equipmentOnRentItemInfo);
			}
		}
		equipmentOnRentBean.cleanup();
		equipmentOnRentBean.endConnection();
		System.out.println("<<< doAccount");
		return equipmentOnRentInfo;
	}
}
