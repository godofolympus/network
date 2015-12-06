import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Simulation {
	// TODO: - Implement changes already mentioned as todos
	// TODO: - Add dynamic shortest path finding
	// TODO: - Implement two methods of congestion control
	// TODO: - Add code that collects data from the entire network and save
	// somewhere. Plot using matplotlib or some other library
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
		int dataCollectionFreq = 100;
		int stopping_count = 300;
		double prevTime = 0.0;

		// Begin simulation by popping from eventQueue until it is empty
		while (eventQueue.size() != 0) {
			// TODO: Remove stopping count before submitting
			if (eventCount > stopping_count)
				break;

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

		 //Print out contents of dataCollector object
		 for (DataElement element : dataCollector.dataList) {
			 System.out.println(element.linkDataList);
			 System.out.println(element.flowDataList);
			 System.out.println(element.hostDataList);
			 System.out.println();
		 }
	}

}
