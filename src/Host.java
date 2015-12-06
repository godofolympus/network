import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Host extends Component {
	HashMap<String, Flow> currentFlows = new HashMap<String, Flow>();

	// Variables for data collection
	int bytesSent = 0;
	int bytesReceived = 0;

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
			if (packet.id == flow.minUnacknowledgedPacketReceiver) {
				// Schedule acknowledgment packet to send back to src
				// System.out.println("New ACK Packet for Packet " + packet.id);
				flow.minUnacknowledgedPacketReceiver++;
				Packet ackPacket = new Packet(packet.id,
						Constants.PacketType.ACK, Constants.ACK_SIZE, this,
						packet.srcHost, packet.flowName);
				ackPacket.negPacketId = flow.minUnacknowledgedPacketReceiver;
				ackPacket.dataSendingTime = packet.dataSendingTime;
				Link link = this.links.values().iterator().next();
				Component currentDst = link.getAdjacentEndpoint(this);
				newEvents.add(new SendPacketEvent(time, ackPacket, this,
						currentDst, link));
			}
		} else if (packet.packetType == Constants.PacketType.ACK) {
			// Remove packet from sending buffer if received ack packet.
			// Update RTT time based on the time it took to arrive
			// flow.rtt = 1.1*(0.5 * (time - packet.dataSendingTime)
			// + 0.5 * flow.rtt + 0.0001);
			flow.rtt = (time - packet.dataSendingTime + 0.0001) * 1.1;

			// Update sending buffer window.
			//flow.currentPackets++;
			int nextUnacknowledgedPacket = packet.negPacketId;
			boolean minChanged = flow.minUnacknowledgedPacketSender < nextUnacknowledgedPacket;
			while (flow.minUnacknowledgedPacketSender < nextUnacknowledgedPacket) {
				flow.currentPackets++;
				flow.minUnacknowledgedPacketSender++;
			}

			// if (flow.currentPackets < flow.totalPackets)
			// System.out.println("Next Unacknowledged Packet: Packet "
			// + flow.minUnacknowledgedPacketSender);

			// Schedule new data packets to fill up window.
			if (minChanged && flow.currentPackets < flow.totalPackets) {
				flow.maxPacketId = flow.minUnacknowledgedPacketReceiver;
				int windowInt = flow.maxPacketId;
				while (flow.sendingBuffer.size() < Math.floor(flow.windowSize)) {
					Packet nextPacket = new Packet(flow.maxPacketId,
							Constants.PacketType.DATA, Constants.PACKET_SIZE,
							flow.srcHost, flow.dstHost, flow.flowName);
					// System.out.println("New DATA Packet " + nextPacket.id);
					flow.maxPacketId++;
					flow.sendingBuffer.put(nextPacket.id, nextPacket);
					Link link = flow.srcHost.links.values().iterator().next();
					Component currentDst = link
							.getAdjacentEndpoint(flow.srcHost);
					SendPacketEvent sendEvent = new SendPacketEvent(time,
							nextPacket, flow.srcHost, currentDst, link);
					newEvents.add(sendEvent);
					newEvents.add(new NegAckEvent(time + flow.rtt, nextPacket, sendEvent, flow.flowName + "," + windowInt));
				}
			}
		} else {
			// TODO: Handle other data packets
		}

		return newEvents;
	}

	public int hashCode() {
		return this.name.hashCode();
	}
}
