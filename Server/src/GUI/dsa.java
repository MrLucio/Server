package GUI;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import mazzoServer.creaMazzo;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class dsa {

	private JFrame frame;
	private JPanel panel_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					dsa window = new dsa();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public dsa() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 750, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ImageIcon im1=new ImageIcon("C:\\Users\\L\\Desktop\\cards.png");
		BufferedImage img;
		try {
			
			img = ImageIO.read(new File("C:\\Users\\L\\Desktop\\cards.png"));
			frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

			JPanel panel_3 = new JPanel();
			frame.getContentPane().add(panel_3);
			panel_3.setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			JScrollPane scroll=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll.setBorder(null);
			panel.setMinimumSize(new Dimension(10, 180));
			panel.setMaximumSize(new Dimension(1500, 185));

			panel_3.add(scroll, BorderLayout.SOUTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			creaMazzo mazzo = new creaMazzo();
			mazzo.generaMazzo();
			HashMap<String, String> cartaPescata = mazzo.pescaCarta();
			System.out.println("Ho pescato : " + cartaPescata.get("value") + " " + cartaPescata.get("color"));
			int[] coord = mazzo.coordinateMazzo(cartaPescata);

			JPanel panel_11 = new JPanel();
			panel.add(panel_11);

			JLabel lblDsa = new JLabel(myResize(img, 0, 0));
			panel_11.add(lblDsa);
			lblDsa.setMinimumSize(new Dimension(30, 45));
			lblDsa.setHorizontalAlignment(SwingConstants.CENTER);

			Movement mv1 = new Movement(lblDsa);

			JPanel panel_12 = new JPanel();
			panel.add(panel_12);

			JLabel label_1 = new JLabel(myResize(img, coord[1], coord[0]));
			panel_12.add(label_1);
			label_1.setMinimumSize(new Dimension(30, 45));
			label_1.setIgnoreRepaint(true);
			Movement mv = new Movement(label_1);

			JPanel panel_1 = new JPanel();
			panel_3.add(panel_1, BorderLayout.NORTH);
			panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

			panel_2 = new JPanel();
			panel_3.add(panel_2, BorderLayout.WEST);
			panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

			JPanel panel_4 = new JPanel();
			panel_3.add(panel_4, BorderLayout.EAST);

			JLabel label_2 = new JLabel(myResize(img, coord[1], coord[0]));

			JPanel panel_5 = new JPanel();
			panel_3.add(panel_5);
			panel_5.setLayout(new BorderLayout(0, 0));
			panel_5.add(label_2);

			JPanel panel_13 = new JPanel();
			panel.add(panel_13);
			JLabel label_9 = new JLabel(myResize(img, coord[1], coord[0]));
			panel_13.add(label_9);
			label_9.setMinimumSize(new Dimension(30, 45));

			JPanel panel_14 = new JPanel();
			panel.add(panel_14);
			JLabel label_8 = new JLabel(myResize(img, coord[1], coord[0]));
			panel_14.add(label_8);
			label_8.setMaximumSize(new Dimension(240, 360));
			label_8.setMinimumSize(new Dimension(30, 45));

			JPanel panel_15 = new JPanel();
			panel.add(panel_15);
			JLabel label_7 = new JLabel(myResize(img, coord[1], coord[0]));
			panel_15.add(label_7);

			JPanel panel_16 = new JPanel();
			panel.add(panel_16);
			JLabel label_6 = new JLabel(myResize(img, coord[1], coord[0]));
			panel_16.add(label_6);

			JPanel panel_6 = new JPanel();
			frame.getContentPane().add(panel_6);

			Object[] cols = { "Players" };
			Object[][] data = { { "Luciano" }, { "Stini" }, { "Sart" } };
			panel_6.setPreferredSize(new Dimension(200, panel_6.HEIGHT));
			panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));
			Dimension myPanSize = new Dimension(85, 30);

			JPanel panel_7 = new JPanel();
			panel_7.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
			panel_7.setMaximumSize(myPanSize);
			panel_6.add(panel_7);

			JLabel lblNewLabel = new JLabel("New label");
			panel_7.add(lblNewLabel);

			JPanel panel_8 = new JPanel();
			panel_8.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
			panel_8.setMaximumSize(myPanSize);
			panel_6.add(panel_8);

			JLabel lblNewLabel_1 = new JLabel("New label");
			panel_8.add(lblNewLabel_1);

			JPanel panel_9 = new JPanel();
			panel_9.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
			panel_9.setMaximumSize(myPanSize);
			panel_6.add(panel_9);

			JLabel lblNewLabel_2 = new JLabel("New label");
			panel_9.add(lblNewLabel_2);

			JPanel panel_10 = new JPanel();
			panel_10.setForeground(Color.WHITE);
			panel_10.setBackground(Color.BLACK);
			panel_10.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
			panel_10.setMaximumSize(myPanSize);
			panel_6.add(panel_10);

			JLabel label = new JLabel("New label");
			label.setForeground(Color.WHITE);
			panel_10.add(label);

			
			frame.setVisible(true);

			

			JButton showDialogButton = new JButton("Text Button");
			showDialogButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.remove(lblDsa);
					panel.validate();
					panel.invalidate();
					panel.repaint();
				}
			});
			// frame.getContentPane().add(showDialogButton);

		} catch (IOException e) {
			System.err.println("File immagine carte non trovato");
		}

	}

	private static ImageIcon myResize(BufferedImage img, int col, int row) {
		for (int j = 0; j < 3; j++) {
			for (int i = (col * 240) + 29; i <= (col * 240) + 213; i++)
				img.setRGB(i, (row * 360) + 358 + j, Color.black.getRGB());
		}
		for (int j = 0; j < 2; j++) {
			for (int i = (row * 360) + 29; i <= (row * 360) + 334; i++)
				img.setRGB((col * 240) + 238 + j, i, Color.black.getRGB());
		}
		ImageIcon img1 = new ImageIcon(img.getSubimage(col * 240, row * 360, 240, 360));
		img1 = new ImageIcon(img1.getImage().getScaledInstance(120, 180, Image.SCALE_DEFAULT));
		return img1;
	}


	public void prova(JPanel pnl) {
		JLabel lblRover = new JLabel(new ImageIcon("C:\\Users\\L\\Desktop\\uno_retro.png")) {
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				AffineTransform aT = g2.getTransform();
				Shape oldshape = g2.getClip();
				double x = getWidth() / 2.0;
				double y = getHeight() / 2.0;
				aT.rotate(Math.toRadians(90), x, y);
				g2.setTransform(aT);
				g2.setClip(oldshape);
				super.paintComponent(g);
			}
		};
		pnl.add(lblRover);
	}
}
