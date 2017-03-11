
/*
 * FindDialog.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.sf.jdec.ui.util;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

public class FindDialog extends JDialog {
    
    private int start = -1;
    private int finish = -1;
    private JFrame myowner;
    private JTabbedPane tbp;
    private JTextField findText;
    
    private ButtonModel findUp;
    private ButtonModel findDown;
    private int searchIndex = -1;
    private boolean toSearchUp = false;
    private java.lang.String searchData="";
    
    public JTextField getFindText() {
        return findText;
    }
    
    
    public FindDialog(JFrame owner) {
        super(owner, "Jdec Find Window", false);
        myowner = owner;
        tbp = new JTabbedPane();
        
        JPanel p1 = new JPanel(new BorderLayout());
        JPanel p2 = new JPanel(new BorderLayout());
        JPanel p3 = new JPanel();
        
        p3.setLayout(new FlowLayout());
        p3.add(new JLabel("Text-->"));
        findText = new JTextField();
        findText.setColumns(20);
        p3.add(findText);
        p2.add(p3, BorderLayout.CENTER);
        JPanel p4 = new JPanel(new GridLayout(6, 2));
        ButtonGroup butgrp = new ButtonGroup();
        JRadioButton srchUp = new JRadioButton("Search up");
        findUp = srchUp.getModel();
        butgrp.add(srchUp);
        p4.add(srchUp);
        JRadioButton srchDwn = new JRadioButton("Search down", true);
        findDown = srchDwn.getModel();
        butgrp.add(srchDwn);
        p4.add(srchDwn);
        p2.add(p4, BorderLayout.SOUTH);
        p1.add(p2, BorderLayout.CENTER);
        JPanel p6 = new JPanel(new FlowLayout());
        JPanel p7 = new JPanel(new GridLayout(4, 6));
        FindListener listener =new FindListener();
        JButton btFind = new JButton("Find Next");
        JButton close = new JButton("close");
        btFind.addActionListener(listener);
        p7.add(btFind);
        p7.add(close);
        p6.add(p7);
        p1.add(p6, BorderLayout.EAST);
        getContentPane().add(p1, BorderLayout.CENTER);
        pack();
        setResizable(false);
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
                setVisible(false);
                dispose();
            }
        });
        
    }
    
    
    
    class FindListener implements ActionListener {
        
        public void actionPerformed(ActionEvent ae) {
            
            JEditorPane editorwindow = UIUtil.getUIUtil().getCurrentFocussedInRightTabPane();
            int pos = editorwindow.getCaretPosition();
            toSearchUp = findUp.isSelected();
            
            try {
                Document doc = editorwindow.getDocument();
                if (toSearchUp)
                    searchData = doc.getText(0, pos);
                else
                    searchData = doc.getText(pos, doc.getLength()-pos);
                
                searchIndex = pos;
                String key = "";
                
                key=findText.getText();
                int start = -1;
                int finish = -1;
                if (toSearchUp) {
                    start = searchData.lastIndexOf(key, pos-1);
                } else {
                    start = searchData.indexOf(key, pos-searchIndex);
                }
                if (start < 0) {
                    throw new Exception();
                }
                finish = start+key.length();
                if (!toSearchUp) {
                    start = start+searchIndex;
                    finish = finish+searchIndex;
                }
                selectAnyTextFound(start, finish, toSearchUp);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(myowner,"Unable to Find text");
            }
        }
    }
    
    public void selectAnyTextFound(int fromWhere, int tillWhere, boolean updownstate) {
        StyleConstants.setForeground(method, Color.WHITE);
        StyleConstants.setBackground(method,new Color(51,0,51));
        if (updownstate) {
            UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().setCaretPosition(tillWhere);
            UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().moveCaretPosition(fromWhere);
        } else {
            UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().select(fromWhere, tillWhere);
        }
        
        start = UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().getSelectionStart();
        finish = UIUtil.getUIUtil().getCurrentFocussedInRightTabPane().getSelectionEnd();
        JEditorPane ep=UIUtil.getUIUtil().getCurrentFocussedInRightTabPane();
        Document d=ep.getDocument();
        Iterator s=resetShades.keySet().iterator();
        while(s.hasNext()) {
            String str1=(String)s.next();
            String str2=(String)resetShades.get(str1);
            int istr1=Integer.parseInt(str1);
            int istr2=Integer.parseInt(str2);
            ((DefaultStyledDocument)d).setCharacterAttributes(istr1,istr2,orig,true);
        }
        if(d instanceof DefaultStyledDocument) {
            resetShades.put(""+start,""+(finish-start+1));
            ((DefaultStyledDocument)d).setCharacterAttributes(start,(finish-start+1),method,true);
            //UIUtil.getUIUtil().getEditorWindow().moveCaretPosition(tillWhere+20);
        }
    }
    private HashMap resetShades=new HashMap();
    private SimpleAttributeSet method=new SimpleAttributeSet();
    private SimpleAttributeSet orig=new SimpleAttributeSet();
}

