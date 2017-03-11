/*
 *  FindJar.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.ui.main.UILauncher;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;


public class FindJar extends JFrame
 {

    public static void main(String a[])
    {
        new FindJar("Jdec Find Jar");
    }

    public FindJar(String s)
    {
        super(s);
        parent=this;
        createComp();
        setSize(480,200);
        setVisible(true);
    }
    private JTextField cpath=null;
    private JButton srchclz=null;
    private JButton close=null;

    public void createComp()
    {

        JPanel search=new JPanel();
        search.removeAll();

        search.setBorder(new TitledBorder("Find Class in a Jar In path"));

        //search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

        JPanel path=new JPanel();
        //path.setLayout(new BoxLayout(path, BoxLayout.X_AXIS));
        path.add(new JLabel("Enter search path"));
        cpath=new JTextField(20);
        path.add(cpath);

        JPanel CLASS=new JPanel();
        //CLASS.setLayout(new BoxLayout(CLASS, BoxLayout.X_AXIS));
        CLASS.add(new JLabel("Enter ClassName(Qualified) To search"));
        final JTextField clname=new JTextField(20);
        srchclz=new JButton("Search");
        close=new JButton("Close");
        CLASS.add(clname);
        CLASS.add(srchclz);
        JPanel cl=new JPanel();
        cl.add(close);

        JPanel pkg=new JPanel();
       // pkg.setLayout(new BoxLayout(pkg, BoxLayout.X_AXIS));
        search.add(path,BorderLayout.NORTH);
        search.add(CLASS,BorderLayout.CENTER);
        search.add(pkg,BorderLayout.SOUTH);
        search.add(cl,BorderLayout.EAST);
        search.revalidate();
        close.addActionListener(new ActionListener(){
        	
        	public void actionPerformed(ActionEvent ae)
        	{
        		setVisible(false);
        		dispose();
        	}
        });
        JPanel header=new JPanel();
        header.add(tip);
        JButton how=new JButton("How Jdec Finds The Jar");
        how.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                String msg="Jdec Takes a search path as input";
                msg+="\nThis can be either comma separated Jar list or simple a folder path";
                msg+="\nJdec Then searches each jar if given or searches folder";
                msg+="\nin case of folder , it only looks for Jars and ignores other entries..";
                msg+="\nSo if The class to be found is present in folder but NOT in jar ";
                msg+="\nthen Jdec will not find it";
                JOptionPane.showMessageDialog(FindJar.this,msg);
            }
        });
        header.add(how);
        getContentPane().add(header,BorderLayout.NORTH);
        getContentPane().add(search,BorderLayout.CENTER);
        // resultpanel.remove(search);
        // resultpanel.add(search,BorderLayout.SOUTH);
        tip.setEnabled(true);
        //resultpanel.revalidate();
        srchclz.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                final java.lang.String f=clname.getText();
                final java.lang.String s=cpath.getText();
                Thread t1=new Thread()
                     {
                    public void run()
                    {
                        boolean found=false;
                        UILauncher.getUIutil().setSearch(true);
                        for(int z=0;z<searchJars.size();z++)
                        {
                            java.lang.String s1=(java.lang.String)searchJars.get(z);
                            found=checkJarForClass(s1.trim(),f.trim());
                            if(found)
                            {
                                UILauncher.getUIutil().setSearch(false);
                                reqdJar=s1.trim();
                                break;
                            }
                        }
                        if(!found && searchJars.size()==0 && new File(s.trim()).exists())  // Folder
                        {
                            StringBuffer sb=new StringBuffer("");
                            found=checkForClassNameInFolder(s.trim(),f.trim(),sb);
                            if(found)
                            {
                                UILauncher.getUIutil().setSearch(false);
                                reqdJar=sb.toString();

                            }

                        }
                        UILauncher.getUIutil().setSearch(false);
                        if(!found)
                        {
                            JOptionPane.showMessageDialog(FindJar.this,"Could Not Find Class In Search Path");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(FindJar.this,"Class Found in Jar "+reqdJar);
                        }

                    }

                };
                Thread t2=new Thread()
                     {
                    public void run()
                    {
                        JFrame frame=LaunchProgressBarFrame();
                        frame.setVisible(true);
                        while(UILauncher.getUIutil().searchForClassName())
                        {
                            try
                            {
                                Thread.sleep(1000);
                            }
                            catch(Exception e)
                            {

                            }

                        }
                        frame.setVisible(false);

                    }

                };
                try
                {
                    boolean ok=continueSearch(f,cpath.getText());
                    if(ok)
                    {
                        //if(searchJars.size() > 0)
                        //{
                        t1.setPriority(Thread.MAX_PRIORITY);
                        t2.setPriority(Thread.NORM_PRIORITY);
                        UILauncher.getUIutil().setSearch(true);
                        t2.start();
                        t1.start();

                        //}
                        /*else
                        {
                        JOptionPane.showMessageDialog(JarPreviewFrame.this,"Error while regsitering Jars Entered As Search Path");
                        } */
                    }
                }
                catch(Exception e)
                {
                   
                    JOptionPane.showMessageDialog(FindJar.this,e.getMessage());
                }
            }




        });



    }
    public static JFrame LaunchProgressBarFrame()
    {
        JFrame f = new JFrame("Jdec Progress...");
        srchprog=f;

        JLabel statusLabel = new JLabel();
        statusLabel.setText("Please Wait for Jdec to finish executing the current task....");
        Dimension d=UILauncher.getMainFrame().getToolkit().getScreenSize();
        f.setBounds((int)d.getWidth()/2-130,(int)d.getHeight()/2-80,380, 85);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel contents = (JPanel)f.getContentPane();
        contents.setSize(400,150);
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contents.add(statusLabel	, BorderLayout.NORTH);
        contents.add(progressBar);
        f.getContentPane( ).add(progressBar);
        f.addWindowListener(wndCloser);
        return f;



    }

    static JFrame srchprog=null;

    static WindowListener wndCloser = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            srchprog.setVisible(false);
            srchprog.dispose();
            srchprog=null;
        }
    };

    private boolean continueSearch(java.lang.String f,java.lang.String cpath) throws Exception
       {
          boolean b =true;
          if(cpath==null)
          {
              throw new Exception("Search Path Cannot Be Empty");
          }
          if(f==null)
          {
              throw new Exception("ClassName(QName) Cannot Be Empty");
          }
          File file=new File(cpath);

          if(file.exists() && file.isDirectory() && f!=null && f.length() > 0)
          {
               return b;
          }
          if(file.exists() && !file.isDirectory() && file.getAbsolutePath().endsWith("jar")==false)// && f!=null && f.length() > 0)

          {
               throw new Exception("Search Path is neither a Folder nor a Jar");
          }
          if(!file.exists())
          {
              StringTokenizer tokens=new StringTokenizer(cpath,",");
              while(tokens.hasMoreTokens())
              {
                  String tmp=tokens.nextElement().toString().trim();
                  if(tmp.endsWith("jar"))
                   searchJars.add(tmp);
              }
          }
          return b;
       }

 private ArrayList searchJars=new ArrayList();


    private boolean checkJarForClass(java.lang.String jar, java.lang.String qname)
    {
        File file=new File(jar);
        if(!file.exists())
        {
            return false;

        }
        else
        {
           try
           {
                JarFile jarf=new JarFile(jar);
                Enumeration en=jarf.entries();
                while(en.hasMoreElements())
                {
                    ZipEntry zipe=(ZipEntry)en.nextElement();
                    if(zipe!=null)
                    {
                      String name=zipe.getName();
                      if(name.indexOf("/")!=-1)name=name.replaceAll("/",".");
                      if(name.indexOf(qname)!=-1)
                      {
                          return true;
                      }
                    }
                }
           }
           catch(Exception e)
           {
               return false;
           }

        }

        return false;

    }

    private boolean checkForClassNameInFolder(String folder,String qname,StringBuffer sb)
    {
        
        boolean ok=false;
        File f=new File(folder);
        String list[]=f.list();
        for(int z=0;list!=null && z<list.length;z++)
        {
            File d=new File(folder+File.separator+list[z]);
            if(!d.isDirectory() && d.getAbsolutePath().endsWith("jar"))
            {
               ok=checkJarForClass(d.getAbsolutePath(),qname);

               if(ok){
                   sb.append(d.getAbsolutePath());
                   return ok;
               }
            }
            if(d.isDirectory())
            {

                boolean b1=checkForClassNameInFolder(d.getAbsolutePath(),qname,sb);
                if(b1)
                {
                   return b1;
                }
            }

        }
        return ok;
    }
    private String reqdJar=null;

    private JButton tip=new JButton("Search Tip");
    {
    	tip.addActionListener(L.getL());

    }
    	static private L Lref=null;
    static class L implements ActionListener{

              private L()
              {

              }
              public static L getL()
              {
                  if(Lref!=null)
                  {
                      return Lref;
                  }
                  else
                  {
                      Lref=new L();
                      return Lref;
                  }
              }


                public void actionPerformed(ActionEvent a)
                {
                    String s="Search Path can include comma separated Jar List";
                    s+="\nUser can specify a folder also(Single)";
                    s+="\nExample For Class : java.util.ArrayList.class";
                    s+="\nExample for Search Path : c:\\<java install folder>";
                    JOptionPane.showMessageDialog(parent,s,"How to Specify Path",JOptionPane.INFORMATION_MESSAGE);
                }

        }
         private static FindJar parent=null;
}