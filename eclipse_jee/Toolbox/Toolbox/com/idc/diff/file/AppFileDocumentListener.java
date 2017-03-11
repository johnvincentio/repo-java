package com.idc.diff.file;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AppFileDocumentListener implements DocumentListener {
	private Appfile m_appfile;
	private boolean m_active = true;
	public AppFileDocumentListener (Appfile appfile) {
		m_appfile = appfile;
	}

	public void setActive (boolean active) {m_active = active;}
	public void changedUpdate(DocumentEvent documentEvent) {handleEvent (documentEvent);}
	public void insertUpdate(DocumentEvent documentEvent) {handleEvent (documentEvent);}
	public void removeUpdate(DocumentEvent documentEvent) {handleEvent (documentEvent);}

	public void handleEvent (DocumentEvent documentEvent) {
		if (m_active) m_appfile.setFilenameMessage("");
		System.out.println("active "+m_active);
		System.out.println("Offset: " + documentEvent.getOffset());
		System.out.println("Length: " + documentEvent.getLength());
		DocumentEvent.EventType type = documentEvent.getType();
		String typeString = null;
		if (type.equals(DocumentEvent.EventType.CHANGE)) {
			typeString = "Change";
		} else if (type.equals(DocumentEvent.EventType.INSERT)) {
			typeString = "Insert";
		} else if (type.equals(DocumentEvent.EventType.REMOVE)) {
			typeString = "Remove";
		}
		System.out.println("Type  : " + typeString);
	}
}

/*
	public void printInfo(DocumentEvent documentEvent) {
		System.out.println("Offset: " + documentEvent.getOffset());
		System.out.println("Length: " + documentEvent.getLength());
		DocumentEvent.EventType type = documentEvent.getType();
		String typeString = null;
		if (type.equals(DocumentEvent.EventType.CHANGE)) {
			typeString = "Change";
		} else if (type.equals(DocumentEvent.EventType.INSERT)) {
			typeString = "Insert";
		} else if (type.equals(DocumentEvent.EventType.REMOVE)) {
			typeString = "Remove";
		}
		System.out.println("Type  : " + typeString);
		Document documentSource = documentEvent.getDocument();
		Element rootElement = documentSource.getDefaultRootElement();
		DocumentEvent.ElementChange change = documentEvent.getChange(rootElement);
		System.out.println("Change: " + change);
	}
*/
