import java.util.HashMap;

public class Flow {
	String flowName;
	Host srcHost;
	Host dstHost;
	double dataAmount;
	double startTime;
	double rtt = 0.03;
	
	Constants.TCP tcp;
	double slowStartThresh;

	double windowSize;
	int totalPackets;
	int currentPackets = 0;
	int minUnacknowledgedPacketSender = 0;
	int minUnacknowledgedPacketReceiver = 0;
	int windowFailed = 0;
	int dupPacketId = -1;
	int dupPacketCount = 0;
	boolean fastRetransmit = false;
	// TODO: sendingBuffer and receivingBuffer should be moved into host
	HashMap<Integer, Packet> sendingBuffer = new HashMap<Integer, Packet>();
	//HashMap<Integer, Double> sendingTimes = new HashMap<Integer, Double>();
	HashMap<Integer, Packet> receivingBuffer = new HashMap<Integer, Packet>();
	

	// Variables used to store sendRate and receiveRate data
	int bytesSent = 0;
	int bytesReceived = 0;
	double rttSum = 0.0;
	int acksReceived = 0;
	double windowSizeSum = 0.0;
	int windowChangedCount = 0;

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
