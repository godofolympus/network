import java.util.HashMap;

/**
 * Generic class used to represent a Router in the network 
 */
public class Router extends Component {
	HashMap<String, Component> components;

	// This router's routing table. The second routing table is used by the
	// dynamic shortest paths algorithm
	HashMap<String, Link> routingTable = new HashMap<String, Link>();
	HashMap<String, Link> routingTableCopy;
	
	// A hash map that indicates the estimated distance/cost from this router
	// to the specified component
	HashMap<String, Double> distances = new HashMap<String, Double>();

	public Router(String name) {
		super(name);
		this.links = new HashMap<String, Link>();
		routingTable = new HashMap<String, Link>();
	}
}
