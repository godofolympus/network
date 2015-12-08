import java.util.HashMap;

public class Router extends Component {
	HashMap<String, Component> components;

	// This router's routing table
	HashMap<String, Link> routingTable = new HashMap<String, Link>();
	
	// The distance vector/hash map from this router to other components
	HashMap<String, Double> distances = new HashMap<String, Double>();
	
	// Collection of neighbors distance vector/hash map
	HashMap<Link, HashMap<String, Double>> distancesList = new HashMap<Link, HashMap<String, Double>>();

	public Router(String name) {
		super(name);
		this.links = new HashMap<String, Link>();
		routingTable = new HashMap<String, Link>();
	}

	public void initializeBellmanFord() {
		// For all components in the network, set their distance to inf
		for (String key : components.keySet()) {
			distances.put(key, Double.POSITIVE_INFINITY);
		}
		
		// Set distance of self to zero
		distances.put(this.name, 0.0);
		
		// For all the links connected to this router
		for (Link link : links.values()) {
			// Set the distance to the adjacent components
			Component adjComponent = link.getAdjacentEndpoint(this);
			distances.put(adjComponent.name, link.totalDelay);
			
			// Add this distance to the routing table
			routingTable.put(adjComponent.name, link);
		}
	}

	public boolean updateRoutingTable() {
		boolean distancesChanged = false;
		if (distancesList == null) {
			return false;
		}
		
		// For each neighbor that sent updated routing info, process distances.
		for (Link link : distancesList.keySet()) {
			HashMap<String, Double> newDistances = distancesList.get(link);
			for (String componentName : newDistances.keySet()) {
				double newDist = link.totalDelay
						+ newDistances.get(componentName);
				
				// Dynamic programming step to update distance.
				if (newDist < distances.get(componentName)) {
					distances.put(componentName, newDist);
					routingTable.put(componentName, link);
					distancesChanged = true;
				}
			}
		}
		return distancesChanged;
	}

	// TODO Change this to send packets to neighbors
	public void sendRoutingInfo() {
		// Send distance hash map to neighbors
		for (Link link : links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(this);
			
			// For every neighbor that is a router
			if (adjComponent instanceof Router) {
				Router r = (Router) adjComponent;
				r.distancesList.put(link, distances);
			}
		}
	}
	
	// TODO: Handle routing packets
	public Event receivePacket(Double time, Packet packet) {
		Link nextLink = this.routingTable.get(packet.dstHost.name);
		Component nextDst = nextLink.getAdjacentEndpoint(this);
		return new SendPacketEvent(time, packet, this, nextDst, nextLink);
	}

}
