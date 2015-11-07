public class Link {
	String linkName;
	Component leftEndPoint;
	Component rightEndPoint;
	double linkRate;
	double linkDelay;
	double bufferSize;

	public Link(String name, Component leftEndPoint, Component rightEndPoint,
			double linkRate, double linkDelay, double bufferSize) {
		this.linkName = name;
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.linkRate = linkRate;
		this.linkDelay = linkDelay;
		this.bufferSize = bufferSize;
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		} else {
			return this.linkName.equals(((Link) obj).linkName);
		}
	}
}
