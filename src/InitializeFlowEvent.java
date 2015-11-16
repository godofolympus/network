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
		flow.srcHost.currentFlows.put(flow.flowName, flow);
		List<Event> events = new ArrayList<Event>();

		for (int i = 0; i < Math.min(flow.windowSize, flow.totalPackets); i++) {
			Packet packet = new Packet("" + flow.maxPacketId,
					Constants.PacketType.DATA, Constants.PACKET_SIZE,
					flow.srcHost, flow.dstHost, flow.flowName);
			flow.maxPacketId++;
			flow.outgoingPackets.put(packet.id, packet);
			Link link = flow.srcHost.links.values().iterator().next();
			Component currentDst = link.getAdjacentEndpoint(flow.srcHost);
			events.add(new SendPacketEvent(time + flow.maxPacketId / 100000.0,
					packet, flow.srcHost, currentDst, link));
		}

		return events;
	}
}
