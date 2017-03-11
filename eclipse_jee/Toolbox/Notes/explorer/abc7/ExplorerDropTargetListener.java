package com.idc.explorer.abc7;

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

public class ExplorerDropTargetListener implements DropTargetListener {
	private static final long serialVersionUID = 1L;

	private ExplorerGUI m_app;
	public ExplorerDropTargetListener (ExplorerGUI app) {m_app = app;}

	public void dragExit(DropTargetEvent e) {
		LogHelper.info("--- ExplorerTree:dragExit; DropTargetEvent");
	}
	public void dragEnter(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dragEnter; DropTargetDragEvent");
	}
	public void dragOver(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dragOver; DropTargetDragEvent");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dropActionChanged; DropTargetDragEvent");
	}

	public synchronized void drop (DropTargetDropEvent e) {
		System.out.println(">>> ExplorerTree:drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List fileList = (java.util.List)
				tr.getTransferData (DataFlavor.javaFileListFlavor);
				Iterator iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					System.out.println("file.getAbsolutePath() "+file.getAbsolutePath());
					m_app.remakeContentPane (file);
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
		System.out.println ("<<< ExplorerTree:drop");
	}
}
