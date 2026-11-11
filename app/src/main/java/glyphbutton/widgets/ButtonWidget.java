package glyphbutton.widgets;

public class ButtonWidget implements Widget {
    private final String id;
    private final String label;
    private final Runnable onPress;

    private int x;
    private int y;

    // private int color;
    // private int backgroundColor;

    public ButtonWidget(String id, String label, Runnable onPress) {
        this.id = id;
        this.label = label;
        this.onPress = onPress;
    }

    @Override
    public String render(boolean focused) {
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
}
