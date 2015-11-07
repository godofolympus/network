public abstract class Event implements Comparable<Event> {
	double time;

	@Override
	public int compareTo(Event o) {
		return (int) (this.time - o.time);
	}

	public abstract void handle();

}
