import java.util.ArrayList;

public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private class _IP_ADDR {
		/*
		 * Data structure for representing IP Address, Will be used for ARP
		 * Packet structure. 4 bytes are required to represent the IP Address.
		 */
		private byte[] addr = new byte[4];

		// Initialize values ​​to 0x00
		public _IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
	}

	private class _MAC_ADDR {
		/*
		 * Data structure for representing Mac Address, Will be used for ARP
		 * Packet structure. 8 bytes are required to represent the MAC Address.
		 */
		private byte[] addr = new byte[6];

		// Initialize values ​​to 0x00
		public _MAC_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}

	private class _ARP_HEADER {
		/*
		 * Data structure for representing ARP Message Format
		 * 
		 * ARP Header Structure Specification (in order) 
		 * # [Hardware Type] - 2 Bytes 
		 * # [Protocol Type] - 2 Bytes 
		 * # [Length of hardware address] - 1 Byte 
		 * # [Length of Protocol address] - 1 Byte 
		 * # [Opcode] - 2 Bytes 
		 * # [Sender hardware address] - 6 Bytes 
		 * # [Sender protocol address] - 4 Bytes 
		 * # [Target hardware address] - 6 Bytes 
		 * # [Target protocol address] - 4 Bytes
		 */
		byte[] hardware_type = new byte[2];
		byte[] protocol_type = new byte[2];
		byte length_of_hardware_addr;
		byte length_of_protocol_addr;
		byte[] opcode = new byte[2];
		_MAC_ADDR sender_mac = new _MAC_ADDR();
		_MAC_ADDR target_mac = new _MAC_ADDR();
		_IP_ADDR sender_ip = new _IP_ADDR();
		_IP_ADDR target_ip = new _IP_ADDR();
		
		public _ARP_HEADER(){
			// _ARP_HEADER constructor
		}
		
		@SuppressWarnings("unused")
		public _ARP_HEADER(byte[] pHardwareType, byte[] pProtocolType,
				byte[] pOpcode, _MAC_ADDR pSenderMacAddress,
				_MAC_ADDR pTargetMacAddress, _IP_ADDR pSenderIpAddress,
				_IP_ADDR pTargetIpAddress) {

			// _ARP_HEADER constructor with Parameters
			this.hardware_type = pHardwareType;
			this.protocol_type = pProtocolType;
			this.length_of_hardware_addr = (byte) 0x06;
			this.length_of_protocol_addr = (byte) 0x04;
			this.opcode = pOpcode;
			this.sender_mac = pSenderMacAddress;
			this.target_mac = pTargetMacAddress;
			this.sender_ip = pSenderIpAddress;
			this.target_ip = pTargetIpAddress;
		}
	}

	private byte[] Encapsulate() {
		
		_ARP_HEADER test = new _ARP_HEADER();
		byte[] res = new byte[10];
		return res;

	}

	public ARPLayer(String pName) {
		// ARPLayer class constructor
		pLayerName = pName;

	}

	public boolean Send(byte[] input, int length) {
		// <!> additional implementation required later
		this.GetUnderLayer().Send(input, length);
		return false;
	}

	public boolean Receive(byte[] input) {
		// <!> additional implementation required later
		this.GetUpperLayer(0).Receive(input);
		return true;
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
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}

}
