public class LinkData {
	double occupancyData;
	int packetsLost;
	double flowRate;

	public LinkData(double occData, int packLost, double flowRate) {
		this.occupancyData = occData;
		this.packetsLost = packLost;
		this.flowRate = flowRate;
	}

	public String toString() {
		return "occData: " + occupancyData + "\tpacketsLost: " + packetsLost
				+ "\tflowRate: " + flowRate;
	}
}
