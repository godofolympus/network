import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NegAckEvent extends Event {
	SendPacketEvent sendEvent;
	int windowId;

	public NegAckEvent(double time, Packet packet, SendPacketEvent sendEvent, int windowId) {
		super(time, packet);
		this.sendEvent = sendEvent;
		this.windowId = windowId;
	}

	@Override
	public List<Event> handle() {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		Host srcHost = (Host) sendEvent.src;
		Flow flow = srcHost.currentFlows.get(packet.flowName);
		if(this.time - sendEvent.time < flow.rtt){
			if(flow.sendingBuffer.containsKey(packet.id)){
				this.time = sendEvent.time + flow.rtt;
				newEvents.add(this);
			}
			return newEvents;
		}
		// If packet is still in the sending buffer then an ack packet has not
		// been received, and so re-send the packet.
		if (flow.sendingBuffer.containsKey(packet.id)) {
			if(windowId < flow.windowFailed){
				System.out.println("Window Already Failed");
				return newEvents;
			}
			System.out.println("Ack Failed for Packet " + packet.id + ", Adding New Packets, Window Size: " + flow.windowSize + ", Time: " + time);
			// Handle a missed ack packet depending on the tcp algorithm
			switch(flow.tcp) {
			case TAHOE:
				flow.slowStartThresh = Math.max(1, (int)flow.windowSize/2);
				flow.windowSize = 1.0;
				flow.dupPacketId = -1;
				flow.dupPacketCount = 0;
				flow.fastRetransmit = false;
				break;
			case RENO:
				break;
			case FAST:
				break;
			}
			flow.windowSizeSum += flow.windowSize;
			flow.windowChangedCount++;
			flow.windowFailed += 1;
			flow.sendingBuffer.clear();
			for(int packetId = flow.minUnacknowledgedPacketSender; packetId < Math.min(flow.totalPackets, flow.minUnacknowledgedPacketSender+Math.floor(flow.windowSize)); packetId++){
				Packet nextPacket = new Packet(packetId,
						Constants.PacketType.DATA, Constants.PACKET_SIZE,
						flow.srcHost, flow.dstHost, flow.flowName);
				//System.out.println("New DATA Packet " + nextPacket.id);
				flow.sendingBuffer.put(nextPacket.id, nextPacket);
				Link link = flow.srcHost.links.values().iterator().next();
				Component currentDst = link
						.getAdjacentEndpoint(flow.srcHost);
				//System.out.println("Sending Packet " + nextPacket.id);
				SendPacketEvent sendEvent = new SendPacketEvent(time,
						nextPacket, flow.srcHost, currentDst, link);
				newEvents.add(sendEvent);
				newEvents.add(new NegAckEvent(time + flow.rtt, nextPacket, sendEvent, flow.windowFailed));
			}
			//System.out.println("Max Packet Id " + flow.maxPacketId);
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	public String toString() {
		return super.toString()
				+ "\t\t\tEvent Type: NegAckEvent\t\t\t\tDetails: Check that ACK for packet "
				+ this.packet.flowName + "-" + this.packet.id
				+ " arrived at host " + packet.dstHost.name + " before one rtt";
	}
}
