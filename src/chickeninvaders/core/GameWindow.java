package chickeninvaders.core;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
    private final GamePanel panel;

    public GameWindow() {
        super("Chicken Invaders Cute Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public void showWindow() {
        setVisible(true);
        panel.start();
    }
}
