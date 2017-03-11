package com.idc.explorer.original;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.idc.trace.LogHelper;

public class JVApp extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private static final String m_strWorkDir = "c:/jvExplorer";

	private JTree m_tree = null;
	private DefaultMutableTreeNode m_top;

	public JVApp (String msg, String[] args) {
		super(msg);
//		m_grepdir = new Grepdir(this);
		setContentPane(makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);	
	}
	public static void main(String[] args) {
		new JVApp("JVApp", args);
	}

	private Container makeContentPane() {

		String strJarFile = "c:/tmp/1/cleanimports.jar";
//		NodeItemInfo nodeItemInfo = JVJar.listJar(strJarFile, m_strWorkDir);

		Expander expander = new Expander (new File (m_strWorkDir));
		String strZipFile = "c:/tmp/1/Auction.zip";
		NodeItemInfo nodeItemInfo = expander.listZip (new File(strZipFile));

		m_top =	new DefaultMutableTreeNode(nodeItemInfo.getName());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			m_top.add (new DefaultMutableTreeNode(item.getName()));
		}

		m_tree = new JTree(m_top);
//		m_tree.setEditable(false);
//		m_tree.setShowsRootHandles(true);
//		m_tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
//		m_popupListener = new PopupListener();
//		m_tree.addMouseListener(m_popupListener);

		JPanel middlePane = new JPanel();
		middlePane.setLayout(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane(m_tree);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollPane.setViewportBorder(BorderFactory.createEtchedBorder());
//		scrollPane.setBounds(new Rectangle(3, 3, 239, 220));		// x,y, width, height
		middlePane.add (scrollPane, BorderLayout.CENTER);
//		middlePane.add (m_tree);
		return middlePane;
		/*
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(middlePane,BorderLayout.CENTER);
		return pane;
		*/
	}	

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			LogHelper.info("textfield");
//			searchSelected();
		}
		else if (source instanceof JButton) {
			LogHelper.info("jbutton");
		}
		else
			LogHelper.info("else type");
	}

	public void doStopClient() {
		/*
		if (m_appThread != null) {
			m_appThread.setStop();
			try {
				while (m_appThread.isAlive()) {
					LogHelper.info("thread is alive");
					Thread.sleep(10);
					LogHelper.info("Sleeping");
				}
				LogHelper.info("thread is not alive");
			}
			catch (InterruptedException e) {
				LogHelper.info("no sleep");
			}
		}
		m_appThread = null;
		*/
		LogHelper.info("exiting app...");
		System.exit(0);
	}
}

/*
		m_fileChooser = new JFileChooser();
//		m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		String strCwd = System.getProperty("user.dir");
		String strCwd = getDirField();
		m_fileChooser.setCurrentDirectory(new File(strCwd));

		JPanel paneB = new JPanel();
		paneB.add(m_comboDirs);		
		paneB.add(m_btnDir);
		paneB.add(m_dirField);		

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(paneA, BorderLayout.NORTH);
		topPane.add(paneB, BorderLayout.SOUTH);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		midPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		m_messagesArea = new JTextArea(40,80);
		m_messagesArea.setEditable(false);		
		m_messagesArea.setDragEnabled(true);	
		midPane.add(new JScrollPane(m_messagesArea),BorderLayout.CENTER);
		
		JPanel lowPane = new JPanel();
		m_btnClearText = new JButton("Clear Text");
		m_btnClearText.addActionListener(this);
		lowPane.add(m_btnClearText);
		m_btnApp = new JButton("Search");
		m_btnApp.setDefaultCapable(true);
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);
						
		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JProgressBar();
		lowPane.add(m_progress);
	
		m_editField = new JTextField(25);
		m_editField.addActionListener(this);
		m_btnEdit = new JButton("Edit");
		m_btnEdit.addActionListener(this);
		m_btnClear = new JButton("Clear");
		m_btnClear.addActionListener(this);		
		lowPane.add(m_editField);		
		lowPane.add(m_btnEdit);
		lowPane.add(m_btnClear);	
*/
