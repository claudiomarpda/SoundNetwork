package soundnetwork;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class Receiver {

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final AudioFormat format;
    private final DataLine.Info sourceInfo;
    private final DataLine.Info targetInfo;
    private SourceDataLine sourceLine;
    private final TargetDataLine targetLine;

    public Receiver() throws LineUnavailableException, InterruptedException {
        format = new AudioFormat(44100, 8, 1, true, false);
        sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        sourceLine.open();
        targetInfo = new DataLine.Info(TargetDataLine.class, format);
        targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetLine.open();
    }

    public void listen(long timeInMilliseconds) throws InterruptedException {
        long end = System.currentTimeMillis() + timeInMilliseconds;
        Thread targetThread = new Thread(() -> {
            targetLine.start();
            byte[] data = new byte[targetLine.getBufferSize() / 5];
            int readBytes;
            while (System.currentTimeMillis() < end) {
                readBytes = targetLine.read(data, 0, data.length);
//                for(int i =0; i < data.length; i++) {
//                    System.out.println(data[i]);
//                }
                outputStream.write(data, 0, readBytes);
            }
        });

        System.out.println("RECORDING...");
        targetThread.start();
        Thread.sleep(timeInMilliseconds);
        targetLine.stop();
        targetLine.close();
    }

    public void play(long timeInMilliseconds) throws InterruptedException {
        long end = System.currentTimeMillis() + timeInMilliseconds;

        Thread sourceThread = new Thread(() -> {
            sourceLine.start();
            while (System.currentTimeMillis() < end) {
                sourceLine.write(outputStream.toByteArray(), 0, outputStream.size());
            }
        });
        System.out.println("PLAYING...");
        sourceThread.start();
        Thread.sleep(timeInMilliseconds);
        sourceLine.stop();
        sourceLine.close();
    }

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        final Receiver receiver = new Receiver();
        receiver.listen(6000);
        receiver.play(6000);
    }

}
