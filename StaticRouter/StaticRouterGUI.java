package StaticRouter;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.sound.midi.Soundbank;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Font;

import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.SystemColor;

public class StaticRouterGUI extends JFrame implements BaseLayer {

	/*
	 * For easy setting while testing # idx 0 - 4: for Address Setting # idx 5 ~ :
	 * for Routing Table Setting
	 *
	 * > Usage example
	 *
	 * String [] setting_1 = {<Interface_0 MAC>,<Interface_0 IP>,<Interface_1MAC>,<Interface_1 IP>, <target IP>} 
	 * 
	 * String [] setting_2 = {<Interface_0 MAC>,<Interface_0 IP>,<Interface_1 MAC>,<Interface_1 IP>, <target IP>, <RoutingTable Entry 0>,
	 * <RoutingTable Entry 1> ...}
	 */

	/* 
	 * Environment #1
	 * Host A MAC: 00:0C:29:C7:D7:3E
	 * Host B MAC: 00:0C:29:C2:B1:50
	 * Router Interface_1 MAC: 00:0C:29:13:E4:39
	 * Router Interface_2 MAC: 00:0C:29:13:E4:4D
	 */
	
	/*---- Settings ----*/
	private static final int SETTING_NUMBER = 2; // -1: default setting
	
	/*-------------------- Setting #1 --------------------*/
	String[] setting_1_addrSetting = new String[] { "00:0C:29:D2:99:B3", "192.168.1.1", "00:0C:29:D2:99:BD", "192.168.2.1",
			"0.0.0.0" };
	String[][] setting_1_routingTableData = new String[][] {
			{ "0", "192.168.1.0", "255.255.255.0", "-", "U", "Interface_0", "-" },
			{ "1", "192.168.2.0", "255.255.255.0", "-", "U", "Interface_1", "-" },
			{ "2", "0.0.0.0", "0.0.0.0", "0.0.0.0", "UG", "-", "-" } };
	/*----------------------------------------------------*/

			
	/*-------------------- Setting #2 --------------------*/
	String[] setting_2_addrSetting = new String[] { "00:0C:29:B4:EF:6F", "192.168.1.2", "0:0:0:0:0:0", "0.0.0.0",
			"192.168.2.0" };
	String[][] setting_2_routingTableData = new String[][] {
			{ "0", "192.168.1.0", "255.255.255.0", "-", "U", "Interface_0", "-" },
			{ "1", "0.0.0.0", "0.0.0.0", "192.168.1.1", "UG", "Interface_0", "-" } };
	/*----------------------------------------------------*/

			
	/*-------------------- Setting #3 --------------------*/
	String[] setting_3_addrSetting = new String[] { "00:0C:29:26:BA:71", "192.168.2.2", "0:0:0:0:0:0", "0.0.0.0",
			"192.168.1.0" };
	String[][] setting_3_routingTableData = new String[][] {
		{ "0", "192.168.2.0", "255.255.255.0", "-", "U", "Interface_1", "-" },
		{ "1", "0.0.0.0", "0.0.0.0", "192.168.2.1", "UG", "Interface_1", "-" } };
	/*----------------------------------------------------*/
	
	/*---- Variables ----*/		
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private static LayerManager m_LayerMgr = new LayerManager();

	private JFileChooser fileChooser = new JFileChooser();
	private JTextArea textField_targetIp;
	private JFrame frmArpgui;
	private JTextArea textField;
	static JTextArea textField_1; // ARP Cache Table
	private JTextArea textField_proxyDeviceName;
	private JTextArea textField_proxyIpAddr;
	private JTextArea textField_proxyMacAddr;
	JButton btn_addrSettingReset;
	private int selected_index_0;// To store seleceted index for NIC comboBox 0
	private int selected_index_1;// To store seleceted index for NIC comboBox 1

	// nicList of interface 0
	JComboBox comboBox_nicList_0 = new JComboBox();
	// nicList of interface 1
	JComboBox comboBox_nicList_1 = new JComboBox();

