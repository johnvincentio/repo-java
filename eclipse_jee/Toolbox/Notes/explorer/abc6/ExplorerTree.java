package com.idc.explorer.abc6;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
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
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.idc.trace.LogHelper;

public class ExplorerTree extends JTree implements DropTargetListener, 
				DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;

	private ExplorerGUI m_app;
	private File m_workingDirectory;

	private ExplorerPopupListener m_popupListener;

	private DragSource m_dragSource;

	public File getWorkingDirectory() {return m_workingDirectory;}
	public ExplorerModel getExplorerModel() {return (ExplorerModel) getModel();}

	public ExplorerTree (ExplorerGUI expanderGUI, File workingDirectory, File file) {
		super (new ExplorerModel (workingDirectory, file));
		System.out.println(">>> ExplorerTree::constructor");
		m_app = expanderGUI;
		m_workingDirectory = workingDirectory;

		getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);

		setEditable (false);
		setShowsRootHandles (true);
		
		TreeCellRenderer renderer = new ExplorerTreeCellRenderer();
		setCellRenderer(renderer);

		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

		m_dragSource = new DragSource();
		int actions = DnDConstants.ACTION_COPY_OR_MOVE;
		m_dragSource.createDefaultDragGestureRecognizer (this, actions, this);

		m_popupListener = new ExplorerPopupListener (this);
		addMouseListener (m_popupListener);

		System.out.println("<<< ExplorerTree::constructor");
	}

	public NodeItemInfo getNodeItemInfo() {
		return (NodeItemInfo) getLastSelectedPathComponent();
	}

	public String getNodeName() {
		return ((NodeItemInfo) getLastSelectedPathComponent()).getName();
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
	public void dragEnter(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dragEnter; DropTargetDragEvent");
	}
	public void dragExit(DropTargetEvent e) {
		LogHelper.info("--- ExplorerTree:dragExit; DropTargetEvent");
	}
	public void dragOver(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dragOver; DropTargetDragEvent");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
		LogHelper.info("--- ExplorerTree:dropActionChanged; DropTargetDragEvent");
	}
	public void dragEnter(DragSourceDragEvent dsde) {
		LogHelper.info("--- ExplorerTree:dragEnter; DragSourceDragEvent");
	}

	public void dragExit(DragSourceEvent dse) {
		LogHelper.info("--- ExplorerTree:dragExit; DragSourceEvent");
	}
	public void dragOver(DragSourceDragEvent dsde) {
		LogHelper.info("--- ExplorerTree:dragOver; DragSourceDragEvent");
	}

	public void dragGestureRecognized_99 (DragGestureEvent evt) {
		LogHelper.info(">>> ExplorerTree:dragGestureRecognized; DragGestureEvent");
		Transferable t = new StringSelection("aString");
		m_dragSource.startDrag (evt, DragSource.DefaultCopyDrop, t, this);
		LogHelper.info("<<< ExplorerTree:dragGestureRecognized; DragGestureEvent");
	}

	public void dragGestureRecognized (DragGestureEvent dge) {
		LogHelper.info(">>> ExplorerTree:dragGestureRecognized");
//		TreePath path = m_tree.getSelectionPath();
//		if ((path == null) || (path.getPathCount() <= 1)) {
//			return;//    	 We can't move the root node or an empty selection
//		}
		NodeItemInfo nodeItemInfo = (NodeItemInfo) getLastSelectedPathComponent();
		NodeItemInfo.show (0, nodeItemInfo);
		/*
		Object obj = node.getUserObject();
		System.out.println(" obj "+obj);
		TreePath path = getSelectionPath();
		Object obj1 = path.getLastPathComponent();
		System.out.println(" obj1 "+obj1);

		TransferableTreeNode transferable = new TransferableTreeNode(path);
*/
//		DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) m_tree.get;
//		NodeItemInfo nodeItemInfo = (NodeItemInfo) node.getUserObject();
//		NodeItemInfo.show (0, nodeItemInfo);
//    	oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//   	TransferableTreeNode transferable = new TransferableTreeNode(path);
//    	dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);
		LogHelper.info("<<< ExplorerTree:dragGestureRecognized");
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		System.out.println("Action: " + dsde.getDropAction());
		System.out.println("Target Action: " + dsde.getTargetActions());
		System.out.println("User Action: " + dsde.getUserAction());
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		System.out.println("Drop Action: " + dsde.getDropAction());
	}
}

/*
	public String getNodeName() {
		NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
		if (node != null) {
			if (node.isLeaf()) {
				Object nodeInfo = node.getUserObject();
				strReturn = nodeInfo.toString();
			}
		}
		return node.getName();
	}
*/
