/*
 *  SplashScreen.java Copyright (c) 2006,07 Swaroop Belur
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
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;


public class SplashScreen extends JFrame implements ImageObserver{

      public static JProgressBar progress;
      static
      {
        progress=new JProgressBar(0,100);
      }
      public boolean showSplash(Image img)
      {

    	  java.lang.String classname=getLNFClassName();
    	  if(classname!=null)
    	  {
	    	  try
	    	  {
	    		  
	    		  UIManager.setLookAndFeel(classname); 
	    		  SwingUtilities.updateComponentTreeUI(this);
	    		  
	    		  
	    	  }
	    	  catch(Exception exp)
	    	  {
	    		  try{
                      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                      SwingUtilities.updateComponentTreeUI(this);
                  }
                  catch(Exception ex){}
	    		  
	    	  }
    	  }else
    	  {
    		  try{
                  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                  SwingUtilities.updateComponentTreeUI(this);
              }
              catch(Exception ex){}
    	  }
         

          JWindow splash=new JWindow(this,getGraphicsConfiguration());
          JPanel panel=new JPanel();
          //panel.setSize(450,470);
          panel.setSize(500,500);
          JLabel lbl=new JLabel();
          ImageIcon icon=new ImageIcon(img);
          lbl.setIcon(icon);
          panel.add(lbl);
          splash.getContentPane().add(panel,BorderLayout.CENTER);
          st=new JLabel("Please Wait Jdec is loading");
          st.setForeground(Color.RED);
          JPanel footer=new JPanel();
          footer.setLayout(new BoxLayout(footer,BoxLayout.Y_AXIS));
          footer.add(st);
          footer.add(progress);
          splash.getContentPane().add(footer,BorderLayout.SOUTH);
          splash.setSize(panel.getWidth(),panel.getHeight());
          panel.setBorder(new MatteBorder(3,3,3,3,Color.DARK_GRAY));
          Dimension d=getToolkit().getScreenSize();
          splash.setLocation(d.getSize().width/2-splash.getSize().width/2,d.getSize().height/2-splash.getSize().height/2);
          splash.setVisible(true);
          pack();
          splash.show();
          return true;
      }
    public void advance(final int i)
    {

        progress.setValue(i);                 
        
        /* try{
          SwingUtilities.invokeLater(new Runnable(){
              public void run()
              {
                double d=progress.getPercentComplete();
                progress.setValue(i);
              }
          });
          }
          catch(Exception exp){}*/
    }

    public SplashScreen()
    {
          super("");

    }

    public void splash()
    {
        java.lang.String fsep=File.separator;
        java.lang.String prefix="imgs"+fsep; //jdecSplashCopy_2.gif
        File img=new File(prefix+"jdecSplash.gif");
        showSplash(Toolkit.getDefaultToolkit().getImage(img.getAbsolutePath()));
    }

   
    public JLabel st;
    
    private java.lang.String getLNFClassName()
    {
    	
    	String def=System.getProperty("user.lookNfeel.choice");
    	if(def!=null)
    	{
    		return def;
    	}
        File configf=new File(System.getProperty("user.home")+"JDecUserPreferences.txt");
        if(!configf.exists())
        {
        	return null;
        }
        try {
			FileInputStream fis=new FileInputStream(configf);
			BufferedInputStream bis=new BufferedInputStream(fis);
			BufferedReader br=new BufferedReader(new InputStreamReader(bis));
			String temp=br.readLine();
			while(temp!=null) {
				
				if(temp.indexOf("DefaultLNKFeel")!=-1)
				{
					int eq=temp.indexOf("=");
					if(eq!=-1)
					{
						String clazz=temp.substring(eq+1);
						if(clazz!=null)
						{
							clazz=clazz.trim();
							return clazz;
						}
					}
					else
						return null;
				}
				else
				{
					temp=br.readLine();
					continue;
				}
					
			}
			return null;
			
			
		} catch (IOException e) {
		
			return null;
		}
        
    
    	
    }

}
