package soundnetwork.link;

public class Frame {

    private String source;
    private String destination;
    private String payload;

    public Frame() {
    }

    public Frame(String source, String destination, String payload) {
        this.source = source;
        this.destination = destination;
        this.payload = payload;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
