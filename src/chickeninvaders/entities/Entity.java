package chickeninvaders.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected int width;
    protected int height;
    protected boolean active = true;
    protected BufferedImage image;

    public abstract void update(double dt);

    public void draw(Graphics2D g) {
        if (active && image != null) {
            g.drawImage(image, (int) x, (int) y, width, height, null);
        }
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public double centerX() {
        return x + width / 2.0;
    }

    public double centerY() {
        return y + height / 2.0;
    }
}
