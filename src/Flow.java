import java.util.HashMap;

/**
 * Generic class used to represent a Flow in the network.
 */
public class Flow {
	// Data that should be specified in the test case
	String flowName;
	Host srcHost;
	Host dstHost;
	double dataAmount;
	double startTime;
	int totalPackets;
	
	// Variables used to control the window
	double windowSize;
	int windowFailed = 0;
	
	// Variables used for congestion control
	double rtt = Constants.DEFAULT_RTT;
	double timeout = Constants.RTT_MULTIPLIER * Constants.DEFAULT_RTT;
	double minRtt = rtt;
	double slowStartThresh;
	Constants.TCP tcp;
	
	// Variables used for error control
	int currentPackets = 0;
	int minUnacknowledgedPacketSender = 0;
	int minUnacknowledgedPacketReceiver = 0;
	HashMap<Integer, Packet> sendingBuffer = new HashMap<Integer, Packet>();
	HashMap<Integer, Packet> receivingBuffer = new HashMap<Integer, Packet>();
	
	// Variables used for fast recovery and fast retransmit
	int dupPacketId = -1;
	int dupPacketCount = 0;
	boolean fastRecovery = false;
	
	// Variable to indicate when the flow has concluded
	boolean flowFinished = false;

	// Variables used to store sendRate and receiveRate data
	int bytesSent = 0;
	int bytesReceived = 0;
	double rttSum = 0.0;
	int acksReceived = 0;

	public Flow(String name, Host src, Host dest, double amt, double time) {
		this.flowName = name;
		this.srcHost = src;
		this.dstHost = dest;
		this.dataAmount = amt;
		this.startTime = time;

		this.totalPackets = (int) Math.ceil(amt / Constants.DATA_PACKET_SIZE);
		this.windowSize = Constants.DEFAULT_WINDOW_SIZE;
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
