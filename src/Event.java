import java.util.List;

public abstract class Event implements Comparable<Event> {
	double time;

	public Event(double time) {
		this.time = time;
	}
	
	@Override
	public int compareTo(Event o) {
		return (int) (this.time - o.time);
	}

	public abstract List<Event> handle();

}
