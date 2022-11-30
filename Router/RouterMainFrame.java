package Router;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.Font;

public final class RouterMainFrame extends JFrame {
	
	/*---- Variables for ARP Cache Table ----*/
	
	// Table header for Jtable_arpCacheTable
	private final static String[] JtableArpCacheTable_Header = { "IP Address", "MAC Address", "State" };
	// Table model for Jtable_arpCacheTable
	static DefaultTableModel JtableArpCacheTable_Model = new DefaultTableModel(JtableArpCacheTable_Header, ARPCacheTable.ARP_CACHE_TABLE_CAPACITY);
	// Declare GUI component for visualize information of ARPCacheTable data structure
	private static JTable Jtable_arpCacheTable = new JTable(JtableArpCacheTable_Model);
	
	
	/*---- Variables for Routing Table ----*/
	
	// Table header for Jtable_routingTable
	private  final static String[] JtableRoutingTable_Header = { "Destination IP", "NetMask", "Gateway", "Flag", "Interface", "Metrics" };
	// Table model for Jtable_routingTable
	static DefaultTableModel JtableRoutingTable_Model = new DefaultTableModel(JtableRoutingTable_Header, RoutingTable.ROUTING_TABLE_CAPACITY);
	// Declare GUI component for visualize information of RoutingTable data structure
	private static JTable Jtable_routingTable = new JTable(JtableRoutingTable_Model);
	

	/*---- Variables for extra components ----*/
	private JTextField textfield_gateway;
	private JTextField textfield_netmask;
	private JTextField textfield_destination;
	JCheckBox checkbox_upFlag = new JCheckBox("Up");
	JCheckBox checkbox_gatewayFlag = new JCheckBox("Gateway");
	JCheckBox checkbox_hostFlag = new JCheckBox("Host");
	JComboBox comboBox_interface =new JComboBox(new String[] {"Interface 1", "Interface 2"});

	public RouterMainFrame() {
		this.initialize();
	}
	
