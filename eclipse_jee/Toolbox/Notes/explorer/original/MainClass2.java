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

public class MainClass2 {
	public static void main(final String args[]) {
		JFrame frame = new JFrame("Tree Tips");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DefaultMutableTreeNode m_top =	new DefaultMutableTreeNode (new BookInfo ("Top", "Dog"));
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("Fred", "Flintstone")));
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("Pliny", "Elder")));
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("Stew", "Socrates")));

		JTree tree = new JTree (m_top);
		
		ToolTipManager.sharedInstance().registerComponent(tree);
		TreeCellRenderer renderer = new ToolTipTreeCellRenderer2();
		tree.setCellRenderer(renderer);
		JScrollPane scrollPane = new JScrollPane(tree);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(300, 150);
		frame.setVisible(true);
	}
}

class ToolTipTreeCellRenderer2 implements TreeCellRenderer {
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

	public ToolTipTreeCellRenderer2() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
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
				if (tip instanceof BookInfo) {
					System.out.println("tip instanceof BookInfo");
					text = ((BookInfo) tip).getValue();
				}
				else {
					System.out.println("tip not instanceof BookInfo");
					text = (String) tip;
				}
			} else {
				System.out.println("getTreeCellRendererComponent - 4");
				tip = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
				text = (String) tip;
				System.out.println("getTreeCellRendererComponent - 5");
			}
			System.out.println("getTreeCellRendererComponent - 6; text :"+text+":");
			renderer.setToolTipText(text);
		}
		System.out.println("getTreeCellRendererComponent - 10");
		renderer.setText(text);
		System.out.println("<<< getTreeCellRendererComponent");
		return renderer;
	}
}

/*
	public static void main(final String args[]) {
		JFrame frame = new JFrame("Tree Tips");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DefaultMutableTreeNode m_top =	new DefaultMutableTreeNode("Something");
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("Fred", "Flintstone")));
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("Pliny", "Elder")));
		m_top.add (new DefaultMutableTreeNode(new BookInfo ("S.", "Socrates")));

		JTree tree = new JTree (m_top);

		JTree tree = new JTree (new String[] { "a", "b", "c" });
		ToolTipManager.sharedInstance().registerComponent(tree);
		TreeCellRenderer renderer = new ToolTipTreeCellRenderer();
		tree.setCellRenderer(renderer);
		JScrollPane scrollPane = new JScrollPane(tree);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(300, 150);
		frame.setVisible(true);
	}
*/