import java.util.List;

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

	public String toString() {
		return "Time: " + time;
	}

	public abstract List<Event> handle();

}
