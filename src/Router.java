public class Router extends Component {
	String routerName;
	Link leftLink;
	Link rightLink;
	// Routing table?

	public Router(String name) {
		this.routerName = name;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.routerName.equals(((Router) obj).routerName);
		}
	}
}
