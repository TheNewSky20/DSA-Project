package chickeninvaders.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class Input implements KeyListener {
    private final Set<Integer> keys = new HashSet<>();
    private final Set<Integer> once = new HashSet<>();

    public boolean down(int code) {
        return keys.contains(code);
    }

    public boolean pressedOnce(int code) {
        if (keys.contains(code) && !once.contains(code)) {
            once.add(code);
            return true;
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
        once.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
