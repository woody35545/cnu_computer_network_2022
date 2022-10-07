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

public class ARPGUI extends JFrame implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	private static LayerManager m_LayerMgr = new LayerManager();

	private JFrame frmArpgui;
	private JTextArea textField_targetIp;
	private JTextArea textField_2;
	private JTextArea textField_3;
	private JTextArea textField_4;
	private JTextArea textField_5;

	private int selected_index;
	JComboBox comboBox = new JComboBox();
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Initialize layers
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new ARPGUI("ARPGUI"));

		// Connect all currently existing layers
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *IP ( *ARPGUI ) *ARPGUI )");
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
		frmArpgui.setBounds(100, 100, 1117, 386);
		frmArpgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmArpgui.getContentPane().setLayout(null);
		JLabel ARP_1 = new JLabel("ARP");
		ARP_1.setBounds(27, 10, 50, 15);
		frmArpgui.getContentPane().add(ARP_1);

		JPanel ARP = new JPanel();
		ARP.setBounds(27, 29, 311, 301);
		frmArpgui.getContentPane().add(ARP);
		ARP.setLayout(null);

		JButton btnNewButton = new JButton("Item delete");
		btnNewButton.setBounds(12, 213, 135, 25);
		ARP.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("All delete");
		btnNewButton_1.setBounds(164, 213, 135, 25);
		ARP.add(btnNewButton_1);

		textField_targetIp = new JTextArea();
		textField_targetIp.setBounds(12, 270, 175, 25);
		ARP.add(textField_targetIp);
		textField_targetIp.setColumns(10);

		JButton btnNewButton_2 = new JButton("Send");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String targetIp = textField_targetIp.getText();
				((ARPLayer)m_LayerMgr.GetLayer("ARP")).setARPHeaderDstIp(Utils.convertStrFormatIpToByteFormat(targetIp));
				((ARPLayer) m_LayerMgr.GetLayer("ARP")).Send();
			}
		});
		btnNewButton_2.setBounds(199, 270, 100, 25);
		ARP.add(btnNewButton_2);

		JLabel ARP_1_3 = new JLabel("Target IP");
		ARP_1_3.setBounds(12, 246, 133, 15);
		ARP.add(ARP_1_3);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 287, 195);
		ARP.add(scrollPane);
		
		String header[] = { "IP Address", "MAC Address", "State" };
		DefaultTableModel model = new DefaultTableModel(header, 30);
		table = new JTable(model);
		scrollPane.setViewportView(table);

		JPanel proxy_ARP = new JPanel();
		proxy_ARP.setBounds(383, 29, 311, 301);
		frmArpgui.getContentPane().add(proxy_ARP);
		proxy_ARP.setLayout(null);

		JButton btnNewButton_3 = new JButton("Add");
		btnNewButton_3.setBounds(12, 268, 135, 23);
		proxy_ARP.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton("Delete");
		btnNewButton_4.setBounds(164, 268, 135, 23);
		proxy_ARP.add(btnNewButton_4);

		textField_2 = new JTextArea();
		textField_2.setBounds(105, 164, 162, 21);
		proxy_ARP.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JTextArea();
		textField_3.setColumns(10);
		textField_3.setBounds(105, 195, 162, 21);
		proxy_ARP.add(textField_3);

		textField_4 = new JTextArea();
		textField_4.setColumns(10);
		textField_4.setBounds(105, 226, 162, 21);
		proxy_ARP.add(textField_4);

		textField_5 = new JTextArea();
		textField_5.setBounds(12, 10, 287, 144);
		proxy_ARP.add(textField_5);
		textField_5.setColumns(10);

		JLabel ARP_1_3_1_1 = new JLabel("Device");
		ARP_1_3_1_1.setBounds(12, 169, 61, 15);
		proxy_ARP.add(ARP_1_3_1_1);

		JLabel ARP_1_3_1 = new JLabel("IP Address");
		ARP_1_3_1.setBounds(12, 200, 81, 15);
		proxy_ARP.add(ARP_1_3_1);

		JLabel ARP_1_3_1_2 = new JLabel("Mac Address");
		ARP_1_3_1_2.setBounds(12, 231, 81, 15);
		proxy_ARP.add(ARP_1_3_1_2);

		JPanel GARP = new JPanel();
		GARP.setBounds(763, 29, 311, 116);
		frmArpgui.getContentPane().add(GARP);
		GARP.setLayout(null);

		JLabel ARP_1_3_1_1_1 = new JLabel("HW");
		ARP_1_3_1_1_1.setBounds(23, 21, 61, 15);
		GARP.add(ARP_1_3_1_1_1);

		JTextArea textField_6 = new JTextArea();
		textField_6.setColumns(10);
		textField_6.setBounds(33, 47, 255, 21);
		GARP.add(textField_6);

		JButton btnNewButton_4_1 = new JButton("Send");
		btnNewButton_4_1.setBounds(111, 83, 91, 23);
		GARP.add(btnNewButton_4_1);

		JLabel ARP_1_1 = new JLabel("Proxy ARP");
		ARP_1_1.setBounds(383, 10, 83, 15);
		frmArpgui.getContentPane().add(ARP_1_1);

		JLabel ARP_1_2 = new JLabel("Gratitous ARP");
		ARP_1_2.setBounds(763, 10, 109, 15);
		frmArpgui.getContentPane().add(ARP_1_2);

		JPanel GARP_1 = new JPanel();
		GARP_1.setLayout(null);
		GARP_1.setBounds(763, 173, 311, 157);
		frmArpgui.getContentPane().add(GARP_1);

		JLabel ARP_1_3_1_1_1_1 = new JLabel("MAC");
		ARP_1_3_1_1_1_1.setBounds(23, 63, 64, 15);
		GARP_1.add(ARP_1_3_1_1_1_1);

		JTextArea textArea_srcMacAddr = new JTextArea();
		textArea_srcMacAddr.setColumns(10);
		textArea_srcMacAddr.setBounds(87, 58, 201, 21);
		GARP_1.add(textArea_srcMacAddr);

		JTextArea textArea_srcIpAddr = new JTextArea();
		textArea_srcIpAddr.setColumns(10);
		textArea_srcIpAddr.setBounds(87, 93, 201, 21);
		GARP_1.add(textArea_srcIpAddr);

		JButton btn_set = new JButton("Set");
		btn_set.addActionListener(new ActionListener() {
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

			}
		});
		btn_set.setBounds(87, 124, 90, 23);
		GARP_1.add(btn_set);

		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(23, 98, 64, 15);
		GARP_1.add(ARP_1_3_1_1_1_1_1);

		comboBox.setBounds(12, 25, 201, 23);
		GARP_1.add(comboBox);

		JButton btnNewButton_5 = new JButton("Reset ");
		btnNewButton_5.setBounds(198, 124, 90, 23);
		GARP_1.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton("Select");
		btnNewButton_6.addActionListener(new ActionListener() {
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
			}
		});
		btnNewButton_6.setBounds(220, 25, 80, 23);
		GARP_1.add(btnNewButton_6);
		SetCombobox();
		JLabel ARP_1_2_1 = new JLabel("Setting");
		ARP_1_2_1.setBounds(763, 155, 109, 15);
		frmArpgui.getContentPane().add(ARP_1_2_1);
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
	
	
	public void addData(String[] pDataArr) {
		DefaultTableModel model=(DefaultTableModel)table.getModel();
		model.addRow(pDataArr);
	}
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
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
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
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
		return null;
	}

	@Override
	public BaseLayer GetUpperLayer() {
		// TODO Auto-generated method stub
		return null;
	}
}
