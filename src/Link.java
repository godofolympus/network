import java.util.LinkedList;

/**
 * Generic class used to represent a Link in the network. 
 */
public class Link {
	// Variables specified in the test cases
	String linkName;
	Component leftEndPoint;
	Component rightEndPoint;
	double linkRate;
	double linkDelay;
	double bufferSize;
	
	// Variables used to store congestion metrics
	double totalLeftDelay;
	double totalRightDelay;
	
	// Variables used to store buffer occupancy
	double currentLeftBufferAmt;
	double currentRightBufferAmt;
	
	// Variables that represent the buffers on either end of the link
	LinkedList<Packet> leftBuffer = new LinkedList<Packet>();
	LinkedList<Packet> rightBuffer = new LinkedList<Packet>();
	
	
	//LinkedList<Double> leftArrivalTimes = new LinkedList<Double>();
	//LinkedList<Double> rightArrivalTimes = new LinkedList<Double>();
	
	// Variables used to store relevant data values
	int packetsLost;
	double bytesSent;
	double bytesTime;

	public Link(String name, Component leftEndPoint, Component rightEndPoint,
			double linkRate, double linkDelay, double bufferSize) {
		this.linkName = name;
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.linkRate = linkRate;
		this.linkDelay = linkDelay;
		this.totalLeftDelay = linkDelay;
		this.totalRightDelay = linkDelay;
		this.bufferSize = bufferSize;
		this.bytesTime = linkDelay;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.linkName.equals(((Link) obj).linkName);
		}
	}

	public Component getAdjacentEndpoint(Component endpoint) {
		if (endpoint.equals(leftEndPoint)) {
			return rightEndPoint;
		} else {
			return leftEndPoint;
		}
	}

	public int hashCode() {
		return this.linkName.hashCode();
	}

	public String toString() {
		return this.linkName;
	}

}
