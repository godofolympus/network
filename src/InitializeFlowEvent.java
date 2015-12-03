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
		flow.dstHost.currentFlows.put(flow.flowName, flow);
		List<Event> events = new ArrayList<Event>();

		for (int i = 0; i < Math.min(flow.windowSize, flow.totalPackets); i++) {
			// Add new SendPacketEvent
			Packet packet = new Packet(flow.maxPacketId,
					Constants.PacketType.DATA, Constants.PACKET_SIZE,
					flow.srcHost, flow.dstHost, flow.flowName);
			flow.maxPacketId++;
			// Place packet in sending window buffer
			flow.sendingBuffer.put(packet.id, packet);
			flow.sendingTimes.put(packet.id, time);
			Link link = flow.srcHost.links.values().iterator().next();
			Component currentDst = link.getAdjacentEndpoint(flow.srcHost);
			SendPacketEvent sendEvent = new SendPacketEvent(time, packet,
					flow.srcHost, currentDst, link);
			events.add(sendEvent);
			// Schedule NegAckEvent if ack packet is not received
			events.add(new NegAckEvent(time + flow.rtt, packet, sendEvent));
		}

		return events;
	}
}
