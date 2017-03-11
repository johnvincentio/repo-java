package com.idc.sudoku.gui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.idc.trace.LogHelper;

public class MyInputVerifier extends InputVerifier {
	@Override
	public boolean verify (JComponent input) {
		LogHelper.debug (">>> MyInputVerifier::verify");
		String text = ((JTextField) input).getText();
		LogHelper.debug ("text :"+text+":");
		try {
			Integer value = new Integer(text);
			if (value > 9) return false;
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}
