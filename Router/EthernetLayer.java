package Router;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class EthernetLayer implements BaseLayer {
	public static final byte[] TYPE_ARP = new byte[] { (byte) 0x08, (byte) 0x06 };
	public static final byte[] TYPE_IPv4 = { (byte) 0x08, (byte) 0x00 };

	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
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
			for (int i = 0; i < 46; i++) {
				padding[i] = (byte) 0x00;
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

	public synchronized boolean Send(byte[] input, int length, String networkInterface) {
		/*
		 * <!> additional implementation required later Temporarily implemented to test
		 * whether the inter-layer data forwarding function is performed smoothly
		 */
		boolean isFromARPLayer = false;
		/* Check which upper layer the packet came from */
		if (input.length > 4)
			isFromARPLayer = Utils.compareBytes(Arrays.copyOfRange(input, 0, 4), new byte[] { 0x00, 0x01, 0x08, 0x00 });
		
		if (isFromARPLayer) {

			// ARP Request Packet should be sent as broadcast
			// Make BroadCast Frame
			this.m_sHeader.set_enet_type(new byte[] { 0x08, 0x06 });
			this.m_sHeader.set_destination_address(
					new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff });

		}
		/* else, This is from the IP layer */
		else {
			this.m_sHeader.set_enet_type(new byte[] { 0x08, 0x00 });
		}
		
		

//			System.out.println("Ethernet Send: ");
//			Utils.showPacket(encapsulated);

		
		byte[] encapsulated = Encapsulate(this.m_sHeader, input);

		if (networkInterface.equals("Interface_1")) {
			((NILayer) this.GetUnderLayer(0)).Port1_Send(encapsulated, encapsulated.length);
		} else if (networkInterface.equals("Interface_2")) {
			((NILayer) this.GetUnderLayer(0)).Port2_Send(encapsulated, encapsulated.length);
		}
		return true;
	}

	public synchronized boolean Receive(byte[] input) {
		
		byte[] receivedSrcMacAddr = Arrays.copyOfRange(input, 6, 12);
		boolean isFrameISent = false;
		byte[] receivedType = Arrays.copyOfRange(input, 12, 14);

		if(this.getRouterGUI().NODE_TYPE.equals("ROUTER")) {
		
			/*---- check if received frame is frame that I sent ----*/
			
			// check if received frame is Sent from me (NIC port #1)
			if (Utils.compareBytes(Utils.convertAddrFormat(RouterGUI.MAC_ADDR_PORT1), receivedSrcMacAddr)) {
				isFrameISent = true;
			}
			// check if received frame is Sent from me (NIC port #2)
			if (Utils.compareBytes(Utils.convertAddrFormat(RouterGUI.MAC_ADDR_PORT2), receivedSrcMacAddr)){
				isFrameISent = true;
			}
			if(isFrameISent == false) {
				// if it's not frame that I sent, send frame to IP or ARP Layer according to value of type field
				
				// if it's IP Packet, Call IP Layer's Receive
				if (Utils.compareBytes(receivedType, TYPE_IPv4)) {
					byte[] decapsulated = this.Decapsulate(input);

					// call IPLayer.Routing(..);
					this.getIPLayer().Routing(decapsulated, decapsulated.length);
				}
				// if it's ARP Packet, Call ARP Layer's Receive
				else if (Utils.compareBytes(receivedType, TYPE_ARP)) {
					byte[] decapsulated = this.Decapsulate(input);
					// call ARPLayer.Recevie(..);
					((ARPLayer)this.GetUpperLayer(0)).Receive(decapsulated);
				}
			return true;
			}
		}
		
		else if(this.getRouterGUI().NODE_TYPE== "HOST") {
		/*-- for host type device --*/

		
		/*
		 * <!> additional implementation required later Temporarily implemented to test
		 * whether the inter-layer data forwarding function is performed smoothly
		 */

			return false;

		}
		return false;
		
	}

	public _ETHERNET_HEADER GetEthernetHeader() {
		/*
		 * Getter for the ethernet header object declared as a member variable in the
		 * ethernet layer.
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
	public IPLayer getIPLayer() {
		return ((IPLayer)this.GetUpperLayer(1));
	}
	public RouterGUI getRouterGUI() {
		return ((RouterGUI)this.GetUpperLayer(2));
	}

}
