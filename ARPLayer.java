import java.util.ArrayList;

public class ARPLayer implements BaseLayer {

	// Default hardware type = Ethernet(0x0001)
	private static final byte[] DEFAULT_HARDWARE_TYPE = new byte[]{0x00, 0x01};
	// Default protocol type = IPv4(0x0800)
	private static final byte[] DEFAULT_PROTOCOL_TYPE = new byte[]{0x08, 0x00};
	// Default length of hardware address = 0x06 (MAC Address)
	private static final byte DEFAULT_LENGTH_OF_HARDWARE_ADDRESS = (byte)0x06;
	// Default length of protocol address = 0x04 (IP Address)
	private static final byte DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS = (byte)0x04;

	
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	private _ARP_HEADER m_sHeader = new _ARP_HEADER();
	private _ARP_CACHE_TABLE arp_cache_table = new _ARP_CACHE_TABLE(); 
	private class _ARP_CACHE_TABLE{
		private final static int Capacity = 30;
		private int size = 0;
		private _IP_ADDR[]  ip_addr = new _IP_ADDR[Capacity];
		private _MAC_ADDR[] mac_addr = new _MAC_ADDR[Capacity];
		
		public _ARP_CACHE_TABLE() {
			// _ARP_CACHE_TABLE constructor
		}
		
		public void add(_IP_ADDR ip_addr, _MAC_ADDR mac_addr) {
			if (is_exist(ip_addr)) {
				// if ip_addr already exist in cache table, update mac_addr
				update_cache_table(ip_addr, mac_addr);
			}
			else {
				// if ip_addr not exist in cache table, add ip_addr, mac_addr
				this.ip_addr[size] = ip_addr;
				this.mac_addr[size] = mac_addr;
				size++;
			}
		}
		
		public boolean is_exist(_IP_ADDR ip_addr) {
			//<!> 수정해야함. 이렇게 구현하면 incomplete 상태에 있는 값도 mac 주소를 아는 것으로 판단하게 됨.
			
			//check mac_addr in cache table by ip_addr
			for(int i = 0;i < size;i++) {
				if(this.ip_addr[i] == ip_addr) {
					// if ip_addr is in cache table
					return true;
				}
			}
			// if ip_addr is not in cache table
			return false;
		}
		
		public _MAC_ADDR get_mac_address(_IP_ADDR ip_addr) {
			//find mac_addr in cache table by ip_addr
			for(int i = 0;i < size;i++) {
				if(this.ip_addr[i] == ip_addr) {
					// if ip_addr is in cache table, return its mac_addr
					return this.mac_addr[i];
				}
			}
			// if ip_addr is not in cache table, return null
			return null;
		}
		
		public boolean update_cache_table(_IP_ADDR ip_addr, _MAC_ADDR mac_addr) {
			// function for update mac_addr in cache table by ip_addr
			for(int i = 0;i < size;i++) {
				if(this.ip_addr[i] == ip_addr) {
					// if ip_addr is in cache table, update mac_addr and return true
					this.mac_addr[i] = mac_addr;
					return true;
				}
			}
			// if ip_addr is not in cache table, return false
			return false;
		}
		
		public boolean delete_cache_table(_IP_ADDR ip_addr) {
			// function for delete ip_addr in cache table
			for(int i = 0;i < size;i++) {
				if(this.ip_addr[i] == ip_addr) {
					// find ip_addr index in cache table
					for(int j = i; j<size-1 ; j++) {
						// pull elements one step since found index
							this.ip_addr[j] = this.ip_addr[j+1];
							return true;
					}
				}
			}
			// if ip_addr is not in cache table, return false
			return false;
		}
	}
	
	private class _IP_ADDR {
		/*
		 * Data structure for representing IP Address, Will be used for ARP
		 * Packet structure. 4 bytes are required to represent the IP Address.
		 */
		private byte[] addr = new byte[4];
		private int length_of_addr = 4;

		// Initialize values ​​to 0x00
		public _IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
		public int get_length_of_addr(){
			return this.length_of_addr;
		}
	}

	private class _MAC_ADDR {
		/*
		 * Data structure for representing Mac Address, Will be used for ARP
		 * Packet structure. 8 bytes are required to represent the MAC Address.
		 */
		
		private byte[] addr = new byte[6];
		private int length_of_addr = 6;
		// Initialize values ​​to 0x00
		public _MAC_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
		public int get_length_of_addr(){
			return this.length_of_addr;
		}
	}

	private class _ARP_HEADER {
		private int length_of_header = 28;
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
				byte pLengthOfMacAddr,
				byte pLengthOfProtocolAddr, 
				byte[] pOpcode, _MAC_ADDR pSenderMacAddress,
				_MAC_ADDR pTargetMacAddress, _IP_ADDR pSenderIpAddress,
				_IP_ADDR pTargetIpAddress) {

			// _ARP_HEADER constructor with Parameters
			this.hardware_type = pHardwareType;
			this.protocol_type = pProtocolType;
			this.length_of_hardware_addr = pLengthOfMacAddr;
			this.length_of_protocol_addr = pLengthOfProtocolAddr;
			this.opcode = pOpcode;
			this.sender_mac = pSenderMacAddress;
			this.target_mac = pTargetMacAddress;
			this.sender_ip = pSenderIpAddress;
			this.target_ip = pTargetIpAddress;
		}

