import java.util.ArrayList;
import java.util.List;

public class DataCollector {
	Network network;
	List<DataElement> dataList = new ArrayList<DataElement>();

	public DataCollector(Network network) {
		this.network = network;
	}

	public double collectData(double prevTime, double currentTime) {
		// Do not reset any data values and simply return the prevTime
		// so that we can attempt to collect data again later
		if (prevTime == currentTime) {
			return prevTime;
		}

		// Create DataElement object to store data for this time range
		DataElement dataElement = new DataElement(prevTime, currentTime);

		// Iterate over links
		for (Link link : network.links.values()) {

			// Collect link data
			double occData = link.currentLeftBufferAmt
					+ link.currentRightBufferAmt;
			int packetsLost = link.packetsLost;
			double flowRate = link.bytesSent / (currentTime - prevTime);

			// Reset link data
			link.packetsLost = 0;
			link.bytesSent = 0;

			// Create new LinkData element and save it 
			LinkData linkData = new LinkData(occData, packetsLost, flowRate);
			dataElement.addLinkData(link.linkName, linkData);
		}

		// Iterate over flows
		for (Flow flow : network.flows.values()) {
			
			// Collect flow data
			double sendRate = 0.0;
			double recRate = 0.0; 
			double rtt = 0.0;
			
			// Reset data
			
			// Create new FlowData element and put it in our DataElement
			FlowData flowData = new FlowData(sendRate, recRate, rtt);
			dataElement.addFlowData(flow.flowName, flowData);
		}
		
		// Iterate over hosts
		for (Host host : network.hosts.values()) {
			
			// Collect host data
			double sendRate = 0.0;
			double recRate = 0.0;
			
			// Reset data
			
			// Create new HostData element
			HostData hostData = new HostData(sendRate, recRate);
			dataElement.addHostData(host.name, hostData);
		}

		// Append this dataElement to our list
		dataList.add(dataElement);
		return currentTime;
	}

}
