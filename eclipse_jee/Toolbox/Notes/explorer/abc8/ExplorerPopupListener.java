package com.idc.explorer.abc8;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ExplorerPopupListener extends MouseAdapter {
	private ExplorerTree m_explorerTree;
	private ExplorerPopupMenu m_popupAll;
	public ExplorerPopupListener (ExplorerTree explorerTree) {
		System.out.println(">>> ExplorerPopupListener::constructor");
		m_explorerTree = explorerTree;
		m_popupAll = new ExplorerPopupMenu (m_explorerTree);
		System.out.println("<<< ExplorerPopupListener::constructor");
	}

	public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
	public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
	private void maybeShowPopup(MouseEvent e) {
		System.out.println(">>> ExplorerPopupListener::maybeShowPopup");
		if (e.isPopupTrigger()) {
			NodeItemInfo node = m_explorerTree.getNodeItemInfo();
			if (node == null) return;
			System.out.println("ExplorerPopupListener; node :"+node+":");
//			if (node.isLeaf())
				m_popupAll.show(e.getComponent(), e.getX(), e.getY());
		}
		System.out.println("<<< ExplorerPopupListener::maybeShowPopup");
	}
}
