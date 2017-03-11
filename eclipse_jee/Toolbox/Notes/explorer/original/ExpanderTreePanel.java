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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.idc.trace.LogHelper;

public class ExpanderTreePanel extends JPanel 
			implements ActionListener, DropTargetListener, DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = 1;

	private DragSource m_dragSource;

	private ExplorerGUI m_app;

	private static final String newline = "\n";
	private JTree m_tree = null;
	private DefaultMutableTreeNode m_top;
	private MouseListener m_popupListener;
	private JPopupMenu m_popupAll;
	private JMenuItem m_menuItemStart;
	private JMenuItem m_menuItemStop;
	private JMenuItem m_menuItemLog1;
	private JMenuItem m_menuItemLog2;
	private JMenuItem m_menuItemLog3;
	private JMenuItem m_menuItemLog4;
	private JMenuItem m_menuItemLog5;
	private JMenuItem m_menuItemCreate;
	private JMenuItem m_menuItemDelete;
	private JMenuItem m_menuItemStatus;
	private JMenuItem m_menuItemFirstSteps;
	private JMenuItem m_menuItemConfig;
	private JMenuItem m_menuItemAdmin;

	private JMenu m_menuConfig;
	private JMenu m_menuConfigCell;
	private JMenuItem m_menuItemCell1;
	private JMenuItem m_menuItemCell2;
	private JMenuItem m_menuItemCell3;
	private JMenuItem m_menuItemCell4;
	private JMenuItem m_menuItemCell5;
	private JMenuItem m_menuItemCell6;
	private JMenuItem m_menuItemCell7;

	private JMenu m_menuConfigNodes;
	private JMenuItem m_menuItemNodes1;
	private JMenuItem m_menuItemNodes2;
	private JMenuItem m_menuItemNodes3;
	private JMenuItem m_menuItemNodes4;
	private JMenuItem m_menuItemNodes5;
	private JMenuItem m_menuItemNodes6;

	private JMenu m_menuConfigServer;
	private JMenuItem m_menuItemServer1;
	private JMenuItem m_menuItemServer2;
	private JMenuItem m_menuItemServer3;
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
	
	public ExpanderTreePanel (ExplorerGUI app, NodeItemInfo nodeItemInfo) {
		System.out.println("ServerTreePanel constructor");
		m_app = app;

		setBackground(Color.white);
		setLayout(new BorderLayout());

		m_popupAll = new JPopupMenu();
		m_menuItemStart = new JMenuItem("Start");
		m_menuItemStart.addActionListener(this);
		m_popupAll.add(m_menuItemStart);
		m_menuItemStop = new JMenuItem("Stop");
		m_menuItemStop.addActionListener(this);
		m_popupAll.add(m_menuItemStop);
		m_menuItemStatus = new JMenuItem("Status");
		m_menuItemStatus.addActionListener(this);
		m_popupAll.add(m_menuItemStatus);
		m_popupAll.addSeparator();

		m_menuItemFirstSteps = new JMenuItem("First Steps");
		m_menuItemFirstSteps.addActionListener(this);
		m_popupAll.add(m_menuItemFirstSteps);
		m_popupAll.addSeparator();

		m_menuItemLog1 = new JMenuItem("StartServer.log");
		m_menuItemLog1.addActionListener(this);
		m_popupAll.add(m_menuItemLog1);
		m_menuItemLog2 = new JMenuItem("StopServer.log");
		m_menuItemLog2.addActionListener(this);
		m_popupAll.add(m_menuItemLog2);
		m_menuItemLog3 = new JMenuItem("SystemOut.log");
		m_menuItemLog3.addActionListener(this);
		m_popupAll.add(m_menuItemLog3);
		m_menuItemLog4 = new JMenuItem("SystemErr.log");
		m_menuItemLog4.addActionListener(this);
		m_popupAll.add(m_menuItemLog4);
		m_menuItemLog5 = new JMenuItem("trace.log");
		m_menuItemLog5.addActionListener(this);
		m_popupAll.add(m_menuItemLog5);
		m_popupAll.addSeparator();

		m_menuItemCreate = new JMenuItem("Create Profile");
		m_menuItemCreate.addActionListener(this);
		m_popupAll.add(m_menuItemCreate);
		m_menuItemDelete = new JMenuItem("Delete Profile");
		m_menuItemDelete.addActionListener(this);
		m_popupAll.add(m_menuItemDelete);
		m_popupAll.addSeparator();

		m_menuItemConfig = new JMenuItem("Configuration");
		m_menuItemConfig.addActionListener(this);
		m_popupAll.add(m_menuItemConfig);
		m_menuItemAdmin = new JMenuItem("Admin Console");
		m_menuItemAdmin.addActionListener(this);
		m_popupAll.add(m_menuItemAdmin);
		m_popupAll.addSeparator();

		m_menuConfig = new JMenu("Config");
		m_menuConfigCell = new JMenu("Cell");
		m_menuConfig.add(m_menuConfigCell);
		m_menuItemCell1 = new JMenuItem("cell.xml");
		m_menuItemCell1.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell1);
		m_menuItemCell7 = new JMenuItem("namebindings.xml");
		m_menuItemCell7.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell7);
		m_menuItemCell2 = new JMenuItem("namestore.xml");
		m_menuItemCell2.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell2);
		m_menuItemCell3 = new JMenuItem("resources.xml");
		m_menuItemCell3.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell3);
		m_menuItemCell4 = new JMenuItem("security.xml");
		m_menuItemCell4.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell4);
		m_menuItemCell5 = new JMenuItem("variables.xml");
		m_menuItemCell5.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell5);
		m_menuItemCell6 = new JMenuItem("virtualhosts.xml");
		m_menuItemCell6.addActionListener(this);
		m_menuConfigCell.add(m_menuItemCell6);

		m_menuConfigNodes = new JMenu("Nodes");
		m_menuConfig.add(m_menuConfigNodes);
		m_menuItemNodes1 = new JMenuItem("namestore.xml");
		m_menuItemNodes1.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes1);
		m_menuItemNodes2 = new JMenuItem("node.xml");
		m_menuItemNodes2.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes2);
		m_menuItemNodes3 = new JMenuItem("resources.xml");
		m_menuItemNodes3.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes3);
		m_menuItemNodes4 = new JMenuItem("serverindex.xml");
		m_menuItemNodes4.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes4);
		m_menuItemNodes5 = new JMenuItem("systemapps.xml");
		m_menuItemNodes5.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes5);
		m_menuItemNodes6 = new JMenuItem("variables.xml");
		m_menuItemNodes6.addActionListener(this);
		m_menuConfigNodes.add(m_menuItemNodes6);

		m_menuConfigServer = new JMenu("Server");
		m_menuConfig.add(m_menuConfigServer);
		m_menuItemServer1 = new JMenuItem("resources.xml");
		m_menuItemServer1.addActionListener(this);
		m_menuConfigServer.add(m_menuItemServer1);
		m_menuItemServer2 = new JMenuItem("server.xml");
		m_menuItemServer2.addActionListener(this);
		m_menuConfigServer.add(m_menuItemServer2);
		m_menuItemServer3 = new JMenuItem("variables.xml");
		m_menuItemServer3.addActionListener(this);
		m_menuConfigServer.add(m_menuItemServer3);

		m_popupAll.add(m_menuConfig);

		m_top =	new DefaultMutableTreeNode(nodeItemInfo.getName());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			m_top.add (new DefaultMutableTreeNode(item.getName()));
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
			/*
			if (source == m_menuItemStart) {
				if (strNode != null) m_app.getServers().startServer (strNode);
			}
			else if (source == m_menuItemStop) {
				if (strNode != null) m_app.getServers().stopServer (strNode);
			}
			else if (source == m_menuItemStatus) {
				if (strNode != null) m_app.getServers().statusServer (strNode);
			}
			else if (source == m_menuItemFirstSteps) {
				if (strNode != null) m_app.getServers().firstSteps (strNode);
			}
			else if (source == m_menuItemLog1) {
				if (strNode != null) m_app.getServers().Log1Server (strNode);
			}
			else if (source == m_menuItemLog2) {
				if (strNode != null) m_app.getServers().Log2Server (strNode);
			}
			else if (source == m_menuItemLog3) {
				if (strNode != null) m_app.getServers().Log3Server (strNode);
			}
			else if (source == m_menuItemLog4) {
				if (strNode != null) m_app.getServers().Log4Server (strNode);
			}
			else if (source == m_menuItemLog5) {
				if (strNode != null) m_app.getServers().Log5Server (strNode);
			}
			else if (source == m_menuItemCreate) {
				if (strNode != null) m_app.getServers().CreateServer();
			}
			else if (source == m_menuItemDelete) {
				if (strNode != null) m_app.getServers().DeleteServer (strNode);
			}
			else if (source == m_menuItemConfig) {
				if (strNode != null) m_app.getServers().ConfigServer (strNode);
			}
			else if (source == m_menuItemIracJacl) {
				if (strNode != null) m_app.doServersJacl("irac.jacl",strNode);
			}
			else if (source == m_menuItemHercJacl) {
				if (strNode != null) m_app.doServersJacl("herc.jacl",strNode);
			}
			else if (source == m_menuItemAdmin) {
				if (strNode != null) m_app.getServers().AdminConsole (strNode);
			}
			else if ((source == m_menuItemCell1) || (source == m_menuItemCell2) ||
					(source == m_menuItemCell3) || (source == m_menuItemCell4) ||
					(source == m_menuItemCell5) || (source == m_menuItemCell6) ||
					(source == m_menuItemCell7)) {
				if (strNode != null) m_app.getServers().showConfigCellFile (strNode, source.getText());
			}
			else if ((source == m_menuItemNodes1) || (source == m_menuItemNodes2) ||
					(source == m_menuItemNodes3) || (source == m_menuItemNodes4) ||
					(source == m_menuItemNodes5) || (source == m_menuItemNodes6)) {
				if (strNode != null) m_app.getServers().showConfigNodesFile (strNode, source.getText());
			}
			else if ((source == m_menuItemServer1) || (source == m_menuItemServer2) || (source == m_menuItemServer3)) {
				if (strNode != null) m_app.getServers().showConfigServerFile (strNode, source.getText());
			}
			*/
		}
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
