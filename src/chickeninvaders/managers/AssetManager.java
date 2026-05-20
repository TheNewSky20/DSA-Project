package chickeninvaders.managers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class AssetManager {
    private final HashMap<String, BufferedImage> images = new HashMap<>();

    public AssetManager() {
        String[] keys = {
                "background", "player", "bullet", "laser", "missile", "egg",
                "chicken_baby", "chicken_fast", "chicken_tank",
                "boss_giant", "boss_angry", "boss_mecha",
                "item_double", "item_triple", "item_laser", "item_missile", "item_shield", "item_heal"
        };
        for (String key : keys) {
            load(key);
        }
    }

    private void load(String key) {
        try {
            images.put(key, ImageIO.read(new File("assets/images/" + key + ".png")));
        } catch (Exception e) {
            System.err.println("Cannot load image: " + key);
        }
    }

    public BufferedImage get(String key) {
        return images.get(key);
    }
}
