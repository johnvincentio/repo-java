package net.sf.jdec.ui.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

class WideCaret extends DefaultCaret
{
	protected Highlighter.HighlightPainter getSelectionPainter() {
		return new hl();
	 }
	
	class hl extends DefaultHighlightPainter //implements Highlighter.HighlightPainter
	{
		hl()
		{
			super(Color.BLUE);
		}
		
		public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
			g.setColor(Color.BLUE);
			super.paint(g,p0,p1,bounds,c);
		}
		
	}
}
