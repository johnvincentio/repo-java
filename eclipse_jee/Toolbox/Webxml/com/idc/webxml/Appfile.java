package com.idc.webxml;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.file.exec.OutputLine;

public class Appfile extends JPanel implements ActionListener, DropTargetListener, OutputLine {
	private static final long serialVersionUID = 1;
//	private App m_app;
//	private Clipboard m_clipboard;
	private JTextField m_filenameField;
	private JTextArea m_textArea;
	private JButton m_btnConvert;

	public Appfile(App app) {
//		m_app = app;
//		m_clipboard = m_app.getToolkit().getSystemClipboard();
	}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		m_filenameField = new JTextField(20);
//		 		 m_filenameField.setText("/tmp/web.xml");
		new DropTarget(m_filenameField,
		DnDConstants.ACTION_COPY_OR_MOVE,
		this);
		topPane.add(m_filenameField);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(40,60);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(false);
		m_textArea.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createEmptyBorder(5,5,5,5),m_textArea.getBorder()));
//		 		 System.out.println("tabsize "+m_textArea.getTabSize());
		m_textArea.setTabSize(4);
//		 		 System.out.println("tabsize "+m_textArea.getTabSize());

		JScrollPane scroll = new JScrollPane(m_textArea,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll,        BorderLayout.CENTER);
		midPane.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));        //t,l,b,r

		JPanel lowPane = new JPanel();
		m_btnConvert = new JButton("Convert");
		m_btnConvert.addActionListener(this);
		lowPane.add(m_btnConvert);

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}
	@SuppressWarnings("unchecked")
	public synchronized void drop(DropTargetDropEvent e) {
		System.out.println(">>> Abc:drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List<File> fileList = (java.util.List<File>) tr.getTransferData (DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File)iterator.next();
					convertWebXML (file.getAbsolutePath());
				}
				e.getDropTargetContext().dropComplete(true);
			}
			else if (tr.isDataFlavorSupported (DataFlavor.stringFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				String str = ((String) tr.getTransferData(DataFlavor.stringFlavor)).trim();
				if (str.startsWith("file:/")) str = str.substring(7);
				System.out.println("File is :"+str+":");
				File file = new File(str);
				if (file.isFile()) {
					convertWebXML (file.getAbsolutePath());
				}
				e.getDropTargetContext().dropComplete(true);
			}
			else {
				System.err.println ("Rejected");
				e.rejectDrop();
			}
		}
		catch (IOException io) {
			io.printStackTrace();
			e.rejectDrop();
		}
		catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
			e.rejectDrop();
		}
		System.out.println ("<<< Abc:drop");
	}
	public void dragEnter(DropTargetDragEvent e) {
//		 		 System.out.println("--- Abc:dragEnter");
	}
	public void dragExit(DropTargetEvent e) {
//		 		 System.out.println("--- Abc:dragExit");
	}
	public void dragOver(DropTargetDragEvent e) {
//		 System.out.println("--- Abc:dragOver");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
//		 System.out.println("--- Abc:dropActionChanged");
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
	public void writeToTextArea(final String line) {
		JVString jvstr = new JVString(line);
		jvstr.replace("        ","    ");
		jvstr.replace("    ","\t");
		setMessagesArea (jvstr.getString());
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			System.out.println("jbutton");
			if (source == m_btnConvert) {
				System.out.println("btnConvert");
				convertWebXML(m_filenameField.getText());
			}
		}
		else
		System.out.println("else type");
	}
	private void convertWebXML(String strFile) {
		System.out.println("convertWebXML; file :"+strFile+":");
		File file = new File (strFile);
		if (file.exists() && file.canRead()) {
			Control control = new Control (this);
			control.convertWebXML(strFile);
		}
	}
	public void println(String msg) {
		writeToTextArea(msg);
	}
	public void close() {}
}
/*
	private void clearFileNameField () {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_filenameField.setText("");
				validate();
			}
		}
		);
	}
private void setCaretToEnd() {
	m_textArea.setCaretPosition(m_textArea.getText().length());
}
private void clearMessages () {
	SwingUtilities.invokeLater (
	new Runnable() {
		public void run() {
			m_textArea.setText("");
			validate();
		}
	}
	);
}
*/
