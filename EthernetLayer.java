import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class EthernetLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private class _ETHERNET_ADDR {
		private byte[] addr = new byte[6];
		private int length_of_addr = 6;

		public _ETHERNET_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}

		public int get_length_of_addr() {
			return this.length_of_addr;
		}
	}

	private class _ETHERNET_HEADER {
		_ETHERNET_ADDR enet_dstaddr;
		_ETHERNET_ADDR enet_srcaddr;
		byte[] enet_type;
		byte[] enet_data;

		public _ETHERNET_HEADER() {
			this.enet_dstaddr = new _ETHERNET_ADDR();
			this.enet_srcaddr = new _ETHERNET_ADDR();
			this.enet_type = new byte[2];
			this.enet_data = null;
		}

		public _ETHERNET_ADDR get_destination_address() {
			return this.enet_dstaddr;
		}

		public void set_destination_address(byte[] pDstaddr) {
			this.enet_dstaddr.addr = pDstaddr;
		}

		public _ETHERNET_ADDR get_source_address() {
			return this.enet_srcaddr;
		}

		public void set_source_address(byte[] pSrcaddr) {
			this.enet_srcaddr.addr = pSrcaddr;
		}

		public byte[] get_enet_type() {
			return this.enet_type;
		}

		public void set_enet_type(byte[] bytes) {
			this.enet_type = bytes;
		}

	}

	_ETHERNET_HEADER m_sHeader = new _ETHERNET_HEADER();

	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;

	}

	public byte[] Encapsulate(_ETHERNET_HEADER pHeader, byte[] pPayload) {
		// <!> Need to check
		// header length - header.data length = 14
		if (pPayload == null) {
			// Padding
			byte[] padding = new byte[46];
			for(int i =0; i<46; i++) {
				padding[i]=(byte)0x00;
			}
			pPayload = padding;
		}
		
		int idx_ptr = 0;
		byte[] encapsulated = new byte[14 + pPayload.length];
		for (int i = 0; i < pHeader.enet_dstaddr.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.enet_dstaddr.addr[i];
		}
		for (int i = 0; i < pHeader.enet_srcaddr.get_length_of_addr(); i++) {
			encapsulated[idx_ptr++] = pHeader.enet_srcaddr.addr[i];
		}
		for (int i = 0; i < pHeader.enet_type.length; i++) {
			encapsulated[idx_ptr++] = pHeader.enet_type[i];
		}
		for (int i = 0; i < pPayload.length; i++) {
			encapsulated[idx_ptr++] = pPayload[i];
		}

		return encapsulated;

	}

	public byte[] Decapsulate(byte[] pEthFrame) {
		// <!> Need to check
		// offset(header length - header.data length)
		int offset = 14;
		byte[] decapsulated = new byte[pEthFrame.length - offset];
		for (int i = 0; i < decapsulated.length; i++) {
			decapsulated[i] = pEthFrame[offset + i];
		}
		return decapsulated;
	}

	public boolean Send(byte[] input, int length) {
		/*
		 * <!> additional implementation required later Temporarily implemented
		 * to test whether the inter-layer data forwarding function is performed
		 * smoothly
		 */
		boolean isFromARPLayer = true;
		byte[] arrToCompare = new byte[]{0x00,0x01,0x08,0x00};
		//boolean isFromIPLayer = true;
		
		/* Check which upper layer the packet came from */ 
		if(input.length > 4)
		for (int i=0; i<4; i++){
			if(input[i] != arrToCompare[i]){
				isFromARPLayer =false;
			}
		}
		
		 /* if ARP Layer, The first 4 bytes will have the same value as this. {0x00, 0x01, 0x08, 0x00}*/
		if (isFromARPLayer){
			System.out.println("Ethernet Send: ");

			// ARP Request Packet should be sent as broadcast
			// Make BroadCast Frame
			System.out.println("This is ARP Packet");
			this.m_sHeader.set_enet_type(new byte[] {0x08,0x06});
			this.m_sHeader.set_destination_address(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff});
		}	
		/* else, This is from the IP layer */		
		else{
		}
			byte[] encapsulated = Encapsulate(this.m_sHeader,input);
			
			Utils.showPacket(encapsulated);
			this.GetUnderLayer().Send(encapsulated, length);
			return true;
	}


	public boolean Receive(byte[] input) {
		/*
		 * <!> additional implementation required later Temporarily implemented
		 * to test whether the inter-layer data forwarding function is performed
		 * smoothly
		 */
	
		boolean isFrameISent = true;
		boolean isFrameForMe = true;
		boolean isBroadcastFrame = true;

		for (int i = 0; i < 6; i++) {
			/* Check whether received frame is Broadcast Frame */
			if (input[i] != (byte) 0xff) {
				isBroadcastFrame = false;
			}
			/* Check whether received frame is the frame I sent */
			if (this.GetEthernetHeader().get_source_address().addr[i] != input[i+6]) {
				isFrameISent = false;
			}

			/* Check whether I am the destination of this frame */
			if (this.GetEthernetHeader().get_source_address().addr[i] != input[i]) {
				isFrameForMe = false;
			}
		}

		if (!(isFrameISent) && (isBroadcastFrame || isFrameForMe)) {
			/*
			 * Receive only when the above conditions are satisfied If the above
			 * conditions are satisfied, the sending layer is determined
			 * according to the protocol type in the frame.
			 */
			System.out.println("Ethernet Received: ");
			Utils.showPacket(input);

			if (input[12] == (byte) 0x08 && input[13] == (byte) 0x00) {
				// if protocol type == IPv4 : 1
				byte[] decapsulated = this.Decapsulate(input);
				// call IPLayer.receive(..);
				this.GetUpperLayer(1).Receive(decapsulated);
			}

			else if (input[12] == (byte) 0x08 && input[13] == (byte) 0x06) {
				// if protocol type == ARP : 0
				byte[] decapsulated = this.Decapsulate(input);
				// call ARPLayer.Recevie(..);
				this.GetUpperLayer(0).Receive(decapsulated);
			}
			return true;
		}
		return false;
	}

	public _ETHERNET_HEADER GetEthernetHeader() {
		/*
		 * Getter for the ethernet header object declared as a member variable
		 * in the ethernet layer.
		 */
		return this.m_sHeader;
	}
	
	public void setEthernetHeaderSrcMacAddr(byte[] pSrcAddress) {
		this.m_sHeader.enet_srcaddr.addr = pSrcAddress;
	}
	public void setEthernetHeaderDstMacAddr(byte[] pDstAddress) {
		this.m_sHeader.enet_dstaddr.addr = pDstAddress;
	}
	public void setEthernetHeaderType(byte[] pHeaderType) {
		this.m_sHeader.set_enet_type(pHeaderType);
	}
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
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
