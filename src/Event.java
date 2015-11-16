import java.util.List;

public abstract class Event implements Comparable<Event> {
	double time;

	public Event(double time) {
		this.time = time;
	}
	
	@Override
	public int compareTo(Event o) {
		return this.time - o.time <= 0 ? -1 : 1;
	}
	
	public String toString(){
		return ""+time;
	}

	public abstract List<Event> handle();

}
