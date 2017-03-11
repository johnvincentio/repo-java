package com.idc.explorer.original;

/**
 * @author John Vincent
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.idc.trace.LogHelper;

public class Expander2TreePanel extends JPanel 
			implements ActionListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1;

	private DragSource m_dragSource;

	private Explorer2GUI m_app;

	private static final String newline = "\n";
	private JTree m_tree = null;
	private DefaultMutableTreeNode m_top;
	private MouseListener m_popupListener;
	private JPopupMenu m_popupAll;
	private JMenuItem m_menuItemEdit;
	private JMenuItem m_menuItemOpen;

/*
	lbl.setTransferHandler(new TransferHandler("text"));
    MouseListener ml = new MouseAdapter(){
      public void mousePressed(MouseEvent e){
        JComponent jc = (JComponent)e.getSource();
        TransferHandler th = jc.getTransferHandler();
        th.exportAsDrag(jc, e, TransferHandler.COPY);
      }
    };
    lbl.addMouseListener(ml);
	*/
	
	public Expander2TreePanel (Explorer2GUI app, NodeItemInfo nodeItemInfo) {
		System.out.println("Expander2TreePanel constructor");
		m_app = app;

		setBackground(Color.white);
		setLayout(new BorderLayout());

		m_popupAll = new JPopupMenu();
		m_menuItemEdit = new JMenuItem("Edit");
		m_menuItemEdit.addActionListener(this);
		m_popupAll.add(m_menuItemEdit);
		m_menuItemOpen = new JMenuItem("Open");
		m_menuItemOpen.addActionListener(this);
		m_popupAll.add(m_menuItemOpen);

		m_top =	new DefaultMutableTreeNode(nodeItemInfo.getName());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
//			m_top.add (new DefaultMutableTreeNode(item.getName()));
			m_top.add (new DefaultMutableTreeNode(item));
		}

		m_tree = new JTree (m_top);
		m_tree.setEditable (false);
		m_tree.setShowsRootHandles (true);
		m_tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_popupListener = new PopupListener();
		m_tree.addMouseListener (m_popupListener);
		new DropTarget (m_tree, DnDConstants.ACTION_COPY_OR_MOVE, this);

		m_dragSource = new DragSource();
		int actions = DnDConstants.ACTION_COPY_OR_MOVE;
		m_dragSource.createDefaultDragGestureRecognizer (m_tree, actions, this);

		JScrollPane scrollPane = new JScrollPane(m_tree);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportBorder(BorderFactory.createEtchedBorder());
		add(scrollPane,BorderLayout.CENTER);
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed");
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem source = (JMenuItem)(e.getSource());
			String s = "Action event detected."
				+ newline
				+ "    Event source: " + source.getText()
				+ " (an instance of " + getClassName(source) + ")";
			System.out.println(s + newline);
			String strNode = getNodeName();
			System.out.println("node "+strNode);
			NodeItemInfo nodeItemInfo = getNodeItemInfo();
			System.out.println("nodeItemInfo "+nodeItemInfo);

			if (source == m_menuItemEdit) {
				System.out.println("Edit request found");
//				if (strNode != null) m_app.getServers().startServer (strNode);
			}
			else if (source == m_menuItemOpen) {
				System.out.println("Open request found");
//				if (strNode != null) m_app.getServers().startServer (strNode);
			}
		}
	}

	private NodeItemInfo getNodeItemInfo() {
		NodeItemInfo nodeItemInfo = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
		if (node != null) {
			if (node.isLeaf()) {
				nodeItemInfo = (NodeItemInfo) node.getUserObject();
			}
		}
		return nodeItemInfo;
	}
	private String getNodeName() {
		String strReturn = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
		if (node != null) {
			if (node.isLeaf()) {
				Object nodeInfo = node.getUserObject();
				strReturn = nodeInfo.toString();
			}
		}
		return strReturn;
	}
	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex+1);
	}

	public synchronized void drop (DropTargetDropEvent e) {
		LogHelper.info(">>> drop");
		try {
			Transferable tr = e.getTransferable();
			if (tr.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				java.util.List fileList = (java.util.List) tr.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator iterator = fileList.iterator();
				while (iterator.hasNext()) {
					File file = (File) iterator.next();
					System.out.println("filename :"+file.getPath()+":");
					m_app.remakeContentPane (file);
				}
				e.getDropTargetContext().dropComplete(true);
			} else {
				System.err.println ("Rejected");
				e.rejectDrop();
			}
		}
		catch (IOException io) {
			io.printStackTrace();
			e.rejectDrop();
		}
		catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
			e.rejectDrop();
		}
		LogHelper.info ("<<< drop");
	}
	public void dragEnter(DropTargetDragEvent e) {
		LogHelper.info("--- Abc:dragEnter; DropTargetDragEvent");
	}
	public void dragExit(DropTargetEvent e) {
		LogHelper.info("--- Abc:dragExit; DropTargetEvent");
	}
	public void dragOver(DropTargetDragEvent e) {
		LogHelper.info("--- Abc:dragOver; DropTargetDragEvent");
	}
	public void dropActionChanged(DropTargetDragEvent e) {
		LogHelper.info("--- Abc:dropActionChanged; DropTargetDragEvent");
	}
	public void dragEnter(DragSourceDragEvent dsde) {
		LogHelper.info("--- Abc:dragEnter; DragSourceDragEvent");
	}

	public void dragExit(DragSourceEvent dse) {
		LogHelper.info("--- Abc:dragExit; DragSourceEvent");
	}
	public void dragOver(DragSourceDragEvent dsde) {
		LogHelper.info("--- Abc:dragOver; DragSourceDragEvent");
	}

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
		public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
				if (node == null) return;
				if (node.isLeaf())
					m_popupAll.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	public void dragGestureRecognized_99 (DragGestureEvent evt) {
		LogHelper.info(">>> Abc:dragGestureRecognized; DragGestureEvent");
		Transferable t = new StringSelection("aString");
		m_dragSource.startDrag (evt, DragSource.DefaultCopyDrop, t, this);
		LogHelper.info("<<< Abc:dragGestureRecognized; DragGestureEvent");
	}

	public void dragGestureRecognized (DragGestureEvent dge) {
		LogHelper.info(">>> Abc:dragGestureRecognized");
//		TreePath path = m_tree.getSelectionPath();
//		if ((path == null) || (path.getPathCount() <= 1)) {
//			return;//    	 We can't move the root node or an empty selection
//		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
		Object obj = node.getUserObject();
		System.out.println(" obj "+obj);
		TreePath path = m_tree.getSelectionPath();
		Object obj1 = path.getLastPathComponent();
		System.out.println(" obj1 "+obj1);

//		DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) m_tree.get;
//		NodeItemInfo nodeItemInfo = (NodeItemInfo) node.getUserObject();
//		NodeItemInfo.show (0, nodeItemInfo);
//    	oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//   	TransferableTreeNode transferable = new TransferableTreeNode(path);
//    	dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);
		LogHelper.info("<<< Abc:dragGestureRecognized");
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		System.out.println("Action: " + dsde.getDropAction());
		System.out.println("Target Action: " + dsde.getTargetActions());
		System.out.println("User Action: " + dsde.getUserAction());
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		System.out.println("Drop Action: " + dsde.getDropAction());
	}
}

