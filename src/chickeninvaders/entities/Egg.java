package chickeninvaders.entities;

import java.awt.image.BufferedImage;

public class Egg extends Entity {
    public Egg(double x, double y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.vy = 230;
        this.width = 26;
        this.height = 34;
        this.image = image;
    }

    @Override
    public void update(double dt) {
        y += vy * dt;
        if (y > 700) {
            active = false;
        }
    }
}
