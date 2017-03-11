
package com.idc.excel;

public class Workbook {
	private DocumentProperties m_doc = new DocumentProperties();
	private Worksheets m_worksheets = new Worksheets();
	
	public DocumentProperties getDocumentProperties() {return m_doc;}
	public void setDocumentProperties (DocumentProperties doc) {
		m_doc = doc;
	}
	public Worksheets getWorksheets() {return m_worksheets;}
	public void setWorksheets (Worksheets worksheets) {
		m_worksheets = worksheets;
	}
	public void addWorksheet (Worksheet worksheet) {
		if (worksheet != null) m_worksheets.add(worksheet);
	}
	public void show(String msg) {
		System.out.println("Show workbook");
		System.out.println(m_doc.toString());
		m_worksheets.show(msg);
	}
	private Totals m_totals = new Totals();
	public Totals getTotals() {return m_totals;}
	public void setTotals() {
		Worksheet worksheet;
		getWorksheets().reset();
		while (getWorksheets().hasNext()) {
			worksheet = getWorksheets().getWorksheet();
//			System.out.println("Section; "+section.getName()+" "+section.getDs());
			setTotals (worksheet.getTotals());
			getWorksheets().getNext();
		}
	}
	private void setTotals (Totals subtotals) {
		m_totals.incrementTotals(subtotals);
	}
	public void showTotals() {
		System.out.println("Workbook :");		
		m_totals.show("Workbook::showTotals");
	}
}

