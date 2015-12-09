import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles the logic of updating a flow's window size for TCP FAST
 */
public class FastUpdateEvent extends Event{
	Flow flow;
	double timeInterval = 0.1;
	
	public FastUpdateEvent(double startTime, double timeInterval, Flow flow){
		super(startTime);
		this.flow = flow;
		this.timeInterval = timeInterval;
	}

	/**
	 * This function updates a flow's window size using the update rule for 
	 * TCP FAST. It then reschedules a new FastUpdateEvent as long as the flow
	 * is still sending data
	 */
	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		
		// Update the window size using the TCP FAST update rule
		flow.windowSize = flow.minRtt/flow.rtt*flow.windowSize + Constants.TCP_FAST_ALPHA;
		
		// Schedule new events only if the flow is still sending packets
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
