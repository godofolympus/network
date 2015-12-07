import java.util.ArrayList;
import java.util.List;

public class InitializeFlowEvent extends Event {
	Flow flow;

	public InitializeFlowEvent(double startTime, Flow flow) {
		super(startTime);
		this.flow = flow;
	}

	@Override
	public List<Event> handle() {
		// Put this flow into the currentFlows list in both the src and dst
		flow.srcHost.currentFlows.put(flow.flowName, flow);
		flow.dstHost.currentFlows.put(flow.flowName, flow);
		List<Event> events = new ArrayList<Event>();

		// Set up the first few packets to send
		for (int packetId = 0; packetId < Math.min(Math.floor(flow.windowSize), flow.totalPackets); packetId++) {
			// Create new Packet object based on flow information
			Packet packet = new Packet(packetId,
					Constants.PacketType.DATA, Constants.PACKET_SIZE,
					flow.srcHost, flow.dstHost, flow.flowName);

			// Place packet/time in respective buffer
			flow.sendingBuffer.put(packet.id, packet);

			// Retrieve the single link connected to src host and the adjacent
			// component
			Link link = flow.srcHost.links.values().iterator().next();
			Component currentDst = link.getAdjacentEndpoint(flow.srcHost);

			// Create new SendPacketEvent to trigger the sending of this packet
			SendPacketEvent sendEvent = new SendPacketEvent(time, packet,
					flow.srcHost, currentDst, link);
			events.add(sendEvent);

			// Schedule NegAckEvent to handle any packets that have not been
			// acknowledged within one RTT
			events.add(new NegAckEvent(time + flow.rtt, packet, sendEvent, flow.windowFailed));
		}

		// Return a list of events to add to the event priority queue
		return events;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: InitializeFlowEvent\t\t\tDetails: Flow "
				+ this.flow.flowName + " going from host "
				+ this.flow.srcHost.name + " to host " + this.flow.dstHost.name;
	}
}
