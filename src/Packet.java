public class Packet {
	String id;
	Constants.PacketType packetType;
	int size;
	Host srcHost;
	Host dstHost;

	public Packet(int id, Constants.PacketType packetType, int size, Host srcHost,
			Host dstHost) {
		this.id = srcHost.hostName + " " + id + " " + dstHost.hostName;
		this.packetType = packetType;
		this.size = size;
		this.srcHost = srcHost;
		this.dstHost = dstHost;
	}
}
