import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends Event {
	Component component;

	public ReceivePacketEvent(double time, Packet packet, Component component) {
		super(time, packet);
		this.component = component;
	}

	@Override
	// TODO: Potentially move some of this logic into other files
	// TODO: Add comment
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();

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
		
		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: ReceivePacketEvent\t\t\tDetails: Receiving packet "
				+ this.packet.flowName + "-" + this.packet.id + "-"
				+ this.packet.packetType + " at comp " + this.component.name;
	}
}
