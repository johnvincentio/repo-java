package com.idc.explorer.jv1;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class ToolTipTreeCellRenderer implements TreeCellRenderer {
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

	public ToolTipTreeCellRenderer() {
	}

	public Component getTreeCellRendererComponent (JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		System.out.println(">>> getTreeCellRendererComponent");

		String text = null;
		Object tip = null;
		if (value != null) {
			System.out.println("getTreeCellRendererComponent - 1");

			if (value instanceof DefaultMutableTreeNode) {
				System.out.println("getTreeCellRendererComponent - 2");
				tip = ((DefaultMutableTreeNode) value).getUserObject();
				System.out.println("getTreeCellRendererComponent - 3");
				if (tip instanceof NodeItemInfo) {
					System.out.println("tip instanceof NodeItemInfo");
					text = ((NodeItemInfo) tip).getName();
				} else {
					System.out.println("tip not instanceof NodeItemInfo");
					text = (String) tip;
				}
			}
			else if (value instanceof NodeItemInfo) {
				System.out.println("getTreeCellRendererComponent - 20");
				text = ((NodeItemInfo) value).getName();
			}
			else {
				System.out.println("getTreeCellRendererComponent - 4");
				System.out.println("value class :"+value.getClass()+":");
				tip = tree.convertValueToText (value, selected, expanded, leaf, row, hasFocus);
				if (tip == null) System.out.println("tip is null");
				System.out.println("tip class :"+tip.getClass()+":");
				if (tip instanceof NodeItemInfo) {
					System.out.println("tip instanceof NodeItemInfo");
					text = ((NodeItemInfo) tip).getName();
				} else {
					System.out.println("tip not instanceof NodeItemInfo");
					text = (String) tip;
				}
				text = (String) tip;
				System.out.println("getTreeCellRendererComponent - 5");
			}
			System.out.println("getTreeCellRendererComponent - 6; text :" + text + ":");
			renderer.setToolTipText(text);
		}
		System.out.println("getTreeCellRendererComponent - 10");
		renderer.setText(text);
		System.out.println("<<< getTreeCellRendererComponent");
		return renderer;
	}
}
