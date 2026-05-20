package chickeninvaders.managers;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;

public class SoundManager {
    private final HashMap<String, Clip> clips = new HashMap<>();
    private double soundCooldown;

    public void update(double dt) {
        if (soundCooldown > 0) {
            soundCooldown -= dt;
        }
    }

    public void play(String key) {
        if (soundCooldown > 0 && key.equals("shoot")) {
            return;
        }
        Clip clip = getClip(key);
        if (clip == null) {
            return;
        }
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
        if (key.equals("shoot")) {
            soundCooldown = 0.10;
        }
    }

    private Clip getClip(String key) {
        if (clips.containsKey(key)) {
            return clips.get(key);
        }
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("assets/sounds/" + key + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clips.put(key, clip);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }
}
