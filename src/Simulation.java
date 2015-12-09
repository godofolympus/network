import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.knowm.xchart.Chart;
import org.knowm.xchart.SwingWrapper;

public class Simulation {
	// TODO: - Implement changes already mentioned as todos
	// TODO: - Add dynamic shortest path finding
	// TODO: - Implement two methods of congestion control
	// TODO: - Add more comments and structure code better
	// TODO: - Test code

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Retrieve filename from user through std input
		System.out.print("Enter Filename for Test Case: ");
		Scanner sc = new Scanner(System.in);
		String filename = sc.next();
		sc.close();

		// Parse file and create Network object, containing details of the
		// network architecture. Create the event priority queue for the
		// simulation.
		Network network = Network.parse(filename);
		DataCollector dataCollector = new DataCollector(network);
		PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

		// Routing Table
		// TODO Change this so that it happens dynamically throughout simulation
		// of network
		eventQueue.add(new RoutingEvent(-Constants.ROUTING_TIMEOUT - 0.1, network.routers));

		// Initialize eventQueue with initializeFlowEvent objects for
		// each flow we want to simulate
		for (Flow flow : network.flows.values()) {
			InitializeFlowEvent flowEvent = new InitializeFlowEvent(
					flow.startTime, flow);
			eventQueue.add(flowEvent);
		}

		// Define variables to use during simulation
		int eventCount = 0;
		int dataCollectionFreq = 1000;
		int routingFreq = 1000;
		int outputFreq = 10000;
		double prevTime = 0.0;
		
		double time = 0;
		double timeInterval = 0.1; // TCP RENO: 0.02 TCP FAST: 0.1
		
		
		double nextRoutingTime = Constants.ROUTING_INTERVAL - 1.0;
		
		// Begin simulation by popping from eventQueue until it is empty
		while (eventQueue.size() != 0) {
			// Pull next event and print out its information
			Event event = eventQueue.poll();

			// Collect data after handling a given number of events
			//if (eventCount % dataCollectionFreq == 0) {
			//	prevTime = dataCollector.collectData(prevTime, event.time);
			//}
			while(event.time > time && event.time > 0.0){
				dataCollector.collectData(time, time + timeInterval);
				time += timeInterval;
			}
			
			
			// Add routing event after every few seconds
			if (event.time >= nextRoutingTime) {
				nextRoutingTime += Constants.ROUTING_INTERVAL;
				eventQueue.add(new RoutingEvent(event.time, network.routers));
			}
			
			// Output current event after a given number of events
			if (eventCount % outputFreq == 0) {
				System.out.println(event);
			}
			

			// Call the event handler
			List<Event> newEvents = event.handle();

			// If handling this event spawns new events, add them to the
			// priority queue
			if (newEvents != null && newEvents.size() > 0) {
				if(eventQueue.size() <= network.flows.size() && newEvents.get(0) instanceof FastUpdateEvent){
					break;
				}
				eventQueue.addAll(newEvents);
			}
			
			eventCount++;
		}
		System.out.println("Simulation concluded");

		// Collect data in proper format
		ArrayList<Double> timeList = new ArrayList<Double>();
		HashMap<String, ArrayList<Double>> hostData = new HashMap<String, ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> linkData = new HashMap<String, ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> flowData = new HashMap<String, ArrayList<Double>>();

		for (DataElement element : dataCollector.dataList) {

			// Add the time element
			timeList.add((element.endTime +  element.startTime) / 2);

			// Iterate over hosts
			for (String field : element.hostDataList.keySet()) {
				if (!hostData.containsKey(field)) {
					hostData.put(field, new ArrayList<Double>());
				}

				hostData.get(field).add(element.hostDataList.get(field));
			}

			// Iterate over links
			for (String field : element.linkDataList.keySet()) {
				if (!linkData.containsKey(field)) {
					linkData.put(field, new ArrayList<Double>());
				}

				linkData.get(field).add(element.linkDataList.get(field));
			}

			// Iterate over flows
			for (String field : element.flowDataList.keySet()) {
				if (!flowData.containsKey(field)) {
					flowData.put(field, new ArrayList<Double>());
				}

				flowData.get(field).add(element.flowDataList.get(field));
			}
		}
		
		// Plot the data that we collected
		
		// Iterate over hosts
		HashMap<String, Chart> hostMap = new HashMap<String, Chart>();
		for (String field : hostData.keySet()) {
			ArrayList<Double> fieldValues = hostData.get(field);

			Chart chart = null;
			if(hostMap.containsKey(field.substring(field.indexOf('-') + 1))){
				chart = hostMap.get(field.substring(field.indexOf('-') + 1));
			}
			
			chart = Graph.plot(field.substring(field.indexOf('-') + 1), "time (seconds)", field.substring(field.indexOf('-') + 1), field, timeList, fieldValues, null, chart);
			hostMap.put(field.substring(field.indexOf('-') + 1), chart);
		}
		
		for(Chart chart:hostMap.values()){
			new SwingWrapper(chart).displayChart();
		}
		
		// Iterate over links
		HashMap<String, Chart> linkMap = new HashMap<String, Chart>();
		for (String field : linkData.keySet()) {
			ArrayList<Double> fieldValues = linkData.get(field);

			if (Integer.parseInt(field.substring(1, field.indexOf('-'))) > 2 || Integer.parseInt(field.substring(1, field.indexOf('-'))) == 0) {
				continue;
			}
			
			Chart chart = null;
			if(linkMap.containsKey(field.substring(field.indexOf('-') + 1))){
				chart = linkMap.get(field.substring(field.indexOf('-') + 1));
			}
			
			chart = Graph.plot(field.substring(field.indexOf('-') + 1), "time (seconds)", field.substring(field.indexOf('-') + 1), field, timeList, fieldValues, null, chart);
			linkMap.put(field.substring(field.indexOf('-') + 1), chart);
		}
		
		for(Chart chart:linkMap.values()){
			new SwingWrapper(chart).displayChart();
		}

		// Iterate over flows
		HashMap<String, Chart> flowMap = new HashMap<String, Chart>();
		for (String field : flowData.keySet()) {
			ArrayList<Double> fieldValues = flowData.get(field);

			Chart chart = null;
			if(flowMap.containsKey(field.substring(field.indexOf('-') + 1))){
				chart = flowMap.get(field.substring(field.indexOf('-') + 1));
			}
			
			chart = Graph.plot(field.substring(field.indexOf('-') + 1), "time (seconds)", field.substring(field.indexOf('-') + 1), field, timeList, fieldValues, null, chart);
			flowMap.put(field.substring(field.indexOf('-') + 1), chart);
		}
		
		for(Chart chart:flowMap.values()){
			new SwingWrapper(chart).displayChart();
		}
		
	}

}
