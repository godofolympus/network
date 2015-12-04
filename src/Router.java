import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router extends Component {
	// HashMap<String, Link> links = new HashMap<String, Link>();
	HashMap<String, Component> components;

	// Routing table?
	HashMap<String, Link> routing = new HashMap<String, Link>();
	HashMap<String, Double> distances = new HashMap<String, Double>();
	HashMap<Link, HashMap<String, Double>> distancesList = new HashMap<Link, HashMap<String, Double>>();

	public Router(String name) {
		super(name);
		this.links = new HashMap<String, Link>();
		routing = new HashMap<String, Link>();
	}

	public void initializeBellmanFord() {
		for (String key : components.keySet()) {
			distances.put(key, Double.POSITIVE_INFINITY);
		}
		distances.put(this.name, 0.0);
		for (Link link : links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(this);
			distances.put(adjComponent.name, link.linkDelay);
			routing.put(adjComponent.name, link);
		}
	}

	public boolean updateTable() {
		boolean distancesChanged = false;
		if (distancesList == null) {
			return false;
		}
		// For each neighbor that sent updated routing info process distances.
		for (Link link : distancesList.keySet()) {
			HashMap<String, Double> newDistances = distancesList.get(link);
			for (String componentName : newDistances.keySet()) {
				double newDist = link.linkDelay
						+ newDistances.get(componentName);
				// Dynamic programming step to update distance.
				if (newDist < distances.get(componentName)) {
					distances.put(componentName, newDist);
					routing.put(componentName, link);
					distancesChanged = true;
				}
			}
		}
		return distancesChanged;
	}

	// TODO Change this to send packets to neighbors
	public void sendRoutingInfo() {
		// Send distance vector to neighbors.
		for (Link link : links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(this);
			if (adjComponent instanceof Router) {
				Router r = (Router) adjComponent;
				r.distancesList.put(link, distances);
			}
		}
	}
	
	public Event receivePacket(Double time, Packet packet) {
		Link nextLink = this.routing.get(packet.dstHost.name);
		Component nextDst = nextLink.getAdjacentEndpoint(this);
		return new SendPacketEvent(time, packet, this, nextDst, nextLink);
	}

}
