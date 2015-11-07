
public class Link {
	String name;

	public Link(String name){
		this.name = name;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.name.equals(((Link) obj).name);
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

}
