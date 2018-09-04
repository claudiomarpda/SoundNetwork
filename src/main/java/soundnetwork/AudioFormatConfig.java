package soundnetwork;

public interface AudioFormatConfig {

    // Number of samples per second, per channel
    int SAMPLE_RATE = 44100;

    // Number of bits per sample (per channel)
    int SAMPLE_SIZE_IN_BITS = 8;

    // The number of audio channels in this format (1 for mono, 2 for stereo)
    int CHANNELS = 1;

    // Indicates whether the data is signed or unsigned
    boolean SIGNED = true;

    // Indicates whether the audio data is stored in big-endian or little-endian order
    boolean BIG_ENDIAN = false;
    
}
