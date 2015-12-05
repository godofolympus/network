import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends Event {
	Component component;
	Link link;

	public ReceivePacketEvent(double time, Packet packet, Component component, Link link) {
		super(time, packet);
		this.component = component;
		this.link = link;
	}

	@Override
	// TODO: Add comment
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Packet has been received, so increment packetsSent in link
		if (packet.packetType == Constants.PacketType.DATA) {
			link.bytesSent += Constants.PACKET_SIZE;
		} else if (packet.packetType == Constants.PacketType.ACK) {
			link.bytesSent += Constants.ACK_SIZE;
		} else {
			// TODO: Handle other cases
		}

		if (component.name.equals(packet.dstHost.name)) {
			// Packet has reached destination host
			Host host = (Host) component;
			newEvents.addAll(host.receivePacket(this.time, this.packet));

		} else {
			// Packet received at a router
			Router router = (Router) component;
			Event nextEvent = router.receivePacket(this.time, this.packet);
			newEvents.add(nextEvent);
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: ReceivePacketEvent\t\t\tDetails: Receiving packet "
				+ this.packet.flowName + "-" + this.packet.id + "-"
				+ this.packet.packetType + " at comp " + this.component.name;
	}
}
