public class FlowData {
	double sendRate;
	double receiveRate;
	double rtt;

	public FlowData(double sendRate, double recRate, double rtt) {
		this.sendRate = sendRate;
		this.receiveRate = recRate;
		this.rtt = rtt;
	}

	public String toString() {
		return "sendRate: " + sendRate + "\treceieveRate: " + receiveRate
				+ "rtt: " + rtt;
	}
}
