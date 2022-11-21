package StaticRouter;

import java.util.Arrays;

public final class RoutingTableManager {
	public static _ROUTING_TABLE routingTable = new _ROUTING_TABLE();

	// Destination
	public static class _ROUTING_TABLE {

		private final static int Capacity = 30;
		private int size = 0;
		private String[] Destination = new String[Capacity];
		private String[] NetMask = new String[Capacity];
		private String[] Gateway = new String[Capacity];
		private String[] Flag = new String[Capacity];
		private String[] Interface = new String[Capacity];
		private String[] Metric = new String[Capacity];

		public _ROUTING_TABLE() {
		}

		public boolean addElement(String pDestination, String pNetMask, String pGateway, String pFlag,
				String pInterface, String pMetric) {
			if (this.isExist(pDestination)) {
				this.updateElement(pDestination, pNetMask, pGateway, pFlag, pInterface, pMetric);
				return true;
			}
			if (this.size < this.Capacity && !this.isExist(pDestination)) {
				Destination[size] = pDestination;
				NetMask[size] = pNetMask;
				Gateway[size] = pGateway;
				Flag[size] = pFlag;
				Interface[size] = pInterface;
				Metric[size] = pMetric;

				size++;
				this.showTable();
				return true;
			}
			return false;
		}

		private int getIndexOf(String pDestination) {
			if (this.isExist(pDestination))
				for (int i = 0; i < size; i++) {
					if (this.Destination[i].equals(pDestination))
						return i;
				}
			return -1;
		}

		public boolean updateElement(String pDestination, String pNetMask, String pGateway, String pFlag,
				String pInterface, String pMetric) {
			if (!this.isExist(pDestination)) {
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

		public boolean deleteElement(String pDestination) {
			if (this.isExist(pDestination)) {
				int idx = 0;
				for (int i = 0; i < this.size; i++) {
					if (this.Destination[i].equals(pDestination)) {
						idx = i;
					}
				}
				this.Destination = this.removeElementFromArray(this.Destination, idx);
				this.NetMask = this.removeElementFromArray(this.NetMask, idx);
				this.Gateway = this.removeElementFromArray(this.Gateway, idx);
				this.Flag = this.removeElementFromArray(this.Flag, idx);
				this.Interface = this.removeElementFromArray(this.Interface, idx);
				this.Metric = this.removeElementFromArray(this.Metric, idx);

				this.size--;
				return true;
			}
			return false;
		}

		public void resetTable() {
			this.size = 0;

			this.Destination = new String[Capacity];
			this.NetMask = new String[Capacity];
			this.Gateway = new String[Capacity];
			this.Flag = new String[Capacity];
			this.Interface = new String[Capacity];
			this.Metric = new String[Capacity];

		}

		public boolean isExist(String pDestination) {
			for (int i = 0; i < this.size; i++) {
				if (this.Destination[i].equals(pDestination)) {
					return true;
				}
			}
			return false;
		}
		
		public byte[] getSubnet(byte[] pIpByte) {
			byte[] tmp = {0,0,0,0};
			byte[] drop = {-1,-1,-1,-1};
			for(int i =0; i< this.size;i++) {
				for(int j = 0; j<4; j++) {
					// xor netmask&ipAddr
					tmp[j] = (byte)((Utils.convertAddrFormat(this.NetMask[i])[j])^pIpByte[j]);
				}
				if(Arrays.equals(tmp, Utils.convertAddrFormat(this.Destination[i])) ) {
					return Utils.convertAddrFormat(this.Destination[i]);
				}
			}
			return drop;
		}

		private String[] removeElementFromArray(String[] arr, int index) {
			if (arr == null || index < 0 || index >= arr.length) {
				return arr;
			}
			String[] removed_arr = new String[arr.length - 1];
			System.arraycopy(arr, 0, removed_arr, 0, index);
			System.arraycopy(arr, index + 1, removed_arr, index, arr.length - index - 1);
			return removed_arr;
		}

		public void showTable() {
			System.out.println("[ ROUTING TABLE ] - (size: " + this.size + ")");
			for (int i = 0; i < this.size; i++) {
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

	public static void refreshTableGUI() {
		for (int i = 0; i < routingTable.size; i++) {
			RoutingTableGUI.initTableValue(new String[] { Integer.toString(i), routingTable.Destination[i],
					routingTable.NetMask[i], routingTable.Gateway[i], routingTable.Flag[i], routingTable.Interface[i],
					routingTable.Metric[i] });
		}
	}

	public static void addElementAndRefresh(String pDestination, String pNetMask, String pGateway, String pFlag,
			String pInterface, String pMetric) {
		routingTable.addElement(pDestination, pNetMask, pGateway, pFlag, pInterface, pMetric);
		refreshTableGUI();
	}

	public static void deleteTableElement(String pDestination) {
		RoutingTableGUI.resetTableGui();
		routingTable.deleteElement(pDestination);
		refreshTableGUI();
	}

	public static _ROUTING_TABLE getRoutingTable() {
		routingTable.showTable();
		return routingTable;
	}

}
