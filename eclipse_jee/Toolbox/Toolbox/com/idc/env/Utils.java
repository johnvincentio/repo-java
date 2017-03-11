package com.idc.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idc.trace.LogHelper;

public class Utils {

	public static void showEnv (String msg) {
		Map<String, String> map = new HashMap<String, String> (System.getenv());
		showSortedMap (msg, map);
	}

	public static void showSortedMap (String msg, Map<String, String> map) {
		List<MapItemInfo> list = makeList (map);
		LogHelper.debug ("*** ("+msg+") Start Sorted here ***");
		for (Iterator<MapItemInfo> iter = list.iterator(); iter.hasNext(); ) {
			MapItemInfo item = (MapItemInfo) iter.next();
			LogHelper.debug (item.getKey() + "=" + item.getValue());
		}
		LogHelper.debug ("*** End Sorted here ***");
	}

	public static List<MapItemInfo> makeList (Map<String, String> map) {
		List<MapItemInfo> list = new ArrayList<MapItemInfo>();
		Iterator<Map.Entry<String, String>> keyValuePairs = map.entrySet().iterator();
		for (int i = 0; i < map.size(); i++) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) keyValuePairs.next();
			list.add (new MapItemInfo ((String) entry.getKey(), (String) entry.getValue()));
		}
		Collections.sort (list);
		return list;
	}
}
