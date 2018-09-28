package soundnetwork.physical;

import soundnetwork.sap.PhysicalSap;

import javax.sound.sampled.TargetDataLine;

import static soundnetwork.physical.Util.CLOCK_IN_MILLIS;

/**
 * Obtains input data from a Transmitter. It means the transmission has started.
 */
public class Receiver implements Runnable {

    private TargetDataLine targetLine;
    private PhysicalSap physicalSap;
    private boolean sync;
    private boolean listening;
    private int noiseThreshold;
    private boolean maySync;

    public Receiver(TargetDataLine targetLine, int noiseThreshold, PhysicalSap physicalSap) {
        this.targetLine = targetLine;
        this.physicalSap = physicalSap;
        this.listening = true;
        this.sync = false;
        this.noiseThreshold = noiseThreshold;
        this.maySync = true;
    }

    @Override
    public void run() {

        System.out.print("Receiver thread: ");

        int sum = 0;
        int size = 0;
        byte[] data = new byte[1];

        long now = System.currentTimeMillis();
        long cycle = now + CLOCK_IN_MILLIS;

        // Avoid function call inside the loop of this thread for better performance

        while (listening) {
            targetLine.read(data, 0, data.length);
            sum += Math.abs(data[0]);
            size++;

            // Update time to read input now
            if (sync) {
                now = cycle + 1;
                maySync = false;
                sync = false;
            }

            if (now >= cycle) {
                int avg = sum / size;

                // If there is sound
                if (avg > noiseThreshold) {
                    physicalSap.add("1");
                    System.out.print("1");

                } else {
                    physicalSap.add("0");
                    System.out.print("0");
                }
                cycle = System.currentTimeMillis() + CLOCK_IN_MILLIS;
                maySync = true;

                sum = 0;
                size = 0;
            }

            now = System.currentTimeMillis();
        }
    }

    public void synchronize() {
        if(maySync) {
            sync = true;
        }
    }

    public void stop() {
        this.listening = false;
    }

}
