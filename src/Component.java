import java.util.HashMap;

/**
 * Component represents a generic packet sending/receiving entity in the
 * network. It serves as the superclass of Router and Host
 */
public abstract class Component {
	String name;
	// A hash map that holds all the adjacent links to this component
	HashMap<String, Link> links = new HashMap<String, Link>();

	public Component(String name) {
		this.name = name;
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.name.equals(((Component) obj).name);
		}
	}

	public String toString() {
		return this.name;
	}
}
