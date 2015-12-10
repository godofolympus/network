import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles the logic of receiving a packet at a network component
 */
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
		
		// Collect data for packet leaving the link
		link.bytesSent -= packet.size;
		link.bytesTime -= packet.size / link.linkRate;

		// Packet has been received, so update the congestion metric of the link
		if (component == link.rightEndPoint) {
			link.totalRightDelay = time - packet.linkArrivalTime;
		} else {
			link.totalLeftDelay = time - packet.linkArrivalTime;
		}

		// Handle a packet according to whether it is received at a host
		// or at a router
		if ((packet.dstHost != null) && (component.name.equals(packet.dstHost.name))) {
			// Checking that packet.dstHost is not null automatically checks
			// that packet.packetType is not ROUTING

			Host host = (Host) component;
			Flow flow = host.currentFlows.get(packet.flowName);

			// Packet has reached destination host so count it towards the
			// flow's receiveRate
			host.bytesReceived += packet.size;
			if (packet.packetType == Constants.PacketType.DATA) {
				// Data packet received at host
				flow.bytesReceived += Constants.DATA_PACKET_SIZE;
				
			} else if (packet.packetType == Constants.PacketType.ACK) {
				
				// Ack packet received at host
				flow.rttSum += time - packet.dataSendingTime;
				flow.acksReceived++;

				// Adjust the window size depending on TCP congestion algorithm
				switch (flow.tcp) {
				case RENO:
					// This code block handles duplicate packets and entering/exiting 
					// Fast Recovery
					if (!flow.fastRecovery) {
						// If we are not currently in Fast Recovery mode, we
						// keep track of duplicate acks
						if (packet.nextPacketId == flow.dupPacketId
								&& packet.id > packet.nextPacketId) {
							// If this is duplicate ack, we increment the count
							flow.dupPacketCount++;
						} else {
							// This is a new ack packet, so we reset the
							// duplicate value and counts
							flow.dupPacketId = packet.nextPacketId;
							flow.dupPacketCount = 0;
						}

						// If we now have three duplicate ack, we enter Fast Recovery
						if (flow.dupPacketCount == 3) {
							// Adjust the slow start threshold and the new window size
							flow.slowStartThresh = Math.max(
									(int) flow.windowSize / 2, 2);
							flow.windowSize = flow.slowStartThresh + 3;
							flow.fastRecovery = true;

							// Fast Retransmit of the lost packet
							Packet minPacket = new Packet(packet.nextPacketId,
									Constants.PacketType.DATA,
									Constants.DATA_PACKET_SIZE, flow.srcHost,
									flow.dstHost, flow.flowName);
							Link link = flow.srcHost.links.values().iterator()
									.next();
							Component currentDst = link
									.getAdjacentEndpoint(flow.srcHost);
							SendPacketEvent sendEvent = new SendPacketEvent(
									time, minPacket, flow.srcHost, currentDst,
									link);
							newEvents.add(sendEvent);
							newEvents.add(new NegAckEvent(time + flow.timeout,
									minPacket, sendEvent, flow.windowFailed));
						}
					} else {
						// If we are are currently in Fast Recovery
						if (flow.dupPacketId == packet.nextPacketId) {
							// If we get a duplicate packet, we infer the successful arrival
							// of a previously sent packet, so we increment window size
							flow.windowSize++;
						} else {
							// We received a new ack packet so we can exit Fast
							// Recovery
							flow.windowSize = flow.slowStartThresh;
							flow.dupPacketId = -1;
							flow.dupPacketCount = 0;
							flow.fastRecovery = false;
						}
					}

					// This code block handles normal execution for Slow Start
					// and Congestion Avoidance
					// Only executes if the ack is a new ack packet
					if (!flow.fastRecovery
							&& packet.nextPacketId > flow.minUnacknowledgedPacketSender) {

						// Adjust window size depending on the phase we are in
						if (flow.windowSize < flow.slowStartThresh) {
							// Slow Start
							flow.windowSize++;

						} else {
							// Congestion Avoidance
							flow.windowSize += 1.0 / flow.windowSize;
						}
					}

					// End TCP Reno
					break;
				case FAST:
					// Do nothing for TCP FAST
					break;
				}
			}

			// Handle the package appropriately
			newEvents.addAll(host.receivePacket(this.time, this.packet));
			
			if (flow.minUnacknowledgedPacketSender == flow.totalPackets) {
				// Flow has finished transmitting
				flow.flowFinished = true;
				flow.windowSize = 0.0;
			}

		} else {
			// Packet received at a router
			Router router = (Router) component;

			// Handle depending on packet type
			if (packet.packetType == Constants.PacketType.ROUTING) {

				// Routing packet
				List<Event> events = handleRouterRouting(router, packet, link);
				if (events != null)
					newEvents.addAll(events);

			} else if (packet.packetType == Constants.PacketType.DATA
					|| packet.packetType == Constants.PacketType.ACK) {

				// Data or ack packet
				newEvents.add(handleRouterDataAck(router, packet));

			}
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

	/**
	 * This function handles the logic of a router receiving a routing 
	 * packet by running the update step of the Bellman Ford algorithm. 
	 * If changes are made to the router's distances, we send additional
	 * routing packets to adjacent routers
	 */
	private List<Event> handleRouterRouting(Router r, Packet p, Link l) {
		boolean distancesChanged = false;
		
		// Iterate over all components with distances in this distance map
		RoutingPacket rp = (RoutingPacket) p;
		for (String componentName : rp.newDistances.keySet()) {
			
			// Calculate new distance to this component
			double newDist = rp.newDistances.get(componentName)
					+ l.totalLeftDelay + l.totalRightDelay;
			
			// Dynamic programming step to update distance
			if (newDist < r.distances.get(componentName)) {
				r.distances.put(componentName, newDist);
				r.routingTableCopy.put(componentName, l);
				distancesChanged = true;
			}
		}
		
		if (distancesChanged) {
			return sendRoutingInfo(r);
		} else {
			return null;
		}
	}

	/**
	 * This function handles the logic of a router receiving a data or 
	 * ack packet. We simply send it along the appropriate link
	 */
	private Event handleRouterDataAck(Router r, Packet p) {
		Link nextLink = r.routingTable.get(p.dstHost.name);
		Component nextDst = nextLink.getAdjacentEndpoint(r);
		return new SendPacketEvent(time, p, r, nextDst, nextLink);
	}
	
	/**
	 * This function sends updated distance info to all adjacent routers
	 */
	private List<Event> sendRoutingInfo(Router r) {
		ArrayList<Event> newEvents = new ArrayList<Event>();

		// Send the distance information of this router to its neighbors
		for (Link link : r.links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(r);

			// Only send info to routers
			if (adjComponent instanceof Router) {
				RoutingPacket routingPacket = new RoutingPacket(
						Constants.PacketType.ROUTING,
						Constants.ROUTING_PACKET_SIZE, r,
						(Router) adjComponent, r.distances);
				SendPacketEvent sendPacketEvent = new SendPacketEvent(
						this.time, routingPacket, r, adjComponent, link);
				newEvents.add(sendPacketEvent);
			}
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
