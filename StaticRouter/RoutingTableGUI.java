package StaticRouter;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

public class RoutingTableGUI extends JFrame {
	private RoutingTableAddForm routingTableAddForm = new RoutingTableAddForm();
	private JPanel contentPane;
	private static JTable JTable_routingTable;
	// static final RoutingTableManager routingTableManager = new
	// RoutingTableManager();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoutingTableGUI frame = new RoutingTableGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RoutingTableGUI() {
		initialize();
	}
	
	
	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 709, 287);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(12, 10, 671, 230);
		contentPane.add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 659, 187);
		panel.add(scrollPane);

		String header[] = { "Destination", "NetMask", "Gateway", "Flag", "Interface", "Metrics" };
		DefaultTableModel model = new DefaultTableModel(header, 30);
		JTable_routingTable = new JTable(model);

		scrollPane.setViewportView(JTable_routingTable);

		JButton btn_add = new JButton("add");
		btn_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				routingTableAddForm.setVisible(true);
			}
		});
		btn_add.setBounds(229, 207, 95, 23);
		panel.add(btn_add);

		JButton btn_delete = new JButton("delete");
		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected_row = JTable_routingTable.getSelectedRow();
				String value = String.valueOf(JTable_routingTable.getValueAt(selected_row, 0));
				System.out.println("sel row: " + selected_row);
				RoutingTableManager.deleteTableElement(value);
			}
		});
		btn_delete.setBounds(336, 207, 95, 23);
		panel.add(btn_delete);
		
	}
	
	public static void initTableValue(String[] pDataArr) {
		/*
		 * idx 1: Destination idx 2: NetMask idx 3: Gateway idx 4: Flag idx 5: Interface
		 * idx : Metric
		 */
		int idx = Integer.parseInt(pDataArr[0]);
		JTable_routingTable.setValueAt(pDataArr[1], idx, 0);
		JTable_routingTable.setValueAt(pDataArr[2], idx, 1);
		JTable_routingTable.setValueAt(pDataArr[3], idx, 2);
		JTable_routingTable.setValueAt(pDataArr[4], idx, 3);
		JTable_routingTable.setValueAt(pDataArr[5], idx, 4);
		JTable_routingTable.setValueAt(pDataArr[6], idx, 5);

	}

	public static void addElement(String pDestination, String pNetMask, String pGateway, String pFlag,
			String pInterface, String pMetric) {
		RoutingTableManager.addElementAndRefresh(pDestination, pNetMask, pGateway, pFlag, pInterface, pMetric);
	}

	public static void resetTableGui() {
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 6; j++) {
				JTable_routingTable.setValueAt("", i, j);
			}
		}
	}

//	public RoutingTableManager getRoutingTableManager() {
//		return this.routingTableManager;
//	}
}
