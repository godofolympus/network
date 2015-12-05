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

		// If Packet is still in the sending buffer then an ack packet has not
		// been received, and so re-send the packet.
		if (flow.sendingBuffer.containsKey(packet.id)) {
			newEvents.add(new SendPacketEvent(time, packet, flow.srcHost,
					sendEvent.dst, sendEvent.link));
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: NegAckEvent\t\t\t\tDetails: Check that ACK for packet "
				+ this.packet.flowName + "-" + this.packet.id
				+ " arrived at host " + packet.dstHost.name + " before one rtt";
	}
}
