package com.idc.esp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.idc.esp.data.AccountDataInfo;
import com.idc.esp.data.AccountDataItemInfo;
import com.idc.esp.utils.UtilHelper;
import com.idc.utils.JVString;

public class AccountsReader {
	private File m_dataFile;

	public AccountsReader (File m_dataFile) {
		this.m_dataFile = m_dataFile;
	}

	public AccountDataInfo readFile() {
		System.out.println (">>> AccountsReader::readFile; file " + m_dataFile.getPath());
		AccountDataInfo accountDataInfo = new AccountDataInfo();
		BufferedReader br = null;
		int count = 0;
		try {
			br = new BufferedReader (new FileReader (m_dataFile));
			String line;
			while ((line = br.readLine ()) != null) {
				count++;
				if (count < 2) continue;
				String[] parts = line.split (",");
				if (parts.length != 7) continue;
				AccountDataItemInfo accountDataItemInfo = new AccountDataItemInfo (UtilHelper.parseLong (parts[0]), 
						JVString.replace (parts[1], "\"", ""),
						JVString.replace (parts[2], "\"", ""),
						JVString.replace (parts[3], "\"", ""),
						UtilHelper.parseInt (parts[4]), UtilHelper.parseInt (parts[5]), UtilHelper.parseLong (parts[6]));
				accountDataInfo.add (accountDataItemInfo);
			}
		}
		catch (Exception ex) {
			System.out.println ("Exception; reason "+ex.getMessage());
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (Exception ex) {
					System.out.println ("Unable to close " + m_dataFile.getPath());
				}
			}
		}
//		System.out.println("accountDataInfo :"+accountDataInfo+":");
		System.out.println("<<< AccountsReader::readFile; read records "+count+" AccountDataInfo records "+accountDataInfo.getSize());
		return accountDataInfo;
	}
}