/*
		  public void dropActionChanged(DragSourceDragEvent dsde) {
		    System.out.println("Action: " + dsde.getDropAction());
		    System.out.println("Target Action: " + dsde.getTargetActions());
		    System.out.println("User Action: " + dsde.getUserAction());
		  }

		  public void dragDropEnd(DragSourceDropEvent dsde) {
		    System.out.println("Drop Action: " + dsde.getDropAction());
		    if (dsde.getDropSuccess()
		        && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
		      ((DefaultTreeModel) m_tree.getModel()).removeNodeFromParent(oldNode);
		    }
		  }

		 
			class TransferableTreeNode implements Transferable {

				  public DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
				      "Tree Path");

				  DataFlavor flavors[] = { TREE_PATH_FLAVOR };

				  TreePath path;

				  public TransferableTreeNode(TreePath tp) {
				    path = tp;
				  }

				  public synchronized DataFlavor[] getTransferDataFlavors() {
				    return flavors;
				  }

				  public boolean isDataFlavorSupported(DataFlavor flavor) {
				    return (flavor.getRepresentationClass() == TreePath.class);
				  }

				  public synchronized Object getTransferData(DataFlavor flavor)
				      throws UnsupportedFlavorException, IOException {
				    if (isDataFlavorSupported(flavor)) {
				      return (Object) path;
				    } else {
				      throw new UnsupportedFlavorException(flavor);
				    }
				  }
				}
*/
