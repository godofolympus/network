import java.util.ArrayList;
import java.util.HashMap;

public class Component {
	String name;
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
