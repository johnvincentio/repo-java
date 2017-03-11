package com.idc.diff.all.gui;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.trace.LogHelper;

public class DirectoryOptionsPanel extends JPanel implements ActionListener, DropTargetListener {
	private static final long serialVersionUID = 1;

	private DiffallGui m_diffallGui;

	private JFileChooser m_fileChooserBase;
	private JButton m_btnDirBase;
	private JTextField m_dirBaseField;

	private JFileChooser m_fileChooserDiff;
	private JButton m_btnDirDiff;
	private JTextField m_dirDiffField;

	public Options getOptions() {return new Options();}

	public DirectoryOptionsPanel (DiffallGui diffallGui) {
		m_diffallGui = diffallGui;

		JPanel paneBaselineDirectory = new JPanel();
		m_btnDirBase = new JButton ("Baseline Directory");
		m_btnDirBase.addActionListener (this);
		m_dirBaseField = new JTextField (40);
		m_dirBaseField.setText ("/tmp101/a");
		m_dirBaseField.addActionListener (this);
		new DropTarget (m_dirBaseField, DnDConstants.ACTION_COPY_OR_MOVE, this);
		m_fileChooserBase = new JFileChooser();
		m_fileChooserBase.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		m_fileChooserBase.setCurrentDirectory (new File (m_dirBaseField.getText()));
		paneBaselineDirectory.add (m_btnDirBase);
		paneBaselineDirectory.add (m_dirBaseField);	
		
		JPanel paneCompareDirectory = new JPanel();
		m_btnDirDiff = new JButton ("Compare Directory");
		m_btnDirDiff.addActionListener (this);
		m_dirDiffField = new JTextField (40);
		m_dirDiffField.setText ("/tmp101/b");
		m_dirDiffField.addActionListener (this);
		new DropTarget (m_dirDiffField, DnDConstants.ACTION_COPY_OR_MOVE, this);
		m_fileChooserDiff = new JFileChooser();
		m_fileChooserDiff.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		m_fileChooserDiff.setCurrentDirectory (new File (m_dirDiffField.getText()));
		paneCompareDirectory.add (m_btnDirDiff);
		paneCompareDirectory.add (m_dirDiffField);		
		
		setLayout (new GridLayout (1, 2, 0, 0));
		add (paneBaselineDirectory);
		add (paneCompareDirectory);
	}

	public boolean isDirectoriesSame() {
		return m_dirBaseField.getText().equals (m_dirDiffField.getText());
	}
	public boolean isOptionsValid() {
		if (! isDirBaseFieldValid()) return false;
		if (! isDirDiffFieldValid()) return false;
		return true;
	}

	private boolean isDirBaseFieldValid() {
		String strDir = m_dirBaseField.getText();
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}
	private boolean isDirDiffFieldValid() {
		String strDir = m_dirDiffField.getText();
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
//			LogHelper.info("jbutton");
			if (source == m_btnDirBase) {
//				LogHelper.info("btnDirBase");
				m_fileChooserBase.setCurrentDirectory (new File (m_dirBaseField.getText()));
				int retval = m_fileChooserBase.showOpenDialog (m_diffallGui.getContentPane());
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooserBase.getSelectedFile();
					setDirBaseField (file.getPath());
				}
			}
			else if (source == m_btnDirDiff) {
//				LogHelper.info("btnDirDiff");
				m_fileChooserDiff.setCurrentDirectory (new File (m_dirDiffField.getText()));
				int retval = m_fileChooserDiff.showOpenDialog (m_diffallGui.getContentPane());
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooserDiff.getSelectedFile();
					setDirDiffField (file.getPath());
				}
			}
		}
//		else
//			LogHelper.info("else type");
	}

	public void setDirBaseField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_dirBaseField.setText(msg);
					m_diffallGui.validate();
				}
			}
		);		
	}
	public void setDirDiffField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_dirDiffField.setText(msg);
					m_diffallGui.validate();
				}
			}
		);		
	}

	public void setDirBaseButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnDirBase.setEnabled(bBtn);
				}
			}
		);
	}
	public void setDirDiffButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnDirDiff.setEnabled(bBtn);
				}
			}
		);
	}

	@SuppressWarnings("unchecked")
	public synchronized void drop (DropTargetDropEvent dtde) {
		LogHelper.info(">>> drop");
		try {
			Transferable tr = dtde.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				DropTargetContext dtc = dtde.getDropTargetContext();
				JTextField jtf = (JTextField) dtc.getComponent();

				dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List<File> fileList = (java.util.List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					LogHelper.info("file "+file.getPath());
					if (file.isDirectory()) {
						jtf.setText(file.getPath());
						break;
					}
				}
				dtde.getDropTargetContext().dropComplete(true);
			} else {
				System.err.println ("Rejected");
				dtde.rejectDrop();
			}
		}
		catch (IOException io) {
			io.printStackTrace();
			dtde.rejectDrop();
		}
		catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
			dtde.rejectDrop();
		}
		LogHelper.info("<<< drop");
	}
	public void dragEnter(DropTargetDragEvent e) {
//		System.out.println("--- Abc:dragEnter");
	}
	public void dragExit(DropTargetEvent e) {
//		System.out.println("--- Abc:dragExit");
	}
	public void dragOver(DropTargetDragEvent e) {
//		System.out.println("--- Abc:dragOver");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
//		System.out.println("--- Abc:dropActionChanged");
	}

	public class Options {
		public File getBaseDirectory() {return new File (m_dirBaseField.getText());}
		public File getCurrentDirectory() {return new File (m_dirDiffField.getText());}
	}
}
