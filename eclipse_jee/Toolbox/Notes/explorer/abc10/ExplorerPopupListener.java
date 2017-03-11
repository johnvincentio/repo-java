package com.idc.explorer.abc10;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ExplorerPopupListener extends MouseAdapter {
	private ExplorerTree m_explorerTree;
	public ExplorerPopupListener (ExplorerTree explorerTree) {
		System.out.println(">>> ExplorerPopupListener::constructor");
		m_explorerTree = explorerTree;
		System.out.println("<<< ExplorerPopupListener::constructor");
	}

	public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
	public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
	private void maybeShowPopup(MouseEvent e) {
		System.out.println(">>> ExplorerPopupListener::maybeShowPopup");
		if (e.isPopupTrigger()) {
			NodeItemInfo nodeItemInfo = m_explorerTree.getNodeItemInfo();
			if (nodeItemInfo == null) return;
			System.out.println("ExplorerPopupListener; node :"+nodeItemInfo+":");

			ExplorerPopupMenu popupAll = new ExplorerPopupMenu (m_explorerTree, nodeItemInfo);
			popupAll.show (e.getComponent(), e.getX(), e.getY());
		}
		System.out.println("<<< ExplorerPopupListener::maybeShowPopup");
	}
}
