package Router;

public final class ARPCacheTable {
	/** fields for ARP Cache Table **/
	
	// max size of ARP cache table
	private final static int ARP_CACHE_TABLE_CAPACITY = 30;
	// field for current size of ARP cache table
	private static int size = 0;
	// field for ip address of entries
    private static String[] ipAddress = new String[ARP_CACHE_TABLE_CAPACITY];
	// field for mac address of entries
    private static String[] macAddress = new String[ARP_CACHE_TABLE_CAPACITY];
	// field for arp state of entries. (Complete or Incomplete)
    private static String[] state = new String[ARP_CACHE_TABLE_CAPACITY];
	
    public static boolean isExist(String pIpAddr) {
        for (int i = 0; i < size; i++) {
           if (ipAddress[i].equals(pIpAddr)) {
              return true;
           }
        }
        return false;
     }
    
    public static boolean addElement(String pIpAddress, String pMacAddress, String pState) {
        if (isExist(pIpAddress)) {
        	updateElement(pIpAddress, pMacAddress, pState);
           return true;
        }
        if (size < ARP_CACHE_TABLE_CAPACITY && !isExist(pIpAddress)) {
        	ipAddress[size] = pIpAddress;
        	macAddress[size] = pMacAddress;
           state[size] = pState;
           size++;
           return true;
        }
        return false;
     }
    
    public boolean addElement(String pIpAddr) {
        if (size < ARP_CACHE_TABLE_CAPACITY && !isExist(pIpAddr)) {
        	ipAddress[size] = pIpAddr;
        	macAddress[size] = "??:??:??:??:??:??";
           state[size] = "incomplete";
           size++;
           return true;
        }
        return false;
     }
    
    public static boolean updateElement(String pIpAddress, String pMacAddress, String pState) {
        if (!isExist(pIpAddress)) {
           return false;
        }
        for (int i = 0; i < size; i++) {
           if (ipAddress[i].equals(pIpAddress)) {
        	   macAddress[i] = pMacAddress;
              state[i] = pState;
              return true;
           }
        }
        return false;
     }
    
    public static boolean deleteElement(String pIpAddress) {
        if (isExist(pIpAddress)) {
           int idx = 0;
           for (int i = 0; i < size; i++) {
              if (ipAddress[i].equals(pIpAddress)) {
                 idx = i;
              }
           }
           ipAddress = Utils.removeElementFromArray(ipAddress, idx);
           macAddress = Utils.removeElementFromArray(macAddress, idx);
           state = Utils.removeElementFromArray(state, idx);
           size--;

           return true;
        }
        return false;
     }
    
    public static void reset() {
        size = 0;
        ipAddress = new String[ARP_CACHE_TABLE_CAPACITY];
        macAddress = new String[ARP_CACHE_TABLE_CAPACITY];
        state = new String[ARP_CACHE_TABLE_CAPACITY];
     }
    
    public static void showArpTable() {
        System.out.println("[ ARP CACHE TABLE ] - (current size: " + size + ")");
        for (int i = 0; i < size; i++) {
           System.out.print(ipAddress[i] + " | ");
           System.out.print(macAddress[i] + " | ");
           System.out.println(state[i]);
        }
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("");
}
    
}
