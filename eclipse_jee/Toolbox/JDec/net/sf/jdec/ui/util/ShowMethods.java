/*
 *  ShowMethods.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.io.Writer;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.ui.core.JdecTree;
import net.sf.jdec.util.ClassStructure;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;

public class ShowMethods extends JFrame implements MouseListener,ActionListener {
    
    private String ms[]=null;
    public static boolean createClone=true;
    private JdecTree tree=null;
    private JTree jtree=null;
    public ShowMethods() {
        super("Class Structure");
        tree=new JdecTree(ConsoleLauncher.mainClassStructure,false);
        initialize(tree);//ConsoleLauncher.getcurrentClassMethods());
    }
    
    
    
    
    
    
    
    
    private boolean present=false;
    private ArrayList allMethods =null;
    private void initialize(JdecTree tree) {
        
        //if(methods==null || methods.size() > 0)
        //methods=ConsoleLauncher.getcurrentClassMethods();
        if(tree!=null) {
            allMethods=new ArrayList();
            jtree=tree.getTree();
            JMenu menu=new JMenu("Actions");
            JMenu cp=new JMenu("Clipboard");
            JMenuBar mbar=new JMenuBar();
            setJMenuBar(mbar);
            JMenuItem asc=new JMenuItem("Sort Ascending");
            JMenuItem desc=new JMenuItem("Sort Descending");
            JMenuItem jump=new JMenuItem("Jump to Selected Method");
            JMenuItem close=new JMenuItem("Close");
            JMenuItem code=new JMenuItem("Copy Selected Method Code");
            JMenuItem mlist=new JMenuItem("Copy This List");
            JMenuItem viewer=new JMenuItem("Open Clipboard Viewer");
            mbar.add(menu);
            mbar.add(cp);
            menu.add(asc);
            //menu.add(desc);
            //menu.add(jump);
            menu.add(new JSeparator());
            menu.add(close);
            
            cp.add(code);
            //cp.add(mlist);
            cp.add(viewer);
            
            
            
            
            pane=new JScrollPane(tree);
            present=true;
            text=new JTextField();
            text.setColumns(20);
            text.setText("<type here to filter [Need to press enter]>");
            text.setFocusable(true);
            text.moveCaretPosition(0);
            //text.addKeyListener(this);
            //getContentPane().add(text,BorderLayout.NORTH);
            getContentPane().add(pane,BorderLayout.CENTER);
            setBounds(300,250,300,400);
            getContentPane().setBackground(Color.GREEN);
            getContentPane().setForeground(Color.YELLOW);
            setVisible(true);
            getContentPane().setBackground(Color.GREEN);
            getContentPane().setForeground(Color.YELLOW);
            //list.setBackground(new Color(204,204,204));
            //list.setForeground(new Color(51,0,51));
            text.addActionListener(this);
            text.setActionCommand("filter");
            asc.addActionListener(this);
            asc.setActionCommand("asc");
            desc.addActionListener(this);
            desc.setActionCommand("desc");
            jump.addActionListener(this);
            jump.setActionCommand("jump");
            close.addActionListener(this);
            close.setActionCommand("close");
            code.addActionListener(this);
            code.setActionCommand("code");
            mlist.addActionListener(this);
            mlist.setActionCommand("mlist");
            viewer.addActionListener(this);
            viewer.setActionCommand("viewer");
            
            //pane.setBackground(new Color(204,255,255));
            //pane.setForeground(new Color(51,0,51));
        }
    }
    private JTextField text=null;
    //private JList list=null;
    private JScrollPane pane=null;
    
    public boolean showMethods() {
        return present;
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
    
    
    
    
    
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals("asc")) {
            
            
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getTree().getModel().getRoot();
            //JDecTree.expandTreeFully(root);
            try{
                bfs(root,"asc");
            } catch(Throwable t){
                try {
                    t.printStackTrace(Writer.getWriter("log"));
                } catch (IOException ex) {
                   
                }
            }
            this.getContentPane().remove(pane);
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            JTree sortedTree = new JTree(treeModel);
            jtree=sortedTree;
            
            // Add listeners
            sortedTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            JdecTree.JDecStructureTreeExpansionListener exp=tree.new JDecStructureTreeExpansionListener();
            sortedTree.addTreeExpansionListener(exp);
            JdecTree.JdecStructureTreeSelectionListener sel=tree.new JdecStructureTreeSelectionListener();
            sortedTree.addTreeSelectionListener(sel);
            sortedTree.setShowsRootHandles(true);
            sortedTree.setEditable(false);
            
            
            JScrollPane sortedpane=new JScrollPane(sortedTree);
            //pane.revalidate();
            this.getContentPane().add(sortedpane,BorderLayout.CENTER);
            this.getContentPane().validate();
            //list=new JList(str);
            validate();
            repaint();
        } else if(e.getActionCommand().equals("desc")) {
            
            // First revert to original tree
            /*this.getContentPane().remove(pane);
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getTree().getModel().getRoot();
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            JTree sortedTree = new JTree(treeModel);
            JScrollPane sortedpane=new JScrollPane(sortedTree);
            //pane.revalidate();
            this.getContentPane().add(sortedpane,BorderLayout.CENTER);
            this.getContentPane().validate();
            //list=new JList(str);
            validate();
            repaint();*/
            this.dispose();
            this.setVisible(false);
            initialize(tree);
            
            
            DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getTree().getModel().getRoot();
            //JDecTree.expandTreeFully(root);
            try{
                bfs(root,"desc");
            } catch(Throwable t){
                try {
                    t.printStackTrace(Writer.getWriter("log"));
                } catch (IOException ex) {
                   
                }
            }
            this.getContentPane().remove(pane);
            DefaultTreeModel  treeModel = new DefaultTreeModel(root);
            JTree sortedTree = new JTree(treeModel);
            JScrollPane sortedpane=new JScrollPane(sortedTree);
            //pane.revalidate();
            this.getContentPane().add(sortedpane,BorderLayout.CENTER);
            this.getContentPane().validate();
            //list=new JList(str);
            validate();
            repaint();
        } else if(e.getActionCommand().equals("jump")) {
            
            
        } else if(e.getActionCommand().equals("close")) {
            setVisible(false);
            dispose();
        } else if(e.getActionCommand().equals("viewer")) {
            ClipBoardViewer clipBrdViewer = new ClipBoardViewer(
                "Jdec ClipBoard Viewer");
            clipBrdViewer.display();
        } else if(e.getActionCommand().equals("code")) {
            
            TreePath path=jtree.getSelectionPath();
            if(path==null){
                JOptionPane.showMessageDialog(this,"Please select a method...");
            }
            DefaultMutableTreeNode selNode=(DefaultMutableTreeNode) (path.getLastPathComponent());
            
            if(selNode==null) {
                JOptionPane.showMessageDialog(this,"Please select a method...");
            } else {
                DefaultMutableTreeNode p=(DefaultMutableTreeNode) selNode.getParent();
                if(p==null){
                    String x=path.toString();
                    int br=x.indexOf("[");
                    if(br!=-1){
                        x=x.substring(br+1);
                        br=x.indexOf("]");
                        if(br!=-1){
                            x=x.substring(0,br);
                            StringTokenizer st=new StringTokenizer(x,",");
                            ArrayList list=new ArrayList();
                            while(st.hasMoreTokens()){
                                list.add(st.nextToken());
                            }
                            if(list.size() > 1){
                                String nm=(String)list.get(list.size()-2);
                                if(ConsoleLauncher.classMethodRefMap!=null){
                                    HashMap map= ConsoleLauncher.classMethodRefMap;
                                    if(nm!=null){
                                        HashMap bl=(HashMap)map.get(nm.trim());
                                        if(bl!=null){
                                            Behaviour method=(Behaviour) bl.get(selNode.toString());
                                            if(method!=null){
                                                String codes=method.getCodeStatements();
                                                JEditorPane editor=UIUtil.getUIUtil().getEditorWindow();
                                                String bkp=editor.getText();
                                                editor.setText(codes);
                                                editor.selectAll();
                                                editor.copy();
                                                editor.setText(bkp);
                                                JTabbedPane tabs=UIUtil.getUIUtil().getRightTabbedPane();
                                                tabs.setSelectedIndex(tabs.indexOfTab("Decompiled Output"));
                                            }
                                        }
                                        
                                        
                                    }
                                }
                            }
                        }
                    }
                    
                }
                
                
                if(p!=null){
                    
                    String pn=p.toString();
                    if(pn!=null){
                        if(ConsoleLauncher.classMethodRefMap!=null && ConsoleLauncher.classMethodRefMap.size() > 0){
                            HashMap bl=(HashMap) ConsoleLauncher.classMethodRefMap.get(pn);
                            if(bl!=null){
                                Behaviour method=(Behaviour) bl.get(selNode.toString());
                                if(method!=null){
                                    String codes=method.getCodeStatements();
                                    JEditorPane editor=UIUtil.getUIUtil().getEditorWindow();
                                    String bkp=editor.getText();
                                    editor.setText(codes);
                                    editor.selectAll();
                                    editor.copy();
                                    editor.setText(bkp);
                                    JTabbedPane tabs=UIUtil.getUIUtil().getRightTabbedPane();
                                    tabs.setSelectedIndex(tabs.indexOfTab("Decompiled Output"));
                                }
                            }
                        }
                    }
                }
                
            }
        }
    }
    
    private void sortAsc(DefaultMutableTreeNode root) {
        if(root==null)return;
        String s=root.toString();
        if(s==null)return ;
        ClassStructure cst= ConsoleLauncher.getClassStructure(s);
        if(cst==null)return;
        ArrayList list=cst.getMethods();
        String[] ms=(String[])list.toArray(new String[list.size()]);
        Arrays.sort(ms);
        ArrayList sorted=new ArrayList();
        for(int x=0;x<ms.length;x++){
            sorted.add(ms[x]);
        }
        cst.setMethods(sorted);
        String dummy="d";
    }
    LinkedList allChildren=new LinkedList();
    
    void bfs(DefaultMutableTreeNode root,String arg) {
        
        if(root==null)return;
        //if(root.getChildCount()==0)return;
        tree.expandTreeFully(root);
        HashMap map=new HashMap();
        int total=root.getChildCount();
        ArrayList innerList=new ArrayList();
        for(int z=0;z<total;z++){
            
            DefaultMutableTreeNode child=(DefaultMutableTreeNode)root.getChildAt(z);
            if(child!=null && child.toString().indexOf("Scanning")==-1){
                if(child.getChildCount()==0 ){
                    map.put(child.toString(),child);
                } else
                    innerList.add(child);
            }
        }
        ArrayList temp=new ArrayList();
        Iterator it=map.keySet().iterator();
        while(it.hasNext()){
            temp.add((String)it.next());
        }
        String str[]=(String[])temp.toArray(new String[temp.size()]);
        Arrays.sort(str);
        
        root.removeAllChildren();
        
        if(arg!=null && arg.equals("asc")){
            for(int x=0;x<str.length;x++){
                String s=str[x];
                DefaultMutableTreeNode temproot=(DefaultMutableTreeNode)map.get(s);
                root.add(temproot);
                temproot.setParent(root);
            }
        } else if(arg!=null && arg.equals("desc")){
            for(int x=str.length-1;x>=0;x--){
                String s=str[x];
                DefaultMutableTreeNode temproot=(DefaultMutableTreeNode)map.get(s);
                root.add(temproot);
                temproot.setParent(root);
            }
        }
        
        for(int m=0;m<innerList.size();m++){
            DefaultMutableTreeNode temproot=(DefaultMutableTreeNode)innerList.get(m);
            root.add(temproot);
            temproot.setParent(root);
        }
        
        // Done with methods for this root.
        // Visit Each child now in  bfs order
        for(int n=0;n<root.getChildCount();n++){
            allChildren.add(root.getChildAt(n));
        }
        
        if(allChildren!=null && allChildren.size() > 0)
            bfs((DefaultMutableTreeNode)allChildren.removeFirst(),arg);
        
    }
    
    void collectMethods(DefaultMutableTreeNode root) {
        
        if(root==null)return;
        //if(root.getChildCount()==0)return;
        tree.expandTreeFully(root);
        int total=root.getChildCount();
        ArrayList innerList=new ArrayList();
        for(int z=0;z<total;z++){
            
            DefaultMutableTreeNode child=(DefaultMutableTreeNode)root.getChildAt(z);
            if(child!=null && child.toString().indexOf("Scanning")==-1){
                if(child.getChildCount()==0 ){
                    allMethods.add(child.toString());
                } else
                    innerList.add(child);
            }
        }
        
        
        
        
        // Done with methods for this root.
        // Visit Each child now in  bfs order
        for(int n=0;n<innerList.size();n++){
            allChildren.add(innerList.get(n));
        }
        
        if(allChildren!=null && allChildren.size() > 0)
            collectMethods((DefaultMutableTreeNode)allChildren.removeFirst());
        
    }
    
    
    
}
