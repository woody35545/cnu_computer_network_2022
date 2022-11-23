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
	private static JTable table_ProxyTable;
	JButton btn_addrSettingReset;
	
	public static String HOST_IP_ADDR;
	public static String HOST_MAC_ADDR;
	public static String DEST_IP;
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
		m_LayerMgr.AddLayer(new StaticRouterGUI("StaticRouterGUI"));

		// Connect all currently existing layers
		m_LayerMgr.ConnectLayers(
				" NI ( *Ethernet ( *ARP ( *IP ( *TCP ( *ChatApp ( *ARPGUI ( ) ) *FileApp ( *ARPGUI ) ) ) ) +IP ( -Ethernet ) ) ");
		((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetUpperLayer(m_LayerMgr.GetLayer("ARPGUI"));

		System.out.println(m_LayerMgr.GetLayer("ARP").GetUpperLayer(0).GetLayerName());

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
		frmArpgui.setBounds(100, 100, 1145, 746);
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
		JPanel panel_addressSetting = new JPanel();
		panel_addressSetting.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Address Setting"));

		panel_addressSetting.setLayout(null);
		panel_addressSetting.setBounds(757, 10, 362, 197);
		frmArpgui.getContentPane().add(panel_addressSetting);
		JLabel ARP_1_3_1_1_1_1 = new JLabel("My MAC");
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
		JButton btn_addrSelect = new JButton("Select");
		JButton btn_addrSettingReset = new JButton("Reset");
		JButton btn_addrSet = new JButton("Set");

		btn_addrSet.setEnabled(false);
		JTextArea textArea_dstIpAddr = new JTextArea();

		btn_addrSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!Utils.checkIsIpFormatString(textArea_srcIpAddr.getText())
						|| !Utils.checkIsMacFormatString(textArea_srcMacAddr.getText())) {
					JOptionPane.showMessageDialog(null, "Please check address format");
				}

				else {

					HOST_MAC_ADDR = textArea_srcMacAddr.getText();
					HOST_IP_ADDR = textArea_srcIpAddr.getText();
					DEST_IP = textArea_dstIpAddr.getText();
					
					//DEST_IP = tf_targetIp
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
					
					
					getIpLayer().setIpHeaderDstIPAddr(Utils.convertAddrFormat(DEST_IP));
					
					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index);

					textArea_srcMacAddr.setEnabled(false);
					textArea_srcIpAddr.setEnabled(false);

					btn_sendArpRequest.setEnabled(true);
					btn_arpItemDelete.setEnabled(true);
					btn_arpAllDelete.setEnabled(true);
					btn_proxyArpAdd.setEnabled(true);
					btn_proxyArpDelete.setEnabled(true);
					btn_addrSettingReset.setEnabled(true);
					comboBox_nicList.setEditable(false);
					comboBox_nicList.setEnabled(false);
					btn_addrSet.setEnabled(false);
					btn_addrSelect.setEnabled(false);
				}
			}
		});
		btn_addrSet.setBounds(77, 161, 96, 23);
		panel_addressSetting.add(btn_addrSet);
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("My IP");
		ARP_1_3_1_1_1_1_1.setBounds(12, 99, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1);
		comboBox_nicList.setBounds(12, 36, 266, 23);
		panel_addressSetting.add(comboBox_nicList);

		btn_addrSettingReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea_srcMacAddr.setEnabled(true);
				textArea_srcIpAddr.setEnabled(true);

				btn_sendArpRequest.setEnabled(false);
				btn_arpItemDelete.setEnabled(false);
				btn_arpAllDelete.setEnabled(false);
				btn_proxyArpAdd.setEnabled(false);
				btn_proxyArpDelete.setEnabled(false);
				btn_addrSettingReset.setEnabled(false);
				// btn_fileSend.setEnabled(false);

				btn_addrSelect.setEnabled(true);
				comboBox_nicList.setEditable(true);
				comboBox_nicList.setEnabled(true);
				btn_addrSet.setEnabled(true);
				btn_addrSettingReset.setEnabled(false);

			}
		});
		btn_addrSettingReset.setEnabled(false);
		btn_addrSettingReset.setBounds(182, 161, 96, 23);
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
		
		textArea_dstIpAddr.setBounds(77, 130, 201, 21);
		panel_addressSetting.add(textArea_dstIpAddr);
		textArea_dstIpAddr.setText("169.254.32.212");
		textArea_dstIpAddr.setColumns(10);
		textArea_dstIpAddr.setBorder(new LineBorder(Color.GRAY));
		
		JLabel lblNewLabel = new JLabel("Target IP");
		lblNewLabel.setBounds(12, 134, 52, 15);
		panel_addressSetting.add(lblNewLabel);
		SetCombobox();
		
		JButton btn_routingTable = new JButton("Routing Table");
		btn_routingTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				routingTableGUI.setVisible(true);
			}
		});
		btn_routingTable.setBounds(950, 228, 169, 31);
		frmArpgui.getContentPane().add(btn_routingTable);
		
		JButton btn_send = new JButton("Send Packet");
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((IPLayer)m_LayerMgr.GetLayer("IP")).Send(new byte[] {0x00, 0x00}, 2);
			}
		});
		btn_send.setBounds(757, 228, 169, 31);
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
		return ((IPLayer)m_LayerMgr.GetLayer("IP"));
	}
	
	public ARPLayer getArpLayer() {
		return ((ARPLayer)m_LayerMgr.GetLayer("ARP"));

	}
	
	public EthernetLayer getEthernetLayer() {
		return ((EthernetLayer)m_LayerMgr.GetLayer("Ethernet"));
	}
}
