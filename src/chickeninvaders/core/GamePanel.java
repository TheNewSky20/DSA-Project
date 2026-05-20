package chickeninvaders.core;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 960;
    public static final int HEIGHT = 640;

    private final Input input = new Input();
    private final GameEngine engine = new GameEngine(input, WIDTH, HEIGHT);
    private Thread thread;
    private boolean running;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(input);
    }

    public void start() {
        requestFocusInWindow();
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            double dt = Math.min(0.033, (now - last) / 1_000_000_000.0);
            last = now;
            engine.update(dt);
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        engine.draw((Graphics2D) g);
    }
}
