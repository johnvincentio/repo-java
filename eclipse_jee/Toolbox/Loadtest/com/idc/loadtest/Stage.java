
package com.idc.loadtest;

import java.io.Serializable;

import com.idc.http.Form;
import com.idc.http.FormItem;

public class Stage implements Serializable {
	private static final long serialVersionUID = 1;
	private String url;
	private Form form = new Form();
	private boolean post = true;
	private boolean browser = false;
	private String description;

	public String getUrl() {return url;}
	public Form getForm() {return form;}
	public boolean isPost() {return post;}
	public boolean isBrowser() {return browser;}
	public String getDescription() {return description;}
	public String getMessage() {
		if (description != null && description.length() > 0)
			return description;
		return url;
	}

	public void setUrl (String url) {this.url = url;}
	public void setForm (Form form) {this.form = form;}
	public void addFormItem (FormItem formItem) {form.add (formItem);}
	public void setPost (String str) {
		if (str == null) return;
		if (str.trim().equalsIgnoreCase("get")) post = false;
	}
	public void setOutput (String str) {
		if (str == null) return;
		if (str.trim().equalsIgnoreCase("browser")) browser = true;
	}
	public void setDescription (String description) {this.description = description;}

	public String toString() {
		return "("+getUrl()+","+isPost()+","+isBrowser()+","+getForm().toString()+","+getDescription()+")";
	}
}
