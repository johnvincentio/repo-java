package com.idc.p1;

import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JFrame;

///////////////////////////////////////////////////////////// ClockAnalogBuf
public class ClockAnalogBuf extends JApplet {
	private static final long serialVersionUID = 1;
    
    //=============================================================== fields
    private Clock _clock;                        // Our clock component.
    
    //================================================================= main
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Analog Clock");
        window.setContentPane(new ClockAnalogBuf());
        window.pack();                          // Layout components
        window.setLocationRelativeTo(null);     // Center window.
        window.setVisible(true);
    }
    
    //========================================================== constructor
    public ClockAnalogBuf() {
        //... Create an instance of our new clock component.
        _clock = new Clock();
        
        //... Set the applet's layout and add the clock to it.
        setLayout(new BorderLayout());
        add(_clock, BorderLayout.CENTER);
        
        //... Start the clock running.
        start(); 
    }
    
    //=============================================================== start
    @Override public void start() {
        _clock.start();
    }
    
    //================================================================ stop
    @Override public void stop() {
        _clock.stop();
    }
}
