package soundnetwork;

import static soundnetwork.Util.BYTE_SIZE;
import static soundnetwork.Util.TRANSMISSION_START;

public class SoundDetector {

    private int bitsOn = 0;
    private String bits = "";

    public void addBitOn() {
        if (isFull()) {
            discardFirstBit();
        }
        bitsOn++;
        bits += "1";
    }

    public void addBitOff() {
        if (isFull()) {
            discardFirstBit();
        }
        bits += "0";
    }

    public boolean isTransmission() {
        if(bitsOn <= 0) {
            return false;
        }
        return bits.equals(TRANSMISSION_START);
    }

    private boolean isFull() {
        return bits.length() >= BYTE_SIZE;
    }

    /**
     *  Discard the first bit to insert the next one
     */
    private void discardFirstBit() {
        bits = bits.substring(1);
    }
}
