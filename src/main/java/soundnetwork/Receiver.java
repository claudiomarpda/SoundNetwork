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
     * Threshold is the maximum value considered as 'ambient' or 'noise' sound that you do not want to be captured.
     * Adjust the threshold according to your microphone sensibility and the output sound from the transmitter.
     * Analyze your input behaviour running NoiseTest class and set your threshold.
     */
    private int noiseThreshold;

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


        SoundDetector soundDetector = new SoundDetector();

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

            do {
                targetLine.read(data, 0, data.length);

                sum += Math.abs(data[0]);
                size++;

                if (now >= cycle) {

                    int avg = sum / size;
                    if (avg > noiseThreshold) {

                        if(soundDetector.isTransmission()) {
                            result.append("1");
                            System.out.print("1");
                        }
                        else {
                            soundDetector.addBitOn();
                        }
                    } else {

                        if(soundDetector.isTransmission()) {
                            result.append("0");
                            System.out.print("0");
                        }
                        else {
                            soundDetector.addBitOff();
                        }
                    }

                    sum = 0;
                    size = 0;
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
        final Receiver receiver = new Receiver(4);
        System.out.println("LISTENING...");
        receiver.listen(10000);
        Thread.sleep(10000);
        receiver.close();

        System.out.println("\n" + receiver.getResult() + "\nReceived ^");
    }

}
