import java.util.HashMap;

public class DataElement {
	double startTime;
	double endTime;

	HashMap<String, HostData> hostDataList;
	HashMap<String, LinkData> linkDataList;
	HashMap<String, FlowData> flowDataList;

	public DataElement(double prevTime, double currentTime) {

		this.startTime = prevTime;
		this.endTime = currentTime;
		
		hostDataList = new HashMap<String, HostData>();
		linkDataList = new HashMap<String, LinkData>();
		flowDataList = new HashMap<String, FlowData>();
	}
	
	public void addHostData(String hostName, HostData hostData) {
		hostDataList.put(hostName, hostData);
	}
	
	public void addLinkData(String linkName, LinkData linkData) {
		linkDataList.put(linkName, linkData);
	}
	
	public void addFlowData(String flowName, FlowData flowData) {
		flowDataList.put(flowName, flowData);
	}
}
