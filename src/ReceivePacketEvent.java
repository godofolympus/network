import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends Event {
	Packet packet;
	Component component;

	public ReceivePacketEvent(double time, Packet packet, Component component) {
		super(time);
		this.packet = packet;
		this.component = component;
	}

	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		System.out.println(
				"Recieved Packet " + packet.id + " to " + packet.dstHost.name
						+ " at " + component.name + ", Time: " + time);
		if (component.name.equals(packet.dstHost.name)) {
			Host host = (Host) component;
			if (packet.packetType == Constants.PacketType.DATA) {
				System.out.println("New ACK Packet to " + packet.id);
				Packet ackPacket = new Packet(packet.id,
						Constants.PacketType.ACK, Constants.ACK_SIZE, host,
						packet.srcHost, packet.flowName);
				Link link = host.links.values().iterator().next();
				Component currentDst = link.getAdjacentEndpoint(host);
				newEvents.add(new SendPacketEvent(time, ackPacket, host,
						currentDst, link));
			} else {
				Flow flow = host.currentFlows.get(packet.flowName);
				flow.outgoingPackets.remove(packet.id);
				flow.currentPackets++;
				if (flow.currentPackets < flow.totalPackets
						&& flow.outgoingPackets.size() < flow.windowSize) {
					Packet nextPacket = new Packet("" + flow.maxPacketId,
							Constants.PacketType.DATA, Constants.PACKET_SIZE,
							flow.srcHost, flow.dstHost, flow.flowName);
					System.out.println("New Packet " + nextPacket.id);
					flow.maxPacketId++;
					flow.outgoingPackets.put(nextPacket.id, nextPacket);
					Link link = flow.srcHost.links.values().iterator().next();
					Component currentDst = link
							.getAdjacentEndpoint(flow.srcHost);
					newEvents.add(new SendPacketEvent(time, nextPacket,
							flow.srcHost, currentDst, link));
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
