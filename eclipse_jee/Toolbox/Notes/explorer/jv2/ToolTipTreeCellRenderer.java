package com.idc.explorer.jv2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ToolTipTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	public ToolTipTreeCellRenderer() {}

	public Component getTreeCellRendererComponent (JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	    super.getTreeCellRendererComponent(
	            tree, value, selected, expanded,
	            leaf, row, hasFocus);
		System.out.println(">>> getTreeCellRendererComponent");
		System.out.println("selected "+selected);

		Font font = getFont();
		System.out.println(" "+font.getFontName());
		System.out.println(" "+font.getFamily());
		System.out.println(" "+font.getStyle());
		System.out.println(" "+font.getSize());

		String text = null;
		Object tip = null;
		if (value != null) {
//			System.out.println("getTreeCellRendererComponent - 1");

			if (value instanceof DefaultMutableTreeNode) {
//				System.out.println("getTreeCellRendererComponent - 2");
				tip = ((DefaultMutableTreeNode) value).getUserObject();
//				System.out.println("getTreeCellRendererComponent - 3");
				if (tip instanceof NodeItemInfo) {
//					System.out.println("tip instanceof NodeItemInfo");
					text = ((NodeItemInfo) tip).getName();
				} else {
//					System.out.println("tip not instanceof NodeItemInfo");
					text = (String) tip;
				}
			}
			else if (value instanceof NodeItemInfo) {
//				System.out.println("getTreeCellRendererComponent - 20");
				text = ((NodeItemInfo) value).getName();
			}
			else {
//				System.out.println("getTreeCellRendererComponent - 4");
//				System.out.println("value class :"+value.getClass()+":");
				tip = tree.convertValueToText (value, selected, expanded, leaf, row, hasFocus);
				if (tip == null) System.out.println("tip is null");
//				System.out.println("tip class :"+tip.getClass()+":");
				if (tip instanceof NodeItemInfo) {
//					System.out.println("tip instanceof NodeItemInfo");
					text = ((NodeItemInfo) tip).getName();
				} else {
//					System.out.println("tip not instanceof NodeItemInfo");
					text = (String) tip;
				}
				text = (String) tip;
//				System.out.println("getTreeCellRendererComponent - 5");
			}
//			System.out.println("getTreeCellRendererComponent - 6; text :" + text + ":");
			setToolTipText(text);
		}
//		System.out.println("getTreeCellRendererComponent - 10");
//		renderer.setTextSelectionColor (new Color (10, 30, 200));
//		renderer.setForeground(Color.BLUE);
//		renderer.setBackground(Color.YELLOW);

		setText(text);
/*
		if (selected) {
			setFont(new Font("TimesNewRoman",Font.BOLD + Font.ITALIC, 14));
			setBackgroundSelectionColor(Color.RED);
			setTextSelectionColor(Color.ORANGE);
		}
		else{
			setFont(new Font("TimesNewRoman",Font.ITALIC, 9));
			setBackgroundNonSelectionColor(Color.GREEN);
		}
*/
		System.out.println("<<< getTreeCellRendererComponent - text :"+text+":");
		return this;
	}
}
