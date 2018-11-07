package soundnetwork;

import soundnetwork.link.Linker;
import soundnetwork.link.LinkerSap;
import soundnetwork.network.NetWork;
import soundnetwork.network.sap.NetSap;
import soundnetwork.physical.sap.PhysicalSap;
import soundnetwork.physical.sap.FileSap;

public class ReceiverApp {

    public static void main(String[] args) {
        NetSap netSap = new NetWork();
        LinkerSap linkerSap = new Linker(netSap);
        PhysicalSap physicalSap = new FileSap(linkerSap);
        physicalSap.receive();
    }

}
