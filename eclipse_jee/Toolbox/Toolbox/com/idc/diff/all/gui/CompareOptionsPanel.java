package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class CompareOptionsPanel extends JPanel implements ItemListener {
	private static final long serialVersionUID = 1;

	private JCheckBox m_chkCompareFiles;
	private JCheckBox m_chkDeleteIdentical;
	private JCheckBox m_chkShowDifferences;
	private JCheckBox m_chkIgnoreWhiteSpace;
	public Options getOptions() {return new Options();}

	public CompareOptionsPanel() {
		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Compare Options"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));
		paneOptions.setLayout (new GridLayout (2, 2, 0, 0));
		m_chkCompareFiles = new JCheckBox ("Compare");
		m_chkCompareFiles.setToolTipText ("On will compare files.");
		m_chkCompareFiles.addItemListener(this);

		m_chkDeleteIdentical = new JCheckBox ("Delete if identical");
		m_chkDeleteIdentical.setToolTipText ("On will delete files if identical.");
		m_chkDeleteIdentical.addItemListener(this);

		m_chkShowDifferences = new JCheckBox ("Show Differences");
		m_chkShowDifferences.setToolTipText ("On will show differences if found to be different.");
		m_chkShowDifferences.addItemListener(this);

		m_chkIgnoreWhiteSpace = new JCheckBox ("Ignore All White Space");
		m_chkIgnoreWhiteSpace.setToolTipText ("The compare will ignore all white space.");
		m_chkIgnoreWhiteSpace.addItemListener(this);

		paneOptions.add (m_chkCompareFiles);
		paneOptions.add (m_chkDeleteIdentical);
		paneOptions.add (m_chkShowDifferences);
		paneOptions.add (m_chkIgnoreWhiteSpace);

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}

	public void itemStateChanged (ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			m_chkCompareFiles.setSelected (true);
		}
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			Object source = e.getItemSelectable();
			if (source == m_chkCompareFiles) {
				m_chkDeleteIdentical.setSelected (false);
				m_chkShowDifferences.setSelected (false);
				m_chkIgnoreWhiteSpace.setSelected (false);
			}
		}
	}

	public class Options {
		public boolean isCompareFiles() {return m_chkCompareFiles.isSelected();}
		public boolean isDeleteIdentical() {return m_chkDeleteIdentical.isSelected();}
		public boolean isShowDifferences() {return m_chkShowDifferences.isSelected();}
		public boolean isIgnoreWhiteSpace() {return m_chkIgnoreWhiteSpace.isSelected();}
	}
}
