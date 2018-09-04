package soundnetwork;

import static soundnetwork.Util.TRANSMISSION_END;
import static soundnetwork.Util.TRANSMISSION_START;

public class Detector {

    private String bits = "00000000";
    private boolean transmission;

    public void addBitOn() {
        discardFirstBit();
        bits += "1";
    }

    public void addBitOff() {
        discardFirstBit();
        bits += "0";
    }

    /**
     * Discard the first bit to insert the next one
     */
    private void discardFirstBit() {
        bits = bits.substring(1);
    }

    public void checkTransmissionStart() {
        transmission = bits.contains(TRANSMISSION_START);
    }

    public void checkTransmissionEnd() {
        transmission = bits.equals(TRANSMISSION_END);
    }

    public boolean isTransmission() {
        return transmission;
    }
}
