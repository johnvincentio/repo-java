package com.idc.diff.file;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.file.JVFile;
import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class Appfile extends JPanel {
	private static final long serialVersionUID = -3114803771589778952L;

	private AppGUI m_appGUI;
	private JTextField m_filenameField;
	private JTextField m_dataField;
	private JTextArea m_textArea;
	private int m_nLineNumber = 0;

	public Appfile (AppGUI appGUI) {m_appGUI = appGUI;}
	public String getCompareFilename() {return m_filenameField.getText();}
	public boolean isCompareFilename() {return (m_filenameField.getText() != null && m_filenameField.getText().length () > 0);}

	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		m_filenameField = new JTextField(20);
		topPane.add (m_filenameField, BorderLayout.CENTER);

		m_dataField = new JTextField(5);
		m_dataField.addActionListener (new AppfileActionListener (this));
		new DropTarget (m_dataField, DnDConstants.ACTION_COPY, new AppfileDropTargetListener (this, false));
		topPane.add(m_dataField, BorderLayout.EAST);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(50,35);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(true);
		m_textArea.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder(5,5,5,5),m_textArea.getBorder()));
//		m_textArea.getDocument().addDocumentListener (new AppFileDocumentListener (this));

		JScrollPane scroll = new JScrollPane(m_textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll, BorderLayout.CENTER);
		midPane.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));        //t,l,b,r

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public void handleDroppedFile (File file) {
		LogHelper.info(">>> handleDroppedFile");
		clearMessagesArea();
		m_nLineNumber = 0;
		readFile (file.getAbsolutePath());
		m_textArea.setCaretPosition(0);
		setFilenameMessage(file.getAbsolutePath());
		LogHelper.info("<<< handleDroppedFile");
	}
	public void handleDroppedData (String data) {
		LogHelper.info(">>> handleDroppedData");
		File tmp = m_appGUI.getTempFileName();
		JVFile.writeFile (data, tmp);
		handleDroppedFile (tmp);
		setDataMessage ("");
		LogHelper.info("<<< handleDroppedData");
	}
	public void handlePastedData (String data) {
		LogHelper.info(">>> handlePastedData");
		String[] lines = data.split (" \t");
		System.out.println("records "+lines.length);
		StringBuffer buf = new StringBuffer();
		for (int cnt = 0; cnt < lines.length; cnt++) {
			if (cnt > 0) buf.append ("\t");
			buf.append (lines[cnt]).append("\n");
		}
		File tmp = m_appGUI.getTempFileName();
		JVFile.writeFile (buf.toString(), tmp);
		handleDroppedFile (tmp);
		setDataMessage ("");
		LogHelper.info("<<< handlePastedData");
	}
	public File handlePastedData() {
		LogHelper.info(">>> handlePastedData");
		String data = m_textArea.getText();
		File tmp = m_appGUI.getTempFileName();
		JVFile.writeFile (data, tmp);
		setDataMessage ("");
		LogHelper.info("<<< handlePastedData");
		return tmp;
	}

	private void readFile (final String filename) {
//		System.out.println(">>> Test:readFile");
		BufferedReader buf = null;
		String line;
		try {
			buf = new BufferedReader(new FileReader(filename));
			while ((line = buf.readLine()) != null) {
				handleLine(line);
			}
			m_textArea.setCaretPosition(0);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		finally {
			try {
				if (buf != null) buf.close();
			}
			catch (IOException exception2) {
				exception2.printStackTrace();
			}
		}
//		System.out.println("<<< Test:readFile");
	}

	public void setFilenameMessage (final String msg) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						m_filenameField.setText(msg);
						validate();
					}
				}
			);
	}
	public void setDataMessage (final String msg) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						m_dataField.setText(msg);
						validate();
					}
				}
			);
	}
	public void setMessagesArea (final String msg) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						m_textArea.append(msg);
						m_textArea.append("\n");
					}
				}
			);
	}
	public void clearMessagesArea () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_textArea.setText("");
					m_textArea.setCaretPosition(m_textArea.getText().length());
					validate();
				}
			}
		);
	}
	private void handleLine (final String line) {
		String str = line;
		JVString.replace (str, "\t","    ");
		String str2 = ++m_nLineNumber+".  " + str;
		setMessagesArea (str2);
	}
}

/*
private void readFileToEditor(final String filename) {
System.out.println(">>> Test:readFileToEditor");
EditorKit kit = m_textPane.getEditorKit();
Document document = m_textPane.getDocument();

try {
document.remove(0,document.getLength());
kit.read(new FileReader(filename), document, 0);
}
catch(Exception ex) {
ex.printStackTrace();
}
System.out.println("<<< Test:readFileToEditor");
}
*/
