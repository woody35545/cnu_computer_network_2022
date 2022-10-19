import java.util.ArrayList;
import java.util.Arrays;

public class IPLayer implements BaseLayer {
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	public final static int IPHEADER = 20;
	
	private class _IPLayer_HEADER {
		byte[] ip_versionLen;	// ip version -> IPv4 : 4
		byte[] ip_serviceType;	// type of service
		byte[] ip_packetLen;	// total packet length
		byte[] ip_datagramID;	// datagram id
		byte[] ip_offset;		// fragment offset
		byte[] ip_ttl;			// time to live in gateway hops
		byte[] ip_protocol;		// IP protocol
		byte[] ip_cksum;		// header checksum
		byte[] ip_srcaddr;		// IP address of source
		byte[] ip_dstaddr;		// IP address of destination
		byte[] ip_data;			// variable length data
		
		public _IPLayer_HEADER(){
			this.ip_versionLen = new byte[1];
			this.ip_serviceType = new byte[1];
			this.ip_packetLen = new byte[2];
			this.ip_datagramID = new byte[2];
			this.ip_offset = new byte[2];
			this.ip_ttl = new byte[1];
			this.ip_protocol = new byte[1];
			this.ip_cksum = new byte[2];
			this.ip_srcaddr = new byte[4];
			this.ip_dstaddr= new byte[4];
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
		Utils.consoleMsg("Call by TCPLayer.send");			
		Utils.consoleMsg("### IPLayer.send() ###");
		Utils.consoleMsg("<IP Header>");
		Utils.consoleMsg("*Source IP | " + Utils.convertAddrFormat(m_sHeader.ip_srcaddr));
		Utils.consoleMsg("*Target IP | " + Utils.convertAddrFormat(m_sHeader.ip_dstaddr));
		Utils.consoleMsg("Send to EthernetLayer..\n");
		
		//port 0x2080 : Chat App Layer , port 0x2090 : File App Layer
		if((input[0]==(byte)0x20 && input[1]==(byte)0x80 )) {
			// from Chat App Layer
		
			
			
			m_sHeader.ip_offset[0] = 0x00;
			m_sHeader.ip_offset[1] = 0x03;
			byte[] bytes = ObjToByte(m_sHeader,input,length);	//IP 占쎈엘占쎈쐭 �빊遺쏙옙 (EnCapsulate)
		
			((EthernetLayer)this.GetUnderLayer(1)).setEthernetHeaderDstMacAddr(Utils.convertAddrFormat(ARPGUI.CHAT_DEST_MAC_ADDR));
			this.GetUnderLayer(1).Send(bytes,length+IPHEADER);
			return true;
		}

		
		else if (input[0]==(byte)0x20 && input[1]==(byte)0x90){
			// from Chat File App Layer
			return true;
		}
			
		
		return false;
	}
	
	public byte[] RemoveCappHeader(byte[] input, int length) {
		//筌╈돦�뮄占쎌넅 占쎈뻥占쎈쐲 IP 占쎈엘占쎈쐭占쎈굶占쎌뱽 占쎌젫椰꾧퀬釉놂옙�뼄. 
		byte[] remvHeader = new byte[length-IPHEADER];
		for(int i=0;i<length-IPHEADER;i++) {
			remvHeader[i] = input[i+IPHEADER];
		}
		return remvHeader;
		
	}
	
	

	public synchronized boolean Receive(byte[] input) {
		byte[] receivedSrcIP = Arrays.copyOfRange(input, 12, 16);
		byte[] receivedDstIP = Arrays.copyOfRange(input, 16, 20);

		Utils.consoleMsg("### IPLayer.Receive() ###");
		Utils.consoleMsg("<Received IP Header>");
		Utils.consoleMsg("*Source IP | " + Utils.convertAddrFormat(receivedSrcIP));
		Utils.consoleMsg("*Destination IP | " + Utils.convertAddrFormat(receivedDstIP));
		Utils.consoleMsg("Send up to TCP Layer..\n");
		
		byte[] data = RemoveCappHeader(input, input.length);
		
		if(me_equals_dst_Addr(input)) {
			this.GetUpperLayer(0).Receive(data);
			return true;
		}else {
			return false;
		}
	}
	
	public boolean me_equals_dst_Addr(byte[] input) {//占쎈솭占쎄땅占쎌벥 dst 雅뚯눘�꺖占쎌삂 占쎄땀 雅뚯눘�꺖占쎌삂 揶쏆늿占쏙쭪占� 占쎌넇占쎌뵥
		for(int i = 0;i<4;i++) {
			if(input[i+16]!=m_sHeader.ip_srcaddr[i]) return false;
		}

		return true;
	}
	public boolean me_equals_src_Addr(byte[] input) {//占쎈솭占쎄땅占쎌벥 src 雅뚯눘�꺖占쎌삂 占쎄땀 雅뚯눘�꺖占쎌삂 揶쏆늿占쏙쭪占� 占쎌넇占쎌뵥
		for(int i = 0;i<4;i++) {
			if(input[i+12]!=m_sHeader.ip_srcaddr[i]) return false;
		}

		return true;
	}
	
	
	public void setIpHeaderSrcIPAddr(byte[] pSrcAddr) {
		this.m_sHeader.ip_srcaddr=pSrcAddr;
	}
	public void setIpHeaderDstIPAddr(byte[] pDstAddr) {
		this.m_sHeader.ip_dstaddr=pDstAddr;

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

	

}
