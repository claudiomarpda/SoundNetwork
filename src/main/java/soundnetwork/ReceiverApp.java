package soundnetwork;

import soundnetwork.link.Linker;
import soundnetwork.link.LinkerSap;
import soundnetwork.physical.sap.PhysicalSap;
import soundnetwork.physical.sap.FileSap;

public class ReceiverApp {

    public static void main(String[] args) {
        LinkerSap linkerSap = new Linker();
        PhysicalSap physicalSap = new FileSap(linkerSap);
        physicalSap.receive();
    }

}
