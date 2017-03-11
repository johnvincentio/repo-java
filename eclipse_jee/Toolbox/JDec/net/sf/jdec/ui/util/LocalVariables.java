package net.sf.jdec.ui.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.Behaviour;
import net.sf.jdec.reflection.JavaClass;


public class LocalVariables extends JFrame implements ActionListener 
{

	
	
	public LocalVariables()
	{
		super("Local Variable Window");
		JavaClass clazz= ConsoleLauncher.getClazzRef();
		ArrayList methodSignatures=new ArrayList();
		Container c=getContentPane();
		JPanel panel=new JPanel();
		
		
		if(clazz!=null)
		{
			ArrayList methodList=clazz.getALLMethods();
			for(int s=0;s<methodList.size();s++)
			{
				Behaviour b=(Behaviour)methodList.get(s);
				java.lang.String name=b.getBehaviourName();
				java.lang.String params=b.getUserFriendlyMethodParams();
				String signature=name+params;
				methodSignatures.add(signature);	
			}
		}
		if(methodSignatures.size() > 0)
		{
			JLabel label=new JLabel("Please Select A Method From List...");
			panel.add(label,BorderLayout.NORTH);
			JComboBox combo=new JComboBox();
			populate(methodSignatures,combo);
			panel.add(combo,BorderLayout.SOUTH);
			panel.setVisible(true);
		}
		c.add(panel,BorderLayout.NORTH);
		setBounds(200,200,400,300);
		setVisible(true);
	}
	
	
	private void populate(ArrayList methodSignatures,JComboBox combo)
	{
		
		for(int s=0;s<methodSignatures.size();s++)
		{
		  String desc=(String)methodSignatures.get(s);
		  combo.addItem(desc);
		}
		combo.setActionCommand("combo");
		combo.addActionListener(this);
		
	}
	
	
	public void actionPerformed(ActionEvent ae)
	{
		//System.out.println(ae.getActionCommand());
		if(ae.getActionCommand().equals("combo"))
		{
			
			JComboBox methods=(JComboBox)ae.getSource();
			//System.out.println(methods.getSelectedItem());
		}
	}
		
	
	
	
}
