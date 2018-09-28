package soundnetwork;

import soundnetwork.physical.Listener;
import soundnetwork.physical.Transmitter;
import soundnetwork.sap.PhysicalAccess;
import soundnetwork.sap.PhysicalSap;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static soundnetwork.physical.Util.CLOCK_IN_MILLIS;
import static soundnetwork.physical.Util.TRANSMISSION_START;

public class Main {

    public static void main(String[] args) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
        PhysicalSap physicalSap = new PhysicalAccess();
        Listener listener = new Listener(6, physicalSap);
        Transmitter transmitter = new Transmitter();

        System.out.println("LISTENING...");
//        String bits = TRANSMISSION_START + "0101" + TRANSMISSION_END;
        String bits = TRANSMISSION_START + "11";
        listener.listen(CLOCK_IN_MILLIS * (bits.length() + 2));

        System.out.println("PLAYING...");
        for (char c : bits.toCharArray()) {
            if (c == '1') {
                transmitter.play(CLOCK_IN_MILLIS);
                Thread.sleep(100);
            }
            else {
                Thread.sleep(CLOCK_IN_MILLIS);
            }
        }

        Thread.sleep(CLOCK_IN_MILLIS);
        listener.close();
        transmitter.close();
        System.out.println("\nSent v\n" + bits + "\n");
        System.out.println(physicalSap.getResult() + "\nReceived ^");
    }
}
