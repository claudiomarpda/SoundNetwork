package soundnetwork;

import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.*;

import static soundnetwork.AudioFormatConfig.*;

/**
 * Noise test for finding out the sound threshold of a particular ambient and microphone sensibility.
 */
public class NoiseTest {

    // How much time to listen
    private static final long TIME_IN_MILLIS = 10000;

    private SourceDataLine sourceLine;
    private TargetDataLine targetLine;

    @Before
    public void setup() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();
    }

    @Test
    public void listen() {
        targetLine.start();
        byte[] data = new byte[1];
        long end = System.currentTimeMillis() + TIME_IN_MILLIS;
        int greater = -259;

        while (System.currentTimeMillis() < end) {
            targetLine.read(data, 0, data.length);
            System.out.println("Input: " + data[0]);

            int abs = Math.abs(data[0]);
            if(abs > greater) {
                greater = abs;
            }
        }

        System.out.println("Threshold: " + greater);
    }
}
