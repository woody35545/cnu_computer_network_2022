package Router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class ARPLayer implements BaseLayer {
   private static final byte[] UNKNOWN_DESTINATION_MAC_ADDR = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
         (byte) 0x00, (byte) 0x00, (byte) 0x00 };
   private static final byte[] DESTINATION_MAC_ZERO_PADDING = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
         (byte) 0x00, (byte) 0x00, (byte) 0x00 };
   private static final byte[] OPCODE_ARP_REQUEST = new byte[] { 0x00, 0x01 };
   private static final byte[] OPCODE_ARP_REPLY = new byte[] { 0x00, 0x02 };

   // Default hardware type = Ethernet(0x0001)
   private static final byte[] DEFAULT_HARDWARE_TYPE = new byte[] { 0x00, 0x01 };
   // Default protocol type = IPv4(0x0800)
   private static final byte[] DEFAULT_PROTOCOL_TYPE = new byte[] { 0x08, 0x00 };
   // Default length of hardware address = 0x06 (MAC Address)
   private static final byte DEFAULT_LENGTH_OF_HARDWARE_ADDRESS = (byte) 0x06;
   // Default length of protocol address = 0x04 (IP Address)
   private static final byte DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS = (byte) 0x04;
   public int nUpperLayerCount = 0;
   public int nUnderLayerCount = 0;
   public String pLayerName = null;
   public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
   public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
   private _ARP_HEADER m_sHeader = new _ARP_HEADER();
