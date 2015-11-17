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
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		System.out.println("Sending " + packet.packetType + " Packet "
				+ packet.id + " from " + src.name + " to " + packet.dstHost
				+ ", Time: " + time);
		if (link.currentBufferAmt < link.bufferSize - packet.size) {
			Constants.Direction dir = null;
			if (src.equals(link.leftEndPoint)) {
				link.packets.offer(packet);
				dir = Constants.Direction.RIGHT;
				link.directions.offer(dir);
			} else {
				link.packets.offer(packet);
				dir = Constants.Direction.LEFT;
				link.directions.offer(dir);
			}
			if (link.packets.size() == 1) {
				newEvents.add(new BufferToLinkEvent(
						this.time + packet.size / link.linkRate, link, packet,
						dir));
			}
			link.currentBufferAmt += packet.size;
		}
		return newEvents;
	}
}
