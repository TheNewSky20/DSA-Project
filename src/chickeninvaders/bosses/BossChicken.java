package chickeninvaders.bosses;

import chickeninvaders.entities.Entity;
import chickeninvaders.entities.Egg;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BossChicken extends Entity {
    private final BossType type;
    private final int maxHp;
    private int hp;
    private double fireTimer;
    private double waveTimer;

    public BossChicken(double x, double y, int level, BossType type, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.image = image;
        this.width = 190;
        this.height = 155;
        this.maxHp = 320 + level * 55;
        this.hp = maxHp;
        this.vx = type == BossType.ANGRY ? 250 : 180;
    }

    @Override
    public void update(double dt) {
        fireTimer -= dt;
        waveTimer += dt;
        x += vx * dt;
        y = 40 + Math.sin(waveTimer * 2.0) * 18;
        if (x <= 0) {
            x = 0;
            vx = Math.abs(vx);
        }
        if (x + width >= 960) {
            x = 960 - width;
            vx = -Math.abs(vx);
        }
    }

    public List<Egg> shoot(BufferedImage eggImage) {
        List<Egg> eggs = new ArrayList<>();
        if (fireTimer > 0) {
            return eggs;
        }

        if (type == BossType.GIANT) {
            eggs.add(new Egg(centerX() - 40, centerY() + 60, eggImage));
            eggs.add(new Egg(centerX() + 40, centerY() + 60, eggImage));
            fireTimer = 1.35;
        } else if (type == BossType.ANGRY) {
            eggs.add(new Egg(centerX(), centerY() + 70, eggImage));
            eggs.add(new Egg(centerX() - 75, centerY() + 45, eggImage));
            fireTimer = 1.10;
        } else {
            eggs.add(new Egg(centerX() - 80, centerY() + 55, eggImage));
            eggs.add(new Egg(centerX(), centerY() + 75, eggImage));
            eggs.add(new Egg(centerX() + 80, centerY() + 55, eggImage));
            fireTimer = 1.55;
        }
        return eggs;
    }

    public void damage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            active = false;
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public BossType getType() {
        return type;
    }
}
