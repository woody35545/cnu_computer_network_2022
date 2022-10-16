import java.util.ArrayList;
import java.util.Arrays;

public class FileTransferAppLayer implements BaseLayer {
	private static final int FRAGMENT_SIZE = 1400;

	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	_FILE_TRANSFER_HEADER m_sHeader = new _FILE_TRANSFER_HEADER();

	private class _FILE_TRANSFER_HEADER {
		/*
		 * Data structure for File Trasfer type message.
		 * 
		 * [file_total_length] - length of data (not header total length)
		 *
		 * [fragment types] - 0x00: not fragment - 0x01: initial part of fragment -
		 * 0x02: middle part of fragment - 0x03: last part of fragement
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

	
	public boolean Send(byte[] pData, int pLength) {
		if (pData.length > FRAGMENT_SIZE) {
			// Fragmentation needed!

			int fragmentTotalLength; // length of fragment
			int fragmentedLength= 0; // Fragmented size from full data size
		
			
			// Declare variable to check if it is the last fragment during whole fragmentation
			boolean isLastFragment = false; 
		
			
			// Start fragmentation
			while(!isLastFragment) { 
				// check if it's last one.
				if(pData.length - fragmentedLength < FRAGMENT_SIZE) {
					//If this is the last fragment, it fragments up to this point and does not fragment any more.
					isLastFragment = false;
					
					// Assign total length of last fragment
					fragmentTotalLength = pData.length - fragmentedLength;
					
					// set Header's File total length
					this.setFileTotalLength(fragmentTotalLength);
					
					// Set header's fragment type feild value to last fragment type(0x03)
					this.m_sHeader.fragment_type = (byte)0x03;
					
					// Declare an array to hold fragmented data
					byte[] fragmentedData = new byte[fragmentTotalLength];
					
					// Allocate the data of the current fragment part from the original data
					// Part: (Last Fragment offset ~ this fragment length) of original data
					fragmentedData = Arrays.copyOfRange(pData, fragmentedLength, fragmentedLength+fragmentTotalLength);
					
					// encapsulate data to call send function of it's Underlayer(TCP)


				}
				else {
					// If it's not last part of fragment(if it's middle part), total length must be FRAGMENT SIZE
					fragmentTotalLength = FRAGMENT_SIZE;
					
					// Set header's fragment type feild value to middle fragment type(0x02)
					this.m_sHeader.fragment_type = (byte)0x02;
					
					// set Header's File total length
					this.setFileTotalLength(fragmentTotalLength);
					
					// update fragemented length
					fragmentedLength += fragmentTotalLength;
					
					
					// Declare an array to hold fragmented data
					byte[] fragmentedData = new byte[fragmentTotalLength];
					
					// Allocate the data of the current fragment part from the original data
					// Part: (Last Fragment offset ~ this fragment length) of original data
					fragmentedData = Arrays.copyOfRange(pData, fragmentedLength, fragmentedLength+fragmentTotalLength);
					
					// Set Header's data field
					this.setFileData(fragmentedData);
					
					// encapsulate data to call send function of it's Underlayer(TCP)
					
					
				}
				
				
				
			
			}
			
		} else {
			// Just send normally

		}
		return true;
	}

	public boolean Receive(byte[] pData) {
		return true;
	}
	
	
	
	public void setFileTotalLength(int pTotalLengthInt) {
		// Casting integer type total length to byte using arithmetic shift
		// Casting to assign a higher digit to a lower index of file_total_length array
		for (int i = 0; i < this.m_sHeader.file_total_length.length; i++) {
			this.m_sHeader.file_total_length[i] = (byte)((pTotalLengthInt >> (8*(this.m_sHeader.file_total_length.length-1-i))) & 0xff);
		}
	
	}
	
	public void setFileFragmentNumber(int pFileFramentNumber) {
		// Casting integer type fragment_number to byte using arithmetic shift
		
		// Casting to assign a higher digit to a lower index of fragment_number array
		for (int i = 0; i < this.m_sHeader.fragment_number.length; i++) {
			this.m_sHeader.fragment_number[i] = (byte)((pFileFramentNumber >> (8*(this.m_sHeader.fragment_number.length-1-i))) & 0xff);
		}
	}

	public void setFileData(byte[] pData) {
		this.m_sHeader.data = pData;
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
