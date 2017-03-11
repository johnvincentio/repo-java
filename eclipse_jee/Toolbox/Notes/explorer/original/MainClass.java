package com.idc.explorer.original;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class MainClass {

	public static void main(final String args[]) {
		JFrame frame = new JFrame("Tree Tips");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTree tree = new JTree(new String[] { "a", "b", "c" });
		ToolTipManager.sharedInstance().registerComponent(tree);
		TreeCellRenderer renderer = new ToolTipTreeCellRenderer();
		tree.setCellRenderer(renderer);
		JScrollPane scrollPane = new JScrollPane(tree);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(300, 150);
		frame.setVisible(true);
	}
}

class ToolTipTreeCellRenderer implements TreeCellRenderer {
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

	public ToolTipTreeCellRenderer() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Object tip = null;
		if (value != null) {

			if (value instanceof DefaultMutableTreeNode) {
				tip = ((DefaultMutableTreeNode) value).getUserObject();
			} else {
				tip = tree.convertValueToText(value, selected, expanded, leaf,
						row, hasFocus);
			}
			renderer.setToolTipText((String) tip);
		}
		renderer.setText((String) tip);
		return renderer;
	}
}
