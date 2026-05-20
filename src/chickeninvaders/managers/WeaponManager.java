package chickeninvaders.managers;

import chickeninvaders.entities.Bullet;
import chickeninvaders.weapons.WeaponType;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WeaponManager {
    private WeaponType current = WeaponType.BASIC;

    public WeaponType getCurrent() {
        return current;
    }

    public void setWeapon(WeaponType type) {
        current = type;
    }

    public List<Bullet> shoot(double x, double y, BufferedImage bulletImage, BufferedImage laserImage, BufferedImage missileImage) {
        List<Bullet> bullets = new ArrayList<>();
        if (current == WeaponType.DOUBLE) {
            bullets.add(new Bullet(x - 22, y, 0, -650, 16, false, bulletImage));
            bullets.add(new Bullet(x + 8, y, 0, -650, 16, false, bulletImage));
        } else if (current == WeaponType.TRIPLE) {
            bullets.add(new Bullet(x - 8, y, 0, -690, 18, false, bulletImage));
            bullets.add(new Bullet(x - 32, y + 8, -120, -650, 18, false, bulletImage));
            bullets.add(new Bullet(x + 16, y + 8, 120, -650, 18, false, bulletImage));
        } else if (current == WeaponType.LASER) {
            bullets.add(new Bullet(x - 8, y - 20, 0, -850, 36, false, laserImage));
        } else if (current == WeaponType.MISSILE) {
            bullets.add(new Bullet(x - 10, y, 0, -520, 55, true, missileImage));
        } else {
            bullets.add(new Bullet(x - 8, y, 0, -650, 14, false, bulletImage));
        }
        return bullets;
    }
}
