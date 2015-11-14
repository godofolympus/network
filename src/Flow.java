import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Flow {
	String flowName;
	Host srcHost;
	Host dstHost;
	double dataAmount;
	double startTime;
	
	int windowSize;
	int totalPackets;
	List<Packet> packets;
	HashMap<String, Packet> outgoingPackets;

	public Flow(String name, Host src, Host dest, double amt, double time) {
		this.flowName = name;
		this.srcHost = src;
		this.dstHost = dest;
		this.dataAmount = amt;
		this.startTime = time;
		
		this.totalPackets = (int) Math.ceil(amt/Constants.PACKET_SIZE);
		this.windowSize = Constants.DEFAULT_WINDOW_SIZE;
		this.packets = new ArrayList<Packet>();
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
