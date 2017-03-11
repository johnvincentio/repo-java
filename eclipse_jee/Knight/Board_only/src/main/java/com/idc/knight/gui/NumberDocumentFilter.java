package com.idc.knight.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

class NumberDocumentFilter extends DocumentFilter {

	@Override
	public void insertString (DocumentFilter.FilterBypass fp, int offset, String string, AttributeSet aset) throws BadLocationException {
//		System.out.println(">>> NumberDocumentFilter::insertString; string :"+string+":");
		if (isValid (string))
			super.insertString (fp, offset, string, aset);
//		System.out.println("<<< NumberDocumentFilter::insertString");
	}

	@Override
	public void replace (DocumentFilter.FilterBypass fp, int offset, int length, String string, AttributeSet aset) throws BadLocationException {
//		System.out.println(">>> NumberDocumentFilter::replace; string :"+string+":");
		if (isValid (string))
			super.replace (fp, offset, length, string, aset);
//		System.out.println("<<< NumberDocumentFilter::replace");
	}

	private boolean isValid (String string) {
//		System.out.println(">>> NumberDocumentFilter::isValid; string :"+string+":");
		for (int i = 0; i < string.length(); i++) {
			if (! Character.isDigit (string.charAt(i))) {
//				System.out.println("<<< NumberDocumentFilter::isValid = false");
				return false;
			}
		}
//		System.out.println("<<< NumberDocumentFilter::isValid = true");
		return true;
	}
}
