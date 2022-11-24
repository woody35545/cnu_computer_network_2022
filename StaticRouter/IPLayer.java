package StaticRouter;

import java.util.ArrayList;
import java.util.Arrays;

import StaticRouter.ARPLayer._ARP_CACHE_TABLE;

public class IPLayer implements BaseLayer {
	public RoutingTableManager routingTableManager = new RoutingTableManager();

	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	public final static int IPHEADER = 20;

	private class _IPLayer_HEADER {
		byte[] ip_versionLen; // ip version -> IPv4 : 4
		byte[] ip_serviceType; // type of service
		byte[] ip_packetLen; // total packet length
		byte[] ip_datagramID; // datagram id
		byte[] ip_offset; // fragment offset
		byte[] ip_ttl; // time to live in gateway hops
		byte[] ip_protocol; // IP protocol
		byte[] ip_cksum; // header checksum
		byte[] ip_srcaddr; // IP address of source
		byte[] ip_dstaddr; // IP address of destination
		byte[] ip_data; // variable length data

		public _IPLayer_HEADER() {
			this.ip_versionLen = new byte[1];
			this.ip_serviceType = new byte[1];
			this.ip_packetLen = new byte[2];
			this.ip_datagramID = new byte[2];
			this.ip_offset = new byte[2];
			this.ip_ttl = new byte[1];
			this.ip_protocol = new byte[1];
			this.ip_cksum = new byte[2];
			this.ip_srcaddr = new byte[4];
			this.ip_dstaddr = new byte[4];
			this.ip_data = null;
		}
	}

	_IPLayer_HEADER m_sHeader = new _IPLayer_HEADER();

	public IPLayer(String pName) {
		pLayerName = pName;
		m_sHeader = new _IPLayer_HEADER();
	}

	private byte[] ObjToByte(_IPLayer_HEADER m_sHeader2, byte[] input, int length) {
		byte[] buf = new byte[length + IPHEADER];

		buf[0] = m_sHeader2.ip_versionLen[0];
		buf[1] = m_sHeader2.ip_serviceType[0];
		buf[2] = m_sHeader2.ip_packetLen[0];
		buf[3] = m_sHeader2.ip_packetLen[1];
		buf[4] = m_sHeader2.ip_datagramID[0];
		buf[5] = m_sHeader2.ip_datagramID[1];
		buf[6] = m_sHeader2.ip_offset[0];
		buf[7] = m_sHeader2.ip_offset[1];
		buf[8] = m_sHeader2.ip_ttl[0];
		buf[9] = m_sHeader2.ip_protocol[0];
		buf[10] = m_sHeader2.ip_cksum[0];
		buf[11] = m_sHeader2.ip_cksum[1];
		// addr
		for (int i = 0; i < 4; i++) {
			buf[12 + i] = m_sHeader2.ip_srcaddr[i];
			buf[16 + i] = m_sHeader2.ip_dstaddr[i];
		}
		// data
		for (int i = 0; i < length; i++) {
			buf[20 + i] = input[i];
		}
		return buf;
	}

