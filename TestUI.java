import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class TestUI extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	String path;

	private static LayerManager m_LayerMgr = new LayerManager();

	private JTextField textField_srcMac;
	private JTextField textField_DstMac;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Initialize layers
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new ChatAppLayer("Chat"));
		m_LayerMgr.AddLayer(new TestUI("TestUI"));

		// Connect all currently existing layers
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP ( *Chat ( *TestUI ) )");
	}

	public TestUI(String pName) {
		pLayerName = pName;
		getContentPane().setLayout(null);
		setBounds(250, 250, 400, 250);

		JPanel panel = new JPanel();
		panel.setBounds(12, 10, 354, 182);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel label_NIC = new JLabel("NIC List");
		label_NIC.setBounds(0, 36, 57, 15);
		panel.add(label_NIC);
		
		JLabel lblSourceMac = new JLabel("Src MAC");
		lblSourceMac.setBounds(0, 67, 86, 15);
		panel.add(lblSourceMac);
		
		JLabel lblDestinationMac = new JLabel("Dst MAC");
		lblDestinationMac.setBounds(0, 97, 86, 15);
		panel.add(lblDestinationMac);
		
		textField_DstMac = new JTextField();
		textField_DstMac.setBounds(57, 93, 222, 21);
		panel.add(textField_DstMac);
		textField_DstMac.setColumns(10);
		
		textField_srcMac = new JTextField();
		textField_srcMac.setBounds(57, 62, 222, 21);
		panel.add(textField_srcMac);
		textField_srcMac.setColumns(10);
		
		JComboBox comboBox_NIC = new JComboBox();
		comboBox_NIC.setBounds(57, 30, 145, 23);
		panel.add(comboBox_NIC);
		
		JButton NIC_select_button = new JButton("Select");
		NIC_select_button.setBounds(203, 29, 76, 25);
		panel.add(NIC_select_button);
		
		JButton btn_set = new JButton("Set");
		btn_set.setBounds(284, 29, 57, 85);
		panel.add(btn_set);
		
		JButton btn_send = new JButton("Send");
		btn_send.setBounds(0, 133, 341, 23);
		panel.add(btn_send);
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btn_set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		NIC_select_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		setVisible(true);
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

}
