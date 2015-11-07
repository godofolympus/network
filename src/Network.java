import java.util.HashMap;
import java.util.Scanner;

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

	public static Network parse(String filename) {
		Scanner sc = new Scanner(filename);
		int H = sc.nextInt();
		int R = sc.nextInt();
		int L = sc.nextInt();
		int F = sc.nextInt();

		HashMap<String, Component> components = new HashMap<String, Component>();
		HashMap<String, Host> hosts = new HashMap<String, Host>();
		HashMap<String, Router> routers = new HashMap<String, Router>();
		HashMap<String, Link> links = new HashMap<String, Link>();
		HashMap<String, Flow> flows = new HashMap<String, Flow>();

		for (int i = 0; i < H; i++) {
			String hostName = sc.next();
			Host host = new Host(hostName);

			hosts.put(hostName, host);
			components.put(hostName, host);
		}

		for (int i = 0; i < R; i++) {
			String routerName = sc.next();
			Router router = new Router(routerName);

			routers.put(routerName, router);
			components.put(routerName, router);
		}

		for (int i = 0; i < L; i++) {
			String linkName = sc.next();
			String leftEndpoint = sc.next();
			String rightEndpoint = sc.next();
			double linkRate = sc.nextDouble();
			double linkDelay = sc.nextDouble();
			double bufferSize = sc.nextDouble();

			links.put(linkName, new Link(linkName,
					components.get(leftEndpoint),
					components.get(rightEndpoint), linkRate, linkDelay,
					bufferSize));
		}

		for (int i = 0; i < F; i++) {
			String flowName = sc.next();
			String src = sc.next();
			String dst = sc.next();
			double amt = sc.nextDouble();
			double time = sc.nextDouble();
			flows.put(flowName,
					new Flow(flowName, hosts.get(src), hosts.get(dst), amt,
							time));
		}

		sc.close();
		return new Network(hosts, routers, links, flows);
	}
}