//   public _ARP_CACHE_TABLE arpCacheTable = new _ARP_CACHE_TABLE();
//   private _PROXY_CACHE_TABLE proxyCacheTable = new _PROXY_CACHE_TABLE();

   public ARPLayer(String pName) {
      // ARPLayer class constructor
      pLayerName = pName;
   }

   private class _ARP_HEADER {
      private int length_of_header = 28;
      /*
       * Data structure for representing ARP Message Format
       * 
       * ARP Header Structure Specification (in order) # [Hardware Type] - 2 Bytes #
       * [Protocol Type] - 2 Bytes # [Length of hardware address] - 1 Byte # [Length
       * of Protocol address] - 1 Byte # [Opcode] - 2 Bytes # [Sender hardware
       * address] - 6 Bytes # [Sender protocol address] - 4 Bytes # [Target hardware
       * address] - 6 Bytes # [Target protocol address] - 4 Bytes
       */
      byte[] hardwareType = new byte[2];
      byte[] protocolType = new byte[2];
      byte lengthOfHardwareAddr;
      byte lengthOfProtocolAddr;
      byte[] opCode = new byte[2];
      _MAC_ADDR senderMac = new _MAC_ADDR();
      _MAC_ADDR targetMac = new _MAC_ADDR();
      _IP_ADDR senderIp = new _IP_ADDR();
      _IP_ADDR targetIp = new _IP_ADDR();

      public _ARP_HEADER() {
         // _ARP_HEADER constructor
      }
      @SuppressWarnings("unused")
      public _ARP_HEADER(byte[] pHardwareType, byte[] pProtocolType, byte pLengthOfMacAddr,
            byte pLengthOfProtocolAddr, byte[] pOpcode, _MAC_ADDR pSenderMacAddress, _IP_ADDR pSenderIpAddress,
            _MAC_ADDR pTargetMacAddress, _IP_ADDR pTargetIpAddress) {
         // _ARP_HEADER constructor with Parameters
         this.hardwareType = pHardwareType;
         this.protocolType = pProtocolType;
         this.lengthOfHardwareAddr = pLengthOfMacAddr;
         this.lengthOfProtocolAddr = pLengthOfProtocolAddr;
         this.opCode = pOpcode;
         this.senderMac = pSenderMacAddress;
         this.targetMac = pTargetMacAddress;
         this.senderIp = pSenderIpAddress;
         this.targetIp = pTargetIpAddress;
      }

      public void SetHardwareType(byte[] pHardwareType) {
         this.hardwareType = pHardwareType;
      }

      public void SetProtocolType(byte[] pProtocolType) {
         this.protocolType = pProtocolType;
      }

      public void SetLengthOfHardwareAddress(byte _pLengthOfHardwareAddress) {
         this.lengthOfHardwareAddr = _pLengthOfHardwareAddress;
      }

      public void SetLengthOfProtocolAddress(byte pLengthOfProtocolAddress) {
         this.lengthOfProtocolAddr = pLengthOfProtocolAddress;
      }

      public void SetOpCode(byte[] pOpCode) {
         this.opCode = pOpCode;
      }

      public void SetSenderMacAddress(byte[] pSenderMacAddress) {
         this.senderMac.addr = pSenderMacAddress;
      }

      public void SetTargetMacAddress(byte[] pTargetMacAddress) {
         this.targetMac.addr = pTargetMacAddress;
      }

      public void SetSenderIPAddress(byte[] pSenderIPAddress) {
         this.senderIp.addr = pSenderIPAddress;
      }

      public void SetTargetIPAddress(byte[] pTargetIPAddress) {
         this.targetIp.addr = pTargetIPAddress;
      }

      public int get_length_of_header() {
         return this.length_of_header;
      }
   }

   
   private class _IP_ADDR {
      /*
       * Data structure for representing IP Address, Will be used for ARP Packet
       * structure. 4 bytes are required to represent the IP Address.
       */
      private byte[] addr = new byte[4];
      private int lengthOfAddr = 4;

      // Initialize values  뗢 땤o 0x00
      public _IP_ADDR() {
         for (int i = 0; i < this.lengthOfAddr; i++) {
            this.addr[i] = (byte) 0x00;
         }
      }

      public _IP_ADDR(byte[] pAddr) {
         for (int i = 0; i < this.lengthOfAddr; i++) {
            this.addr[i] = pAddr[i];
         }
      }

      public byte[] getAddrByte() {
         return this.addr;
      }

      public String getAddrStr() {
         String addr_str = (addr[0] & 0xFF) + "." + (addr[1] & 0xFF) + "." + (addr[2] & 0xFF) + "."
               + (addr[3] & 0xFF);
         return addr_str;
      }

      public int getLengthOfAddr() {
         return this.lengthOfAddr;
      }
   }

   private class _MAC_ADDR {
      /*
       * Data structure for representing Mac Address, Will be used for ARP Packet
       * structure. 8 bytes are required to represent the MAC Address.
       */
      private byte[] addr = new byte[6];
      private int lengthOfAddr = 6;

      // Initialize values  뗢 땤o 0x00
      public _MAC_ADDR() {
         for (int i = 0; i < this.lengthOfAddr; i++) {
            this.addr[i] = (byte) 0x00;
         }
      }

      public _MAC_ADDR(byte[] pAddr) {
         for (int i = 0; i < this.lengthOfAddr; i++) {
            this.addr[i] = pAddr[i];
         }
      }

      public byte[] getAddrByte() {
         return this.addr;
      }

      public String getAddrStr() {
         String addr_str = (addr[0] & 0xFF) + ":" + (addr[1] & 0xFF) + ":" + (addr[2] & 0xFF) + ":"
               + (addr[3] & 0xFF) + ":" + (addr[4] & 0xFF) + ":" + (addr[5] & 0xFF);
         return addr_str;
      }

      public int getLengthOfAddr() {
         return this.lengthOfAddr;
      }
   }

   public byte[] Encapsulate(_ARP_HEADER pHeader) {
      // <!> Need to check
      // Encapsulate function to create byte array type ARP Packet
      int idx_ptr = 0;
      byte[] encapsulated = new byte[pHeader.get_length_of_header()];
      for (int i = 0; i < pHeader.hardwareType.length; i++) {
         encapsulated[idx_ptr++] = pHeader.hardwareType[i];
      }
      for (int i = 0; i < pHeader.protocolType.length; i++) {
         encapsulated[idx_ptr++] = pHeader.protocolType[i];
      }
      encapsulated[idx_ptr++] = pHeader.lengthOfHardwareAddr;
      encapsulated[idx_ptr++] = pHeader.lengthOfProtocolAddr;
      for (int i = 0; i < pHeader.opCode.length; i++) {
         encapsulated[idx_ptr++] = pHeader.opCode[i];
      }
      for (int i = 0; i < pHeader.senderMac.getLengthOfAddr(); i++) {
         encapsulated[idx_ptr++] = pHeader.senderMac.addr[i];
      }
      for (int i = 0; i < pHeader.senderIp.getLengthOfAddr(); i++) {
         encapsulated[idx_ptr++] = pHeader.senderIp.addr[i];
      }
      for (int i = 0; i < pHeader.targetMac.getLengthOfAddr(); i++) {
         encapsulated[idx_ptr++] = pHeader.targetMac.addr[i];
      }
      for (int i = 0; i < pHeader.targetIp.getLengthOfAddr(); i++) {
         encapsulated[idx_ptr++] = pHeader.targetIp.addr[i];
      }
      return encapsulated;
   }

   public byte[] Decapsulate(byte[] pARPPacket) {
      // <!> Need to check
      int offset = 28;
      byte[] decapsulated = new byte[pARPPacket.length - 28];
      for (int i = 0; i < decapsulated.length; i++) {
         decapsulated[i] = pARPPacket[offset + i];
      }
      return decapsulated;
   }

   public _ARP_HEADER MakeARPRequestHeader() {
      _ARP_HEADER arpHeader = new _ARP_HEADER(DEFAULT_HARDWARE_TYPE, DEFAULT_PROTOCOL_TYPE,
            DEFAULT_LENGTH_OF_HARDWARE_ADDRESS, DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS, OPCODE_ARP_REQUEST,
            this.m_sHeader.senderMac, this.m_sHeader.senderIp, new _MAC_ADDR(UNKNOWN_DESTINATION_MAC_ADDR),
            this.m_sHeader.targetIp);
      return arpHeader;
   }

   public boolean Send() {
      /*
       * If there is no Mac address for the destination IP in the ARP cache table,
       * then Send ARP Request
       */
      _ARP_HEADER ARPRequestHeader = this.MakeARPRequestHeader();
      byte[] encapsulated = this.Encapsulate(ARPRequestHeader);
      ARPCacheTable.addElement(Utils.convertAddrFormat(ARPRequestHeader.targetIp.addr));
//      this.addARPCacheTableElement(Utils.convertByteFormatIpToStrFormat(ARPRequestHeader.targetIp.addr));
      this.GetUnderLayer(0).Port1_Send(encapsulated, encapsulated.length);
      System.out.println(encapsulated);
      return true;
   }



   public void setARPHeaderSrcIp(byte[] pSrcIP) {
      this.m_sHeader.senderIp.addr = pSrcIP;
   }

   public void setARPHeaderDstIp(byte[] pTargetIP) {
      this.m_sHeader.targetIp.addr = pTargetIP;
   }

   public void setARPHeaderSrcMac(byte[] pSrcMac) {
      this.m_sHeader.senderMac.addr = pSrcMac;
   }

   public void setARPHeaderDstMac(byte[] pTargetMac) {
      this.m_sHeader.targetMac.addr = pTargetMac;
   }

   public void setARPHeaderOpcode(byte[] pOpcode) {
      this.m_sHeader.opCode = pOpcode;
   }

