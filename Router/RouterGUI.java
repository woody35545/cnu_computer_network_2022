package Router;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import Router.ARPLayer;
import Router.BaseLayer;
import Router.EthernetLayer;
import Router.IPLayer;
import Router.LayerManager;
import Router.NILayer;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class RouterGUI extends JFrame implements BaseLayer{
	
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private static LayerManager m_LayerMgr = new LayerManager();
	RouterMainFrame _router_main_frame = new RouterMainFrame();
	
	JTextArea textArea_srcMacAddr_port2 = new JTextArea();
	JTextArea textArea_srcIpAddr_port2 = new JTextArea();

	public static String NODE_TYPE;

	JComboBox comboBox_nicList_1 = new JComboBox();
	JComboBox comboBox_nicList_2 = new JComboBox();
	
	private int selected_index_1;// To store seleceted index for NIC comboBox 0
	private int selected_index_2;// To store seleceted index for NIC comboBox 1
	
	// To store IP/MAC address of Port 1
	public static String IP_ADDR_PORT1;
	public static String MAC_ADDR_PORT1;
	// To store IP/MAC address of Port 2
	public static String IP_ADDR_PORT2;
	public static String MAC_ADDR_PORT2;
	
	
	public RouterGUI(String pName) {
		this.pLayerName = pName;
		this.initNicCombobox();
		this.initialize();

	}
	
	public void initialize() {
		setSize(743,340);
		getContentPane().setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null); 
		JPanel panel_addressSetting = new JPanel();
		panel_addressSetting.setLayout(null);
		panel_addressSetting.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Address Setting"));
		panel_addressSetting.setBounds(0, 25, 719, 224);
		getContentPane().add(panel_addressSetting);
		
		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(22, 131, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1);
		
		JTextArea textArea_srcMacAddr_port1 = new JTextArea();
		textArea_srcMacAddr_port1.setColumns(10);
		textArea_srcMacAddr_port1.setBorder(new LineBorder(Color.gray));
		textArea_srcMacAddr_port1.setBounds(77, 127, 201, 21);
		panel_addressSetting.add(textArea_srcMacAddr_port1);
		
		JTextArea textArea_srcIpAddr_port1 = new JTextArea();
		textArea_srcIpAddr_port1.setColumns(10);
		textArea_srcIpAddr_port1.setBorder(new LineBorder(Color.gray));
		textArea_srcIpAddr_port1.setBounds(77, 152, 201, 21);
		panel_addressSetting.add(textArea_srcIpAddr_port1);
		
		JButton btn_addrSet = new JButton("Set");
		btn_addrSet.setEnabled(false);
		btn_addrSet.setBounds(77, 328, 96, 23);
		panel_addressSetting.add(btn_addrSet);
		
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(22, 156, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1);
		comboBox_nicList_1.setEditable(true);
		comboBox_nicList_1.setBounds(12, 92, 266, 23);
		panel_addressSetting.add(comboBox_nicList_1);
		
		JButton btn_addrSettingReset = new JButton("Reset");
		btn_addrSettingReset.setEnabled(false);
		btn_addrSettingReset.setBounds(182, 328, 96, 23);
		panel_addressSetting.add(btn_addrSettingReset);
		
		JButton btn_nicSelect1 = new JButton("Select");
		btn_nicSelect1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = comboBox_nicList_1.getSelectedItem().toString();
				selected_index_1 = comboBox_nicList_1.getSelectedIndex();
				
				try {
					textArea_srcMacAddr_port1.setText(Utils.convertAddrFormat(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_1).getHardwareAddress()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				textArea_srcIpAddr_port1.setText(Utils.convertAddrFormat(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_1).getAddresses().get(0).getAddr().getData()));
				
				// .GetAdapterObject(selected_index_1).getAddresses().get(0).getAddr().getData() -> IPv4 Address of Adapter (selected_index_1)
			
				
				//System.out.println(Utils.convertAddrFormat(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_1).getAddresses().get(0).getAddr().getData()));
				
				//((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_0).getAddresses().toString()
			}
		});
		btn_nicSelect1.setBounds(282, 92, 68, 23);
		panel_addressSetting.add(btn_nicSelect1);
		
		JTextArea textArea_dstIpAddr = new JTextArea();
		textArea_dstIpAddr.setColumns(10);
		textArea_dstIpAddr.setBorder(new LineBorder(Color.GRAY));
		textArea_dstIpAddr.setBounds(77, 297, 201, 21);
		panel_addressSetting.add(textArea_dstIpAddr);
		
		JLabel lblNewLabel_4 = new JLabel("Target IP");
		lblNewLabel_4.setBounds(22, 301, 52, 15);
		panel_addressSetting.add(lblNewLabel_4);
		
		JButton btn_nicSelect2 = new JButton("Select");
		btn_nicSelect2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = comboBox_nicList_2.getSelectedItem().toString();
				selected_index_2 = comboBox_nicList_2.getSelectedIndex();
				try {
					textArea_srcMacAddr_port2.setText(Utils.convertAddrFormat(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_2).getHardwareAddress()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				textArea_srcIpAddr_port2.setText(Utils.convertAddrFormat(((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index_2).getAddresses().get(0).getAddr().getData()));
				
			}
		});
		btn_nicSelect2.setBounds(645, 92, 68, 23);
		panel_addressSetting.add(btn_nicSelect2);
		comboBox_nicList_2.setEditable(true);
		comboBox_nicList_2.setBounds(375, 92, 266, 23);
		panel_addressSetting.add(comboBox_nicList_2);
		
		textArea_srcMacAddr_port2.setColumns(10);
		textArea_srcMacAddr_port2.setBorder(new LineBorder(Color.gray));
		textArea_srcMacAddr_port2.setBounds(440, 125, 201, 21);
		panel_addressSetting.add(textArea_srcMacAddr_port2);
		
		textArea_srcIpAddr_port2.setColumns(10);
		textArea_srcIpAddr_port2.setBorder(new LineBorder(Color.gray));
		textArea_srcIpAddr_port2.setBounds(440, 152, 201, 21);
		panel_addressSetting.add(textArea_srcIpAddr_port2);
		
		JLabel ARP_1_3_1_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1_1.setBounds(385, 156, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_1_1);
		
		JLabel ARP_1_3_1_1_1_1_2 = new JLabel("MAC");
		ARP_1_3_1_1_1_1_2.setBounds(385, 131, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_2);
		
		JComboBox comboBox_type = new JComboBox(new String[] {"HOST", "ROUTER"});
		comboBox_type.setBounds(77, 32, 201, 23);
		panel_addressSetting.add(comboBox_type);
		
		JLabel ARP_1_3_1_1_1_1_3 = new JLabel("Type");
		ARP_1_3_1_1_1_1_3.setBounds(12, 36, 64, 15);
		panel_addressSetting.add(ARP_1_3_1_1_1_1_3);
		
		JLabel lblNewLabel_1_2 = new JLabel("Interface_0");
		lblNewLabel_1_2.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_1_2.setBounds(12, 74, 96, 15);
		panel_addressSetting.add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Interface_1");
		lblNewLabel_1_1_1.setFont(new Font("굴림", Font.BOLD, 12));
		lblNewLabel_1_1_1.setBounds(375, 78, 96, 15);
		panel_addressSetting.add(lblNewLabel_1_1_1);
		
		JButton btn_typeSelect_1 = new JButton("Set");
		btn_typeSelect_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				NODE_TYPE = comboBox_type.getSelectedItem().toString();
				MAC_ADDR_PORT1 = textArea_srcMacAddr_port1.getText();
				IP_ADDR_PORT1 = textArea_srcIpAddr_port1.getText();
				
				MAC_ADDR_PORT2 = textArea_srcMacAddr_port2.getText();
				IP_ADDR_PORT2 = textArea_srcIpAddr_port2.getText();
				
				_router_main_frame.setVisible(true);
				((NILayer)m_LayerMgr.GetLayer("NI")).Port1_SetAdapterNumber(selected_index_1);
				((NILayer)m_LayerMgr.GetLayer("NI")).Port2_SetAdapterNumber(selected_index_2);

				textArea_srcMacAddr_port1.setEditable(false);
				textArea_srcIpAddr_port1.setEditable(false);
				textArea_srcMacAddr_port2.setEditable(false);
				textArea_srcIpAddr_port2.setEditable(false);
				btn_nicSelect1.setEnabled(false);
				btn_nicSelect2.setEnabled(false);
				comboBox_nicList_1.setEnabled(false);
				comboBox_nicList_2.setEnabled(false);
				comboBox_type.setEnabled(false);
			}
			
		});
		btn_typeSelect_1.setBounds(10, 259, 110, 34);
		getContentPane().add(btn_typeSelect_1);
		this.setVisible(true);
	}


	public static void main (String[] args) {
		// TODO Auto-generated method stub
		
		
		// Initialize layers
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new RouterGUI("RouterGUI"));
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *RouterGUI ( ) ) ) *IP ( ");
		((EthernetLayer)m_LayerMgr.GetLayer("Ethernet")).SetUpperLayer(m_LayerMgr.GetLayer("RouterGUI"));
		((ARPLayer)m_LayerMgr.GetLayer("ARP")).SetUpperLayer(m_LayerMgr.GetLayer("RouterGUI"));

		System.out.println(m_LayerMgr.GetLayer("Ethernet").GetUpperLayer(2).GetLayerName());
		ARPCacheTable.addElement("192.168.2.2","00:0c:29:c2:b1:50","Complete");
		ARPCacheTable.addElement("192.168.1.2","00:0c:29:c7:d1:3e","Complete");

		RoutingTable.addElement("192.168.1.0", "255.255.255.0", "192.168.1.2", "UG", "Interface_1", "-");
		RoutingTable.addElement("192.168.2.0", "255.255.255.0", "192.168.2.2", "UG", "Interface_2", "-");

		
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
	
	@SuppressWarnings("unchecked")
	private void initNicCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();
		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++) {
			//this.comboBox_nicList_1.addItem(m_pAdapterList.get(i).getDescription());
			//this.comboBox_nicList_2.addItem(m_pAdapterList.get(i).getDescription());
			this.comboBox_nicList_1.addItem(m_pAdapterList.get(i).getName());
			this.comboBox_nicList_2.addItem(m_pAdapterList.get(i).getName());
		}
	}
	
}
