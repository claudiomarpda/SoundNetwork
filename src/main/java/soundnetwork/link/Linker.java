package soundnetwork.link;

import soundnetwork.network.sap.NetSap;
import soundnetwork.physical.sap.PhysicalSap;

import static soundnetwork.link.Linker.FrameState.*;

public class Linker implements LinkerSap {

    private static final int BYTE_SIZE = 8;

    enum FrameState {
        SOURCE, DEST, PAYLOAD, END, DENIED
    }

    private PhysicalSap physicalSap;

    private String bits;
    private FrameState state;
    private Frame frame;
    private NetSap netSap;

    public Linker(NetSap netSap) {
        state = SOURCE;
        bits = "";
        frame = new Frame();
        this.netSap = netSap;
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

            if(!netSap.belongs(frame.getDestination())) {
                state = DENIED;
            }
            else {
                state = PAYLOAD;
            }
        }
        else if(state == PAYLOAD && bit.equals("\n")) {
            String s = bits.substring(BYTE_SIZE * 2);
            frame.setPayload(s);
            state = END;
        }

        if(state == END || state == DENIED) {

            if(state == DENIED) {
                System.out.println("No match for destination");
            }
            else {
                // END
                System.out.println(frame);
            }
            // Back to receive input
            bits = "";
            state = SOURCE;
            frame = new Frame();
        }


    }

    @Override
    public void transmit(String bits) {
        physicalSap.transmit(String.valueOf(bits));
    }

    public void setPhysicalSap(PhysicalSap physicalSap) {
        this.physicalSap = physicalSap;
    }
}
