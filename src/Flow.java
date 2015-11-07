
public class Flow {
	String name;
	
	public boolean equals(Object obj){
	     if (obj == null || obj.getClass() != getClass()) {
	         return false;
	      } else {
	          return this.name.equals(((Flow)obj).name);
	      }
	}
}
