package chickeninvaders.entities;

import java.awt.image.BufferedImage;

public class Bullet extends Entity {
    private final int damage;
    private final boolean missile;

    public Bullet(double x, double y, double vx, double vy, int damage, boolean missile, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.missile = missile;
        this.width = missile ? 26 : 14;
        this.height = missile ? 42 : 34;
        this.image = image;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isMissile() {
        return missile;
    }

    public void steerTo(double tx, double ty, double dt) {
        if (!missile) {
            return;
        }
        double dx = tx - centerX();
        double dy = ty - centerY();
        double length = Math.max(1, Math.sqrt(dx * dx + dy * dy));
        vx += dx / length * 260 * dt;
        vy += dy / length * 260 * dt;
    }

    @Override
    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
        if (x < -80 || x > 1040 || y < -100 || y > 720) {
            active = false;
        }
    }
}
