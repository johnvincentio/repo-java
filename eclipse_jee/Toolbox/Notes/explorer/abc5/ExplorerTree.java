package com.idc.explorer.abc5;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.idc.trace.LogHelper;

public class ExplorerTree extends JTree implements ActionListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1L;

	private ExplorerGUI m_app;
	private File m_workingDirectory;
	private ExplorerModel m_model;
	private MouseListener m_popupListener;
	private JPopupMenu m_popupAll;
	private JMenuItem m_menuItemEdit;
	private JMenuItem m_menuItemExpand;
	private JMenuItem m_menuItemDecompile;
	private JMenuItem m_menuItemSaveAs;

	private JFileChooser m_fileChooser;
	private DragSource m_dragSource;

	public ExplorerTree (ExplorerGUI expanderGUI, File workingDirectory, File file) {
		super (new ExplorerModel (workingDirectory, file));
		System.out.println(">>> ExplorerTree");
		m_app = expanderGUI;
		m_workingDirectory = workingDirectory;

		m_model = (ExplorerModel) getModel();

		getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);

		setEditable (false);
		setShowsRootHandles (true);
		
		TreeCellRenderer renderer = new ExplorerTreeCellRenderer();
		setCellRenderer(renderer);

		m_popupAll = new JPopupMenu();
		m_menuItemEdit = new JMenuItem("Edit");
		m_menuItemEdit.addActionListener(this);
		m_popupAll.add(m_menuItemEdit);
		m_menuItemExpand = new JMenuItem("Expand");
		m_menuItemExpand.addActionListener(this);
		m_popupAll.add(m_menuItemExpand);
		m_menuItemDecompile = new JMenuItem("Decompile");
		m_menuItemDecompile.addActionListener(this);
		m_popupAll.add(m_menuItemDecompile);
		m_menuItemSaveAs = new JMenuItem("Save As");
		m_menuItemSaveAs.addActionListener(this);
		m_popupAll.add(m_menuItemSaveAs);

		m_popupListener = new PopupListener();
		addMouseListener (m_popupListener);

		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setCurrentDirectory (new File (System.getProperty ("user.dir")));

		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

		m_dragSource = new DragSource();
		int actions = DnDConstants.ACTION_COPY_OR_MOVE;
		m_dragSource.createDefaultDragGestureRecognizer (this, actions, this);

		System.out.println("<<< ExplorerTree");
	}

	public void actionPerformed (ActionEvent e) {
		System.out.println(">>> ExplorerTree::actionPerformed");
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem) (e.getSource());
			System.out.println( "Action event detected.\n" 
				+ "    Event source: " + source.getText()
				+ " (an instance of " + getClassName(source) + ")\n");
			String strNode = getNodeName();
			System.out.println("node "+strNode);
			NodeItemInfo nodeItemInfo = getNodeItemInfo();
			System.out.println("nodeItemInfo "+nodeItemInfo);

			if (source == m_menuItemEdit) {
				System.out.println("Edit request found");
				if (nodeItemInfo.isEditable()) {
					if (nodeItemInfo.isBrowser())
						browserFile (nodeItemInfo.getPath());
					else
						editFile (nodeItemInfo.getPath());
				}
			}
			else if (source == m_menuItemExpand) {
				System.out.println("Expand request found");
				if (! nodeItemInfo.isExpanded() && nodeItemInfo.isExpandable())
					m_model.expandNode (nodeItemInfo);
			}
			else if (source == m_menuItemDecompile) {
				System.out.println("Decompile request found");
				if (nodeItemInfo.isJavaDecompilable()) {
					m_model.decompile(nodeItemInfo);
				}
			}
			else if (source == m_menuItemSaveAs) {
				System.out.println ("SaveAs request found");
	            int returnVal = m_fileChooser.showSaveDialog(this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = m_fileChooser.getSelectedFile();
	                System.out.println("save to directory is :"+file.getPath()+":");
	        		Explorer expander = new Explorer (m_workingDirectory);
	        		NodeItemInfo item = expander.unPack (nodeItemInfo.getFile(), file);
	        		System.out.println("item "+item);
	            }
			}
		}
		System.out.println("<<< ExplorerTree::actionPerformed");
	}

	public void browserFile (final String strFile) {
		String[] strCmd = {ExplorerGUI.BROWSER, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	private void editFile (final String strFile) {
		String[] strCmd = {ExplorerGUI.EDITOR, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	private NodeItemInfo getNodeItemInfo() {
		NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
		/*
		if (node != null) {
			if (node.isLeaf()) {
				nodeItemInfo = (NodeItemInfo) node.getUserObject();
			}
		}
		*/
		return node;
	}
	private String getNodeName() {
		NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
		/*
		if (node != null) {
			if (node.isLeaf()) {
				Object nodeInfo = node.getUserObject();
				strReturn = nodeInfo.toString();
			}
		}
		*/
		return node.getName();
	}
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex+1);
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

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
		public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
		private void maybeShowPopup(MouseEvent e) {
			System.out.println(">>> PopupListener::maybeShowPopup");
			if (e.isPopupTrigger()) {
				NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
				if (node == null) return;
				System.out.println("node :"+node+":");
//				if (node.isLeaf())
					m_popupAll.show(e.getComponent(), e.getX(), e.getY());
			}
			System.out.println("<<< PopupListener::maybeShowPopup");
		}
	}
}
