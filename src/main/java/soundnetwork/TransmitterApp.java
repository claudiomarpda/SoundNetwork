package soundnetwork;

import soundnetwork.link.Linker;
import soundnetwork.link.LinkerSap;
import soundnetwork.physical.sap.FileSap;
import soundnetwork.physical.sap.PhysicalSap;

public class TransmitterApp {

    public static void main(String[] args) {
        LinkerSap linkerSap = new Linker();
        PhysicalSap physicalSap = new FileSap(linkerSap);
        linkerSap.transmit("1010");
    }

}
