/*
*  TextEditor.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.ui.util.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.text.*;

import net.sf.jdec.ui.util.JavaFileFilter;
import net.sf.jdec.ui.util.highlight.CategoryChooser;
import net.sf.jdec.ui.core.JdecDocument;
import net.sf.jdec.ui.core.JdecEditorKit;



public class TextEditor extends JFrame  implements UndoableEditListener,CaretListener,MouseListener,KeyListener{
    JToolBar bar = new JToolBar( );
    JTextArea ta;
    JLabel lab=new JLabel("Jdec Basic Editor");
    UndoManager undom=new UndoManager();
    JMenuItem u=null;
    JMenuItem r=null;
    TextEditor ref=null;
    public TextEditor( ) {
        super("Basic Text Editor...");
        ref=this;
        textComp = createTextComponent( );
        Container content = getContentPane( );
        content.add(textComp, BorderLayout.CENTER);
        content.add(lab, BorderLayout.SOUTH);
        setJMenuBar(createMenuBar( ));
        setSize(600, 440);
        
    }

    protected JScrollPane createTextComponent( ) {
        JScrollPane scroll=new JScrollPane();
        ta = new JTextArea( );
        ta.setBackground(Color.LIGHT_GRAY);
        JdecEditorKit kit=new JdecEditorKit();
        //ta.setEditorKitForContentType("text/java", kit);
        //ta.setContentType("text/java");
        
        ta.setDocument(new JdecDocument());
        ta.addKeyListener(this);
        ta.addCaretListener(this);
        ta.addMouseListener(this);
        ta.setLineWrap(false);
        ta.setWrapStyleWord(false);
        ta.getDocument().addUndoableEditListener(this);
        JViewport vport=new JViewport();
        vport.setView(ta);
        scroll.setViewport(vport);
        return scroll;
    }
    protected JMenuBar createMenuBar( ) {
        JMenuBar menubar = new JMenuBar( );
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu format = new JMenu("Tools");
        JMenuItem syn=new JMenuItem("Syntax Highlighting");
        format.add(syn);
        syn.addActionListener(
                new ActionListener()
				{
                    public void actionPerformed(ActionEvent ae)
                    {
                        CategoryChooser chooser=new CategoryChooser("Jdec Syntax Highlighting Module");

                    }

                }


        );

        JMenuItem TIME=new JMenuItem("Time/Date");

        TIME.addActionListener(
                new ActionListener()
				{
                    public void actionPerformed(ActionEvent ae)
                    {

                        ta.setText(ta.getText()+"\n"+new Date().toString());
                        lab.setText("Jdec Basic Editor"+"                                                 Status:  Time Inserted");
                    }

                }


        );



        menubar.add(file);
        menubar.add(edit);
        //menubar.add(format);
        JMenuItem open=new JMenuItem("Open File");
        JMenuItem save=new JMenuItem("Save File As");
        JMenuItem exit=new JMenuItem("Exit Editor");
        JMenuItem New=new JMenuItem("New");
        file.add(New);
        file.add(open);
        file.add(save);
        file.add(new JSeparator());
        file.add(exit);

        open.addActionListener(new OpenListener());
        save.addActionListener(new SaveListener());
        exit.addActionListener(new ExitListener());

        JMenuItem copy=new JMenuItem("Copy");
        copy.setActionCommand("Copy");
        JMenuItem cut=new JMenuItem("Cut");
        cut.setActionCommand("Cut");
        JMenuItem paste=new JMenuItem("Paste");
        paste.setActionCommand("Paste");
        JMenuItem sel=new JMenuItem("SelectAll");
        sel.setActionCommand("sel");

        u=new JMenuItem("Undo");
        u.setActionCommand("undoevent");


        r=new JMenuItem("Redo");
        r.setActionCommand("redoevent");

        if(undom.canUndo())
        {
            u.setEnabled(true);
        }
        else
        {
            u.setEnabled(false);
        }


        if(undom.canRedo())
        {
            r.setEnabled(true);
        }
        else
        {
            r.setEnabled(false);
        }
        EditListener e=new EditListener();
        copy.addActionListener(e);
        cut.addActionListener(e);
        paste.addActionListener(e);
        sel.addActionListener(e);
        u.addActionListener(e);
        r.addActionListener(e);

        edit.add(copy);
        edit.add(cut);
        edit.add(paste);
        edit.add(sel);
        edit.add(u);
        edit.add(r);
        edit.add(new JSeparator());
        edit.add(TIME);

        JMenu tool=new JMenu("Tools");
        JMenuItem compile=new JMenuItem("Compile Java");
        JMenuItem run=new JMenuItem("Run Java");
        tool.add(compile);
        tool.add(run);
        //menubar.add(tool);

        toolListener tl=new toolListener();
        compile.setActionCommand("compile");
        run.setActionCommand("run");
        compile.addActionListener(tl);
        run.addActionListener(tl);


        New.addActionListener(
                new ActionListener()
				{
                    public void actionPerformed(ActionEvent ae)
                    {

                        if((ta.getText()!=null && ta.getText().length() > 0) && currentFile==null)
                        {
                            int opt=JOptionPane.showConfirmDialog(TextEditor.this,"The text in the untitled file has been changed...\nWould you like to save youe work first ?","User Info",JOptionPane.INFORMATION_MESSAGE);
                            if(opt==JOptionPane.YES_OPTION)
                            {
                                savework();
                                lab.setText("Jdec basic Editor"+"                                                 Status:  File Saved...");
                                ta.setText("");
                            }
                            if(opt==JOptionPane.NO_OPTION)
                            {
                                ta.setText("");
                            }
                        }

                        if((ta.getText()!=null  || ta.getText().length() > 0)&& currentFile!=null )
                        {
                            if(currentfiletext.equals(ta.getText())==false)
                            {
                                int opt=JOptionPane.showConfirmDialog(TextEditor.this,"The text in the current file has been changed...\nWould you like to save youe work first ?","User Info",JOptionPane.INFORMATION_MESSAGE);
                                if(opt==JOptionPane.YES_OPTION)
                                {
                                    savework();
                                    lab.setText("Jdec Basic Editor"+"                                                 Status:  File Saved...");
                                    ta.setText("");
                                }
                                if(opt==JOptionPane.NO_OPTION)
                                {
                                    ta.setText("");
                                }
                            }
                        }


                    }
                }
        );
        return menubar;
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        undom.addEdit(e.getEdit());
        if(undom.canUndo())
        {
            u.setEnabled(true);
        }
        else
        {
            u.setEnabled(false);
        }

        if(undom.canRedo())
        {
            r.setEnabled(true);
        }
        else
        {
            r.setEnabled(false);
        }


    }

    int docPos=-1;
    int startPos=-1;
    int endPos=-1;

    public void caretUpdate(CaretEvent e) {

       docPos=e.getDot();
        startPos=e.getDot();
        endPos=e.getMark();

      /*try
      {
        docPos=e.getDot();
        /*String c=null;
        int reqdpos=-1;
        int startpos=-1;
        try
        {
            c=ta.getText(docPos,1);

            if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals("("))
            {
               startpos=docPos;
            }
            else
            {
                c=ta.getText(docPos-1,1);
                startpos=docPos-1;
            }
            if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals(")"))
            {

            }
            else
            {
                startpos=-1;
            }
        }
        catch(BadLocationException be)
        {
            c=null;
        }
        if(c!=null && startpos!=-1)
        {
            Stack stack=new Stack();
            TextEditor.bracket brk=null;
            if(c.equals("{"))
            {
                brk=ref.new bracket();
                brk.setClose(false);
                brk.setFlower(true);
                brk.setBracket("{");
            }
            else if(c.equals("}"))
            {
                brk=ref.new bracket();
                brk.setClose(true);
                brk.setFlower(true);
                brk.setBracket("}");
            }
            else if(c.equals("("))
            {
                brk=ref.new bracket();
                brk.setClose(false);
                brk.setFlower(false);
                brk.setBracket("(");
            }
            else if(c.equals(")"))
            {
                brk=ref.new bracket();
                brk.setClose(true);
                brk.setFlower(false);
                brk.setBracket(")");
            }
            reqdpos=-1;
            String str=ta.getText();
            if(brk!=null)
            {


                char cur=' ';
                reqdpos=-1;
                if(c.equals("{"))
                {

                    for(int x=startpos+1;x<str.length();x++)
                    {
                        cur=ta.getText(x,1).charAt(0);
                        if(cur=='{')
                        {

                            stack.push("{");

                        }
                        if(cur=='}')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
           else if(c.equals("("))
                {

                    for(int x=startpos+1;x<str.length();x++)
                    {
                         cur=ta.getText(x,1).charAt(0);
                        if(cur=='(')
                        {

                            stack.push("(");

                        }
                        if(cur==')')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                 highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
              else    if(c.equals(")"))
                {

                    for(int y=startpos-1;y>=0;y--)
                    {
                        cur=ta.getText(y,1).charAt(0);
                        if(cur==')')
                        {

                            stack.push(")");

                        }
                        if(cur=='(')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=y;
                                 highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
            else  if(c.equals("}"))
                {

                    for(int x=startpos-1;x>=0;x--)
                    {
                         cur=ta.getText(x,1).charAt(0);
                        if(cur=='}')
                        {

                            stack.push("}");

                        }
                        if(cur=='{')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
            }

        }

      }
       catch(BadLocationException be)
       {
         be.printStackTrace();
       } */
    }


    private void checkForMatchingBracket()
    {
        try
      {
        String c=null;
        int reqdpos=-1;
        int startpos=-1;
        try
        {
            c=ta.getText(docPos,1);

            if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals(")"))
            {
               startpos=docPos;
            }
            else
            {
                c=ta.getText(docPos-1,1);
                startpos=docPos-1;
            }
            if(c.equals("{") || c.equals("}") || c.equals("(") || c.equals(")"))
            {

            }
            else
            {
                startpos=-1;
            }
        }
        catch(BadLocationException be)
        {
            c=null;
        }
        if(c!=null && startpos!=-1)
        {
            Stack stack=new Stack();
            TextEditor.bracket brk=null;
            if(c.equals("{"))
            {
                brk=ref.new bracket();
                brk.setClose(false);
                brk.setFlower(true);
                brk.setBracket("{");
            }
            else if(c.equals("}"))
            {
                brk=ref.new bracket();
                brk.setClose(true);
                brk.setFlower(true);
                brk.setBracket("}");
            }
            else if(c.equals("("))
            {
                brk=ref.new bracket();
                brk.setClose(false);
                brk.setFlower(false);
                brk.setBracket("(");
            }
            else if(c.equals(")"))
            {
                brk=ref.new bracket();
                brk.setClose(true);
                brk.setFlower(false);
                brk.setBracket(")");
            }
            reqdpos=-1;
            String str=ta.getText();
            if(brk!=null)
            {


                char cur=' ';
                reqdpos=-1;
                if(c.equals("{"))
                {

                    for(int x=startpos+1;x<str.length();x++)
                    {
                        cur=ta.getText(x,1).charAt(0);
                        if(cur=='{')
                        {

                            stack.push("{");

                        }
                        if(cur=='}')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
           else if(c.equals("("))
                {

                    for(int x=startpos+1;x<str.length();x++)
                    {
                         cur=ta.getText(x,1).charAt(0);
                        if(cur=='(')
                        {

                            stack.push("(");

                        }
                        if(cur==')')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                 highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
              else    if(c.equals(")"))
                {

                    for(int y=startpos-1;y>=0;y--)
                    {
                        cur=ta.getText(y,1).charAt(0);
                        if(cur==')')
                        {

                            stack.push(")");

                        }
                        if(cur=='(')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=y;
                                 highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
            else  if(c.equals("}"))
                {

                    for(int x=startpos-1;x>=0;x--)
                    {
                         cur=ta.getText(x,1).charAt(0);
                        if(cur=='}')
                        {

                            stack.push("}");

                        }
                        if(cur=='{')
                        {
                            if(stack.size() > 0)
                            {
                                stack.pop();
                            }
                            else if(stack.size() == 0)
                            {
                                reqdpos=x;
                                highLightBracket(reqdpos);
                                break;
                            }
                        }

                    }
                }
            }

        }

      }
       catch(BadLocationException be)
       {
         
       }
    }

    private void highLightBracket(int reqdpos)
    {
         javax.swing.text.Document thisDoc=ta.getDocument();

                                StyleConstants.setForeground(BracketAttribute, Color.RED);
                                StyleConstants.setBackground(BracketAttribute, Color.BLACK);
                                StyleConstants.setBold(BracketAttribute, true);
                                bracketMarkedPositions.add(new Integer(reqdpos));

                                clearPreviousHighlights();
                                ((DefaultStyledDocument)thisDoc).setCharacterAttributes(reqdpos,1,BracketAttribute,true);
    }

    ArrayList bracketMarkedPositions=new ArrayList();

     private void clearPreviousHighlights()
     {
      javax.swing.text.Document thisDoc=ta.getDocument();
      for(int z=0;z<bracketMarkedPositions.size()-1;z++)
      {
        int p=((Integer)bracketMarkedPositions.get(z)).intValue();
        ((DefaultStyledDocument)thisDoc).setCharacterAttributes(p,1,nohigh,true);
      }
     }
        private SimpleAttributeSet BracketAttribute=new SimpleAttributeSet();
    private SimpleAttributeSet nohigh=new SimpleAttributeSet();

    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());

            if(menu!=null && menu.isVisible())
            {
                menu.setVisible(false);
            }


    }

    public void keyReleased(KeyEvent e) {
        //System.out.println(e.getKeyCode());
                   if(menu!=null && menu.isVisible())
                   {
                       menu.setVisible(false);
                   }


    }

    public void keyTyped(KeyEvent e) {
        //System.out.println(e.getKeyCode());
                   if(menu!=null && menu.isVisible())
                   {
                       menu.setVisible(false);
                   }


    }


    class bracket
    {
        public boolean isFlower() {
            return flower;
        }

        public void setFlower(boolean flower) {
            this.flower = flower;
        }

        public String getBracket() {
            return bracket;
        }

        public void setBracket(String bracket) {
            this.bracket = bracket;
        }

        boolean flower=true;
        String bracket=null;

        public boolean isClose() {
            return close;
        }

        public void setClose(boolean close) {
            this.close = close;
        }

        boolean close=false;


    }


    JPopupMenu menu=null;
    public void mouseClicked(MouseEvent e) {
        if(docPos!=-1)
        {
            checkForMatchingBracket();
            //return;
        }

       /* if(e.getButton()==MouseEvent.BUTTON3)
        {
            ref.requestFocus();
            ref.requestFocusInWindow();
            if(menu!=null)
                menu.setVisible(false);
            menu=new JPopupMenu();
            Rectangle d=ref.getBounds();
            //menu.setBounds(startPos,endPos,300,300);
            //menu.setLocation(new Point(e.getX(),e.getY()));
           // JOptionPane.showMessageDialog(null,e.getX()+" "+e.getY()+" ?");
           // JOptionPane.showMessageDialog(null,d.getX()+" "+d.getY());
            //JOptionPane.showMessageDialog(null,""+endPos);

            menu.setLocation(new Point(e.getX(),e.getY()));
            JMenuItem copy=new JMenuItem("Copy");
            JMenuItem cut=new JMenuItem("Cut");
            JMenuItem paste=new JMenuItem("Paste");
            JMenuItem lower=new JMenuItem("toLowerCase");
            JMenuItem upper=new JMenuItem("toUpperCase");
            //JMenuItem paste=new JMenuItem("Paste");
            menu.add(copy);
            menu.add(cut);
            menu.add(paste);
            menu.add(lower);
            menu.add(upper);
            menu.setVisible(true);
            copy.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent a)
                {
                    ta.copy();
                }
            });
            cut.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent a)
                {
                    ta.cut();
                }
            });
            paste.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent a)
                {

                    ta.paste();
                }
            });
            lower.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent a)
                {
                   //System.out.println("XXXX"+endPos+" "+startPos);
                   try
                   {
                    if(endPos > startPos)
                    {
                        String s=ta.getText(startPos,endPos-startPos+1).toLowerCase();
                        ta.getDocument().insertString(startPos,s,null);

                    }
                    else
                    {
                       String s= ta.getText(endPos,startPos-endPos+1).toLowerCase();
                       ta.getDocument().insertString(endPos,s,null);

                    }
                   }
                    catch(BadLocationException be){

                       be.printStackTrace();
                   }
                }
            });
            upper.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent a)
                {
                     
                   try
                   {
                    if(endPos > startPos)
                        ta.getText(startPos,endPos-startPos+1).toUpperCase();
                    else
                        ta.getText(endPos,startPos-endPos+1).toUpperCase();
                   }
                    catch(BadLocationException be){}
                }
            });


        }
        if(e.getButton()!=MouseEvent.BUTTON3)
        {
            if(menu!=null)
                menu.setVisible(false);
        }*/

    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | ExceptionSettings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | ExceptionSettings | File Templates.
    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | ExceptionSettings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | ExceptionSettings | File Templates.
    }


    class toolListener implements ActionListener
	{
        public void actionPerformed(ActionEvent ev) {
            if(ev.getActionCommand().equals("compile"))
            {

            }
            else if(ev.getActionCommand().equals("run"))
            {

            }

        }
    }

    public JScrollPane getTextComponent( ) { return textComp; }

    private JScrollPane textComp;
    private Hashtable actionHash = new Hashtable( );


    public class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {

            int opt=JOptionPane.showConfirmDialog(TextEditor.this,"Are You Sure !");
            if(opt==JOptionPane.YES_OPTION)
            {
                TextEditor.this.setVisible(false);
                TextEditor.this.dispose();
            }

        }
    }

    private String currentFile=null;
    private String currentfiletext=null;

    class OpenListener implements ActionListener {


        public void actionPerformed(ActionEvent ev) {
            JFileChooser chooser = new JFileChooser();
            javax.swing.filechooser.FileFilter filter=new javax.swing.filechooser.FileFilter()
			{
                public  boolean accept(File f)
                {
                    return true;
                }
                public  String getDescription()
                {
                    return "All Files";
                }

            };
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(TextEditor.this) !=JFileChooser.APPROVE_OPTION)
                return;
            File file = chooser.getSelectedFile();
            if (file == null)
                return;
            String s=file.getAbsolutePath();
            File temp=new File(s);
            String s2=s;
            boolean ok=true;
            String tmp="";
            if(!temp.exists())
            {
                ok=false;
                // try some formats  // Dont know why it happens
                // Happens for GTK Look and Feel and Motif
                // May be for Java Format It hides extension !!!
                // Will Attempt to try 4 formats..Wild guess

                tmp=s+".java";
                temp=new File(tmp);
                //System.out.println(temp.exists());

                if(!temp.exists())
                {
                    tmp=s+".txt";
                    temp=new File(tmp);
                    //System.out.println(temp.exists());
                }
                if(!temp.exists())
                {
                    tmp=s+".class";
                    temp=new File(tmp);
                    //System.out.println(temp.exists());
                }
                if(!temp.exists())
                {
                    tmp=s+".log";
                    temp=new File(tmp);
                    //System.out.println(temp.exists());
                }


            }
            if(!ok)s2=tmp;
            s=s2;
            //JOptionPane.showMessageDialog(UILauncher.getMainFrame(),s);
            FileReader reader = null;
            try {
                reader = new FileReader(s);
                JViewport view=textComp.getViewport();
                JTextArea text=(JTextArea)view.getView();
                text.read(reader, null);
                currentFile=s;
                currentfiletext=text.getText();
                lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  File Opened ..."+currentFile);

            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(TextEditor.this,
                        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close( );
                    } catch (IOException x) {}
                }
            }
        }
    }

    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            savework();
        }

    }

    void savework()
    {

        JFileChooser chooser = new JFileChooser( );
        JavaFileFilter filter=new JavaFileFilter();
        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(TextEditor.this) !=JFileChooser.APPROVE_OPTION)
            return;
        File file = chooser.getSelectedFile( );
        if (file == null)
            return;
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);

            JViewport view=textComp.getViewport();
            JTextArea text=(JTextArea)view.getView();
            text.write(writer);

        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(TextEditor.this,
                    "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close( );
                } catch (IOException x) {}
            }
        }
    }

    public static void main(String[] args) {
        new TextEditor() ;
    }

    class EditListener implements ActionListener
	{


        public void actionPerformed(ActionEvent ae)
        {

            
            if(ae.getActionCommand().equals("undoevent"))
            {
                undom.undo();
                if(undom.canUndo())
                {
                    u.setEnabled(true);
                }
                else
                {
                    u.setEnabled(false);
                }

                if(undom.canRedo())
                {
                    r.setEnabled(true);
                }
                else
                {
                    r.setEnabled(false);
                }


            }
            if(ae.getActionCommand().equals("redoevent"))
            {
                undom.redo();
                if(undom.canUndo())
                {
                    u.setEnabled(true);
                }
                else
                {
                    u.setEnabled(false);
                }

                if(undom.canRedo())
                {
                    r.setEnabled(true);
                }
                else
                {
                    r.setEnabled(false);
                }

            }

            if(ae.getActionCommand().equals("Copy"))
            {

                JScrollPane pane=textComp;
                JViewport view=(JViewport)pane.getViewport();
                JTextArea editor=(JTextArea)view.getView();
                editor.copy();
                Highlighter hl=editor.getHighlighter();
                int l=hl.getHighlights().length;
                if(l > 0)
                {
                    lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Text Copied...");
                }
                else
                {
                    lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Could Not Copy...");
                }

            }
            else if(ae.getActionCommand().equals("Cut"))
            {
                JScrollPane pane=textComp;
                JViewport view=(JViewport)pane.getViewport();
                JTextArea editor=(JTextArea)view.getView();
                editor.cut();
                Highlighter hl=editor.getHighlighter();
                int l=hl.getHighlights().length;
                if(l > 0)
                {
                    lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Text Cut...");
                }
                else
                {
                    lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Could Not Cut...");
                }
            }
            else if(ae.getActionCommand().equals("Paste"))
            {

                JScrollPane pane=textComp;
                JViewport view=(JViewport)pane.getViewport();
                JTextArea editor=(JTextArea)view.getView();
                editor.paste();
                lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Text Pasted");
            }

            else if(ae.getActionCommand().equals("sel"))
            {
                JScrollPane pane=textComp;
                JViewport view=(JViewport)pane.getViewport();
                JTextArea editor=(JTextArea)view.getView();
                editor.selectAll();
                lab.setText("Initial Developer: Swaroop Belur"+"                                                 Status:  Selected All Content");
            }
        }
    }
}
