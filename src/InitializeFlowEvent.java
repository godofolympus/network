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
			Packet packet = new Packet(i, Constants.PacketType.DATA_PACKET,
					Constants.PACKET_SIZE, flow.srcHost, flow.dstHost);
			flow.outgoingPackets.put(packet.id, packet);
			events.add(new SendPacketEvent(time, packet));
		}

		return events;
	}
}
