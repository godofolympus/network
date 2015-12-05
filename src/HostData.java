public class HostData {
	double sendRate;
	double receiveRate;
	
	public HostData(double sendRate, double recRate) {
		this.sendRate = sendRate;
		this.receiveRate = recRate;
	}
	
	public String toString() {
		return "sendRate: " + sendRate + "\treceiveRate: " + receiveRate;
	}
}
