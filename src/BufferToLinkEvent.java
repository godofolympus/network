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
	// TODO: Does this consider link capacity at all?
	// TODO: Is this half-duplex or full-duplex?
	public List<Event> handle() {
		
		ArrayList<Event> newEvents = new ArrayList<Event>();
		
		// Handle transfer of the packet depending on direction
		if (direction == Constants.Direction.RIGHT){
			link.leftBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay, packet, link.rightEndPoint));
			link.currentLeftBufferAmt -= packet.size;
		} else {
			link.rightBuffer.poll();
			newEvents.add(new ReceivePacketEvent(this.time + link.linkDelay, packet, link.leftEndPoint));
			link.currentRightBufferAmt -= packet.size;
		}
		
		// If at least one packet is waiting to be sent, schedule the next BufferToLinkEvent
		if (!(link.leftBuffer.isEmpty() && link.rightBuffer.isEmpty())) {
			Constants.Direction nextDir;
			Packet nextPacket;
			
			if (link.leftBuffer.isEmpty()) {
				// Left buffer is empty, choose the packet on the right
				nextDir = Constants.Direction.LEFT;
				nextPacket = link.rightBuffer.peek();
			} else if (link.rightBuffer.isEmpty()) {
				// Right buffer is empty, choose the packet on the left
				nextDir = Constants.Direction.RIGHT;
				nextPacket = link.leftBuffer.peek();
			} else {
				// Both buffers have elements, choose the packet that arrived first
				double leftTime = link.leftArrivalTimes.peek();
				double rightTime = link.rightArrivalTimes.peek();
				
				if (leftTime < rightTime) {
					nextDir = Constants.Direction.RIGHT;
					nextPacket = link.leftBuffer.peek();
				} else {
					nextDir = Constants.Direction.LEFT;
					nextPacket = link.rightBuffer.peek();
				}
			}
			
			// Schedule the next BufferToLinkEvent based on the previous info
			newEvents.add(new BufferToLinkEvent(this.time + nextPacket.size / link.linkRate, link, nextPacket, nextDir));
		}
		
		return newEvents;
	}
	
	public String toString() {
		if (this.direction == Constants.Direction.RIGHT) {
			return super.toString() + "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.id + " from comp " + this.link.leftEndPoint.name + " to comp "
					+ this.link.rightEndPoint.name + " over link " + this.link.linkName;
		} else {
			return super.toString() + "\t\t\tEvent Type: BufferToLinkEvent\t\t\tDetails: Sending packet "
					+ this.packet.id + " from comp " + this.link.rightEndPoint.name + " to comp "
					+ this.link.leftEndPoint.name + " over link " + this.link.linkName;
		}
	}
}
