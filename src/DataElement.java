import java.util.HashMap;

/**
 * This class is used to encapsulate the data collected over the time period 
 * extending from startTime to endTime. The data can then be accessed fairly
 * easily for plotting later on. The fields that we collect data for can be
 * found in the Constants class. 
 */
public class DataElement {
	double startTime;
	double endTime;

	// Hash maps to store the data values we are interested in
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
	
	public void addHostData(String hostName, double sendRate, double receiveRate) {
		hostDataList.put(hostName + "-" + Constants.hostFields[0], sendRate);
		hostDataList.put(hostName + "-" + Constants.hostFields[1], receiveRate);
	}
	
	public void addLinkData(String linkName, double occupancyData, double packetsLost, double flowRate) {
		linkDataList.put(linkName + "-" + Constants.linkFields[0], occupancyData);
		linkDataList.put(linkName + "-" + Constants.linkFields[1], packetsLost);
		linkDataList.put(linkName + "-" + Constants.linkFields[2], flowRate);
	}
	
	public void addFlowData(String flowName, double sendRate, double receiveRate, double rtt, double windowSize) {
		flowDataList.put(flowName + "-" + Constants.flowFields[0], sendRate);
		flowDataList.put(flowName + "-" + Constants.flowFields[1], receiveRate);
		flowDataList.put(flowName + "-" + Constants.flowFields[2], rtt);
		flowDataList.put(flowName + "-" + Constants.flowFields[3], windowSize);
	}
}
