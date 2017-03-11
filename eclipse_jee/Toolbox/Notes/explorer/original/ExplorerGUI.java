package com.idc.explorer.original;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.idc.grepgui.AppThread;
import com.idc.swing.JVLabel;
import com.idc.swing.JVMessagesArea;
import com.idc.swing.progress.JVProgressBar;
import com.idc.trace.LogHelper;

/*
 * TODO; Dnd to the file text field
 * DnD from JTree to Explorer
 * Open, Edit, Expand from leaf
 * Need tree to be dynamic
 * Write messages to messagePanel
 * Need progress indicator?
 * Do we still need the submit button?
 */
public class ExplorerGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;

	private String m_strWorkDir = "c:/jvExplorer";

	private JTextField m_filename;

	private JButton m_btnApp;
	private JTextField m_txtStatus;
	private JVProgressBar m_progress;
	private JVMessagesArea m_messagesArea;

	private Expander m_expander;
	private AppThread m_appThread = null;

	public ExplorerGUI (String msg, String[] args) {
		super(msg);
//		if (args.length > 0) strWorkDir = args[0];		// TODO; this should be the file to expand
//		System.out.println("strWorkDir :"+strWorkDir+":");
		
		String strJarFile = "c:/tmp/1/connector.jar";
		String strZipFile = "c:/tmp/1/Auction.zip";
		String strGzFile = "c:/tmp/1/xml-commons-external-1.2.01-src.tar.gz";
		String strTarFile = "c:/tmp/1/abc.tar";

		setContentPane (makeContentPane (new File (strJarFile)));
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);	
	}
	public void remakeContentPane (File file) {
		System.out.println(">>> remakeContentPane");
//		setContentPane (makeContentPane (file));
		getContentPane().removeAll();
		getContentPane().add (makeContentPane (file));
		pack();
		validate();
		repaint();
		System.out.println("<<< remakeContentPane");
	}

	public AppThread getAppThread() {return m_appThread;}
	public static void main(String[] args) {
		new ExplorerGUI("ExplorerGUI", args);
	}

	private Container makeContentPane(File file) {
		m_expander = new Expander (new File (m_strWorkDir));

		JPanel topPane = new JPanel();
		topPane.add ((new JVLabel ("File")).getLabel());
		m_filename = new JTextField(50);
		topPane.add (m_filename);
		m_btnApp = new JButton ("Submit");
		m_btnApp.addActionListener(this);
		topPane.add (m_btnApp);

		JPanel paneA = new JPanel();
		paneA.setLayout (new BorderLayout());
		m_messagesArea = new JVMessagesArea();

		paneA.setLayout(new BorderLayout());
		paneA.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane(5, 80);
		m_messagesArea.getTextArea().setEditable(false);
		m_messagesArea.getTextArea().setDragEnabled(false);
		paneA.add(new JScrollPane(m_messagesArea.getTextArea()),BorderLayout.CENTER);

		JPanel paneB = new JPanel();
		m_progress = new JVProgressBar();
		m_txtStatus = new JTextField(30);
		paneB.add (m_progress);
		paneB.add (m_txtStatus);

		JPanel bottomPane = new JPanel();
		bottomPane.setLayout (new BorderLayout());
		bottomPane.add (paneA, BorderLayout.CENTER);
		bottomPane.add (paneB, BorderLayout.SOUTH);

		NodeItemInfo nodeItemInfo = m_expander.unPack (file);
/*
		String strJarFile = "c:/tmp/1/cleanimports.jar";
//		NodeItemInfo nodeItemInfo = JVJar.listJar(strJarFile);

		String strZipFile = "c:/tmp/1/Auction.zip";
		NodeItemInfo nodeItemInfo = m_expander.unZip (new File (strZipFile));

		String strGzFile = "c:/tmp/1/xml-commons-external-1.2.01-src.tar.gz";
//		NodeItemInfo nodeItemInfo = Expander.listGz (strGzFile);

//		String strTarFile = "c:/tmp/1/abc.tar";
//		NodeItemInfo nodeItemInfo = Expander.listTar (strTarFile);
*/
		NodeItemInfo.show (0, nodeItemInfo);

		/*
		m_top =	new DefaultMutableTreeNode(nodeItemInfo.getName());
		for (Iterator iter = nodeItemInfo.getNodeInfo().getItems(); iter.hasNext(); ) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			m_top.add (new DefaultMutableTreeNode(item.getName()));
		}

		m_tree = new JTree(m_top);
		m_tree.setEditable(false);
		m_tree.setShowsRootHandles(true);
		m_tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
//		m_popupListener = new PopupListener();
//		m_tree.addMouseListener(m_popupListener);
*/
/*
		JPanel middlePane = new JPanel();
		middlePane.setLayout(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane (m_tree);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportBorder(BorderFactory.createEtchedBorder());
//		scrollPane.setBounds(new Rectangle(3, 3, 239, 220));		// x,y, width, height
		middlePane.add (scrollPane,BorderLayout.CENTER);
*/
		ExpanderTreePanel expanderTreePanel = new ExpanderTreePanel (this, nodeItemInfo);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(topPane,BorderLayout.NORTH);
		pane.add(expanderTreePanel,BorderLayout.CENTER);
		pane.add(bottomPane,BorderLayout.SOUTH);			
		return pane;
	}	

	public void doApp() {

	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			LogHelper.info("textfield");
//			searchSelected();
		}
		else if (source instanceof JButton) {
			LogHelper.info("jbutton");
			if (source == m_btnApp) {
				LogHelper.info("btnApp");
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				/*
				if (strBtn.equals("Search"))
					searchSelected();
				else if (strBtn.equals("Stop")) {
					m_appThread.setStop();
					setStopped();
				}
				*/							
			}
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
	public void setStopped() {
//		setButtonText(true);
//		setEditButtonActive(true);
//		setDirButtonActive(true);
//		setClearButtonActive(true);				
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
