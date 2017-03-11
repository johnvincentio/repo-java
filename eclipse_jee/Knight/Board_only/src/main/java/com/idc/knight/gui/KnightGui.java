package com.idc.knight.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import com.idc.knight.xml.Scenario;

public class KnightGui extends JFrame implements ItemListener, DocumentListener {
	private static final long serialVersionUID = 3225080704519116487L;

	private static final String SOLUTIONS_DIR = "C:/jvDevelopment/repo_four/eclipse_jee/Knight_solutions";
//	private static final String SOLUTIONS_DIR = "C:/work/work202/new";

	private static final int MAX_X_SIZE = 8;
	private static final int MAX_Y_SIZE = 8;

	private JComboBox<Integer> m_comboXSize = new JComboBox<Integer>();
	private JComboBox<Integer> m_comboYSize = new JComboBox<Integer>();
	private JComboBox<Integer> m_comboXStart = new JComboBox<Integer>();
	private JComboBox<Integer> m_comboYStart = new JComboBox<Integer>();
	private BoardGui m_boardGui;
	private JTextField m_solution_number;
	private JTextField m_actual_solutions;
	private JLabel m_txtStatus;

	public static void main (String[] args) {(new KnightGui()).doApp();}

	private void doApp() {
		System.out.println(">>> KnightGui::doApp");
		setContentPane (makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStop();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);
		System.out.println("<<< KnightGui::doApp");
	}
	public void doStop() {System.exit(0);}

	private Container makeContentPane() {
		System.out.println(">>> KnightGui::makeContentPane");
		JPanel topPane = new JPanel();

		topPane.add (new JLabel ("X size"));
		for (int cnt = 3; cnt <= MAX_X_SIZE; cnt++)
			m_comboXSize.addItem (new Integer(cnt));
		m_comboXSize.setSelectedItem (new Integer(MAX_X_SIZE));
		m_comboXSize.setEnabled (true);
		m_comboXSize.addItemListener (this);
		topPane.add (m_comboXSize);

		topPane.add (new JLabel ("Y size"));
		for (int cnt = 3; cnt <= MAX_Y_SIZE; cnt++)
			m_comboYSize.addItem (new Integer(cnt));
		m_comboYSize.setSelectedItem (new Integer(MAX_Y_SIZE));
		m_comboYSize.setEnabled (true);
		m_comboYSize.addItemListener (this);
		topPane.add (m_comboYSize);

		topPane.add (new JLabel ("X start"));
		for (int cnt = 1; cnt <= MAX_X_SIZE; cnt++)
			m_comboXStart.addItem (new Integer(cnt));
		m_comboXStart.setSelectedItem (1);
		m_comboXStart.setEnabled (true);
		m_comboXStart.addItemListener (this);
		topPane.add (m_comboXStart);

		topPane.add (new JLabel ("Y start"));
		for (int cnt = 1; cnt <= MAX_Y_SIZE; cnt++)
			m_comboYStart.addItem (new Integer(cnt));
		m_comboYStart.setSelectedItem (1);
		m_comboYStart.setEnabled (true);
		m_comboYStart.addItemListener (this);
		topPane.add (m_comboYStart);

		topPane.add (new JLabel ("Solution"));
		m_solution_number = new JTextField(5);
		m_solution_number.setText ("0");
		m_solution_number.setEnabled (false);
		m_solution_number.addKeyListener (new MyKeyAdapter (this));
		m_solution_number.getDocument().addDocumentListener(this);
		((AbstractDocument) m_solution_number.getDocument()).setDocumentFilter (new NumberDocumentFilter());
		topPane.add (m_solution_number);

		topPane.add (new JLabel ("of"));
		m_actual_solutions = new JTextField(10);
		m_actual_solutions.setText ("0");
		m_actual_solutions.setEnabled (false);
		topPane.add (m_actual_solutions);

		JPanel midPane = new JPanel();		// panel for the board
		midPane.setLayout (new BorderLayout());
		m_boardGui = new BoardGui (SOLUTIONS_DIR, MAX_X_SIZE, MAX_Y_SIZE);
		midPane.add (m_boardGui, BorderLayout.CENTER);

		JPanel lowPane = new JPanel();
		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);

		JPanel pane = new JPanel();
		pane.setLayout (new BorderLayout());
		pane.add (topPane,BorderLayout.NORTH);
		pane.add (midPane,BorderLayout.CENTER);
		pane.add (lowPane,BorderLayout.SOUTH);

