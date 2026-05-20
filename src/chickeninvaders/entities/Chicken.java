package chickeninvaders.entities;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Chicken extends Entity {
    private final ChickenType type;
    private final Random random = new Random();
    private int hp;
    private double time;

    public Chicken(double x, double y, ChickenType type, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
        this.width = type == ChickenType.TANK ? 62 : 52;
        this.height = type == ChickenType.TANK ? 62 : 52;

        if (type == ChickenType.FAST) {
            vx = 150;
            hp = 16;
        } else if (type == ChickenType.TANK) {
            vx = 70;
            hp = 42;
        } else {
            vx = 95;
            hp = 22;
        }
    }

    @Override
    public void update(double dt) {
        time += dt;
        x += vx * dt;
        if (type == ChickenType.FAST) {
            y += Math.sin(time * 7) * 40 * dt;
        } else {
            y += Math.sin(time * 3) * 20 * dt;
        }
        if (x < 20 || x > 880) {
            vx *= -1;
        }
    }

    public void damage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            active = false;
        }
    }

    public boolean shouldDropEgg() {
        return random.nextDouble() < 0.0035;
    }

    public int score() {
        if (type == ChickenType.FAST) {
            return 140;
        }
        if (type == ChickenType.TANK) {
            return 220;
        }
        return 100;
    }
}
