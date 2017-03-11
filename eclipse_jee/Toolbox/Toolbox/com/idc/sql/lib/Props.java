package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import java.io.Serializable;

import com.idc.trace.LogHelper;

public class Props implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String database;
	private String url;
	private String dbdriver;
	private String schema;
	public Props() {}

	public String getUsername() {return username;}
	public String getPassword() {return password;}
	public String getDatabase() {return database;}
	public String getUrl() {return url;}
	public String getDbdriver() {return dbdriver;}
	public String getSchema() {return schema;}
	public void setUsername (String username) {this.username = username;}
	public void setPassword (String password) {this.password = password;}
	public void setDatabase (String database) {this.database = database;}
	public void setUrl (String url) {this.url = url;}
	public void setDbdriver (String dbdriver) {this.dbdriver = dbdriver;}
	public void setSchema (String schema) {this.schema = schema;}
	public String toString() {
		return "("+getUsername()+","+getPassword()+","+getDatabase()+","+getUrl()+","+
			getDbdriver()+","+getSchema()+")";
	}

	public boolean initProps (String propertiesFile) {
		try {
			Properties defaultProps = new Properties();
			FileInputStream in = new FileInputStream(propertiesFile);
			defaultProps.load(in);
			in.close();
			username = defaultProps.getProperty("db2v8.username");
			password = defaultProps.getProperty("db2v8.password");
			database = defaultProps.getProperty("db2v8.databasename");
			url = defaultProps.getProperty("db2v8.url")+database;
			dbdriver = defaultProps.getProperty("db2v8.driver");
			schema = defaultProps.getProperty("db2v8.schema");
			return true;
		}
		catch (IOException exception) {
			LogHelper.error ("trouble with properties file");
			return false;
		}
	}
}
