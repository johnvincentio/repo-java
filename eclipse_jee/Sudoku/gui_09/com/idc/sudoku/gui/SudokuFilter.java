package com.idc.sudoku.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class SudokuFilter extends FileFilter {

	public boolean accept(File f) {
//		LogHelper.debug("--- SudokuFilter::accept; file "+f.getPath());
		if (f.isDirectory()) return true;
		return f.getName().toLowerCase().endsWith("sudoku");
	}

	public String getDescription() {
		return "Sudoku Files (*.sudoku)";
	}
}
