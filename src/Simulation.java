import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Simulation {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Retrieve filename from user
		System.out.print("Enter Filename for Test Case: ");
		Scanner sc = new Scanner(System.in);
		String filename = sc.next();
		sc.close();

		// Parse file and create Network object, containing details of the
		// network architecture. Create the event priority queue for the
		// simulation
		Network network = Network.parse(filename);
		PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

		// Routing Table
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
		while (eventQueue.size() != 0) {
			if(count >= 200)
				break;
			//System.out.println("Event Queue: " + eventQueue);
			Event event = eventQueue.poll();
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
	}

}
