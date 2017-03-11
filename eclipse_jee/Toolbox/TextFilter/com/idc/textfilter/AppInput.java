package com.idc.textfilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.idc.swing.JVMessagesArea;
import com.idc.swing.progress.JVProgressBar;
import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class AppInput extends JPanel implements ActionListener, DropTargetListener {
	private static final long serialVersionUID = 1;

	private JComboBox m_comboDirs;

	private JVMessagesArea m_messagesArea;	

	private JButton m_btnClearText;
	private JButton m_btnApp;
	private JLabel m_txtStatus;
	private JVProgressBar m_progress;

	private TextFilterGUI m_app;
	public AppInput(TextFilterGUI app) {m_app = app;}

	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public Container makeContentPane() {
		JPanel paneA = new JPanel();
		m_comboDirs = new JComboBox();
		for (int num = 1; num < 20; num++) m_comboDirs.addItem(num);
		m_comboDirs.addActionListener(this);

		JPanel paneB = new JPanel();
		paneB.add (new JLabel("Configuration:"));
		paneB.add(m_comboDirs);

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(paneA, BorderLayout.NORTH);
		topPane.add(paneB, BorderLayout.SOUTH);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		midPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane(40, 80);
		m_messagesArea.getTextArea().setEditable(true);
		m_messagesArea.getTextArea().setDragEnabled(true);
		midPane.add(new JScrollPane(m_messagesArea.getTextArea()),BorderLayout.CENTER);

		JPanel lowPane = new JPanel();
		m_btnClearText = new JButton("Clear Text");
		m_btnClearText.addActionListener(this);
		lowPane.add(m_btnClearText);
		m_btnApp = new JButton("Execute");
		m_btnApp.setDefaultCapable(true);
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);

		m_txtStatus = new JLabel();
		m_progress = new JVProgressBar();
		lowPane.add (m_txtStatus);
		lowPane.add(m_progress);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}

	public JVMessagesArea getMessagesArea() {return m_messagesArea;}
	public JVProgressBar getProgressBar() {return m_progress;}
	public Integer getSelectedItem() {return (Integer) m_comboDirs.getSelectedItem();}

	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn) msg = "Execute";
		else msg = "Stop";
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnApp.setText(msg);
				}
			}
		);
	}
	public void setStatusMessage (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_txtStatus.setText(msg);
					validate();
				}
			}
		);
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			if (source == m_btnApp) {
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				if (strBtn.equals("Execute")) {
					m_app.startThread();
					setButtonText(false);
				}
				else if (strBtn.equals("Stop")) {
					m_app.stopThread();
					setButtonText(true);
				}							
			}
			else if (source == m_btnClearText)
				m_messagesArea.clear();
		}
		else
			LogHelper.info("else type");
	}

	public synchronized void drop(DropTargetDropEvent e) {
		LogHelper.info(">>> drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List<?> fileList = (java.util.List<?>) tr.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<?> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					readFile (file.getAbsolutePath());
					m_messagesArea.reposition();
				}
				e.getDropTargetContext().dropComplete(true);
			} else {
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
		LogHelper.info ("<<< drop");
	}
	public void dragEnter(DropTargetDragEvent e) {
//		LogHelper.info("--- Abc:dragEnter");
	}
	public void dragExit(DropTargetEvent e) {
//		LogHelper.info("--- Abc:dragExit");
	}
	public void dragOver(DropTargetDragEvent e) {
//		LogHelper.info("--- Abc:dragOver");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
//		LogHelper.info("--- Abc:dropActionChanged");
	}
	private void readFile(final String filename) {
//		LogHelper.info(">>> readFile");
		BufferedReader buf = null;
		String line;
		try {
			buf = new BufferedReader(new FileReader(filename));
			while ((line = buf.readLine()) != null) {
				JVString.replace(line, "\t","    ");
				m_messagesArea.add (line);
			}
			m_messagesArea.reposition();
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
//		LogHelper.info("<<< readFile");
	}
}
