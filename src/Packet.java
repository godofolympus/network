public class Packet {
	String id;
	Constants.PacketType packetType;
	int size;
	Host srcHost;
	Host dstHost;
	String flowName;

	public Packet(String id, Constants.PacketType packetType, int size,
			Host srcHost, Host dstHost, String flowName) {
		this.id = id;
		this.packetType = packetType;
		this.size = size;
		this.srcHost = srcHost;
		this.dstHost = dstHost;
		this.flowName = flowName;
	}
}
