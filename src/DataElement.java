import java.util.HashMap;

public class DataElement {
	double startTime;
	double endTime;

	HashMap<String, Double> hostDataList;
	HashMap<String, Double> linkDataList;
	HashMap<String, Double> flowDataList;

	public DataElement(double prevTime, double currentTime) {

		this.startTime = prevTime;
		this.endTime = currentTime;
		
		hostDataList = new HashMap<String, Double>();
		linkDataList = new HashMap<String, Double>();
		flowDataList = new HashMap<String, Double>();
	}
	
	public void addHostData(String hostName, HostData hostData) {
		hostDataList.put(hostName + "-" + Constants.hostFields[0], hostData.sendRate);
		hostDataList.put(hostName + "-" + Constants.hostFields[1], hostData.receiveRate);
	}
	
	public void addLinkData(String linkName, LinkData linkData) {
		linkDataList.put(linkName + "-" + Constants.linkFields[0], linkData.occupancyData);
		linkDataList.put(linkName + "-" + Constants.linkFields[1], (double) linkData.packetsLost);
		linkDataList.put(linkName + "-" + Constants.linkFields[2], linkData.flowRate);
	}
	
	public void addFlowData(String flowName, FlowData flowData) {
		flowDataList.put(flowName + "-" + Constants.flowFields[0], flowData.sendRate);
		flowDataList.put(flowName + "-" + Constants.flowFields[1], flowData.receiveRate);
		flowDataList.put(flowName + "-" + Constants.flowFields[2], flowData.rtt);
		flowDataList.put(flowName + "-" + Constants.flowFields[3], (double) flowData.windowSize);
	}
}
