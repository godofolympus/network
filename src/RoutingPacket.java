import java.util.HashMap;

/**
 * Generic class to represent RoutingPackets in the network. Unlike data
 * and ack packets, these packets do not have a src/dst Host. Additionally
 * they contain a hash map of (component, distance) pairs that correspond to
 * distance estimates for a given router
 */
public class RoutingPacket extends Packet {
	// Hash map that contains the srcRouter's shortest distances
	HashMap<String, Double> newDistances;
	Router srcRouter;
	Router dstRouter;
	
	public RoutingPacket(Constants.PacketType packetType, int size,
			Router srcRouter, Router dstRouter, HashMap<String, Double> routingDistances) {
		super(-1, packetType, size, null, null, "ROUTING");
		this.srcRouter = srcRouter;
		this.dstRouter = dstRouter;
		this.newDistances = routingDistances;
	}
}
