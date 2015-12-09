import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class is used to encapsulate the various elements that comprise a
 * Network. Additionally, this class contains parsing code that reads the
 * specifications of a network and constructs the elements of it accordingly.
 * 
 * The details of how to specify the network to be simulated can be found in the
 * README file.
 */
public class Network {
	HashMap<String, Host> hosts;
	HashMap<String, Router> routers;
	HashMap<String, Link> links;
	HashMap<String, Flow> flows;
	HashMap<String, Component> components;

	public Network(HashMap<String, Host> hosts,
			HashMap<String, Router> routers, HashMap<String, Link> links,
			HashMap<String, Flow> flows) {
		this.hosts = hosts;
		this.routers = routers;
		this.links = links;
		this.flows = flows;
	}

	/**
	 * This function parses the network located in the file specified as an
	 * argument. See the README file for the correct way to format this file
	 */
	public static Network parse(String filename) throws IOException {
		// Create a Scanner object and retrieve the counts of each element
		Scanner sc = new Scanner(new File(filename));
		int H = sc.nextInt();
		int R = sc.nextInt();
		int L = sc.nextInt();
		int F = sc.nextInt();

		// Construct the various hash maps used to store our data
		HashMap<String, Component> components = new HashMap<String, Component>();
		HashMap<String, Host> hosts = new HashMap<String, Host>();
		HashMap<String, Router> routers = new HashMap<String, Router>();
		HashMap<String, Link> links = new HashMap<String, Link>();
		HashMap<String, Flow> flows = new HashMap<String, Flow>();

		// Construct the hosts
		for (int i = 0; i < H; i++) {
			String hostName = sc.next();
			Host host = new Host(hostName);

			hosts.put(hostName, host);
			components.put(hostName, host);
		}

		// Construct the routers
		for (int i = 0; i < R; i++) {
			String routerName = sc.next();
			Router router = new Router(routerName);
			routers.put(routerName, router);
			components.put(routerName, router);
		}

		// Give each router a list of all components in the network
		// This is used in the dynamic shortest paths algorithm
		for (Router router : routers.values()) {
			router.components = components;
		}

		// Construct the links
		for (int i = 0; i < L; i++) {
			String linkName = sc.next();
			String leftEndpoint = sc.next();
			String rightEndpoint = sc.next();
			double linkRate = sc.nextDouble();
			double linkDelay = sc.nextDouble();
			double bufferSize = sc.nextDouble();

			// We perform some unit conversion. We expect the input
			// units to match those given in the original three test cases.
			// We convert time values into seconds and size values into bytes
			Link link = new Link(linkName, components.get(leftEndpoint),
					components.get(rightEndpoint),
					linkRate * 1000 * 1000 / 8.0, linkDelay / 1000,
					bufferSize * 1000);
			links.put(linkName, link);

			// We also add this link to the hash map of adjacent
			// links for both endpoints
			components.get(leftEndpoint).links.put(linkName, link);
			components.get(rightEndpoint).links.put(linkName, link);
		}

		// Construct the flows
		for (int i = 0; i < F; i++) {
			String flowName = sc.next();
			String src = sc.next();
			String dst = sc.next();
			double amt = sc.nextDouble();
			double time = sc.nextDouble();
			Flow flow = new Flow(flowName, hosts.get(src), hosts.get(dst),
					amt * 1000 * 1000, time);
			flows.put(flowName, flow);
		}

		// Additionally, for each flow we get the preferred method of
		// congestion control and set some default values
		for (int i = 0; i < F; i++) {
			String flowName = sc.next();
			String tcp = sc.next();

			if (tcp.equals("RENO")) {
				Flow flow = flows.get(flowName);
				flow.tcp = Constants.TCP.RENO;
				flow.windowSize = 1.0;
				flow.slowStartThresh = 1000;

			} else if (tcp.equals("FAST")) {
				Flow flow = flows.get(flowName);
				flow.tcp = Constants.TCP.FAST;
				flow.windowSize = 1.0;
				flow.slowStartThresh = 1000;
			}
		}

		sc.close();
		return new Network(hosts, routers, links, flows);
	}
}
