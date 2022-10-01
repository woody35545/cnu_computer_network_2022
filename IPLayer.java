public class IPLayer implements BaseLayer {
	
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

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
		
	}

}
