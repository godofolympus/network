/*
 * All time values are interpreted in seconds. All data amounts are
 * interpreted in bytes. 
 */
public class Constants {
	public static int DEFAULT_WINDOW_SIZE = 10;
	public static TCP DEFAULT_TCP = TCP.RENO;
	public static int PACKET_SIZE = 1024;
	public static int ACK_SIZE = 64;
	public static double TCP_FAST_TIME_INTERVAL = 0.2;
	
	public static String[] hostFields = {"hostSendRate", "hostReceiveRate"};
	public static String[] linkFields = {"occupancyData", "packetsLost", "linkRate", /*"totalLeftDelay", "totalRightDelay"*/};
	public static String[] flowFields = {"flowSendRate", "flowReceiveRate", "rtt", "windowSize", "slowStartThresh"};

	
	// Packet types
	public enum PacketType {
		DATA, ACK
	}

	// Direction types
	public enum Direction {
		RIGHT, LEFT
	}
	
	// TCP Congestion Control Types
	public enum TCP {
		RENO, FAST
	}
}
