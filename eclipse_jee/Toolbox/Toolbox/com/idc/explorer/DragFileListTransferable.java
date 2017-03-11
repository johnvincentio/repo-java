package com.idc.explorer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DragFileListTransferable implements Transferable {
	private java.util.List<File> fileList = new ArrayList<File>();
	private static DataFlavor[] flavors = {DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};

	public DragFileListTransferable (NodeItemInfo nodeItemInfo) {
		fileList.add (nodeItemInfo.getFile());
	}

	public DataFlavor[] getTransferDataFlavors() {return flavors;}

	public boolean isDataFlavorSupported (DataFlavor flavor) {
		return Arrays.asList(flavors).contains (flavor);
	}

	public synchronized Object getTransferData (DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals (DataFlavor.javaFileListFlavor)) {
			return fileList;
		} else if (flavor.equals (DataFlavor.stringFlavor)) {
			return fileList.toString();
		} else {
			throw new UnsupportedFlavorException (flavor);
		}
	}
}
