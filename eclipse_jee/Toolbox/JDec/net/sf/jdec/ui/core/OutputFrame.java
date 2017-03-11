
/*
 * OutputFrame.java Copyright (c) 2006,07 Swaroop Belur
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
package net.sf.jdec.ui.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import net.sf.jdec.io.Writer;

import net.sf.jdec.ui.config.DecompilerConfigDetails;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.ui.util.ClipBoardViewer;
import net.sf.jdec.ui.util.FindDialog;
import net.sf.jdec.ui.util.UIUtil;
import net.sf.jdec.ui.util.highlight.CategoryChooser;


/**
 * @author swaroop belur
 */
public class OutputFrame extends UIObject implements CaretListener,MouseListener,KeyListener,MouseMotionListener{
    
    public void setDesc(java.lang.String desc) {
        this.whatAmI=desc;
    }
    
    private int adjustment=0;
    JLabel copy=new JLabel();
    JLabel cut=new JLabel();
    JLabel paste=new JLabel();
    JLabel copye=new JLabel();
    JLabel copyc=new JLabel();
    JLabel copyp=new JLabel();
    JLabel clipv=new JLabel();
    JLabel find=new JLabel();
    JLabel selall=new JLabel();
    JLabel jb=new JLabel();
    JLabel je=new JLabel();
    JLabel format=new JLabel();
    JLabel custome=new JLabel();
    JLabel syntax=new JLabel();
    private JEditorPane jeditor=null;
    private UIObserver observer=null;
    private DecompilerConfigDetails config=null;
    private RightClickListener rclick=new RightClickListener();
    final JPopupMenu  popup=new JPopupMenu();
    
    
    public OutputFrame(UIObserver observer,java.lang.String str,java.lang.String type) {
        UIUtil.getUIUtil().decompileFrame=this;
        jeditor=new JEditorPane();//"text/plain",str);
        
        jeditor.setText(str);
        
        jeditor.addCaretListener(this);
        //jeditor.setMinimumSize(new Dimension(200,400));
        //AccessibleHypertext t=jeditor.new  JEditorPaneAccessibleHypertextSupport
        this.observer=observer;
        
        java.lang.String en=UILauncher.getUIConfigRef().getSyntaxEnabled();
        if(type.equals("style")) {
            
            if(en!=null) {
                
                if(en.equalsIgnoreCase("yes")) {
                    type="style";
                } else {
                    type="plain";
                }
                
            } else {
                type="style";
            }
            if(type.equals("style")) {
                kit=new JdecEditorKit();
                jeditor.setEditorKitForContentType("text/java", kit);
                jeditor.setContentType("text/java");
                /*Font font=jeditor.getFont();
                try {
                 
                                        Field field=Font.class.getDeclaredField("name");
                                        field.setAccessible(true);
                                        field.set(font,"Arial Unicode MS");
                                } catch (SecurityException e) {
                 
                                        e.printStackTrace();
                                } catch (IllegalArgumentException e) {
                 
                                        e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                 
                                        e.printStackTrace();
                                } catch (IllegalAccessException e) {
                 
                                        e.printStackTrace();
                                }
                                jeditor.setFont(font);*/
            } else {
                jeditor.setContentType("text/plain");
                DefaultEditorKit defedkit=new DefaultEditorKit() {
                    public Document createDefaultDocument() {
                        PlainDocument plainDoc=new PlainDocument();
                        return plainDoc;
                    }
                };
                jeditor.setEditorKitForContentType("text/plain", defedkit);
            }
        } else {
            jeditor.setContentType("text/plain");
            DefaultEditorKit defedkit=new DefaultEditorKit() {
                public Document createDefaultDocument() {
                    PlainDocument plainDoc=new PlainDocument();
                    return plainDoc;
                }
            };
            jeditor.setEditorKitForContentType("text/plain", defedkit);
            
        }
        jeditor.addMouseListener(this);
        jeditor.addCaretListener(this);
        jeditor.addKeyListener(this);
    }
    
    
    
