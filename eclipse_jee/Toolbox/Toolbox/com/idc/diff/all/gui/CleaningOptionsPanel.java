package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class CleaningOptionsPanel extends JPanel implements ItemListener {
	private static final long serialVersionUID = -4189527450368947501L;

	private JCheckBox m_chkCleaning;
	private JCheckBox m_chkDeleteListedAsDelete;
	private JCheckBox m_chkDeleteListedAsIgnore;
	public Options getOptions() {return new Options ();}

	public CleaningOptionsPanel() {
		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Cleaning Options"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));
		paneOptions.setLayout (new GridLayout (1, 2, 0, 0));

		m_chkCleaning = new JCheckBox ("Perform cleaning");
		m_chkCleaning.setToolTipText ("On will perform cleaning.");
		paneOptions.add (m_chkCleaning);
		m_chkCleaning.addItemListener(this);

		m_chkDeleteListedAsDelete = new JCheckBox ("Delete Listed As Delete");
		m_chkDeleteListedAsDelete.setToolTipText ("On will delete files of listed as Delete Extensions and Delete Directories.");
		paneOptions.add (m_chkDeleteListedAsDelete);
		m_chkDeleteListedAsDelete.addItemListener(this);

		m_chkDeleteListedAsIgnore = new JCheckBox ("Delete Listed As Ignore");
		m_chkDeleteListedAsIgnore.setToolTipText ("On will delete files of type not listed as Compare Extensions.");
		paneOptions.add (m_chkDeleteListedAsIgnore);
		m_chkDeleteListedAsIgnore.addItemListener(this);

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}

	public void itemStateChanged (ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			m_chkCleaning.setSelected (true);
		}
		else if (e.getStateChange() == ItemEvent.DESELECTED) {
			Object source = e.getItemSelectable();
			if (source == m_chkCleaning) {
				m_chkDeleteListedAsDelete.setSelected (false);
				m_chkDeleteListedAsIgnore.setSelected (false);
			}
		}
	}

	public class Options {
		public boolean isCleaning() {return m_chkCleaning.isSelected();}
		public boolean isDeleteListedAsDelete() {return m_chkDeleteListedAsDelete.isSelected();}
		public boolean isDeleteListedAsIgnore() {return m_chkDeleteListedAsIgnore.isSelected();}
	}
}
