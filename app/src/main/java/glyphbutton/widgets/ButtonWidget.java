package glyphbutton.widgets;

public class ButtonWidget implements Widget {
    private final String id;
    private String label;
    private Runnable onPress;

    private boolean focused = false;

    private int x;
    private int y;

    private boolean onWindowBorder = false;

    // private int color;
    // private int backgroundColor;

    /**
     * 
     * @param id
     * @param label
     */
    public ButtonWidget(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public boolean focused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public String draw() {
        if (focused) {
            return "\033[30;104m" + label + "\033[0m";
        } else {
            return label;
        }
    }

    @Override
    public void activate() {
        if (onPress != null) {
            onPress.run();
        }
    }

    public void setOnPress(Runnable onPress) {
        this.onPress = onPress;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return label.length();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean onWindowBorder() {
        return onWindowBorder;
    }

    public void setOnWindowBorder(boolean onWindowBorder) {
        this.onWindowBorder = onWindowBorder;
    }

}
