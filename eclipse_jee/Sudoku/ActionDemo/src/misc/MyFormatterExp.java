package misc;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyFormatterExp extends JFrame {

	public MyFormatterExp() {

		setTitle("Example: Custom Formatter");
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Number :");
		JFormattedTextField tf = new JFormattedTextField(new MyFormatter());
		tf.setColumns(10);
		panel.add(label);
		panel.add(tf);
		JButton button = new JButton("Click Me");
		panel.add(button);
		getContentPane().add(panel, BorderLayout.SOUTH);
		pack();

	}

	public static void main(String[] args) {
		MyFormatterExp mfe = new MyFormatterExp();
		mfe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mfe.setVisible(true);
	}
}
