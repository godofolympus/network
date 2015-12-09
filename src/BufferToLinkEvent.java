import java.util.ArrayList;
import java.util.List;

public class BufferToLinkEvent extends Event {
	Link link;
	Constants.Direction direction;

	public BufferToLinkEvent(double time, Link link, Packet packet,
			Constants.Direction direction) {
		super(time, packet);
		this.link = link;
		this.direction = direction;
	}

	@Override
	public List<Event> handle() {

		ArrayList<Event> newEvents = new ArrayList<Event>();
		Packet nextPacket = null;

		link.bytesSent += packet.size;
		link.bytesTime += packet.size/link.linkRate;
		
		// Handle transfer of the packet depending on direction
		if (direction == Constants.Direction.RIGHT) {
			link.leftBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.rightEndPoint, link));
			link.currentLeftBufferAmt -= packet.size;

			// If there are more packets on the left buffer, schedule them for
			// transit across the link
			if ((nextPacket = link.leftBuffer.peek()) != null) {
				newEvents.add(new BufferToLinkEvent(this.time + nextPacket.size
						/ link.linkRate, link, nextPacket,
						Constants.Direction.RIGHT));
			}
		} else {
			link.rightBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.leftEndPoint, link));
			link.currentRightBufferAmt -= packet.size;

			// If there are more packets on the right buffer, schedule them for
			// transit across the link
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
