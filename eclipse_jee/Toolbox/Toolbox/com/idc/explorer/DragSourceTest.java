package com.idc.explorer;

import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class DragSourceTest {
	public static void main(String[] args) {
		JFrame frame = new DragSourceFrame();
		frame.setVisible(true);
	}
}

class DragSourceFrame extends JFrame implements DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;

	private JList fileList;
	private DefaultListModel model;
	private Object[] draggedValues;

	public DragSourceFrame() {
		setTitle("DragSourceTest");
		setSize(300, 200);
		addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				System.exit(0);
			}
		});

		Container contentPane = getContentPane();
		File f = new File(".").getAbsoluteFile();
		File[] files = f.listFiles();
		model = new DefaultListModel();
		for (int i = 0; i < files.length; i++)
			model.addElement(files[i]);
		fileList = new JList(model);
		contentPane.add(new JScrollPane(fileList), "Center");
		contentPane.add(new JLabel("Drag files from this list"), "North");

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(fileList,
				DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	// DragGestureListener method

	public void dragGestureRecognized(DragGestureEvent event) {
		draggedValues = fileList.getSelectedValues();
		Transferable transferable = new FileListTransferable(draggedValues);
		event.startDrag(null, transferable, this);
	}

	// DragSourceListener methods

	public void dragEnter(DragSourceDragEvent event) {}
	public void dragOver(DragSourceDragEvent event) {}
	public void dragExit(DragSourceEvent event) {}
	public void dropActionChanged(DragSourceDragEvent event) {}

	public void dragDropEnd(DragSourceDropEvent event) {
		if (event.getDropSuccess()) {
			int action = event.getDropAction();
			if (action == DnDConstants.ACTION_MOVE) {
				for (int i = 0; i < draggedValues.length; i++)
					model.removeElement(draggedValues[i]);
			}
		}
	}
}

class FileListTransferable implements Transferable {
	private static DataFlavor[] flavors = {DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};
	@SuppressWarnings("unchecked")
	private java.util.List fileList;

	@SuppressWarnings("unchecked")
	public FileListTransferable(Object[] files) {
		fileList = new ArrayList(Arrays.asList(files));
	}

	public DataFlavor[] getTransferDataFlavors() {return flavors;}

	public boolean isDataFlavorSupported (DataFlavor flavor) {
		return Arrays.asList(flavors).contains(flavor);
	}

	public synchronized Object getTransferData (DataFlavor flavor)
			throws UnsupportedFlavorException {
		if (flavor.equals(DataFlavor.javaFileListFlavor)) {
			return fileList;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return fileList.toString();
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
