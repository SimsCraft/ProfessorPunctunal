package com.simcraft.managers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Manages audio playback for the game using a singleton pattern.
 * <p>
 * The {@link SoundManager} loads, plays, and stops audio clips efficiently.
 * Audio files can be loaded either from the JAR's resources or as external
 * files.
 */
public class SoundManager {

    // ----- STATIC VARIABLES -----
    /**
     * Singleton instance of {@link SoundManager}.
     */
    private static SoundManager instance;

    /**
     * Directory where sound files are stored.
     */
    private static final String SOUNDS_FOLDER = "/sounds/";

    // ----- INSTANCE VARIABLES -----
    /**
     * Stores audio clips mapped by their unique keys.
     */
    private final Map<String, Clip> clips;

    /**
     * Volume control (range: 0.0 to 1.0).
     */
    private float volume;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to enforce the singleton pattern.
     */
    private SoundManager() {
        clips = new HashMap<>();
        volume = 1.0f; // Default volume
        loadDefaultClips();
    }

    /**
     * Loads the default audio clips when the sound manager is initialized.
     */
    private void loadDefaultClips() {
        loadAndStoreClip("background", "background.wav");
        loadAndStoreClip("footstep", "footstep.wav");
        loadAndStoreClip("game_over", "game_over.wav");
    }

    // ----- SINGLETON GETTER -----
    /**
     * Returns the singleton instance of the {@link SoundManager}.
     *
     * @return The single instance of {@link SoundManager}.
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    // ----- GETTERS -----
    /**
     * Retrieves a loaded clip by its key.
     *
     * @param key The key of the clip.
     * @return The corresponding {@link Clip}, or {@code null} if not found.
     */
    public Clip getClip(String key) {
        return clips.get(key);
    }

    /**
     * Retrieves all available clip keys.
     *
     * @return A {@link Set} of clip keys.
     */
    public Set<String> getAvailableClips() {
        return clips.keySet();
    }

    /**
     * Gets the current volume level.
     *
     * @return The volume level (range: 0.0 to 1.0).
     */
    public float getVolume() {
        return volume;
    }

