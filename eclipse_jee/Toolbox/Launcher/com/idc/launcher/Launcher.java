package com.idc.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idc.file.exec.ExecuteCommand;
import com.idc.file.exec.PrintLine;
import com.idc.trace.LogHelper;
import com.idc.xml.smart.AttributeInfo;
import com.idc.xml.smart.AttributeItemInfo;
import com.idc.xml.smart.JVxml;
import com.idc.xml.smart.NodeInfo;
import com.idc.xml.smart.NodeItemInfo;

public class Launcher {
	public static void main(String[] args) {
		LogHelper.info (">>> Launcher::main");
		(new Launcher()).doApp (args);
		LogHelper.info ("<<< Launcher::main");
	}

	private void doApp (String[] args) {
		if (args.length < 2) {
			LogHelper.fatal ("Launcher Usage; Launcher scenario launcher_file");
			return;
		}

		File launcherFile = new File (args[1]);
		if (! launcherFile.isFile()) {
			LogHelper.fatal ("Launcher file :"+args[1]+": not found");
			return;
		}

		ScenarioItemInfo scenarioItemInfo = makeXml (launcherFile, args[0]);
		if (scenarioItemInfo == null) {
			LogHelper.fatal ("Launcher scenario :"+args[0]+": not found");
			return;
		}
		LogHelper.debug ("scenarioItemInfo "+scenarioItemInfo);

		ParamInfo paramInfo = scenarioItemInfo.getParamInfo();

		String[] strCmd = new String[2 + paramInfo.getSize()];
		int pos = 0;
		strCmd[pos++] = scenarioItemInfo.getApplication();
		strCmd[pos++] = scenarioItemInfo.getMain();
		for (Iterator<ParamItemInfo> iter = paramInfo.getItems(); iter.hasNext(); ) {
			ParamItemInfo paramItemInfo = (ParamItemInfo) iter.next();
			strCmd[pos++] = paramItemInfo.getName();
		}

		Map<String, String> map = new HashMap<String, String> (System.getenv());
		map.remove ("CLASSPATH");
		map.remove ("PATH");
		map.remove ("Path");
		map.put ("CLASSPATH", scenarioItemInfo.getClasspath());
		map.put ("Path", scenarioItemInfo.getPath());

		EnvInfo envInfo = scenarioItemInfo.getEnvInfo();
		for (Iterator<EnvItemInfo> iter = envInfo.getItems(); iter.hasNext(); ) {
			EnvItemInfo envItemInfo = (EnvItemInfo) iter.next();
			map.put (envItemInfo.getName(), envItemInfo.getValue());
		}
		String[] envp = getMap (map);

		showSortedMap (map);

		File cwd = new File (scenarioItemInfo.getCwd());		// cwd must exist

		LogHelper.info ("scenarioItemInfo "+scenarioItemInfo);	//TODO; remove this

		ExecuteCommand executeCommand = new ExecuteCommand();
		executeCommand.executeCommandDetach (strCmd, envp, cwd, new PrintLine());

//		ExecuteCommandThread executeCommandThread = new ExecuteCommandThread (strCmd, envp, cwd, new PrintLine());
//		executeCommandThread.start();
	}

	private ScenarioItemInfo makeXml (File launcherFile, String scenario) {
		LogHelper.info (">>> Launcher::makeXml; launcher file :"+launcherFile.getPath()+" scenario "+scenario);
		ScenarioInfo scenarioInfo  = new ScenarioInfo();
		NodeItemInfo nodeItemInfo = JVxml.loadXML (launcherFile);
		LogHelper.debug (nodeItemInfo.toString());
		NodeInfo nodeInfo = nodeItemInfo.getNodeInfo();
		for (Iterator<NodeItemInfo> iter = nodeInfo.getItems(); iter.hasNext(); ) {
			NodeItemInfo nodeItemInfo2 = (NodeItemInfo) iter.next();
			AttributeInfo attribInfo = nodeItemInfo2.getAttributeInfo();
			LogHelper.debug ("attribInfo "+attribInfo);
			AttributeItemInfo attributeItemInfo = attribInfo.getAttributeItemInfo("name", scenario);
			LogHelper.debug ("attributeItemInfo "+attributeItemInfo);
			if (attributeItemInfo == null) continue;

			ParamInfo pathInfo = new ParamInfo();
			ParamInfo classPathInfo = new ParamInfo();
			ParamInfo paramInfo = new ParamInfo();
			EnvInfo envInfo = new EnvInfo();
			NodeInfo nodeInfo2 = nodeItemInfo2.getNodeInfo();
			for (Iterator<NodeItemInfo> iter2 = nodeInfo2.getItems(); iter2.hasNext(); ) {
				NodeItemInfo nodeItemInfo3 = (NodeItemInfo) iter2.next();
				LogHelper.debug ("nodeItemInfo3 "+nodeItemInfo3);	//find env and params
				if ("envs".equalsIgnoreCase(nodeItemInfo3.getName())) {
					NodeInfo nodeInfo3 = nodeItemInfo3.getNodeInfo();
					for (Iterator<NodeItemInfo> iter3 = nodeInfo3.getItems(); iter3.hasNext(); ) {
						NodeItemInfo nodeItemInfo4 = (NodeItemInfo) iter3.next();
						AttributeInfo attribInfo4 = nodeItemInfo4.getAttributeInfo();
						envInfo.add (new EnvItemInfo (
								attribInfo4.getAttributeItemInfo("name"),
								attribInfo4.getAttributeItemInfo("value")));
					}
				}
				else if ("params".equalsIgnoreCase(nodeItemInfo3.getName())) {
					NodeInfo nodeInfo3 = nodeItemInfo3.getNodeInfo();
					for (Iterator<NodeItemInfo> iter3 = nodeInfo3.getItems(); iter3.hasNext(); ) {
						NodeItemInfo nodeItemInfo4 = (NodeItemInfo) iter3.next();
						AttributeInfo attribInfo4 = nodeItemInfo4.getAttributeInfo();
						paramInfo.add (new ParamItemInfo (attribInfo4.getAttributeItemInfo("name")));
					}
				}
				else if ("path".equalsIgnoreCase(nodeItemInfo3.getName())) {
					NodeInfo nodeInfo3 = nodeItemInfo3.getNodeInfo();
					for (Iterator<NodeItemInfo> iter3 = nodeInfo3.getItems(); iter3.hasNext(); ) {
						NodeItemInfo nodeItemInfo4 = (NodeItemInfo) iter3.next();
						AttributeInfo attribInfo4 = nodeItemInfo4.getAttributeInfo();
						pathInfo.add (new ParamItemInfo (attribInfo4.getAttributeItemInfo("name")));
					}
				}
				else if ("classPath".equalsIgnoreCase(nodeItemInfo3.getName())) {
					NodeInfo nodeInfo3 = nodeItemInfo3.getNodeInfo();
					for (Iterator<NodeItemInfo> iter3 = nodeInfo3.getItems(); iter3.hasNext(); ) {
						NodeItemInfo nodeItemInfo4 = (NodeItemInfo) iter3.next();
						AttributeInfo attribInfo4 = nodeItemInfo4.getAttributeInfo();
						classPathInfo.add (new ParamItemInfo (attribInfo4.getAttributeItemInfo("name")));
					}
				}
			}

			LogHelper.debug ("attributeItemInfo "+attributeItemInfo.toString());
			scenarioInfo.add (new ScenarioItemInfo (
					attribInfo.getAttributeItemInfo("name"),
					attribInfo.getAttributeItemInfo("application"),
					attribInfo.getAttributeItemInfo("main"),
					attribInfo.getAttributeItemInfo("cwd"),
					pathInfo, classPathInfo, 
					paramInfo, envInfo));
		}
		ScenarioItemInfo scenarioItemInfo = scenarioInfo.getScenarioItemInfo(scenario);
		LogHelper.info ("<<< Launcher::makeXml");
		return scenarioItemInfo;
	}

