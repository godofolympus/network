public class FlowData {
	double sendRate;
	double receiveRate;
	double rtt;
	int windowSize;

	public FlowData(double sendRate, double recRate, double rtt, int windowSize) {
		this.sendRate = sendRate;
		this.receiveRate = recRate;
		this.rtt = rtt;
		this.windowSize = windowSize;
	}

	public String toString() {
		return "sendRate: " + sendRate + "\treceieveRate: " + receiveRate
				+ " rtt: " + rtt + "windowSize: " + windowSize;
	}
}
