import java.util.ArrayList;

public class IPLayer implements BaseLayer {
	public int p_UnderLayerCount = 0;
	public String p_LayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public BaseLayer p_UpperLayer = null;
	
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
		p_LayerName = pName;
		m_sHeader = new _IPLayer_HEADER(); 
	}
	
	private byte[] ObjToByte(_IPLayer_HEADER m_sHeader2, byte[] input, int length) {
		byte[] buf = new byte[length + 20];

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
		for (int i = 0; i < 4; i++) {
			buf[12 + i] = m_sHeader2.ip_srcaddr[i];
			buf[16 + i] = m_sHeader2.ip_dstaddr[i];
		}
		for (int i = 0; i < length; i++) {
			buf[20 + i] = input[i];
		}
		return buf;
	}
	 
	public boolean Send(byte[] input, int length) {
		byte[] bytes = ObjToByte(m_sHeader,input,length);
		
		//((ARPLayer)this.GetUnderLayer()).Send(m_sHeader.ip_srcaddr, m_sHeader.ip_dstaddr);
		return true;
	}


	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return this.p_LayerName;
	}

	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		// TODO Auto-generated method stub
		return this.p_UnderLayer.get(nindex); //ARPLayer, EthernetLayer
	}

	@Override
	public BaseLayer GetUpperLayer() {
		// TODO Auto-generated method stub
		return this.p_UpperLayer;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		this.p_UnderLayer.add(p_UnderLayerCount++, pUnderLayer); //ARPLayer, EthernetLayer
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		this.p_UpperLayer = pUpperLayer;
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		return null;
	}

}
