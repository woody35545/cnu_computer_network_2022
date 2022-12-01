package Router;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.ByteBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class NILayer implements BaseLayer {
	
	public int nUnderLayerCount = 0;
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_UnderLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	int m_iNumAdapter_port1;
	int m_iNumAdapter_port2;
	public Pcap m_AdapterObject_port1;
	public Pcap m_AdapterObject_port2;
	public PcapIf device;
	public List<PcapIf> m_pAdapterList;
	StringBuilder errbuf = new StringBuilder();

	public NILayer(String pName) {
		// super(pName);
		pLayerName = pName;

		m_pAdapterList = new ArrayList<PcapIf>();
		m_iNumAdapter_port1 = 0;
		m_iNumAdapter_port2 = 0;
		SetAdapterList();
	}

	public void Port1_PacketStartDriver() {
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 2 * 1000; // (timeout/1000) seconds
		m_AdapterObject_port1 = Pcap.openLive(m_pAdapterList.get(m_iNumAdapter_port1).getName(), snaplen, flags, timeout, errbuf);
	}
	public void Port2_PacketStartDriver() {
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 2 * 1000; // (timeout/1000) seconds
		m_AdapterObject_port2 = Pcap.openLive(m_pAdapterList.get(m_iNumAdapter_port2).getName(), snaplen, flags, timeout, errbuf);
	}

	public PcapIf GetAdapterObject(int iIndex) {
		return m_pAdapterList.get(iIndex);
	}

	public void Port1_SetAdapterNumber(int iNum) {
		m_iNumAdapter_port1 = iNum;
		Port1_PacketStartDriver();
		Port1_Receive();
	}
	
	public void Port2_SetAdapterNumber(int iNum) {
		m_iNumAdapter_port2 = iNum;
		Port2_PacketStartDriver();
		Port2_Receive();
	}

	
	public void SetAdapterList() {
		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
	}

	public boolean Port1_Send(byte[] input, int length) {
		ByteBuffer buf = ByteBuffer.wrap(input);

		if (m_AdapterObject_port1.sendPacket(buf) != Pcap.OK) {
			System.err.println(m_AdapterObject_port1.getErr());
			return false;
		}
		System.out.println("Port 1 Send packet");

		return true;
	}
	public boolean Port2_Send(byte[] input, int length) {
		// TODO Auto-generated method stub
		ByteBuffer buf = ByteBuffer.wrap(input);

		if (m_AdapterObject_port2.sendPacket(buf) != Pcap.OK) {
			System.err.println(m_AdapterObject_port2.getErr());
			return false;
		}
		System.out.println("Port 2 Send packet");
		//Utils.showPacket(input);
		return true;
	}
	public boolean Port1_Receive() {
		Port1_Receive_Thread port1_receive_thread = new Port1_Receive_Thread(m_AdapterObject_port1, this.GetUpperLayer(0));
		Thread port1_receive_thread_obj = new Thread(port1_receive_thread);
		port1_receive_thread_obj.start();

		return false;
	}
	
	public boolean Port2_Receive() {
		Port2_Receive_Thread port2_receive_thread = new Port2_Receive_Thread(m_AdapterObject_port2, this.GetUpperLayer(0));
		Thread port2_receive_thread_obj = new Thread(port2_receive_thread);
		port2_receive_thread_obj.start();
		return false;

	}


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




}

class Port1_Receive_Thread implements Runnable {
	byte[] data;
	Pcap AdapterObject;
	BaseLayer UpperLayer;

	public Port1_Receive_Thread(Pcap m_AdapterObject, BaseLayer m_UpperLayer) {
		// TODO Auto-generated constructor stub
		AdapterObject = m_AdapterObject;
		UpperLayer = m_UpperLayer;
	}

	@Override
	public void run() {	
		while (true) {
			PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
				public void nextPacket(PcapPacket packet, String user) {
					//System.out.printf("Capture >> Time: %s, Length: %d\n", new Date(packet.getCaptureHeader().timestampInMillis()), packet.getCaptureHeader().caplen());
					data = packet.getByteArray(0, packet.size());
					UpperLayer.Receive(data);
				//	System.out.println("Port 1 Received");
				}
			};

			//AdapterObject.loop(100000, jpacketHandler, "");
			AdapterObject.loop(1000, jpacketHandler, "");

		}
	}
	
	
}
class Port2_Receive_Thread implements Runnable {
	byte[] data;
	Pcap AdapterObject;
	BaseLayer UpperLayer;

	public Port2_Receive_Thread(Pcap m_AdapterObject, BaseLayer m_UpperLayer) {
		// TODO Auto-generated constructor stub
		AdapterObject = m_AdapterObject;
		UpperLayer = m_UpperLayer;
	}

	@Override
	public void run() {	
		while (true) {
			PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
				public void nextPacket(PcapPacket packet, String user) {
					//System.out.printf("Capture >> Time: %s, Length: %d\n", new Date(packet.getCaptureHeader().timestampInMillis()), packet.getCaptureHeader().caplen());
					data = packet.getByteArray(0, packet.size());
					UpperLayer.Receive(data);
					//System.out.println("Port 2 Received");

				}
			};
			AdapterObject.loop(1000, jpacketHandler, "");

			//AdapterObject.loop(100000, jpacketHandler, "");
		}
	}	
}
