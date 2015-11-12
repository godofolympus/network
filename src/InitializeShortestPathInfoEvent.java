import java.util.List;

public class InitializeShortestPathInfoEvent extends Event {

	public InitializeShortestPathInfoEvent(double time) {
		super(time);
	}

	@Override
	public List<Event> handle() {
		return null;
	}

}
