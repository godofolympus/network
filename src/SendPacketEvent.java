import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the logic of sending a packet between two 
 * adjacent Component objects.
 */
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

	/**
	 * This function takes a packet that is being sent from Component src
	 * to Component dst and places it on the correct side of the link buffer.
	 * It also collects some data if appropriate 
	 */
	@Override
	public List<Event> handle() {

		// Declare variables for newEvents and direction
		ArrayList<Event> newEvents = new ArrayList<Event>();
		Constants.Direction dir = null;
		
		// Some data collection code
		if (packet.packetType == Constants.PacketType.DATA || packet.packetType == Constants.PacketType.ACK) {
			// We only collect this data for data and ack packets
			if (src.name.equals(packet.srcHost.name)) {
				// If we are sending this packet from the srcHost, we increase
				// the total bytes sent by the host
				Host host = (Host) src;
				Flow flow = host.currentFlows.get(packet.flowName);
				host.bytesSent += packet.size;
				
				if (packet.packetType == Constants.PacketType.DATA) {
					// This time value is used in our RTT calculations later on
					packet.dataSendingTime = time;
					
					// For data packets, we also increase the total bytes
					// sent by the host
					flow.bytesSent += packet.size;
				}
			}
		}
		

		// Determine which direction this packet is traveling and if there is
		//  room on the corresponding buffer
		if (src.equals(link.leftEndPoint)
				&& (link.currentLeftBufferAmt + packet.size <= link.bufferSize)) {
			// Place packet on the left buffer and change corresponding values
			link.leftBuffer.offer(packet);
			packet.linkArrivalTime = time;
			link.currentLeftBufferAmt += packet.size;
			dir = Constants.Direction.RIGHT;

			// New BufferToLinkEvents are created by previous BufferToLinkEvent,
			// so we need to create the first BufferToLinkEvent manually
			if ( link.leftBuffer.size() == 1) {
				newEvents.add(new BufferToLinkEvent(this.time + packet.size
						/ link.linkRate, link, packet, dir));
			}

		} else if (src.equals(link.rightEndPoint)
				&& (link.currentRightBufferAmt + packet.size <= link.bufferSize)) {
			// Place packet on the right buffer and change corresponding values
			link.rightBuffer.offer(packet);
			packet.linkArrivalTime = time;
			link.currentRightBufferAmt += packet.size;
			dir = Constants.Direction.LEFT;

			// New BufferToLinkEvents are created by previous BufferToLinkEvent,
			// so we need to create the first BufferToLinkEvent manually
			if (link.rightBuffer.size() == 1) {
				newEvents.add(new BufferToLinkEvent(this.time + packet.size
						/ link.linkRate, link, packet, dir));
			}

		} else {
			// The packet is dropped, so update this value
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
