package soundnetwork.physical;

import soundnetwork.sap.PhysicalAccess;
import soundnetwork.sap.PhysicalSap;

import javax.sound.sampled.*;

import static soundnetwork.physical.AudioFormatConfig.*;
import static soundnetwork.physical.Util.CLOCK_IN_MILLIS;
import static soundnetwork.physical.Util.TRANSMISSION_END;

/**
 * Receives input sound in real time from a transmitter to decode bits and make communication with sound.
 * <p>
 * References:
 * https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioFormat.html
 * https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
 */
public class Listener {

    /*
     * Threshold is the maximum value considered as 'ambient' or 'noise' sound that you do not want to be captured.
     * Adjust the threshold according to your microphone sensibility and the output sound from the transmitter.
     * Analyze your input behaviour running NoiseTest class and set your threshold.
     */
    private final int noiseThreshold;
    private final SourceDataLine sourceLine;
    private final TargetDataLine targetLine;
    private PhysicalSap physicalSap;

    public Listener(int noiseThreshold, PhysicalSap physicalSap) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();

        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();

        this.noiseThreshold = noiseThreshold;
        this.physicalSap = physicalSap;
    }

    /**
     * Listens to input sound in real time.
     *
     * @param timeInMilliseconds: how much time to listen
     */
    public void listen(long timeInMilliseconds) {
        final long end = System.currentTimeMillis() + timeInMilliseconds;

        // Avoid function call inside the loop of this thread for better performance

        new Thread(() -> {
            targetLine.start();
            byte[] data = new byte[1];

            Receiver receiver = new Receiver(targetLine, noiseThreshold, physicalSap);

            int sum = 0;
            int size = 0;

            long now = 0;
            long interval = 50;
            long cycle = System.currentTimeMillis() + interval;

            boolean transmission = false;

            do {
                targetLine.read(data, 0, data.length);
                sum += Math.abs(data[0]);
                size++;

                if (now >= cycle) {
                    int avg = sum / size;
                    if (!transmission) {

                        // If there is sound
                        if (avg > noiseThreshold) {

                            // Start transmission
                            transmission = true;
                            new Thread(receiver).start();

                            System.out.println("start");
                        }
                    } else {

                        if (physicalSap.getBits().equals(TRANSMISSION_END)) {
                            transmission = false;
                            receiver.stop();
                        } else {
                            String last = physicalSap.getCurrent();
                            String current = avg > noiseThreshold ? "1" : "0";

                            if (!current.equals(last)) {
                                System.out.print("*");
                                // Update the time now
                                receiver.synchronize();
                                try {
                                    Thread.sleep(CLOCK_IN_MILLIS - 50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    sum = 0;
                    size = 0;
                    cycle = System.currentTimeMillis() + interval;
                }

                now = System.currentTimeMillis();
            } while (now < end);

            receiver.stop();

        }).start();

    }

    public void close() {
        targetLine.stop();
        targetLine.drain();
        sourceLine.close();
        targetLine.close();
    }

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        PhysicalSap physicalSap = new PhysicalAccess();
        final Listener listener = new Listener(6, physicalSap);
        System.out.println("LISTENING...");
        listener.listen(20000);
        Thread.sleep(20000);
        listener.close();

        String result = physicalSap.getResult();
        System.out.println("\n" + result + "\nReceived full^");
    }

}
