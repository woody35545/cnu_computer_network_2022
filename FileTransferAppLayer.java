import java.util.ArrayList;

public class FileTransferAppLayer implements BaseLayer {
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private class _FILE_TRANSFER_HEADER {
		/*
		 * Data structure for File Trasfer type message.
		 * 
		 * [file_total_length] - length of data (not header total length)
		 *
		 * [fragment types] 
		 * - 0x00: not fragment 
		 * - 0x01: initial part of fragment 
		 * - 0x02: middle part of fragment 
		 * - 0x03: last part of fragement
		 * 
		 * [fragment number] - if data send with fragmentation mode, this field
		 * represent the sequence of fragement.
		 * 
		 * [data] - data that user want to send
		 */
		private byte[] file_total_length;
		private byte fragment_type;
		private byte[] fragment_number;
		private byte[] data;

		public _FILE_TRANSFER_HEADER() {
			// Empty constructor of file transfer header

		}

		public _FILE_TRANSFER_HEADER(byte[] pFileTotalLength, byte pFragmentType, byte[] pFragmentNumber,
				byte[] pData) {
			// Constructor of file transfer header
			this.file_total_length = pFileTotalLength;
			this.fragment_number = pFragmentNumber;
			this.fragment_type = pFragmentType;
			this.data = pData;
		}

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
