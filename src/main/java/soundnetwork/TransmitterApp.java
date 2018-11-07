package soundnetwork;

import soundnetwork.link.Linker;
import soundnetwork.link.LinkerSap;
import soundnetwork.network.NetWork;
import soundnetwork.physical.sap.FileSap;
import soundnetwork.physical.sap.PhysicalSap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TransmitterApp {

    public static void main(String[] args) {
        LinkerSap linkerSap = new Linker(new NetWork());
        PhysicalSap physicalSap = new FileSap(linkerSap);

        try {
            String msg = new String(Files.readAllBytes(Paths.get("src/main/resources/input.txt")));
            linkerSap.transmit(msg);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
