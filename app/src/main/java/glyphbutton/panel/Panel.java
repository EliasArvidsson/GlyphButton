package glyphbutton.panel;

import java.util.ArrayList;
import java.util.List;
import glyphbutton.widgets.*;;

public abstract class Panel {
    private ButtonWidget closeButton;

    private int width;
    private int height;
    private String title;
    private int borderStyle = 1;
    private List<Widget> widgets = new ArrayList<>();

    public Panel(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void setOnClose(Runnable onClose) {
        if (closeButton != null)
            closeButton.setOnPress(onClose);
    }

    /**
     * Returns the (inner) width of the window
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the (inner) height of the window
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the (inner) width of the window
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the (inner) height of the window
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

}
