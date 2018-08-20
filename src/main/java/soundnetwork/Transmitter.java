package soundnetwork;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Transmitter {

    private Clip audioClip;

    private AudioInputStream audioStream;

    public Transmitter() throws IOException, UnsupportedAudioFileException, InterruptedException, LineUnavailableException {
        audioStream = AudioSystem.getAudioInputStream(new File("src/main/resources/beep-one-tenth.wav"));
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.open(audioStream);
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void play(long timeInMilliseconds) throws InterruptedException {
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        new Thread(() -> audioClip.start()).start();
        Thread.sleep(timeInMilliseconds);
        audioClip.stop();
    }

    public void close() throws IOException {
        audioClip.close();
        audioStream.close();
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        final Transmitter s = new Transmitter();
        s.play(1000);
        Thread.sleep(3000);
        s.play(1000);
        Thread.sleep(5000);
        s.close();
    }

}
