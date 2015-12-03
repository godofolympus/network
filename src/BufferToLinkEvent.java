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
		System.out.println("Buffer to Link Event at Link " + link.linkName
				+ ": Packet " + packet.id + ", " + "Time: " + time);
		link.packets.poll();
		link.directions.poll();
		// Add ReceivePacketEvent based on direction
		if (direction == Constants.Direction.RIGHT) {
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.rightEndPoint));
		} else {
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay,
					packet, link.leftEndPoint));
		}
		// Add BufferToLinkEvent for next packet in buffer
		if (link.packets.size() > 0) {
			Packet nextPacket = link.packets.peek();
			Constants.Direction nextDir = link.directions.peek();
			newEvents.add(new BufferToLinkEvent(
					this.time + nextPacket.size / link.linkRate, link,
					nextPacket, nextDir));
		}
		link.currentBufferAmt -= packet.size;
		return newEvents;
	}
}
