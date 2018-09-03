package soundnetwork;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.sound.sampled.*;

import static soundnetwork.Util.CLOCK_IN_MILLIS;

public class Receiver {

    private final SourceDataLine sourceLine;
    private final TargetDataLine targetLine;
    private StringBuilder result;

    public Receiver() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(190000, 8, 1, true, false);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();
        result = new StringBuilder();
    }

    public void listen(long timeInMilliseconds) throws InterruptedException {

        long end = System.currentTimeMillis() + timeInMilliseconds;

        new Thread(() -> {
            targetLine.start();
            byte[] data = new byte[1];

            int size = 0;
            int sum = 0;

            long lap = System.currentTimeMillis() + CLOCK_IN_MILLIS;
            long now = 0;
            do {
                targetLine.read(data, 0, data.length);

                sum += Math.abs(data[0]);
                size++;

                if (now >= lap) {
                    if (isValid(average(sum, size))) {
                        System.out.print("1");
                        result.append("1");
                    } else {
                        System.out.print("0");
                        result.append("0");
                    }
                    sum = 0;
                    size = 0;
                    System.out.println();
                    lap = System.currentTimeMillis() + CLOCK_IN_MILLIS;
                }
                now = System.currentTimeMillis();
            } while (now < end);
        }).start();
    }

    private int average(int value, int size) {
        return value / size;
    }

    public StringBuilder getResult() {
        return result;
    }

    private boolean isValid(int data) {
        final byte V = 3;
        return data <= -V || data >= V;
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
