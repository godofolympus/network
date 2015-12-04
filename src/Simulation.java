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

		// Begin simulation by popping from eventQueue until it is empty
		int count = 0;
		int stopping_count = 200;
		
		while (eventQueue.size() != 0) {
			if (count >= stopping_count)
				break;
			
			Event event = eventQueue.poll();
			System.out.println(event);
			List<Event> newEvents = event.handle();
			
			// If handling this event spawns new events, add them to the
			// priority queue
			if (newEvents != null) {
				for (Event newEvent : newEvents) {
					eventQueue.add(newEvent);
				}
			}
			count++;
		}
		
		System.out.println("Simulation concluded");
	}

}
