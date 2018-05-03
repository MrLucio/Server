package rotateCards;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class dsaadsads {

    public static void main(String args[]) throws Exception {
        JFrame frame = new JFrame("Rotation Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final BufferedImage bi = ImageIO.read(new File("C:\\Users\\L\\Desktop\\cards.png"));
        JLabel lblRover = new JLabel(new ImageIcon("C:\\Users\\L\\Desktop\\cards.png")) {
        	protected void paintComponent(Graphics g) {
        	Graphics2D g2 = (Graphics2D)g;
        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        							RenderingHints.VALUE_ANTIALIAS_ON);
        	AffineTransform aT = g2.getTransform();
        	Shape oldshape = g2.getClip();
        	double x = getWidth()/2.0;
        	double y = getHeight()/2.0;
        	aT.rotate(Math.toRadians(90), x, y);
        	g2.setTransform(aT);
        	g2.setClip(oldshape);
        	super.paintComponent(g);
            }
        };
        frame.setSize(200, 200);
        frame.getContentPane().add(lblRover);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}