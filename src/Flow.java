import java.util.HashMap;

public class Flow {
	String flowName;
	Host srcHost;
	Host dstHost;
	double dataAmount;
	double startTime;
	double rtt = 0.02;

	int windowSize;
	int totalPackets;
	int currentPackets = 0;
	int maxPacketId = 0;
	int minUnacknowledgedPacketSender = 0;
	int minUnacknowledgedPacketReceiver = 0;
	// TODO: sendingBuffer and receivingBuffer should be moved into host
	HashMap<Integer, Packet> sendingBuffer = new HashMap<Integer, Packet>();
	HashMap<Integer, Double> sendingTimes = new HashMap<Integer, Double>();
	HashMap<Integer, Packet> receivingBuffer = new HashMap<Integer, Packet>();

	public Flow(String name, Host src, Host dest, double amt, double time) {
		this.flowName = name;
		this.srcHost = src;
		this.dstHost = dest;
		this.dataAmount = amt;
		this.startTime = time;

		this.totalPackets = (int) Math.ceil(amt / Constants.PACKET_SIZE);
		this.windowSize = Constants.DEFAULT_WINDOW_SIZE;
	}

	public void updateWindowSize() {
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.flowName.equals(((Flow) obj).flowName);
		}
	}

	public int hashCode() {
		return this.flowName.hashCode();
	}
}
