import java.util.HashMap;

public class RoutingPacket extends Packet {
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