	JTextArea textArea_srcMacAddr_0 = new JTextArea();
	JTextArea textArea_srcIpAddr_0 = new JTextArea();
	JTextArea textArea_srcMacAddr_1 = new JTextArea();
	JTextArea textArea_srcIpAddr_1 = new JTextArea();
	JTextArea textArea_dstIpAddr = new JTextArea();

	private static JTable table_ARPTable;
	private static JTable table_ProxyTable;

	public static String NODE_TYPE;
	// To store IP/MAC address of Inteface 0
	public static String IP_ADDR_0;
	public static String MAC_ADDR_0;
	// To store IP/MAC address of Inteface 1
	public static String IP_ADDR_1;
	public static String MAC_ADDR_1;
	// To store target ip address
	public static String DEST_IP;
	public static String ARP_DEST_IP_ADDR;
	// Init RoutingTableGUI
	public RoutingTableGUI routingTableGUI = new RoutingTableGUI();
	
	/*----------------*/		


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Initialize layers
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new StaticRouterGUI("StaticRouterGUI"));

		// Connect all currently existing layers
//		m_LayerMgr.ConnectLayers(
//				" NI ( *Ethernet ( *ARP ( *IP ( *StaticRouterGUI");
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *StaticRouterGUI ( ) ) ) *IP ( ");

		// Test
//		System.out.println(m_LayerMgr.GetLayer("Ethernet").GetUpperLayer(0).GetLayerName());
//
//		System.out.println(m_LayerMgr.GetLayer("Ethernet").GetUpperLayer(1).GetLayerName());
//		System.out.println(m_LayerMgr.GetLayer("IP").GetUnderLayer(0).GetLayerName());
//		System.out.println(m_LayerMgr.GetLayer("IP").GetUnderLayer(1).GetLayerName());
//		System.out.println(m_LayerMgr.GetLayer("ARP").GetUpperLayer(0).GetLayerName());

	}

	/**
	 * Create the application.
	 */
	public StaticRouterGUI(String pName) {
		this.pLayerName = pName;
		initNicCombobox(); // initialize NIC List comboBox
		initUserSetting();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmArpgui = new JFrame();
		frmArpgui.setForeground(Color.BLACK);
		frmArpgui.setTitle("ARPGUI");
		frmArpgui.setBounds(100, 100, 1145, 470);
		frmArpgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmArpgui.getContentPane().setLayout(null);
		JPanel panel_ARP = new JPanel();
		panel_ARP.setBounds(27, 10, 315, 361);
		panel_ARP.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "ARP"));
		frmArpgui.getContentPane().add(panel_ARP);
		panel_ARP.setLayout(null);
		JButton btn_arpItemDelete = new JButton("Item delete");
		btn_arpItemDelete.setEnabled(false);
		btn_arpItemDelete.setBounds(12, 223, 135, 25);
		panel_ARP.add(btn_arpItemDelete);
		JButton btn_arpAllDelete = new JButton("All delete");
		btn_arpAllDelete.setEnabled(false);
		btn_arpAllDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).deleteAllARPCacheTableElement();
			}
		});
		btn_arpAllDelete.setBounds(164, 223, 135, 25);
		panel_ARP.add(btn_arpAllDelete);
		textField_targetIp = new JTextArea();
		textField_targetIp.setBorder(new LineBorder(Color.gray));

		textField_targetIp.setText("169.254.32.212");
		textField_targetIp.setBounds(22, 285, 175, 25);
		panel_ARP.add(textField_targetIp);
		textField_targetIp.setColumns(10);
		JButton btn_sendArpRequest = new JButton("Send");
		btn_sendArpRequest.setEnabled(false);
		btn_sendArpRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Utils.checkIsIpFormatString(textField_targetIp.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				} else {

					ARP_DEST_IP_ADDR = textField_targetIp.getText();
					((ARPLayer) m_LayerMgr.GetLayer("ARP"))
							.setARPHeaderDstIp(Utils.convertAddrFormat(ARP_DEST_IP_ADDR));
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).Send();
				}
			}
		});
		btn_sendArpRequest.setBounds(209, 285, 100, 25);
		panel_ARP.add(btn_sendArpRequest);
		JLabel ARP_1_3 = new JLabel("Target IP");
		ARP_1_3.setBounds(22, 261, 133, 15);
		panel_ARP.add(ARP_1_3);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 20, 287, 195);
		panel_ARP.add(scrollPane);
		String header[] = { "IP Address", "MAC Address", "State" };
		DefaultTableModel model = new DefaultTableModel(header, 30);
		table_ARPTable = new JTable(model);
		scrollPane.setViewportView(table_ARPTable);

		JPanel proxy_ARP = new JPanel();
		proxy_ARP.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Proxy ARP"));

		proxy_ARP.setBounds(383, 10, 342, 361);
		frmArpgui.getContentPane().add(proxy_ARP);
		proxy_ARP.setLayout(null);
		JButton btn_proxyArpAdd = new JButton("Add");
		btn_proxyArpAdd.setEnabled(false);
		btn_proxyArpAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (textField_proxyDeviceName.getText().equals(null)
						|| textField_proxyDeviceName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please check device name");

				}

				else if (!Utils.checkIsIpFormatString(textField_proxyIpAddr.getText())
						|| !Utils.checkIsMacFormatString(textField_proxyMacAddr.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				}

				else {
					ARPLayer arpLayer = (ARPLayer) (m_LayerMgr.GetLayer("ARP"));
					if (arpLayer != null) {
						String device_textField = textField_proxyDeviceName.getText();
						String ip_textField = textField_proxyIpAddr.getText();
						String mac_textField = textField_proxyMacAddr.getText();
						arpLayer.addPROXYCacheTableElement(device_textField, ip_textField, mac_textField);

						textField_proxyDeviceName.setText("");
						textField_proxyIpAddr.setText("");
						textField_proxyMacAddr.setText("");
					}

				}
			}
		});
		btn_proxyArpAdd.setBounds(22, 287, 135, 23);
		proxy_ARP.add(btn_proxyArpAdd);
		JButton btn_proxyArpDelete = new JButton("Delete");
		btn_proxyArpDelete.setEnabled(false);
		btn_proxyArpDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected_row = table_ProxyTable.getSelectedRow();
				String value = String.valueOf(table_ProxyTable.getValueAt(selected_row, 1));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).deletePROXYCacheTableElement(value);
			}
		});
		btn_proxyArpDelete.setBounds(177, 287, 135, 23);
		proxy_ARP.add(btn_proxyArpDelete);
		textField_proxyDeviceName = new JTextArea();
		textField_proxyDeviceName.setBorder(new LineBorder(Color.gray));

		textField_proxyDeviceName.setBounds(105, 183, 207, 21);
		proxy_ARP.add(textField_proxyDeviceName);
		textField_proxyDeviceName.setColumns(10);
		textField_proxyIpAddr = new JTextArea();
		textField_proxyIpAddr.setBorder(new LineBorder(Color.gray));

		textField_proxyIpAddr.setColumns(10);
		textField_proxyIpAddr.setBounds(105, 214, 207, 21);
		proxy_ARP.add(textField_proxyIpAddr);
		textField_proxyMacAddr = new JTextArea();
		textField_proxyMacAddr.setBorder(new LineBorder(Color.gray));

		textField_proxyMacAddr.setColumns(10);
		textField_proxyMacAddr.setBounds(105, 245, 207, 21);
		proxy_ARP.add(textField_proxyMacAddr);
		JLabel ARP_1_3_1_1 = new JLabel("Device");
		ARP_1_3_1_1.setBounds(12, 188, 61, 15);
		proxy_ARP.add(ARP_1_3_1_1);
		JLabel ARP_1_3_1 = new JLabel("IP Address");
		ARP_1_3_1.setBounds(12, 219, 81, 15);
		proxy_ARP.add(ARP_1_3_1);
		JLabel ARP_1_3_1_2 = new JLabel("Mac Address");
		ARP_1_3_1_2.setBounds(12, 250, 81, 15);
		proxy_ARP.add(ARP_1_3_1_2);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 29, 318, 147);
		proxy_ARP.add(scrollPane_2);

		String proxy_header[] = { "Device", "IP Address", "MAC Address" };
		DefaultTableModel proxy_model = new DefaultTableModel(proxy_header, 30);
		table_ProxyTable = new JTable(proxy_model);
		scrollPane_2.setViewportView(table_ProxyTable);
		JPanel panel_addressSetting = new JPanel();
		panel_addressSetting.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Address Setting"));

		panel_addressSetting.setLayout(null);
		panel_addressSetting.setBounds(757, 10, 362, 361);
		frmArpgui.getContentPane().add(panel_addressSetting);
		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(22, 110, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1);
		textArea_srcMacAddr_0.setEnabled(false);
		textArea_srcMacAddr_0.setEditable(false);
		textArea_srcMacAddr_0.setBorder(new LineBorder(Color.gray));
		textArea_srcMacAddr_0.setColumns(10);
		textArea_srcMacAddr_0.setBounds(77, 106, 201, 21);
		panel_addressSetting.add(textArea_srcMacAddr_0);

		textArea_srcIpAddr_0.setEnabled(false);
		textArea_srcIpAddr_0.setEditable(false);
		textArea_srcIpAddr_0.setBorder(new LineBorder(Color.gray));
		textArea_srcIpAddr_0.setColumns(10);
		textArea_srcIpAddr_0.setBounds(77, 131, 201, 21);
		panel_addressSetting.add(textArea_srcIpAddr_0);
		JButton btn_nicSelect1 = new JButton("Select");
		btn_nicSelect1.setEnabled(false);
		JButton btn_addrSettingReset = new JButton("Reset");
		JButton btn_addrSet = new JButton("Set");

		btn_addrSet.setEnabled(false);
		btn_addrSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!Utils.checkIsIpFormatString(textArea_srcIpAddr_0.getText())
						|| !Utils.checkIsMacFormatString(textArea_srcMacAddr_0.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				}

				else {
					MAC_ADDR_0 = textArea_srcMacAddr_0.getText();
					IP_ADDR_0 = textArea_srcIpAddr_0.getText();
					DEST_IP = textArea_dstIpAddr.getText();

					if (NODE_TYPE == "ROUTER") {
						MAC_ADDR_1 = textArea_srcMacAddr_1.getText();
						IP_ADDR_1 = textArea_srcIpAddr_1.getText();
					}
					// DEST_IP = tf_targetIp
					String srcIP = textArea_srcIpAddr_0.getText();

					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEthernetHeaderType(new byte[] { 0x08, 0x00 });
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
							.setEthernetHeaderSrcMacAddr(Utils.convertStrFormatMacToByteFormat(MAC_ADDR_0));
					((ARPLayer) m_LayerMgr.GetLayer("ARP"))
							.setARPHeaderSrcIp(Utils.convertStrFormatIpToByteFormat(IP_ADDR_0));
					((ARPLayer) m_LayerMgr.GetLayer("ARP"))
							.setARPHeaderSrcMac(Utils.convertStrFormatMacToByteFormat(MAC_ADDR_0));
					((IPLayer) m_LayerMgr.GetLayer("IP"))
							.setIpHeaderSrcIPAddr(Utils.convertStrFormatIpToByteFormat(IP_ADDR_0));

					getIpLayer().setIpHeaderDstIPAddr(Utils.convertAddrFormat(DEST_IP));

					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index_0);

					textArea_srcMacAddr_0.setEnabled(false);
					textArea_srcIpAddr_0.setEnabled(false);
					textArea_srcMacAddr_1.setEnabled(false);
					textArea_srcIpAddr_1.setEnabled(false);
					btn_sendArpRequest.setEnabled(true);
					btn_arpItemDelete.setEnabled(true);
					btn_arpAllDelete.setEnabled(true);
					btn_proxyArpAdd.setEnabled(true);
					btn_proxyArpDelete.setEnabled(true);
					btn_addrSettingReset.setEnabled(true);
					comboBox_nicList_0.setEditable(false);
					comboBox_nicList_0.setEnabled(false);
					comboBox_nicList_1.setEditable(false);
					comboBox_nicList_1.setEnabled(false);
					btn_addrSet.setEnabled(false);
					btn_nicSelect1.setEnabled(false);
				}
			}
		});
		btn_addrSet.setBounds(77, 328, 96, 23);
		panel_addressSetting.add(btn_addrSet);
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(22, 135, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1);
		comboBox_nicList_0.setEnabled(false);
		comboBox_nicList_0.setEditable(true);
		comboBox_nicList_0.setBounds(12, 71, 266, 23);
		panel_addressSetting.add(comboBox_nicList_0);

		btn_addrSettingReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_srcMacAddr_0.setEnabled(true);
				textArea_srcIpAddr_0.setEnabled(true);
				textArea_srcMacAddr_1.setEnabled(true);
				textArea_srcIpAddr_1.setEnabled(true);
				btn_sendArpRequest.setEnabled(false);
				btn_arpItemDelete.setEnabled(false);
				btn_arpAllDelete.setEnabled(false);
				btn_proxyArpAdd.setEnabled(false);
				btn_proxyArpDelete.setEnabled(false);
				btn_addrSettingReset.setEnabled(false);
				// btn_fileSend.setEnabled(false);

				btn_nicSelect1.setEnabled(true);
				comboBox_nicList_0.setEditable(true);
				comboBox_nicList_0.setEnabled(true);
				comboBox_nicList_1.setEditable(true);
				comboBox_nicList_1.setEnabled(true);
				btn_addrSet.setEnabled(true);
				btn_addrSettingReset.setEnabled(false);

			}
		});
		btn_addrSettingReset.setEnabled(false);
		btn_addrSettingReset.setBounds(182, 328, 96, 23);
		panel_addressSetting.add(btn_addrSettingReset);
		btn_nicSelect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String selected = comboBox_nicList_0.getSelectedItem().toString();
				selected_index_0 = comboBox_nicList_0.getSelectedIndex();

				textArea_srcMacAddr_0.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_0)
							.getHardwareAddress();
					if (!(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_0).getAddresses()
							.toString().length() == 2)) {
						// System.out.println("select another one");

						String hexNumber;
						for (int i = 0; i < 6; i++) {
							hexNumber = Integer.toHexString(0xff & MacAddress[i]);
							textArea_srcMacAddr_0.append(hexNumber.toUpperCase());
							if (i != 5)
								textArea_srcMacAddr_0.append(":");

						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_0).getAddresses().toString()
						.length() == 2) {
					JOptionPane.showMessageDialog(null,
							"Please select another device\nBecause it doesn't have MAC address.");
				} else {
					textArea_srcMacAddr_0.setEditable(true);
					textArea_srcMacAddr_0.setEnabled(true);
					textArea_srcIpAddr_0.setEditable(true);
					textArea_srcIpAddr_0.setEnabled(true);
					btn_addrSet.setEnabled(true);
				}
			}
		});
		btn_arpItemDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected_row = table_ARPTable.getSelectedRow();
				if (selected_row != -1) {
					String value = String.valueOf(table_ARPTable.getValueAt(selected_row, 0));
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).deleteARPCacheTableElement(value);
				}
			}
		});
		btn_nicSelect1.setBounds(282, 71, 68, 23);
		panel_addressSetting.add(btn_nicSelect1);

		textArea_dstIpAddr.setBounds(77, 297, 201, 21);
		panel_addressSetting.add(textArea_dstIpAddr);
		textArea_dstIpAddr.setColumns(10);
		textArea_dstIpAddr.setBorder(new LineBorder(Color.GRAY));

		JLabel lblNewLabel = new JLabel("Target IP");
		lblNewLabel.setBounds(22, 301, 52, 15);
		panel_addressSetting.add(lblNewLabel);

		JButton btn_nicSelect2 = new JButton("Select");
		btn_nicSelect2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//

				String selected = comboBox_nicList_1.getSelectedItem().toString();
				// selected_index = comboBox_nicList_1.getSelectedIndex();

				textArea_srcMacAddr_1.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI"))
							.GetAdapterObject(comboBox_nicList_1.getSelectedIndex()).getHardwareAddress();
					if (!(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(comboBox_nicList_1.getSelectedIndex())
							.getAddresses().toString().length() == 2)) {
						// System.out.println("select another one");

						String hexNumber;
						for (int i = 0; i < 6; i++) {
							hexNumber = Integer.toHexString(0xff & MacAddress[i]);
							textArea_srcMacAddr_1.append(hexNumber.toUpperCase());
							if (i != 5)
								textArea_srcMacAddr_1.append(":");

						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_0).getAddresses().toString()
						.length() == 2) {
					JOptionPane.showMessageDialog(null,
							"Please select another device\nBecause it doesn't have MAC address.");
				} else {
					textArea_srcMacAddr_1.setEditable(true);
					textArea_srcMacAddr_1.setEnabled(true);
					textArea_srcIpAddr_1.setEditable(true);
					textArea_srcIpAddr_1.setEnabled(true);
					btn_addrSet.setEnabled(true);
				}
				//
			}
		});
		btn_nicSelect2.setEnabled(false);
		btn_nicSelect2.setBounds(282, 191, 68, 23);
		panel_addressSetting.add(btn_nicSelect2);
		comboBox_nicList_1.setEnabled(false);

		comboBox_nicList_1.setEditable(true);
		comboBox_nicList_1.setBounds(12, 191, 266, 23);
		panel_addressSetting.add(comboBox_nicList_1);

		textArea_srcMacAddr_1.setEnabled(false);
		textArea_srcMacAddr_1.setEditable(false);
		textArea_srcMacAddr_1.setColumns(10);
		textArea_srcMacAddr_1.setBorder(new LineBorder(Color.gray));
		textArea_srcMacAddr_1.setBounds(77, 224, 201, 21);
		panel_addressSetting.add(textArea_srcMacAddr_1);

		textArea_srcIpAddr_1.setEnabled(false);
		textArea_srcIpAddr_1.setEditable(false);
		textArea_srcIpAddr_1.setColumns(10);
		textArea_srcIpAddr_1.setBorder(new LineBorder(Color.gray));
		textArea_srcIpAddr_1.setBounds(77, 251, 201, 21);
		panel_addressSetting.add(textArea_srcIpAddr_1);

		JLabel ARP_1_3_1_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1_1.setBounds(22, 255, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1_1);

		JLabel ARP_1_3_1_1_1_1_2 = new JLabel("MAC");
		ARP_1_3_1_1_1_1_2.setBounds(22, 230, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_2);

		JComboBox comboBox_type = new JComboBox(new String[] { "HOST", "ROUTER" });
		comboBox_type.setBounds(87, 24, 191, 23);
		panel_addressSetting.add(comboBox_type);

		JButton btn_typeSelect = new JButton("Select");
		btn_typeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String selected = comboBox_type.getSelectedItem().toString();
				selected_index_0 = comboBox_type.getSelectedIndex();

				System.out.println(selected);
				NODE_TYPE = selected;

				if (NODE_TYPE == "HOST") {
					btn_nicSelect1.setEnabled(true);
					btn_nicSelect2.setEnabled(false);
					comboBox_nicList_0.setEnabled(true);
				} else if (NODE_TYPE == "ROUTER") {
					btn_nicSelect1.setEnabled(true);
					btn_nicSelect2.setEnabled(true);
					comboBox_nicList_0.setEnabled(true);
					comboBox_nicList_1.setEnabled(true);

				}
			}
		});
		btn_typeSelect.setBounds(282, 24, 68, 23);
		panel_addressSetting.add(btn_typeSelect);

		JLabel ARP_1_3_1_1_1_1_3 = new JLabel("Type");
		ARP_1_3_1_1_1_1_3.setBounds(22, 28, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_3);

		JLabel lblNewLabel_1 = new JLabel("Interface_0");
		lblNewLabel_1.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_1.setBounds(12, 53, 96, 15);
		panel_addressSetting.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("Interface_1");
		lblNewLabel_1_1.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_1_1.setBounds(12, 177, 96, 15);
		panel_addressSetting.add(lblNewLabel_1_1);

		JButton btn_routingTable = new JButton("Routing Table");
		btn_routingTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				routingTableGUI.setVisible(true);
			}
		});
		btn_routingTable.setBounds(937, 381, 169, 31);
		frmArpgui.getContentPane().add(btn_routingTable);

		JButton btn_send = new JButton("Send Packet");
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((IPLayer) m_LayerMgr.GetLayer("IP")).Send(new byte[] { 0x00, 0x00 }, 2);
			}
		});
		btn_send.setBounds(767, 381, 169, 31);
		frmArpgui.getContentPane().add(btn_send);
		frmArpgui.setVisible(true);
	}

	private void initNicCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();
		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++) {
			this.comboBox_nicList_0.addItem(m_pAdapterList.get(i).getDescription());
			this.comboBox_nicList_1.addItem(m_pAdapterList.get(i).getDescription());
		}
	}

	private void initUserSetting() {
		if (SETTING_NUMBER != -1) {
			String[] addrSetting = new String[] {};
			String[][] routingTableSetting = new String[][] {{}};
			
			
			if (SETTING_NUMBER == 1) {
				System.out.println("User setting 1");
				addrSetting = setting_1_addrSetting;
				routingTableSetting= setting_1_routingTableData;
			}
			else if (SETTING_NUMBER == 2) {
				System.out.println("User setting 2");
				addrSetting = setting_2_addrSetting;
				routingTableSetting= setting_2_routingTableData;
			}
			else if (SETTING_NUMBER == 3) {
				System.out.println("User setting 3");
				addrSetting = setting_3_addrSetting;
				routingTableSetting= setting_3_routingTableData;
			}
			RoutingTableManager.initTableDataSet(routingTableSetting);
			textArea_srcMacAddr_0.setText(addrSetting[0]);
			textArea_srcIpAddr_0.setText(addrSetting[1]);
			textArea_srcMacAddr_1.setText(addrSetting[2]);
			textArea_srcIpAddr_1.setText(addrSetting[3]);
			textArea_dstIpAddr.setText(addrSetting[4]);

		}
	}

	public static void initTableValue(String[] pDataArr) {
		/*
		 * pDataArr # index 0 - Index of Element # index 1 - IP Address # index 2 - MAC
		 * Address # index 3 - State
		 */
		int idx = Integer.parseInt(pDataArr[0]);
		// Initialize the IP Address corresponding to the index
		table_ARPTable.setValueAt(pDataArr[1], idx, 0);
		// Initialize the MAC Address corresponding to the index
		table_ARPTable.setValueAt(pDataArr[2], idx, 1);
		// Initialize the State corresponding to the index
		table_ARPTable.setValueAt(pDataArr[3], idx, 2);

	}

	public static void initProxyTableValue(String[] pDataArr) {
		/*
		 * pDataArr # index 0 - Index of Element # index 1 - IP Address # index 2 - MAC
		 * Address # index 3 - State
		 */
		int idx = Integer.parseInt(pDataArr[0]);
		// Initialize the IP Address corresponding to the index
		table_ProxyTable.setValueAt(pDataArr[1], idx, 0);
		// Initialize the MAC Address corresponding to the index
		table_ProxyTable.setValueAt(pDataArr[2], idx, 1);
		// Initialize the State corresponding to the index
		table_ProxyTable.setValueAt(pDataArr[3], idx, 2);

	}

	public static void resetArpTableGui() {
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 3; j++) {
				table_ARPTable.setValueAt("", i, j);
			}
		}
	}

	public static void resetProxyTable() {
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 3; j++) {
				table_ProxyTable.setValueAt("", i, j);
			}
		}
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer.add(nUnderLayerCount++, pUnderLayer);
		// nUpperLayerCount++;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}

	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUnderLayerCount || nUnderLayerCount < 0)
			return null;
		return p_UnderLayer.get(nindex);
	}

	public IPLayer getIpLayer() {
		return ((IPLayer) m_LayerMgr.GetLayer("IP"));
	}

	public ARPLayer getArpLayer() {
		return ((ARPLayer) m_LayerMgr.GetLayer("ARP"));

	}

	public EthernetLayer getEthernetLayer() {
		return ((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"));
	}
}
