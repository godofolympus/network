import java.util.ArrayList;
import java.util.List;

public class FastUpdateEvent extends Event{
	Flow flow;
	double timeInterval = 0.1;
	
	public FastUpdateEvent(double startTime, double timeInterval, Flow flow){
		super(startTime);
		this.flow = flow;
		this.timeInterval = timeInterval;
	}

	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		flow.windowSize = flow.minRtt/flow.rtt*flow.windowSize + 30;
		
		if (!flow.flowFinished) {
			newEvents.add(new FastUpdateEvent(this.time+timeInterval, timeInterval, flow));
		}
		
		return newEvents;
	}
	
	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: FastUpdateEvent\t\t\tDetails: Updating Window with TCP FAST";
	}
}
