
package com.idc.excel;

public class Worksheet {
	private String m_name;
	public String getWorksheetName() {return m_name;}
	public void setWorksheetName(String name) {m_name = name;}

	private Sections m_sections = new Sections();
	public Sections getSections() {return m_sections;}
	public void setSections (Sections sections) {
		m_sections = sections;
	}
	public void addSection (Section section) {
		if (section != null) m_sections.add(section);
	}
	public void show(String msg) {
		System.out.println(">>> Worksheet::show; "+msg);
		System.out.println(" Show Worksheet :"+getWorksheetName());
		m_sections.show("Worksheet::show");
		System.out.println(" End of Show Worksheet :"+getWorksheetName());
		System.out.println("<<< Worksheet::show; "+msg);
	}
	private Totals m_totals = new Totals();
	public Totals getTotals() {return m_totals;}
	public void setTotals() {
//		System.out.println(">>> Worksheet::setTotals");
		Section section;
		getSections().reset();
		while (getSections().hasNext()) {
			section = getSections().getSection();
//			System.out.println("Section; "+section.getName()+" "+section.getDs());
			setSubTotals (section.getTotals());
//			section.showTotals("Worksheet::setTotals; after setTotals");
			getSections().getNext();
		}
//		System.out.println("<<< Worksheet::setTotals");
	}
	private void setSubTotals (Totals subtotals) {
//		System.out.println(">>> Worksheet::setSubTotals");
//		subtotals.show("show subtotals");
//		m_totals.show("m_totals before increment");
		m_totals.incrementTotals(subtotals);
//		m_totals.show("m_totals after increment");
//		System.out.println("<<< Worksheet::setSubTotals");
	}
	public void showTotals(String msg) {
		System.out.println(">>> Worksheet::showTotals; "+msg);
		System.out.println("Worksheet :"+getWorksheetName());		
		m_totals.show("Worksheet::showTotals");
		System.out.println("<<< Worksheet::showTotals; "+msg);
	}
}
