
package com.idc.excel;

public class DocumentProperties {
	private String author;
	private String company;
	public DocumentProperties() {}

    public String getAuthor() {return author;}
    public String getCompany() {return company;}

    public void setAuthor (String author) {this.author = author;}
    public void setCompany (String company) {this.company = company;}

    public String toString() {
        return "("+getAuthor()+","+getCompany()+")";
    }
}
