package chat_file;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;

public class ARPGUI {

	private JFrame frmArpgui;
	private JTextArea textField;
	private JTextArea textField_1;
	private JTextArea textField_2;
	private JTextArea textField_3;
	private JTextArea textField_4;
	private JTextArea textField_5;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ARPGUI window = new ARPGUI();
					window.frmArpgui.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ARPGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmArpgui = new JFrame();
		frmArpgui.setTitle("ARPGUI");
		frmArpgui.setBounds(100, 100, 1117, 399);
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
		btnNewButton.setBounds(42, 213, 91, 23);
		ARP.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("All delete");
		btnNewButton_1.setBounds(170, 213, 91, 23);
		ARP.add(btnNewButton_1);
		
		textField = new JTextArea();
		textField.setBounds(52, 254, 154, 21);
		ARP.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("Send");
		btnNewButton_2.setBounds(208, 253, 91, 23);
		ARP.add(btnNewButton_2);
		
		JLabel ARP_1_3 = new JLabel("IP_addr");
		ARP_1_3.setBounds(5, 257, 46, 15);
		ARP.add(ARP_1_3);
		
		textField_1 = new JTextArea();
		textField_1.setBounds(12, 10, 287, 193);
		ARP.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel proxy_ARP = new JPanel();
		proxy_ARP.setBounds(383, 29, 311, 301);
		frmArpgui.getContentPane().add(proxy_ARP);
		proxy_ARP.setLayout(null);
		
		JButton btnNewButton_3 = new JButton("Add");
		btnNewButton_3.setBounds(43, 268, 91, 23);
		proxy_ARP.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("Delete");
		btnNewButton_4.setBounds(176, 268, 91, 23);
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
		ARP_1_3_1_1.setBounds(32, 169, 61, 15);
		proxy_ARP.add(ARP_1_3_1_1);
		
		JLabel ARP_1_3_1 = new JLabel("IP_addr");
		ARP_1_3_1.setBounds(32, 200, 61, 15);
		proxy_ARP.add(ARP_1_3_1);
		
		JLabel ARP_1_3_1_2 = new JLabel("MAC_addr");
		ARP_1_3_1_2.setBounds(32, 231, 61, 15);
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
		
		JTextArea textField_6_1 = new JTextArea();
		textField_6_1.setColumns(10);
		textField_6_1.setBounds(87, 58, 201, 21);
		GARP_1.add(textField_6_1);
		
		JButton btnNewButton_4_1_1 = new JButton("Setting");
		btnNewButton_4_1_1.setBounds(197, 124, 91, 23);
		GARP_1.add(btnNewButton_4_1_1);
		
		JTextArea textField_6_1_1 = new JTextArea();
		textField_6_1_1.setColumns(10);
		textField_6_1_1.setBounds(87, 93, 201, 21);
		GARP_1.add(textField_6_1_1);
		
		JLabel ARP_1_3_1_1_1_1_1 = new JLabel("IP");
		ARP_1_3_1_1_1_1_1.setBounds(23, 98, 64, 15);
		GARP_1.add(ARP_1_3_1_1_1_1_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(87, 24, 201, 23);
		GARP_1.add(comboBox);
		
		JLabel ARP_1_2_1 = new JLabel("Address");
		ARP_1_2_1.setBounds(763, 155, 109, 15);
		frmArpgui.getContentPane().add(ARP_1_2_1);
	}
}
