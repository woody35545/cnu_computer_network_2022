import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
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
		frmArpgui.setBounds(100, 100, 1151, 722);
		frmArpgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmArpgui.getContentPane().setLayout(null);
		JLabel ARP_1 = new JLabel("ARP");
		ARP_1.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1.setBounds(27, 10, 50, 15);
		frmArpgui.getContentPane().add(ARP_1);
		JPanel ARP = new JPanel();
		ARP.setBounds(27, 29, 311, 301);
		frmArpgui.getContentPane().add(ARP);
		ARP.setLayout(null);
		JButton btn_arpItemDelete = new JButton("Item delete");
		btn_arpItemDelete.setEnabled(false);
		btn_arpItemDelete.setBounds(12, 213, 135, 25);
		ARP.add(btn_arpItemDelete);
		JButton btn_arpAllDelete = new JButton("All delete");
		btn_arpAllDelete.setEnabled(false);
		btn_arpAllDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).deleteAllARPCacheTableElement();
			}
		});
		btn_arpAllDelete.setBounds(164, 213, 135, 25);
		ARP.add(btn_arpAllDelete);
		textField_targetIp = new JTextArea();
		textField_targetIp.setText("168.188.129.2");
		textField_targetIp.setBounds(12, 270, 175, 25);
		ARP.add(textField_targetIp);
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
		btn_sendArpRequest.setBounds(199, 270, 100, 25);
		ARP.add(btn_sendArpRequest);
		JLabel ARP_1_3 = new JLabel("Target IP");
		ARP_1_3.setBounds(12, 246, 133, 15);
		ARP.add(ARP_1_3);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 287, 195);
		ARP.add(scrollPane);
		String header[] = { "IP Address", "MAC Address", "State" };
		DefaultTableModel model = new DefaultTableModel(header, 30);
		table_ARPTable = new JTable(model);
		scrollPane.setViewportView(table_ARPTable);
		
		
		JPanel proxy_ARP = new JPanel();
		proxy_ARP.setBounds(383, 29, 342, 301);
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
	    btn_proxyArpAdd.setBounds(12, 268, 135, 23);
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
	    btn_proxyArpDelete.setBounds(164, 268, 135, 23);
	    proxy_ARP.add(btn_proxyArpDelete);
		textField_2 = new JTextArea();
		textField_2.setBounds(105, 164, 207, 21);
		proxy_ARP.add(textField_2);
		textField_2.setColumns(10);
		textField_3 = new JTextArea();
		textField_3.setColumns(10);
		textField_3.setBounds(105, 195, 207, 21);
		proxy_ARP.add(textField_3);
		textField_4 = new JTextArea();
		textField_4.setColumns(10);
		textField_4.setBounds(105, 226, 207, 21);
		proxy_ARP.add(textField_4);
		JLabel ARP_1_3_1_1 = new JLabel("Device");
		ARP_1_3_1_1.setBounds(12, 169, 61, 15);
		proxy_ARP.add(ARP_1_3_1_1);
		JLabel ARP_1_3_1 = new JLabel("IP Address");
		ARP_1_3_1.setBounds(12, 200, 81, 15);
		proxy_ARP.add(ARP_1_3_1);
		JLabel ARP_1_3_1_2 = new JLabel("Mac Address");
		ARP_1_3_1_2.setBounds(12, 231, 81, 15);
		proxy_ARP.add(ARP_1_3_1_2);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 10, 318, 147);
		proxy_ARP.add(scrollPane_2);
		
		String proxy_header[] = { "Device", "IP Address", "MAC Address"};
		DefaultTableModel proxy_model = new DefaultTableModel(proxy_header, 30);
		table_ProxyTable = new JTable(proxy_model);
		scrollPane_2.setViewportView(table_ProxyTable);
		
		JPanel GARP = new JPanel();
		GARP.setBounds(763, 29, 362, 116);
		frmArpgui.getContentPane().add(GARP);
		GARP.setLayout(null);
		JLabel ARP_1_3_1_1_1 = new JLabel("HW");
		ARP_1_3_1_1_1.setBounds(12, 51, 61, 15);
		GARP.add(ARP_1_3_1_1_1);
		JTextArea textField_srcMac = new JTextArea();
		textField_srcMac.setColumns(10);
		textField_srcMac.setBounds(54, 47, 207, 21);
		GARP.add(textField_srcMac);
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
		btn_sendGarp.setBounds(271, 47, 91, 23);
		GARP.add(btn_sendGarp);
		JLabel ARP_1_1 = new JLabel("Proxy ARP");
		ARP_1_1.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1_1.setBounds(383, 10, 83, 15);
		frmArpgui.getContentPane().add(ARP_1_1);
		JLabel ARP_1_2 = new JLabel("Gratitous ARP");
		ARP_1_2.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1_2.setBounds(763, 10, 109, 15);
		frmArpgui.getContentPane().add(ARP_1_2);
		JPanel GARP_1 = new JPanel();
		GARP_1.setLayout(null);
		GARP_1.setBounds(763, 174, 362, 156);
		frmArpgui.getContentPane().add(GARP_1);
		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(12, 63, 64, 15);
		GARP_1.add(ARP_1_3_1_1_1_1);
		JTextArea textArea_srcMacAddr = new JTextArea();
		textArea_srcMacAddr.setEnabled(false);
		textArea_srcMacAddr.setEditable(false);
		textArea_srcMacAddr.setColumns(10);
		textArea_srcMacAddr.setBounds(77, 58, 201, 21);
		textArea_srcMacAddr.setText("0:C:29:D2:99:B3");
		GARP_1.add(textArea_srcMacAddr);
		
		JTextArea textArea_srcIpAddr = new JTextArea();
		textArea_srcIpAddr.setEnabled(false);
		textArea_srcIpAddr.setEditable(false);
		textArea_srcIpAddr.setColumns(10);
		textArea_srcIpAddr.setBounds(77, 88, 201, 21);
		textArea_srcIpAddr.setText("168.188.129.1");
		GARP_1.add(textArea_srcIpAddr);
		
		JButton btn_addrSelect = new JButton("Select");
		JButton btn_chatSet = new JButton("Set");
		JButton btn_addrSettingReset = new JButton("Reset");
		JButton btn_addrSet = new JButton("Set");
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
		btn_addrSet.setBounds(77, 126, 90, 23);
		GARP_1.add(btn_addrSet);
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(12, 88, 64, 15);
		GARP_1.add(ARP_1_3_1_1_1_1_1);
		comboBox.setBounds(12, 25, 266, 23);
		GARP_1.add(comboBox);
		
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
		btn_addrSettingReset.setBounds(188, 126, 90, 23);
		GARP_1.add(btn_addrSettingReset);
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
		btn_addrSelect.setBounds(282, 25, 80, 23);
		GARP_1.add(btn_addrSelect);
		SetCombobox();
		JLabel ARP_1_2_1 = new JLabel("My Address Setting");
		ARP_1_2_1.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1_2_1.setBounds(763, 155, 159, 15);
		frmArpgui.getContentPane().add(ARP_1_2_1);
		JPanel panel = new JPanel();
		panel.setBounds(27, 357, 698, 261);
		frmArpgui.getContentPane().add(panel);
		panel.setLayout(null);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 10, 490, 198);
		panel.add(scrollPane_1);
		JTextArea textArea_chatView = new JTextArea();
		scrollPane_1.setViewportView(textArea_chatView);
		textField_chatContent = new JTextField();
		textField_chatContent.setFont(new Font("±¼¸²", Font.PLAIN, 15));
		textField_chatContent.setBounds(12, 218, 383, 32);
		panel.add(textField_chatContent);
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
		
		btn_chatSend.setBounds(407, 218, 95, 32);
		panel.add(btn_chatSend);
		
		JLabel chat_dst_ip = new JLabel("Chat Destination IP");
		chat_dst_ip.setBounds(524, 41, 135, 15);
		panel.add(chat_dst_ip);
		
		JTextArea textField_ChatDstIP = new JTextArea();
		textField_ChatDstIP.setText("168.188.129.1");
		textField_ChatDstIP.setBounds(524, 66, 159, 25);
		panel.add(textField_ChatDstIP);
		textField_ChatDstIP.setColumns(10);
		
		JLabel chat_dst_mac = new JLabel("Chat Destination MAC");
		chat_dst_mac.setBounds(524, 105, 159, 15);
		panel.add(chat_dst_mac);
		
		JTextArea textField_ChatDstMac = new JTextArea();
		textField_ChatDstMac.setBounds(524, 129, 159, 25);
		panel.add(textField_ChatDstMac);
		textField_ChatDstMac.setColumns(10);
		
		JLabel ARP_1_2_1_1 = new JLabel("Chat Setting");
		ARP_1_2_1_1.setFont(new Font("굴림", Font.BOLD, 14));
		ARP_1_2_1_1.setBounds(514, 16, 159, 15);
		panel.add(ARP_1_2_1_1);
		
		btn_chatSet.setEnabled(false);
		btn_chatSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String chatDstIPStr = textField_ChatDstIP.getText();
				String chatDstMACStr =textField_ChatDstMac.getText();
				


				
				textField_ChatDstIP.setEditable(false);
				textField_ChatDstIP.setEnabled(false);
				textField_ChatDstMac.setEditable(false);
				textField_ChatDstMac.setEnabled(false);

				btn_chatSend.setEnabled(true);
				btn_chatSet.setEnabled(false);

			}
		});
		btn_chatSet.setBounds(524, 164, 159, 32);
		panel.add(btn_chatSet);
		JLabel lblNewLabel = new JLabel("Chatting");
		lblNewLabel.setFont(new Font("굴림", Font.BOLD, 14));
		lblNewLabel.setBounds(27, 340, 118, 15);
		frmArpgui.getContentPane().add(lblNewLabel);
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
