import java.util.ArrayList;
import java.util.List;

public class DataCollector {
	Network network;
	List<DataElement> dataList = new ArrayList<DataElement>();

	public DataCollector(Network network) {
		this.network = network;
	}

	public double collectData(double prevTime, double currentTime) {
		if (prevTime == currentTime) {
			System.out.println("Same time");
			return prevTime;
		}
		
		// Create DataElement object to store data for this time range
		DataElement dataElement = new DataElement(prevTime, currentTime);
		
		// Iterate over links
		for (Link link: network.links.values()){
			
			// Collect link data
			double occupancyData = link.currentLeftBufferAmt + link.currentRightBufferAmt;
			int packetsLost = link.packetsLost;
			double flowRate = link.bytesSent / (currentTime - prevTime);
			
			// Reset link data
			link.packetsLost = 0;
			link.bytesSent = 0;
			
			// Create new LinkData element and put it into our DataElement object
			LinkData linkData = new LinkData(occupancyData, packetsLost, flowRate);
			dataElement.addLinkData(link.linkName, linkData);
		}
		
		// Append this dataElement to our list
		dataList.add(dataElement);
		return currentTime;
	}
	
	
}
