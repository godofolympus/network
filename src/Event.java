import java.util.List;

/**
 * Generic Event class. Serves as the superclass to all events handled by the
 * event priority queue. All classes that extend this class must implement a
 * handle() method
 */
public abstract class Event implements Comparable<Event> {
	double time;
	Packet packet;

	public Event(double time) {
		this.time = time;
	}

	public Event(double time, Packet packet) {
		this.time = time;
		this.packet = packet;
	}

	@Override
	public int compareTo(Event o) {
		double diff = this.time - o.time;
		if (diff == 0 && this.packet != null && o.packet != null) {
			return this.packet.id - o.packet.id;
		}
		return diff < 0 ? -1 : 1;
	}

	// Truncate the time value to standardize output
	public String toString() {
		return "Time: " + String.format("%.8f", time);
	}

	// All classes that extend Event must implement this method
	public abstract List<Event> handle();

}
