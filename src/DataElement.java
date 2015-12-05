import java.util.HashMap;

public class DataElement {
	double startTime;
	double endTime;

	HashMap<String, HostData> hostDataList;
	HashMap<String, RouterData> routerDataList;
	HashMap<String, LinkData> linkDataList;
	HashMap<String, FlowData> flowDataList;

	public DataElement(double prevTime, double currentTime) {

		this.startTime = prevTime;
		this.endTime = currentTime;
		
		hostDataList = new HashMap<String, HostData>();
		routerDataList = new HashMap<String, RouterData>();
		linkDataList = new HashMap<String, LinkData>();
		flowDataList = new HashMap<String, FlowData>();
	}
	
	public void addHost(String hostName, HostData hostData) {
		hostDataList.put(hostName, hostData);
	}
	
	public void addRouter(String routerName, RouterData routerData) {
		routerDataList.put(routerName, routerData);
	}
	
	public void addLinkData(String linkName, LinkData linkData) {
		linkDataList.put(linkName, linkData);
	}
	
	public void addFlow(String flowName, FlowData flowData) {
		flowDataList.put(flowName, flowData);
	}
}
