package Router;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ARPCacheTable.showArpTable();
		
		ARPCacheTable.addElement("192.168.1.1", "AA:BB", "Complete");
		ARPCacheTable.addElement("192.168.1.2", "AA:BB", "Complete");
		ARPCacheTable.addElement("192.168.1.3", "AA:BB", "Complete");
		ARPCacheTable.showArpTable();

		ARPCacheTable.deleteElement("192.168.1.2");

		ARPCacheTable.showArpTable();
		ARPCacheTable.addElement("192.168.1.3", "AA:BB:CC", "Complete");

		ARPCacheTable.showArpTable();
		
		RouterMainFrame _router_main_frame = new RouterMainFrame();

	}

}
