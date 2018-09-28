package soundnetwork.physical;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static soundnetwork.physical.Util.CLOCK_IN_MILLIS;
import static soundnetwork.physical.Util.TRANSMISSION_END;
import static soundnetwork.physical.Util.TRANSMISSION_START;

public class Transmitter {

    private Clip audioClip;
    private AudioInputStream audioStream;

    public Transmitter() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        audioStream = AudioSystem.getAudioInputStream(new File("src/main/resources/beep-one-tenth.wav"));
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioStream);
    }

    public void play(long timeInMilliseconds) throws InterruptedException {
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        audioClip.start();
        Thread.sleep(timeInMilliseconds);
        audioClip.stop();
        audioClip.drain();
    }

    public void close() throws IOException {
        audioClip.close();
        audioStream.close();
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        System.out.println("PLAYING...");
        Transmitter transmitter = new Transmitter();
        String bits = TRANSMISSION_START + "0101" + TRANSMISSION_END;

        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                transmitter.play(CLOCK_IN_MILLIS);
            }else {
                Thread.sleep(CLOCK_IN_MILLIS);
            }
        }

        Thread.sleep(CLOCK_IN_MILLIS);
        transmitter.close();
        System.out.println("\nSent v\n" + bits);
    }

}
