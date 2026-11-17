package glyphbutton.panel.overlay;

import glyphbutton.panel.Panel;

public abstract class Overlay extends Panel {

    private OverlayPosition positions;

    public Overlay(int width, int height, String title) {
        super(width, height, title);
        
    }

    public void setPosition(OverlayPosition positions) {
        this.positions = positions;
    }

    public OverlayPosition getPosition() {
        return positions;
    }

}
