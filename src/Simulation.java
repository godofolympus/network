import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

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
		eventQueue.add(new RoutingEvent(-1, network.routers));

		// Initialize eventQueue with initializeFlowEvent objects for
		// each flow we want to simulate
		for (Flow flow : network.flows.values()) {
			InitializeFlowEvent flowEvent = new InitializeFlowEvent(
					flow.startTime, flow);
			eventQueue.add(flowEvent);
		}

		// Define variables to use during simulation
		int eventCount = 0;
		int dataCollectionFreq = 1;
		int stopping_count = 100000;
		double prevTime = 0.0;

		// Begin simulation by popping from eventQueue until it is empty
		while (eventQueue.size() != 0) {
			// TODO: Remove stopping count before submitting
			//if (eventCount > stopping_count)
			//	break;

			// Pull next event and print out its information
			Event event = eventQueue.poll();
			System.out.println(event);

			// Collect data after handling a given number of events
			if (eventCount % dataCollectionFreq == 0) {
				prevTime = dataCollector.collectData(prevTime, event.time);
			}

			// Call the event handler
			List<Event> newEvents = event.handle();

			// If handling this event spawns new events, add them to the
			// priority queue
			if (newEvents != null) {
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
		for (String field : hostData.keySet()) {
			ArrayList<Double> fieldValues = hostData.get(field);
			Graph.plot(field, "time (seconds)", field.substring(field.indexOf('-') + 1), null, timeList, fieldValues, null);
		}

		// Iterate over links
		for (String field : linkData.keySet()) {
			ArrayList<Double> fieldValues = linkData.get(field);
			Graph.plot(field, "time (seconds)", field.substring(field.indexOf('-') + 1), null, timeList, fieldValues, null);
		}

		// Iterate over flows
		for (String field : flowData.keySet()) {
			ArrayList<Double> fieldValues = flowData.get(field);
			Graph.plot(field, "time (seconds)", field.substring(field.indexOf('-') + 1), null, timeList, fieldValues, null);
		}
		
	}

}
