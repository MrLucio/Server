package GUI;

import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.Dimension;

public class tabellaPlayers {

	private JFrame frame;
	private JTable table;
	private JPanel panel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					tabellaPlayers window = new tabellaPlayers();
					
					window.frame.setVisible(true);
					window.frame.getContentPane().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public tabellaPlayers() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		Object[][] data = { { "Luciano" }, { "Stini" }, { "Sart" } };
		Object[] cols = { "Players" };

		DefaultTableModel model = new DefaultTableModel(data, cols)
		{
			public Class getColumnClass(int column)
			{
				return getValueAt(0, 0).getClass();
			}
		};
		
		JTable table1 = new JTable( model )
		{
			private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
			private Border inside = new EmptyBorder(0, 1, 0, 1);
			private Border highlight = new CompoundBorder(outside, inside);

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				JComponent jc = (JComponent)c;

				// Add a border to the selected row

				/*if (isRowSelected(row))
					jc.setBorder( highlight );
				*/
				return c;
			}
		};
		table1.setSelectionBackground(new Color(0,0,0,255));
		table1.setEnabled(false);
		table1.setRowSelectionInterval(3, 3);
		table1.setIntercellSpacing(new Dimension(0, 0));
		table1.setBorder(new LineBorder(new Color(0, 0, 0)));
		table1.getColumnModel().getColumn(0).setPreferredWidth(100);
		table1.setRowHeight(35);
		
		table1.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		

		panel.add(table1);

		
		frame.getContentPane().setVisible(false);
		
		
	}

}
