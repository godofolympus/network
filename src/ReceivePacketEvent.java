import java.util.ArrayList;
import java.util.List;

public class ReceivePacketEvent extends Event {
	Component component;
	Link link;

	public ReceivePacketEvent(double time, Packet packet, Component component,
			Link link) {
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

				// Data packet received at host. Adjust data collection
				// variables
				host.bytesReceived += Constants.PACKET_SIZE;
				flow.bytesReceived += Constants.PACKET_SIZE;
			} else if (packet.packetType == Constants.PacketType.ACK) {

				// Ack packet received at host. Adjust data collection variables
				host.bytesReceived += Constants.ACK_SIZE;
				flow.rttSum += time - packet.dataSendingTime;
				flow.acksReceived++;

				// Adjust the window size depending on TCP congestion algorithm
				switch (flow.tcp) {
				case TAHOE:
					// Only increment windowSize if this is a new ACK packet
					if (packet.id >= flow.minUnacknowledgedPacketSender
							&& flow.sendingBuffer.containsKey(packet.id)) {
						
						// Adjust window size depending on the phase we are in
						if (flow.windowSize < flow.slowStartThresh) {
							flow.windowSize++;
						} else {
							flow.windowSize += 1.0 / flow.windowSize;
						}
						
						flow.windowSizeSum += flow.windowSize;
						flow.windowChangedCount++;
					}
					break;
				case RENO:
					break;
				case FAST:
					break;
				}
				

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
