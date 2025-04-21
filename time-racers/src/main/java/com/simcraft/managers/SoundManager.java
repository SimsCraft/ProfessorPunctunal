package com.simcraft.managers;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public class SoundManager {

    private Clip backgroundMusic;
    private Clip footstepSound;
    private Clip gameOverSound;
    HashMap<String, Clip> clips;

    private static SoundManager instance = null;	// keeps track of Singleton instance
    private float volume;

    public SoundManager() {
        loadSounds();
        clips = new HashMap<String, Clip>();
        Clip clip = loadClip("Sound/Footstep.wav");
        clips.put("foot", clip);

        volume = 1.0f;
    }

    private void loadSounds() {
        backgroundMusic = loadClip("Sound/Background.wav");
        footstepSound = loadClip("Sound/Footstep.wav");
        gameOverSound = loadClip("Sound/GameOver.wav");
    }

    private Clip loadClip(String path) {
        try {
            File file = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            System.out.println("Error loading sound: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void playFootstep() {
        if (footstepSound != null && !footstepSound.isRunning()) {
            footstepSound.setFramePosition(0); // Reset to start
            footstepSound.start();
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public void playGameOver() {
        if (gameOverSound != null) {
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }

    public static SoundManager getInstance() {	// class method to retrieve instance of Singleton
        if (instance == null) {
            instance = new SoundManager();
        }

        return instance;
    }

    public Clip loadClips(String fileName) {	// gets clip from the specified file
        AudioInputStream audioIn;
        Clip clip = null;

        try {
            File file = new File(fileName);
            audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            System.out.println("Error opening sound files: " + e);
        }
        return clip;
    }

    public Clip getClip(String title) {

        return clips.get(title);
    }

    public void playClip(String title, boolean looping) {
        Clip clip = getClip(title);
        if (clip != null) {
            clip.setFramePosition(0);
            if (looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
            }else {
                clip.start();
            }
        }
    }

    public void stopClip(String title) {
        Clip clip = getClip(title);
        if (clip != null) {
            clip.stop();
        }

    }
}
