package com.idc.parse;

import java.util.HashMap;
import java.util.Map;

public class JdbcItemInfo {
	private Map map = new HashMap();
	public void set (String key, String value) {
		map.put(key, value);
	}
}
