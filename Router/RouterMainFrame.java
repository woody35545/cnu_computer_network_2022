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

public class RouterMainFrame extends JFrame {
	
	/*---- Variables for ARP Cache Table ----*/
	
	// Table header for Jtable_arpCacheTable
	private final String[] JtableArpCacheTable_Header = { "IP Address", "MAC Address", "State" };
	// Table model for Jtable_arpCacheTable
	DefaultTableModel JtableArpCacheTable_Model = new DefaultTableModel(JtableArpCacheTable_Header, ARPCacheTable.ARP_CACHE_TABLE_CAPACITY);
	// Declare GUI component for visualize information of ARPCacheTable data structure
	private JTable Jtable_arpCacheTable = new JTable(JtableArpCacheTable_Model);
	
	
	/*---- Variables for Routing Table ----*/
	
	// Table header for Jtable_routingTable
	private final String[] JtableRoutingTable_Header = { "Destination IP", "NetMask", "Gateway", "Flag", "Interface", "Metrics" };
	// Table model for Jtable_routingTable
	DefaultTableModel JtableRoutingTable_Model = new DefaultTableModel(JtableRoutingTable_Header, RoutingTable.ROUTING_TABLE_CAPACITY);
	// Declare GUI component for visualize information of RoutingTable data structure
	private JTable Jtable_routingTable = new JTable(JtableRoutingTable_Model);

	
	public RouterMainFrame() {
		this.initialize();
	}
	
	public void initialize() {
		// Align the frame to the center of the screen
		setLocationRelativeTo(null);  
		
		// Set initial size of frame
		setSize(1021,536);
		
		// Set layout to Absolute Layout
		getContentPane().setLayout(null);
		
	
		
		JPanel panel_arpCacheTablePanel = new JPanel();
		panel_arpCacheTablePanel.setBounds(53, 244, 496, 224);
		getContentPane().add(panel_arpCacheTablePanel);
		panel_arpCacheTablePanel.setLayout(null);
		

	
		JScrollPane scrollPane_forArpCacheTable = new JScrollPane();
		scrollPane_forArpCacheTable.setBounds(12, 27, 472, 187);
		panel_arpCacheTablePanel.add(scrollPane_forArpCacheTable);
		scrollPane_forArpCacheTable.setViewportView(Jtable_arpCacheTable);
		
		JLabel lblNewLabel_1 = new JLabel("ARP Cache Table");
		lblNewLabel_1.setBounds(12, 10, 115, 15);
		panel_arpCacheTablePanel.add(lblNewLabel_1);
		
		JPanel panel_routingTablePanel = new JPanel();
		panel_routingTablePanel.setBounds(53, 10, 909, 224);
		panel_routingTablePanel.setLayout(null);

		getContentPane().add(panel_routingTablePanel);
		
		JScrollPane scrollPane_forRoutingTable = new JScrollPane();
		scrollPane_forRoutingTable.setBounds(12, 25, 885, 189);
		panel_routingTablePanel.add(scrollPane_forRoutingTable);
		scrollPane_forRoutingTable.setViewportView(Jtable_routingTable);
		
		JLabel lblNewLabel = new JLabel("Routing Table");
		lblNewLabel.setBounds(12, 10, 108, 15);
		panel_routingTablePanel.add(lblNewLabel);
	
		// set frame's visibility to "True" for display
		this.setVisible(true);
	}
}
