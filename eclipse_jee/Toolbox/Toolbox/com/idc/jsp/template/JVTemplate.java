
package com.idc.jsp.template;

public class JVTemplate {
	private String m_strTitle;
	private String m_strComment;	// for programmers comment

	private JVHeader m_header = null;
	private JVFooter m_footer = null;
	private JVBody m_body = null;
	private JVMenu m_menu = null;

	public JVTemplate() {
		m_header = new JVHeader ();
		m_footer = new JVFooter ();
		m_body = new JVBody ();
		m_menu = new JVMenu ();
	}

	public String getTitle() {return m_strTitle;}
	public String getComment() {return m_strComment;}
	public void setTitle (String str) {m_strTitle = str;}
	public void setComment (String str) {m_strComment = str;}

	public JVHeader getHeader() {return m_header;}
	public JVFooter getFooter() {return m_footer;}
	public JVBody getBody() {return m_body;}
	public JVMenu getMenu() {return m_menu;}
}
