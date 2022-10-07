import java.util.ArrayList;

public class ARPLayer implements BaseLayer {

	private static final byte[] UNKNOWN_DESTINATION_MAC_ADDR = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };
	private static final byte[] OPCODE_ARP_REQUEST = new byte[] { 0x00, 0x01 };
	// Default hardware type = Ethernet(0x0001)
	private static final byte[] DEFAULT_HARDWARE_TYPE = new byte[] { 0x00, 0x01 };
	// Default protocol type = IPv4(0x0800)
	private static final byte[] DEFAULT_PROTOCOL_TYPE = new byte[] { 0x08, 0x00 };
	// Default length of hardware address = 0x06 (MAC Address)
	private static final byte DEFAULT_LENGTH_OF_HARDWARE_ADDRESS = (byte) 0x06;
	// Default length of protocol address = 0x04 (IP Address)
	private static final byte DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS = (byte) 0x04;

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	private _ARP_HEADER m_sHeader = new _ARP_HEADER();
	private _ARP_CACHE_TABLE arpCacheTable = new _ARP_CACHE_TABLE();

	public ARPLayer(String pName) {
		// ARPLayer class constructor
		pLayerName = pName;

	}

	private class _ARP_HEADER {
		private int length_of_header = 28;
		/*
		 * Data structure for representing ARP Message Format
		 * 
		 * ARP Header Structure Specification (in order) # [Hardware Type] - 2 Bytes #
		 * [Protocol Type] - 2 Bytes # [Length of hardware address] - 1 Byte # [Length
		 * of Protocol address] - 1 Byte # [Opcode] - 2 Bytes # [Sender hardware
		 * address] - 6 Bytes # [Sender protocol address] - 4 Bytes # [Target hardware
		 * address] - 6 Bytes # [Target protocol address] - 4 Bytes
		 */

		byte[] hardwareType = new byte[2];
		byte[] protocolType = new byte[2];
		byte lengthOfHardwareAddr;
		byte lengthOfProtocolAddr;
		byte[] opCode = new byte[2];
		_MAC_ADDR senderMac = new _MAC_ADDR();
		_MAC_ADDR targetMac = new _MAC_ADDR();
		_IP_ADDR senderIp = new _IP_ADDR();
		_IP_ADDR targetIp = new _IP_ADDR();

		public _ARP_HEADER() {
			// _ARP_HEADER constructor
		}

		@SuppressWarnings("unused")
		public _ARP_HEADER(byte[] pHardwareType, byte[] pProtocolType, byte pLengthOfMacAddr,
				byte pLengthOfProtocolAddr, byte[] pOpcode, _MAC_ADDR pSenderMacAddress, _MAC_ADDR pTargetMacAddress,
				_IP_ADDR pSenderIpAddress, _IP_ADDR pTargetIpAddress) {

			// _ARP_HEADER constructor with Parameters
			this.hardwareType = pHardwareType;
			this.protocolType = pProtocolType;
			this.lengthOfHardwareAddr = pLengthOfMacAddr;
			this.lengthOfProtocolAddr = pLengthOfProtocolAddr;
			this.opCode = pOpcode;
			this.senderMac = pSenderMacAddress;
			this.targetMac = pTargetMacAddress;
			this.senderIp = pSenderIpAddress;
			this.targetIp = pTargetIpAddress;
		}

		public void SetHardwareType(byte[] pHardwareType) {
			this.hardwareType = pHardwareType;
		}

		public void SetProtocolType(byte[] pProtocolType) {
			this.protocolType = pProtocolType;
		}

		public void SetLengthOfHardwareAddress(byte _pLengthOfHardwareAddress) {
			this.lengthOfHardwareAddr = _pLengthOfHardwareAddress;
		}

		public void SetLengthOfProtocolAddress(byte pLengthOfProtocolAddress) {
			this.lengthOfProtocolAddr = pLengthOfProtocolAddress;
		}

		public void SetOpCode(byte[] pOpCode) {
			this.opCode = pOpCode;
		}

		public void SetSenderMacAddress(byte[] pSenderMacAddress) {
			this.senderMac.addr = pSenderMacAddress;
		}

		public void SetTargetMacAddress(byte[] pTargetMacAddress) {
			this.targetMac.addr = pTargetMacAddress;
		}

		public void SetSenderIPAddress(byte[] pSenderIPAddress) {
			this.senderIp.addr = pSenderIPAddress;
		}

		public void SetTargetIPAddress(byte[] pTargetIPAddress) {
			this.targetIp.addr = pTargetIPAddress;
		}

		public int get_length_of_header() {
			return this.length_of_header;
		}

	}

	private class _ARP_CACHE_TABLE {
		private final static int Capacity = 30;
		private int size = 0;
		private String[] ipAddr = new String[Capacity];
		private String[] macAddr = new String[Capacity];
		private String[] state = new String[Capacity];

		public _ARP_CACHE_TABLE() {
			// _ARP_CACHE_TABLE constructor
		}

		public boolean addArpCacheTableElement(String pIpAddr, String pMacAddr, String pState) {

			if (this.size < this.Capacity && !this.isExist(pIpAddr)) {
				ipAddr[size] = pIpAddr;
				macAddr[size] = pMacAddr;
				state[size] = pState;
				size++;
				return true;
				

			}
			return false;
		}
		public boolean addArpCacheTableElement(String pIpAddr) {
			if (this.size < this.Capacity && !this.isExist(pIpAddr)) {
				ipAddr[size] = pIpAddr;
				macAddr[size] = "??:??:??:??:??:??";
				state[size] = "incomplete";
				size++;
				return true;

			}
			return false;
		}

		public boolean addArpCacheTableElement(_IP_ADDR pIpAddr) {
			// Implement layer

			return false;
		}

		public boolean isExist(String pIpAddr) {
			// Implement layer
			return false;
		}

		public void showArpTable() {
			for(int i =0; i<this.size; i++) {
				System.out.print(ipAddr[i] + " | ");
				System.out.print(macAddr[i] +" | ");
				System.out.println(state[i]);

			}
			/*
			 * for (int i = 0; i < this.size - 1; i++) { for (int j = 0; j < 4; j++) {
			 * System.out.print(this.ipAddr[i].getAddr()[j] + "."); } }
			 */
		}

	}

	private class _IP_ADDR {
		/*
		 * Data structure for representing IP Address, Will be used for ARP Packet
		 * structure. 4 bytes are required to represent the IP Address.
		 */
		private byte[] addr = new byte[4];
		private int lengthOfAddr = 4;

		// Initialize values ​​to 0x00
		public _IP_ADDR() {
			for (int i = 0; i < this.lengthOfAddr; i++) {
				this.addr[i] = (byte) 0x00;
			}
		}

		public _IP_ADDR(byte[] pAddr) {
			for (int i = 0; i < this.lengthOfAddr; i++) {
				this.addr[i] = pAddr[i];
			}
		}

		public byte[] getAddrByte() {
			return this.addr;
		}

		public String getAddrStr() {
			return null;
		}

		public int getLengthOfAddr() {
			return this.lengthOfAddr;
		}
	}

	private class _MAC_ADDR {
		/*
		 * Data structure for representing Mac Address, Will be used for ARP Packet
		 * structure. 8 bytes are required to represent the MAC Address.
		 */

		private byte[] addr = new byte[6];
		private int lengthOfAddr = 6;

		// Initialize values ​​to 0x00
		public _MAC_ADDR() {
			for (int i = 0; i < this.lengthOfAddr; i++) {
				this.addr[i] = (byte) 0x00;
			}
		}

		public _MAC_ADDR(byte[] pAddr) {
			for (int i = 0; i < this.lengthOfAddr; i++) {
				this.addr[i] = pAddr[i];
			}
		}

		public byte[] getAddrByte() {
			return this.addr;
		}

		public String getAddrStr() {
			return null;
		}

		public int getLengthOfAddr() {
			return this.lengthOfAddr;
		}
	}

	public byte[] Encapsulate(_ARP_HEADER pHeader) {
		// <!> Need to check
		// Encapsulate function to create byte array type ARP Packet
		int idx_ptr = 0;
		byte[] encapsulated = new byte[pHeader.get_length_of_header()];

		for (int i = 0; i < pHeader.hardwareType.length; i++) {
			encapsulated[idx_ptr++] = pHeader.hardwareType[i];
		}

		for (int i = 0; i < pHeader.protocolType.length; i++) {
			encapsulated[idx_ptr++] = pHeader.protocolType[i];
		}

		encapsulated[idx_ptr++] = pHeader.lengthOfHardwareAddr;
		encapsulated[idx_ptr++] = pHeader.lengthOfProtocolAddr;

		for (int i = 0; i < pHeader.opCode.length; i++) {
			encapsulated[idx_ptr++] = pHeader.opCode[i];
		}

		for (int i = 0; i < pHeader.senderMac.getLengthOfAddr(); i++) {
			encapsulated[idx_ptr++] = pHeader.senderMac.addr[i];
		}
		for (int i = 0; i < pHeader.senderIp.getLengthOfAddr(); i++) {
			encapsulated[idx_ptr++] = pHeader.senderIp.addr[i];
		}
		for (int i = 0; i < pHeader.targetMac.getLengthOfAddr(); i++) {
			encapsulated[idx_ptr++] = pHeader.targetMac.addr[i];
		}
		for (int i = 0; i < pHeader.targetIp.getLengthOfAddr(); i++) {
			encapsulated[idx_ptr++] = pHeader.targetIp.addr[i];
		}

		return encapsulated;

	}

	public byte[] Decapsulate(byte[] pARPPacket) {
		// <!> Need to check

		int offset = 28;
		byte[] decapsulated = new byte[pARPPacket.length - 28];
		for (int i = 0; i < decapsulated.length; i++) {
			decapsulated[i] = pARPPacket[offset + i];
		}
		return decapsulated;
	}

	public _ARP_HEADER MakeARPRequestHeader() {

		_ARP_HEADER header = new _ARP_HEADER(DEFAULT_HARDWARE_TYPE, DEFAULT_PROTOCOL_TYPE,
				DEFAULT_LENGTH_OF_HARDWARE_ADDRESS, DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS, OPCODE_ARP_REQUEST,
				this.m_sHeader.senderMac, new _MAC_ADDR(UNKNOWN_DESTINATION_MAC_ADDR), this.m_sHeader.senderIp,
				this.m_sHeader.targetIp);
		return header;

	}

	public boolean Send() {
		// <!> additional implementation required later

		// if (!this.arpCacheTable.isExist(this.m_sHeader.target_ip.getByteAddr())) {
		/*
		 * If there is no Mac address for the destination IP in the ARP cache table,
		 * then Send ARP Request
		 */

		// OpCode of ARP Request = 0x0001
		_ARP_HEADER ARPRequestHeader = this.MakeARPRequestHeader();
		// System.out.println(this.arpCacheTable.addArpCacheTableElement(this.m_sHeader.target_ip));
		// this.arpCacheTable.showArpTable();
		byte[] encapsulated = this.Encapsulate(ARPRequestHeader);
		this.arpCacheTable.addArpCacheTableElement(ARPRequestHeader.targetIp.addr);
		this.addARPCacheTableElement(ARPRequestHeader.targetIp.addr);
		this.arpCacheTable.showArpTable();
		System.out.println("ARPLayer Send: ");
		Utils.showPacket(encapsulated);
		this.GetUnderLayer().Send(encapsulated, encapsulated.length);

		// }

		return true;
	}
	
	public void addARPCacheTableElement(String pIpAddr, String pMacAddr, String pState) { 
		this.arpCacheTable.addArpCacheTableElement(pIpAddr,pMacAddr,pState);
		String[] data = new String[]{pIpAddr,pMacAddr,pState};
		//((TestUI)(this.GetUpperLayer().GetUpperLayer())).addData(data);
	}
	public void addARPCacheTableElement(String pIpAddr) { 
		this.arpCacheTable.addArpCacheTableElement(pIpAddr);
		String[] data = new String[]{pIpAddr,"??:??:??:??:??:??","incomplete"};
		//((TestUI)(this.GetUpperLayer(0).GetUpperLayer(0))).addData(data);
	}

	public void setARPHeaderSrcIp(byte[] pSrcIP) {
		this.m_sHeader.senderIp.addr = pSrcIP;
	}

	public void setARPHeaderDstIp(byte[] pTargetIP) {
		this.m_sHeader.targetIp.addr = pTargetIP;
	}

	public void setARPHeaderSrcMac(byte[] pSrcMac) {
		this.m_sHeader.senderMac.addr = pSrcMac;
	}

	public void setARPHeaderDstMac(byte[] pTargetMac) {
		this.m_sHeader.targetMac.addr = pTargetMac;
	}

	public boolean Receive(byte[] input) {
		// <!> additional implementation required later
		byte[] message = input;

		//Object[] value = new Object[4];
		byte[] dstIP = new byte[4];
		byte[] dstMac = new byte[6];
		byte[] targetIP = new byte[4];

		System.arraycopy(message, 14, dstIP, 0, 4);
		System.arraycopy(message, 8, dstMac, 0, 6);
		System.arraycopy(message, 24, targetIP, 0, 4);

		String ipAddressToString = (dstIP[0] & 0xFF) + "." + (dstIP[1] & 0xFF) + "." + (dstIP[2] & 0xFF) + "."
            + (dstIP[3] & 0xFF);
		String targetIpAddressToString = (targetIP[0] & 0xFF) + "." + (targetIP[1] & 0xFF) + "." + (targetIP[2] & 0xFF)
            + "." + (targetIP[3] & 0xFF);
		String srcIpAddressToString = (m_sHeader.senderIp.addr[0] & 0xFF) + "."
            + (m_sHeader.senderIp.addr[1] & 0xFF) + "."
            + (m_sHeader.senderIp.addr[2] & 0xFF) + "."
            + (m_sHeader.senderIp.addr[3] & 0xFF);

		if (message[6] == (byte) 0x00 && message[7] == (byte) 0x01) { // ARP-request Receive ("Complete")
			if (ipAddressToString.equals(targetIpAddressToString) && ipAddressToString.equals(srcIpAddressToString)) {
				System.out.println("receive test.");
				return true;
			}
		}
       return true;
	//this.GetUpperLayer(0).Receive(input);
	//return true;
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
