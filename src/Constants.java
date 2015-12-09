/*
 * Class to hold generic constants. All time values are interpreted in seconds. 
 * All data amounts are interpreted in bytes. 
 */
public class Constants {
	// Congestion Control constants
	public static int DEFAULT_WINDOW_SIZE = 10;
	public static double DEFAULT_RTT = 1.0;
	public static double RTT_MULTIPLIER = 3.0;
	public static double TCP_FAST_TIME_INTERVAL = 0.2;
	public static double TCP_FAST_ALPHA = 30.0;

	// Packet sizes
	public static int DATA_PACKET_SIZE = 1024;
	public static int ACK_PACKET_SIZE = 64;
	public static int ROUTING_PACKET_SIZE = 1024;

	// Dynamic Routing constants
	public static double ROUTING_TIMEOUT = 1.0;
	public static double ROUTING_INTERVAL = 5.0;
	
	// Data Collection and Display constants
	public static int EVENT_OUTPUT_FREQ = 10000;
	public static double COLLECTING_INTERVAL = 0.1;

	// Data Collection fields
	public static String[] hostFields = { "hostSendRate", "hostReceiveRate" };
	public static String[] linkFields = { "occupancyData", "packetsLost",
			"linkRate" };
	public static String[] flowFields = { "flowSendRate", "flowReceiveRate",
			"packetDelay", "windowSize" };

	// Packet Types
	public enum PacketType {
		DATA, ACK, ROUTING
	}

	// Direction Types
	public enum Direction {
		RIGHT, LEFT
	}

	// TCP Congestion Control Types
	public enum TCP {
		RENO, FAST
	}
}
