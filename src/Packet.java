public class Packet {
	int id;
	Constants.PacketType packetType;
	int size;
	Host srcHost;
	Host dstHost;
	String flowName;
	int negPacketId = -1;
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