	public void initialize() {

		setTitle("StaticRouter");

	
		// Set initial size of frame
		setSize(1300,557);
	
		// Align the frame to the center of the screen
		setLocationRelativeTo(null);  
		
		// Set layout to Absolute Layout
		getContentPane().setLayout(null);
		
	
		
		JPanel panel_arpCacheTablePanel = new JPanel();
		panel_arpCacheTablePanel.setBounds(53, 10, 909, 224);
		getContentPane().add(panel_arpCacheTablePanel);
		panel_arpCacheTablePanel.setLayout(null);
		

	
		JScrollPane scrollPane_forArpCacheTable = new JScrollPane();
		scrollPane_forArpCacheTable.setBounds(12, 27, 885, 187);
		panel_arpCacheTablePanel.add(scrollPane_forArpCacheTable);
		scrollPane_forArpCacheTable.setViewportView(Jtable_arpCacheTable);
		
		JLabel lblNewLabel_1 = new JLabel("ARP Cache Table");
		lblNewLabel_1.setBounds(12, 10, 115, 15);
		panel_arpCacheTablePanel.add(lblNewLabel_1);
		
		JPanel panel_routingTablePanel = new JPanel();
		panel_routingTablePanel.setBounds(53, 253, 909, 255);
		panel_routingTablePanel.setLayout(null);

		getContentPane().add(panel_routingTablePanel);
		
		JScrollPane scrollPane_forRoutingTable = new JScrollPane();
		scrollPane_forRoutingTable.setBounds(12, 25, 885, 189);
		panel_routingTablePanel.add(scrollPane_forRoutingTable);
		scrollPane_forRoutingTable.setViewportView(Jtable_routingTable);
		
		JLabel lblNewLabel = new JLabel("Routing Table");
		lblNewLabel.setBounds(12, 10, 108, 15);
		panel_routingTablePanel.add(lblNewLabel);
		
		JButton btn_routingTableEntryDelete = new JButton("Delete");
		btn_routingTableEntryDelete.setBounds(12, 224, 91, 23);
		panel_routingTablePanel.add(btn_routingTableEntryDelete);
		
		JPanel panel = new JPanel();
		panel.setBounds(974, 253, 306, 255);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Interface");
		lblNewLabel_2.setBounds(12, 136, 50, 15);
		panel.add(lblNewLabel_2);
		

		comboBox_interface.setBounds(109, 136, 176, 23);
		panel.add(comboBox_interface);
		
		JButton btn_add = new JButton("Add");
		btn_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String _flag = new String("");
				if(checkbox_upFlag.isSelected()) {
					_flag += "U";
				}
				if(checkbox_gatewayFlag.isSelected()) {
					_flag += "G";
				}
				if(checkbox_hostFlag.isSelected()) {
					_flag += "H";
				}
				RoutingTable.addElement(textfield_destination.getText(), textfield_netmask.getText(), textfield_gateway.getText(), _flag, comboBox_interface.getSelectedItem().toString(), "1");
				RoutingTable.showTable();
				
				/*---- reset fields ----*/
				textfield_destination.setText("");
				textfield_netmask.setText("");
				textfield_gateway.setText("");
				checkbox_upFlag.setSelected(false);
				checkbox_gatewayFlag.setSelected(false);
				checkbox_hostFlag.setSelected(false);
			}
		});
		btn_add.setBounds(109, 169, 176, 23);
		panel.add(btn_add);
		
		
		checkbox_hostFlag.setBounds(241, 107, 56, 23);
		panel.add(checkbox_hostFlag);
		
		checkbox_gatewayFlag.setBounds(161, 107, 76, 23);
		panel.add(checkbox_gatewayFlag);
		
		checkbox_upFlag.setBounds(109, 107, 48, 23);
		panel.add(checkbox_upFlag);
		
		JLabel lblNewLabel_1_1 = new JLabel("Flag");
		lblNewLabel_1_1.setBounds(12, 111, 50, 15);
		panel.add(lblNewLabel_1_1);
		
		JLabel lblGateway = new JLabel("Gateway");
		lblGateway.setBounds(12, 86, 85, 15);
		panel.add(lblGateway);
		
		textfield_gateway = new JTextField();
		textfield_gateway.setColumns(10);
		textfield_gateway.setBounds(109, 83, 176, 21);
		panel.add(textfield_gateway);
		
		textfield_netmask = new JTextField();
		textfield_netmask.setColumns(10);
		textfield_netmask.setBounds(109, 58, 176, 21);
		panel.add(textfield_netmask);
		
		JLabel lblNetmask = new JLabel("Netmask");
		lblNetmask.setBounds(12, 61, 85, 15);
		panel.add(lblNetmask);
		
		JLabel lblNewLabel_3 = new JLabel("Destination");
		lblNewLabel_3.setBounds(12, 36, 85, 15);
		panel.add(lblNewLabel_3);
		
		textfield_destination = new JTextField();
		textfield_destination.setColumns(10);
		textfield_destination.setBounds(109, 33, 176, 21);
		panel.add(textfield_destination);
	
		// set frame's visibility to "True" for display
		// setVisible must be call after placing all the components
		this.setVisible(false);
	}

	public static void resetARPCacheTableGUI() {
		for (int i = 0; i < ARPCacheTable.ARP_CACHE_TABLE_CAPACITY; i++) {
			for (int j = 0; j < 6; j++) {
				Jtable_arpCacheTable.setValueAt("", i, j);
			}
		}
	}
	
	public static void refreshARPCacheTableGUI() {
		resetARPCacheTableGUI();
		for (int i = 0; i < ARPCacheTable.size; i++) {
			Jtable_arpCacheTable.setValueAt(ARPCacheTable.ipAddress[i], i, 0);
			Jtable_arpCacheTable.setValueAt(ARPCacheTable.macAddress[i], i, 1);
			Jtable_arpCacheTable.setValueAt(ARPCacheTable.state[i], i, 2);

		}
	}
	public static void resetRoutingTableGUI() {
		for (int i = 0; i < RoutingTable.ROUTING_TABLE_CAPACITY; i++) {
			for (int j = 0; j < 6; j++) {
				Jtable_routingTable.setValueAt("", i, j);
			}
		}
	}
	
	public static void refreshRoutingTableGUI() {
		resetRoutingTableGUI();
		for (int i = 0; i < RoutingTable.size; i++) {
			Jtable_routingTable.setValueAt(RoutingTable.Destination[i], i, 0);
			Jtable_routingTable.setValueAt(RoutingTable.NetMask[i], i, 1);
			Jtable_routingTable.setValueAt(RoutingTable.Gateway[i], i, 2);
			Jtable_routingTable.setValueAt(RoutingTable.Flag[i], i, 3);
			Jtable_routingTable.setValueAt(RoutingTable.Interface[i], i, 4);
			Jtable_routingTable.setValueAt(RoutingTable.Metric[i], i, 5);

		}
	}
	

}
