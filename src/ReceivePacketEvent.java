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
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Packet has been received, so increment packetsSent in link
		// TODO: Is this the proper way to measure linkRate?
		// Should we instead average sendRate and receiveRate for a link? 
		if (packet.packetType == Constants.PacketType.DATA) {
			link.bytesSent += Constants.PACKET_SIZE;
		} else if (packet.packetType == Constants.PacketType.ACK) {
			link.bytesSent += Constants.ACK_SIZE;
		} else {
			// TODO: Handle other cases
		}

		// Handle a packet according to whether it is received at a host 
		// or at a router
		if (component.name.equals(packet.dstHost.name)) {
			// Packet has reached destination host so count it towards the 
			// flows receiveRate
			Host host = (Host) component;
			Flow flow = host.currentFlows.get(packet.flowName);
			
			if (packet.packetType == Constants.PacketType.DATA) {
				host.bytesReceived += Constants.PACKET_SIZE;
				flow.bytesReceived += Constants.PACKET_SIZE;
			} else if (packet.packetType == Constants.PacketType.ACK) {
				// TODO: There is a bug here because flow.sendingTimes.get(id) may not
				// always contain the packetId we want since the code that handles 
				// ack packets in Host deletes from the sendingTimes HashMap when
				// it receives an ack packet.
				host.bytesReceived += Constants.ACK_SIZE;
				flow.rttSum += time - packet.dataSendingTime;
				flow.acksReceived++;
			} else {
				// TODO: Handle other cases
			}

			// Handle the package appropriately
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
