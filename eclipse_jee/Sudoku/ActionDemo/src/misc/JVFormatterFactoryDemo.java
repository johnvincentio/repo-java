package misc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.*;

public class JVFormatterFactoryDemo extends JPanel implements PropertyChangeListener {
	// Values for the text fields
	private int numPeriods = 30;

	// Labels to identify the fields
	private JLabel numPeriodsLabel;
	private JLabel testLabel;

	// Strings for the labels
	private static String numPeriodsString = "Years: ";
	private static String testString = "Single Number: ";

	// Fields for data entry
	private JFormattedTextField numPeriodsField;
	private JFormattedTextField testField;


	public JVFormatterFactoryDemo() {
		super(new BorderLayout());

		// Create the labels.
		numPeriodsLabel = new JLabel(numPeriodsString);
		testLabel = new JLabel(testString);

		// Create the text fields and set them up.

		numPeriodsField = new JFormattedTextField();
		numPeriodsField.setValue(new Integer(numPeriods));
		numPeriodsField.setColumns(10);
		numPeriodsField.addPropertyChangeListener("value", this);

		MyFormatter testFormatter = new MyFormatter();
		testFormatter.setAllowsInvalid(false);

		testField = new JFormattedTextField(testFormatter);
		testField.setColumns(3);
		testField.addPropertyChangeListener("value", this);

		// Tell accessibility tools about label/textfield pairs.
		numPeriodsLabel.setLabelFor(numPeriodsField);
		testLabel.setLabelFor(testField);

		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(numPeriodsLabel);
		labelPane.add(testLabel);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));
		fieldPane.add(numPeriodsField);
		fieldPane.add(testField);

		// Put the panels in this panel, labels on left,
		// text fields on right.
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(labelPane, BorderLayout.CENTER);
		add(fieldPane, BorderLayout.LINE_END);
	}

	/** Called when a field's "value" property changes. */
	public void propertyChange(PropertyChangeEvent e) {
		Object source = e.getSource();
		if (source == numPeriodsField) {
			numPeriods = ((Number) numPeriodsField.getValue()).intValue();
		}
		else if (source == testField) {
			;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("FormatterFactoryDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add contents to the window.
		frame.add(new JVFormatterFactoryDemo());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
