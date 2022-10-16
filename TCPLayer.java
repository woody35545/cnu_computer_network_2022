import java.util.ArrayList;

public class TCPLayer implements BaseLayer {
	private static final byte[] CHAT_APP_PROT = new byte[] {(byte)0x20,(byte)0x80};
	private static final byte[] FILE_TRANSFER_APP_PROT = new byte[] {(byte)0x20,(byte)0x90};

	
	
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	
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
		this.pLayerName = pName;
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
		//this.GetUnderLayer(0).Send(data, length+24);
		
		this.m_sHeader.tcp_data = input;
		
		//if(app == this.GetUpperLayer(0).GetLayerName()){
		if (input[2] == (byte)0x00) {
			System.out.println("TCPLayer >> Thisis from Chatting Application");
		//ChatAppLayer(0x2080) : 0
			this.setSourcePort(CHAT_APP_PROT);
			this.setDestinationPort(CHAT_APP_PROT);
 		}
		//else if(app == this.GetUpperLayer(1).GetLayerName()){
		else if(input[2] == (byte)0x01) {
			//FileAppLayer(0x2090) : 1
			System.out.println("TCPLayer >> Thisis from File Transfer Application");
			this.setSourcePort(FILE_TRANSFER_APP_PROT);
			this.setDestinationPort(FILE_TRANSFER_APP_PROT);
		}
		
		System.out.println("TCP Send: ");
		Utils.showPacket(objToByte(this.m_sHeader, input, length));
		this.GetUnderLayer(0).Send(objToByte(this.m_sHeader, input, length), length+24);
		
		return true;

	}

	public byte[] RemoveCappHeader(byte[] input, int length){
		byte[] headerRemoved = new byte[length-24];
		for(int i =0; i<length-24; i++){
			headerRemoved[i] = input[24+i];
		}
		
		return headerRemoved;
	}
	
	public boolean Receive(byte[] input){
		byte[] data;
		System.out.println("TCP received:");
		Utils.showPacket(input);
		if(input[2]==(byte)0x20 && input[3]==(byte)0x80){
			
			System.out.println("TCP >> This is ChatAppLayer data:");

			//ChatAppLayer(0x2080) : 0
			data = RemoveCappHeader(input, input.length);
			this.GetUpperLayer(0).Receive(data);
			
			return true;
		}
		else if(input[2]==(byte)0x20 && input[3]==(byte)0x90){
			//FileAppLayer(0x2090) : 1
			System.out.println("TCP >> This is FileAppLayer data:");

			data = RemoveCappHeader(input, input.length);
			this.GetUpperLayer(1).Receive(data);
			
			return true;
		}
	
		
		return false;
	}
	
	
	public void setSourcePort(byte[] pSourcePort) {
		this.m_sHeader.tcp_sport=pSourcePort;
	}
	
	public void setDestinationPort(byte[] pDestinationPort) {
		this.m_sHeader.tcp_dport =pDestinationPort;
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
}
