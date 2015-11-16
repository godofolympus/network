import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Flow {
	String flowName;
	Host srcHost;
	Host dstHost;
	double dataAmount;
	double startTime;
	double rtt = 10;

	int windowSize;
	int totalPackets;
	int currentPackets = 0;
	int maxPacketId = 0;
	HashMap<String, Packet> outgoingPackets = new HashMap<String, Packet>();

	public Flow(String name, Host src, Host dest, double amt, double time) {
		this.flowName = name;
		this.srcHost = src;
		this.dstHost = dest;
		this.dataAmount = amt;
		this.startTime = time;

		this.totalPackets = (int) Math.ceil(amt/Constants.PACKET_SIZE);
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
