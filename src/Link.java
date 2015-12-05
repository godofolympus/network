import java.util.ArrayList;
import java.util.LinkedList;

public class Link {
	String linkName;
	Component leftEndPoint;
	Component rightEndPoint;
	double linkRate;
	double linkDelay;
	double currentLeftBufferAmt;
	double currentRightBufferAmt;
	double bufferSize;
	LinkedList<Packet> leftBuffer = new LinkedList<Packet>();
	LinkedList<Packet> rightBuffer = new LinkedList<Packet>();
	LinkedList<Double> leftArrivalTimes = new LinkedList<Double>();
	LinkedList<Double> rightArrivalTimes = new LinkedList<Double>();
	
	// Used by DataCollector to plot data
	int packetsLost;
	int bytesSent;

	public Link(String name, Component leftEndPoint, Component rightEndPoint,
			double linkRate, double linkDelay, double bufferSize) {
		this.linkName = name;
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.linkRate = linkRate;
		this.linkDelay = linkDelay;
		this.bufferSize = bufferSize;
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
		return this.linkName + ":(" + leftEndPoint.name + ","
				+ rightEndPoint.name + ")";
	}

}
