package StaticRouter;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RoutingTableAddForm extends JFrame {

	private JPanel contentPane;
	private JTextField tf_destination;
	private JLabel lblNetmask;
	private JTextField tf_netmask;
	private JLabel lblGateway;
	private JTextField tf_gateway;
	private JLabel lblNewLabel_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RoutingTableAddForm frame = new RoutingTableAddForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RoutingTableAddForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 409, 365);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(12, 10, 383, 309);
		contentPane.add(panel);
		panel.setLayout(null);

		tf_destination = new JTextField();
		tf_destination.setBounds(141, 36, 178, 31);
		panel.add(tf_destination);
		tf_destination.setColumns(10);

		JLabel lblNewLabel = new JLabel("Destination");
		lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 17));
		lblNewLabel.setBounds(44, 43, 97, 15);
		panel.add(lblNewLabel);

		lblNetmask = new JLabel("Netmask");
		lblNetmask.setFont(new Font("굴림", Font.PLAIN, 17));
		lblNetmask.setBounds(44, 84, 97, 15);
		panel.add(lblNetmask);

		tf_netmask = new JTextField();
		tf_netmask.setColumns(10);
		tf_netmask.setBounds(141, 77, 178, 31);
		panel.add(tf_netmask);

		lblGateway = new JLabel("Gateway");
		lblGateway.setFont(new Font("굴림", Font.PLAIN, 17));
		lblGateway.setBounds(44, 128, 97, 15);
		panel.add(lblGateway);

		tf_gateway = new JTextField();
		tf_gateway.setColumns(10);
		tf_gateway.setBounds(141, 121, 178, 31);
		panel.add(tf_gateway);

		lblNewLabel_3 = new JLabel("Flag");
		lblNewLabel_3.setFont(new Font("굴림", Font.PLAIN, 17));
		lblNewLabel_3.setBounds(44, 171, 97, 15);
		panel.add(lblNewLabel_3);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Up");
		chckbxNewCheckBox.setBounds(141, 168, 45, 23);
		panel.add(chckbxNewCheckBox);

		JCheckBox chckbxGateway = new JCheckBox("Gateway");
		chckbxGateway.setBounds(186, 168, 84, 23);
		panel.add(chckbxGateway);

		JCheckBox chckbxHost = new JCheckBox("Host");
		chckbxHost.setBounds(274, 168, 55, 23);
		panel.add(chckbxHost);

		JComboBox comboBox = new JComboBox(new String[] {"Interface_0", "Interface_1"});
		comboBox.setBounds(141, 214, 178, 23);
		panel.add(comboBox);

		JLabel lblNewLabel_3_1 = new JLabel("Interface");
		lblNewLabel_3_1.setFont(new Font("굴림", Font.PLAIN, 17));
		lblNewLabel_3_1.setBounds(44, 218, 97, 15);
		panel.add(lblNewLabel_3_1);

		JButton btnNewButton = new JButton("Add");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String destination = tf_destination.getText();
				String netMask = tf_netmask.getText();
				String gateway = tf_gateway.getText();
				RoutingTableGUI.addElement(destination, netMask, gateway, "-", "-", "-");
				dispose();

			}
		});
		btnNewButton.setBounds(67, 275, 95, 23);
		panel.add(btnNewButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(206, 275, 95, 23);
		panel.add(btnCancel);
	}
}
