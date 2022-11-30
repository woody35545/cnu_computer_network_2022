package Router;

public final class RoutingTable {
	private final static int ROUTING_TABLE_CAPACITY = 30;
	private static int size = 0;
	private static String[] Destination = new String[ROUTING_TABLE_CAPACITY];
	private static String[] NetMask = new String[ROUTING_TABLE_CAPACITY];
	private static String[] Gateway = new String[ROUTING_TABLE_CAPACITY];
	private static String[] Flag = new String[ROUTING_TABLE_CAPACITY];
	private static String[] Interface = new String[ROUTING_TABLE_CAPACITY];
	private static String[] Metric = new String[ROUTING_TABLE_CAPACITY];

	public static boolean addElement(String pDestination, String pNetMask, String pGateway, String pFlag,
			String pInterface, String pMetric) {
		if (isExist(pDestination)) {
			updateElement(pDestination, pNetMask, pGateway, pFlag, pInterface, pMetric);
			return true;
		}
		if (size < ROUTING_TABLE_CAPACITY && !isExist(pDestination)) {
			Destination[size] = pDestination;
			NetMask[size] = pNetMask;
			Gateway[size] = pGateway;
			Flag[size] = pFlag;
			Interface[size] = pInterface;
			Metric[size] = pMetric;

			size++;
			return true;
		}
		return false;
	}

	public static boolean updateElement(String pDestination, String pNetMask, String pGateway, String pFlag,
			String pInterface, String pMetric) {
		if (!isExist(pDestination)) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (Destination[i].equals(pDestination)) {
				NetMask[size] = pNetMask;
				Gateway[size] = pGateway;
				Flag[size] = pFlag;
				Interface[size] = pInterface;
				Metric[size] = pMetric;
				return true;
			}
		}
		return false;
	}

	public static boolean deleteElement(String pDestination) {
		if (isExist(pDestination)) {
			int idx = 0;
			for (int i = 0; i < size; i++) {
				if (Destination[i].equals(pDestination)) {
					idx = i;
				}
			}
			Destination = Utils.removeElementFromArray(Destination, idx);
			NetMask = Utils.removeElementFromArray(NetMask, idx);
			Gateway = Utils.removeElementFromArray(Gateway, idx);
			Flag = Utils.removeElementFromArray(Flag, idx);
			Interface = Utils.removeElementFromArray(Interface, idx);
			Metric = Utils.removeElementFromArray(Metric, idx);

			size--;
			return true;
		}
		return false;
	}

	public static boolean isExist(String pDestination) {
		for (int i = 0; i < size; i++) {
			if (Destination[i].equals(pDestination)) {
				return true;
			}
		}
		return false;
	}

	public static void reset() {
		size = 0;
		Destination = new String[ROUTING_TABLE_CAPACITY];
		NetMask = new String[ROUTING_TABLE_CAPACITY];
		Gateway = new String[ROUTING_TABLE_CAPACITY];
		Flag = new String[ROUTING_TABLE_CAPACITY];
		Interface = new String[ROUTING_TABLE_CAPACITY];
		Metric = new String[ROUTING_TABLE_CAPACITY];
	}

	public static void showTable() {
		System.out.println("[ ROUTING TABLE ] - (size: " + size + ")");
		for (int i = 0; i < size; i++) {
			System.out.print(Destination[i] + " | ");
			System.out.print(NetMask[i] + " | ");
			System.out.print(Gateway[i] + " | ");
			System.out.print(Flag[i] + " | ");
			System.out.print(Interface[i] + " | ");
			System.out.println(Metric[i]);

		}
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("");
	}

}
