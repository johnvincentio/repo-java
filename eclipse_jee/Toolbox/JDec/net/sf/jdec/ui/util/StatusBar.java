package net.sf.jdec.ui.util;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sf.jdec.ui.core.Manager;
import net.sf.jdec.ui.main.UILauncher;

public class StatusBar extends JPanel {
	JProgressBar pbar;
	static final int MY_MINIMUM=0;
	static final int MY_MAXIMUM=100;
	public StatusBar( ) {
		
	}
	public void updateBar(int newValue) {
		pbar.setValue(newValue);
	}
	public void display()  {
		try
		{
			final StatusBar it = new StatusBar( );
			JFrame frame = new JFrame("Please wait");
			frame.setContentPane(it);
			frame.setBounds(UILauncher.getMainFrame().getWidth()/2,UILauncher.getMainFrame().getHeight()/2,400,90);
			frame.pack( );
			UIManager.setLookAndFeel(UILauncher.getUIutil().getLookAndFeelClass(UILauncher.getUIutil().getCurrentLNF()));
			SwingUtilities.updateComponentTreeUI(frame);
			
			Thread st=Manager.getManager().getStatusThread();
			frame.setVisible(true);
			
			for (int i = MY_MINIMUM; i <= MY_MAXIMUM || Manager.getManager().isShowProgressBar(); i++) {
				final int percent=i;
				it.updateBar(percent);
				try {
					SwingUtilities.invokeAndWait(new Runnable( ) {
						public void run( ) {
								it.updateBar(percent);
						}
					});
					st.sleep(10);
				} catch (InterruptedException e) {;}
			}
			
			
			
			frame.setVisible(false);
			
		}
		catch(Exception e){

              try
              {
                LogWriter lg=LogWriter.getInstance();
                lg.writeLog("[ERROR]: Method display\n\tClass "+this.getClass());
                lg.flush();

              }
              catch(Exception exp)
              {
            
              }

        }

		}
		
	}
