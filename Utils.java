public final class Utils {
	/*
	 * This class implements useful functions that can be used in general for
	 * implementing layer logic.
	 */
	public static boolean compare_address(byte[] address_1, byte[] address_2) {
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

}