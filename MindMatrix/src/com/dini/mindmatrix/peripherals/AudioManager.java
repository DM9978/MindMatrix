package com.dini.mindmatrix.peripherals;

import javafx.embed.swing.JFXPanel;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AudioManager {
    private static AudioManager instance;
    private Clip backgroundMusicClip;
    private Map<String, Clip> sfxClips = new HashMap<>();
    private double volume = 0.5;
    private double sfxVolume = 0.5;
    private boolean isMusicOn = true;
    private boolean isSFXOn = true;
    private static final String SETTINGS_FILE = "settings.properties";

    private AudioManager() {
        loadSettings();
        new JFXPanel();
        loadMusic();
        preloadSFX();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadMusic() {
        if (!isMusicOn) return;
        try {
            String musicFilePath = "/resources/background.wav";
            URL musicURL = getClass().getResource(musicFilePath);
            if (musicURL == null) {
                System.err.println("Bg music not found: " + musicFilePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicURL);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioIn);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(volume);
            backgroundMusicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void playClickSound() {
        if (!isSFXOn) return;
        playSound("click");
    }

    public void playHoverSound() {
        if (!isSFXOn || isWrongSoundPlaying) return;

        try {
            Clip clip = sfxClips.get("hover");
            if (clip == null) {
                System.err.println("Hover sound not found.");
                return;
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);

            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) (Math.log(sfxVolume) / Math.log(10) * 20);
            volumeControl.setValue(db);

            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean isWrongSoundPlaying = false;


    public void playWrongSound() {
        if (!isSFXOn) return;

        try {
            isWrongSoundPlaying = true;

            Clip clip = sfxClips.get("wrong");
            if (clip == null) {
                System.err.println("Wrong sound not found.");
                return;
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);

            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) (Math.log(sfxVolume) / Math.log(10) * 20);
            volumeControl.setValue(db);

            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    isWrongSoundPlaying = false;
                    clip.setFramePosition(0);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            isWrongSoundPlaying = false;
        }
    }




    void playSound(String soundKey) {
        if (!isSFXOn) return;
        try {
            Clip clip = sfxClips.get(soundKey);
            if (clip == null) {
                System.err.println("Not found: " + soundKey);
                return;
            }

            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) (Math.log(sfxVolume) / Math.log(10) * 20);
            volumeControl.setValue(db);

            clip.setFramePosition(0);
            clip.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void preloadSFX() {
        preloadSound("wrong", "/resources/wr.wav");
        preloadSound("click", "/resources/ww.wav");
        preloadSound("hover", "/resources/hover.wav");
        preloadSound("over", "/resources/over.wav");
        preloadSound("correct", "/resources/correct2.wav");
    }


    /** Sound tracks preloading method from chatGPT**/

    private void preloadSound(String soundKey, String filePath) {
        try {
            URL soundURL = getClass().getResource(filePath);
            if (soundURL == null) {
                System.err.println("Not found: " + filePath);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            sfxClips.put(soundKey, clip);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void setVolume(double volume) {
        this.volume = volume;
        if (backgroundMusicClip != null) {
            FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) (Math.log(volume) / Math.log(10) * 20);
            volumeControl.setValue(db);
        }
        saveSettings();
    }

    public void setSFXVolume(double sfxVolume) {
        this.sfxVolume = sfxVolume;
        saveSettings();
    }

    public void setMusicOn(boolean isOn) {
        this.isMusicOn = isOn;
        if (isOn) {
            loadMusic();
        } else {
            pause();
        }
        saveSettings();
    }

    public void setSFXOn(boolean isOn) {
        this.isSFXOn = isOn;
        saveSettings();
    }

    public void play() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.setFramePosition(0);
            backgroundMusicClip.start();
        }
    }


    public void pause() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.stop();
        }
    }

    private void loadSettings() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
            volume = Double.parseDouble(properties.getProperty("volume", "0.5"));
            sfxVolume = Double.parseDouble(properties.getProperty("sfxVolume", "0.5"));
            isMusicOn = Boolean.parseBoolean(properties.getProperty("musicOn", "true"));
            isSFXOn = Boolean.parseBoolean(properties.getProperty("SFXOn", "true"));
        } catch (IOException e) {
            System.err.println("Error");
            saveSettings();
        }
    }

    private void saveSettings() {
        Properties properties = new Properties();
        properties.setProperty("volume", String.valueOf(volume));
        properties.setProperty("sfxVolume", String.valueOf(sfxVolume));
        properties.setProperty("musicOn", String.valueOf(isMusicOn));
        properties.setProperty("SFXOn", String.valueOf(isSFXOn));
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playGameOverSound() {
        if (!isMusicOn) return;
        playSound("over");
    }

    public double getVolume() {
        return volume;
    }

    public double getSFXVolume() {
        return sfxVolume;
    }

    public boolean isMusicOn() {
        return isMusicOn;
    }

    public boolean isSFXOn() {
        return isSFXOn;
    }
}
