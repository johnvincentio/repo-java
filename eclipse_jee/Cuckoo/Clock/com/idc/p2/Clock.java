package com.idc.p2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Clock extends JPanel {
	private static final long serialVersionUID = 1;
        
    private JButton tickButton, resetButton;
    private JLabel hourLabel, minuteLabel;
    private int minutes = 0;    
    
    public Clock(){
        JPanel panel = new JPanel();        
        panel.setLayout(new FlowLayout());
        
        tickButton = new JButton("Tick");
        resetButton = new JButton("Reset");        
        hourLabel = new JLabel("12:");
        minuteLabel = new JLabel("00");
        
        panel.add(tickButton);
        panel.add(resetButton);
        panel.add(hourLabel);
        panel.add(minuteLabel);
        
        setLayout(new BorderLayout());       
        add("South", panel);       
    }
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawOval(0, 0, 100, 100);
        double hourAngle = 2 * Math.PI * (minutes - 3 * 60)/ (12 * 60);
        double minuteAngle = 2 * Math.PI * (minutes - 15)/ 60;
        g.drawLine(50, 50,50 + (int)(30 * Math.cos(hourAngle)), 50 + (int)(30 * Math.sin(hourAngle)));
        g.drawLine(50, 50,50 + (int)(45 * Math.cos(minuteAngle)), 50 + (int)(45 * Math.sin(minuteAngle)));
    }
    
    public void reset() {
        minutes = 0;
        repaint();
    }
    
    public void tick() {
        minutes++;
        repaint();
    }
    
    public int getMinutes(){
        return minutes;
    }
}
