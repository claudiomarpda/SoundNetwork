package soundnetwork;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

import static soundnetwork.Util.CLOCK_IN_MILLIS;

public class Main {

    public static void main(String[] args) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {

        Receiver receiver = new Receiver(7);
        Transmitter transmitter = new Transmitter();

        System.out.println("LISTENING...");
        String transmission = "11100111";
        String bits = transmission + "10101";
//        String bits = "1010110011";
//        String bits = "101010010010001110011";
        receiver.listen(CLOCK_IN_MILLIS * (bits.length() + 1));


        System.out.println("PLAYING...");
        for (char c : bits.toCharArray()) {
            if (c == '1') {
                transmitter.play(CLOCK_IN_MILLIS);
            }
            else {
                Thread.sleep(CLOCK_IN_MILLIS);
            }
        }

        Thread.sleep(CLOCK_IN_MILLIS);
        receiver.close();
        transmitter.close();
        System.out.println("\nSent v\n" + bits);
        System.out.println(receiver.getResult() + "\nReceived ^");
    }
}
