package com.idc.appender;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.idc.trace.LogHelper;

public class AppCleanerPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1;
	private AppCleaner m_app;
	private Clipboard m_clipboard;
	private JTextArea m_textArea;
	private JButton m_btnCopyText;
	private JButton m_btnClearMessages;
	private JButton m_btnPasteText;

	public AppCleanerPanel(AppCleaner app) {
		m_app = app;
		m_clipboard = m_app.getToolkit().getSystemClipboard();
	}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_textArea = new JTextArea(40,60);
		m_textArea.setLineWrap(false);
		m_textArea.setEditable(false);
		m_textArea.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(5,5,5,5),m_textArea.getBorder()));

		JScrollPane scroll = new JScrollPane(m_textArea,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll,        BorderLayout.CENTER);
		midPane.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));        //t,l,b,r

		JPanel lowPane = new JPanel();
        m_btnPasteText = new JButton("Paste");
        m_btnPasteText.addActionListener(this);
        lowPane.add(m_btnPasteText);
        m_btnCopyText = new JButton("Copy Text");
        m_btnCopyText.addActionListener(this);
        lowPane.add(m_btnCopyText);
        m_btnClearMessages = new JButton("Clear");
        m_btnClearMessages.addActionListener(this);
        lowPane.add(m_btnClearMessages);

		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}
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
					validate();
				}
			}
		);
	}
	public void copyMessagesArea () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_textArea.selectAll();
					m_textArea.copy();
					validate();
				}
			}
		);
	}
	private void writeToTextArea(final String line) {
		Data data = new Data();
		Rows rows = data.makeData(line);
		Iterator<Row> iter = rows.getItems();
		while (iter.hasNext()) {
			Row row = (Row) iter.next();
//			row.show();
			setMessagesArea (row.getRow());
		}
	}

/*
	private void writeToTextArea(final String line) {
		System.out.println("line :"+line+":");
		JVString jvstr = new JVString(line);
//		jvstr.dump("before replacing stuff");
        jvstr.replace("        ","    ");
		jvstr.replace("    ","\t");
		jvstr.removeNull();
//		jvstr.dump("before replacing stuff");
		setMessagesArea (jvstr.getString());
	}
*/
	private void pasteText() {
		Transferable clipData = m_clipboard.getContents(m_clipboard);
		if (clipData != null) {
			try {
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String s = (String)(clipData.getTransferData(
							DataFlavor.stringFlavor));
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
	/*
	private void copyText() {
//		String strText = m_textArea.getText();
//		System.out.println("text :"+strText+":");
//		StringSelection data = new StringSelection(strText);
//		m_clipboard.setContents(data, data);
		m_textArea.selectAll();
		m_textArea.copy();
	}
	*/
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			LogHelper.info("jbutton");
			if (source == m_btnClearMessages) {
				LogHelper.info("btnClearMessages");
				clearMessages();
			}
			else if (source == m_btnPasteText) {
				LogHelper.info("btnPasteText");
				pasteText();
			}
			else if (source == m_btnCopyText) {
				LogHelper.info("btnCopyText");
//				copyText();
				copyMessagesArea();
			}			
	}
	else
		LogHelper.info("else type");
	}
}
