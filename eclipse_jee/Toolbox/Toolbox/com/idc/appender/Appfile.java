package com.idc.appender;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.file.JVFile;
import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class Appfile extends JPanel implements ActionListener, DropTargetListener {
	private static final long serialVersionUID = 1;
	private App m_app;
	private Clipboard m_clipboard;
	private JTextField m_filenameField;
	private JTextArea m_textArea;
	private JButton m_btnClearFileField;
	private JButton m_btnClearMessages;
	private JButton m_btnSaveFile;
	private JButton m_btnPasteText;
	private JFileChooser m_fileChooser;

	public Appfile(App app) {
		m_app = app;
		m_clipboard = m_app.getToolkit().getSystemClipboard();
		makeFileChooser();
	}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		m_filenameField = new JTextField(20);
		new DropTarget(m_filenameField, 
				DnDConstants.ACTION_COPY_OR_MOVE,
				this);
		topPane.add(m_filenameField);
        m_btnClearFileField = new JButton("Clear");
        m_btnClearFileField.addActionListener(this);
        topPane.add(m_btnClearFileField);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(40,60);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(false);
		m_textArea.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(5,5,5,5),m_textArea.getBorder()));
		LogHelper.info("tabsize "+m_textArea.getTabSize());
		m_textArea.setTabSize(4);
		LogHelper.info("tabsize "+m_textArea.getTabSize());

		JScrollPane scroll = new JScrollPane(m_textArea,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll, BorderLayout.CENTER);
		midPane.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));        //t,l,b,r

		JPanel lowPane = new JPanel();
        m_btnSaveFile = new JButton("Save to File");
        m_btnSaveFile.addActionListener(this);
        lowPane.add(m_btnSaveFile);
        m_btnPasteText = new JButton("Paste");
        m_btnPasteText.addActionListener(this);
        lowPane.add(m_btnPasteText);
        m_btnClearMessages = new JButton("Clear");
        m_btnClearMessages.addActionListener(this);
        lowPane.add(m_btnClearMessages);

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}
	private void writeHeaderMessage (String msg) {
		writeToTextArea("");
		writeToTextArea("***********************************************************************************");
		writeToTextArea(msg);
		writeToTextArea("***********************************************************************************");
		writeToTextArea(""); 		
	}
	private void readFile(final String filename) {
		LogHelper.info("Read file :"+filename+":");
		writeHeaderMessage (filename);
		BufferedReader buf = null;
		String line;
		try{
			buf = new BufferedReader(new FileReader(filename));
			while((line = buf.readLine()) != null) {
				writeToTextArea(line);                        
			}
			setCaretToEnd();
			setFilenameMessage(filename);
		}
		catch(Exception exception) {
			LogHelper.info("Exception; "+exception.getMessage());
//			exception.printStackTrace();
		}
		finally {
			try {
				if (buf != null) buf.close();
			}
			catch(IOException exception2) {
				LogHelper.info("Exception; "+exception2.getMessage());
//				exception2.printStackTrace();
			}
		}
		LogHelper.info("Finished reading the file");
	}
	private void writeFile (File file) {
		String strFile = file.getPath().trim();
		System.out.println("strFile :"+strFile+":");
		String strText = m_textArea.getText();
		if (JVFile.writeFile (strText, file))
			LogHelper.info("Wrote the file");
		else
			LogHelper.info("Could not write to the file");
	}
	@SuppressWarnings("unchecked")
	public synchronized void drop(DropTargetDropEvent e) {
		System.out.println(">>> Abc:drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				Object obj = tr.getTransferData (DataFlavor.javaFileListFlavor);
				java.util.List<File> fileList = (java.util.List<File>) obj;
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File)iterator.next();
					readFile (file.getAbsolutePath());
				}
				e.getDropTargetContext().dropComplete(true);
			}
			else if (tr.isDataFlavorSupported (DataFlavor.stringFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				String str = ((String) tr.getTransferData(DataFlavor.stringFlavor)).trim();
				if (str.startsWith("file:/")) str = str.substring(7);
				LogHelper.info("File is :"+str+":");
				File file = new File(str);
				if (file.isFile()) {
					readFile (file.getAbsolutePath());
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
//		System.out.println("--- Abc:dragEnter");
	}
	public void dragExit(DropTargetEvent e) {
//		System.out.println("--- Abc:dragExit");
	}
	public void dragOver(DropTargetDragEvent e) {
//	System.out.println("--- Abc:dragOver");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
//	System.out.println("--- Abc:dropActionChanged");
	}
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
	private void writeToTextArea (final String line) {
		String str = line;
		JVString.replace (str, "        ","    ");
		JVString.replace (str, "    ","\t");
		setMessagesArea (str);
	}
	private void makeFileChooser() {
		m_fileChooser = new JFileChooser();
		m_fileChooser.setApproveButtonText("Save");
		m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		m_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String strCwd = System.getProperty("user.dir");
		m_fileChooser.setCurrentDirectory(new File(strCwd));
	}
	private void pasteText() {
		Transferable clipData = m_clipboard.getContents(m_clipboard);
		if (clipData != null) {
			try {
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String s = (String)(clipData.getTransferData (DataFlavor.stringFlavor));
					writeHeaderMessage("PASTED TEXT");
					writeToTextArea(s);                        
					setCaretToEnd();				
				}
			}
			catch (UnsupportedFlavorException ufe) {
				System.err.println("Unsupported flavor: " + ufe);
			} catch (IOException ufe) {
				System.err.println("Unable to get data: " + ufe);
			}
		}
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			LogHelper.info("jbutton");
			if (source == m_btnClearFileField) {
				LogHelper.info("btnClearFileField");
				clearFileNameField();
			}
			else if (source == m_btnClearMessages) {
				LogHelper.info("btnClearMessages");
				clearMessages();
			}
			else if (source == m_btnPasteText) {
				LogHelper.info("btnPasteText");
				pasteText();
			}
			else if (source == m_btnSaveFile) {
				LogHelper.info("btnSaveFile");
				int retval = m_fileChooser.showOpenDialog(Appfile.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooser.getSelectedFile();
					writeFile(file);
				}
			}
	}
	else
		LogHelper.info("else type");
	}
}
