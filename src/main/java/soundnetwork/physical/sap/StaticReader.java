package soundnetwork.physical.sap;

import soundnetwork.link.LinkerSap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StaticReader implements PhysicalSap {

    private LinkerSap linkerSap;
    private BufferedReader reader;

    public StaticReader(LinkerSap linkerSap) {
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/input.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.linkerSap = linkerSap;
    }

    public void start() {

        char[] buffer = new char[1];

        try {
            while(reader.read(buffer) > 0) {
                String s = String.valueOf(buffer);
                linkerSap.receive(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
