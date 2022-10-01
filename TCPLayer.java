import java.util.ArrayList;

public class TCPLayer implements BaseLayer {
	public int p_UpperLayerCount = 0;
	public String p_LayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_UpperLayer = new ArrayList<BaseLayer>();
	
	private class TCPLayer_HEADER {
		byte[] tcp_sport; // source port
		byte[] tcp_dport; // destination port
		byte[] tcp_seq; // sequence number
		byte[] tcp_ack; // acknowledged sequence
		byte[] tcp_offset; // no use
		byte[] tcp_flag; // control flag
		byte[] tcp_window; // no use
		byte[] tcp_cksum; // check sum
		byte[] tcp_urgptr; // no use 
		byte[] Padding;
		byte[] tcp_data; // data part

		public TCPLayer_HEADER() {
			this.tcp_sport = new byte[2];
			this.tcp_dport = new byte[2];
			this.tcp_seq = new byte[4];
			this.tcp_ack = new byte[4];
			this.tcp_offset = new byte[1];
			this.tcp_flag = new byte[1];
			this.tcp_window = new byte[2];
			this.tcp_cksum = new byte[2];
			this.tcp_urgptr = new byte[2];
			this.Padding = new byte[4];
			this.tcp_data = null;
		}
	}

	TCPLayer_HEADER m_sHeader = new TCPLayer_HEADER();

	
	
	public TCPLayer(String pName) {
		this.p_LayerName = pName;
		m_sHeader = new TCPLayer_HEADER();
	}
	
	public byte[] objToByte(TCPLayer_HEADER m_sHeader2, byte[] input, int length){
		byte[] buf = new byte[length+24];

		buf[0]=m_sHeader2.tcp_sport[0];
		buf[1]=m_sHeader2.tcp_sport[1];
		buf[2]=m_sHeader2.tcp_dport[0];
		buf[3]=m_sHeader2.tcp_dport[1];
		buf[12]=m_sHeader2.tcp_offset[0];
		buf[13]=m_sHeader2.tcp_flag[0];
		buf[14]=m_sHeader2.tcp_window[0];
		buf[15]=m_sHeader2.tcp_window[1];
		buf[16]=m_sHeader2.tcp_cksum[0];
		buf[17]=m_sHeader2.tcp_cksum[1];
		buf[18]=m_sHeader2.tcp_urgptr[0];
		buf[19]=m_sHeader2.tcp_urgptr[1];
		
		for (int i = 0; i < 4; i++) {
			buf[4 + i] = m_sHeader2.tcp_seq[i];
			buf[8 + i] = m_sHeader2.tcp_ack[i];
			buf[20 + i] = m_sHeader2.Padding[i];
		}
		
		for(int i=0;i<length;i++){
			buf[i+24]=input[i];
		}

		return buf;

	}
	
	
	public boolean Send(byte[] input, int length) {

		byte[] data = objToByte(this.m_sHeader,input,length);
		this.GetUnderLayer().Send(data, length+20);
		
		return true;

	}

	
	
	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return this.p_LayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		return this.p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		return this.p_UpperLayer.get(nindex); //ChatAppLayer, FileAppLayer
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		this.p_UpperLayer.add(p_UpperLayerCount++, pUpperLayer); //ChatAppLayer, FileAppLayer
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