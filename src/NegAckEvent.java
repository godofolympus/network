import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles the logic of RTT timeouts for packets that have not been
 * acknowledged.
 */
public class NegAckEvent extends Event {
	SendPacketEvent sendEvent;
	int windowId;

	public NegAckEvent(double time, Packet packet, SendPacketEvent sendEvent,
			int windowId) {
		super(time, packet);
		this.sendEvent = sendEvent;
		this.windowId = windowId;
	}

	/**
	 * This function handles the event of a packet not receiving an ACK by the
	 * timeout period. It automatically reschedules the event based on the
	 * current RTT estimate and dismisses the event if unnecessary.
	 */
	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Retrieve the host and flow for this NegAckEvent
		Host srcHost = (Host) sendEvent.src;
		Flow flow = srcHost.currentFlows.get(packet.flowName);

		// Check that this NegAckEvent executes at the proper time,
		// taking into consideration our most recent flow rtt
		if (this.time - sendEvent.time < flow.timeout) {
			if (flow.sendingBuffer.containsKey(packet.id)) {
				// Reschedule this event if the packet is still in the network
				this.time = sendEvent.time + flow.timeout;
				newEvents.add(this);
			}
			return newEvents;
		}

		// If packet is still in the sending buffer then the packet is lost
		if (flow.sendingBuffer.containsKey(packet.id)) {
			// If another packet in the window has already timed-out, we can
			// dismiss this NegAckEvent since all packets in the window will
			// already be resent properly
			if (windowId < flow.windowFailed) {
				return newEvents;
			}

			// Handle a missed ACK packet depending on the TCP algorithm
			switch (flow.tcp) {
			case RENO:
				// If we miss an ACK, then enter Slow Start
				flow.slowStartThresh = Math.max(1, (int) flow.windowSize / 2);
				flow.windowSize = 1.0;
				flow.dupPacketId = -1;
				flow.dupPacketCount = 0;
				flow.fastRecovery = false;
				break;
			case FAST:
				break;
			}

			// In Go-Back-N, we resend the entire window if an ACK is missed. We
			// update windowFailed to dismiss future NegAckEvents for the
			// current window.
			flow.windowFailed += 1;
			flow.sendingBuffer.clear();

			// Resend the current window with the new window size
			for (int packetId = flow.minUnacknowledgedPacketSender; packetId < Math
					.min(flow.totalPackets, flow.minUnacknowledgedPacketSender
							+ Math.floor(flow.windowSize)); packetId++) {
				// New packet to be sent
				Packet nextPacket = new Packet(packetId,
						Constants.PacketType.DATA, Constants.DATA_PACKET_SIZE,
						flow.srcHost, flow.dstHost, flow.flowName);

				// Code to send this packet
				flow.sendingBuffer.put(nextPacket.id, nextPacket);
				Link link = flow.srcHost.links.values().iterator().next();
				Component currentDst = link.getAdjacentEndpoint(flow.srcHost);
				SendPacketEvent sendEvent = new SendPacketEvent(time,
						nextPacket, flow.srcHost, currentDst, link);
				newEvents.add(sendEvent);
				newEvents.add(new NegAckEvent(time + flow.timeout, nextPacket,
						sendEvent, flow.windowFailed));
			}
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
