import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutingEvent extends Event {
	HashMap<String, Router> routers;

	public RoutingEvent(double time, HashMap<String, Router> routers) {
		super(time);
		this.routers = routers;
	}

	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		for (Router router : routers.values()) {
			initializeBellmanFord(router);
			newEvents.addAll(sendRoutingInfo(router));
		}

		// Create a new ChangeRouting event. After a short period of time,
		// all routers will switch over to the new routingTable calculated by
		// this routing event process
		ChangeRoutingEvent changeRoutingEvent = new ChangeRoutingEvent(
				this.time + Constants.ROUTING_TIMEOUT, routers);
		newEvents.add(changeRoutingEvent);

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	private void initializeBellmanFord(Router r) {
		// For this router, create new version of routing table and distances
		r.routingTableCopy = new HashMap<String, Link>();
		r.distances = new HashMap<String, Double>();

		// Set the distance to all components of the network to infinity
		for (String key : r.components.keySet()) {
			r.distances.put(key, Double.POSITIVE_INFINITY);
		}

		// Set distance of self to zero
		r.distances.put(r.name, 0.0);

		// For all adjacent links to this router
		for (Link link : r.links.values()) {
			// Retrieve adjacent component
			Component adjComponent = link.getAdjacentEndpoint(r);
			// Add this info to distance and our copy of routing table
			r.distances.put(adjComponent.name, link.totalLeftDelay
					+ link.totalRightDelay + link.linkDelay);
			r.routingTableCopy.put(adjComponent.name, link);
		}
	}

	private List<Event> sendRoutingInfo(Router r) {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Send the distance information of this router to its neighbors
		for (Link link : r.links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(r);

			// Only send info to routers
			if (adjComponent instanceof Router) {
				RoutingPacket routingPacket = new RoutingPacket(
						Constants.PacketType.ROUTING,
						Constants.ROUTING_PACKET_SIZE, r, (Router) adjComponent,
						r.distances);
				SendPacketEvent sendPacketEvent = new SendPacketEvent(this.time,
						routingPacket, r, adjComponent, link);
				newEvents.add(sendPacketEvent);
			}
		}

		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: Routing Event\t\t\tDetails: Dynamic Shortest Paths Routing";
	}
}
