package soundnetwork;

import javax.sound.sampled.*;

import static soundnetwork.AudioFormatConfig.*;
import static soundnetwork.Util.CLOCK_IN_MILLIS;

/**
 * Receives input sound in real time from a transmitter to decode bits and make communication with sound.
 * <p>
 * References:
 * https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioFormat.html
 * https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
 */
public class Receiver {

    /*
     * Threshold is the maximum value considered as 'ambient' or 'noisy' sound that you do not want to be captured.
     * Adjust the threshold according to your microphone sensibility and the output sound from the transmitter.
     * Analyze your input behaviour in NoisyTest class and set your threshold.
     */
    private static final int NOISE_THRESHOLD = 3;

    private final SourceDataLine sourceLine;
    private final TargetDataLine targetLine;
    private StringBuilder result;

    public Receiver() throws LineUnavailableException {
        // Constructs an AudioFormat with a linear PCM encoding and the given parameters.
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();
        result = new StringBuilder();
    }

    /**
     * Listens to input sound in real time.
     *
     * @param timeInMilliseconds: how much time to listen
     */
    public void listen(long timeInMilliseconds) {

        long end = System.currentTimeMillis() + timeInMilliseconds;

        // Avoid function call inside this thread for better performance.
        new Thread(() -> {
            targetLine.start();
            byte[] data = new byte[1];

            int size = 0;
            int sum = 0;

            long cycle = System.currentTimeMillis() + CLOCK_IN_MILLIS;
            long now = 0;
            do {
                targetLine.read(data, 0, data.length);

                sum += Math.abs(data[0]);
                size++;

                if (now >= cycle) {

                    int avg = sum / size;
                    if (avg >= NOISE_THRESHOLD) {
//                        System.out.print("1");
                        result.append("1");
                    } else {
//                        System.out.print("0");
                        result.append("0");
                    }
                    sum = 0;
                    size = 0;
//                    System.out.println();
                    cycle = System.currentTimeMillis() + CLOCK_IN_MILLIS;
                }
                now = System.currentTimeMillis();
            } while (now < end);
        }).start();
    }

    public StringBuilder getResult() {
        return result;
    }

    public void close() {
        targetLine.stop();
        targetLine.drain();
        sourceLine.close();
        targetLine.close();
    }

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        final Receiver receiver = new Receiver();
        System.out.println("LISTENING...");
        receiver.listen(10000);
        Thread.sleep(10000);
        receiver.close();

    }

}
