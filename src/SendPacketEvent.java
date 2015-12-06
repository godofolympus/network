import java.util.ArrayList;
import java.util.List;

public class SendPacketEvent extends Event {
	Link link;
	Component src;
	Component dst;

	public SendPacketEvent(double time, Packet packet, Component src,
			Component dst, Link link) {
		super(time, packet);
		this.packet = packet;
		this.src = src;
		this.dst = dst;
		this.link = link;
	}

	@Override
	// TODO: Check that the two buffer code is correct
	public List<Event> handle() {

		// Declare variables for newEvents and direction
		ArrayList<Event> newEvents = new ArrayList<Event>();
		Constants.Direction dir = null;

		// If this SendPacketEvent object is being sent from the srcHost
		// then update the host sendRate. If it is also a data packet, then
		// also update the flow sendRate
		if (src.name.equals(packet.srcHost.name)) {
			Host host = (Host) src;
			Flow flow = host.currentFlows.get(packet.flowName);
			
			if (packet.packetType == Constants.PacketType.DATA) {
				host.bytesSent += Constants.PACKET_SIZE;
				flow.bytesSent += Constants.PACKET_SIZE;
			} else {
				host.bytesSent += Constants.ACK_SIZE;
			}
		}

		// Determine which direction this packet is traveling and if theres room
		// on the corresponding buffer
		if (src.equals(link.leftEndPoint)
				&& (link.currentLeftBufferAmt + packet.size <= link.bufferSize)) {
			// Place packet on the left buffer and change corresponding values
			link.leftBuffer.offer(packet);
			link.leftArrivalTimes.offer(time);
			link.currentLeftBufferAmt += packet.size;
			dir = Constants.Direction.RIGHT;

			// New BufferToLinkEvents are created by previous BufferToLinkEvent,
			// so we need to create the first BufferToLinkEvent manually
			if ((link.rightBuffer.size() + link.leftBuffer.size()) == 1) {
				newEvents.add(new BufferToLinkEvent(this.time + packet.size
						/ link.linkRate, link, packet, dir));
			}

		} else if (src.equals(link.rightEndPoint)
				&& (link.currentRightBufferAmt + packet.size <= link.bufferSize)) {
			// Place packet on the right buffer and change corresponding values
			link.rightBuffer.offer(packet);
			link.rightArrivalTimes.offer(time);
			link.currentRightBufferAmt += packet.size;
			dir = Constants.Direction.LEFT;

			// New BufferToLinkEvents are created by previous BufferToLinkEvent,
			// so we need to create the first BufferToLinkEvent manually
			if ((link.rightBuffer.size() + link.leftBuffer.size()) == 1) {
				newEvents.add(new BufferToLinkEvent(this.time + packet.size
						/ link.linkRate, link, packet, dir));
			}

		} else {
			// Handle case where packet is dropped
			link.packetsLost++;
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: SendPacketEvent\t\t\tDetails: Placing packet "
				+ this.packet.flowName + "-" + this.packet.id + "-"
				+ this.packet.packetType + " from " + this.src.name + " to "
				+ this.dst.name + " on link " + this.link.linkName
				+ "'s buffer";
	}
}
