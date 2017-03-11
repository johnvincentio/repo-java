package com.idc.diff.file;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.idc.file.exec.ExecuteCommand;
import com.idc.swing.JVMessagesArea;
import com.idc.utils.JVString;

public class Appdiff extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6777754804746588106L;

	private Appfile m_appFile1, m_appFile2;
	private JButton m_btnDiff;

	private JVMessagesArea m_messagesArea;

	public Appdiff (Appfile a1, Appfile a2) {m_appFile1 = a1; m_appFile2 = a2;}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		m_btnDiff = new JButton("Compare");
		m_btnDiff.addActionListener(this);
		topPane.add(m_btnDiff);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane (50, 35);
//		m_textArea = new JTextArea(50,35);
		getMessagesArea().getTextArea().setLineWrap(false);
		getMessagesArea().getTextArea().setEditable(false);
		getMessagesArea().getTextArea().setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder(5,5,5,5), getMessagesArea().getTextArea().getBorder()));

		JScrollPane scroll = new JScrollPane (getMessagesArea().getTextArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		midPane.add(scroll,	BorderLayout.CENTER);
		midPane.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder(5,5,0,0),midPane.getBorder()));//t,l,b,r

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		return pane;
	}
	public Dimension getPreferredSize() {return new Dimension(400, 800);}
	public Dimension getMinimumSize() {return getPreferredSize();}

	public JVMessagesArea getMessagesArea() {return m_messagesArea;}
	public void handleLine (final String line) {
		String str = line;
		JVString.replace (str, "\t","	");
		m_messagesArea.add (str);
	}
	public void clearMessagesArea () {
		m_messagesArea.clear();
	}

	public void close() {}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			if (source == m_btnDiff) {
				clearMessagesArea();
				compareSelected();
			}			
		}
		else
			System.out.println("else type");
	}
	private void compareSelected() {
//		Diff diff = new Diff (this);
		String file1 = m_appFile1.getCompareFilename();
		String file2 = m_appFile2.getCompareFilename();
		System.out.println ("m_appFile1.isCompareFilename "+m_appFile1.isCompareFilename());
		System.out.println ("m_appFile2.isCompareFilename "+m_appFile2.isCompareFilename());
		if (! m_appFile1.isCompareFilename()) file1 = m_appFile1.handlePastedData().getPath();
		if (! m_appFile2.isCompareFilename()) file2 = m_appFile2.handlePastedData().getPath();
		System.out.println ("file1 :"+file1+":");
		System.out.println ("file2 :"+file2+":");
		doCompare (file1, file2, false);
//		diff.doDiff (file1, file2);
	}

	private void doCompare (String baseFile, String newFile, boolean bIgnoreWhiteSpace) {
		System.out.println (">>> doCompare; baseFile :"+baseFile+": newFile :"+newFile+":");		
		ExecuteCommand exec = new ExecuteCommand();
		if (ExecuteCommand.isWindows()) {
			System.out.println("Windows compare");
			if (bIgnoreWhiteSpace) {
				String[] strCmd = { "fc", "/W", "/N", baseFile, newFile };
				exec.executeCommand (strCmd, m_messagesArea);
			} else {
				String[] strCmd = { "fc", "/N", baseFile, newFile };
				exec.executeCommand (strCmd, m_messagesArea);
			}
			exec = null;
		}
		else {
			if (bIgnoreWhiteSpace) {
				System.out.println("Unix compare");
				String[] strCmd = { "diff", "-w", "--strip-trailing-cr", baseFile, newFile };
				exec.executeCommand (strCmd, m_messagesArea);
			} else {
				String[] strCmd = { "diff", "--strip-trailing-cr", baseFile, newFile };
				exec.executeCommand (strCmd, m_messagesArea);
			}
			exec = null;
		}
		System.out.println ("<<< doCompare");
	}
}
