package com.idc.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TestAnimation10 {

    public static void main(String[] args) {
        new TestAnimation10();
    }

    public TestAnimation10() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                }

                final TrackPane trackPane = new TrackPane();
                JSlider slider = new JSlider(1, 5000);
                slider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        trackPane.setCongestion(((JSlider) e.getSource()).getValue());
                    }
                });
                slider.setValue(5);

                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(trackPane);
                frame.add(slider, BorderLayout.SOUTH);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            }
        });
    }

    public class TrackPane extends JPanel {

        private List<Car> activeCarList;
        private List<Car> carPool;
        private int maxCars = 1;
        private List<Point2D[]> points;
        private Ellipse2D areaOfEffect;

        public TrackPane() {

            points = new ArrayList<>(25);

            activeCarList = new ArrayList<>(25);
            carPool = new ArrayList<>(25);
            setLayout(null);

            Timer timer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Rectangle bounds = areaOfEffect.getBounds();
                    List<Car> tmp = new ArrayList<>(activeCarList);
                    for (Car car : tmp) {
                        car.move();
                        if (!bounds.intersects(car.getBounds())) {
                            remove(car);
                            activeCarList.remove(car);
                            carPool.add(car);
                        }
                    }
                    updatePool();
                    repaint();
                }
            });

            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();

            updateAreaOfEffect();
        }

        protected void updateAreaOfEffect() {
            double radius = Math.max(getWidth(), getHeight()) * 1.5d;
            double x = (getWidth() - radius) / 2d;
            double y = (getHeight() - radius) / 2d;
            areaOfEffect = new Ellipse2D.Double(x, y, radius, radius);
        }

        @Override
        public void invalidate() {
//            super.invalidate();
            updateAreaOfEffect();
        }

        protected void updatePool() {
            if (activeCarList.size() < maxCars) {
                int count = Math.min(maxCars - activeCarList.size(), 10);
                for (int index = 0; index < count; index++) {
                    Car car = null;

                    if (carPool.isEmpty()) {
                        car = new Car();
                    } else {
                        car = carPool.remove(0);
                    }

                    double direction = car.getDirection();
                    double startAngle = direction - 180;

                    double radius = areaOfEffect.getWidth();
                    Point2D startPoint = getPointAt(radius, startAngle);

                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;

                    double x = cx + (startPoint.getX() - car.getWidth() / 2);
                    double y = cy + (startPoint.getY() - car.getHeight() / 2);
                    car.setLocation((int) x, (int) y);

                    Point2D targetPoint = getPointAt(radius, direction);

                    points.add(new Point2D[]{startPoint, targetPoint});

                    add(car);

                    activeCarList.add(car);
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Font font = g.getFont();
            font = font.deriveFont(Font.BOLD, 48f);
            FontMetrics fm = g.getFontMetrics(font);
            g.setFont(font);
            g.setColor(Color.RED);
            String text = Integer.toString(maxCars);
            int x = getWidth() - fm.stringWidth(text);
            int y = getHeight() - fm.getHeight() + fm.getAscent();
            g.drawString(text, x, y);
            text = Integer.toString(getComponentCount());
            x = getWidth() - fm.stringWidth(text);
            y -= fm.getHeight();
            g.drawString(text, x, y);
            text = Integer.toString(activeCarList.size());
            x = getWidth() - fm.stringWidth(text);
            y -= fm.getHeight();
            g.drawString(text, x, y);
            text = Integer.toString(carPool.size());
            x = getWidth() - fm.stringWidth(text);
            y -= fm.getHeight();
            g.drawString(text, x, y);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        public void setCongestion(int value) {
            maxCars = value;
        }

        @Override
        public void validate() {
        }

        @Override
        public void revalidate() {
        }

//        @Override
//        public void repaint(long tm, int x, int y, int width, int height) {
//        }
//
//        @Override
//        public void repaint(Rectangle r) {
//        }
//        public void repaint() {
//        }
        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            System.out.println(propertyName);
//            // Strings get interned...
//            if (propertyName == "text"
//                            || propertyName == "labelFor"
//                            || propertyName == "displayedMnemonic"
//                            || ((propertyName == "font" || propertyName == "foreground")
//                            && oldValue != newValue
//                            && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
//
//                super.firePropertyChange(propertyName, oldValue, newValue);
//            }
        }

        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }

    protected static Point2D getPointAt(double radius, double angle) {

        double x = Math.round(radius / 2d);
        double y = Math.round(radius / 2d);

        double rads = Math.toRadians(-angle);

        double fullLength = Math.round((radius / 2d));

        double xPosy = (Math.cos(rads) * fullLength);
        double yPosy = (Math.sin(rads) * fullLength);

        return new Point2D.Double(xPosy, yPosy);

    }

    public class Car extends JPanel {

        private double direction;
        private double speed;
        private BufferedImage background;

        public Car() {
            setOpaque(false);
            direction = Math.random() * 360;
            speed = 5 + (Math.random() * 10);
            int image = 1 + (int) Math.round(Math.random() * 5);
            try {
                String name = "/Car0" + image + ".png";
                background = ImageIO.read(getClass().getResource(name));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            setSize(getPreferredSize());
//            setBorder(new LineBorder(Color.RED));
        }

        public void setDirection(double direction) {
            this.direction = direction;
            revalidate();
            repaint();
        }

        public double getDirection() {
            return direction;
        }

        public void move() {
            Point at = getLocation();
            at.x += (int) (speed * Math.cos(Math.toRadians(-direction)));
            at.y += (int) (speed * Math.sin(Math.toRadians(-direction)));
            setLocation(at);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            if (background != null) {
                double radian = Math.toRadians(direction);
                double sin = Math.abs(Math.sin(radian)), cos = Math.abs(Math.cos(radian));
                int w = background.getWidth(), h = background.getHeight();
                int neww = (int) Math.floor(w * cos + h * sin);
                int newh = (int) Math.floor(h * cos + w * sin);
                size = new Dimension(neww, newh);
            }
            return size;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int x = (getWidth() - background.getWidth()) / 2;
            int y = (getHeight() - background.getHeight()) / 2;
            g2d.rotate(Math.toRadians(-(direction + 180)), getWidth() / 2, getHeight() / 2);
            g2d.drawImage(background, x, y, this);
            g2d.dispose();

//            Debug graphics...
//            int cx = getWidth() / 2;
//            int cy = getHeight() / 2;
//
//            g2d = (Graphics2D) g.create();
//            g2d.setColor(Color.BLUE);
//            double radius = Math.min(getWidth(), getHeight());
//            Point2D pointAt = getPointAt(radius, direction);
//            g2d.draw(new Ellipse2D.Double(cx - (radius / 2d), cy - (radius / 2d), radius, radius));
//            
//            double xo = cx;
//            double yo = cy;
//            double xPos = cx + pointAt.getX();
//            double yPos = cy + pointAt.getY();
//            
//            g2d.draw(new Line2D.Double(xo, yo, xPos, yPos));
//            g2d.draw(new Ellipse2D.Double(xPos - 2, yPos - 2, 4, 4));
//            g2d.dispose();
        }

        @Override
        public void invalidate() {
        }

        @Override
        public void validate() {
        }

        @Override
        public void revalidate() {
        }

        @Override
        public void repaint(long tm, int x, int y, int width, int height) {
        }

        @Override
        public void repaint(Rectangle r) {
        }

        @Override
        public void repaint() {
        }

        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//            System.out.println(propertyName);
//            // Strings get interned...
//            if (propertyName == "text"
//                            || propertyName == "labelFor"
//                            || propertyName == "displayedMnemonic"
//                            || ((propertyName == "font" || propertyName == "foreground")
//                            && oldValue != newValue
//                            && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
//
//                super.firePropertyChange(propertyName, oldValue, newValue);
//            }
        }

        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }
}
