package soundnetwork;

import javax.sound.sampled.*;

import static soundnetwork.AudioFormatConfig.*;
import static soundnetwork.Util.CLOCK_IN_MILLIS;
import static soundnetwork.Util.TRANSMISSION_END;
import static soundnetwork.Util.TRANSMISSION_START;

/**
 * Receives input sound in real time from a transmitter to decode bits and make communication with sound.
 * <p>
 * References:
 * https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioFormat.html
 * https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
 */
public class Receiver {

    /*
     * Threshold is the maximum value considered as 'ambient' or 'noise' sound that you do not want to be captured.
     * Adjust the threshold according to your microphone sensibility and the output sound from the transmitter.
     * Analyze your input behaviour running NoiseTest class and set your threshold.
     */
    private final int noiseThreshold;
    private final SourceDataLine sourceLine;
    private final TargetDataLine targetLine;
    private StringBuilder result;

    public Receiver(int noiseThreshold) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();

        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();

        result = new StringBuilder();
        this.noiseThreshold = noiseThreshold;
    }

    /**
     * Listens to input sound in real time.
     *
     * @param timeInMilliseconds: how much time to listen
     */
    public void listen(long timeInMilliseconds) {

        long end = System.currentTimeMillis() + timeInMilliseconds;

        // Avoid function call inside the loop of this thread for better performance
        new Thread(() -> {
            System.out.print("Listening thread: ");
            targetLine.start();
            byte[] data = new byte[1];

            long now = 0;
            int sum = 0;
            int size = 0;
            long cycle = System.currentTimeMillis() + CLOCK_IN_MILLIS;

            StringBuilder bits = new StringBuilder("00000000");
            boolean transmission = false;

            do {
                targetLine.read(data, 0, data.length);

                sum += Math.abs(data[0]);
                size++;

                if (now >= cycle) {

                    int avg = sum / size;
                    // If there is sound
                    if (avg > noiseThreshold) {

                        // Add 1
                        bits.append("1");
                        bits.deleteCharAt(0);

                        if (transmission) {
                            result.append("1");
                        } else {
                            // Check start of transmission
                            transmission = bits.substring(6, 8).equals(TRANSMISSION_START);
                        }

                        System.out.print("1");

                    } else {

                        // Add 0
                        bits.append("0");
                        bits.deleteCharAt(0);

                        if (transmission) {
                            result.append("0");

                            // Check end of transmission
                            if (bits.toString().equals(TRANSMISSION_END)) {
                                transmission = false;

                                if(result.length() >= 8) {
                                    // Delete the transmission end signals
                                    result.delete(result.length() - 8, result.length());
                                }
                            }
                        }

                        System.out.print("0");
                    }

                    sum = 0;
                    size = 0;
                    cycle = System.currentTimeMillis() + CLOCK_IN_MILLIS;
                }

                now = System.currentTimeMillis();
            } while (now < end);
        }).start();

    }

    public String getResult() {
        return result.toString();
    }

    public void close() {
        targetLine.stop();
        targetLine.drain();
        sourceLine.close();
        targetLine.close();
    }

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        final Receiver receiver = new Receiver(2);
        System.out.println("LISTENING...");
        receiver.listen(20000);
        Thread.sleep(20000);
        receiver.close();

        String result = receiver.getResult();
        System.out.println("\n" + result + "\nReceived full^");
    }

}
