import java.util.ArrayList;

public class ChatAppLayer implements BaseLayer{
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private class _CAPP_HEADER{
		byte[] capp_totlen;
		byte capp_type;
		byte capp_unused;
		byte[] capp_data;
		
		public _CAPP_HEADER(){
			this.capp_totlen = new byte[2];
			this.capp_type = 0x00;
			this.capp_unused = 0x00;
			this.capp_data = null;
		}
	}
	
	_CAPP_HEADER m_sHeader = new _CAPP_HEADER();
	
	public ChatAppLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}
	
	public void ResetHeader(){
		for(int i=0; i<2; i++){
			m_sHeader.capp_totlen[i] = (byte) 0x00;
		}
		m_sHeader.capp_type = (byte) 0x00;	// if Chat: 0x00, FileTransfer: 0x01
		m_sHeader.capp_unused = (byte) 0x00;	
		m_sHeader.capp_data = null;	
	}
	
	public byte[] ObjToByte(_CAPP_HEADER Header, byte[] input, int length){
	
		return null;		
	}
	
	public byte[] Encapsulate(_CAPP_HEADER pHeader, byte[] input) {
		byte[] encapsulated = new byte[4+input.length];
		for (int i=0; i<pHeader.capp_totlen.length; i++) {
			encapsulated[i]= pHeader.capp_totlen[i];
		}
		
		encapsulated[2]=pHeader.capp_unused;
		for (int i=0; i<pHeader.capp_data.length; i++) {
			encapsulated[3]=pHeader.capp_data[i];
		}
		return encapsulated;
		
	}
	
	public byte[] Decapsulate(byte[] input) {
		byte[] decapsulated= new byte[input.length-4];
		for (int i =0; i<decapsulated.length; i++) {
			decapsulated[i] = input[i+4];
		}
		
		return decapsulated;
		
	}
    public boolean Send(byte[] input, int length) {   
		/* <!> additional implementation required later */
	    // Need to check
    	this.m_sHeader.capp_data = input; 
    	byte[] encapsulated = this.Encapsulate(m_sHeader, input);
    	this.GetUnderLayer(0).Send(encapsulated,encapsulated.length, pLayerName);
		return false;
	}

          
	public boolean Receive(byte[] input){
		this.GetUpperLayer(0).Receive(this.Decapsulate(input));
		return true;
	}
	
	public void setAppType(byte pAppType) {
		this.m_sHeader.capp_type = pAppType;
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