//   public void resetPROXYCacheTableGUI() {
//      StaticRouterGUI.resetProxyTable();
//   }
//
//   public void refreshPROXYCacheTableGUI() {
//      for (int i = 0; i < this.proxyCacheTable.size; i++) {
//         StaticRouterGUI.initProxyTableValue(new String[] { Integer.toString(i), this.proxyCacheTable.deviceName[i],
//               this.proxyCacheTable.ipAddr[i], this.proxyCacheTable.macAddr[i] });
//      }
//   }
//
//   public void addPROXYCacheTableElement(String pDeviceName, String pIpAddr, String pMacAddr) {
//      this.proxyCacheTable.addProxyCacheTableElement(pDeviceName, pIpAddr, pMacAddr);
//      this.refreshPROXYCacheTableGUI();
//   }
//
//   public void addPROXYCacheTableElement(String pIpAddr) {
//      this.proxyCacheTable.addProxyCacheTableElement(pIpAddr);
//      this.refreshPROXYCacheTableGUI();
//   }
//
//   public void deletePROXYCacheTableElement(String pIpAddr) {
//      // this.resetPROXYCacheTableGUI();
//      this.proxyCacheTable.deleteProxyCacheTable(pIpAddr);
//      this.refreshPROXYCacheTableGUI();
//   }
//
//   public void deleteAllPROXYCacheTableElement() {
//      this.proxyCacheTable.resetProxyCacheTable();
//      this.resetPROXYCacheTableGUI();
//   }

   public boolean Receive(byte[] input) {
      byte[] receivedSenderMAC = Arrays.copyOfRange(input, 8, 14);
      byte[] receivedSenderIP = Arrays.copyOfRange(input, 14, 18);
      byte[] receivedTargetMAC = Arrays.copyOfRange(input, 18, 24);
      byte[] receivedTargetIP = Arrays.copyOfRange(input, 24, 28);
      byte[] receivedOpcode = new byte[] { (byte) input[6], (byte) input[7] };

      if (Utils.compareBytes(receivedOpcode, OPCODE_ARP_REQUEST)) {
         // If I received ARP Request type packet.

         // Check if i am the target
         if (Utils.compareBytes(receivedTargetIP, this.m_sHeader.senderIp.addr)) {
            // If it's Reqeust to me, Reply my MAC Address ( ARP REPLY )
            _ARP_HEADER arpReplyHeader = new _ARP_HEADER(DEFAULT_HARDWARE_TYPE, DEFAULT_PROTOCOL_TYPE,
                  DEFAULT_LENGTH_OF_HARDWARE_ADDRESS, DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS, OPCODE_ARP_REQUEST,
                  this.m_sHeader.senderMac, new _IP_ADDR(receivedTargetIP), new _MAC_ADDR(receivedSenderMAC),
                  new _IP_ADDR(receivedSenderIP));

            // Make Header to byte type packet
            byte[] replyPacket = this.Encapsulate(arpReplyHeader);

            // send Reply Packet to ethernet
            // this.GetUnderLayer(0).Send(replyPacket, replyPacket.length);
         }

         // If I'm not target of this request packet, Searching my proxy table (Proxy
         // ARP)
//         else if (proxyCacheTable.isExist(Utils.convertAddrFormat(receivedTargetIP))) {
//            // if target IP exists in my proxy table
//            // Create a reply packet by adding my MAC address instead
//            _ARP_HEADER arpReplyHeader = new _ARP_HEADER(DEFAULT_HARDWARE_TYPE, DEFAULT_PROTOCOL_TYPE,
//                  DEFAULT_LENGTH_OF_HARDWARE_ADDRESS, DEFAULT_LENGTH_OF_PROTOCOL_ADDRESS, OPCODE_ARP_REPLY,
//                  this.m_sHeader.senderMac, new _IP_ADDR(Arrays.copyOfRange(input, 24, 28)),
//                  new _MAC_ADDR(Arrays.copyOfRange(input, 8, 14)),
//                  new _IP_ADDR(Arrays.copyOfRange(input, 14, 18)));
//
//            // make packet(byte type)
//            byte[] replyPacket = this.Encapsulate(arpReplyHeader);
//
//            // send Proxy-Reply to ethernet
//            this.GetUnderLayer(0).Send(replyPacket, replyPacket.length);
//         }
         return true;
      }

      else if (Utils.compareBytes(receivedOpcode, OPCODE_ARP_REPLY)) {
         // If I Received ARP reply packet, Then Update ARP cache table
    	 ARPCacheTable.addElement(Utils.convertAddrFormat(receivedSenderIP),Utils.convertAddrFormat(receivedSenderMAC),"Complete");
//         this.addARPCacheTableElement(Utils.convertAddrFormat(receivedSenderIP),
//               Utils.convertAddrFormat(receivedSenderMAC), "Complete");
	       if(this.getRouterGUI().NODE_TYPE == "ROUTER"){
//        	 ((IPLayer) this.GetUpperLayer(0)).setIpHeaderSrcIPAddr(Utils.convertAddrFormat(this.getRouterGUI().IP_ADDR_1));
//        	 ((EthernetLayer)this.GetUnderLayer(0)).setEthernetHeaderSrcMacAddr(Utils.convertAddrFormat(this.getRouterGUI().MAC_ADDR_1));
//        	 ((EthernetLayer)this.GetUnderLayer(0)).setEthernetHeaderDstMacAddr(receivedSenderMAC);
//        	 System.out.println("Routing to host: " + Utils.convertAddrFormat(receivedSenderMAC));
//        	((IPLayer) this.GetUpperLayer(0)).Send(new byte[] {(byte)0x00,(byte)0x00}, 2);
         }
         return true;
      }
      return false;
   }

//   public String getMacAddr(byte[] ip_dstaddr){
//      String mac_addr = this.arpCacheTable.getMacAddr(Utils.convertAddrFormat(ip_dstaddr));      //ARP 캐시테이블에 해당 IP의 맥 주소가 있는지 찾기
//      if (mac_addr.equals("IsNotExist")){   //없을 경우
//         // Router의 IP에 대해서 ARP Request 전송
//         
//         setARPHeaderDstIp(ip_dstaddr);
//         //setARPHeaderSrcIp();
//         
//         this.Send();      //send ARP Request
//         
//         //thread
//         
//         
//      }
//      return mac_addr;
//   }
   
//   public _ARP_CACHE_TABLE getArpCacheTable(){
//	   return this.arpCacheTable;
//   }
   
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
   
   public RouterGUI getRouterGUI() {
	   return ((RouterGUI)this.GetUpperLayer(1));
   }
}
