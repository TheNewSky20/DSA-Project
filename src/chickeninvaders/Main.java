package chickeninvaders;

import chickeninvaders.core.GameWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow().showWindow());
    }
}
