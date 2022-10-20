import java.util.ArrayList;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class FileTransferAppLayer implements BaseLayer {
	public static final byte TYPE_NOT_FRAGMENTED = (byte)0x00;
	public static final byte TYPE_INITIAL_FRAGMENT = (byte)0x01;
	public static final byte TYPE_MIDDLE_FRAGMENT = (byte)0x02;
	public static final byte TYPE_LAST_FRAGMENT = (byte)0x03;
	public static final byte TYPE_FILENAME = (byte)0x04;

	private static final int FRAGMENT_SIZE = 1400;
	private static final int LENGTH_OF_HEADER_EXCEPT_DATA = 29;

	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	_FILE_TRANSFER_HEADER m_sHeader = new _FILE_TRANSFER_HEADER();

	int receivedLength = 0;
	private ByteBuffer receivedBuffer = ByteBuffer.allocate(FRAGMENT_SIZE*100000);
	
	public FileTransferAppLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
	}
	
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
		private byte[] file_total_length = new byte[4];
		private byte fragment_type;
		private byte[] fragment_number = new byte[4];
		private byte[] file_name = new byte[20];
		private byte[] data;

		public _FILE_TRANSFER_HEADER() {
			// Empty constructor of file transfer header

		}

		public _FILE_TRANSFER_HEADER(byte[] pFileTotalLength, byte pFragmentType, byte[] pFragmentNumber, byte[] pFileName,
				byte[] pData) {
			// Constructor of file transfer header
			this.file_total_length = pFileTotalLength;
			this.fragment_number = pFragmentNumber;
			this.fragment_type = pFragmentType;
			this.file_name = pFileName;
			this.data = pData;
		}

		public int getTotalHeaderLength() {
			// retrun total header length
			return LENGTH_OF_HEADER_EXCEPT_DATA + data.length;
		}

	}
	

	public byte[] Encapsulate(_FILE_TRANSFER_HEADER pHeader) {
		// <!> Need to check
		// Encapsulate function to create byte array type ARP Packet
		int idx_ptr = 0;
		byte[] encapsulated = new byte[pHeader.getTotalHeaderLength()];

		for (int i = 0; i < pHeader.file_total_length.length; i++) {
			encapsulated[idx_ptr++] = m_sHeader.file_total_length[i];
		}
		encapsulated[idx_ptr++] = pHeader.fragment_type;

		for (int i = 0; i < pHeader.fragment_number.length; i++) {
			encapsulated[idx_ptr++] = pHeader.fragment_number[i];
		}
		for (int i =0; i< pHeader.file_name.length; i++){
			encapsulated[idx_ptr++] = pHeader.file_name[i];
		}
		for (int i = 0; i < pHeader.data.length; i++) {
			encapsulated[idx_ptr++] = pHeader.data[i];
		}
		return encapsulated;
	}
	
	public byte[] Decapulate(byte[] pData) {
	
		byte[] decapsulated = new byte[pData.length - LENGTH_OF_HEADER_EXCEPT_DATA];
		for (int i = 0; i < decapsulated.length; i++) {
			decapsulated[i] = pData[LENGTH_OF_HEADER_EXCEPT_DATA + i];
		}
		return decapsulated;
		
	}
	public boolean Send(byte[] pData, int pLength) {
		int fragmentTotalLength; // length of fragment
		int fileTotalLength = pData.length;
		System.out.println(new String(this.m_sHeader.file_name));
		if (fileTotalLength > FRAGMENT_SIZE) {
			// If Fragmentation needed!
			System.out.println("Fragmenting Process Start");

			int fragmentedLength = 0; // Fragmented size from full data size
			
			// Sequence of Fragment start with 0 
			int fragmentNumber = 0;
			
			// Declare variable to check if it is the last fragment during whole
			// fragmentation
			boolean isLastFragment = false;
			boolean isFirstFragment = true;

			// Start fragmentation
			while (!isLastFragment) {
				// check if it's last one.
				if (fileTotalLength- fragmentedLength < FRAGMENT_SIZE) {
					// If this is the last fragment, it fragments up to this point and does not
					// fragmenting any more.
					isLastFragment = true;
					
					// Assign total length of last fragment
					fragmentTotalLength = pData.length - fragmentedLength;

					// set Header's File total length
					this.setFileTotalLength(fileTotalLength);

					// Set header's fragment type feild value to last fragment type(0x03)
					this.m_sHeader.fragment_type = (byte) 0x03;

					// Set header's fragment's sequence
					this.setFileFragmentNumber(fragmentNumber++);
					
					// Declare an array to hold fragmented data
					byte[] fragmentedData = new byte[fragmentTotalLength];

					// Allocate the data of the current fragment part from the original data
					// Part: (Last Fragment offset ~ this fragment length) of original data
					fragmentedData = Arrays.copyOfRange(pData, fragmentedLength,
							fragmentedLength + fragmentTotalLength);

					// encapsulate data to call send function of it's Underlayer(TCP)
					byte[] encapsulated = this.Encapsulate(m_sHeader);

					
					// send to TCP Layer
					String fragmentTypeStr = "Null";
					if (this.m_sHeader.fragment_type == (TYPE_NOT_FRAGMENTED))fragmentTypeStr="Not Fragmented";
					else if (this.m_sHeader.fragment_type == (TYPE_INITIAL_FRAGMENT))fragmentTypeStr="Fragmented(Frist of Fragment)";
					else if (this.m_sHeader.fragment_type == (TYPE_MIDDLE_FRAGMENT))fragmentTypeStr="Fragmented(Middle of Fragment)";
					else if (this.m_sHeader.fragment_type == (TYPE_LAST_FRAGMENT))fragmentTypeStr="Fragmented(Last of Fragment)";
					
					((TCPLayer)this.GetUnderLayer(0)).setSourcePort(TCPLayer.FILE_TRANSFER_APP_PROT);
					((TCPLayer)this.GetUnderLayer(0)).setDestinationPort(TCPLayer.FILE_TRANSFER_APP_PROT);
					((TCPLayer) this.GetUnderLayer(0)).Send(encapsulated, encapsulated.length);
					
					// update fragemented length
					fragmentedLength += fragmentTotalLength;
					System.out.println("Fragment Number = " + this.castByteArrToInt(this.m_sHeader.fragment_number));
					System.out.println("Sending file.... ("+ fragmentedLength + "/" + fileTotalLength + ")");
					System.out.println("File Send Complete !");

				}

				else {
					// If it's not last part of fragment(if it's middle part), total length must be
					// FRAGMENT SIZE
					fragmentTotalLength = FRAGMENT_SIZE;

					if (isFirstFragment)

					{
						// Set header's fragment type feild value to initial fragment type(0x01)
						this.m_sHeader.fragment_type = (byte) 0x01;
						//After this, next frame is not the first frame, so change isFirstFrame to false
						isFirstFragment = false;
					}
					// If it's not first fragment, Set header's fragment type feild value to middle fragment type(0x02)
					else this.m_sHeader.fragment_type = (byte) 0x02;

					// set Header's File total length
					this.setFileTotalLength(fileTotalLength);

					// Set header's fragment's sequence
					this.setFileFragmentNumber(fragmentNumber++);
					
					// Declare an array to hold fragmented data
					byte[] fragmentedData = new byte[fragmentTotalLength];

					// Allocate the data of the current fragment part from the original data
					// Part: (Last Fragment offset ~ this fragment length) of original data
					fragmentedData = Arrays.copyOfRange(pData, fragmentedLength,
							fragmentedLength + fragmentTotalLength);

					// Set Header's data field
					this.setFileData(fragmentedData);

					// encapsulate data to call send function of it's Underlayer(TCP)
					byte[] encapsulated = this.Encapsulate(m_sHeader);


					// send to TCP Layer
					String fragmentTypeStr = "Null";
					if (this.m_sHeader.fragment_type == (TYPE_NOT_FRAGMENTED))fragmentTypeStr="Not Fragmented";
					else if (this.m_sHeader.fragment_type == (TYPE_INITIAL_FRAGMENT))fragmentTypeStr="Fragmented(Frist of Fragment)";
					else if (this.m_sHeader.fragment_type == (TYPE_MIDDLE_FRAGMENT))fragmentTypeStr="Fragmented(Middle of Fragment)";
					else if (this.m_sHeader.fragment_type == (TYPE_LAST_FRAGMENT))fragmentTypeStr="Fragmented(Last of Fragment)";
					
					((TCPLayer)this.GetUnderLayer(0)).setSourcePort(TCPLayer.FILE_TRANSFER_APP_PROT);
					((TCPLayer)this.GetUnderLayer(0)).setDestinationPort(TCPLayer.FILE_TRANSFER_APP_PROT);
					((TCPLayer) this.GetUnderLayer(0)).Send(encapsulated, encapsulated.length);
					
					// update fragemented length
					fragmentedLength += fragmentTotalLength;
					System.out.println("Fragment Number = " + this.castByteArrToInt(this.m_sHeader.fragment_number));
					System.out.println("Sending file.... ("+ fragmentedLength + "/" + fileTotalLength + ")");

				}

			}

		} else {
			// If fragmentation is not needed, Just send normally
			System.out.println("Fragmenting Process is not needed. Just send normally");
			fragmentTotalLength = pData.length;

			// Set header's fragment type feild value to not fragmented type(0x00)
			this.m_sHeader.fragment_type = (byte) 0x00;

			// set Header's File total length
			this.setFileTotalLength(fileTotalLength);

			// Set Header's Fragement Number to '0', Cause it won't be used when Fragmentation is not needed
			this.setFileFragmentNumber(0);
			
			// Declare an array to hold fragmented data
			byte[] fragmentedData = new byte[fragmentTotalLength];

			// Allocate the data of the current fragment part from the original data
			// Part: (Last Fragment offset ~ this fragment length) of original data
			fragmentedData = Arrays.copyOfRange(pData, 0, fragmentTotalLength);

			// Set Header's data field
			this.setFileData(fragmentedData);

			// encapsulate data to call send function of it's Underlayer(TCP)
			byte[] encapsulated = this.Encapsulate(m_sHeader);

			// send to TCP Layer
			String fragmentTypeStr = "Null";
			if (this.m_sHeader.fragment_type == (TYPE_NOT_FRAGMENTED))fragmentTypeStr="Not Fragmented";
			else if (this.m_sHeader.fragment_type == (TYPE_INITIAL_FRAGMENT))fragmentTypeStr="Fragmented(Frist of Fragment)";
			else if (this.m_sHeader.fragment_type == (TYPE_MIDDLE_FRAGMENT))fragmentTypeStr="Fragmented(Middle of Fragment)";
			else if (this.m_sHeader.fragment_type == (TYPE_LAST_FRAGMENT))fragmentTypeStr="Fragmented(Last of Fragment)";
			
			
			((TCPLayer)this.GetUnderLayer(0)).setSourcePort(TCPLayer.FILE_TRANSFER_APP_PROT);
			((TCPLayer)this.GetUnderLayer(0)).setDestinationPort(TCPLayer.FILE_TRANSFER_APP_PROT);
			((TCPLayer) this.GetUnderLayer(0)).Send(encapsulated, encapsulated.length);
			System.out.println("File Send Complete!");	

		}
		return true;
	}

	public boolean Receive(byte[] pData) {
		// Assign Decapsulated data
		byte[] decapsulated = this.Decapulate(pData);
		byte receivedFragmentType = pData[4];
		String receivedFragmentTypeStr = "Null";
		if (receivedFragmentType == (TYPE_NOT_FRAGMENTED))receivedFragmentTypeStr="Not Fragmented";
		else if (receivedFragmentType == (TYPE_INITIAL_FRAGMENT))receivedFragmentTypeStr="Fragmented(Frist of Fragment)";
		else if (receivedFragmentType == (TYPE_MIDDLE_FRAGMENT))receivedFragmentTypeStr="Fragmented(Middle of Fragment)";
		else if (receivedFragmentType == (TYPE_LAST_FRAGMENT))receivedFragmentTypeStr="Fragmented(Last of Fragment)";
		String fileNameStr = new String(this.getFileNameFromByte(pData));
		// Check if Received Data is Fragmented data
		if(this.getFragmentTypeFromByte(pData) == (byte)0x00) {
			// this is not Fragmented type data
			
			
			// Converting Byte type original data(= decapsulated data) to file type
			Utils.convertByteToFile(fileNameStr, "pwd", decapsulated);
			
		}
		else { 
			// if this is Fragmented type data (if type == 0x01 or 0x02 or 0x03)
			
			// Assign total length of received file.
			int receivedFileTotalLength = this.castByteArrToInt(this.getFileTotalLengthFromByte(pData));
			
			if(this.getFragmentTypeFromByte(pData) == (byte)0x01) {
				// if it's first fragment data of whole data, then initialize receivedBuffer size to total length of received file
				//this.receivedBuffer = ByteBuffer.allocate(receivedFileTotalLength);
				// initialize receivedLength to 0 before collecting fragments.
				this.receivedLength = 0;
			}
			
					
			// Start collecting Fragment to Receive Buffer
				
			// Declare variable to store fragmentNumber of received fragment
			int fragmentNumber = this.castByteArrToInt(this.getFileFragmentNumberFromByte(pData));
			
			// Put fragment into buffer with considering fragment number
			for(int i=0; i<decapsulated.length; i++)
			this.receivedBuffer.put(fragmentNumber*FRAGMENT_SIZE + i, decapsulated[i]);
			
			// Update received length
			this.receivedLength += decapsulated.length;			
			
			if(this.getFragmentTypeFromByte(pData)==(byte)0x03) {
				// If it's last fragment, it means that it collected all fragments of whole file.
				// After complete collecting all fragments, then make file with collecting bytes			
				
				byte[] bufferToByteArr = Arrays.copyOfRange(this.receivedBuffer.array(),0,receivedFileTotalLength);
				Utils.convertByteToFile(fileNameStr, "pwd", bufferToByteArr);
				
				// Reset receivedBuffer after making file for next receive.
				this.receivedBuffer.clear();
				
				// Reset ReceivedLength to '0' for next collecting.
				this.receivedLength = 0;
				}
			
			
			
		}
		return true;
	}
	public void setFileName(String pFileName){
		byte[] pFileNameBytes = pFileName.getBytes();
		for(int i =0; i<pFileNameBytes.length; i++){
			this.m_sHeader.file_name[i] =pFileNameBytes[i] ;
		}	
		}
	
	public void setFileTotalLength(int pTotalLengthInt) {
		// Casting integer type total length to byte using arithmetic shift
		// Casting to assign a higher digit to a lower index of file_total_length array
		for (int i = 0; i < this.m_sHeader.file_total_length.length; i++) {
			this.m_sHeader.file_total_length[i] = (byte) ((pTotalLengthInt >> (8
					* (this.m_sHeader.file_total_length.length - 1 - i))) & 0xff);
		}

	}

	public void setFileFragmentNumber(int pFileFramentNumber) {
		// Casting integer type fragment_number to byte using arithmetic shift

		// Casting to assign a higher digit to a lower index of fragment_number array
		for (int i = 0; i < this.m_sHeader.fragment_number.length; i++) {
			this.m_sHeader.fragment_number[i] = (byte) ((pFileFramentNumber >> (8
					* (this.m_sHeader.fragment_number.length - 1 - i))) & 0xff);
		}
	}
	
	public byte getFragmentTypeFromByte(byte[] pHeaderByte) { 
		// Return value of Fragment type field from File transfer header type byte array 
		
		// The offset of Fragment type field from File transfer header is '4' 
		return pHeaderByte[4];
	}
	public byte[] getFileTotalLengthFromByte(byte[] pHeaderByte) { 
		// Return value of File Total Length field from File transfer header type byte array 
		
		// The offset of Fragment type field from File transfer header is '0:4' 
		byte[] fileTotalLength = Arrays.copyOfRange(pHeaderByte, 0, 4);
		
		return fileTotalLength;
	} 
	
	public byte[] getFileFragmentNumberFromByte(byte[] pHeaderByte) { 
		// Return value of File Fragment Number field from File transfer header type byte array 
		
		// The offset of File Fragment Number field from File transfer header is '5:9' 
		byte[] fileFragmentNumber = Arrays.copyOfRange(pHeaderByte, 5, 9);
		
		return fileFragmentNumber;
	} 
	public byte[] getFileNameFromByte(byte[] pHeaderByte){
		return Arrays.copyOfRange(pHeaderByte,9,29);
	}
	public int castByteArrToInt(byte[] pByte) { 
		// Casting Byte array values to one integer value
		int byteArrToInt = 0;
		for (int i = 0; i < pByte.length; i++) {
			byteArrToInt |=  ((pByte[i] & 0xff) << 8*(pByte.length-1-i)) ;
		}
		return byteArrToInt;
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