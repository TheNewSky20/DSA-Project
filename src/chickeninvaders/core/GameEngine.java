package chickeninvaders.core;

import chickeninvaders.bosses.BossChicken;
import chickeninvaders.bosses.BossType;
import chickeninvaders.entities.Bullet;
import chickeninvaders.entities.Chicken;
import chickeninvaders.entities.ChickenType;
import chickeninvaders.entities.Egg;
import chickeninvaders.entities.Entity;
import chickeninvaders.entities.Player;
import chickeninvaders.items.DropItem;
import chickeninvaders.items.ItemType;
import chickeninvaders.managers.AssetManager;
import chickeninvaders.managers.LeaderboardManager;
import chickeninvaders.managers.ScoreEntry;
import chickeninvaders.managers.SoundManager;
import chickeninvaders.managers.WeaponManager;
import chickeninvaders.weapons.WeaponType;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class GameEngine {
    private final Input input;
    private final int width;
    private final int height;
    private final AssetManager assets = new AssetManager();
    private final SoundManager sound = new SoundManager();
    private final WeaponManager weapon = new WeaponManager();
    private final Random random = new Random();
    private final Stack<GameState> stateStack = new Stack<>();
    private final Queue<Chicken> spawnQueue = new LinkedList<>();
    private final ArrayList<Chicken> chickens = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Egg> eggs = new ArrayList<>();
    private final ArrayList<DropItem> items = new ArrayList<>();

    private Player player;
    private BossChicken boss;
    private GameState state = GameState.MENU;
    private int level = 1;
    private int score = 0;
    private double backgroundY = 0;
    private final LeaderboardManager leaderboard = new LeaderboardManager();
    private boolean scoreSaved = false;
    private int menuIndex = 0;
    private String playerName = "Player";

    public GameEngine(Input input, int width, int height) {
        this.input = input;
        this.width = width;
        this.height = height;
        this.player = new Player(width / 2.0 - 36, height - 95, assets.get("player"));
        createLevel();
    }

    public void update(double dt) {
        backgroundY = (backgroundY + 80 * dt) % height;
        sound.update(dt);

        if (state == GameState.MENU) {
            if (input.pressedOnce(KeyEvent.VK_UP)) menuIndex = Math.max(0, menuIndex - 1);
            if (input.pressedOnce(KeyEvent.VK_DOWN)) menuIndex = Math.min(2, menuIndex + 1);
            if (input.pressedOnce(KeyEvent.VK_ENTER)) {
                if (menuIndex == 0) state = GameState.PLAYING;
                else if (menuIndex == 1) { }
                else System.exit(0);
            }
            return;
        }

        if (input.pressedOnce(KeyEvent.VK_P)) {
            togglePause();
        }

        if (state == GameState.PAUSED) {
            return;
        }

        if (state == GameState.GAME_OVER) {
            if (!scoreSaved) {
                leaderboard.saveScore(playerName, score);
                scoreSaved = true;
            }
            if (input.pressedOnce(KeyEvent.VK_ENTER)) {
                restart();
            }
            return;
        }

        player.control(input, dt, width, height);
        autoShoot();
        spawnEnemies();
        updateObjects(dt);
        updateMissiles(dt);
        checkCollisions();
        cleanObjects();
        checkLevelClear();
    }

    private void autoShoot() {
        if (!player.canShoot() || bullets.size() > 30) {
            return;
        }
        bullets.addAll(weapon.shoot(
                player.centerX(),
                player.centerY() - 45,
                assets.get("bullet"),
                assets.get("laser"),
                assets.get("missile")
        ));
        player.resetCooldown();
        sound.play("shoot");
    }

    private void spawnEnemies() {
        while (chickens.size() < 14 && !spawnQueue.isEmpty()) {
            chickens.add(spawnQueue.poll());
        }
    }

    private void updateObjects(double dt) {
        player.update(dt);
        for (Chicken chicken : chickens) {
            chicken.update(dt);
            if (chicken.shouldDropEgg() && eggs.size() < 25) {
                eggs.add(new Egg(chicken.centerX(), chicken.centerY(), assets.get("egg")));
            }
        }
        for (Bullet bullet : bullets) {
            bullet.update(dt);
        }
        for (Egg egg : eggs) {
            egg.update(dt);
        }
        for (DropItem item : items) {
            item.update(dt);
        }
        if (boss != null) {
            boss.update(dt);
            if (eggs.size() < 25) {
                eggs.addAll(boss.shoot(assets.get("egg")));
            }
        }
    }

    private void updateMissiles(double dt) {
        for (Bullet bullet : bullets) {
            if (!bullet.isMissile()) {
                continue;
            }
            Entity target = findNearestTarget(bullet.centerX(), bullet.centerY());
            if (target != null) {
                bullet.steerTo(target.centerX(), target.centerY(), dt);
            }
        }
    }

    private Entity findNearestTarget(double x, double y) {
        Entity best = boss;
        double bestDistance = boss == null ? Double.MAX_VALUE : distance(x, y, boss.centerX(), boss.centerY());
        for (Chicken chicken : chickens) {
            double d = distance(x, y, chicken.centerX(), chicken.centerY());
            if (d < bestDistance) {
                best = chicken;
                bestDistance = d;
            }
        }
        return best;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void checkCollisions() {
        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) {
                continue;
            }
            for (Chicken chicken : chickens) {
                if (intersects(bullet, chicken)) {
                    bullet.deactivate();
                    chicken.damage(bullet.getDamage());
                    if (!chicken.isActive()) {
                        score += chicken.score();
                        maybeDropItem(chicken.centerX(), chicken.centerY());
                    }
                    break;
                }
            }
            if (boss != null && boss.isActive() && intersects(bullet, boss)) {
                bullet.deactivate();
                boss.damage(bullet.getDamage());
                if (!boss.isActive()) {
                    score += 2500;
                    boss = null;
                    bullets.clear();
                    eggs.clear();
                    nextLevel();
                    return;
                }
            }
        }

        for (Egg egg : eggs) {
            if (intersects(egg, player)) {
                egg.deactivate();
                player.damage(8);
                if (player.getHp() <= 0) {
                    state = GameState.GAME_OVER;
                }
            }
        }

        for (DropItem item : items) {
            if (intersects(item, player)) {
                applyItem(item.getType());
                item.deactivate();
                sound.play("powerup");
            }
        }
    }

    private boolean intersects(Entity a, Entity b) {
        Rectangle ra = a.bounds();
        Rectangle rb = b.bounds();
        return a.isActive() && b.isActive() && ra.intersects(rb);
    }

    private void maybeDropItem(double x, double y) {
        if (random.nextDouble() > 0.22) {
            return;
        }
        ItemType[] values = ItemType.values();
        ItemType type = values[random.nextInt(values.length)];
        String key = "item_" + type.name().toLowerCase();
        items.add(new DropItem(x, y, type, assets.get(key)));
    }

    private void applyItem(ItemType type) {
        if (type == ItemType.DOUBLE) {
            weapon.setWeapon(WeaponType.DOUBLE);
        } else if (type == ItemType.TRIPLE) {
            weapon.setWeapon(WeaponType.TRIPLE);
        } else if (type == ItemType.LASER) {
            weapon.setWeapon(WeaponType.LASER);
        } else if (type == ItemType.MISSILE) {
            weapon.setWeapon(WeaponType.MISSILE);
        } else if (type == ItemType.SHIELD) {
            player.activateShield();
        } else if (type == ItemType.HEAL) {
            player.heal(25);
        }
    }

    private void cleanObjects() {
        chickens.removeIf(chicken -> !chicken.isActive());
        bullets.removeIf(bullet -> !bullet.isActive());
        eggs.removeIf(egg -> !egg.isActive());
        items.removeIf(item -> !item.isActive());
    }

    private void checkLevelClear() {
        if (spawnQueue.isEmpty() && chickens.isEmpty() && boss == null) {
            nextLevel();
        }
    }

    private void nextLevel() {
        level++;
        createLevel();
    }

    private void createLevel() {
        spawnQueue.clear();
        chickens.clear();
        bullets.clear();
        eggs.clear();
        items.clear();

        if (level % 5 == 0) {
            createBoss();
            return;
        }
        boss = null;
        int rows = Math.min(4, 2 + level / 4);
        int cols = Math.min(8, 5 + level / 3);
        createWaveRecursive(0, 0, rows, cols);
    }

    private void createWaveRecursive(int row, int col, int rows, int cols) {
        if (row >= rows) {
            return;
        }
        ChickenType type = chooseChickenType(row, col);
        String imageKey = chickenImage(type);
        Chicken chicken = new Chicken(
                90 + col * 95,
                70 + row * 68,
                type,
                assets.get(imageKey)
        );
        spawnQueue.offer(chicken);
        if (col == cols - 1) {
            createWaveRecursive(row + 1, 0, rows, cols);
        } else {
            createWaveRecursive(row, col + 1, rows, cols);
        }
    }

    private ChickenType chooseChickenType(int row, int col) {
        int value = (row + col + level) % 3;
        if (value == 1) {
            return ChickenType.FAST;
        }
        if (value == 2) {
            return ChickenType.TANK;
        }
        return ChickenType.BABY;
    }

    private String chickenImage(ChickenType type) {
        if (type == ChickenType.FAST) {
            return "chicken_fast";
        }
        if (type == ChickenType.TANK) {
            return "chicken_tank";
        }
        return "chicken_baby";
    }

    private void createBoss() {
        BossType type = bossType();
        String key = "boss_giant";
        if (type == BossType.ANGRY) {
            key = "boss_angry";
        } else if (type == BossType.MECHA) {
            key = "boss_mecha";
        }
        boss = new BossChicken(width / 2.0 - 95, 50, level, type, assets.get(key));
    }

    private BossType bossType() {
        int index = (level / 5 - 1) % 3;
        if (index == 1) {
            return BossType.ANGRY;
        }
        if (index == 2) {
            return BossType.MECHA;
        }
        return BossType.GIANT;
    }

    private void togglePause() {
        if (state == GameState.PLAYING) {
            stateStack.push(state);
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = stateStack.isEmpty() ? GameState.PLAYING : stateStack.pop();
        }
    }

    private void restart() {
        level = 1;
        score = 0;
        player = new Player(width / 2.0 - 36, height - 95, assets.get("player"));
        weapon.setWeapon(WeaponType.BASIC);
        scoreSaved = false;
        state = GameState.PLAYING;
        createLevel();
    }

    public void draw(Graphics2D g) {
        drawBackground(g);

        if (state == GameState.MENU) {
            drawMenu(g);
            return;
        }

        for (Chicken chicken : chickens) {
            chicken.draw(g);
        }
        if (boss != null) {
            boss.draw(g);
        }
        for (DropItem item : items) {
            item.draw(g);
        }
        for (Egg egg : eggs) {
            egg.draw(g);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        player.draw(g);
        drawHud(g);

        if (state == GameState.PAUSED) {
            drawOverlay(g, "PAUSED", "Press P to resume");
        }
        if (state == GameState.GAME_OVER) {
            drawOverlay(g, "GAME OVER", "Score: " + score + " | Press ENTER to restart");
            drawLeaderboard(g);
        }
    }

    private void drawBackground(Graphics2D g) {
        BufferedImage background = assets.get("background");
        g.drawImage(background, 0, (int) backgroundY - height, width, height, null);
        g.drawImage(background, 0, (int) backgroundY, width, height, null);
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Chicken Invaders Cute Edition", 145, 250);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("ENTER: Select | UP/DOWN: Menu", 240, 320);
        String[] options = {"Start Game","Leaderboard (saved in leaderboard.txt)","Exit"};
        for(int i=0;i<options.length;i++){
            g.setColor(i==menuIndex?Color.YELLOW:Color.WHITE);
            g.drawString(options[i], 290, 380 + i*35);
        }
    }

    private void drawHud(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 20, 28);
        g.drawString("Level: " + level, 20, 52);
        g.drawString("Weapon: " + weapon.getCurrent(), 20, 76);
        drawBar(g, 20, 90, 220, 20, player.getHp(), 100, Color.GREEN, "HP");
        if (boss != null) {
            drawBar(g, 300, 24, 360, 22, boss.getHp(), boss.getMaxHp(), Color.RED, boss.getType().name() + " BOSS");
        }
    }

    private void drawBar(Graphics2D g, int x, int y, int w, int h, int value, int max, Color color, String label) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(color);
        int fill = (int) (w * Math.max(0, Math.min(1, value / (double) max)));
        g.fillRoundRect(x, y, fill, h, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, w, h, 10, 10);
        g.drawString(label + ": " + value + "/" + max, x + 8, y + h - 4);
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 56));
        g.drawString(title, width / 2 - 150, height / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString(subtitle, width / 2 - 130, height / 2 + 45);
    }

    private void drawLeaderboard(Graphics2D g) {
        java.util.List<ScoreEntry> scores = leaderboard.getTopScores();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("TOP 10 PLAYERS", width / 2 - 100, height / 2 + 80);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int y = height / 2 + 110;
        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry s = scores.get(i);
            g.drawString((i + 1) + ". " + s.getName() + " - " + s.getScore(), width / 2 - 120, y);
            y += 24;
        }
    }
}