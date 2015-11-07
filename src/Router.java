
public class Router {
	String name;
	
	public Router(String name){
		this.name = name;
	}
	
	public boolean equals(Object obj){
	     if (obj == null || obj.getClass() != getClass()) {
	         return false;
	      } else {
	          return this.name.equals(((Router)obj).name);
	      }
	}
	
    public int hashCode() {
    	return this.name.hashCode();
    }

}
