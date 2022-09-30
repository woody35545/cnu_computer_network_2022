public final class Utils {
	/*
	 * This class implements useful functions that can be used in general for
	 * implementing layer logic.
	 */
	
	private static final boolean DEBUG_MODE = true; 
	public static boolean compareAddress(byte[] address_1, byte[] address_2) {
		/*
		 * Address comparison function defined in byte array format. 
		 * Available for both mac and ip addresses
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
				System.out.print("[" + i + "]"
						+ String.format("%02X ", packet[i]));
				if ((i + 1) % 16 == 0) {
					System.out.println("");

				}
			}
		}
	}

}