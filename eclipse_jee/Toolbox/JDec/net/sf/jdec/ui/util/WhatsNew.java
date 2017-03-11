/*
 *  WhatsNew.java Copyright (c) 2006,07 Swaroop Belur
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

/***
 *  IMPORTANT: This class should be updated for every release
 *  Method to change: getText();
 *  @author sbelur
 */
public class WhatsNew extends JFrame implements MouseListener,KeyListener {

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
                WhatsNew.this.setVisible(false);
                WhatsNew.this.dispose();
            }
        }
        );
        cp.add(close);

        JScrollPane sp=new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(sp,BorderLayout.CENTER);
        getContentPane().add(cp,BorderLayout.SOUTH);
        text.setBackground(new Color(204,255,204));
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int)d.getWidth()/2,(int)d.getHeight()/2-300,350,320);
        setVisible(true);
        this.requestFocusInWindow(true);
    }
    private String getText()
    {
        StringBuffer s=new StringBuffer("");
        s.append("Changes...\n");
        s.append("**********\n");
        s.append("Decompiler Side\n");
        s.append("******************\n");
        s.append("1> Jdec Bug Fixes...\n");
        s.append("2> Support for Annotations and Generics\n");
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
