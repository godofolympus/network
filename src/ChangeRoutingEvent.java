import java.util.HashMap;
import java.util.List;

public class ChangeRoutingEvent extends Event {
	HashMap<String, Router> routers;

	public ChangeRoutingEvent(double time, HashMap<String, Router> routers) {
		super(time);
		this.routers = routers;
	}

	@Override
	public List<Event> handle() {
		// Iterate over routers and switch current routingTable with the copy
		for (Router router : routers.values()) {
			router.routingTable = router.routingTableCopy;
		}

		return null;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: ChangeRoutingEvent\t\t\tDetails: All routers switching to new routing tables";
	}
}
