import java.util.HashMap;

public class Host extends Component {
	HashMap<String, Flow> currentFlows = new HashMap<String, Flow>();

	public Host(String name) {
		super(name);
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.name.equals(((Host) obj).name);
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}
}
