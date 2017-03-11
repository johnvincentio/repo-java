package com.idc.esp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.idc.esp.data.MemberDataInfo;
import com.idc.esp.data.MemberDataItemInfo;
import com.idc.esp.utils.UtilHelper;
import com.idc.utils.JVString;

public class MembersReader {
	private File m_dataFile;

	public MembersReader (File m_dataFile) {
		this.m_dataFile = m_dataFile;
	}

	public MemberDataInfo readFile() {
		System.out.println (">>> MembersReader::readFile; file " + m_dataFile.getPath());
		MemberDataInfo memberDataInfo = new MemberDataInfo();
		BufferedReader br = null;
		int count = 0;
		try {
			br = new BufferedReader (new FileReader (m_dataFile));
			String line;
			while ((line = br.readLine ()) != null) {
				count++;
				if (count < 2) continue;
				String[] parts = line.split (",");
				if (parts.length != 3) continue;
				String username = JVString.replace (parts[0], "\"", "");
				MemberDataItemInfo memberDataItemInfo = new MemberDataItemInfo (username, UtilHelper.parseLong (parts[1]), UtilHelper.parseLong (parts[2]));
				memberDataInfo.add (memberDataItemInfo);
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
//		System.out.println("memberDataInfo :"+memberDataInfo+":");
		System.out.println("<<< MembersReader::readFile; read records "+count+" MemberDataInfo records "+memberDataInfo.getSize());
		return memberDataInfo;
	}
}
