import java.util.List;

public class InitializeFlowEvent extends Event {
	Flow flow;

	public InitializeFlowEvent(double startTime, Flow flow) {
		super(startTime);
		this.flow = flow;
	}

	@Override
	public List<Event> handle() {
		return null;
	}

}
