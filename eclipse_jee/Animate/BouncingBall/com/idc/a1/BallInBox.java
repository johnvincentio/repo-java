package com.idc.a1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BallInBox extends JPanel {
	private static final long serialVersionUID = 1;
    //... Instance variables representing the ball.
    private Ball m_ball         = new Ball(0, 0, 2, 3);
    
    //... Instance variables for the animiation
    private int   m_interval  = 35;  // Milliseconds between updates.
    private Timer m_timer;           // Timer fires to anmimate one step.

    /** Set panel size and creates timer. */
    public BallInBox() {
        setPreferredSize(new Dimension(200, 80));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        m_timer = new Timer(m_interval, new TimerAction());
    }
    
    /** Turn animation on or off.
     *@param turnOnOff Specifies state of animation.
     */
    public void setAnimation(boolean turnOnOff) {
        if (turnOnOff) {
            m_timer.start();  // start animation by starting the timer.
        } else {
            m_timer.stop();   // stop timer
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Paint background, border
        m_ball.draw(g);           // Draw the ball.
    }
    
    class TimerAction implements ActionListener {
        /** ActionListener of the timer.  Each time this is called,
         *  the ball's position is updated, creating the appearance of
         *  movement.
         *@param e This ActionEvent parameter is unused.
         */
        public void actionPerformed(ActionEvent e) {
            m_ball.setBounds(getWidth(), getHeight());
            m_ball.move();  // Move the ball.
            repaint();      // Repaint indirectly calls paintComponent.
        }
    }
}
