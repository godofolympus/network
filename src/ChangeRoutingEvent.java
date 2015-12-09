import java.util.HashMap;
import java.util.List;

/**
 * Event that handles the logic of switching the routing tables that all routers
 * use simultaneously. If we assume that all routers know when routing begins,
 * then this is event is scheduled for a short time following the start of
 * routing
 */
public class ChangeRoutingEvent extends Event {
	HashMap<String, Router> routers;

	public ChangeRoutingEvent(double time, HashMap<String, Router> routers) {
		super(time);
		this.routers = routers;
	}

	/**
	 * This function iterates over all routers and replaces the routing table
	 * with the updated routing table
	 */
	@Override
	public List<Event> handle() {
		// Iterate over routers and switch current routingTable with the copy
		for (Router router : routers.values()) {
			router.routingTable = router.routingTableCopy;
		}

		// No additional events created by this event, so we return null
		return null;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: ChangeRoutingEvent\t\t\tDetails: All routers switching to new routing tables";
	}
}
