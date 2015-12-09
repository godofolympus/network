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
		/*
		 * if (packet.packetType == Constants.PacketType.DATA) { link.bytesSent
		 * += Constants.PACKET_SIZE; } else if (packet.packetType ==
		 * Constants.PacketType.ACK) { link.bytesSent += Constants.ACK_SIZE; }
		 * else { // TODO: Handle other cases }
		 */
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
		// TODO: Should check packet type before we check srcHost/dstHost values 
		// since in RoutingPacket, these values can be null
		if ((packet.dstHost != null) && (component.name.equals(packet.dstHost.name))) {
			// Packet has reached destination host so count it towards the
			// flows receiveRate
			Host host = (Host) component;
			Flow flow = host.currentFlows.get(packet.flowName);

			if (packet.packetType == Constants.PacketType.DATA) {

				// Data packet received at host. Adjust data collection
				// variables
				host.bytesReceived += Constants.DATA_PACKET_SIZE;
				flow.bytesReceived += Constants.DATA_PACKET_SIZE;
			} else if (packet.packetType == Constants.PacketType.ACK) {

				// Ack packet received at host. Adjust data collection variables
				host.bytesReceived += Constants.ACK_PACKET_SIZE;
				flow.rttSum += time - packet.dataSendingTime;
				flow.acksReceived++;

				// Adjust the window size depending on TCP congestion algorithm
				switch (flow.tcp) {
				case RENO:
					// This code block handles duplicate packets and
					// entering/exiting Fast Recovery
					if (!flow.fastRecovery) {
						// If we are not currently in Fast Recovery mode, we
						// keep track of duplicate ACKS
						if (packet.negPacketId == flow.dupPacketId
								&& flow.windowSize >= flow.slowStartThresh) {
							// If this is duplicate ACK, we increment the count
							flow.dupPacketCount++;
						} else {
							// This is a new ACK packet, so we reset the
							// duplicate value and counts
							flow.dupPacketId = packet.negPacketId;
							flow.dupPacketCount = 0;
						}

						// If we now have three duplicate ACKs, we enter Fast
						// Recovery
						if (flow.dupPacketCount == 3) {
							// System.out.println("Fast Retransmit for Packet" +
							// packet.negPacketId);
							// System.out.println(flow.windowSize + "," +
							// flow.slowStartThresh);

							// Adjust the slow start threshold and the new
							// window size for
							flow.slowStartThresh = Math.max(
									(int) flow.windowSize / 2, 2);
							flow.windowSize = flow.slowStartThresh + 3;
							flow.fastRecovery = true;

							// Fast Retransmit of the lost packet
							Packet minPacket = new Packet(packet.negPacketId,
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
							newEvents.add(new NegAckEvent(time + flow.rtt,
									minPacket, sendEvent, flow.windowFailed));
						}
					} else {
						// If we are are currently in Fast Recovery
						if (flow.dupPacketId == packet.negPacketId) {
							// If we get a duplicate packet, we infer the
							// successful arrival
							// of a previously sent packet, so we increment
							// window size
							flow.windowSize++;
						} else {
							// We received a new ACK so we can exit Fast
							// Recovery
							flow.windowSize = flow.slowStartThresh;
							flow.dupPacketId = -1;
							flow.dupPacketCount = 0;
							flow.fastRecovery = false;
						}
					}

					// This code block handles normal execution for Slow Start
					// and Congestion Avoidance
					// Only executes if the ACK is a new ACK packet
					if (!flow.fastRecovery
							&& packet.negPacketId > flow.minUnacknowledgedPacketSender) {

						// Adjust window size depending on the phase we are in
						if (flow.windowSize < flow.slowStartThresh) {
							// Slow Start
							flow.windowSize++;

						} else {
							// Congestion Avoidance
							flow.windowSize += 1.0 / flow.windowSize;
						}
					}

					// End Reno
					break;
				case FAST:
					// Adjust window size depending on the phase we are in
					// if (flow.windowSize < flow.slowStartThresh) {
					// Slow Start
					// flow.windowSize++;
					// }
					// break;
				}
			} else {
				// TODO: Print statement for debugging remove later
				System.out.println("OTHER PACKET RECEIVED");
			}

			// Handle the package appropriately
			newEvents.addAll(host.receivePacket(this.time, this.packet));
			
			// Flow has finished transmitting
			if (flow.minUnacknowledgedPacketSender == flow.totalPackets) {
				flow.flowFinished = true;
			}

		} else {
			// Packet received at a router
			Router router = (Router) component;
			// Event nextEvent = router.receivePacket(this.time, this.packet);
			// newEvents.add(nextEvent);

			// Handle depending on packet type
			if (packet.packetType == Constants.PacketType.ROUTING) {

				// Routing packet
				List<Event> events = handleRouterRouting(router, packet, link);
				if (events != null)
					newEvents.addAll(events);

			} else if (packet.packetType == Constants.PacketType.DATA
					|| packet.packetType == Constants.PacketType.ACK) {

				// Data or ACK packet
				newEvents.add(handleRouterDataAck(router, packet));

			} else {
				// TODO: Print statement for debugging remove later
				System.out.println("Other Packet Received");
			}
		}

		// Return a list of events to add to the event priority queue
		return newEvents;
	}

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

	private Event handleRouterDataAck(Router r, Packet p) {
		Link nextLink = r.routingTable.get(p.dstHost.name);
		Component nextDst = nextLink.getAdjacentEndpoint(r);
		return new SendPacketEvent(time, p, r, nextDst, nextLink);
	}
	
	private List<Event> sendRoutingInfo(Router r) {
		ArrayList<Event> newEvents = new ArrayList<Event>();
		
		// Send the distance information of this router to its neighbors
		for (Link link : r.links.values()) {
			Component adjComponent = link.getAdjacentEndpoint(r);
			
			// Only send info to routers
			if (adjComponent instanceof Router) {
				RoutingPacket routingPacket = new RoutingPacket(Constants.PacketType.ROUTING, Constants.ROUTING_PACKET_SIZE, r, (Router) adjComponent, r.distances);
				SendPacketEvent sendPacketEvent = new SendPacketEvent(this.time, routingPacket, r, adjComponent, link);
				newEvents.add(sendPacketEvent);
				
				// TODO: Also schedule a NegAckEvent 
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
