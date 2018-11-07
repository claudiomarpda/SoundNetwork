package soundnetwork;

import soundnetwork.link.Linker;
import soundnetwork.link.LinkerSap;
import soundnetwork.physical.sap.PhysicalSap;
import soundnetwork.physical.sap.StaticReader;

public class Main {

    public static void main(String[] args) {
        LinkerSap linkerSap = new Linker();
        PhysicalSap physicalSap = new StaticReader(linkerSap);
        physicalSap.start();
    }

}
