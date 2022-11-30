package Router;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public final class Utils {
	/*
	 * This class implements useful functions that can be used in general for
	 * implementing layer logic.
	 */

	private static final boolean DEBUG_MODE = true;

	public static boolean compareBytes(byte[] pByteArr_1, byte[] pByteArr_2) {
		/*
		 * Byte array comparison (only for same size byte array)
		 */
		for (int i = 0; i < pByteArr_1.length; i++) {
			if (((byte) pByteArr_1[i]) != ((byte) pByteArr_2[i])) {
				return false;
			}
		}
		return true;
	}

	public static void showPacket(byte[] packet) {
		if (DEBUG_MODE && packet != null) {
			for (int i = 0; i < packet.length; i++) {
				System.out.print("[" + i + "]" + String.format("%02X ", packet[i]));
				if ((i + 1) % 16 == 0) {
					System.out.println("");

				}
			}
			System.out.println("\n");
		}
	}
	

	public static byte[] convertStrFormatIpToByteFormat(String strFormatIp) {
		// "0.0.0.0" -> byte[]
		String[] strFormatIp_splited = strFormatIp.split(Pattern.quote("."));
		byte[] res_byte = new byte[4];
		for (int i = 0; i < 4; i++) {
			res_byte[i] = (byte) (Integer.parseInt(strFormatIp_splited[i]));
		}
		return res_byte;
	}

	public static byte[] convertStrFormatMacToByteFormat(String pMacStr) {
		// 0:0:0:0:0:0 -> byte[]
		byte[] byteFormatMacAddr = new byte[6];
		String[] splitedStrFormatMac = pMacStr.split(Pattern.quote(":"));
		for (int i = 0; i < 6; i++) {
			byteFormatMacAddr[i] = (byte) Integer.parseInt(splitedStrFormatMac[i], 16);
		}
		return byteFormatMacAddr;

	}

	public static String convertByteFormatIpToStrFormat(byte[] pIpByte) {
		// byte[] ip addr ->"0.0.0.0"
		String strFormatIpAddr = "";
		for (int i = 0; i < 4; i++) {
			int byteIpToInt;
			if ((int) pIpByte[i] < 0)
				byteIpToInt = ((int) pIpByte[i]) + 256;
			else {
				byteIpToInt = ((int) pIpByte[i]);
			}
			strFormatIpAddr += Integer.toString(byteIpToInt);
			if (i != 3) {
				strFormatIpAddr += ".";
			}
		}
		return strFormatIpAddr;
	}

	public static String convertByteFormatMacToStrFormat(byte[] pMacByte) { // mac �뜝�럩�젧�솻洹⑥삕 �뜝�럥堉뽩뜝�룞�삕 �뛾�룆�뾼占쎈턄�뜝�럥諭� �뛾�룄�ｈ굢占� String �솻洹⑥삕�뜝�럩�꼶
		// byte[] mac addr ->"0:0:0:0:0:0"

		String strFormatMacAddr = "";
		for (int j = 0; j < 6; j++) {
			strFormatMacAddr += Integer.toString((pMacByte[j] & 0xff) + 0x100, 16).substring(1);
			if (j != 5) {
				strFormatMacAddr += ":";
			}
		}
		return strFormatMacAddr;
	}

	public static String convertAddrFormat(byte[] pByteFormatAddr) {
		// byte[] to string
		if (pByteFormatAddr.length == 4)
			// if it's IP address
			return convertByteFormatIpToStrFormat(pByteFormatAddr);
		else// if it's MAC address
			return convertByteFormatMacToStrFormat(pByteFormatAddr);
	}

	public static byte[] convertAddrFormat(String pStrFormatAddr) {
		// String to byte[]
		if (pStrFormatAddr.split(Pattern.quote(".")).length == 4)
			// if it's IP address
			return convertStrFormatIpToByteFormat(pStrFormatAddr);
		else
			// else it's MAC address
			return convertStrFormatMacToByteFormat(pStrFormatAddr);
	}

	public static byte[] convertFileToByte(String pFilePath) {
		byte[] byteTypeFileData = null;
		try {
			byteTypeFileData = Files.readAllBytes(new File(pFilePath).toPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return byteTypeFileData;
	}

	public static void convertByteToFile(String pFileName, String pFilePath, byte[] pData) {
		if (pData != null) {
			if  (pFilePath.equals("pwd")){ 
				pFilePath = System.getProperty("user.dir");
			        System.out.println("[Debug] Output file dir: " + pFilePath);
			}
			try {
				File outputFile = new File(pFilePath +"\\" + pFileName.trim());
				FileOutputStream outputFileStream = new FileOutputStream(outputFile);
				outputFileStream.write(pData);
				outputFileStream.close();

			} catch (Throwable e) {
				e.printStackTrace(System.out);

			}
		}

	}
	
	public static boolean checkIsIpFormatString(String pString) {

		String[] pStringSplited = pString.split(Pattern.quote("."));

		if (pStringSplited.length != 4) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			if (Pattern.matches("^[0-9]*$", pStringSplited[i])) {
				if (Integer.parseInt(pStringSplited[i]) > 255) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean checkIsMacFormatString(String pString) {

		String[] pStringSplited = pString.split(Pattern.quote(":"));

		if (pStringSplited.length != 6) {
			return false;
		}
		for (int i = 0; i < 6; i++) {
			if (Pattern.matches("^[0-9a-zA-Z]*$", pStringSplited[i])) {
				if (pStringSplited[i].length() > 2) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static int getFileLength(String pFilePath) {
	    int size = -1;

		File file = new File(pFilePath);
	    if (file.exists() )
	    {
	        size = (int) file.length();
	    }
	
	    return size;
	}
	
	public static String[] removeElementFromArray(String[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
           return arr;
        }
        String[] removed_arr = new String[arr.length - 1];
        System.arraycopy(arr, 0, removed_arr, 0, index);
        System.arraycopy(arr, index + 1, removed_arr, index, arr.length - index - 1);
        return removed_arr;
     }
	public static byte[] subnetOperation(byte[] pIpAddress, byte[] pSubnetMask){
		//System.out.println("subnet " + Utils.convertAddrFormat(pIpAddress) + " with " + Utils.convertAddrFormat(pSubnetMask));
		byte[] subnetMaskResult = new byte[4];
		for (int i=0; i<subnetMaskResult.length; i++){
			subnetMaskResult[i]= (byte)(pIpAddress[i]&pSubnetMask[i]);
		}
		//System.out.println("subnet result = " + Utils.convertAddrFormat(subnetMaskResult));

        return subnetMaskResult;
	
}
}
