import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Font;
import javax.swing.JProgressBar;
public class ARPGUI extends JFrame implements BaseLayer {
	
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private static LayerManager m_LayerMgr = new LayerManager();
	
	private JTextArea textField_targetIp;
	private JFrame frmArpgui;
	private JTextArea textField;
	static JTextArea textField_1; // ARP Cache Table
	private JTextArea textField_2;
	private JTextArea textField_3;
	private JTextArea textField_4;
	
	private int selected_index;
	JComboBox comboBox = new JComboBox();
	private JTable table_ARPTable;
	private JTextField textField_chatContent;
	private JTable table_ProxyTable;
	JButton btn_addrSettingReset;
	private JTextArea textArea_chatView = new JTextArea();

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
		m_LayerMgr.AddLayer(new ARPGUI("ARPGUI"));
		m_LayerMgr.AddLayer(new ChatAppLayer("Chat"));
		
		// Connect all currently existing layers
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *TCP ( *Chat ( *ARPGUI ) ) ) +ARPGUI ) +IP ( -Ethernet ");
		System.out.println(m_LayerMgr.GetLayer("Ethernet").GetUpperLayer(1).GetLayerName());
		System.out.println(m_LayerMgr.GetLayer("IP").GetUnderLayer(1).GetLayerName());

		
	}
	/**
	 * Create the application.
	 */
	public ARPGUI(String pName) {
		this.pLayerName = pName;
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmArpgui = new JFrame();
		frmArpgui.setTitle("ARPGUI");
		frmArpgui.setBounds(100, 100, 1149, 675);
		frmArpgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmArpgui.getContentPane().setLayout(null);
		JPanel panel_ARP = new JPanel();
		panel_ARP.setBounds(27, 10, 315, 320);
		panel_ARP.setBorder(new TitledBorder(new LineBorder(Color.black,1),"ARP"));
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
		textField_targetIp.setText("168.188.129.2");
		textField_targetIp.setBounds(22, 285, 175, 25);
		panel_ARP.add(textField_targetIp);
		textField_targetIp.setColumns(10);
		JButton btn_sendArpRequest = new JButton("Send");
		btn_sendArpRequest.setEnabled(false);
		btn_sendArpRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String targetIp = textField_targetIp.getText();
				((ARPLayer) m_LayerMgr.GetLayer("ARP"))
						.setARPHeaderDstIp(Utils.convertStrFormatIpToByteFormat(targetIp));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).Send();
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
		proxy_ARP.setBorder(new TitledBorder(new LineBorder(Color.black,1),"Proxy ARP"));

		proxy_ARP.setBounds(383, 10, 342, 320);
		frmArpgui.getContentPane().add(proxy_ARP);
		proxy_ARP.setLayout(null);
		JButton btn_proxyArpAdd = new JButton("Add");
		btn_proxyArpAdd.setEnabled(false);
	    btn_proxyArpAdd.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent e) {
	          ARPLayer arpLayer = (ARPLayer)(m_LayerMgr.GetLayer("ARP"));
	          if(arpLayer!=null){
	             String device_textField = textField_2.getText();
	             String ip_textField = textField_3.getText();
	             String mac_textField = textField_4.getText();
	             arpLayer.addPROXYCacheTableElement(device_textField, ip_textField, mac_textField);
	             
	             
	             textField_2.setText("");
	             textField_3.setText("");
	             textField_4.setText("");
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
		textField_2 = new JTextArea();
		textField_2.setBounds(105, 183, 207, 21);
		proxy_ARP.add(textField_2);
		textField_2.setColumns(10);
		textField_3 = new JTextArea();
		textField_3.setColumns(10);
		textField_3.setBounds(105, 214, 207, 21);
		proxy_ARP.add(textField_3);
		textField_4 = new JTextArea();
		textField_4.setColumns(10);
		textField_4.setBounds(105, 245, 207, 21);
		proxy_ARP.add(textField_4);
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
		
		String proxy_header[] = { "Device", "IP Address", "MAC Address"};
		DefaultTableModel proxy_model = new DefaultTableModel(proxy_header, 30);
		table_ProxyTable = new JTable(proxy_model);
		scrollPane_2.setViewportView(table_ProxyTable);
		
		JPanel panel_garp = new JPanel();
		panel_garp.setBorder(new TitledBorder(new LineBorder(Color.black,1),"GARP"));

		panel_garp.setBounds(763, 29, 362, 116);
		frmArpgui.getContentPane().add(panel_garp);
		panel_garp.setLayout(null);
		JLabel ARP_1_3_1_1_1 = new JLabel("HW");
		ARP_1_3_1_1_1.setBounds(12, 51, 61, 15);
		panel_garp.add(ARP_1_3_1_1_1);
		JTextArea textField_srcMac = new JTextArea();
		textField_srcMac.setColumns(10);
		textField_srcMac.setBounds(77, 47, 201, 21);
		panel_garp.add(textField_srcMac);
		JButton btn_sendGarp = new JButton("Send");
		btn_sendGarp.setEnabled(false);
		btn_sendGarp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String srcMac = textField_srcMac.getText();
				((ARPLayer) m_LayerMgr.GetLayer("ARP"))
						.setARPHeaderSrcMac(Utils.convertStrFormatIpToByteFormat(srcMac));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).SendGARP();
			}
		});
		btn_sendGarp.setBounds(283, 47, 68, 23);
		panel_garp.add(btn_sendGarp);
		JLabel ARP_1_2 = new JLabel("Gratitous ARP");
		ARP_1_2.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1_2.setBounds(763, 10, 109, 15);
		frmArpgui.getContentPane().add(ARP_1_2);
		JPanel panel_addressSetting = new JPanel();
		panel_addressSetting.setBorder(new TitledBorder(new LineBorder(Color.black,1),"Address Setting"));

		panel_addressSetting.setLayout(null);
		panel_addressSetting.setBounds(763, 160, 362, 170);
		frmArpgui.getContentPane().add(panel_addressSetting);
		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(12, 74, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1);
		JTextArea textArea_srcMacAddr = new JTextArea();
		textArea_srcMacAddr.setEnabled(false);
		textArea_srcMacAddr.setEditable(false);
		textArea_srcMacAddr.setColumns(10);
		textArea_srcMacAddr.setBounds(77, 69, 201, 21);
		textArea_srcMacAddr.setText("0:C:29:D2:99:B3");
		panel_addressSetting.add(textArea_srcMacAddr);
		
		JTextArea textArea_srcIpAddr = new JTextArea();
		textArea_srcIpAddr.setEnabled(false);
		textArea_srcIpAddr.setEditable(false);
		textArea_srcIpAddr.setColumns(10);
		textArea_srcIpAddr.setBounds(77, 99, 201, 21);
		textArea_srcIpAddr.setText("168.188.129.1");
		panel_addressSetting.add(textArea_srcIpAddr);
		
		JButton btn_addrSelect = new JButton("Select");
		JButton btn_addrSettingReset = new JButton("Reset");
		JButton btn_addrSet = new JButton("Set");
		JButton btn_chatSet = new JButton("Set");

		btn_addrSet.setEnabled(false);
		
		btn_addrSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String srcMac = textArea_srcMacAddr.getText();
				String srcIP = textArea_srcIpAddr.getText();
				((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEthernetHeaderType(new byte[] { 0x08, 0x00 });
				((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"))
						.setEthernetHeaderSrcMacAddr(Utils.convertStrFormatMacToByteFormat(srcMac));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).setARPHeaderSrcIp(Utils.convertStrFormatIpToByteFormat(srcIP));
				((ARPLayer) m_LayerMgr.GetLayer("ARP"))
						.setARPHeaderSrcMac(Utils.convertStrFormatMacToByteFormat(srcMac));
				((IPLayer) m_LayerMgr.GetLayer("IP")).setIpHeaderSrcIPAddr(Utils.convertStrFormatIpToByteFormat(srcIP));
				((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index);
				
				

				textArea_srcMacAddr.setEnabled(false);
				textArea_srcIpAddr.setEnabled(false);
				
				btn_sendGarp.setEnabled(true);
				btn_sendArpRequest.setEnabled(true);
				btn_arpItemDelete.setEnabled(true);
				btn_arpAllDelete.setEnabled(true);
				btn_proxyArpAdd.setEnabled(true);
				btn_proxyArpDelete.setEnabled(true);
				btn_chatSet.setEnabled(true);
				btn_addrSettingReset.setEnabled(true);
				btn_addrSet.setEnabled(false);
				btn_addrSelect.setEnabled(false);

			}
		});
		btn_addrSet.setBounds(77, 137, 96, 23);
		panel_addressSetting.add(btn_addrSet);
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(12, 99, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1);
		comboBox.setBounds(12, 36, 266, 23);
		panel_addressSetting.add(comboBox);
		
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
				
				btn_addrSet.setEnabled(true);
				btn_addrSettingReset.setEnabled(false);

			}
		});
		btn_addrSettingReset.setEnabled(false);
		btn_addrSettingReset.setBounds(182, 137, 96, 23);
		panel_addressSetting.add(btn_addrSettingReset);
		btn_addrSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = comboBox.getSelectedItem().toString();
				selected_index = comboBox.getSelectedIndex();
				textArea_srcMacAddr.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index)
							.getHardwareAddress();
					String hexNumber;
					for (int i = 0; i < 6; i++) {
						hexNumber = Integer.toHexString(0xff & MacAddress[i]);
						textArea_srcMacAddr.append(hexNumber.toUpperCase());
						if (i != 5)
							textArea_srcMacAddr.append(":");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			

				textArea_srcMacAddr.setEditable(true);
				textArea_srcMacAddr.setEnabled(true);
				textArea_srcIpAddr.setEditable(true);
				textArea_srcIpAddr.setEnabled(true);
				btn_addrSet.setEnabled(true);
			}
		});
		btn_arpItemDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected_row = table_ARPTable.getSelectedRow();
				String value = String.valueOf(table_ARPTable.getValueAt(selected_row, 0));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).deleteARPCacheTableElement(value);
			}
		});
		btn_addrSelect.setBounds(282, 36, 68, 23);
		panel_addressSetting.add(btn_addrSelect);
		SetCombobox();
		JPanel panel_chatting = new JPanel();
		panel_chatting.setBorder(new TitledBorder(new LineBorder(Color.black,1),"Chatting"));

		panel_chatting.setBounds(27, 357, 698, 261);
		frmArpgui.getContentPane().add(panel_chatting);
		panel_chatting.setLayout(null);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 28, 490, 170);
		panel_chatting.add(scrollPane_1);
		scrollPane_1.setViewportView(textArea_chatView);
		textField_chatContent = new JTextField();
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
					((ChatAppLayer) m_LayerMgr.GetLayer("Chat")).setAppType((byte)0x00); 
					
					// Call ChatAppLayer.Send
					((ChatAppLayer) m_LayerMgr.GetLayer("Chat")).Send(contentByte,contentByte.length);
					
					// Reset value of chatContent textArea
					textField_chatContent.setText("");
					
				}
			}
		});
		
		btn_chatSend.setBounds(407, 208, 95, 32);
		panel_chatting.add(btn_chatSend);
		
		JPanel panel_chatSetting = new JPanel();
		panel_chatSetting.setBorder(new TitledBorder(new LineBorder(Color.black,1),"Chat Setting"));

		panel_chatSetting.setBounds(514, 28, 172, 223);
		panel_chatting.add(panel_chatSetting);
		panel_chatSetting.setLayout(null);
		
		JLabel chat_dst_ip = new JLabel("Chat Destination IP");
		chat_dst_ip.setBounds(12, 32, 148, 15);
		panel_chatSetting.add(chat_dst_ip);
		
		JTextArea textField_ChatDstIP = new JTextArea();
		textField_ChatDstIP.setBounds(12, 57, 148, 25);
		panel_chatSetting.add(textField_ChatDstIP);
		textField_ChatDstIP.setText("168.188.129.2");
		textField_ChatDstIP.setColumns(10);
		
		JLabel chat_dst_mac = new JLabel("Chat Destination MAC");
		chat_dst_mac.setBounds(12, 106, 148, 15);
		panel_chatSetting.add(chat_dst_mac);
		
		JTextArea textField_ChatDstMac = new JTextArea();
		textField_ChatDstMac.setBounds(12, 130, 148, 25);
		panel_chatSetting.add(textField_ChatDstMac);
		textField_ChatDstMac.setColumns(10);
		btn_chatSet.setBounds(12, 180, 148, 32);
		panel_chatSetting.add(btn_chatSet);
		
		btn_chatSet.setEnabled(false);
		btn_chatSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String chatDstIPStr = textField_ChatDstIP.getText();
				String chatDstMACStr =textField_ChatDstMac.getText();				
		
				((IPLayer) m_LayerMgr.GetLayer("IP")).setIpHeaderDstIPAddr(Utils.convertAddrFormat(chatDstIPStr));
				((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(chatDstMACStr));
				
				textField_ChatDstIP.setEditable(false);
				textField_ChatDstIP.setEnabled(false);
				textField_ChatDstMac.setEditable(false);
				textField_ChatDstMac.setEnabled(false);

				btn_chatSend.setEnabled(true);
				btn_chatSet.setEnabled(false);

			}
		});
		
		JPanel panel_fileTransfer = new JPanel();
		panel_fileTransfer.setBorder(new TitledBorder(new LineBorder(Color.black,1),"File Transfer"));

		panel_fileTransfer.setBounds(763, 360, 362, 258);
		frmArpgui.getContentPane().add(panel_fileTransfer);
		panel_fileTransfer.setLayout(null);
		
		JProgressBar progressBar_fileTransferProgressBar = new JProgressBar();
		progressBar_fileTransferProgressBar.setBounds(22, 208, 328, 40);
		panel_fileTransfer.add(progressBar_fileTransferProgressBar);
		
		JTextArea textField_filePath = new JTextArea();
		textField_filePath.setEditable(false);
		textField_filePath.setColumns(10);
		textField_filePath.setBounds(22, 166, 222, 32);
		panel_fileTransfer.add(textField_filePath);
		
		JButton btn_fileSend = new JButton("Send");
		btn_fileSend.setEnabled(false);
		btn_fileSend.setBounds(255, 166, 95, 32);
		panel_fileTransfer.add(btn_fileSend);
		
		JPanel panel_fileTransferSetting = new JPanel();
		panel_fileTransferSetting.setBorder(new TitledBorder(new LineBorder(Color.black,1),"File Transfer Setting"));

		panel_fileTransferSetting.setBounds(12, 23, 338, 129);
		panel_fileTransfer.add(panel_fileTransferSetting);
		panel_fileTransferSetting.setLayout(null);
		
		JTextArea textField_FileTransferDstIP = new JTextArea();
		textField_FileTransferDstIP.setBounds(9, 52, 141, 25);
		panel_fileTransferSetting.add(textField_FileTransferDstIP);
		textField_FileTransferDstIP.setText("168.188.129.2");
		textField_FileTransferDstIP.setColumns(10);
		
		JButton btn_fileTransferSet = new JButton("Set");
		btn_fileTransferSet.setBounds(9, 87, 310, 32);
		panel_fileTransferSetting.add(btn_fileTransferSet);
		btn_fileTransferSet.setEnabled(false);
		
		JTextArea textField_FileTransferDstMac = new JTextArea();
		textField_FileTransferDstMac.setBounds(178, 52, 141, 25);
		panel_fileTransferSetting.add(textField_FileTransferDstMac);
		textField_FileTransferDstMac.setColumns(10);
		
		JLabel fileTransfer_dst_mac_1 = new JLabel("Destination MAC");
		fileTransfer_dst_mac_1.setBounds(178, 28, 141, 15);
		panel_fileTransferSetting.add(fileTransfer_dst_mac_1);
		
		JLabel fileTransfer_dst_ip_1 = new JLabel("Destination IP");
		fileTransfer_dst_ip_1.setBounds(9, 27, 113, 15);
		panel_fileTransferSetting.add(fileTransfer_dst_ip_1);
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
			this.comboBox.addItem(m_pAdapterList.get(i).getDescription());
	}
	public void initTableValue(String[] pDataArr) {
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

	public void initProxyTableValue(String[] pDataArr) {
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


	public void resetTable() {
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 3; j++) {
				table_ARPTable.setValueAt("", i, j);
			}
		}
	}

	public void resetProxyTable() {
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
