package soundnetwork.physical.sap;

import soundnetwork.link.LinkerSap;

import java.io.*;

public class FileSap implements PhysicalSap {

    private LinkerSap linkerSap;
    private BufferedReader reader;
    private BufferedWriter writer;

    public FileSap(LinkerSap linkerSap) {
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/input.txt"));
            writer = new BufferedWriter(new FileWriter(new File("src/main/resources/output.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        linkerSap.setPhysicalSap(this);
        this.linkerSap = linkerSap;
    }

    public void receive() {

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

    @Override
    public void transmit(String bits) {
        for (int i = 0; i < bits.length(); i++) {
            try {
                writer.write(bits.charAt(i));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
