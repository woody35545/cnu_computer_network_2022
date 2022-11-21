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

	private int selected_index;
	JComboBox comboBox_nicList = new JComboBox();
	private static JTable table_ARPTable;
	private JTextField textField_chatContent;
	private static JTable table_ProxyTable;
	JButton btn_addrSettingReset;
	private JTextArea textArea_chatView = new JTextArea();
	public static JProgressBar progressBar;
	
	public static String HOST_IP_ADDR;
	public static String HOST_MAC_ADDR;
	public static String CHAT_DEST_IP_ADDR;
	public static String CHAT_DEST_MAC_ADDR;
	public static String FILE_DEST_IP_ADDR;
	public static String FILE_DEST_MAC_ADDR;
	public static String FILE_PATH;
	public static String FILE_NAME;
	public static String ARP_DEST_IP_ADDR;
	public RoutingTableGUI routingTableGUI = new RoutingTableGUI();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Initialize layers
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new TCPLayer("TCP"));
		m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
		m_LayerMgr.AddLayer(new FileTransferAppLayer("FileApp"));
		m_LayerMgr.AddLayer(new StaticRouterGUI("ARPGUI"));

		// Connect all currently existing layers
		m_LayerMgr.ConnectLayers(
				" NI ( *Ethernet ( *ARP ( *IP ( *TCP ( *ChatApp ( *ARPGUI ( ) ) *FileApp ( *ARPGUI ) ) ) ) +IP ( -Ethernet ) ) ");
		((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetUpperLayer(m_LayerMgr.GetLayer("ARPGUI"));

		System.out.println(m_LayerMgr.GetLayer("ARP").GetUpperLayer(0).GetLayerName());
		//routingTableManager
		//RoutingTable
		//routingtable.addElement("1", "1", "1", "1", "1", "1");
	//	routingtable.addElement("2", "1", "1", "1", "1", "1");

		//routingtable.showTable();

	}

	/**
	 * Create the application.
	 */
	public StaticRouterGUI(String pName) {
		this.pLayerName = pName;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmArpgui = new JFrame();
		frmArpgui.setForeground(Color.BLACK);
		frmArpgui.setTitle("ARPGUI");
		frmArpgui.setBounds(100, 100, 1166, 740);
		frmArpgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmArpgui.getContentPane().setLayout(null);
		JPanel panel_ARP = new JPanel();
		panel_ARP.setBounds(27, 10, 315, 320);
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

		proxy_ARP.setBounds(383, 10, 342, 320);
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

		JPanel panel_garp = new JPanel();
		panel_garp.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "GARP"));

		panel_garp.setBounds(763, 10, 362, 135);
		frmArpgui.getContentPane().add(panel_garp);
		panel_garp.setLayout(null);
		JLabel ARP_1_3_1_1_1 = new JLabel("HW");
		ARP_1_3_1_1_1.setBounds(10, 65, 61, 15);
		panel_garp.add(ARP_1_3_1_1_1);
		JTextArea textField_srcMac = new JTextArea();
		textField_srcMac.setBorder(new LineBorder(Color.gray));

		textField_srcMac.setColumns(10);
		textField_srcMac.setBounds(75, 61, 201, 21);
		panel_garp.add(textField_srcMac);
		JButton btn_sendGarp = new JButton("Send");
		btn_sendGarp.setEnabled(false);
		btn_sendGarp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Utils.checkIsMacFormatString(textField_srcMac.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				}
				String srcMac = textField_srcMac.getText();
				// Set ARP header's src mac to new one
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).setARPHeaderSrcMac(Utils.convertAddrFormat(srcMac));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).SendGARP();
			}
		});
		btn_sendGarp.setBounds(281, 61, 68, 23);
		panel_garp.add(btn_sendGarp);
		JPanel panel_addressSetting = new JPanel();
		panel_addressSetting.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Address Setting"));

		panel_addressSetting.setLayout(null);
		panel_addressSetting.setBounds(763, 160, 362, 170);
		frmArpgui.getContentPane().add(panel_addressSetting);
		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(12, 74, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1);
		JTextArea textArea_srcMacAddr = new JTextArea();
		textArea_srcMacAddr.setBorder(new LineBorder(Color.gray));
		textArea_srcMacAddr.setEnabled(false);
		textArea_srcMacAddr.setEditable(false);
		textArea_srcMacAddr.setColumns(10);
		textArea_srcMacAddr.setBounds(77, 69, 201, 21);
		textArea_srcMacAddr.setText("0:C:29:D2:99:B3");
		panel_addressSetting.add(textArea_srcMacAddr);

		JTextArea textArea_srcIpAddr = new JTextArea();
		textArea_srcIpAddr.setBorder(new LineBorder(Color.gray));

		textArea_srcIpAddr.setEnabled(false);
		textArea_srcIpAddr.setEditable(false);
		textArea_srcIpAddr.setColumns(10);
		textArea_srcIpAddr.setBounds(77, 99, 201, 21);
		textArea_srcIpAddr.setText("169.254.217.136");
		panel_addressSetting.add(textArea_srcIpAddr);
		JButton btn_fileOpen = new JButton("Open");
		btn_fileOpen.setEnabled(false);
		JButton btn_addrSelect = new JButton("Select");
		JButton btn_addrSettingReset = new JButton("Reset");
		JButton btn_addrSet = new JButton("Set");
		JButton btn_chatSet = new JButton("Set");
		JButton btn_fileTransferSet = new JButton("Set");
		JTextArea textField_FileTransferDstMac = new JTextArea();
		JTextArea textField_FileTransferDstIP = new JTextArea();
		textField_FileTransferDstIP.setBorder(new LineBorder(Color.GRAY));
		JLabel lbl_fileSize = new JLabel("");

		JButton btn_fileSend = new JButton("Send");
		JTextArea textField_filePath = new JTextArea();

		btn_fileTransferSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!Utils.checkIsIpFormatString(textField_FileTransferDstIP.getText())
						|| !Utils.checkIsMacFormatString(textField_FileTransferDstMac.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				} else if (textField_filePath.getText().equals(null) || textField_filePath.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please select file");
				}

				else {
					FILE_DEST_MAC_ADDR = textField_FileTransferDstMac.getText();
					FILE_DEST_IP_ADDR = textField_FileTransferDstIP.getText();
					FILE_PATH = textField_filePath.getText();
					
					lbl_fileSize.setText(Integer.toString(Utils.getFileLength(textField_filePath.getText())) + " Bytes");
					
					textField_FileTransferDstIP.setEnabled(false);
					textField_FileTransferDstMac.setEnabled(false);
					btn_fileTransferSet.setEnabled(false);
					btn_fileOpen.setEnabled(false);
					btn_fileSend.setEnabled(true);
					
				
				}
			}
		});

		btn_addrSet.setEnabled(false);

		btn_addrSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!Utils.checkIsIpFormatString(textArea_srcIpAddr.getText())
						|| !Utils.checkIsMacFormatString(textArea_srcMacAddr.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				}

				else {

					HOST_MAC_ADDR = textArea_srcMacAddr.getText();
					HOST_IP_ADDR = textArea_srcIpAddr.getText();

					String srcIP = textArea_srcIpAddr.getText();

					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEthernetHeaderType(new byte[] { 0x08, 0x00 });
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
							.setEthernetHeaderSrcMacAddr(Utils.convertStrFormatMacToByteFormat(HOST_MAC_ADDR));
					((ARPLayer) m_LayerMgr.GetLayer("ARP"))
							.setARPHeaderSrcIp(Utils.convertStrFormatIpToByteFormat(HOST_IP_ADDR));
					((ARPLayer) m_LayerMgr.GetLayer("ARP"))
							.setARPHeaderSrcMac(Utils.convertStrFormatMacToByteFormat(HOST_MAC_ADDR));
					((IPLayer) m_LayerMgr.GetLayer("IP"))
							.setIpHeaderSrcIPAddr(Utils.convertStrFormatIpToByteFormat(HOST_IP_ADDR));
					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index);

					textArea_srcMacAddr.setEnabled(false);
					textArea_srcIpAddr.setEnabled(false);

					btn_sendGarp.setEnabled(true);
					btn_sendArpRequest.setEnabled(true);
					btn_arpItemDelete.setEnabled(true);
					btn_arpAllDelete.setEnabled(true);
					btn_proxyArpAdd.setEnabled(true);
					btn_fileTransferSet.setEnabled(true);
					btn_proxyArpDelete.setEnabled(true);
					btn_chatSet.setEnabled(true);
					btn_addrSettingReset.setEnabled(true);
					btn_fileOpen.setEnabled(true);
					comboBox_nicList.setEditable(false);
					comboBox_nicList.setEnabled(false);
					btn_addrSet.setEnabled(false);
					btn_addrSelect.setEnabled(false);
				}
			}
		});
		btn_addrSet.setBounds(77, 137, 96, 23);
		panel_addressSetting.add(btn_addrSet);
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(12, 99, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1);
		comboBox_nicList.setBounds(12, 36, 266, 23);
		panel_addressSetting.add(comboBox_nicList);

		btn_addrSettingReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_srcMacAddr.setEnabled(true);
				textArea_srcIpAddr.setEnabled(true);

				btn_sendGarp.setEnabled(false);
				btn_sendArpRequest.setEnabled(false);
				btn_arpItemDelete.setEnabled(false);
				btn_arpAllDelete.setEnabled(false);
				btn_proxyArpAdd.setEnabled(false);
				btn_proxyArpDelete.setEnabled(false);
				btn_chatSet.setEnabled(false);
				btn_addrSettingReset.setEnabled(false);
				btn_fileTransferSet.setEnabled(false);
				// btn_fileSend.setEnabled(false);

				btn_addrSelect.setEnabled(true);
				comboBox_nicList.setEditable(true);
				comboBox_nicList.setEnabled(true);
				btn_addrSet.setEnabled(true);
				btn_addrSettingReset.setEnabled(false);

			}
		});
		btn_addrSettingReset.setEnabled(false);
		btn_addrSettingReset.setBounds(182, 137, 96, 23);
		panel_addressSetting.add(btn_addrSettingReset);
		btn_addrSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				String selected = comboBox_nicList.getSelectedItem().toString();
				selected_index = comboBox_nicList.getSelectedIndex();
				
			
				textArea_srcMacAddr.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index)
							.getHardwareAddress();
					if(!(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index).getAddresses().toString().length()==2)) {
						//System.out.println("select another one");
					
					String hexNumber;
					for (int i = 0; i < 6; i++) {
						hexNumber = Integer.toHexString(0xff & MacAddress[i]);
						textArea_srcMacAddr.append(hexNumber.toUpperCase());
						if (i != 5)
							textArea_srcMacAddr.append(":");
					
				}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index).getAddresses().toString().length()==2) {
					JOptionPane.showMessageDialog(null, "Please select another device\nBecause it doesn't have MAC address.");
				}
				else {
				textArea_srcMacAddr.setEditable(true);
				textArea_srcMacAddr.setEnabled(true);
				textArea_srcIpAddr.setEditable(true);
				textArea_srcIpAddr.setEnabled(true);
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
		btn_addrSelect.setBounds(282, 36, 68, 23);
		panel_addressSetting.add(btn_addrSelect);
		SetCombobox();
		JPanel panel_chatting = new JPanel();
		panel_chatting.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1), "Chatting"));

		panel_chatting.setBounds(27, 357, 698, 261);
		frmArpgui.getContentPane().add(panel_chatting);
		panel_chatting.setLayout(null);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 28, 490, 170);
		panel_chatting.add(scrollPane_1);
		textArea_chatView.setEditable(false);
		scrollPane_1.setViewportView(textArea_chatView);
		textField_chatContent = new JTextField();
		textField_chatContent.setEnabled(false);
		textField_chatContent.setFont(new Font("±¼¸²", Font.PLAIN, 15));
		textField_chatContent.setBounds(12, 208, 383, 32);
		panel_chatting.add(textField_chatContent);
		textField_chatContent.setColumns(10);
		JButton btn_chatSend = new JButton("Send");
		btn_chatSend.setEnabled(false);
		btn_chatSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Assign the chat content entered by the user
				String contentStr = textField_chatContent.getText();
				if (contentStr.length() != 0 && contentStr != null && !contentStr.isEmpty()) {
					// Append contents to ChatView
					textArea_chatView.append("SEND >> " + contentStr + "\n");

					// Send byte type content to ChatApp Layer
					byte[] contentByte = contentStr.getBytes();

					// Set app type to chat app(0x00)
					((ChatAppLayer) m_LayerMgr.GetLayer("ChatApp")).setAppType((byte) 0x00);

					// Call ChatAppLayer.Send

					((ChatAppLayer) m_LayerMgr.GetLayer("ChatApp")).Send(contentByte, contentByte.length);

					// Reset value of chatContent textArea
					textField_chatContent.setText("");

				}
			}
		});

		btn_chatSend.setBounds(407, 208, 95, 32);
		panel_chatting.add(btn_chatSend);

		JPanel panel_chatSetting = new JPanel();
		panel_chatSetting.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1), "Chat Setting"));

		panel_chatSetting.setBounds(514, 28, 172, 223);
		panel_chatting.add(panel_chatSetting);
		panel_chatSetting.setLayout(null);

		JLabel chat_dst_ip = new JLabel("Chat Destination IP");
		chat_dst_ip.setBounds(12, 32, 148, 15);
		panel_chatSetting.add(chat_dst_ip);

		JTextArea textField_ChatDstIP = new JTextArea();
		textField_ChatDstIP.setBorder(new LineBorder(Color.gray));

		textField_ChatDstIP.setBounds(12, 57, 148, 25);
		panel_chatSetting.add(textField_ChatDstIP);
		textField_ChatDstIP.setText("169.254.32.212");
		textField_ChatDstIP.setColumns(10);

		JLabel chat_dst_mac = new JLabel("Chat Destination MAC");
		chat_dst_mac.setBounds(12, 106, 148, 15);
		panel_chatSetting.add(chat_dst_mac);

		JTextArea textField_ChatDstMac = new JTextArea();
		textField_ChatDstMac.setBorder(new LineBorder(Color.gray));

		textField_ChatDstMac.setBounds(12, 130, 148, 25);
		panel_chatSetting.add(textField_ChatDstMac);
		textField_ChatDstMac.setColumns(10);
		btn_chatSet.setBounds(12, 180, 148, 32);
		panel_chatSetting.add(btn_chatSet);

		btn_chatSet.setEnabled(false);
		btn_chatSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!Utils.checkIsIpFormatString(textField_ChatDstIP.getText())
						|| !Utils.checkIsMacFormatString(textField_ChatDstMac.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				} else {
					CHAT_DEST_IP_ADDR = textField_ChatDstIP.getText();
					CHAT_DEST_MAC_ADDR = textField_ChatDstMac.getText();

					((IPLayer) m_LayerMgr.GetLayer("IP"))
							.setIpHeaderDstIPAddr(Utils.convertAddrFormat(CHAT_DEST_IP_ADDR));
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
							.setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(CHAT_DEST_MAC_ADDR));

					textField_ChatDstIP.setEditable(false);
					textField_ChatDstIP.setEnabled(false);
					textField_ChatDstMac.setEditable(false);
					textField_ChatDstMac.setEnabled(false);
					
					textField_chatContent.setEnabled(true);
					btn_chatSend.setEnabled(true);
					btn_chatSet.setEnabled(false);
				}
			}
		});

		JPanel panel_fileTransfer = new JPanel();
		panel_fileTransfer.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "File Transfer"));

		panel_fileTransfer.setBounds(763, 360, 362, 258);
		frmArpgui.getContentPane().add(panel_fileTransfer);
		panel_fileTransfer.setLayout(null);

		JPanel panel_fileTransferSetting = new JPanel();
		panel_fileTransferSetting.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "File Transfer Setting"));

		panel_fileTransferSetting.setBounds(12, 23, 338, 167);
		panel_fileTransfer.add(panel_fileTransferSetting);
		panel_fileTransferSetting.setLayout(null);

		textField_FileTransferDstIP.setBounds(9, 46, 141, 25);
		panel_fileTransferSetting.add(textField_FileTransferDstIP);
		textField_FileTransferDstIP.setText("169.254.32.212");
		textField_FileTransferDstIP.setColumns(10);

		btn_fileTransferSet.setBounds(9, 133, 319, 25);
		panel_fileTransferSetting.add(btn_fileTransferSet);
		btn_fileTransferSet.setEnabled(false);

		textField_FileTransferDstMac.setBorder(new LineBorder(Color.gray));
		textField_FileTransferDstMac.setBounds(187, 47, 141, 25);
		panel_fileTransferSetting.add(textField_FileTransferDstMac);
		textField_FileTransferDstMac.setColumns(10);

		JLabel fileTransfer_dst_mac_1 = new JLabel("Destination MAC");
		fileTransfer_dst_mac_1.setBounds(187, 21, 141, 15);
		panel_fileTransferSetting.add(fileTransfer_dst_mac_1);

		JLabel fileTransfer_dst_ip_1 = new JLabel("Destination IP");
		fileTransfer_dst_ip_1.setBounds(9, 21, 113, 15);
		panel_fileTransferSetting.add(fileTransfer_dst_ip_1);

		textField_filePath.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textField_filePath.setBounds(9, 98, 226, 25);
		panel_fileTransferSetting.add(textField_filePath);
		textField_filePath.setBorder(new LineBorder(Color.gray, 1));
		textField_filePath.setEditable(false);
		textField_filePath.setColumns(10);
		btn_fileOpen.setBounds(245, 98, 83, 25);
		panel_fileTransferSetting.add(btn_fileOpen);
		btn_fileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFrame fileChooseWindow = new JFrame();
				int result = fileChooser.showOpenDialog(fileChooseWindow);

				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
				
					// In Java, you have to put back slash twice when entering the fild path.
					String selectedFilePath = selectedFile.toString().replace("\\", "\\\\");
					// pMacStr.split(Pattern.quote(":"));
					FILE_NAME = selectedFilePath.split(Pattern.quote("\\\\"))[(selectedFilePath.split(Pattern.quote("\\\\")).length) - 1];
					System.out.println(FILE_NAME);
					textField_filePath.setText(selectedFilePath.toString());

					System.out.println(selectedFilePath);
				}

			}
		});

		//Container container = getContentPane();
		textField_chatContent.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_ENTER) {
					// Assign the chat content entered by the user
					String contentStr = textField_chatContent.getText();
					if (contentStr.length() != 0 && contentStr != null && !contentStr.isEmpty()) {
						// Append contents to ChatView
						textArea_chatView.append("SEND >> " + contentStr + "\n");

						// Send byte type content to ChatApp Layer
						byte[] contentByte = contentStr.getBytes();

						// Set app type to chat app(0x00)
						((ChatAppLayer) m_LayerMgr.GetLayer("ChatApp")).setAppType((byte) 0x00);

						// Call ChatAppLayer.Send
						((ChatAppLayer) m_LayerMgr.GetLayer("ChatApp")).Send(contentByte, contentByte.length);

						// Reset value of chatContent textArea
						textField_chatContent.setText("");

					}
				}
				
			}
		});
		
		
	
		JLabel lbl_filePath = new JLabel("File Path");
		lbl_filePath.setBounds(9, 81, 113, 15);
		panel_fileTransferSetting.add(lbl_filePath);
		
		lbl_fileSize.setBounds(73, 81, 162, 15);
		panel_fileTransferSetting.add(lbl_fileSize);

		progressBar = new JProgressBar(0,100);
		progressBar.setStringPainted(true);
		progressBar.setBounds(22, 200, 234, 32);
		panel_fileTransfer.add(progressBar);

		btn_fileSend.setEnabled(false);
		btn_fileSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Make file to byte[]
				byte[] fileToByteArr = Utils.convertFileToByte(FILE_PATH);
				FILE_DEST_IP_ADDR = textField_FileTransferDstIP.getText();
				FILE_DEST_MAC_ADDR = textField_FileTransferDstMac.getText();

				((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(textField_FileTransferDstMac.getText()));;
				// Send to File transfer application layer
				((FileTransferAppLayer)m_LayerMgr.GetLayer("FileApp")).setFileName(FILE_NAME);

				((FileTransferAppLayer) m_LayerMgr.GetLayer("FileApp")).Send(fileToByteArr,fileToByteArr.length);

			}
		});
		btn_fileSend.setBounds(266, 200, 74, 32);
		panel_fileTransfer.add(btn_fileSend);
		
		JButton btn_routingTable = new JButton("routing table");
		btn_routingTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				routingTableGUI.setVisible(true);
			}
		});
		btn_routingTable.setBounds(773, 628, 136, 48);
		frmArpgui.getContentPane().add(btn_routingTable);
		
		JButton btn_send = new JButton("Send");
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((IPLayer)m_LayerMgr.GetLayer("IP")).Send(new byte[] {0x00, 0x00}, 2);
			}
		});
		btn_send.setBounds(921, 628, 136, 48);
		frmArpgui.getContentPane().add(btn_send);
		frmArpgui.setVisible(true);
	}

	private void SetCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();
		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++)
			this.comboBox_nicList.addItem(m_pAdapterList.get(i).getDescription());
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

	public void appendToChatView(String pMessage) {
		textArea_chatView.append("RECEIVED >> " + pMessage + "\n");
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
}
