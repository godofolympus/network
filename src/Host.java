import java.util.HashMap;

public class Host extends Component {
	String hostName;
	Link adjLink;
	HashMap<String, Flow> currentFlows;

	public Host(String name) {
		this.hostName = name;
	}
	
	public
	
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.hostName.equals(((Host) obj).hostName);
		}
	}

	public int hashCode() {
		return this.hostName.hashCode();
	}
}