	public boolean Send(byte[] input, int length) {
		m_sHeader.ip_offset[0] = 0x00;
		m_sHeader.ip_offset[1] = 0x03;
		byte[] bytes = ObjToByte(m_sHeader, input, length);

		// Routing Table의 Entry들과 Subnet 연산 후 해당되는 Entry IP 찾기

		/*
		 * Entry가 존재하는 경우 -> Arp Cache Table에서 IP 조회 1. ARP Cache Table에 있을 경우 -> 찾은 MAC
		 * 주소를 이용해서 EthernetLayer로 바로 전송 2. ARP Cache Table에 없을 경우 Request 전송 2-1. Reply
		 * 올 때까지 대기 2-2. Reply 오면 받은 MAC 주소를 이용해 EthernetLayer로 전송
		 */

		/*
		 * Entry가 존재하지 않는 경우 -> Default Gateway로 전송 1. ARP Table에서 Default Gateway에 대한
		 * MAC 주소 찾기 2. 반환 받은 MAC 주소를 이용해서 packet 만들고 EthernetLayer로 보내기
		 */

		// ARP Cache Table은 getArpCacheTable로 접근가능
		byte[] entry_ip = RoutingTableManager.getRoutingTable().getSubnet(Utils.convertAddrFormat(StaticRouterGUI.DEST_IP));
		if(!entry_ip.equals(new byte[] {-1,-1,-1,-1})){
		String dst_mac_addr = this.getArpCacheTable()
				.getMacAddr(Utils.convertAddrFormat(entry_ip));
		System.out.println(dst_mac_addr);

		if (!dst_mac_addr.equals("IsNotExist")) {
			((EthernetLayer) this.GetUnderLayer(1))
					.setEthernetHeaderSrcMacAddr(Utils.convertAddrFormat(StaticRouterGUI.MAC_ADDR_0));
			((EthernetLayer) this.GetUnderLayer(1)).setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(dst_mac_addr));

			this.GetUnderLayer(1).Send(bytes, length + IPHEADER);
		}

		else {

			// set Target IP
			this.getArpLayer().setARPHeaderDstIp(entry_ip);
			this.getArpLayer().Send();
			// wait until receiving reply from target host
		}

		return true;
		}
		return false;
	}
	public boolean Routing(byte[] input, int length) {
		m_sHeader.ip_offset[0] = 0x00;
		m_sHeader.ip_offset[1] = 0x03;
		byte[] targetIp = Arrays.copyOfRange(input, 16, 20);
		byte[] bytes = ObjToByte(m_sHeader, input, length);

		// Routing Table의 Entry들과 Subnet 연산 후 해당되는 Entry IP 찾기

		/*
		 * Entry가 존재하는 경우 -> Arp Cache Table에서 IP 조회 1. ARP Cache Table에 있을 경우 -> 찾은 MAC
		 * 주소를 이용해서 EthernetLayer로 바로 전송 2. ARP Cache Table에 없을 경우 Request 전송 2-1. Reply
		 * 올 때까지 대기 2-2. Reply 오면 받은 MAC 주소를 이용해 EthernetLayer로 전송
		 */

		/*
		 * Entry가 존재하지 않는 경우 -> Default Gateway로 전송 1. ARP Table에서 Default Gateway에 대한
		 * MAC 주소 찾기 2. 반환 받은 MAC 주소를 이용해서 packet 만들고 EthernetLayer로 보내기
		 */

		// ARP Cache Table은 getArpCacheTable로 접근가능
		byte[] entry_ip = RoutingTableManager.getRoutingTable().getSubnet(targetIp);
		if(!entry_ip.equals(new byte[] {-1,-1,-1,-1})){
		String dst_mac_addr = this.getArpCacheTable()
				.getMacAddr(Utils.convertAddrFormat(entry_ip));
		System.out.println(dst_mac_addr);

		if (!dst_mac_addr.equals("IsNotExist")) {
			((EthernetLayer) this.GetUnderLayer(1))
					.setEthernetHeaderSrcMacAddr(Utils.convertAddrFormat(StaticRouterGUI.MAC_ADDR_0));
			((EthernetLayer) this.GetUnderLayer(1)).setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(dst_mac_addr));

			this.GetUnderLayer(1).Send(bytes, length + IPHEADER);
		}

		else {

			// set Target IP
			this.getArpLayer().setARPHeaderDstIp(entry_ip);
			this.getArpLayer().Send();
			// wait until receiving reply from target host
		}
		

		return true;
		}return false;
	}
	public byte[] RemoveCappHeader(byte[] input, int length) {
		byte[] remvHeader = new byte[length - IPHEADER];
		for (int i = 0; i < length - IPHEADER; i++) {
			remvHeader[i] = input[i + IPHEADER];
		}
		return remvHeader;

	}

	public synchronized boolean Receive(byte[] input) {

		if (StaticRouterGUI.NODE_TYPE.equals("HOST")) {
			// 원래하던대로 UpperLayer로 보내면 됨
			byte[] data = RemoveCappHeader(input, input.length);

			if (me_equals_dst_Addr(input)) {
				System.out.println("It's Packet for me.");

				this.GetUpperLayer(0).Receive(data);

			} else if (StaticRouterGUI.NODE_TYPE.equals("ROUTER")) {
				// Routing 함수 호출
				this.Routing(input, input.length);
			}
		}
		return true;
	}

	public boolean me_equals_dst_Addr(byte[] input) {
														
		for (int i = 0; i < 4; i++) {
			if (input[i + 16] != m_sHeader.ip_srcaddr[i])
				return false;
		}

		return true;
	}

	public boolean me_equals_src_Addr(byte[] input) {
														
		for (int i = 0; i < 4; i++) {
			if (input[i + 12] != m_sHeader.ip_srcaddr[i])
				return false;
		}

		return true;
	}

	public void setIpHeaderSrcIPAddr(byte[] pSrcAddr) {
		this.m_sHeader.ip_srcaddr = pSrcAddr;
	}

	public void setIpHeaderDstIPAddr(byte[] pDstAddr) {
		this.m_sHeader.ip_dstaddr = pDstAddr;

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
		if (nindex < 0 || nindex >= nUnderLayerCount || nUnderLayerCount < 0)
			return null;
		return p_UnderLayer.get(nindex);
	}

	public ARPLayer getArpLayer() {
		return ((ARPLayer) this.GetUnderLayer(0));
	}

	public _ARP_CACHE_TABLE getArpCacheTable() {
		return ((ARPLayer) this.GetUnderLayer(0)).getArpCacheTable();
	}

}