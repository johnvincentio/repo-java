package com.idc.esp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.idc.esp.data.MemberProfileInfo;
import com.idc.esp.data.MemberProfileItemInfo;

public class LDAPReader {
	private File m_dataFile;
	public LDAPReader (File dataFile) {this.m_dataFile = dataFile;}

	public MemberProfileInfo readFile() {
		System.out.println(">>> LDAPReader::readFile; file "+m_dataFile.getPath());
		MemberProfileInfo profileInfo = new MemberProfileInfo();
		MemberProfileItemInfo profileItemInfo = null;

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		String line = null;
		int line_count = 0;
		try {
			fileReader = new FileReader (m_dataFile);
			bufferedReader = new BufferedReader (fileReader);		

			for (line_count = 0; ; line_count++) {
				line = bufferedReader.readLine();
				if (line == null) {
					profileInfo.add (profileItemInfo);
					break;
				}
				if (line.trim().length() < 1) continue;

				String str1 = line.toLowerCase();
				if (str1.startsWith("dn:")) {
					if (profileItemInfo != null) {
						profileInfo.add (profileItemInfo);
					}
					profileItemInfo = new MemberProfileItemInfo();
					continue;
				}

				String[] parts = line.split(": ");
				if (parts.length != 2) continue;
				if (parts[0].equals("objectclass")) continue;
				if (parts[0].equals("hertzpassword")) continue;
				profileItemInfo.setAttribute (parts[0], parts[1]);
			}
		}
		catch (Exception e) {
			System.out.println("Exception in LDAPReader::readFile; "+e.getMessage()+" line count "+line_count);
			e.printStackTrace();
		}
		finally {
			line = null;
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}			
			}
		}
//		System.out.println("profileInfo :"+profileInfo+":");
		System.out.println("<<< LDAPReader::readFile; ldap records "+line_count+" MemberProfile records "+profileInfo.getSize());
		return profileInfo;
	}
}
		