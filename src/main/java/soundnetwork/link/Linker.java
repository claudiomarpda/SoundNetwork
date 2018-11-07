package soundnetwork.link;

import soundnetwork.physical.sap.PhysicalSap;

import static soundnetwork.link.Linker.FrameState.*;

public class Linker implements LinkerSap {

    private static final int BYTE_SIZE = 8;

    enum FrameState {
        SOURCE, DEST, PAYLOAD, END
    }

    private PhysicalSap physicalSap;

    private String bits;
    private FrameState state;
    private Frame frame;

    public Linker() {
        state = SOURCE;
        bits = "";
        frame = new Frame();
    }

    @Override
    public void receive(String bit) {
        System.out.println("linker received: " + bit);

        bits += bit;

        if(bits.length() == BYTE_SIZE && state == SOURCE) {
            String s = bits.substring(0, BYTE_SIZE);
            frame.setSource(s);
            state = DEST;

        }
        else if (bits.length() == BYTE_SIZE * 2 && state == DEST) {
            String s = bits.substring(BYTE_SIZE, BYTE_SIZE * 2);
            frame.setDestination(s);
            state = PAYLOAD;
        }
        else if(state == PAYLOAD && bit.equals("\n")) {
            String s = bits.substring(BYTE_SIZE * 2);
            frame.setPayload(s);
            state = END;
        }

        if(state == END) {

            System.out.println(frame);
            // Send frame to network

            // Back to receive input
            bits = "";
            state = SOURCE;
            frame = new Frame();
        }

    }

    @Override
    public void transmit(String bits) {
        for(int i = 0; i < bits.length(); i++) {
            physicalSap.transmit(String.valueOf(bits.charAt(i)));
        }
    }

    public void setPhysicalSap(PhysicalSap physicalSap) {
        this.physicalSap = physicalSap;
    }
}