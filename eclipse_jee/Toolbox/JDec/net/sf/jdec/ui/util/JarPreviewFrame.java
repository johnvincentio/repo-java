package net.sf.jdec.ui.util;

import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.util.Util;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class JarPreviewFrame extends JFrame implements ActionListener {

	private JPanel panel1=null;
	private JPanel resultpanel=null;
	private JPanel jarName=new JPanel();
	private JButton preview=null;
	private JButton srchclz=null;
    private JButton openJar=null;
    private String filePath=null;
    private Object pkgInfo[]=null;
    private Object general[]=null;
    private JTextField cpath=null;
    private JLabel pkgname=new JLabel();
    private JButton tip=new JButton("Search Tip");
    {
    	tip.addActionListener(L.getL());

    }
    private static JarPreviewFrame parent=null;

	public static void main(String[] args) {
		new JarPreviewFrame();
	}


	public void actionPerformed(ActionEvent e) {

	if(e.getSource()==openJar){

		  JFileChooser filechooser=new JFileChooser();
	      javax.swing.filechooser.FileFilter filter=new javax.swing.filechooser.FileFilter()
	      {
	    	  public  boolean accept(File f)
	    	  {
	    		return f.getAbsolutePath().endsWith(".jar");
	    	  }
	    	  public  String getDescription()
	    	  {
	    		return "*.jar";
	    	  }
	      };

	     // filechooser.setFileFilter(filter);
	      filechooser.setVisible(true);

	      int returnVal = filechooser.showOpenDialog(this);
	      if(returnVal == JFileChooser.APPROVE_OPTION) {
	           filePath=filechooser.getSelectedFile().getAbsolutePath();
	           jarName.removeAll();
               JLabel fp=new JLabel(filePath);
               fp.setForeground(Color.BLUE);

	           jarName.add(fp);
	           jarName.revalidate();
	          }

	}

	else if(e.getSource()==preview)
	{

		try {
			if(filePath!=null)
			{
				boolean valid=isJarValid(filePath);
				if(valid){
					pkgInfo=PreviewJar.getPackages(filePath);
					general=PreviewJar.getJarDetails(filePath);
					showResult();
				}
			}
		} catch (IOException e1) {

			JOptionPane.showMessageDialog(null,e1.getMessage());
		}


	}


	}


	public JarPreviewFrame()
	{
		super("Jdec Preview Jar");
		createComponents();
		parent=this;
		setVisible(true);

	}

	private void createComponents()
	{

      setSize(530,520);
     // setResizable(false);
      panel1=new JPanel();
      openJar=new JButton("Open Jar");
      panel1.add(openJar,BorderLayout.WEST);
      preview=new JButton("Preview Jar");
      panel1.add(preview,BorderLayout.EAST);
      tip.setEnabled(false);
      //panel1.add(tip);

      getContentPane().add(panel1,BorderLayout.NORTH);
      JSeparator sep=new JSeparator();
      getContentPane().add(sep);
      resultpanel=new JPanel();
      getContentPane().add(resultpanel,BorderLayout.CENTER);
      getContentPane().add(jarName,BorderLayout.SOUTH);
      openJar.addActionListener(this);
      preview.addActionListener(this);

	}

	private boolean isJarValid(String filePath) throws IOException
	{
		boolean ok=true;
        File f=new File(filePath);
        if(!f.exists())throw new IOException("File Does Not Exist");
        if(f.getAbsolutePath().endsWith(".jar")==false)
        {
        	throw new IOException("File is not a jar");
        }
     	return ok;

	}

	private void showResult()
	{
	   try
	   {

		   if(pkgInfo==null)throw new Exception("Package info could not be generated");
           pkgInfo=Util.genericRemoveDuplicates(Arrays.asList(pkgInfo)).toArray();
           final JComboBox combo=new JComboBox();
           for (int i = 0; i < pkgInfo.length; i++)
           {
        	   Object o=pkgInfo[i];
        	   combo.addItem(o.toString());
	       }

           JLabel lbl=new JLabel("AllPackages");
           resultpanel.removeAll();
           resultpanel.revalidate();
           resultpanel.repaint();
           //resultpanel.setLayout(new BoxLayout(resultpanel,BoxLayout.Y_AXIS));
           JPanel header=new JPanel();
           header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
           header.add(lbl);
           header.add(combo);
           JButton filteradd=new JButton("Add Pkg To Filter");
           header.add(filteradd);
           filteradd.setToolTipText("The selected Package in the combo box will be transferred to Filter Screen");
           filteradd.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e)
               {
                   ExtraOptions ff=UILauncher.getUIutil().getFilterframe();
                   JPanel inclpanel=ff.getInclPanel();
                   JComboBox incllist=ff.getInclList();
                   incllist.removeItem("All");
                   incllist.removeItem(combo.getSelectedItem().toString());
                   incllist.addItem(combo.getSelectedItem().toString().replaceAll("/","."));
                   ArrayList list=ff.getIncludedListAsArrayList();
                   list.remove("All");
                   list.add(combo.getSelectedItem().toString().replaceAll("/","."));
                   inclpanel.revalidate();

               }
           });
           JPanel genPanel=new JPanel();
           genPanel.setLayout(new BoxLayout(genPanel, BoxLayout.Y_AXIS));

           for(int z=0;z<general.length;z++)
           {
               final java.lang.String val=general[z].toString();
        	   JLabel jl=new JLabel(val);

               final int k=val.indexOf(":");
               java.lang.String val1="";
               if(k!=-1)
               val1=val.substring(0,k);
               if(general[z].toString().length() > 75)
               {
                 JButton v=new JButton("Show Value");
                   v.addActionListener(new ActionListener(){

                       public void actionPerformed(ActionEvent ae)
                       {

                          JOptionPane.showMessageDialog(JarPreviewFrame.this,val.substring(k));
                       }
                   });
                 JPanel temp=new JPanel();
                 temp.setLayout(new FlowLayout());
                 temp.add(new JLabel(val1));
                 temp.add(v);
                 genPanel.add(temp);

               }
               else
               genPanel.add(jl);
           }
           resultpanel.add(header,BorderLayout.NORTH);
           genPanel.setBorder(new TitledBorder("") );
           resultpanel.add(genPanel,BorderLayout.EAST);
           JPanel search=new JPanel();
           search.removeAll();

           search.setBorder(new TitledBorder("Find Class in a Jar In path"));

           search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

           JPanel path=new JPanel();
           path.setLayout(new BoxLayout(path, BoxLayout.X_AXIS));
           path.add(new JLabel("Enter search path"));
           cpath=new JTextField(20);
           path.add(cpath);

           JPanel CLASS=new JPanel();
           CLASS.setLayout(new BoxLayout(CLASS, BoxLayout.X_AXIS));
           CLASS.add(new JLabel("Enter ClassName(Qualified) To search"));
           final JTextField clname=new JTextField(20);
           srchclz=new JButton("Search");
           CLASS.add(clname);
           CLASS.add(srchclz);

           JPanel pkg=new JPanel();
           pkg.setLayout(new BoxLayout(pkg, BoxLayout.X_AXIS));
           search.add(path);
           search.add(CLASS);
           search.add(pkg);
           search.revalidate();
           resultpanel.add(new JSeparator());
          // resultpanel.remove(search);
          // resultpanel.add(search,BorderLayout.SOUTH);
           tip.setEnabled(true);
           resultpanel.revalidate();
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
                               JOptionPane.showMessageDialog(JarPreviewFrame.this,"Could Not Find Class In Search Path");
                           }
                           else
                           {
                               JOptionPane.showMessageDialog(JarPreviewFrame.this,"Class Found in Jar "+reqdJar);
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
                      
                       JOptionPane.showMessageDialog(JarPreviewFrame.this,e.getMessage());
                   }


               }
           });

	   }
	   catch (Exception e)
	   {
		   JOptionPane.showMessageDialog(null,e.getMessage());
	   }


	}

    private String reqdJar=null;

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
     		   JOptionPane.showMessageDialog(parent,s,"How to Specify Path",JOptionPane.INFORMATION_MESSAGE);
     	   }

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
        for(int z=0;z<list.length;z++)
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

}