		public void SetHardwareType(byte[] pHardwareType) {
			this.hardware_type = pHardwareType;
		}

		public void SetProtocolType(byte[] pProtocolType) {
			this.protocol_type = pProtocolType;
		}

		public void SetLengthOfHardwareAddress(byte _pLengthOfHardwareAddress) {
			this.length_of_hardware_addr = _pLengthOfHardwareAddress;
		}

		public void SetLengthOfProtocolAddress(byte pLengthOfProtocolAddress) {
			this.length_of_protocol_addr = pLengthOfProtocolAddress;
		}

		public void SetOpCode(byte[] pOpCode) {
			this.opcode = pOpCode;
		}

		public void SetSenderMacAddress(byte[] pSenderMacAddress) {
			this.sender_mac.addr = pSenderMacAddress;
		}

		public void SetTargetMacAddress(byte[] pTargetMacAddress) {
			this.target_mac.addr = pTargetMacAddress;
		}

		public void SetSenderIPAddress(byte[] pSenderIPAddress) {
			this.sender_ip.addr = pSenderIPAddress;
		}

		public void SetTargetIPAddress(byte[] pTargetIPAddress) {
			this.target_ip.addr = pTargetIPAddress;
		}

		public int get_length_of_header() {
			return this.length_of_header;
		}

	}


	public ARPLayer(String pName) {
		// ARPLayer class constructor
		pLayerName = pName;

	}
	
	public byte[] Encapsulate(_ARP_HEADER pHeader ){
		// <!> Need to check
		//  Encapsulate function to create byte array type ARP Packet
		int idx_ptr = 0;
		byte[] encapsulated = new byte[pHeader.get_length_of_header()];

		for (int i = 0; i < pHeader.hardware_type.length; i++) {
			encapsulated[idx_ptr++] = pHeader.hardware_type[i];
		}
		
		for (int i = 0; i < pHeader.protocol_type.length; i++) {
			encapsulated[idx_ptr++] = pHeader.protocol_type[i];
		}

		encapsulated[idx_ptr++] = pHeader.length_of_hardware_addr;
		encapsulated[idx_ptr++] = pHeader.length_of_protocol_addr;

		for (int i = 0; i < pHeader.opcode.length; i++) {
			encapsulated[idx_ptr++] = pHeader.opcode[i];
		}

		for (int i = 0; i < pHeader.sender_mac.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.sender_mac.addr[i];
		}
		for (int i = 0; i < pHeader.target_mac.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.target_mac.addr[i];
		}
		for (int i = 0; i < pHeader.sender_ip.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.sender_ip.addr[i];
		}
		for (int i = 0; i < pHeader.target_ip.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.target_ip.addr[i];
		}
	
		return encapsulated;
		
	}
	
	public byte[] Decapsulate(byte[] pARPPacket){
		// <!> Need to check
		
		int offset=28;
		byte[] decapsulated = new byte[pARPPacket.length - 28];
		for (int i=0; i<decapsulated.length; i++){
			decapsulated[i] = pARPPacket[offset+i];
		}
		return decapsulated;
	}
	
	public boolean Send() {
		// <!> additional implementation required later
		
		if (!this.arp_cache_table.is_exist(this.m_sHeader.target_ip)) {
			/*
			 * If there is no Mac address for the destination IP in the ARP cache table,
			 * then Send ARP Request
			 */
			
			// OpCode of ARP Request = 0x0001
			
			
			byte[] opCode = new byte[] {0x00,0x01};
			this.m_sHeader.SetHardwareType(DEFAULT_HARDWARE_TYPE);
			this.m_sHeader.SetProtocolType(DEFAULT_PROTOCOL_TYPE);
			this.m_sHeader.SetTargetMacAddress(new byte[] {0x00,0x00,0x00,0x00,0x00,0x00});
			this.m_sHeader.SetOpCode(opCode);
			
			byte[] encapsulated =this.Encapsulate(this.m_sHeader);
			System.out.println("ARPLayer Send: ");
			Utils.showPacket(encapsulated);
			this.GetUnderLayer().Send(encapsulated, encapsulated.length);
			
		}
		

		
		
		return true;
	}

	
	public void setARPHeaderSrcIp(byte[] pSrcIP) {
		this.m_sHeader.sender_ip.addr=pSrcIP;
	}
	public void setARPHeaderDstIP(byte[] pTargetIP) {
		this.m_sHeader.target_ip.addr=pTargetIP;
	}
	
	public void setARPHeaderSrcMac(byte[] pSrcMac) {
		this.m_sHeader.sender_mac.addr=pSrcMac;
	}
	public void setARPHeaderDstMac(byte[] pTargetMac) {
		this.m_sHeader.sender_mac.addr=pTargetMac;
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
