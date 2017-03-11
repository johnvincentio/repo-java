package com.idc.explorer.abc2;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import java.io.File;
import java.util.Vector;

public class ExpanderModel implements TreeModel {
	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	private File m_workingDirectory;
	private NodeItemInfo m_rootNodeItemInfo = null;

	public ExpanderModel (File workingDirectory, File file) {
		System.out.println(">>> ExpanderModel");
		m_workingDirectory = workingDirectory;

		if (file != null) {
			Expander expander = new Expander();
			NodeItemInfo nodeItemInfo = expander.unPack (file);
			NodeItemInfo.show(0, nodeItemInfo);
			m_rootNodeItemInfo = nodeItemInfo;
		}
		System.out.println("<<< ExpanderModel");
	}

	public void seeMe() {
		System.out.println("--- seeMe");
	}

	public void decompile (NodeItemInfo nodeItemInfo) {
		System.out.println(">>> ExpanderModel::decompile");
		System.out.println("path :"+nodeItemInfo.getPath()+":");
		Decompiler decompiler = new Decompiler (m_workingDirectory);
		NodeItemInfo item = decompiler.decompile (nodeItemInfo);
		NodeItemInfo.show(0, item);

		nodeItemInfo.setNodeInfo (item.getNodeInfo());

//		fireTreeStructureChanged (m_rootNodeItemInfo);
		fireTreeStructureChanged (nodeItemInfo);
		System.out.println("<<< ExpanderModel::decompile");
	}

	public void expandNode (NodeItemInfo nodeItemInfo) {
		System.out.println(">>> ExpanderModel::expandNode");
		System.out.println("path :"+nodeItemInfo.getPath()+":");
		Expander expander = new Expander();
		NodeItemInfo item = expander.unPack (nodeItemInfo.getFile());
		NodeItemInfo.show(0, item);

		nodeItemInfo.setNodeInfo (item.getNodeInfo());

//		fireTreeStructureChanged (m_rootNodeItemInfo);
		fireTreeStructureChanged (nodeItemInfo);
		System.out.println("<<< ExpanderModel::expandNode");
	}

	// ////////////// Fire events //////////////////////////////////////////////
	/**
	 * The only event raised by this model is TreeStructureChanged with the root
	 * as path, i.e. the whole tree has changed.
	 */
	protected void fireTreeStructureChanged (NodeItemInfo oldRoot) {
		// int len = treeModelListeners.size();
		System.out.println(">>> ExpanderModel::fireTreeStructureChanged");
		TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(e);
		}
		System.out.println("<<< ExpanderModel::fireTreeStructureChanged");
	}

	// ////////////// TreeModel interface implementation ///////////////////////

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 */
	public void addTreeModelListener (TreeModelListener l) {
		System.out.println(">>> ExpanderModel::addTreeModelListener");
		treeModelListeners.addElement(l);
		System.out.println("<<< ExpanderModel::addTreeModelListener");
	}

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 */
	public Object getChild (Object parent, int index) {
		System.out.println(">>> ExpanderModel::getChild");
		NodeItemInfo p = (NodeItemInfo) parent;
		System.out.println("<<< ExpanderModel::getChild");
		return p.getNodeInfo().getNodeItemInfoAt(index);
	}

	/**
	 * Returns the number of children of parent.
	 */
	public int getChildCount (Object parent) {
		System.out.println("--- ExpanderModel::getChildCount");
		NodeItemInfo p = (NodeItemInfo) parent;
		return p.getNodeInfo().getSize();
	}

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild (Object parent, Object child) {
		System.out.println("--- ExpanderModel::getIndexOfChild");
		NodeItemInfo parentNodeItemInfo = (NodeItemInfo) parent;
		NodeItemInfo childNodeItemInfo = (NodeItemInfo) child;
		return parentNodeItemInfo.getNodeInfo().getIndexNodeItemInfo(
				childNodeItemInfo);
	}

	/**
	 * Returns the root of the tree.
	 */
	public Object getRoot() {
		System.out.println("--- ExpanderModel::getRoot");
		return m_rootNodeItemInfo;
	}

	/**
	 * Returns true if node is a leaf.
	 */
	public boolean isLeaf (Object node) {
		System.out.println("--- ExpanderModel::isLeaf");
		NodeItemInfo p = (NodeItemInfo) node;
		return p.getNodeInfo().getSize() == 0;
	}

	/**
	 * Removes a listener previously added with addTreeModelListener().
	 */
	public void removeTreeModelListener (TreeModelListener l) {
		System.out.println("--- ExpanderModel::removeTreeModelListener");
		treeModelListeners.removeElement(l);
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. Not used by this model.
	 */
	public void valueForPathChanged (TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
	}
}
