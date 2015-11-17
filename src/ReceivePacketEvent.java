import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends Event {
	Component component;

	public ReceivePacketEvent(double time, Packet packet, Component component) {
		super(time, packet);
		this.component = component;
	}

	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		System.out.println("Recieved " + packet.packetType + " Packet "
				+ packet.id + " at " + component.name + " for "
				+ packet.dstHost.name + ", Time: " + time);
		if (component.name.equals(packet.dstHost.name)) {
			Host host = (Host) component;
			Flow flow = host.currentFlows.get(packet.flowName);
			if (packet.packetType == Constants.PacketType.DATA) {
				flow.receivingBuffer.put(packet.id, packet);
				while (flow.receivingBuffer
						.containsKey(flow.minUnacknowledgedPacketReceiver)) {
					flow.minUnacknowledgedPacketReceiver++;
				}
				System.out.println("New ACK Packet for Packet " + packet.id);
				Packet ackPacket = new Packet(packet.id,
						Constants.PacketType.ACK, Constants.ACK_SIZE, host,
						packet.srcHost, packet.flowName);
				ackPacket.negPacketId = flow.minUnacknowledgedPacketReceiver;
				Link link = host.links.values().iterator().next();
				Component currentDst = link.getAdjacentEndpoint(host);
				newEvents.add(new SendPacketEvent(time, ackPacket, host,
						currentDst, link));
			} else if (packet.packetType == Constants.PacketType.ACK) {
				if (flow.sendingBuffer.containsKey(packet.id)) {
					flow.sendingBuffer.remove(packet.id);
					flow.rtt = 0.5 * (time - flow.sendingTimes.get(packet.id))
							+ 0.5 * flow.rtt + 0.0001;
					flow.sendingTimes.remove(packet.id);
				}
				flow.currentPackets++;
				int nextUnacknowledgedPacket = packet.negPacketId;
				boolean minChanged = flow.minUnacknowledgedPacketSender < nextUnacknowledgedPacket;
				while (flow.minUnacknowledgedPacketSender < nextUnacknowledgedPacket) {
					if (flow.sendingBuffer
							.containsKey(flow.minUnacknowledgedPacketSender)) {
						flow.sendingBuffer
								.remove(flow.minUnacknowledgedPacketSender);
						flow.currentPackets++;
					}
					flow.minUnacknowledgedPacketSender++;
				}
				if (flow.currentPackets < flow.totalPackets)
					System.out.println("Next Unacknowledged Packet: Packet "
							+ flow.minUnacknowledgedPacketSender);
				if (minChanged && flow.currentPackets < flow.totalPackets) {
					while (flow.sendingBuffer.size() < flow.windowSize) {
						Packet nextPacket = new Packet(flow.maxPacketId,
								Constants.PacketType.DATA,
								Constants.PACKET_SIZE, flow.srcHost,
								flow.dstHost, flow.flowName);
						System.out.println("New DATA Packet " + nextPacket.id);
						flow.maxPacketId++;
						flow.sendingBuffer.put(nextPacket.id, nextPacket);
						flow.sendingTimes.put(nextPacket.id, time);
						Link link = flow.srcHost.links.values().iterator()
								.next();
						Component currentDst = link
								.getAdjacentEndpoint(flow.srcHost);
						SendPacketEvent sendEvent = new SendPacketEvent(time,
								nextPacket, flow.srcHost, currentDst, link);
						newEvents.add(sendEvent);
						newEvents.add(new NegAckEvent(time + flow.rtt,
								nextPacket, sendEvent));
					}
				}
			}
		} else {
			Router router = (Router) component;
			Link nextLink = router.routing.get(packet.dstHost.name);
			Component currentDst = nextLink.getAdjacentEndpoint(component);
			newEvents.add(new SendPacketEvent(time, packet, component,
					currentDst, nextLink));
		}
		return newEvents;
	}
}