    public void setConfigTable(DecompilerConfigDetails config) {
        this.config=config;
    }
    
    
    public Component getComponent() {
        /* if(observer.getOutputFrameType().equalsIgnoreCase("jeditorPane"))
        return new JScrollPane(jeditor);
        else if(observer.getOutputFrameType().equalsIgnoreCase("table"))
        return config;
        else*/
        //jeditor.setBackground(Color.LIGHT_GRAY);
        /*jeditor.setTransferHandler(new TransferHandler(){
            /*public boolean canImport(JComponent jc,DataFlavor[] dfs)
            {
                return ((JEditorPane)jc).Ca
            }
             public boolean importData(JComponent comp, Transferable t)
             {
         
                 DataFlavor dfs[]=t.getTransferDataFlavors();
                 if(dfs!=null && dfs.length > 0)
                 {
                    DataFlavor file=dfs[0].javaFileListFlavor;
         
         
                 }
                  return true;
         
             }
        });*/
        
        
        jeditor.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                
                
                if(e.getButton()==MouseEvent.BUTTON3){
                    UIUtil.getUIUtil().prevMouseEventX=e.getX();
                    UIUtil.getUIUtil().prevMouseEventY=e.getY();
                    int w=UILauncher.getMainFrame().getWidth();
                    int h=UILauncher.getMainFrame().getHeight();
                    
                    int rw=UIUtil.getUIUtil().getRightTabbedPane().getWidth();
                    int rh=UIUtil.getUIUtil().getRightTabbedPane().getHeight();
                    
                    if((w-rw) > 0){
                        //jeditor.setCaretPosition(e.getX()+(w-rw)-5,e.getY()+95);
                        popup.setLocation(e.getX()+(w-rw)-5,e.getY()+adjustment);
                        
                        //popup.setBounds(e.getX()+(w-rw)+100,e.getY()+(h-rh),300,300);
                        popup.setVisible( false);
                        popup.removeAll();
                        copy=new JLabel("Copy");
                        copy.setFont(new Font("Arial",0,14));
                        cut=new JLabel("Cut");
                        cut.setFont(new Font("Arial",0,14));
                        cut.addMouseListener(rclick);
                        paste=new JLabel("Paste");
                        paste.setFont(new Font("Arial",0,14));
                        copye=new JLabel("Copy entire");
                        copye.setFont(new Font("Arial",0,14));
                        copyc=new JLabel("Copy class name");
                        copyc.setFont(new Font("Arial",0,14));
                        copyp=new JLabel("Copy class file path");
                        copyp.setFont(new Font("Arial",0,14));
                        clipv=new JLabel("Open Clipboard...");
                        clipv.setFont(new Font("Arial",0,14));
                        find=new JLabel("Find");
                        find.setFont(new Font("Arial",0,14));
                        selall=new JLabel("Select all");
                        jb=new JLabel("Jump to beginning");
                        je=new JLabel("Jump to end");
                        jb.setFont(new Font("Arial",0,14));
                        je.setFont(new Font("Arial",0,14));
                        //abel selall=new JLabel("Select all");
                        format=new JLabel("Re-format output");
                        format.setFont(new Font("Arial",0,14));
                        custome=new JLabel("Show in custom editor");
                        custome.setFont(new Font("Arial",0,14));
                        syntax=new JLabel("Change Syntax coloring");
                        syntax.setFont(new Font("Arial",0,14));
                        popup.addMouseListener(new MouseListener() {
                            public void mouseClicked(MouseEvent e) {
                                popup.requestFocus();
                                popup.setVisible(true);
                                jeditor.cut();
                            }
                            public void mouseEntered(MouseEvent e) {
                                
                                popup.requestFocus();
                                popup.setVisible(true);
                            }
                            public void mouseExited(MouseEvent e) {
                                popup.requestFocus();
                                popup.setVisible(true);
                            }
                            public void mousePressed(MouseEvent e) {
                                popup.requestFocus();
                                popup.setVisible(true);
                            }
                            public void mouseReleased(MouseEvent e) {
                                popup.requestFocus();
                                popup.setVisible(true);
                            }
                        });
                        popup.add(copy);
                        copy.addMouseListener(rclick);
                        popup.add(cut);
                        cut.addMouseListener(rclick);
                        popup.add(paste);
                        paste.addMouseListener(rclick);
                        popup.add(find);
                        find.addMouseListener(rclick);
                        popup.add(new JSeparator());
                        popup.add(copye);
                        copye.addMouseListener(rclick);
                        //popup.add(copyc);
                        //copyc.addMouseListener(rclick);
                        //popup.add(copyp);
                        //copyp.addMouseListener(rclick);
                        popup.add(new JSeparator());
                        popup.add(clipv);
                        clipv.addMouseListener(rclick);
                        //popup.add(format);
                        //format.addMouseListener(rclick);
                        popup.add(new JSeparator());
                        popup.add(jb);
                        jb.addMouseListener(rclick);
                        //popup.add(je);
                       // je.addMouseListener(rclick);
                        popup.add(new JSeparator());
                        //popup.add(custome);
                        //custome.addMouseListener(rclick);
                        popup.add(new JSeparator());
                        popup.add(syntax);
                        syntax.addMouseListener(rclick);
                        popup.setBorderPainted(true);
                        popup.setBorder(new EtchedBorder());
                        popup.setVisible( true);
                    }
                    
                } else {
                    if(popup!=null){
                        
                        popup.setVisible(false);
                    }
                }
            }
        });
        scroll=new JScrollPane(jeditor);
        scroll.addMouseMotionListener(this);
        scroll.setMinimumSize(new Dimension(scroll.getWidth(),scroll.getHeight()+255));
        return scroll;
    }
    
    private JScrollPane scroll=null;
    String currentSel="";
    
    int docPos=-1;
    public void caretUpdate(CaretEvent e) {
        docPos=e.getDot();
        try {
            int start=e.getDot();
            int end=e.getMark();
            if(start > end) {
                currentSel=jeditor.getDocument().getText(end,start-end);
            } else {
                currentSel=jeditor.getDocument().getText(start,end-start);
            }
        } catch(BadLocationException b){
            
            currentSel="";
            
        } finally {
            UIUtil.currentSelString=currentSel;
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if(docPos!=-1) {
            checkForMatchingBracket();
        }
        if(e.getSource()==scroll){
            int y=e.getY();
            
        }
    }
    
    public void mouseEntered(MouseEvent e) {
         if(e.getSource()==scroll){
            int y=e.getY();
            
        }
    }
    
    public void mouseExited(MouseEvent e) {
         if(e.getSource()==scroll){
            int y=e.getY();
            
        }
    }
    
    public void mousePressed(MouseEvent e) {
         if(e.getSource()==scroll){
            int y=e.getY();
            
        }
    }
    
    public void mouseReleased(MouseEvent e) {
         if(e.getSource()==scroll){
            int y=e.getY();
            
        }
    }
    
    private void checkForMatchingBracket() {
        try {
            String c=null;
            int reqdpos=-1;
            int startpos=-1;
            try {
                c=jeditor.getText(docPos,1);
                
                if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals("(")) {
                    startpos=docPos;
                } else {
                    c=jeditor.getText(docPos-1,1);
                    startpos=docPos-1;
                }
                if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals(")")) {
                    
                } else {
                    startpos=-1;
                }
            } catch(BadLocationException be) {
                c=null;
            }
            if(c!=null && startpos!=-1) {
                Stack stack=new Stack();
                
                reqdpos=-1;
                String str=jeditor.getText();
                
                char cur=' ';
                reqdpos=-1;
                if(c.equals("{")) {
                    
                    for(int x=startpos+1;x<str.length();x++) {
                        cur=jeditor.getText(x,1).charAt(0);
                        if(cur=='{') {
                            
                            stack.push("{");
                            
                        }
                        if(cur=='}') {
                            if(stack.size() > 0) {
                                stack.pop();
                            } else if(stack.size() == 0) {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }
                        
                    }
                } else if(c.equals("(")) {
                    
                    for(int x=startpos+1;x<str.length();x++) {
                        cur=jeditor.getText(x,1).charAt(0);
                        if(cur=='(') {
                            
                            stack.push("(");
                            
                        }
                        if(cur==')') {
                            if(stack.size() > 0) {
                                stack.pop();
                            } else if(stack.size() == 0) {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }
                        
                    }
                } else    if(c.equals(")")) {
                    
                    for(int y=startpos-1;y>=0;y--) {
                        cur=jeditor.getText(y,1).charAt(0);
                        if(cur==')') {
                            
                            stack.push(")");
                            
                        }
                        if(cur=='(') {
                            if(stack.size() > 0) {
                                stack.pop();
                            } else if(stack.size() == 0) {
                                reqdpos=y;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }
                        
                    }
                } else  if(c.equals("}")) {
                    
                    for(int x=startpos-1;x>=0;x--) {
                        cur=jeditor.getText(x,1).charAt(0);
                        if(cur=='}') {
                            
                            stack.push("}");
                            
                        }
                        if(cur=='{') {
                            if(stack.size() > 0) {
                                stack.pop();
                            } else if(stack.size() == 0) {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }
                        
                    }
                }
            }
            
            
            
        } catch(BadLocationException be) {
            Writer w;
            try {
                w = Writer.getWriter("log");
                be.printStackTrace(w);
            } catch (IOException ex) {
                
            }
            
        }
    }
    private void highLightBracket(int reqdpos) {
        javax.swing.text.Document thisDoc=jeditor.getDocument();
        
        StyleConstants.setForeground(BracketAttribute, Color.RED);
        StyleConstants.setBackground(BracketAttribute, Color.BLACK);
        StyleConstants.setBold(BracketAttribute, true);
        bracketMarkedPositions.add(new Integer(reqdpos));
        
        clearPreviousHighlights();
        if(thisDoc instanceof DefaultStyledDocument)
            ((DefaultStyledDocument)thisDoc).setCharacterAttributes(reqdpos,1,BracketAttribute,true);
    }
    private void clearPreviousHighlights() {
        javax.swing.text.Document thisDoc=jeditor.getDocument();
        for(int z=0;z<bracketMarkedPositions.size()-1;z++) {
            int p=((Integer)bracketMarkedPositions.get(z)).intValue();
            if(thisDoc instanceof DefaultStyledDocument)
                ((DefaultStyledDocument)thisDoc).setCharacterAttributes(p,1,nohigh,true);
        }
    }
    
    private SimpleAttributeSet BracketAttribute=new SimpleAttributeSet();
    private SimpleAttributeSet nohigh=new SimpleAttributeSet();
    public ArrayList bracketMarkedPositions=new ArrayList();
    private JdecEditorKit kit=null;
    
    public JdecEditorKit getKit(){return kit;}
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
        if(docPos!=-1) {
            checkForMatchingBracket();
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        e.getAdjustmentType();
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }
    
    
    public OutputFrame(UIObserver observer,java.lang.String str,java.lang.String type,String version) {
        Writer w=null;
        try {
            w=Writer.getWriter("log");
        } catch (IOException ex) {
           
        }
        UIUtil.getUIUtil().decompileFrame=this;
        jeditor=new JEditorPane();//"text/plain",str);
        
        jeditor.setText(str);
        
        jeditor.addCaretListener(this);
        //jeditor.setMinimumSize(new Dimension(200,400));
        //AccessibleHypertext t=jeditor.new  JEditorPaneAccessibleHypertextSupport
        this.observer=observer;
        
        java.lang.String en=UILauncher.getUIConfigRef().getSyntaxEnabled();
        if(type.equals("style")) {
            
            if(en!=null) {
                
                if(en.equalsIgnoreCase("yes")) {
                    type="style";
                } else {
                    type="plain";
                }
                
            } else {
                type="style";
            }
            if(type.equals("style")) {
                kit=new JdecEditorKit(version);
                jeditor.setEditorKitForContentType("text/java", kit);
                jeditor.setContentType("text/java");
                Font font=jeditor.getFont();
                try {
                    
                    Field field=Font.class.getDeclaredField("name");
                    field.setAccessible(true);
                    field.set(font,"Arial Unicode MS");
                } catch (SecurityException e) {
                    if(w!=null)
                        e.printStackTrace(w);
                } catch (IllegalArgumentException e) {
                    if(w!=null)
                        e.printStackTrace(w);
                } catch (NoSuchFieldException e) {
                    if(w!=null)
                        e.printStackTrace(w);
                } catch (IllegalAccessException e) {
                    if(w!=null)
                        e.printStackTrace(w);
                }
                jeditor.setFont(font);
            } else {
                jeditor.setContentType("text/plain");
                DefaultEditorKit defedkit=new DefaultEditorKit() {
                    public Document createDefaultDocument() {
                        PlainDocument plainDoc=new PlainDocument();
                        return plainDoc;
                    }
                };
                jeditor.setEditorKitForContentType("text/plain", defedkit);
            }
        } else {
            jeditor.setContentType("text/plain");
            DefaultEditorKit defedkit=new DefaultEditorKit() {
                public Document createDefaultDocument() {
                    PlainDocument plainDoc=new PlainDocument();
                    return plainDoc;
                }
            };
            jeditor.setEditorKitForContentType("text/plain", defedkit);
            
        }
        jeditor.addMouseListener(this);
        jeditor.addCaretListener(this);
    }
    
    
    class RightClickListener implements MouseListener{
        public void mouseClicked(MouseEvent e) {
            
            jeditor.setCaretPosition(e.getY());
            Cursor c=new Cursor(Cursor.HAND_CURSOR);
            JLabel obj=(JLabel)e.getSource();
            obj.setCursor(c);
            obj.requestFocus();
            popup.requestFocus();
            popup.setVisible(true);
            if(e.getSource()==copy){
                jeditor.copy();
            }
            if(e.getSource()==cut){
                jeditor.cut();
            }
            if(e.getSource()==paste){
                jeditor.paste();
            }
            if(e.getSource()==copye){
                jeditor.selectAll();
                jeditor.copy();
            }
            if(e.getSource()==clipv){
                ClipBoardViewer viewer=new ClipBoardViewer("Clipboard...");
                viewer.display();
            }
            if(e.getSource()==find){
                FindDialog fd=new FindDialog(UILauncher.getMainFrame());
                fd.getFindText().setText(jeditor.getSelectedText());
                fd.setVisible(true);
            }
            if(e.getSource()==selall){
                jeditor.selectAll();
                //jeditor.copy();
            }
            if(e.getSource()==jb){
                jeditor.setCaretPosition(0);
                jeditor.moveCaretPosition(2);
            }
            if(e.getSource()==syntax){
                CategoryChooser chooser=new CategoryChooser("Jdec Syntax Highlighting Module");
            }
            
        }
        public void mouseEntered(MouseEvent e) {
            
            Cursor c=new Cursor(Cursor.HAND_CURSOR);
            JLabel obj=(JLabel)e.getSource();
            obj.setCursor(c);
            obj.requestFocus();
            popup.requestFocus();
            popup.setVisible(true);
        }
        public void mouseExited(MouseEvent e) {
            popup.requestFocus();
            popup.setVisible(true);
        }
        public void mousePressed(MouseEvent e) {
            popup.requestFocus();
            popup.setVisible(true);
        }
        public void mouseReleased(MouseEvent e) {
            popup.requestFocus();
            popup.setVisible(true);
        }
        
    }
    
    
    
    
}
