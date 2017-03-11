package com.idc.diff.file;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.idc.trace.LogHelper;

public class AppfileDropTargetListener implements DropTargetListener {
	private Appfile m_appfile;
	public AppfileDropTargetListener (Appfile appfile, boolean filenameField) {
		m_appfile = appfile;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void drop (DropTargetDropEvent e) {
		LogHelper.info(">>> drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List<File> fileList = (java.util.List<File>) tr.getTransferData (DataFlavor.javaFileListFlavor);
				Iterator<File> iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					m_appfile.handleDroppedFile (file);
				}
				e.getDropTargetContext().dropComplete (true);
			}
			else if (tr.isDataFlavorSupported (DataFlavor.stringFlavor)) {
				LogHelper.info("it is a String");
				e.acceptDrop (DnDConstants.ACTION_COPY);
				String data = (String) tr.getTransferData(DataFlavor.stringFlavor);
				LogHelper.info("data :"+data+":");
				m_appfile.handleDroppedData (data);
				e.getDropTargetContext().dropComplete (true);
			}
			else {
				LogHelper.error ("Rejected");
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
		finally {
		}
		LogHelper.info ("<<< drop");
	}
	public void dragEnter(DropTargetDragEvent e) {
//		LogHelper.info("--- dragEnter");
	}
	public void dragExit(DropTargetEvent e) {
//		LogHelper.info("--- dragExit");
	}
	public void dragOver(DropTargetDragEvent e) {
//		LogHelper.info("--- dragOver");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
//		LogHelper.info("--- dropActionChanged");
	}
}
