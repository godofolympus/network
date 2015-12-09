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
		
		// TODO: Consider scheduling next Routing Event from here
		return null;
	}
}
