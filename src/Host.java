
public class Host {
	String name;
	
	public Host(String name) {
		this.name = name;
	}
	
	public boolean equals(Object obj) {
	     if (obj == null || obj.getClass() != getClass()) {
	         return false;
	      } else {
	          return this.name.equals(((Host)obj).name);
	      }
	}
	
    public int hashCode() {
    	return this.name.hashCode();
    }
}
