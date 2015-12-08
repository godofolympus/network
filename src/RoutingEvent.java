import java.util.HashMap;
import java.util.List;

public class RoutingEvent extends Event {
	HashMap<String, Router> routers;

	public RoutingEvent(double time, HashMap<String, Router> routers) {
		super(time);
		this.routers = routers;
	}

	@Override
	public List<Event> handle() {
		boolean distancesChanged = true;
		for (Router r : routers.values()) {
			//System.out.println("Name " + r.name + ", " + r.links.size());
			r.initializeBellmanFord();
		}
		for (Router r : routers.values()) {
			r.sendRoutingInfo();
		}
		while (distancesChanged) {
			distancesChanged = false;
			/*
			 * For each router run an iteration of Bellman Ford, and if
			 * distances have changed send info to neighbors.
			 */
			for (Router r : routers.values()) {
				boolean changed = r.updateRoutingTable();
				if (changed) {
					r.sendRoutingInfo();
					distancesChanged = changed;
				}
			}
		}
		return null;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: Routing Event\t\t\tDetails: Dynamic Shortest Paths Routing";
	}
}
