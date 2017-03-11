package com.idc.rda.phase1.items;

public class HeaderItemInfo {
	private int templateVersion;
	private int jira;
	private int sr;
	private String description;

	public HeaderItemInfo (int templateVersion, int jira, int sr, String description) {
		this.templateVersion = templateVersion;
		this.jira = jira;
		this.sr = sr;
		this.description = description;
	}

	public int getTemplateVersion() {return templateVersion;}
	public int getJira() {return jira;}
	public int getSr() {return sr;}
	public String getDescription() {return description;}

	public String toString() {
		return "("+getTemplateVersion()+","+getJira()+","+getSr()+","+getDescription()+")";
	}
}
