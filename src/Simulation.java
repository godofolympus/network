import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.knowm.xchart.Chart;
import org.knowm.xchart.SwingWrapper;

public class Simulation {
	/**
	 * This main function runs the simulation. We first parse the test case
	 * specified by the user through standard input. We then populate our event
	 * priority queue with initial events. Next we continuously pop the event
	 * with the earliest execution time from the priority queue and handle the
	 * event. Lastly, we organize the data we collected during execution and
	 * plot the results
	 */
	public static void main(String[] args) throws IOException {
		// Retrieve filename from user through standard input
		System.out.print("Enter Filename for Test Case: ");
		Scanner sc = new Scanner(System.in);
		String filename = sc.next();
		sc.close();

		// Parse file and create a Network object containing details of the
		// network architecture. Create a DataCollector object to handle
		// data collection. Create the event priority queue for the simulation
		Network network = Network.parse(filename);
		DataCollector dataCollector = new DataCollector(network);
		PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

		// Schedule the first RoutingEvent such that it finishes just before
		// 0.0 seconds so that the network can simulate any events happening
		// from 0.0 seconds and onwards
		eventQueue.add(new RoutingEvent(-Constants.ROUTING_TIMEOUT - 0.1,
				network.routers));

		// Initialize eventQueue with initializeFlowEvent objects for
		// each flow we want to simulate
		for (Flow flow : network.flows.values()) {
			InitializeFlowEvent flowEvent = new InitializeFlowEvent(
					flow.startTime, flow);
			eventQueue.add(flowEvent);
		}

		// Define variables to use during simulation
		int eventCount = 0;
		double intervalStartTime = 0;
		double nextRoutingTime = Constants.ROUTING_INTERVAL
				- Constants.ROUTING_TIMEOUT;

		// Begin simulation by popping from eventQueue until it is empty
		while (eventQueue.size() != 0) {

			// Pull the event with the smallest value for event.time
			Event event = eventQueue.poll();

			// Collect data periodically
			while (event.time > intervalStartTime && event.time > 0.0) {
				dataCollector.collectData(intervalStartTime, intervalStartTime
						+ Constants.COLLECTING_INTERVAL);
				intervalStartTime += Constants.COLLECTING_INTERVAL;
			}

			// Add a RoutingEvent object after a given time interval
			if (event.time >= nextRoutingTime) {
				nextRoutingTime += Constants.ROUTING_INTERVAL;
				eventQueue.add(new RoutingEvent(event.time, network.routers));
			}

			// Output current event after a given number of events have passed
			if (eventCount % Constants.EVENT_OUTPUT_FREQ == 0) {
				System.out.println(event);
			}

			// Call the event handler
			List<Event> newEvents = event.handle();

			// Add any newly spawned events
			if (newEvents != null) {
				eventQueue.addAll(newEvents);
			}

			eventCount++;
		}

		// Simulation has ended
		System.out.println("Simulation concluded");

		// Collect data in proper format
		ArrayList<Double> timeList = new ArrayList<Double>();
		HashMap<String, ArrayList<Double>> hostData = new HashMap<String, ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> linkData = new HashMap<String, ArrayList<Double>>();
		HashMap<String, ArrayList<Double>> flowData = new HashMap<String, ArrayList<Double>>();

		for (DataElement element : dataCollector.dataList) {

			// Add the time element
			timeList.add((element.endTime + element.startTime) / 2);

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
			if (hostMap.containsKey(field.substring(field.indexOf('-') + 1))) {
				chart = hostMap.get(field.substring(field.indexOf('-') + 1));
			}

			chart = Graph.plot(field.substring(field.indexOf('-') + 1),
					"time (seconds)", field.substring(field.indexOf('-') + 1),
					field, timeList, fieldValues, null, chart);
			hostMap.put(field.substring(field.indexOf('-') + 1), chart);
		}

		for (Chart chart : hostMap.values()) {
			new SwingWrapper(chart).displayChart();
		}

		// Iterate over links
		HashMap<String, Chart> linkMap = new HashMap<String, Chart>();
		for (String field : linkData.keySet()) {
			ArrayList<Double> fieldValues = linkData.get(field);

			Chart chart = null;
			if (linkMap.containsKey(field.substring(field.indexOf('-') + 1))) {
				chart = linkMap.get(field.substring(field.indexOf('-') + 1));
			}

			chart = Graph.plot(field.substring(field.indexOf('-') + 1),
					"time (seconds)", field.substring(field.indexOf('-') + 1),
					field, timeList, fieldValues, null, chart);
			linkMap.put(field.substring(field.indexOf('-') + 1), chart);
		}

		for (Chart chart : linkMap.values()) {
			new SwingWrapper(chart).displayChart();
		}

		// Iterate over flows
		HashMap<String, Chart> flowMap = new HashMap<String, Chart>();
		for (String field : flowData.keySet()) {
			ArrayList<Double> fieldValues = flowData.get(field);

			Chart chart = null;
			if (flowMap.containsKey(field.substring(field.indexOf('-') + 1))) {
				chart = flowMap.get(field.substring(field.indexOf('-') + 1));
			}

			chart = Graph.plot(field.substring(field.indexOf('-') + 1),
					"time (seconds)", field.substring(field.indexOf('-') + 1),
					field, timeList, fieldValues, null, chart);
			flowMap.put(field.substring(field.indexOf('-') + 1), chart);
		}

		for (Chart chart : flowMap.values()) {
			new SwingWrapper(chart).displayChart();
		}
	}
}
