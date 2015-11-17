import java.util.ArrayList;
import java.util.List;

public class NegAckEvent extends Event {
	SendPacketEvent sendEvent;

	public NegAckEvent(double time, Packet packet, SendPacketEvent sendEvent) {
		super(time, packet);
		this.sendEvent = sendEvent;
	}

	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		Host srcHost = (Host) sendEvent.src;
		Flow flow = srcHost.currentFlows.get(packet.flowName);
		if (flow.sendingBuffer.containsKey(packet.id)) {
			newEvents.add(new SendPacketEvent(time, packet, flow.srcHost,
					sendEvent.dst, sendEvent.link));
			System.out.println("NOOOOOOOO!");
		}
		return newEvents;
	}
}
