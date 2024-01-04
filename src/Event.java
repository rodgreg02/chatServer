public class Event {

    private String EVENT;
    private String PAYLOAD;

    public Event(String event) {
        String[] receivedEvent = event.trim().split("\\|", 0);
        this.EVENT = receivedEvent[0];
        this.PAYLOAD = receivedEvent[1];
    }

    public String getEVENT() {
        return EVENT;
    }

    public String getPAYLOAD() {
        return PAYLOAD;
    }
}
