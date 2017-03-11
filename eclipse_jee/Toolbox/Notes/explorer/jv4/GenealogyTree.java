package com.idc.explorer.jv4;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.idc.trace.LogHelper;

public class GenealogyTree extends JTree implements ActionListener {
	private static final long serialVersionUID = 1L;

	private GenealogyModel m_model;

	private MouseListener m_popupListener;
	private JPopupMenu m_popupAll;
	private JMenuItem m_menuItemEdit;
	private JMenuItem m_menuItemExpand;
	private JMenuItem m_menuItemDecompile;
	private JMenuItem m_menuItemSaveAs;

	private JFileChooser m_fileChooser;

	public GenealogyTree (File workingDirectory, File file) {
		super (new GenealogyModel (workingDirectory, file));
		System.out.println(">>> GenealogyTree");

		m_model = (GenealogyModel) getModel();

		getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
//		ToolTipManager.sharedInstance().registerComponent(this);

		setEditable (false);
		setShowsRootHandles (true);
		
		TreeCellRenderer renderer = new ToolTipTreeCellRenderer();
//		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//		Icon personIcon = null;
//		renderer.setLeafIcon(personIcon);
//		renderer.setClosedIcon(personIcon);
//		renderer.setOpenIcon(personIcon);
		setCellRenderer(renderer);

		m_popupAll = new JPopupMenu();
		m_menuItemEdit = new JMenuItem("Edit");
		m_menuItemEdit.addActionListener(this);
		m_popupAll.add(m_menuItemEdit);
		m_menuItemExpand = new JMenuItem("Expand");
		m_menuItemExpand.addActionListener(this);
		m_popupAll.add(m_menuItemExpand);
		m_menuItemDecompile = new JMenuItem("Decompile");
		m_menuItemDecompile.addActionListener(this);
		m_popupAll.add(m_menuItemDecompile);
		m_menuItemSaveAs = new JMenuItem("Save As");
		m_menuItemSaveAs.addActionListener(this);
		m_popupAll.add(m_menuItemSaveAs);

		m_popupListener = new PopupListener();
		addMouseListener (m_popupListener);

		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileSelectionMode (JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setCurrentDirectory (new File (System.getProperty ("user.dir")));

		System.out.println("<<< GenealogyTree");
	}

	public void actionPerformed (ActionEvent e) {
		System.out.println("actionPerformed");
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem) (e.getSource());
			System.out.println( "Action event detected.\n" 
				+ "    Event source: " + source.getText()
				+ " (an instance of " + getClassName(source) + ")\n");
			String strNode = getNodeName();
			System.out.println("node "+strNode);
			NodeItemInfo nodeItemInfo = getNodeItemInfo();
			System.out.println("nodeItemInfo "+nodeItemInfo);

			if (source == m_menuItemEdit) {
				System.out.println("Edit request found");
				if (nodeItemInfo.isEditable()) {
					if (nodeItemInfo.isBrowser())
						browserFile (nodeItemInfo.getPath());
					else
						editFile (nodeItemInfo.getPath());
				}
			}
			else if (source == m_menuItemExpand) {
				System.out.println("Expand request found");
				if (! nodeItemInfo.isExpanded() && nodeItemInfo.isExpandable())
					m_model.expandNode (nodeItemInfo);
			}
			else if (source == m_menuItemDecompile) {
				System.out.println("Decompile request found");
				if (nodeItemInfo.isJavaDecompilable()) {
					m_model.decompile(nodeItemInfo);
				}
			}
			else if (source == m_menuItemSaveAs) {
				System.out.println ("SaveAs request found");
	            int returnVal = m_fileChooser.showSaveDialog(this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = m_fileChooser.getSelectedFile();
	                System.out.println("save to directory is :"+file.getPath()+":");
	        		Expander expander = new Expander();
	        		NodeItemInfo item = expander.unPack (nodeItemInfo.getFile(), file);
	        		System.out.println("item "+item);
	            }
			}
		}
	}

	public void browserFile (final String strFile) {
		String[] strCmd = {GenealogyExample.BROWSER, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	private void editFile (final String strFile) {
		String[] strCmd = {GenealogyExample.EDITOR, strFile};		
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec (strCmd);
		}
		catch (IOException e) {
			LogHelper.info ("cannot run command "+strCmd);
		}
	}

	private NodeItemInfo getNodeItemInfo() {
		NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
		/*
		if (node != null) {
			if (node.isLeaf()) {
				nodeItemInfo = (NodeItemInfo) node.getUserObject();
			}
		}
		*/
		return node;
	}
	private String getNodeName() {
		NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
		/*
		if (node != null) {
			if (node.isLeaf()) {
				Object nodeInfo = node.getUserObject();
				strReturn = nodeInfo.toString();
			}
		}
		*/
		return node.getName();
	}
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex+1);
	}

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
		public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
		private void maybeShowPopup(MouseEvent e) {
			System.out.println(">>> PopupListener::maybeShowPopup");
			if (e.isPopupTrigger()) {
				NodeItemInfo node = (NodeItemInfo) getLastSelectedPathComponent();
				if (node == null) return;
				System.out.println("node :"+node+":");
//				if (node.isLeaf())
					m_popupAll.show(e.getComponent(), e.getX(), e.getY());
			}
			System.out.println("<<< PopupListener::maybeShowPopup");
		}
	}
}
/*
else if (source == m_menuItemSaveAs) {
	System.out.println ("SaveAs request found");
    m_button.setBackground(m_currentColor);
    m_colorChooser.setColor(m_currentColor);
    m_dialog.setVisible(true);
}
*/
