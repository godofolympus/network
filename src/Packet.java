/**
 * Generic class used to represent a Packet in the network. Objects of
 * this type are passed between Components. 
 */
public class Packet {
	int id;
	int size;
	Host srcHost;
	Host dstHost;
	String flowName;
	Constants.PacketType packetType;

	// 
	int negPacketId = -1;

	// Variables used to calculate RTT and congestion delay
	double dataSendingTime;
	double linkArrivalTime;

	public Packet(int id, Constants.PacketType packetType, int size,
			Host srcHost, Host dstHost, String flowName) {
		this.id = id;
		this.packetType = packetType;
		this.size = size;
		this.srcHost = srcHost;
		this.dstHost = dstHost;
		this.flowName = flowName;
	}
}
