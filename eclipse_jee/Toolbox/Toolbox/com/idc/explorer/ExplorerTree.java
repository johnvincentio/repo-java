package com.idc.explorer;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

public class ExplorerTree extends JTree {
	private static final long serialVersionUID = 1L;

	private ExplorerGUI m_app;
	private File m_workingDirectory;

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
		setCellRenderer (new ExplorerTreeCellRenderer());

		addMouseListener (new ExplorerPopupListener (this));

		new DropTarget(this, DnDConstants.ACTION_COPY, new ExplorerDropTargetListener (m_app));

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer (this, 
				DnDConstants.ACTION_COPY, 
				new ExplorerDragSourceListener (this));

		System.out.println("<<< ExplorerTree::constructor");
	}

	public NodeItemInfo getNodeItemInfo() {	return (NodeItemInfo) getLastSelectedPathComponent();}
	public String getNodeName() {return ((NodeItemInfo) getLastSelectedPathComponent()).getName();}
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
