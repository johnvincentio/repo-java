package com.idc.explorer.abc9;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ExplorerPopupMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1;

	private ExplorerTree m_explorerTree;

	private JFileChooser m_fileChooser;

	private JMenuItem m_menuItemEdit;
	private JMenuItem m_menuItemExpand;
	private JMenuItem m_menuItemDecompile;
	private JMenuItem m_menuItemSaveAs;

	public ExplorerPopupMenu (ExplorerTree explorerTree, NodeItemInfo nodeItemInfo) {
		System.out.println(">>> ExplorerPopupMenu::constructor");
		m_explorerTree = explorerTree;

		m_menuItemEdit = new JMenuItem("Edit");
		m_menuItemEdit.addActionListener(this);
		if (nodeItemInfo.isEditable())
			add (m_menuItemEdit);

		m_menuItemExpand = new JMenuItem("Expand");
		m_menuItemExpand.addActionListener(this);
		if (! nodeItemInfo.isExpanded() && nodeItemInfo.isExpandable())
			add (m_menuItemExpand);

		m_menuItemDecompile = new JMenuItem("Decompile");
		m_menuItemDecompile.addActionListener(this);
		if (nodeItemInfo.isJavaDecompilable())
			add (m_menuItemDecompile);

		m_menuItemSaveAs = new JMenuItem("Save As");
		m_menuItemSaveAs.addActionListener(this);
		add (m_menuItemSaveAs);

		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setCurrentDirectory (new File (System.getProperty ("user.dir")));
		System.out.println("<<< ExplorerPopupMenu::constructor");
	}

	public void actionPerformed (ActionEvent e) {
		System.out.println(">>> ExplorerPopupMenu::actionPerformed");
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem) (e.getSource());
			System.out.println( "Action event detected.\n" 
				+ "    Event source: " + source.getText()
				+ " (an instance of " + Utils.getClassName(source) + ")\n");
			String strNode = m_explorerTree.getNodeName();
			System.out.println("node "+strNode);
			NodeItemInfo nodeItemInfo = m_explorerTree.getNodeItemInfo();
			System.out.println("nodeItemInfo "+nodeItemInfo);

			if (source == m_menuItemEdit) {
				System.out.println("Edit request found");
				if (nodeItemInfo.isEditable()) {
					if (nodeItemInfo.isBrowser())
						Utils.browserFile (nodeItemInfo.getPath());
					else
						Utils.editFile (nodeItemInfo.getPath());
				}
			}
			else if (source == m_menuItemExpand) {
				System.out.println("Expand request found");
				if (! nodeItemInfo.isExpanded() && nodeItemInfo.isExpandable()) {
						m_explorerTree.getExplorerModel().expandNode (nodeItemInfo);
					}
			}
			else if (source == m_menuItemDecompile) {
				System.out.println("Decompile request found");
				if (nodeItemInfo.isJavaDecompilable()) {
					m_explorerTree.getExplorerModel().decompile(nodeItemInfo);
				}
			}
			else if (source == m_menuItemSaveAs) {
				System.out.println ("SaveAs request found");
	            int returnVal = m_fileChooser.showSaveDialog(this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = m_fileChooser.getSelectedFile();
	                System.out.println("save to directory is :"+file.getPath()+":");
	        		Explorer expander = new Explorer (m_explorerTree.getWorkingDirectory());
	        		NodeItemInfo item = expander.unPack (nodeItemInfo.getFile(), file);
	        		System.out.println("item "+item);
	            }
			}
		}
		System.out.println("<<< ExplorerPopupMenu::actionPerformed");
	}
}
