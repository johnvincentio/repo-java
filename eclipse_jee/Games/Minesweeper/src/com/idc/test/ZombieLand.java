package com.idc.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZombieLand {

	protected static final Random RND = new Random();

	private static BufferedImage m_zombie;
//	private static Image m_zombie;

	private static final String MY_IMAGE = "gifs/mine.gif";		// "/Zombie.png"

	public static void main(String[] args) {
		new ZombieLand();
	}

	public ZombieLand() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
				}

				try {
					m_zombie = ImageIO.read(new File(MY_IMAGE));

				} catch (IOException ex) {
					ex.printStackTrace();
				}

				final ZombiePane zombiePane = new ZombiePane();

				final JSlider slider = new JSlider(1, 10000);
				slider.setMajorTickSpacing(1000);
				slider.setMinorTickSpacing(100);
				slider.setPaintTicks(true);
				slider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						JSlider slider = (JSlider) e.getSource();
						zombiePane.setZombies(slider.getValue());
					}
				});

				JFrame frame = new JFrame("Testing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(zombiePane);
				frame.add(slider, BorderLayout.SOUTH);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						slider.setValue(10000);
					}
				});
			}
		});
	}

	public static class ZombiePane extends JPanel {

		private List<ZombieSprite> sprites;
		protected static final Object SPRITE_LOCK = new Object();

		private int desiredCount = 1;

		public ZombiePane() {
//			m_zombie = getToolkit().getImage("gifs/flag.gif");
			sprites = new ArrayList<>(25);
			sprites.add(new ZombieSprite());
			Thread t = new Thread(new MainLoop());
			t.setDaemon(false);
			t.start();
			Font font = getFont();
			setFont(font.deriveFont(Font.BOLD, 48f));
		}

		public void setZombies(int count) {
			desiredCount = count;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			synchronized (SPRITE_LOCK) {
				for (ZombieSprite sprite : sprites) {
					sprite.paint(g2d);
				}
			}
			String text = Integer.toString(sprites.size());
			FontMetrics fm = g2d.getFontMetrics();
			g2d.drawString(text, getWidth() - fm.stringWidth(text), getHeight()
					- fm.getHeight() + fm.getAscent());
			g2d.dispose();
		}

		protected void cycle() {
			synchronized (SPRITE_LOCK) {
				if (desiredCount != sprites.size()) {
					int count = 0;
					int fill = 100;
					while (sprites.size() > desiredCount && count < fill) {
						sprites.remove(0);
						count++;
					}
					count = 0;
					while (sprites.size() < desiredCount && count < fill) {
						sprites.add(new ZombieSprite());
						count++;
					}
				}

				for (ZombieSprite sprite : sprites) {
					sprite.update(getWidth(), getHeight());
				}
			}
		}

		public class MainLoop implements Runnable {

			private int DELAY = 40;

			public void run() {
				long beforeTime, timeDiff, sleep;
				beforeTime = System.currentTimeMillis();
				while (true) {
					cycle();
					repaint();
					timeDiff = System.currentTimeMillis() - beforeTime;
					sleep = DELAY - timeDiff;
					if (sleep < 0) {
						sleep = 2;
					}
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						System.out.println("interrupted");
					}
					beforeTime = System.currentTimeMillis();
				}
			}
		}
	}

	public static class ZombieSprite {

		// private BufferedImage zombie;
		private Point motionDelta;
		private double rotationDelta;

		private Point location;
		private double angle;

		public ZombieSprite() {
			motionDelta = new Point();
			motionDelta.x = (int) ((Math.random() * 3) + 1);
			motionDelta.y = (int) ((Math.random() * 3) + 1);
			if (Math.random() > 0.5) {
				motionDelta.x *= -1;
			}
			if (Math.random() > 0.5) {
				motionDelta.y *= -1;
			}
			rotationDelta = (int) ((Math.random() * 9) + 1);
			if (Math.random() > 0.5) {
				rotationDelta *= -1;
			}
		}

		public void paint(Graphics2D g2d) {
			if (location != null) {
				Graphics2D g = (Graphics2D) g2d.create();
				AffineTransform at = new AffineTransform();
				at.translate(location.x, location.y);
				at.rotate(Math.toRadians(angle), m_zombie.getWidth() / 2, m_zombie.getHeight() / 2);
				g.setTransform(at);
				g.drawImage(m_zombie, 0, 0, null);
				g.dispose();
			}
		}

		public void update(int width, int height) {
			if (location == null) {
				angle = (Math.random() * 360d);
				location = new Point();
				location.x = (int) (Math.random() * (width - m_zombie.getWidth()));
				location.y = (int) (Math.random() * (height - m_zombie.getHeight()));
			}
			else {
				angle += rotationDelta;
				location.x += motionDelta.x;
				location.y += motionDelta.y;

				if (location.x < 0) {
					location.x = 0;
					motionDelta.x *= -1;
				}
				else if (location.x + m_zombie.getWidth() > width) {
					location.x = width - m_zombie.getWidth();
					motionDelta.x *= -1;
				}
				if (location.y < 0) {
					location.y = 0;
					motionDelta.y *= -1;
				}
				else if (location.y + m_zombie.getHeight() > height) {
					location.y = height - m_zombie.getHeight();
					motionDelta.y *= -1;
				}
			}
		}
	}
}
