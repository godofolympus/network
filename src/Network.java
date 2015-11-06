import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Network {
	List<Host> hosts;
	List<Router> routers;
	List<Link> links;
	List<Flow> flows;
	
	public Network(List<Host> hosts, List<Router> routers, List<Link> links,
			List<Flow> flows){
		this.hosts = hosts;
		this.routers = routers;
		this.links = links;
		this.flows = flows;
	}
	
	public static Network parse(String filename){
		Scanner sc = new Scanner(filename);
		int H = sc.nextInt();
		int R = sc.nextInt();
		int L = sc.nextInt();
		int F = sc.nextInt();
		List<Host> hosts = new ArrayList<Host>();
		for(int i = 0; i < H; i++){
			String hostName = sc.next();
			hosts.add(new Host());
		}
		List<Router> routers = new ArrayList<Router>();
		for(int i = 0; i < R; i++){
			String routerName = sc.next();
			routers.add(new Router());
		}
		List<Link> links = new ArrayList<Link>();
		for(int i = 0; i < L; i++){
			String linkName = sc.next();
			String leftEndpoint = sc.next();
			String rightEndpoint = sc.next();
			double linkRate = sc.nextDouble();
			double linkDelay = sc.nextDouble();
			double bufferSize = sc.nextDouble();
			links.add(new Link());
		}
		List<Flow> flows = new ArrayList<Flow>();
		for(int i = 0; i < F; i++){
			String flowName = sc.next();
			String src = sc.next();
			String dst = sc.next();
			double amt = sc.nextDouble();
			double startTime = sc.nextDouble();
			flows.add(new Flow());
		}
		sc.close();
		return new Network(hosts, routers, links, flows);
	}
}
