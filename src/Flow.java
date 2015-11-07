
public class Flow {
	String flowName;
	Host srcHost;
	Host destHost;
	double dataAmount;
	double startTime;
	
	public Flow(String name, Host src, Host dest, double amt, double time) {
		this.flowName = name;
		this.srcHost = src;
		this.destHost = dest;
		this.dataAmount = amt;
		this.startTime = time;
	}
	
	public boolean equals(Object obj){
	     if (obj == null || obj.getClass() != getClass()) {
	         return false;
	      } else {
	          return this.flowName.equals(((Flow)obj).flowName);
	      }
	}
}
