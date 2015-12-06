/*
 * All time values are interpreted in seconds. All data amounts are
 * interpreted in bytes. 
 */
public class Constants {
	public static int DEFAULT_WINDOW_SIZE = 10;
	public static int PACKET_SIZE = 1024;
	public static int ACK_SIZE = 64;
	
	public static String[] hostFields = {"sendRate", "receiveRate"};
	public static String[] linkFields = {"occupancyData", "packetsLost", "flowRate"};
	public static String[] flowFields = {"sendRate", "receiveRate", "rtt", "windowSize"};

	// Packet types
	public enum PacketType {
		DATA, ACK
	}

	public enum Direction {
		RIGHT, LEFT
	}
}
