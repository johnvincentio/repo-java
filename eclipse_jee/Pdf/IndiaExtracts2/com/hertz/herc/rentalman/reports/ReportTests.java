package com.hertz.herc.rentalman.reports;

import junit.framework.TestCase;

import com.hertz.herc.presentation.util.framework.RentalManDataExtractHelper;
import com.hertz.hercutil.company.RMAccountInfo;
import com.hertz.hercutil.company.RMAccountType;
import com.hertz.hercutil.company.RMAccountsInfo;
import com.hertz.hercutil.company.RMNarpAccountInfo;
import com.hertz.hercutil.rentalman.reports.CSVTemplateHelper;
import com.hertz.hercutil.rentalman.reports.equipmentDetailHistory.EquipmentDetailHistoryInfo;
import com.hertz.hercutil.rentalman.reports.equipmentHistory.EquipmentHistoryInfo;
import com.hertz.irac.framework.HertzSystemException;

public class ReportTests extends TestCase {

	public void testPhase1a() {
		RMAccountType rmAccountType = makeNarpData ();
		String selectedYear = "2007";
		try {
			EquipmentHistoryInfo equipmentHistoryInfo = RentalManDataExtractHelper.doReportEquipmentHistory (rmAccountType, selectedYear);
			assertNotNull (equipmentHistoryInfo);
			StringBuffer buf = CSVTemplateHelper.getEquipmentHistory (equipmentHistoryInfo);
			assertNotNull (buf);
		}
		catch (HertzSystemException hex) {
			fail("exception "+hex.getMessage());
		}
	}

	public void testPhase2a() {
		RMAccountType rmAccountType = makeNarpData ();
		String selectedYear = "2007";
		String category = "";
		String classification = "";
		
		try {
			EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = RentalManDataExtractHelper.doEquipmentDetailHistoryInfo (
					rmAccountType, category, classification, selectedYear);
			assertNotNull (equipmentDetailHistoryInfo);
			StringBuffer buf = CSVTemplateHelper.getEquipmentDetailHistory (equipmentDetailHistoryInfo);
			assertNotNull (buf);
			System.out.println(buf.toString());
		}
		catch (HertzSystemException hex) {
			fail("exception "+hex.getMessage());
		}
	}

	public void testPhase2b() {
		RMAccountType rmAccountType = makeNarpData ();
		String selectedYear = "2007";
		String category = "220";
		String classification = "0050";
		
		try {
			EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = RentalManDataExtractHelper.doEquipmentDetailHistoryInfo (
					rmAccountType, category, classification, selectedYear);
			assertNotNull (equipmentDetailHistoryInfo);
			StringBuffer buf = CSVTemplateHelper.getEquipmentDetailHistory (equipmentDetailHistoryInfo);
			assertNotNull (buf);
			System.out.println(buf.toString());
		}
		catch (HertzSystemException hex) {
			fail("exception "+hex.getMessage());
		}
	}

	public void testPhase2c() {
		RMAccountType rmAccountType = makeNarpData ();
		String selectedYear = "2007";
		String category = "220";		// zero records
		String classification = "0060";
		
		try {
			EquipmentDetailHistoryInfo equipmentDetailHistoryInfo = RentalManDataExtractHelper.doEquipmentDetailHistoryInfo (
					rmAccountType, category, classification, selectedYear);
			assertNotNull (equipmentDetailHistoryInfo);
			StringBuffer buf = CSVTemplateHelper.getEquipmentDetailHistory (equipmentDetailHistoryInfo);
			assertNotNull (buf);
			System.out.println(buf.toString());
		}
		catch (HertzSystemException hex) {
			fail("exception "+hex.getMessage());
		}
	}

	private static RMAccountType makeNarpData () {
		RMAccountInfo rmAccountInfo = Utils.makeRMAccountInfo ("4312", 1, "JV Inc", "12 abc ave", "", "Flint", "MI", "03423");
		RMAccountInfo corpLinkRMAccountInfo = Utils.makeRMAccountInfo ("9999", 1, "Corp Inc", "12 abc ave", "", "Flint", "MI", "03423");
		RMAccountsInfo rmAccountsInfo = new RMAccountsInfo();
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("140276", 1, "JV Inc", "12 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1051245", 1, "JV Inc", "45 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1057733", 1, "JV Inc", "200 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1062364", 1, "JV Inc", "21 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1069213", 1, "JV Inc", "4566 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1071383", 1, "JV Inc", "65 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1739382", 1, "JV Inc", "643 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("1744444", 1, "JV Inc", "77 abc ave", "", "Flint", "MI", "03423"));
		rmAccountsInfo.add (Utils.makeRMAccountInfo ("2619540", 1, "JV Inc", "42376 abc ave", "", "Flint", "MI", "03423"));
		RMNarpAccountInfo rmNarpAccountInfo = new RMNarpAccountInfo (rmAccountInfo, 
				corpLinkRMAccountInfo, rmAccountsInfo);
		return rmNarpAccountInfo;
	}

	private static RMAccountType makeData () {
		RMAccountInfo rmAccountInfo = Utils.makeRMAccountInfo ("2821732", 1, "Ob Inc", "12 def ave", "", "Washington", "DC", "00000");
		return rmAccountInfo;
	}
}
