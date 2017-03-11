package com.idc.sql.lib;

import java.io.Serializable;

public class Scenario implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String databasename;
	private String schema;
	private String system;

	public String getName() {return name;}
	public String getDriver() {return driver;}
	public String getUrl() {return url;}
	public String getUsername() {return username;}
	public String getPassword() {return password;}
	public String getDatabasename() {return databasename;}
	public String getSchema() {return schema;}
	public String getSystem() {return system;}

	public void setName (String name) {this.name = name;}
	public void setDriver (String driver) {this.driver = driver;}
	public void setUrl (String url) {this.url = url;}
	public void setUsername (String username) {this.username = username;}
	public void setPassword (String password) {this.password = password;}
	public void setDatabasename (String databasename) {this.databasename = databasename;}
	public void setSchema (String schema) {this.schema = schema;}
	public void setSystem (String system) {this.system = system;}

	public boolean isAS400() {return system.trim().equalsIgnoreCase("AS400");}
	public boolean isSQLServer() {return system.trim().equalsIgnoreCase("SQLserver");}
	public boolean isMySQL() {return system.trim().equalsIgnoreCase("MYSQL");}
	public String toString() {
		return "("+getName()+","+getDriver()+","+getUrl()+","+getUsername()+","+getPassword()+","+getDatabasename()+","+getSchema()+","+getSystem()+")";
	}
}
