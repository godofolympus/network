import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Host extends Component {
	HashMap<String, Flow> currentFlows = new HashMap<String, Flow>();

	public Host(String name) {
		super(name);
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.name.equals(((Host) obj).name);
		}
	}
	
	public List<Event> receivePacket(double time, Packet packet) {
		List<Event> newEvents = new ArrayList<Event>();
		Flow flow = this.currentFlows.get(packet.flowName);
		
		// Handle packet depending on what the packetType is
		if (packet.packetType == Constants.PacketType.DATA) {
			// If data packet then add packet into receiving buffer.
			flow.receivingBuffer.put(packet.id, packet);
			
			// Update receiving buffer window.
			while (flow.receivingBuffer
					.containsKey(flow.minUnacknowledgedPacketReceiver)) {
				flow.minUnacknowledgedPacketReceiver++;
			}
			
			// Schedule acknowledgment packet to send back to src
			//System.out.println("New ACK Packet for Packet " + packet.id);
			Packet ackPacket = new Packet(packet.id,
					Constants.PacketType.ACK, Constants.ACK_SIZE, this,
					packet.srcHost, packet.flowName);
			ackPacket.negPacketId = flow.minUnacknowledgedPacketReceiver;
			Link link = this.links.values().iterator().next();
			Component currentDst = link.getAdjacentEndpoint(this);
			newEvents.add(new SendPacketEvent(time, ackPacket, this,
					currentDst, link));
			
		} else if (packet.packetType == Constants.PacketType.ACK) {
			// Remove packet from sending buffer if received ack packet.
			// Update RTT time based on the time it took to arrive
			if (flow.sendingBuffer.containsKey(packet.id)) {
				flow.sendingBuffer.remove(packet.id);
				flow.rtt = 0.5 * (time - flow.sendingTimes.get(packet.id))
						+ 0.5 * flow.rtt + 0.0001;
				flow.sendingTimes.remove(packet.id);
			}
			
			// Update sending buffer window.
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
			
			// Schedule new data packets to fill up window.
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
		} else {
			//TODO: Handle other data packets
		}
		
		return newEvents;
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
}
