import java.util.List;

public class ReceivePacketEvent extends Event {
	Packet packet;

	public ReceivePacketEvent(double time, Packet packet) {
		super(time);
		this.packet = packet;
	}

	@Override
	public List<Event> handle() {
		// TODO Auto-generated method stub
		return null;
	}

}
