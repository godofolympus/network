import java.util.List;

public class Host extends Component {
	String hostName;
	Link adjLink;
	List<Flow> currentFlows;
	List<Flow> currentWindowSizes;

	public Host(String name) {
		this.hostName = name;
	}

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
