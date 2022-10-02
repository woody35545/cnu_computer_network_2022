import java.util.regex.Pattern;

public final class Utils {
	/*
	 * This class implements useful functions that can be used in general for
	 * implementing layer logic.
	 */

	private static final boolean DEBUG_MODE = true;

	public static boolean compareAddress(byte[] address_1, byte[] address_2) {
		/*
		 * Address comparison function defined in byte array format. Available for both
		 * mac and ip addresses
		 */
		for (int i = 0; i < address_1.length; i++) {
			if (((byte) address_1[i]) != ((byte) address_2[i])) {
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
			byteFormatMacAddr[i] = (byte)(Integer.parseInt(splitedStrFormatMac[i]));
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

	public static String convertByteFormatMacToStrFormat(byte[] pMacByte) { // mac 정보 담은 바이트 배열 String 변환
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



}
