package Router;

import java.util.Arrays;

import Router.Utils;

public final class RoutingTable {
	public final static int ROUTING_TABLE_CAPACITY = 20;
	public static int size = 0;
	public static String[] Destination = new String[ROUTING_TABLE_CAPACITY];
	public static String[] NetMask = new String[ROUTING_TABLE_CAPACITY];
	public static String[] Gateway = new String[ROUTING_TABLE_CAPACITY];
	public static String[] Flag = new String[ROUTING_TABLE_CAPACITY];
	public static String[] Interface = new String[ROUTING_TABLE_CAPACITY];
	public static String[] Metric = new String[ROUTING_TABLE_CAPACITY];

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
			
			RouterMainFrame.refreshRoutingTableGUI();

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
				RouterMainFrame.refreshRoutingTableGUI();

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
		
			RouterMainFrame.refreshRoutingTableGUI();

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
		RouterMainFrame.refreshRoutingTableGUI();

	}
	
	public static byte[] getSubnet(byte[] pIpByte) {
		System.out.println("with IP: "+Utils.convertAddrFormat(pIpByte)+" Subnet Operation start");
		byte[] tmp = new byte[4];
		byte[] drop = {-1,-1,-1,-1};
		for(int i =0; i< size;i++) {
			for(int j = 0; j<4; j++) {
				tmp[j] = (byte)((Utils.convertAddrFormat(NetMask[i])[j])&pIpByte[j]);
			}
			if((Arrays.equals(tmp, Utils.convertAddrFormat(Destination[i])) && Flag[i].contains("U")))  {
				if(Flag[i].contains("G")) {
					// if flag has 'G', return gateway's ip address
					return Utils.convertAddrFormat(Gateway[i]);
				}else {
					// if flag doesn't has 'G', return real destination's ip address
					System.out.println("Find match entry: "+Utils.convertAddrFormat(pIpByte));

					return pIpByte;
				}
			}
		}
		return drop;
	}
	
	 public static String[] findMatchEntry(byte[] pIpByte){
		 
		 /* Return -> String[]
		  *
		  */
		 String [] res = new String[2]; // res[0]: Match Destination IP, res[1]: Match Destination Interface
		 
		 for (int i=0; i<size; i++){
			 String subnetResult =Utils.convertAddrFormat(Utils.subnetOperation(pIpByte, Utils.convertAddrFormat(NetMask[i])));
			 //System.out.println("Destination["+i+"]:" + Destination[i]);
			 //System.out.println("SubnetResult["+i+"]:" + subnetResult );
			 
			 if(Destination[i].equals(subnetResult)&& Flag[i].contains("U")){
			 if(Flag[i].contains("G")){
				 	// If Gateway flag is on, Return Gateway IP Address as Match IP Address
				 	//System.out.println("Entry found: " + Gateway[i]);
				 		res[0] = Gateway[i];
				 		res[1] = Interface[i];
 					 return res;
				 }else{
					 // If only Up flag is on, Return Destination IP address
					 res[0] = Destination[i];
				 		res[1] = Interface[i];
					 //System.out.println("Entry found: " + Destination[i]);
					 return res; 	 
				 }
			
			 }
			 }
		 //System.out.println("Entry not found :(");

		 return new String[]{"-1","-1"};
	 }
	 public static String getInterface(String pIpAddr) {
	        if (isExist(pIpAddr)) {
	           for (int i = 0; i < size; i++) {
	              if (Destination[i].equals(pIpAddr))
						 System.out.println("Match Interface: " + Interface[i]);

	                 return Interface[i];
	           }
	        }
	        return null;
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
