package glyphbutton.widgets;

public class ButtonWidget implements Widget {
    private final String id;
    private final String label;
    private final Runnable onPress;

    public ButtonWidget(String id, String label, Runnable onPress) {
        this.id = id;
        this.label = label;
        this.onPress = onPress;
    }

    @Override
    public String render(boolean focused) {
        if (focused) {
            return " ▶ [" + label + "]";
        } else {
            return "   [" + label + "]";
        }
    }

    @Override
    public Void activate() {
        if (onPress != null) {
            onPress.run();
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
