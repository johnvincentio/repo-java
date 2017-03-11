/*
 *  KnownIssues.java Copyright (c) 2006,07 Swaroop Belur
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author sbelur
 */
public class KnownIssues extends JFrame implements MouseListener,KeyListener{
    
    /** Creates a new genericFinder of KnownIssues */
    public KnownIssues() {
        super("Known Issues");
    }
    

    public void createMe()
    {
        JTextArea text=new JTextArea();
        text.setText(getText());
        JButton close=new JButton("close");
        JPanel cp=new JPanel();
        close.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                KnownIssues.this.setVisible(false);
                KnownIssues.this.dispose();
            }
        }
        );
        cp.add(close);

        JScrollPane sp=new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(sp,BorderLayout.CENTER);
        getContentPane().add(cp,BorderLayout.SOUTH);
        text.setBackground(new Color(204,255,204));
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int)d.getWidth()/2-75,(int)d.getHeight()/2-300,520,320);
        text.setCaretPosition(0);
        setVisible(true);
        this.requestFocusInWindow(true);
    }
    private String getText()
    {
        StringBuffer s=new StringBuffer("");
        s.append("--------------------------------------------------\n");
        s.append("Known issues in 2.0 ...\n\n");
       
        s.append("--------------Jdec(UI SIDE)------------------------------\n");
        s.append("Very rarely the syntax highlighting code renders \nempty spaces in some color\n");
        s.append("So to see the output clearly, just run the refresh task\n\n");
        s.append("--------------Jdec(Core)------------------------------\n");
        
        s.append("1> Lack of porper support for ternary operations\n");
        s.append("2> Decompilation of arrays like\n");
        s.append("   int a[][]{{a,bc},{a,b,c , new int[]{c,d,e}[0]}} does not give out\n   fully correct result\n");
        s.append("3> If the src contains array like \n");
        s.append("   int a[][][]={ { {1,2,4},{1,2,3} }, { {1,2,4},{1,2,3} } }\n");
        s.append("4> If loop start clashes with try guard and try appears first in source\n    it is possible for the try to be printed later\n    This is a bug which has to be fixed...\n\n" );
        s.append("   Jdec can cause the decompiled output to be produced with not so \n   pretty foramatting sometimes..\n");
        s.append("   However it is very much readable and only in some case,\n   the brackets are not aligned properly\n");
        s.append("   The user is requested to use an external editor for this\n");
        s.append("   Please use [Utilities -> Show in external editor] for this\n\n");
            
        
        
        s.append("--------------------------------------------------\n\n");
        
        
        return s.toString();
    }


    public void mouseClicked(MouseEvent e) {
            this.requestFocusInWindow(true);
            setVisible(true);
    }

    public void mouseEntered(MouseEvent e) {

        this.requestFocusInWindow(true);
        setVisible(true);
    }

    public void mouseExited(MouseEvent e) {
        this.requestFocusInWindow(false);
        setVisible(false);

    }

    public void mousePressed(MouseEvent e) {

        this.requestFocusInWindow(true);
        setVisible(true);
    }

    public void mouseReleased(MouseEvent e) {

        this.requestFocusInWindow(false);
        setVisible(false);
    }

    public void keyPressed(KeyEvent e) {

        
    }

    public void keyReleased(KeyEvent e) {
        
    }

    public void keyTyped(KeyEvent e) {
       
    }
}
