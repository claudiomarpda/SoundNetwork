package soundnetwork.link;

import soundnetwork.physical.sap.PhysicalSap;

public interface LinkerSap {

    void receive(String bit);

    void transmit(String bit);

    void setPhysicalSap(PhysicalSap physicalSap);

}
