package xiatian;

import org.lwjgl.input.Mouse;

public class PowerButton {
    private final int button;
    public boolean clicked;

    public PowerButton(int n) {
        this.button = n;
    }

    public boolean canExcecute() {
        if (Mouse.isButtonDown(this.button)) {
            if (!this.clicked) {
                this.clicked = true;
                return true;
            }
        } else {
            this.clicked = false;
        }
        return false;
    }

}