		createSolutions();

		System.out.println("<<< KnightGui::makeContentPane");
		return pane;
	}
	public long getTotalSolutions() {
		return Long.parseLong (m_actual_solutions.getText());
	}
	public String getSolutionNumberText() {
		return m_solution_number.getText();
	}
	public long getSolutionNumber() {
		return Long.parseLong (m_solution_number.getText());
	}

	public void itemStateChanged (ItemEvent event) {
//		System.out.println(">>> KnightGui::itemStateChanged");
		if (event.getStateChange() == ItemEvent.SELECTED) {
//			System.out.println("have ItemEvent.SELECTED event");
			Object source = event.getSource();
			if (source == m_comboXSize) {
				System.out.println("itemStateChanged::m_comboXSize");
				setBoard();
				setCombo (m_comboXStart, 1, (Integer) m_comboXSize.getSelectedItem(), 1, true);
			}
			else if (source == m_comboYSize) {
				System.out.println("itemStateChanged::m_comboYSize");
				setBoard();
				setCombo (m_comboYStart, 1, (Integer) m_comboYSize.getSelectedItem(), 1, true);
			}
			else if (source == m_comboXStart) {
				System.out.println("itemStateChanged::m_comboXStart");
				createSolutions();
			}
			else if (source == m_comboYStart) {
				System.out.println("itemStateChanged::m_comboYStart");
				createSolutions();
			}
			else {
//				System.out.println("itemStateChanged::found something else");
			}
		}
		else if (event.getStateChange() == ItemEvent.DESELECTED) {
//			System.out.println("have ItemEvent.DESELECTED event");
		}
//		System.out.println("<<< KnightGui::itemStateChanged");
	}
	public void changedUpdate (DocumentEvent event) {
//		System.out.println("--- KnightGui::changedUpdate");
		handleUpdatedSolution();
	}
	public void removeUpdate (DocumentEvent event) {
//		System.out.println("--- KnightGui::removeUpdate");
		handleUpdatedSolution();
	}
	public void insertUpdate (DocumentEvent event) {
//		System.out.println("--- KnightGui::insertUpdate");
		handleUpdatedSolution();
	}

	private void setCombo (final JComboBox<Integer> comboBox, final int from, final int to, final int def, final boolean enabled) {
//		System.out.println(">>> KnightGui::setCombo");
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					comboBox.removeAllItems();
					for (int cnt = from; cnt <= to; cnt++)
						comboBox.addItem (new Integer(cnt));
					comboBox.setSelectedItem (new Integer(def));
					comboBox.setEnabled (enabled);
					validate();
				}
			}
		);
//		System.out.println("<<< KnightGui::setCombo");
	}
	private void setBoard() {
//		System.out.println(">>> KnightGui::setBoard");
		final int x = (Integer) m_comboXSize.getSelectedItem();
		final int y = (Integer) m_comboYSize.getSelectedItem();
//		System.out.println("x "+x+" y "+y);
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_boardGui.createBoard (x, y);
					validate();
				}
			}
		);
//		System.out.println("<<< KnightGui::setBoard");
	}
	private void createSolutions() {
//		System.out.println(">>> KnightGui::createSolutions");
		int max_x = (Integer) m_comboXSize.getSelectedItem();
		int max_y = (Integer) m_comboYSize.getSelectedItem();
		int start_x = (Integer) m_comboXStart.getSelectedItem();
		int start_y = (Integer) m_comboYStart.getSelectedItem();

		Scenario scenario = GuiUtils.getScenario (SOLUTIONS_DIR, max_x, max_y, start_x, start_y);
		if (scenario == null || scenario.getSolutions() < 1) {
			m_actual_solutions.setText ("0");
			m_solution_number.setText ("0");
			m_solution_number.setEnabled (false);
		}
		else {
			m_actual_solutions.setText (Long.toString (scenario.getSolutions()));
			m_solution_number.setText (Long.toString (1L));
			m_solution_number.setEnabled (true);
		}
//		System.out.println("<<< KnightGui::createSolutions");
	}

	private void handleUpdatedSolution() {
//		System.out.println(">>> KnightGui::handleUpdatedSolution");
		int start_x = (Integer) m_comboXStart.getSelectedItem();
		int start_y = (Integer) m_comboYStart.getSelectedItem();
		m_boardGui.handleSolution (start_x, start_y, m_solution_number.getText());
//		System.out.println("<<< KnightGui::handleUpdatedSolution");
	}
}
