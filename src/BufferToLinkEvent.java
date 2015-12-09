import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles the logic of transferring a packet at the front of
 * the link buffer over to the other end of the link
 */
public class BufferToLinkEvent extends Event {
	Link link;
	Constants.Direction direction;

	public BufferToLinkEvent(double time, Link link, Packet packet,
			Constants.Direction direction) {
		super(time, packet);
		this.link = link;
		this.direction = direction;
	}

	/**
	 * This function takes a packet at the front of a link buffer, removes it
	 * from the buffer, and 'places' it onto the actual link. A ReceievePacketEvent
	 * is scheduled when we expect the packet to arrive at the other end of the link.
	 * 
	 * If there is at least one packet going in the same direction and waiting
	 * to be sent, we schedule the next BufferToLinkEvent. 
	 */
	@Override
	public List<Event> handle() {

		ArrayList<Event> newEvents = new ArrayList<Event>();
		Packet nextPacket = null;

		// We collect this data for all packet types
		link.bytesSent += packet.size;
		link.bytesTime += packet.size/link.linkRate;
		
		// Handle transfer of the packet depending on direction
		if (direction == Constants.Direction.RIGHT) {
			// Remove the packet from the buffer
			link.leftBuffer.poll();
			link.currentLeftBufferAmt -= packet.size;
			
			// Schedule the arrival after a propagation delay
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.rightEndPoint, link));

			// If there are more packets on the left buffer, schedule them for
			// transit across the link after a transmission delay
			if ((nextPacket = link.leftBuffer.peek()) != null) {
				newEvents.add(new BufferToLinkEvent(this.time + nextPacket.size
						/ link.linkRate, link, nextPacket,
						Constants.Direction.RIGHT));
			}
		} else {
			// Remove the packet from the buffer
			link.rightBuffer.poll();
			link.currentRightBufferAmt -= packet.size;
			
			// Schedule the arrival after a propagation delay
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.leftEndPoint, link));

			// If there are more packets on the right buffer, schedule them for
			// transit across the link after a transmission delay
			if ((nextPacket = link.rightBuffer.peek()) != null) {
				newEvents.add(new BufferToLinkEvent(this.time + nextPacket.size
						/ link.linkRate, link, nextPacket,
						Constants.Direction.LEFT));
			}
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		// The string returned depends on the direction of this BufferToLinkEvent
		if (this.direction == Constants.Direction.RIGHT) {
			return super.toString()
					+ "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.flowName + "-" + this.packet.id + "-"
					+ this.packet.packetType + " from "
					+ this.link.leftEndPoint.name + " to "
					+ this.link.rightEndPoint.name + " over link "
					+ this.link.linkName;
		} else {
			return super.toString()
					+ "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.flowName + "-" + this.packet.id + "-"
					+ this.packet.packetType + " from "
					+ this.link.rightEndPoint.name + " to "
					+ this.link.leftEndPoint.name + " over link "
					+ this.link.linkName;
		}
	}
}
