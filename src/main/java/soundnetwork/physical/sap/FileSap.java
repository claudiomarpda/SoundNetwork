package soundnetwork.physical.sap;

import soundnetwork.link.LinkerSap;

import java.io.*;

public class FileSap implements PhysicalSap {

    private File file;
    private LinkerSap linkerSap;
    private BufferedReader reader;
    private BufferedWriter writer;

    public FileSap(LinkerSap linkerSap) {
        try {
            file = new File("src/main/resources/physical_source.txt");
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        linkerSap.setPhysicalSap(this);
        this.linkerSap = linkerSap;
    }

    public void receive() {

        char[] buffer = new char[1];

        try {
            while (reader.read(buffer) > 0) {
                String s = String.valueOf(buffer);
                linkerSap.receive(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transmit(String bits) {

        try {
            writer = new BufferedWriter(new FileWriter(file, false));

            System.out.println("Writing... ");

            for (int i = 0; i < bits.length(); i++) {

                writer.write(bits.charAt(i));
                writer.flush();
                System.out.print(bits.charAt(i));
                Thread.sleep(250);

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