	private String[] getMap (Map<String, String> map) {
		List<MapItemInfo> list = makeList (map);
		String[] envp = new String [list.size()];
		int count = 0;
		for (Iterator<MapItemInfo> iter = list.iterator(); iter.hasNext(); ) {
			MapItemInfo item = (MapItemInfo) iter.next();
			envp[count++] = item.getKey() + "=" + item.getValue();
		}
		return envp;
	}

	private List<MapItemInfo> makeList (Map<String, String> map) {
		List<MapItemInfo> list = new ArrayList<MapItemInfo>();
		Iterator<Map.Entry<String, String>> keyValuePairs = map.entrySet().iterator();
		for (int i = 0; i < map.size(); i++) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) keyValuePairs.next();
			list.add (new MapItemInfo ((String) entry.getKey(), (String) entry.getValue()));
		}
		Collections.sort (list);
		return list;
	}

	private void showSortedMap (Map<String, String> map) {
		List<MapItemInfo> list = makeList (map);
		for (Iterator<MapItemInfo> iter = list.iterator(); iter.hasNext(); ) {
			MapItemInfo item = (MapItemInfo) iter.next();
			LogHelper.debug ("*****************Sorted here: "+item.getKey() + "=" + item.getValue());
		}
	}
}

/*
	private String[] getMap (Map map) {
		Iterator keyValuePairs = map.entrySet().iterator();
		String[] envp = new String [map.size()];
		int count = 0;
		for (int i = 0; i < map.size(); i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs.next();
			String key = (String) entry.getKey();
			String value1 = (String) entry.getValue();
			envp[count++] = key + "=" + value1;
		}
		return envp;
	}
	@SuppressWarnings("unused")
	private void showMap99 (Map map) {
		Iterator keyValuePairs = map.entrySet().iterator();
		for (int i = 0; i < map.size(); i++) {
			Map.Entry entry = (Map.Entry) keyValuePairs.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			LogHelper.debug (key + "=" + value);
		}
	}
*/
/*
private ScenarioItemInfo makeXml99 (String scenario) {
	String file = "c:\\jv\\utils\\launcher.xml";
	ScenarioInfo scenarioInfo  = new ScenarioInfo();
	NodeItemInfo nodeItemInfo = JVxml.loadXML (new File (file));
	LogHelper.debug (nodeItemInfo.toString());
	NodeInfo nodeInfo = nodeItemInfo.getNodeInfo();
	for (Iterator iter = nodeInfo.getItems(); iter.hasNext(); ) {
		NodeItemInfo item = (NodeItemInfo) iter.next();
		AttributeInfo attribInfo = item.getAttributeInfo();
		AttributeItemInfo attributeItemInfo = attribInfo.getAttributeItemInfo("application", scenario);
		if (attributeItemInfo == null) continue;

		LogHelper.debug ("attributeItemInfo "+attributeItemInfo.toString());
		scenarioInfo.add (new ScenarioItemInfo (
				attribInfo.getAttributeItemInfo("name"),
				attribInfo.getAttributeItemInfo("application"),
				attribInfo.getAttributeItemInfo("main"),
				attribInfo.getAttributeItemInfo("classpath"),
				attribInfo.getAttributeItemInfo("path"),
				attribInfo.getAttributeItemInfo("cwd"),
				attribInfo.getAttributeItemInfo("params")));
	}
	ScenarioItemInfo scenarioItemInfo = scenarioInfo.getScenarioItemInfo(scenario);
	return scenarioItemInfo;
}
*/
