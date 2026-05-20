package chickeninvaders.items;

import chickeninvaders.entities.Entity;
import java.awt.image.BufferedImage;

public class DropItem extends Entity {
    private final ItemType type;

    public DropItem(double x, double y, ItemType type, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.width = 38;
        this.height = 38;
        this.image = image;
        this.vy = 150;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public void update(double dt) {
        y += vy * dt;
        if (y > 680) {
            active = false;
        }
    }
}
