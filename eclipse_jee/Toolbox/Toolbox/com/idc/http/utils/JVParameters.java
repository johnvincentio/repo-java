package com.idc.http.utils;

import java.util.*;

public class JVParameters {
	private Map<String, String[]> m_map;

	public JVParameters (Map<String, String[]> pMap) {
		m_map = pMap;
	}

	public boolean isValidKey (String strKey) {
		System.out.println(">>> isValidKey :" + strKey + ":");
		Iterator<Map.Entry<String, String[]>> iter = m_map.entrySet().iterator();
		String compKey = strKey.toUpperCase();
		while (iter.hasNext()) {
			Map.Entry<String, String[]> me = (Map.Entry<String, String[]>) iter.next();
			String keyName = (String) me.getKey();
			if (compKey.equals(keyName.toUpperCase()))
				return true;
		}
		return false;
	}

	public String getKeyValue (String strKey, String strDef) {
		System.out.println(">>> getKeyValue :" + strKey + ":");
		String strReturn = strDef;
		Iterator<Map.Entry<String, String[]>> iter = m_map.entrySet().iterator();
		String compKey = strKey.toUpperCase();
		while (iter.hasNext()) {
			Map.Entry<String, String[]> me = (Map.Entry<String, String[]>) iter.next();
			String keyName = (String) me.getKey();
			if (compKey.equals (keyName.toUpperCase())) {
				String vals[] = (String[]) me.getValue();
				strReturn = vals[0];
				break;
			}
		}
		System.out.println("<<< getKeyValue :" + strReturn + ":");
		return strReturn;
	}

	public int getKeyValue (String strKey, int nDef) {
		String strDef = (new Integer(nDef)).toString();
		String strTmp = getKeyValue(strKey, strDef);
		int nReturn = (new Integer(strTmp)).intValue();
		return nReturn;
	}
}
