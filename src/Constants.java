/*
 * All time values are interpreted in seconds. All data amounts are
 * interpreted in bytes. 
 */
public class Constants {
	public static int DEFAULT_WINDOW_SIZE = 10;
	public static TCP DEFAULT_TCP = TCP.RENO;
	public static int PACKET_SIZE = 1024;
	public static int ACK_SIZE = 64;
	
	public static String[] hostFields = {"sendRate", "receiveRate"};
	public static String[] linkFields = {"occupancyData", "packetsLost", "flowRate", "totalDelay"};
	public static String[] flowFields = {"sendRate", "receiveRate", "rtt", "windowSize", "slowStartThresh"};

	
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
