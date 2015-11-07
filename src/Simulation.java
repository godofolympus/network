import java.io.IOException;
import java.util.Scanner;

public class Simulation {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.print("Enter Filename for Test Case: ");
		Scanner sc = new Scanner(System.in);
		String filename = sc.next();
		sc.close();
		System.out.println(filename);
		Network network = Network.parse(filename);
		System.out.println(network.hosts);
	}

}