    // ----- SETTERS -----
    /**
     * Sets the global volume level.
     *
     * @param volume The volume level (range: 0.0 to 1.0).
     */
    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0.0f, 1.0f);
        applyVolumeToAllClips();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads an audio clip from the specified file and stores it in the clips
     * map.
     *
     * @param key The key under which the clip is stored.
     * @param fileName The file name of the audio file.
     * @throws IllegalArgumentException If either the key or file path are blank
     * or {@code null}.
     * @throws IOException If there is an error reading the file or it does not
     * exist.
     * @throws UnsupportedAudioFileException If the audio format is not
     * supported.
     * @throws LineUnavailableException If no audio line is available for
     * playback.
     */
    private void loadAndStoreClip(String key, String fileName) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("SoundManager: Key cannot be null or empty.");
        }

        try {
            clips.put(key, loadClipFromAudioFile(SOUNDS_FOLDER + fileName));
        } catch (IOException e) {
            System.err.println("Failed to load clip (I/O error): " + fileName + " - " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Failed to load clip (Unsupported format): " + fileName + " - " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Failed to load clip (Audio line unavailable): " + fileName + " - " + e.getMessage());
        }
    }

    /**
     * Loads an audio clip from a specified file path.
     * <p>
     * If the audio file is bundled in the JAR (or the classpath), it is loaded
     * using a URL. Otherwise, it is loaded as a regular file.
     * <p>
     * The file must be a valid audio format supported by {@link AudioSystem}.
     *
     * @param filePath The path to the audio file (relative to classpath or
     * absolute).
     * @return A {@link Clip} instance containing the loaded audio.
     * @throws IllegalArgumentException If the file path is empty or
     * {@code null}.
     * @throws IOException If there is an error reading the file or it does not
     * exist.
     * @throws UnsupportedAudioFileException If the audio format is not
     * supported.
     * @throws LineUnavailableException If no audio line is available for
     * playback.
     */
    public static Clip loadClipFromAudioFile(final String filePath) throws IllegalArgumentException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("SoundManager: Must provide a valid file path for the audio clip.");
        }

        AudioInputStream audioIn = null;
        Clip clip;

        try {
            // Try loading as a resource (for classpath resources, e.g., inside JAR file)
            URL audioUrl = SoundManager.class.getResource(filePath);
            if (audioUrl != null) {
                audioIn = AudioSystem.getAudioInputStream(audioUrl);
            } else {
                // If URL is not found, try loading as a normal file (e.g., file system)
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    throw new IOException("SoundManager: Audio file not found: " + filePath);
                }
                audioIn = AudioSystem.getAudioInputStream(file);
            }

            // Get the original audio format
            AudioFormat baseFormat = audioIn.getFormat();

            // Check if the format is supported by the system
            if (isSupportedFormat(baseFormat)) {
                // If the format is supported, load the clip directly
                clip = AudioSystem.getClip();
                clip.open(audioIn);
            } else {
                // Otherwise, attempt to convert to a universally supported format (16-bit PCM)
                AudioInputStream convertedAudioIn = convertToSupportedFormat(audioIn, baseFormat);
                clip = AudioSystem.getClip();
                clip.open(convertedAudioIn);
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new IOException("SoundManager: Unsupported audio format or no available audio line: " + filePath, e);
        } finally {
            if (audioIn != null) {
                audioIn.close();
            }
        }
        System.out.println("SoundManager: Loaded audio clip <'" + filePath + "'>");
        return clip;
    }

    /**
     * Plays a sound clip.
     *
     * @param key The key of the sound clip.
     * @param looping If {@code true}, the sound will loop continuously.
     */
    public void playClip(String key, boolean looping) {
        Clip clip = getClip(key);
        if (clip != null) {
            clip.setFramePosition(0);
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }

    /**
     * Stops a specific sound clip.
     *
     * @param key The key of the sound clip.
     */
    public void stopClip(String key) {
        Clip clip = getClip(key);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Stops all currently playing sound clips.
     */
    public void stopAll() {
        for (Clip clip : clips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Adjusts the volume for all loaded clips.
     */
    private void applyVolumeToAllClips() {
        for (Clip clip : clips.values()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float newVolume = min + (volume * (max - min));
            gainControl.setValue(newVolume);
        }
    }

    /**
     * Checks if the given audio format is supported by the system.
     *
     * @param format The audio format to check.
     * @return true if the format is supported, false otherwise.
     */
    private static boolean isSupportedFormat(AudioFormat format) {
        // Try to find a line that supports this audio format
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        return AudioSystem.isLineSupported(info);
    }

    /**
     * Converts the provided audio stream to a supported audio format (16-bit
     * PCM).
     *
     * @param audioIn The original audio input stream.
     * @param baseFormat The original audio format.
     * @return The converted audio input stream in a supported format (16-bit
     * PCM).
     * @throws UnsupportedAudioFileException If the audio format is unsupported.
     */
    private static AudioInputStream convertToSupportedFormat(AudioInputStream audioIn, AudioFormat baseFormat) throws UnsupportedAudioFileException {
        AudioFormat targetFormat = getSupportedAudioFormat(baseFormat);

        if (targetFormat != null) {
            // Convert to supported format
            return AudioSystem.getAudioInputStream(targetFormat, audioIn);
        } else {
            throw new UnsupportedAudioFileException("SoundManager: No supported audio format found for conversion.");
        }
    }

    /**
     * Returns a supported audio format (16-bit PCM) for a given audio format if
     * it's unsupported.
     *
     * @param baseFormat The audio format that needs to be converted.
     * @return A supported audio format (16-bit PCM) or null if no conversion is
     * possible.
     */
    private static AudioFormat getSupportedAudioFormat(AudioFormat baseFormat) {
        // Try converting to 16-bit PCM, stereo, little-endian
        if (baseFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            return new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // PCM_SIGNED encoding
                    baseFormat.getSampleRate(), // Same sample rate
                    16, // 16-bit depth
                    baseFormat.getChannels(), // Same number of channels (stereo/mono)
                    baseFormat.getChannels() * 2, // 2 bytes per frame (16-bit PCM)
                    baseFormat.getSampleRate(), // Same frame rate
                    false // Little-endian
            );
        }
        return null;  // If the format is already PCM_SIGNED, no conversion is needed.
    }
}
