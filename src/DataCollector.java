import java.util.ArrayList;
import java.util.List;

/**
 * Class used to collect data during the simulation of the network. 
 */
public class DataCollector {
	Network network;
	List<DataElement> dataList = new ArrayList<DataElement>();

	public DataCollector(Network network) {
		this.network = network;
	}

	/**
	 * This function iterates over the elements in the Network object passed
	 * in through the constructor and collects various pieces of information
	 * from the elements of the Network. This data is stored in a DataElement
	 * object and is later plotted
	 */
	public void collectData(double startTime, double endTime) {
		// Create DataElement object to store data for this time range
		DataElement dataElement = new DataElement(startTime, endTime);

		// Iterate over links
		for (Link link : network.links.values()) {

			// Collect link data
			double occData = link.currentLeftBufferAmt
					+ link.currentRightBufferAmt;
			int packetsLost = link.packetsLost;
			double flowRate = link.bytesSent / link.bytesTime;

			// Reset link data
			link.packetsLost = 0;

			// Add to data element
			dataElement.addLinkData(link.linkName, occData, packetsLost, flowRate);
		}

		// Iterate over flows
		for (Flow flow : network.flows.values()) {
			
			// Collect flow data
			double sendRate = flow.bytesSent / (endTime - startTime);
			double recRate = flow.bytesReceived / (endTime - startTime); 
			double rtt = flow.rttSum / flow.acksReceived;
			double windowSize = flow.windowSize;
			
			// Reset data
			flow.bytesSent = 0;
			flow.bytesReceived = 0;
			flow.rttSum = 0.0;
			flow.acksReceived = 0;
			
			// Add to data element
			dataElement.addFlowData(flow.flowName, sendRate, recRate, rtt, windowSize);
		}
		
		// Iterate over hosts
		for (Host host : network.hosts.values()) {
			
			// Collect host data
			double sendRate = host.bytesSent / (endTime - startTime);
			double recRate = host.bytesReceived / (endTime - startTime);
			
			// Reset data
			host.bytesSent = 0;
			host.bytesReceived = 0;
			
			// Add to data element
			dataElement.addHostData(host.name, sendRate, recRate);
		}

		// Append this dataElement to our list
		dataList.add(dataElement);
	}

}
