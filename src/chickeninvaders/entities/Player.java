package chickeninvaders.entities;

import chickeninvaders.core.Input;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private int hp = 100;
    private double cooldown = 0;
    private double shieldTime = 0;

    public Player(double x, double y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = 72;
        this.height = 72;
        this.image = image;
    }

    public void control(Input input, double dt, int screenWidth, int screenHeight) {
        double speed = 520;
        vx = 0;
        vy = 0;

        if (input.down(KeyEvent.VK_LEFT) || input.down(KeyEvent.VK_A)) {
            vx -= speed;
        }
        if (input.down(KeyEvent.VK_RIGHT) || input.down(KeyEvent.VK_D)) {
            vx += speed;
        }
        if (input.down(KeyEvent.VK_UP) || input.down(KeyEvent.VK_W)) {
            vy -= speed;
        }
        if (input.down(KeyEvent.VK_DOWN) || input.down(KeyEvent.VK_S)) {
            vy += speed;
        }

        x += vx * dt;
        y += vy * dt;
        x = Math.max(0, Math.min(screenWidth - width, x));
        y = Math.max(0, Math.min(screenHeight - height, y));

        if (cooldown > 0) {
            cooldown -= dt;
        }
        if (shieldTime > 0) {
            shieldTime -= dt;
        }
    }

    public boolean canShoot() {
        return cooldown <= 0;
    }

    public void resetCooldown() {
        cooldown = 0.16;
    }

    public void damage(int amount) {
        if (shieldTime > 0) {
            return;
        }
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(100, hp + amount);
    }

    public void activateShield() {
        shieldTime = 10.0;
    }

    public boolean hasShield() {
        return shieldTime > 0;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        if (hasShield()) {
            g.setColor(new Color(90, 180, 255, 110));
            g.fillOval((int) x - 8, (int) y - 8, width + 16, height + 16);
        }
    }
}
