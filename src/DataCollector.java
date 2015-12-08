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
			double flowRate = link.bytesSent / link.bytesTime;
			double totalLeftDelay = link.totalLeftDelay;
			double totalRightDelay = link.totalRightDelay;

			// Reset link data
			link.packetsLost = 0;
			//link.bytesSent = 0;

			// Add to data element
			dataElement.addLinkData(link.linkName, occData, packetsLost, flowRate/*, totalLeftDelay, totalRightDelay*/);
		}

		// Iterate over flows
		for (Flow flow : network.flows.values()) {
			
			// Collect flow data
			double sendRate = flow.bytesSent / (currentTime - prevTime);
			double recRate = flow.bytesReceived / (currentTime - prevTime); 
			double rtt = flow.rttSum / flow.acksReceived;
			//double windowSize = flow.windowSizeSum / flow.windowChangedCount;
			double windowSize = flow.windowSize;
			double slowStartThresh = flow.slowStartThresh;
			
			// Reset data
			flow.bytesSent = 0;
			flow.bytesReceived = 0;
			flow.rttSum = 0.0;
			flow.acksReceived = 0;
			flow.windowSizeSum = flow.windowSize;
			flow.windowChangedCount = 1;
			
			// Add to data element
			dataElement.addFlowData(flow.flowName, sendRate, recRate, rtt, windowSize, slowStartThresh);
		}
		
		// Iterate over hosts
		for (Host host : network.hosts.values()) {
			
			// Collect host data
			double sendRate = host.bytesSent / (currentTime - prevTime);
			double recRate = host.bytesReceived / (currentTime - prevTime);
			
			// Reset data
			host.bytesSent = 0;
			host.bytesReceived = 0;
			
			// Add to data element
			dataElement.addHostData(host.name, sendRate, recRate);
		}

		// Append this dataElement to our list
		dataList.add(dataElement);
		return currentTime;
	}

}
