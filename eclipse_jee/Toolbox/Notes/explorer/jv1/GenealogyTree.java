package com.idc.explorer.jv1;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class GenealogyTree extends JTree {
	private static final long serialVersionUID = 1L;

	public GenealogyTree(NodeItemInfo nodeItemInfo) {
		super (new GenealogyModel (nodeItemInfo));
		System.out.println(">>> GenealogyTree");
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		setEditable (false);
		setShowsRootHandles (true);
		
		TreeCellRenderer renderer = new ToolTipTreeCellRenderer();
//		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//		Icon personIcon = null;
//		renderer.setLeafIcon(personIcon);
//		renderer.setClosedIcon(personIcon);
//		renderer.setOpenIcon(personIcon);
		setCellRenderer(renderer);
		System.out.println("<<< GenealogyTree");
	}

	/**
	 * Get the selected item in the tree, and call showAncestor with this item
	 * on the model.
	 */
	public void showAncestor(boolean b) {
		System.out.println(">>> GenealogyTree::showAncestor");
		Object newRoot = null;
		TreePath path = getSelectionModel().getSelectionPath();
		if (path != null) {
			newRoot = path.getLastPathComponent();
		}
		((GenealogyModel) getModel()).showAncestor(b, newRoot);
		System.out.println("<<< GenealogyTree::showAncestor");
	}
}
