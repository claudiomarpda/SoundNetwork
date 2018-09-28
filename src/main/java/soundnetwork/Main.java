package soundnetwork;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static soundnetwork.Util.CLOCK_IN_MILLIS;
import static soundnetwork.Util.TRANSMISSION_END;
import static soundnetwork.Util.TRANSMISSION_START;

public class Main {

    public static void main(String[] args) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {

        Receiver receiver = new Receiver(6);
        Transmitter transmitter = new Transmitter();

        System.out.println("LISTENING...");
        String bits = TRANSMISSION_START + "0101" + TRANSMISSION_END;
        receiver.listen(CLOCK_IN_MILLIS * (bits.length() + 2));


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
        receiver.close();
        transmitter.close();
        System.out.println("\nSent v\n" + bits + "\n");
        System.out.println(receiver.getResult() + "\nReceived ^");
    }
}
