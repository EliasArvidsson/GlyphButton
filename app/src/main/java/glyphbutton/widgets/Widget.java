package glyphbutton.widgets;

public interface Widget {

    int getX();

    int getY();

    void setPosition(int x, int y);

    String getId();

    String getLabel();

    boolean focused();

    void setFocused(boolean focused);

    // void setBorderStyle(int style);

    // void setColor(int color);
    // void setBackgroundColor(int color);
    // void setBorderColor(int color);

    boolean onWindowBorder();

    String draw();

    void activate();

}
