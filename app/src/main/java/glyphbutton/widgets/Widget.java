package glyphbutton.widgets;

public interface Widget {

    int getX();

    int getY();

    void setPosition(int x, int y);

    String getId();

    String getLabel();

    // void setBorderStyle(int style);

    // void setColor(int color);
    // void setBackgroundColor(int color);
    // void setBorderColor(int color);

    String draw(boolean focused);

    void activate();
}
